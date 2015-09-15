package com.ca.apm.swat.jenkins.caapm.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import hudson.model.AbstractBuild;

/***
 * 
 * @author Srikant N @ CA Technology
 * Dt: Sep 2015
 *
 *NOT Being Used Currently but pls provides ability to use CLW
 */


public class CLWDataCollector
{
    /*
    MomConnection momConnection;
    AbstractBuild<?, ?> build;
    int timeRangeInMts;
    long ARTForBuild=0;

    private static final Logger LOGGER = Logger.getLogger(CLWDataCollector.class.getName());

    HashMap<String,SingleMetricDataCollection> metricDataPointCollection = new HashMap<String,SingleMetricDataCollection>();

    public CLWDataCollector( MomConnection momConnection, AbstractBuild<?, ?> build,  int timeRangeInMts) {
        this.momConnection = momConnection;
        this.build = build;
        this.timeRangeInMts = timeRangeInMts;
    }

    public CAAPMPerformanceReport collectDataAndGenerateReport( String metricKey) {

        SingleMetricDataCollection smdCollection = collectMetricDataFromCLW("my metric key");

        CAAPMPerformanceReport report = new CAAPMPerformanceReport("CA APM Report - Build " + build.number, smdCollection, ARTForBuild);

        return report;
    }

    public SingleMetricDataCollection collectMetricDataFromCLW(String metricKey) {

        CAAPMCLWCommand clwCommand = new CAAPMCLWCommand(momConnection, "agentPath", "metricPath", 60, timeRangeInMts);

        StringBuffer queryOutput = clwCommand.executeCLWQuery();

        //somehow get it from MOM using mom connection
        List<DataPoint> dataPointCollection = new ArrayList<DataPoint>();

        metricKey = "Default";

        String[] newLineParse = queryOutput.toString().split("\n");

        int numberOfRows = newLineParse.length;

        LOGGER.log(Level.INFO, "InCollect Metric Data 1 " + newLineParse.length);
        
        try {
            for (int i = 1; i < numberOfRows; i++ ) {

                LOGGER.log(Level.INFO, " In Collect - " + newLineParse[i] );

                String[] temp = newLineParse[i].split(",");

                metricKey =  temp[0] + "|" + temp[1] + "|" + temp[2] + "|" + temp[3] + "|" + temp[4] + ":" + temp[5];

                LOGGER.log(Level.INFO, "InCollect Metric Data Metric Key " + metricKey + " date is " + temp[9] + " value is " + temp[13]);


                dataPointCollection.add(new DataPoint(temp[14],temp[15],temp[13],temp[11],temp[9]));
                
                ARTForBuild += new Long(temp[13]).longValue();

            }
            
            ARTForBuild /= numberOfRows;
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        LOGGER.log(Level.INFO, "InCollect Metric Data AFTER " + newLineParse.length);

        SingleMetricDataCollection smdCollection = new SingleMetricDataCollection( metricKey, 60, dataPointCollection );

        return smdCollection;

    }


    //contains complete set of data for a metric
    public class SingleMetricDataCollection {

        String metricName;
        int frequency = 15;      
        List<DataPoint> dataPointCollection;

        public SingleMetricDataCollection( String metricName, int frequency, List<DataPoint> dataPointCollection ) {
            this.metricName = metricName;
            this.frequency = frequency;
            this.dataPointCollection = dataPointCollection;

        }


        public void addToDataPointCollection(DataPoint dp) {

            dataPointCollection.add(dp);

        }

        public int getFrequency() {
            return frequency;
        }

        public String getMetricName() {
            return metricName;
        }

        public List<DataPoint> getDataPointCollection() {
            return dataPointCollection;
        }

    }

    public class DataPoint {
        public  String min;
        public String max;
        public String value;
        public String count;
        public String time;

        public  DataPoint( String min, String max, String value, String count, String time ) {
            this.min = min;
            this.max = max;
            this.value = value;
            this.count = count;
            this.time = time;
        }

        public String getValue() {
            return value;
        }

        public String getMax() {
            return max;
        }

        public String getTime() {
            return time;
        }

        public String getMin() {
            return min;
        }
    }
*/
}
