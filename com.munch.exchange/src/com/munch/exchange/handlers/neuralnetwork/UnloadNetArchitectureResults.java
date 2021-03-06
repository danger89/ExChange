 
package com.munch.exchange.handlers.neuralnetwork;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;

public class UnloadNetArchitectureResults {
	
	
	private NetworkArchitecture archi=null;
	//private Configuration config=null;
	private boolean canExec=false;
	
	@Inject
	private IEventBroker eventBroker;
	
	@Execute
	public void execute() {
		if(archi==null)return;
		
		archi.clearResultsAndNetwork(false);
		eventBroker.send(IEventConstant.NEURAL_NETWORK_CONFIG_RESULTS_UNLOADING_CALLED,archi);
		
		
	}
	
	
	@CanExecute
	public boolean canExecute() {
		if(this.archi !=null )
			return true;
		
		return false;
	}
	
	//################################
  	//##       Event Reaction       ##
  	//################################
	@Inject
	public void analyseSelection( @Optional  @Named(IServiceConstants.ACTIVE_SELECTION) 
	NetworkArchitecture selArchi){
    	archi=selArchi;
    	if(archi==null)
    		canExec=false;
    	else
    		canExec=true;
    	
	}
	
	
	
	
	
		
}