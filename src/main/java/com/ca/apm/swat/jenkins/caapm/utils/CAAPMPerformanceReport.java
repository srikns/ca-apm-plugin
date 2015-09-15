package com.ca.apm.swat.jenkins.caapm.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.ca.apm.swat.jenkins.caapm.utils.MetricDataCollectionHelper.MetricData;

/***
 * 
 * @author Srikant N @ CA Technology
 * Dt: Sep 2015
 *
 */


public class CAAPMPerformanceReport
{
    //DataCollector dataCollector;
       
    String reportName;
    String failPassReason;
    MetricDataCollectionHelper metricDataCollectionHelper;
    //HashMap<String,SingleMetricDataCollection> metricDataCollection;
    
    public CAAPMPerformanceReport(String name, MetricDataCollectionHelper metricDataCollectionHelper) {
        this.reportName = name;
        this.metricDataCollectionHelper = metricDataCollectionHelper;

    }
    
    public CAAPMPerformanceReport generateReport() {
                
        return this;
    }
    
    
    public String getFailPassReason() {
        return failPassReason;
    }
    
    public void setFailPassReason( String failPassReason) {
        this.failPassReason = failPassReason;
    }
    
    public String getReportName() {
        return reportName;
    }
    
    public long getARTForBuild(String metricKey) {
        if ( metricDataCollectionHelper == null || metricDataCollectionHelper.getMetricKeyToMetricDataMap() == null ||
                metricDataCollectionHelper.getMetricKeyToMetricDataMap().get(metricKey) == null ){
            return 0;
        }
        return metricDataCollectionHelper.getMetricKeyToMetricDataMap().get(metricKey).getAverageART();
    }
    
    public MetricDataCollectionHelper getMetricDataCollectionHelper() {
        return metricDataCollectionHelper;
    }
    
    public Collection<MetricData> getMetricDataCollection() {
        
        try {
            return metricDataCollectionHelper.getMetricKeyToMetricDataMap().values();
    
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        
        return null;
    }
    
    public Collection<String> getMetricKeySet() {
        
        try {
            return metricDataCollectionHelper.getMetricKeyToMetricDataMap().keySet();
    
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        
        return null;
    }
    
 //   public void setARTForBuild() {
   //     this.ARTForBuild = ARTForBuild;
  //  }
    
    public String getMetricName() {
        
        return "CA APM Performance Report Dont know how to do it";
    }
    

}
