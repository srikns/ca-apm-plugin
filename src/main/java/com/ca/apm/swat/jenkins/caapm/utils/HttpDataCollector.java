package com.ca.apm.swat.jenkins.caapm.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.util.logging.Level;

/***
 * 
 * @author Srikant N @ CA Technology
 * Dt: Sep 2015
 *
 */


public class HttpDataCollector extends GenericDataCollector
{

    private static final Logger LOGGER = Logger.getLogger(HttpDataCollector.class.getName());

    public HttpDataCollector(String momURL, String momPort)
    {
        super(momURL, momPort);

    }




    @Override
    public MetricDataCollectionHelper fetchAllMetricData(String agentRegex, String metricRegex, 
                                                         long startTimeInMS, long endTimeInMS)
    {

        int calculatedFreqInSec = 0;
        LOGGER.log(Level.FINEST, "MetricDataCollection fetchAllMetricData startTimeinMS " + startTimeInMS + " endTimeinMS " +
                    endTimeInMS  + " ( endTimeInMS - startTimeInMS)/1000 " +  ( endTimeInMS - startTimeInMS)/1000);

        long relativeTimeInSec = ( endTimeInMS - startTimeInMS)/1000;

        //see if how many data points I can squeeze ( with 15sec, 60 sec or 3600 sec freq)
        long numbOfDataPoints = relativeTimeInSec/15;

        if ( numbOfDataPoints <= MAX_DATA_POINTS) {
            // looks like our freq is 15
            calculatedFreqInSec = 15;
            
            //not enough DP's
            if ( numbOfDataPoints < MIN_DATA_POINTS) {
                LOGGER.log(Level.FINEST, "MetricDataCollection fetchAllMetricData  numbOfDataPoints is less than minimum ");
                return null;
            }
            
        } else { //keep going and try with freq 60sec
            numbOfDataPoints = relativeTimeInSec/60;

            if ( numbOfDataPoints < MAX_DATA_POINTS) {

                calculatedFreqInSec = 60;
                
                // if its less than min for 60 sec then go back to 15 sec
                if (numbOfDataPoints < MIN_DATA_POINTS) {
                    calculatedFreqInSec = 15;
                    numbOfDataPoints = MAX_DATA_POINTS;
                }
                    
            } else {
                numbOfDataPoints = relativeTimeInSec/3600; 
                        
                numbOfDataPoints = ( numbOfDataPoints > MAX_DATA_POINTS ) ? MAX_DATA_POINTS : numbOfDataPoints;
                
                calculatedFreqInSec = 3600;
                
                //if its less than min go back to 60 mts
                if ( numbOfDataPoints < MIN_DATA_POINTS) {
                    calculatedFreqInSec = 60;
                    numbOfDataPoints = MAX_DATA_POINTS;
                }
               
                
            }
            
        }

        LOGGER.log(Level.FINEST, "MetricDataCollection calculatedFreqInSec " + calculatedFreqInSec + " numbOfDataPoints " + numbOfDataPoints + " relativeTimeInSec " + relativeTimeInSec);

        String format = "xml";
        String responseString = fetchMetricData(agentRegex, metricRegex, calculatedFreqInSec +"", "last" + (relativeTimeInSec/60) + "minutes", format);


        try
        {
            return parsedResponse(responseString, format );
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            LOGGER.log(Level.SEVERE, "MetricDataCollection fetchMetricData" + e );
            e.printStackTrace();

            return null;
        }

    }

