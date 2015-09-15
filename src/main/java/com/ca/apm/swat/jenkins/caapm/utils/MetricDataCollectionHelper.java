package com.ca.apm.swat.jenkins.caapm.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 
 * @author Srikant N @ CA Technology
 * Dt: Sep 2015
 *
 */


//contains complete set of data for a metric
public class MetricDataCollectionHelper {

    //private List<MetricData> metricDataCollection1;

    private Map<String, MetricData> metricKeyToMetricDataMap;


    public MetricDataCollectionHelper () {

        metricKeyToMetricDataMap = new HashMap<String, MetricData>();
    }

    public MetricData getMetricData( String metricKey ) {
        return metricKeyToMetricDataMap.get(metricKey);
    }


    public void addMetricData(String metricKey, long min, long max, long value, long count, String time, int frequency ) {


        MetricData metricData = metricKeyToMetricDataMap.get(metricKey);

        if( metricData == null ) {
            metricData = new MetricData();
        }

        metricData.addMetricDataPoint(metricKey, min, max, value, count, time, frequency);

        metricKeyToMetricDataMap.put(metricKey, metricData);

    }


    public boolean containsKey(String metricKey) {

        return metricKeyToMetricDataMap.containsKey(metricKey);

    }

    public Map<String, MetricData> getMetricKeyToMetricDataMap() {

        return metricKeyToMetricDataMap;
    }

    public Collection<MetricData> getMetricDataCollection() {

        return metricKeyToMetricDataMap.values();
    }


    public class MetricData {

        int frequencyInSec = 15;
        int averageART = 0;
        String metricName;
        List<DataPoint> dataPoints;

        public MetricData() {
            dataPoints = new ArrayList<DataPoint>(); 
        }

        public void addMetricDataPoint(String metricKey, long min, long max, long value, long count, String time, int frequency ) {
            this.metricName = metricKey;
            this.frequencyInSec = frequency;
            this.dataPoints.add(new DataPoint( min, max, value, count, time ));

            averageART = updateAverageART();
        }

        public List<DataPoint> getDataPoints() {
            return dataPoints;
        }

        public String getMetricName() {
            return metricName;
        }
        
        public int getFrequency() {
        
            return frequencyInSec;
        }

        public int getAverageART() {
            return averageART;
        }
        
        int updateAverageART() {

            int size = dataPoints.size();

            int tempART = 0;

            for ( int i = 0; i < size; i++ ) {
                tempART += dataPoints.get(i).getValue();
            }

            tempART /= size;
            
            return tempART;

        }
    }


    public class DataPoint {
        public  long min;
        public long max;
        public long value;
        public long count;
        public String startOrEndtime;

        public  DataPoint( long min, long max, long value, long count, String startOrEndtime  ) {
            this.min = min;
            this.max = max;
            this.value = value;
            this.count = count;
            this.startOrEndtime = startOrEndtime;
        }

        public long getValue() {
            return value;
        }

        public long getMax() {
            return max;
        }

        public String getTime() {
            return startOrEndtime;
        }

        public long getMin() {
            return min;
        }

        public long getCount() {
            return count;
        }
    }
}
