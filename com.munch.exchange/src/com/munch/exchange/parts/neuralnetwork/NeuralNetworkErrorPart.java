package com.munch.exchange.parts.neuralnetwork;

import java.awt.BasicStroke;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.wb.swt.ResourceManager;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.job.neuralnetwork.NeuralNetworkOptimizer.OptInfo;
import com.munch.exchange.job.neuralnetwork.NeuralNetworkOptimizerManager;
import com.munch.exchange.job.neuralnetwork.NeuralNetworkOptimizerManager.NNOptManagerInfo;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;
import com.munch.exchange.parts.InfoPart;
import com.munch.exchange.parts.MyMDirtyable;
import com.munch.exchange.services.IExchangeRateProvider;

public class NeuralNetworkErrorPart {
	
	private static Logger logger = Logger.getLogger(NeuralNetworkErrorPart.class);
	
	public static final String NEURALNETWORK_ERROR_EDITOR_ID="com.munch.exchange.partdescriptor.neuralnetworkerroreditor";
	
	@Inject
	private Stock stock;
	
	@Inject
	private IEventBroker eventBroker;
	
	
	@Inject
	IExchangeRateProvider exchangeRateProvider;
	
	//@Inject
	//NeuralNetworkOptimizer optimizer;
	
	@Inject
	NeuralNetworkOptimizerManager optimizerManager;
	
	private Button btnStop;
	private ProgressBar progressBarNetworkError;
	
	private Composite compositeChart;
	private JFreeChart chart;
	private XYSeriesCollection errorData;
	private XYSeries lastSeries;
	
	private HashMap<Integer, XYSeries> dimSerieMap=new HashMap<Integer, XYSeries>();
	private HashMap<Integer, Integer> dimSerieSteps=new HashMap<Integer, Integer>();
	private int nbOfOptSteps=0;
	