    @Override
    public MetricDataCollectionHelper fetchLastNMetricData(String agentRegex, String metricRegex, long frequencyInSec, 
                                                           long startTimeInMS, long endTimeInMS, int numbDPToCollect)
    {
        // TODO Auto-generated method stub


        //long frequencyInSec = 15;
        //String relativeTime = null;

        numbDPToCollect =  numbDPToCollect > MAX_DATA_POINTS  ? MAX_DATA_POINTS : numbDPToCollect;
        numbDPToCollect =  numbDPToCollect < MIN_DATA_POINTS  ? MIN_DATA_POINTS : numbDPToCollect;

        LOGGER.log(Level.FINEST, "MetricDataCollection fetchMetricData startTimeinMS " + startTimeInMS + " endTimeinMS " + endTimeInMS  + 
                   " ( endTimeInMS - startTimeInMS)/1000 " +  ( endTimeInMS - startTimeInMS)/1000);

       // long relativeTimeInMts = 0;

        // numbDP set by user is > 0 meaning user wants last N data points in 15 sec freq

        long relativeTimeInSec = frequencyInSec*numbDPToCollect;

        String relativeTime = "last" + relativeTimeInSec/60+"minutes";


        LOGGER.log(Level.FINEST, "MetricDataCollection relative " + relativeTimeInSec + " Freq in Sec " + frequencyInSec );



        //less than 10 hours
        String format = "xml";
        String responseString = fetchMetricData(agentRegex, metricRegex, frequencyInSec+"", relativeTime, format);


        try
        {
            return parsedResponse(responseString, format );
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            LOGGER.log(Level.SEVERE, "MetricDataCollection fetchMetricData" + e );
            e.printStackTrace();

            return null;
        }

    }

    
    private MetricDataCollectionHelper parsedResponse( String responseString, String format ) throws  Exception {

        MetricDataCollectionHelper metricDataCollectionHelper = null;

        LOGGER.log(Level.FINE, "ParsedResponse " + responseString );

        if (format.equals("xml")) {

            JSONObject json = null;


            try {
                json = XML.toJSONObject(responseString.toString());;
                
                JSONArray jsonDataPoints = json.getJSONObject("introscope-datapoints").getJSONArray("datapoint");
                int length = jsonDataPoints.length();

                if ( length > 0 ) {
                    metricDataCollectionHelper = new MetricDataCollectionHelper();
                }

                for ( int i = 0; i < length; i++ ) {

                    String metricKey = jsonDataPoints.getJSONObject(i).getString("agent-name") + "|" + 
                            jsonDataPoints.getJSONObject(i).getString("metric-name");


                    long min = jsonDataPoints.getJSONObject(i).getLong("minimum");
                    long max = jsonDataPoints.getJSONObject(i).getLong("maximum");
                    long value = jsonDataPoints.getJSONObject(i).getLong("value");
                    long count = jsonDataPoints.getJSONObject(i).getLong("data-point-count");
                    String time = jsonDataPoints.getJSONObject(i).getString("end-timestamp");
                    int frequency = jsonDataPoints.getJSONObject(i).getInt("period-in-seconds");

                    //DataPoint dataPoint =  metricDataCollection.new DataPoint(min, max, value, count, time, frequency );
                    if (!metricDataCollectionHelper.addMetricData(metricKey, min, max, value, count, time, frequency) ) {
                        LOGGER.log(Level.FINE, "*** parsedResponse: Metric Collection cannot add more unique metrics as Max metric limit of " 
                                        + MetricDataCollectionHelper.MAX_NUMB_OF_METRICS + " reached ");
                    }

                    LOGGER.log(Level.FINE, "*** parsedResponse: Metric Collection is " + min +" : " +  max+" : " + value +" : " + count+" : " + time+" : " + frequency );
                }

            } catch (Exception ex ) {
                //no data returned in the response
                LOGGER.log(Level.SEVERE, "MetricDataCollection parsedResponse did not return introscope-datapoints. "
                        + "Possibly due to no metric match. Check agent and metric expression" + ex );
                ex.printStackTrace();
                return null;
            }




        }

        return metricDataCollectionHelper;
    }


    private String fetchMetricData(String agentRegex, String metricRegex, String frequency, String relativeTime, String format ) {

        //String response = null;

        CloseableHttpResponse response = null;
        StringBuffer stringResponse = new StringBuffer();

        try {
            //String relativeTime = endTime + startTime;
            String connectionURL = "http://" + momURL + ":" + momPort +"/data/query";

            String queryParam = "?agentRegex=" + URLEncoder.encode(agentRegex,"UTF-8") + "&metricRegex=" + URLEncoder.encode(metricRegex,"UTF-8") + "&relativeTime=" + relativeTime +
                    "&period=" + frequency + "&format=" + format;

            HttpClientBuilder client =  HttpClientBuilder.create();
            HttpGet request = new HttpGet(connectionURL + queryParam);

            RequestConfig.Builder requestBuilder = RequestConfig.custom();
            requestBuilder = requestBuilder.setConnectTimeout(timeout);
            requestBuilder = requestBuilder.setConnectionRequestTimeout(timeout);
            
            client.setDefaultRequestConfig(requestBuilder.build());
            
            LOGGER.log(Level.FINE, "Connection String is " + connectionURL + queryParam);

            
            response = client.build().execute(request);;
            BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));

            String line = "";
            while ((line = rd.readLine()) != null) {
                stringResponse.append(line);
            }


            return stringResponse.toString();

        } catch ( Exception ex ) {
            ex.printStackTrace();
        } finally {
            try {
                if ( response != null )
                    response.close();
            } catch ( Exception ex ) {
                ex.printStackTrace();
            }
        }

        return null;

    }

    public HttpClientBuilder IgnoreSSLClient( HttpClientBuilder client) throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
            public java.security.cert.X509Certificate[] getAcceptedIssuers(){return null;}

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                                           String authType)
                                                   throws java.security.cert.CertificateException {}
            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                                           String authType)
                                                   throws java.security.cert.CertificateException{}
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            //@SuppressWarnings("deprecation")
            javax.net.ssl.HostnameVerifier hnv = new javax.net.ssl.HostnameVerifier() {
                
                @Override
                public boolean verify(String arg0, SSLSession session)
                {
                    return true;
                }
            };
            SSLConnectionSocketFactory sslcsf = new SSLConnectionSocketFactory( sc, hnv) ;
            //HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            client.setSSLSocketFactory(sslcsf);
            
            return client;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
