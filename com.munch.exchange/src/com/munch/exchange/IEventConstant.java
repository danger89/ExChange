package com.munch.exchange;

public interface IEventConstant {
	
	//RATE
	String RATE_ALLTOPICS = "RATE/*";
	String RATE_NEW = "RATE/NEW";
	String RATE_DELETE = "RATE/DELETED";
	String RATE_UPDATE = "RATE/UPDATED";
	String RATE_LOADING = "RATE/LOADING";
	String RATE_LOADED = "RATE/LOADED";
	String RATE_OPEN = "RATE/OPEN";
	
	//QUOTE
	String QUOTE_ALLTOPICS = "QUOTE/*";
	String QUOTE_LOADED = "QUOTE/LOADED";
	String QUOTE_UPDATE = "QUOTE/UPDATE";
	
	//HISTORICAL_DATA
	String HISTORICAL_DATA_ALLTOPICS = "HISTORICAL_DATA/*";
	String HISTORICAL_DATA_LOADED = "HISTORICAL_DATA/LOADED";
	String HISTORICAL_DATA_LOADING = "HISTORICAL_DATA/LOADING";
	String HISTORICAL_DATA_UPDATE = "HISTORICAL_DATA/UPDATE";
	String HISTORICAL_DATA_CLEARED = "HISTORICAL_DATA/CLEARED";
	
	//FINANCIAL_DATA
	String FINANCIAL_DATA_ALLTOPICS = "FINANCIAL_DATA/*";
	String FINANCIAL_DATA_LOADED = "FINANCIAL_DATA/LOADED";
	String FINANCIAL_DATA_LOADING = "FINANCIAL_DATA/LOADING";
	String FINANCIAL_DATA_UPDATE = "FINANCIAL_DATA/UPDATE";
	String FINANCIAL_DATA_CLEARED = "FINANCIAL_DATA/CLEARED";
	String FINANCIAL_DATA_COMPANY_SIDE = "FINANCIAL_DATA/COMPANY_SIDE";
	
	//NEURAL_NETWORK_DATA
	String NEURAL_NETWORK_DATA_ALLTOPICS = "NEURAL_NETWORK_DATA/*";
	String NEURAL_NETWORK_DATA_LOADED = "NEURAL_NETWORK_DATA/LOADED";
	String NEURAL_NETWORK_DATA_LOADING = "NEURAL_NETWORK_DATA/LOADING";
	String NEURAL_NETWORK_DATA_UPDATE = "NEURAL_NETWORK_DATA/UPDATE";
	String NEURAL_NETWORK_DATA_CLEARED = "NEURAL_NETWORK_DATA/CLEARED";
	
	String NEURAL_NETWORK_NEW_CURRENT = "NEURAL_NETWORK_DATA/NEW_CURRENT";
	String NEURAL_NETWORK_CONFIG_SELECTED = "NEURAL_NETWORK/CONFIG/SELECTED";
	String NEURAL_NETWORK_CONFIG_DIRTY = "NEURAL_NETWORK/CONFIG/DIRTY";
	String NEURAL_NETWORK_CONFIG_INPUT_EDITING = "NEURAL_NETWORK/CONFIG/INPUT_EDITING";
	String NEURAL_NETWORK_CONFIG_INPUT_SAVED = "NEURAL_NETWORK/CONFIG/INPUT_SAVED";
	//String NEURAL_NETWORK_CONFIG_INPUT_SAVING_STEP = "NEURAL_NETWORK/CONFIG/SAVING_STEP";
	String NEURAL_NETWORK_CONFIG_INPUT_CANCELED = "NEURAL_NETWORK/CONFIG/INPUT_CANCELED";
	