	public NeuralNetworkErrorPart() {
	}

	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	public void createControls(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		
		TabFolder tabFolder = new TabFolder(parent, SWT.BOTTOM);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TabItem tbtmGraph = new TabItem(tabFolder, SWT.NONE);
		tbtmGraph.setText("Graph");
		
		Composite compositeGraph = new Composite(tabFolder, SWT.NONE);
		tbtmGraph.setControl(compositeGraph);
		compositeGraph.setLayout(new GridLayout(1, false));
		
		//====================
		//Chart Creation
		//====================
		/*
		Composite compositeChart = new Composite(compositeGraph, SWT.NONE);
		compositeChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		*/
		
		createChart();
		compositeChart = new ChartComposite(compositeGraph, SWT.NONE,chart);
		compositeChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		//====================
		
		
		
		Composite compositeGraphBottom = new Composite(compositeGraph, SWT.NONE);
		GridLayout gl_compositeGraphBottom = new GridLayout(2, false);
		gl_compositeGraphBottom.marginHeight = 0;
		gl_compositeGraphBottom.marginWidth = 0;
		gl_compositeGraphBottom.verticalSpacing = 0;
		compositeGraphBottom.setLayout(gl_compositeGraphBottom);
		compositeGraphBottom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		progressBarNetworkError = new ProgressBar(compositeGraphBottom, SWT.NONE);
		progressBarNetworkError.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		btnStop = new Button(compositeGraphBottom, SWT.NONE);
		btnStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//optimizer.cancel();
				optimizerManager.cancel();
			}
		});
		btnStop.setImage(ResourceManager.getPluginImage("com.munch.exchange", "icons/delete.png"));
		btnStop.setText("Stop");
		
		TabItem tbtmTable = new TabItem(tabFolder, SWT.NONE);
		tbtmTable.setText("Table");
		
		Composite compositeTable = new Composite(tabFolder, SWT.NONE);
		tbtmTable.setControl(compositeTable);
		compositeTable.setLayout(new GridLayout(1, false));
	}
	
	
	 /**
     * Creates a chart.
     *
     * @return a chart.
     */
    private JFreeChart createChart() {
    	//====================
    	//===  Main Axis   ===
    	//====================
    	NumberAxis domainAxis =createDomainAxis();
    	
    	//====================
    	//===  Main Plot   ===
    	//====================
        XYPlot plot1 = createMainPlot(domainAxis);
        
        //=========================
    	//=== Create the Chart  ===
    	//=========================
        chart = new JFreeChart("",
                JFreeChart.DEFAULT_TITLE_FONT, plot1, true);
        chart.setBackgroundPaint(Color.white);
      
        return chart;
    	
    }
	
    private NumberAxis createDomainAxis(){
   	 //Axis
       NumberAxis domainAxis = new NumberAxis("Step");
       domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
       domainAxis.setAutoRange(true);
       domainAxis.setLowerMargin(0.01);
       domainAxis.setUpperMargin(0.01);
       return domainAxis;
   }
    /**
     * Create the Main Plot
     * 
     * @return
     */
    private XYPlot createMainPlot( NumberAxis domainAxis){
    	
    	//====================
    	//=== Main Curves  ===
    	//====================
    	//Creation of data Set
        //XYDataset priceData = createDataset(HistoricalPoint.FIELD_Close);
    	errorData = new XYSeriesCollection();
    	
    	
        //Renderer
        XYItemRenderer renderer1 = new XYLineAndShapeRenderer(true, false);
        renderer1.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                new DecimalFormat("0.0"), new DecimalFormat("0.0000")));
                
        if (renderer1 instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) renderer1;
            renderer.setBaseStroke(new BasicStroke(2.0f));
            renderer.setAutoPopulateSeriesStroke(false);
            renderer.setSeriesPaint(0, Color.BLUE);
            renderer.setSeriesPaint(1, Color.DARK_GRAY);
            //renderer.setSeriesPaint(2, new Color(0xFDAE61));
        }
        
        
        NumberAxis rangeAxis1 = new NumberAxis("Error");
      //  rangeAxis1.setLowerMargin(0.30);  // to leave room for volume bars
        DecimalFormat format = new DecimalFormat("0.00000");
        rangeAxis1.setNumberFormatOverride(format);
        rangeAxis1.setAutoRangeIncludesZero(false);
        
        //Plot
        XYPlot plot1 = new XYPlot(errorData, null, rangeAxis1, renderer1);
        plot1.setBackgroundPaint(Color.lightGray);
        plot1.setDomainGridlinePaint(Color.white);
        plot1.setRangeGridlinePaint(Color.white);
        plot1.setDomainAxis(domainAxis);
        
        
        return plot1;
    	
    }
	
    private XYSeries getLastSerie(){
		if(lastSeries==null)
			resetLastSeries();
		
		return lastSeries;
	}
	
	private void resetLastSeries(){
		lastSeries = new XYSeries("Error");
	}
    
	private void initDimSerieMap(NNOptManagerInfo info){
		
		dimSerieSteps.clear();
		
		//Delete old series
		Set<Integer> keySet=dimSerieMap.keySet();
		for(int i:keySet){
			if(i<info.getMinDim() || i>info.getMaxDim()){
				errorData.removeSeries(dimSerieMap.get(i));
				dimSerieMap.remove(i);
			}
		}
		
		//Add or clear the new series
		for(int i=info.getMinDim();i<=info.getMaxDim();i++){
			if(dimSerieMap.containsKey(i)){
				dimSerieMap.get(i).clear();
			}
			else{
				XYSeries series = new XYSeries("Dim "+i);
				dimSerieMap.put(i, series);
				errorData.addSeries(series);
			}
				
		}
		
		keySet=dimSerieMap.keySet();
		for(int i:keySet){
			dimSerieSteps.put(i, 0);
		}
		
	}
	
	private void updateProgressBar(OptInfo info){
		
		int step=info.getMaximum()-info.getStep()-1;
		if(nbOfOptSteps==0)
			nbOfOptSteps=info.getMaximum();
		
		
		dimSerieSteps.put(info.getNumberOfInnerNeurons(), step);
		int totalNbOfSteps=0;
		for(int i:dimSerieSteps.keySet()){
			totalNbOfSteps+=dimSerieSteps.get(i);
		}
		
		int total=nbOfOptSteps*dimSerieSteps.size();
		progressBarNetworkError.setSelection(totalNbOfSteps);
		progressBarNetworkError.setToolTipText(String.valueOf(100*totalNbOfSteps/total)+"%");
		progressBarNetworkError.setMaximum(total);
		
	}
	
	private void updateChart(OptInfo info){
		if(info.getResults().getResults().isEmpty())return;
		//Search the best results
		boolean[] bestArchi=info.getResults().getBestResult().getBooleanArray();
    	NetworkArchitecture archi=stock.getNeuralNetwork().getConfiguration().searchArchitecture(bestArchi);
    	double error=archi.getOptResults().getBestResult().getValue();
		
    	XYSeries series = dimSerieMap.get(info.getNumberOfInnerNeurons());
    	if(series==null)return;
    	series.add(info.getMaximum()-info.getStep(), error);
	}
	
	
	@PreDestroy
	public void dispose() {
	}

	@Focus
	public void setFocus() {
		// TODO	Set the focus to control
	}
	
	
	/*
	public void setOptimizer(NeuralNetworkOptimizer optimizer) {
		this.optimizer = optimizer;
	}
	*/

	//################################
	//##  EVENT REACTIONS          ##
	//################################
	private boolean isAbleToReact(String rate_uuid){
		
		if (rate_uuid == null || rate_uuid.isEmpty())
			return false;

		ExchangeRate incoming = exchangeRateProvider.load(rate_uuid);
		if (incoming == null || stock == null || btnStop == null)
			return false;
		if (!incoming.getUUID().equals(stock.getUUID()))
			return false;
		
		return true;
	}
	
	@Inject
	private void networkOptManagerStarted(@Optional @UIEventTopic(IEventConstant.NETWORK_OPTIMIZATION_MANAGER_STARTED) NNOptManagerInfo info){
		if(info==null)return;
		if(!isAbleToReact(info.getRate().getUUID()))return;
		
		
		btnStop.setEnabled(true);
		progressBarNetworkError.setEnabled(true);
		progressBarNetworkError.setSelection(0);
		
		initDimSerieMap(info);
		//chart.fireChartChanged();
		
	}
	
	
	@Inject
	private void networkArchitectureNewStep(@Optional @UIEventTopic(IEventConstant.NETWORK_ARCHITECTURE_OPTIMIZATION_NEW_STEP) OptInfo info){
		if(info==null)return;
		if(!isAbleToReact(info.getRate().getUUID()))return;
		
		//logger.info("New Step: "+info.getStep());
		
		if(progressBarNetworkError !=null && !progressBarNetworkError.isDisposed()){
			if(progressBarNetworkError.isEnabled()){
				
				//Update progress Bar
				updateProgressBar(info);
				
				//Update the chart
				updateChart(info);
		    	
			}
		}
	}
	
	@Inject
	private void networkArchitectureNewBest(
			@Optional @UIEventTopic(IEventConstant.NETWORK_ARCHITECTURE_OPTIMIZATION_NEW_BEST_INDIVIDUAL) OptInfo info) {
		if(info==null)return;
		if(!isAbleToReact(info.getRate().getUUID()))return;
		
		if(progressBarNetworkError !=null && !progressBarNetworkError.isDisposed()){
			if(progressBarNetworkError.isEnabled()){
			
				//Update progress Bar
				updateProgressBar(info);
				
				//Update the chart
				updateChart(info);
			}
		}
	}
	
	@Inject
	private void networkOptManagerFinished(
			@Optional @UIEventTopic(IEventConstant.NETWORK_OPTIMIZATION_MANAGER_FINISHED) NNOptManagerInfo info) {
		if(info==null)return;
		if(!isAbleToReact(info.getRate().getUUID()))return;
		
		btnStop.setEnabled(false);
		progressBarNetworkError.setSelection(0);
		progressBarNetworkError.setEnabled(false);
		
	}
	
	
	//################################
	//##          STATIC            ##
	//################################
	
	public static MPart openNeuralNetworkErrorPart(
			Stock stock,
			EPartService partService,
			EModelService modelService,
			MApplication application,
			//NeuralNetworkOptimizer optimizer,
			NeuralNetworkOptimizerManager optimizerManager,
			IEclipseContext context){
		
		MPart part=searchPart(NeuralNetworkErrorPart.NEURALNETWORK_ERROR_EDITOR_ID,stock.getUUID(),modelService, application);
		if(part!=null &&  part.getContributionURI()!=null){
			if(part.getContext()==null){
				setPartContext(part,stock,optimizerManager,context);
			}
			/*
				if(part instanceof NeuralNetworkErrorPart){
					NeuralNetworkErrorPart nne_part=(NeuralNetworkErrorPart) part;
					nne_part.setOptimizer(optimizer);
				}
				*/
			
				partService.bringToTop(part);
				return  part;
		}
		
		
		//Create the part
		part=createPart(stock,optimizerManager,partService,context);
				
		//add the part to the corresponding Stack
		MPartStack myStack=(MPartStack)modelService.find("com.munch.exchange.partstack.rightdown", application);
		myStack.getChildren().add(part);
		//Open the part
		partService.showPart(part, PartState.ACTIVATE);
		return  part;
	}
	
	private static MPart createPart(
			Stock stock,
			//NeuralNetworkOptimizer optimizer,
			NeuralNetworkOptimizerManager optimizerManager,
			EPartService partService,
			IEclipseContext context){
		MPart part = partService.createPart(NeuralNetworkErrorPart.NEURALNETWORK_ERROR_EDITOR_ID);
		
		//MPart part =MBasicFactory.INSTANCE.createPartDescrip;
		
		part.setLabel(stock.getName());
		//part.setIconURI(getIconURI(rate));
		part.setVisible(true);
		part.setDirty(false);
		part.getTags().add(stock.getUUID());
		//part.getTags().add(optimizationType);
		part.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
		
		setPartContext(part,stock,optimizerManager,context);
		
		//OptimizationErrorPart p=(OptimizationErrorPart) part;
		//p.setType(Optimizer.stringToOptimizationType(optimizationType));
		
		return part;
	}
	
	private static void setPartContext(
			MPart part,
			Stock stock,
			//NeuralNetworkOptimizer optimizer,
			NeuralNetworkOptimizerManager optimizerManager,
			IEclipseContext context){
		part.setContext(context.createChild());
		part.getContext().set(Stock.class, stock);
		//part.getContext().set(NeuralNetworkOptimizer.class, optimizer);
		part.getContext().set(NeuralNetworkOptimizerManager.class, optimizerManager);
		part.getContext().set(MDirtyable.class, new MyMDirtyable(part));
	}
	
	private static MPart searchPart(String partId,String tag,EModelService modelService,MApplication application){
		
		List<MPart> parts=getPartList(partId,tag,modelService, application);
		if(parts.isEmpty())return null;
		return parts.get(0);
	}
	
	private static List<MPart> getPartList(String partId,String tag,EModelService modelService,MApplication application){
		List<String> tags=new LinkedList<String>();
		tags.add(tag);
		//tags.add(optimizationType);
			
		List<MPart> parts=modelService.findElements(application,
				partId, MPart.class,tags );
		return parts;
	}
}
