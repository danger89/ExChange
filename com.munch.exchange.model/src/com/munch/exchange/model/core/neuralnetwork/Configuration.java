package com.munch.exchange.model.core.neuralnetwork;

import java.util.Calendar;
import java.util.LinkedList;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.core.optimization.OptimizationResults;
import com.munch.exchange.model.core.optimization.OptimizationResults.Type;
import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.model.xml.XmlParameterElement;

public class Configuration extends XmlParameterElement {
	
	
	static final String FIELD_Period="Period";
	static final String FIELD_DayOfWeekActivated="DayOfWeekActivated";
	static final String FIELD_Name="Name";
	static final String FIELD_LastUpdate="LastUpdate";
	static final String FIELD_AllTimeSeries="AllTimeSeries";
	static final String FIELD_OutputPointList="OutputPointList";
	static final String FIELD_LastInputPointDate="LastInputPointDate";
	
	private PeriodType period=PeriodType.DAY;
	private boolean dayOfWeekActivated=false;
	private String Name="New Neural Network Configuration";
	private Calendar lastUpdate=Calendar.getInstance();
	
	private LinkedList<TimeSeries> allTimeSeries=new LinkedList<TimeSeries>();
	private ValuePointList outputPointList=new ValuePointList();
	
	private Calendar lastInputPointDate=null;
	
	private MultiLayerPerceptron currentNetwork;
	
	
	public MultiLayerPerceptron getCurrentNetwork() {
		return currentNetwork;
	}

	public void setCurrentNetwork(MultiLayerPerceptron currentNetwork) {
	this.currentNetwork = currentNetwork;
	}
	

	public DataSet createTrainingDataSet(){
		
		
		LinkedList<double[]> doubleArrayList=new LinkedList<double[]>();
		for(TimeSeries series:allTimeSeries){
			doubleArrayList.addAll(series.transformSeriesToDoubleArrayList(lastInputPointDate));
		}
		
		double[] outputArray=createOutputArray(doubleArrayList.get(0).length);
		
		DataSet trainingSet = new DataSet(doubleArrayList.size(), 1);
		
		
		for(int i=0;i<doubleArrayList.get(0).length;i++){
			
			//TODO delete
			if(i>600)break;
			double[] input=new double[doubleArrayList.size()];
			double[] output=new double[]{outputArray[i]};
			
			for(int j=0;j<doubleArrayList.size();j++){
				input[j]=doubleArrayList.get(j)[i];
			}
			
			 trainingSet.addRow(new DataSetRow(input, output));
			
		}
		
		trainingSet.normalize();
		
		return trainingSet;
	}
	
	private double[] createOutputArray(int maxNumberOfValues){
		double[] outputArray=new double[maxNumberOfValues];
		double[] tt=outputPointList.toDoubleArray();
		int diff=outputPointList.toDoubleArray().length-maxNumberOfValues;
		for(int i=tt.length-1;i>=0;i--){
			if(i-diff<0)break;
			outputArray[i-diff]=tt[i];
		}
		
		return outputArray;
		
	}
	
	
	
	public Type getOptimizationResultType(){
		switch (period) {
		case DAY:
			return Type.NEURAL_NETWORK_OUTPUT_DAY;
		case HOUR:
			return Type.NEURAL_NETWORK_OUTPUT_HOUR;
		case MINUTE:
			return Type.NEURAL_NETWORK_OUTPUT_MINUTE;
		case SECONDE:
			return Type.NEURAL_NETWORK_OUTPUT_SECONDE;

		default:
			return Type.NEURAL_NETWORK_OUTPUT_DAY;
		}
	}
	
	
	public Calendar getLastInputPointDate() {
		return lastInputPointDate;
	}

	public void setLastInputPointDate(Calendar lastInputPointDate) {
		if(this.lastInputPointDate==null){
			this.lastInputPointDate=lastInputPointDate;return;
		}
	
		if(this.lastInputPointDate.getTimeInMillis()<lastInputPointDate.getTimeInMillis()){
			this.lastInputPointDate=lastInputPointDate;return;
		}
	
	}
	

	public ValuePointList getOutputPointList() {
		return outputPointList;
	}

	public void setOutputPointList(ValuePointList outputPointList) {
	changes.firePropertyChange(FIELD_OutputPointList, this.outputPointList, this.outputPointList = outputPointList);}
	

	public boolean isDayOfWeekActivated() {
		return dayOfWeekActivated;
	}

	public void setDayOfWeekActivated(boolean dayOfWeekActivated) {
	changes.firePropertyChange(FIELD_DayOfWeekActivated, this.dayOfWeekActivated, this.dayOfWeekActivated = dayOfWeekActivated);}
	

	public PeriodType getPeriod() {
		return period;
	}

	public void setPeriod(PeriodType period) {
	changes.firePropertyChange(FIELD_Period, this.period, this.period = period);}
	
	public String getName() {
		return Name;
	}

	public void setName(String name) {
		changes.firePropertyChange(FIELD_Name, this.Name, this.Name = name);
	}

	public Calendar getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Calendar lastUpdate) {
	changes.firePropertyChange(FIELD_LastUpdate, this.lastUpdate, this.lastUpdate = lastUpdate);}
	

	public LinkedList<TimeSeries> getAllTimeSeries() {
		return allTimeSeries;
	}
	
	public LinkedList<TimeSeries> getTimeSeriesFromCategory(TimeSeriesCategory category){
		LinkedList<TimeSeries> list=new LinkedList<TimeSeries>();
		for(TimeSeries series:this.getAllTimeSeries()){
			if(series.getCategory()!=category)continue;
			list.add(series);
		}
		
		return list;
		
	}

	public void setAllTimeSeries(LinkedList<TimeSeries> allTimeSeries) {
	changes.firePropertyChange(FIELD_AllTimeSeries, this.allTimeSeries, this.allTimeSeries = allTimeSeries);}
	

	@Override
	protected void initAttribute(Element rootElement) {
		
		this.setName(rootElement.getAttribute(FIELD_Name));
		this.setLastUpdate(DateTool.StringToDate(rootElement.getAttribute(FIELD_LastUpdate)));
		
		this.setPeriod(PeriodType.fromString((rootElement.getAttribute(FIELD_Period))));
		this.setDayOfWeekActivated(Boolean.getBoolean(rootElement.getAttribute(FIELD_DayOfWeekActivated)));
		
		allTimeSeries.clear();
		
	}

	@Override
	protected void initChild(Element childElement) {
		TimeSeries ent=new TimeSeries();
		if(childElement.getTagName().equals(ent.getTagName())){
			ent.init(childElement);
			allTimeSeries.add(ent);
		}
		else if(childElement.getTagName().equals(outputPointList.getTagName())){
			outputPointList.init(childElement);
		}
		
	}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_Name,this.getName());
		rootElement.setAttribute(FIELD_LastUpdate,DateTool.dateToString( this.getLastUpdate()));
		
		rootElement.setAttribute(FIELD_Period,PeriodType.toString(this.getPeriod()));
		rootElement.setAttribute(FIELD_DayOfWeekActivated,String.valueOf(this.isDayOfWeekActivated()));
		
	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		for(TimeSeries ent:allTimeSeries){
			rootElement.appendChild(ent.toDomElement(doc));
		}
		rootElement.appendChild(outputPointList.toDomElement(doc));
		
	}

}