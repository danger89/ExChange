 
package com.munch.exchange.parts;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.parts.composite.OverviewRateChart;
import com.munch.exchange.parts.composite.RateCommonInfoGroup;
import com.munch.exchange.parts.composite.RateTitle;
import com.munch.exchange.parts.composite.StockInfoGroup;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.IKeyStatisticProvider;

public class RateEditorPart {
	
	private static Logger logger = Logger.getLogger(RateEditorPart.class);
	
	private DataBindingContext m_bindingContext;
	
	
	public static final String RATE_EDITOR_ID="com.munch.exchange.partdescriptor.rateeditor";
	
	@Inject
	ExchangeRate rate;
	
	@Inject
	IEclipseContext context;
	
	@Inject
	IKeyStatisticProvider keyStatisticProvider; 
	
	@Inject
	IExchangeRateProvider exchangeRateProvider;
	
	RateTitle titleComposite;
	OverviewRateChart chartComposite;
	RateCommonInfoGroup commonInfoComposite;
	
	//private Label lblTitle;
	
	@Inject
	public RateEditorPart() {
		//TODO Your code here
	}
	
	@PostConstruct
	public void postConstruct(Composite parent,Shell shell) {
		
		
		TabFolder tabFolder = new TabFolder(parent, SWT.BOTTOM);
		tabFolder.setBounds(0, 0, 122, 43);
		
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("Overview");
		
		Composite compositeOverview = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(compositeOverview);
		compositeOverview.setLayout(new GridLayout(1, false));
		
		
		//Create a context instance
		IEclipseContext localContact=EclipseContextFactory.create();
		localContact.set(Composite.class, compositeOverview);
		localContact.setParent(context);
		
		//////////////////////////////////
		//Create the Title Composite
		//////////////////////////////////
		titleComposite=ContextInjectionFactory.make( RateTitle.class,localContact);
		//Composite compositeTitle = new Composite(compositeOverview, SWT.NONE);
		titleComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		//////////////////////////////////
		//Create the Info Composite
		//////////////////////////////////
		Composite composite_Info = new Composite(compositeOverview, SWT.NONE);
		composite_Info.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		composite_Info.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		commonInfoComposite = new RateCommonInfoGroup(composite_Info, SWT.NONE,rate);
		
		if(rate instanceof Stock){
			new StockInfoGroup(composite_Info,(Stock) rate,shell,exchangeRateProvider);
		}
		
		
		//////////////////////////////////
		//Create the Overview Chart
		//////////////////////////////////
		chartComposite=ContextInjectionFactory.make(OverviewRateChart.class,localContact);
		//chart=new OverviewRateChart(compositeOverview);
		chartComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
	}
	
	
	@PreDestroy
	public void preDestroy() {
		
		//Clear the historical Data
		if(!rate.getHistoricalData().isEmpty()){
			rate.getHistoricalData().clear();
		}
		
	}
	
	
	@Focus
	public void onFocus() {
		//TODO Your code here
	}
	
	
	@Persist
	public void save() {
		//TODO Your code here
	}
	
}