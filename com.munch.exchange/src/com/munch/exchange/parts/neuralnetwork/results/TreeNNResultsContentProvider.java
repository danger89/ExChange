package com.munch.exchange.parts.neuralnetwork.results;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;
import com.munch.exchange.model.core.optimization.ResultEntity;

public class TreeNNResultsContentProvider implements
		IStructuredContentProvider, ITreeContentProvider {
	
	
	
	@Override
	public void dispose() {
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof Configuration){
			Configuration p_el=(Configuration)parentElement;
			return p_el.getNetworkArchitectures().toArray();
			
		}
		if(parentElement instanceof NetworkArchitecture){
			NetworkArchitecture p_el=(NetworkArchitecture)parentElement;
			if(!p_el.hasResults())return null;
			return p_el.getResultsEntities().toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof NetworkArchitecture){
			NetworkArchitecture arch=(NetworkArchitecture)element;
			return arch.getParent();
		}
		if(element instanceof ResultEntity){
			ResultEntity res=(ResultEntity)element;
			return res.getParentId();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof Configuration){
			Configuration el=(Configuration)element;
			return el.getNetworkArchitectures().size()>0;
		}
		if(element instanceof NetworkArchitecture){
			//return true;
			NetworkArchitecture el=(NetworkArchitecture)element;
			return el.hasResults();
		}
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof Configuration 
				|| inputElement instanceof NetworkArchitecture){
			return this.getChildren(inputElement);
		}
		return null;
	}

}