	String NEURAL_NETWORK_CONFIG_RESULTS_CALCULATED = "NEURAL_NETWORK/CONFIG/RESULTS/CALCULATED";
	String NEURAL_NETWORK_CONFIG_RESULTS_REFRESH_CALLED = "NEURAL_NETWORK/CONFIG/RESULTS/REFRESH/CALLED";
	String NEURAL_NETWORK_CONFIG_RESULTS_LOADING_CALLED = "NEURAL_NETWORK/CONFIG/RESULTS/LOADING/CALLED";
	String NEURAL_NETWORK_CONFIG_RESULTS_UNLOADING_CALLED = "NEURAL_NETWORK/CONFIG/RESULTS/UNLOADING/CALLED";
	
	
	//NETWORK_ARCHITECTURE_OPTIMIZATION
	String NETWORK_ARCHITECTURE_OPTIMIZATION_ALLTOPICS = "NETWORK_ARCHITECTURE_OPTIMIZATION/*";
	String NETWORK_ARCHITECTURE_OPTIMIZATION_STARTED = "NETWORK_ARCHITECTURE_OPTIMIZATION/STARTED";
	String NETWORK_ARCHITECTURE_OPTIMIZATION_FINISHED = "NETWORK_ARCHITECTURE_OPTIMIZATION/FINISHED";
	String NETWORK_ARCHITECTURE_OPTIMIZATION_SAVED = "NETWORK_ARCHITECTURE_OPTIMIZATION/SAVED";
	String NETWORK_ARCHITECTURE_OPTIMIZATION_NEW_BEST_INDIVIDUAL = "NETWORK_ARCHITECTURE_OPTIMIZATION/NEW_BEST_INDIVIDUAL";
	String NETWORK_ARCHITECTURE_OPTIMIZATION_NEW_STEP = "NETWORK_ARCHITECTURE_OPTIMIZATION/NEW_STEP";
	
	//NETWORK_OPTIMIZATION_MANAGER
	String NETWORK_OPTIMIZATION_MANAGER_ALLTOPICS = "NETWORK_OPTIMIZATION_MANAGER/*";
	String NETWORK_OPTIMIZATION_MANAGER_STARTED = "NETWORK_OPTIMIZATION_MANAGER/STARTED";
	String NETWORK_OPTIMIZATION_MANAGER_FINISHED = "NETWORK_OPTIMIZATION_MANAGER/FINISHED";
	String NETWORK_OPTIMIZATION_MANAGER_WORKER_STATE_CHANGED = "NETWORK_OPTIMIZATION_MANAGER/WORKER_STATE_CHANGED";
	
	String NETWORK_OPTIMIZATION_MANAGER_SAVED = "NETWORK_OPTIMIZATION_MANAGER/SAVED";
	String NETWORK_OPTIMIZATION_MANAGER_NEW_STEP = "NETWORK_OPTIMIZATION_MANAGER/NEW_STEP";
	
	
	//NETWORK_OPTIMIZATION
	String NETWORK_OPTIMIZATION_ALLTOPICS = "NETWORK_OPTIMIZATION/*";
	String NETWORK_OPTIMIZATION_STARTED = "NETWORK_OPTIMIZATION/STARTED";
	String NETWORK_OPTIMIZATION_LOOP = "NETWORK_OPTIMIZATION/LOOP";
	String NETWORK_OPTIMIZATION_FINISHED = "NETWORK_OPTIMIZATION/FINISHED";
	String NETWORK_OPTIMIZATION_SAVED = "NETWORK_OPTIMIZATION/SAVED";
	String NETWORK_OPTIMIZATION_NEW_BEST_INDIVIDUAL = "NETWORK_OPTIMIZATION/NEW_BEST_INDIVIDUAL";
	String NETWORK_OPTIMIZATION_NEW_STEP = "NETWORK_OPTIMIZATION/NEW_STEP";
	
	//LEARNING
	String NETWORK_LEARNING_ALLTOPICS = "NETWORK_LEARNING/*";
	String NETWORK_LEARNING_STARTED = "NETWORK_LEARNING/STARTED";
	
	//OPTIMIZATION
	String OPTIMIZATION_ALLTOPICS = "OPTIMIZATION/*";
	String OPTIMIZATION_STARTED = "OPTIMIZATION/STARTED";
	String OPTIMIZATION_FINISHED = "OPTIMIZATION/FINISHED";
	String OPTIMIZATION_SAVED = "OPTIMIZATION/SAVED";
	String OPTIMIZATION_NEW_BEST_INDIVIDUAL = "OPTIMIZATION/NEW_BEST_INDIVIDUAL";
	String OPTIMIZATION_NEW_STEP = "OPTIMIZATION/NEW_STEP";
	
	String OPTIMIZATION_RESULTS_LOADED = "OPTIMIZATION/RESULTS/LOADED";
	
	//CHART
	String PERIOD_CHANGED = "PERIOD/CHANGED";
	String CHART_INDICATOR_SELECTED = "CHART/INDICATOR/SELECTED";
	String CHART_INDICATOR_ACTIVATION_CHANGED = "CHART/INDICATOR/ACTIVATION/CHANGED";
	
	
	
	//TEXT
	String TEXT_INFO = "TEXT/INFO";
	
	
}
