package com.munch.exchange.services.internal;

import java.io.File;
import java.util.HashMap;

import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.xml.Xml;
import com.munch.exchange.services.IExchangeRateProvider;
import com.munch.exchange.services.internal.yql.YQLQuotes;
import com.munch.exchange.services.internal.yql.YQLStocks;

public class ExchangeRateProviderLocalImpl implements IExchangeRateProvider {
	
	
	private String workspace;
	final private static String ExchangeRateStr="ExchangeRate.xml";
	
	
	
	@Override
	public void init(String workspace) {
		this.workspace=workspace;
	}
	
	
	private String getExchangeRatePath(ExchangeRate rate) {
		return this.workspace + File.separator
				+ rate.getClass().getSimpleName() + File.separator
				+ rate.getSymbol();
	}
	
	/**
	 * return the directory where the data of the ExchangeRate will be saved
	 * @param rate
	 * @return
	 */
	private File getExchangeRateDir(ExchangeRate rate){
		File dir=new File(this.getExchangeRatePath(rate));
		if(!dir.exists()){
			if(dir.mkdirs())
				return dir;
			else return null;
		}
		
		if(!dir.isDirectory())return null;
		return dir;
	}
	
	
	private boolean save(ExchangeRate rate) {
		if(rate==null)return false;
		
		File dir=this.getExchangeRateDir(rate);
		if(dir==null)return false;
		String exchangeRateFile=dir.getAbsolutePath()+File.separator+ExchangeRateStr;
		rate.setDataPath(dir.getAbsolutePath());
		System.out.println("Writing file: "+exchangeRateFile);
		// TODO Auto-generated method stub
		return Xml.save(rate, exchangeRateFile);
	}
	
	
	/**
	 * find all exchange rates file saved in the local workspace
	 * 
	 * @return
	 */
	private HashMap<String, File> findAllLocalRateFiles(){
		HashMap<String, File> localRateFiles=new HashMap<String, File>();
		
		File workspaceDir = new File(this.workspace);
		if (!workspaceDir.exists() || !workspaceDir.isDirectory())
			return localRateFiles;
		
		File[] subDirs = workspaceDir.listFiles();
		if (subDirs.length == 0)
			return localRateFiles;
		for (int i = 0; i < subDirs.length; i++) {
			File subDir = subDirs[i];
			if (!subDir.isDirectory())
				continue;
			
			File[] XChangeDirs = subDir.listFiles();

			for (int j = 0; j < XChangeDirs.length; j++) {
				
				if (!XChangeDirs[j].isDirectory())continue;
				
				String symbol=XChangeDirs[j].getName();
				
				File localFile=new File(XChangeDirs[j].getAbsolutePath()+File.separator+ExchangeRateStr);
				
				if(localFile.exists() && localFile.canRead()){
					
					localRateFiles.put(symbol, localFile);
				}
					
			}
		}
		
		return localRateFiles;
	}
	
	private ExchangeRate findLocalRateFromSymbol(String symbol){
		
		HashMap<String, File> localRateFiles=findAllLocalRateFiles();
		if(!localRateFiles.containsKey(symbol))
			return null;
		
		String localFile=localRateFiles.get(symbol).getAbsolutePath();
		String rateClassName=localRateFiles.get(symbol).getParentFile().getParentFile().getName();
		ExchangeRate XRate=null;
		
		if(!localFile.isEmpty() && !rateClassName.isEmpty()){
			if(Stock.class.getSimpleName().equals(rateClassName)){
				XRate=new Stock();
			}
			
			if(XRate!=null && Xml.load(XRate, localFile)){
				XRate.setDataPath(localRateFiles.get(symbol).getParent());
				return XRate;
			}
			
		}
		
		return null;
		
	}
	
	@Override
	public ExchangeRate load(String symbol) {
		
		//Try to load the exchange rate from the local data
		ExchangeRate xchangeRate=findLocalRateFromSymbol(symbol);
		if(xchangeRate!=null){
			System.out.println("The ExchangeRate was found localy:\n \""+xchangeRate);
			update(xchangeRate);
			return xchangeRate;
		}
		
		//Try to load the given symbol directly from YQL
		YQLStocks yqlStocks=new YQLStocks(symbol);
		if(!yqlStocks.hasValidResult()){
			System.out.println("Cannot find the symbol \""+symbol+"\" on YQL");
			return null;
		}
		
		ExchangeRate rate=yqlStocks.getExchangeRate();
		//Search the company name
		if(rate!=null){
			YQLQuotes yqlQuotes=new YQLQuotes(symbol);
			rate.setName(yqlQuotes.getName());
			rate.setStockExchange(yqlQuotes.getStockExchange());
		}
		
		//Save the new Exchange Rate:
		if(this.save(rate))
		return rate;
		else
			return null;
		
	}

	@Override
	public boolean update(ExchangeRate rate) {
		
		boolean isUpdated=false;
		
		YQLStocks yqlStocks=new YQLStocks(rate.getSymbol());
		if(!yqlStocks.hasValidResult()){
			System.out.println("Cannot find the symbol \""+rate.getSymbol()+"\" on YQL");
			return isUpdated;
		}
		
		// Update the End Date from YQL
		if(rate instanceof Stock){
			Stock stock=(Stock)rate;
			if(!stock.getEnd().equals(yqlStocks.getEndDate())){
				stock.setEnd(yqlStocks.getEndDate());
				isUpdated=true;
			}
			
			if(stock.isParentUpdateNeeded()){
				YQLStocks yqlStockParent=new YQLStocks(stock.getParentSymbol());
				if(!yqlStockParent.hasValidResult()){
					System.out.println("Cannot find the given parent symbol \""+stock.getParentSymbol()+"\" on YQL");
				}
				else{
					YQLQuotes yqlQuotes=new YQLQuotes(stock.getParentSymbol());
					if(!stock.getParentName().equals(yqlQuotes.getName())){
						stock.setParentName(yqlQuotes.getName());isUpdated=true;
					}
					
					if(stock.getSector()==null || stock.getSector().isEmpty()){
						stock.setSector(yqlStockParent.getSector());isUpdated=true;
					}
					
					if(stock.getIndustry()==null || stock.getIndustry().isEmpty()){
						stock.setIndustry(yqlStockParent.getIndustry());isUpdated=true;
					}
					
				}
				
			}
			
			
			
		}
		
		if(isUpdated){
			System.out.println("The ExchangeRate was updated:\n \""+rate);
			if(this.save(rate)){
				System.out.println("The new Data were automaticaly saved!");
			}
			else{
				System.out.println("Error: cannot save the updated data!");
				return false;
			}
		}
		
		return false;
	}

	public static void main(String[] args) {
		
		
		ExchangeRateProviderLocalImpl provider=new ExchangeRateProviderLocalImpl();
		provider.init("D:\\Paul\\04_Programierung\\03_Boerse\\01_PROG_DATA");
		//provider.save(rate);
		
		//ExchangeRate rate=provider.load("AMZ.DE");
		
		ExchangeRate rate=provider.load("DTE.DE");
		
		
		/*
		if(rate instanceof Stock){
			Stock stock=(Stock)rate;stock.setParentSymbol("AMZN");
			provider.update(stock);
		}
		*/
		
		//provider.save(rate);
		
		

	}

}
