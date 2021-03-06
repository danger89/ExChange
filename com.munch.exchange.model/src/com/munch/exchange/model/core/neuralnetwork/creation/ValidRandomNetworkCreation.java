package com.munch.exchange.model.core.neuralnetwork.creation;

import java.util.Random;

import org.goataa.impl.searchOperations.strings.bits.booleans.nullary.BooleanArrayUniformCreation;

import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;

public class ValidRandomNetworkCreation extends BooleanArrayUniformCreation {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//private int numberOfInnerNeurons;
	private int numberOfInputNeurons;
	private String localSavePath;
	
	public ValidRandomNetworkCreation(int dim, int numberOfInputNeurons, String localSavePath) {
		super(dim);
		
		//this.numberOfInnerNeurons=NetworkArchitecture.calculateNbOfInnerNeurons(dim, numberOfInputNeurons);
		this.numberOfInputNeurons=numberOfInputNeurons;
		this.localSavePath=localSavePath;
	}
	
	@Override
	public boolean[] create(Random r) {
		if(oldResults!=null && !oldResults.isEmpty()){
			return oldResults.pollLast();
		} 
		
		return createValidRandomNetwork(this.n,numberOfInputNeurons, r,localSavePath);
		
	}
	
	
	public static int MAX_LOOPS=400;
	
	public static boolean[] createValidRandomNetwork(int dim, int numberOfInputNeurons, Random r, String localSavePath){
		int numberOfInnerNeurons=NetworkArchitecture.calculateNbOfInnerNeurons(dim, numberOfInputNeurons);
		
		boolean[] cons=null;
		int loop=0;
		while(true){
			cons=createRandomBooleanArray(dim,r);
			NetworkArchitecture arch = new NetworkArchitecture(
					numberOfInputNeurons, numberOfInnerNeurons, cons,localSavePath);
			if(arch.isValid()){
				//System.out.println(arch);
				break;
			}
			
			loop++;
			if(loop>MAX_LOOPS)break;
		}
		
		return cons;
	}
	
	public static boolean[] createRandomBooleanArray(int dim,Random r){
		boolean[] bs;
	    int i;

	    i = dim;
	    bs = new boolean[i];

	    for (; (--i) >= 0;) {
	      bs[i] = r.nextBoolean();
	    }

	    return bs;
	}
	
	
	public static void main(String[] args){
		Random r=new Random();
		int numberOfInputNeurons=15;
		int numberOfInnerNeurons=15;
		int dim=NetworkArchitecture.calculateActivatedConnectionsSize(numberOfInputNeurons, numberOfInnerNeurons);
		
		createValidRandomNetwork(dim,numberOfInputNeurons,r,"");
	}

}
