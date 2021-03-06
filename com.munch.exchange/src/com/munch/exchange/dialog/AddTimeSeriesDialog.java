package com.munch.exchange.dialog;

import java.util.LinkedList;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.neuralnetwork.timeseries.TimeSeries;
import com.munch.exchange.model.core.neuralnetwork.timeseries.TimeSeriesCategory;
import com.munch.exchange.model.core.neuralnetwork.timeseries.TimeSeriesGroup;

public class AddTimeSeriesDialog extends TitleAreaDialog {
	private Combo comboTimeSeries;
	
	
	private TimeSeriesGroup group;
	private Configuration config;
	private TimeSeries series=null;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public AddTimeSeriesDialog(Shell parentShell,TimeSeriesGroup group,Configuration config) {
		super(parentShell);
		this.group=group;
		this.config=config;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage("Please select a time serie to add");
		setTitle("Add Time Serie");
		Composite area = (Composite) super.createDialogArea(parent);
		area.setLayout(new GridLayout(1, false));
		
		comboTimeSeries = new Combo(area, SWT.NONE);
		comboTimeSeries.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		fillComboTimeSerie();
		
		return area;
	}
	
	private void fillComboTimeSerie(){
		LinkedList<String> allSeries=TimeSeriesGroup.getAvailableSerieNames(group);
		if(allSeries.size()==0)return;
		
		for(TimeSeries series:group.getTimeSeriesList()){
			allSeries.remove(series.getName());
		}
		
		for(String name:allSeries){
			comboTimeSeries.add(name);
		}
		if(allSeries.size()==0)return;
		
		comboTimeSeries.setText(allSeries.getFirst());
		
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

	@Override
	protected void okPressed() {
		
		if(comboTimeSeries.getItemCount()>0){
			series=new TimeSeries(comboTimeSeries.getText());
			group.addTimeSeries(series);
			//config.setDirty(true);
			//config.addTimeSeries(series);
		}
		
		super.okPressed();
	}

	public TimeSeries getSeries() {
		return series;
	}
	
	
	
	
	
}
