package com.ca.apm.swat.jenkins.caapm.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

/***
 * 
 * @author Srikant N @ CA Technology
 * Dt: Sep 2015
 *
 */


public abstract class GenericDataCollector
{
    
    String momURL;
    String momPort;
    String frequency;
    String startTime;
    String endTime;
    static int timeout=5000;
    String numbDPToCollect;
    
    String CollectionMechanism;
    
    public static final int MAX_DATA_POINTS = 50;
    public static final int MIN_DATA_POINTS = 10;
    
    private static final Logger LOGGER = Logger.getLogger(GenericDataCollector.class.getName());
    
    public GenericDataCollector(String momURL, String momPort) {
        this.momURL = momURL;
        this.momPort = momPort;
    }
    
    public static boolean testMomConnection( String momURL, String momPort)
    {
        try {

            String serverURL = "http://" + momURL + ":" + momPort + "/data/demo";

            HttpClientBuilder client =  HttpClientBuilder.create();
            HttpGet request = new HttpGet(serverURL);
            
            RequestConfig.Builder requestBuilder = RequestConfig.custom();
            requestBuilder = requestBuilder.setConnectTimeout(timeout);
            
            client.setDefaultRequestConfig(requestBuilder.build());
            CloseableHttpResponse response = client.build().execute(request);

            LOGGER.log(Level.FINE, "Generic Data Collector http connection response code " + response.getStatusLine().getStatusCode());
            
            if ( response.getStatusLine().getStatusCode() == 200 ) {
                return true;
            }

        }  catch (Exception ex ) {
            ex.printStackTrace();
        }

        return false;
    }
    
    public static String testRegex(String momHost, String port, String username, String agentPath, String metricPath) {
        return "ToDo - GenericDataCollector";
    }
    
    public abstract MetricDataCollectionHelper fetchLastNMetricData(String agentRegex, String metricRegex, long frequencyInSec, 
                                                   long startTimeInMS, long endTimeInMS, int numbDPToCollect) ;
    
    public abstract MetricDataCollectionHelper fetchAllMetricData(String agentRegex, String metricRegex, 
                                                                    long startTimeInMS, long endTimeInMS) ;
    
}
