package com.ca.apm.swat.jenkins.caapm;


import java.awt.Color;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.kohsuke.stapler.StaplerProxy;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import com.ca.apm.swat.jenkins.caapm.utils.CAAPMPerformanceReport;
import com.ca.apm.swat.jenkins.caapm.utils.MetricDataCollectionHelper.DataPoint;
import com.ca.apm.swat.jenkins.caapm.utils.MetricDataCollectionHelper.MetricData;
import com.ca.apm.swat.jenkins.caapm.utils.MetricDataCollectionHelper;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.ModelObject;
import hudson.util.DataSetBuilder;
import hudson.util.Graph;
import java.text.SimpleDateFormat;

/***
 * 
 * @author Srikant N @ CA Technology
 * Dt: Sep 2015
 *
 */


public class CAAPMBuildAction implements Action {

    private final AbstractBuild<?, ?> build;
    private final CAAPMPerformanceReport report;
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CAAPMBuildAction.class.getName());
   
    private String webviewHost;
    private String webviewPort;

    public CAAPMBuildAction (AbstractBuild<?, ?> build, CAAPMPerformanceReport report, String webviewHost, String webviewPort ) {
        LOGGER.log(Level.ALL, "**** CAAPMBuildAction constructor called");
        this.build = build;
        this.report = report;
        this.webviewHost = webviewHost;
        this.webviewPort = webviewPort;
        
        
        
    }


    @Override
    public String getDisplayName()
    {
        // TODO Auto-generated method stub
        return "Build " + build.number + " Dashboards ";
    }
/*
    @Override
    public Object getTarget()
    {
        // TODO Auto-generated method stub
        return 
    }
 */
    @Override
    public String getIconFileName()
    {
        // TODO Auto-generated method stub
        return "graph.gif";
    }

    @Override
    public String getUrlName()
    {
        // TODO Auto-generated method stub
        return "caapm-dashboard";
    }

    public AbstractBuild<?, ?> getBuild() {
         LOGGER.log(Level.FINEST, "**** CAAPMBuildAction get Build Called");

        return build;
    }

    public CAAPMPerformanceReport getReport() {

         LOGGER.log(Level.FINEST, "**** CAAPMBuildAction get Report Called");

        return report;
    }

    public String constructWebviewURL( MetricData metricData) {

        if ( metricData == null ) {
             LOGGER.log(Level.FINEST, "**** CAAPM BuildAction ::constructViewURL called with empty metric data object ");
            return "";
        }


        String buildStartTime = null;
        String buildEndTime = null;

        SimpleDateFormat format = new SimpleDateFormat("d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);


        int i = 0;
        DataPoint tempDP=null;
        for ( DataPoint dataPoint : metricData.getDataPoints() ) {
            if ( i++ == 0 ) {
                Date date = null;
                String temp = dataPoint.getTime();
                try {
                    date = format.parse(temp);
                } catch ( Exception ex ) {
                     LOGGER.log(Level.FINEST, "**** BuildAction::constructViewURL could not format start Time " );
                    ex.printStackTrace();
                    break;
                }
                buildStartTime = date.getTime() +"";

            }
            tempDP = dataPoint;
        }

        String temp = tempDP.getTime();
        try {
            Date date = format.parse(temp);
            buildEndTime = date.getTime() +"";
        } catch ( Exception ex ) {
             LOGGER.log(Level.FINEST, "**** BuildAction::constructViewURL could not format end Time " );
            ex.printStackTrace();

        }


        String metricKey = metricData.getMetricName().replace("|", "%257C").replace(":", "%253A");

         LOGGER.log(Level.FINEST, "**** BuildAction::constructViewURL start time " + buildStartTime + " end time " + buildEndTime );

        String webviewURL = null;

        if ( buildStartTime == null || buildEndTime == null ) {

            webviewURL = "http://" + webviewHost + ":" + webviewPort + "/#investigator;smm=false;tab-n=mb;tab-tv=pd;tr=0;uid="+ metricKey;

        } else {
            webviewURL = "http://" + webviewHost + ":" + webviewPort + "/#investigator;et=" + buildEndTime + ";re=" + metricData.getFrequency() + "000;smm=false;st="+ buildStartTime + 
                    ";tab-in=mb;tab-tv=pd;tr=-1;uid="+ metricKey;
        }

        //firstBuildTime = lastBuildTime = null;

         LOGGER.log(Level.FINEST, "**** BuildAction::constructViewURL wv url is " + webviewURL);

        return webviewURL;

    }


    public void doRenderMetricGraph(final StaplerRequest request,
                                    final StaplerResponse response) throws IOException  {
        String metricKey = request.getParameter("metricKey");
        final Graph graph = new GraphImplementation( metricKey, build.number);

         LOGGER.log(Level.FINEST, "**** CAAPM BuildAction rendering metric graph " + metricKey);

        graph.doPng(request, response);
    }

    private class GraphImplementation extends Graph {
        private final String metricKey;
        private final int buildNumb;

        protected GraphImplementation(final String metricKey, final int buildNumb ) {
            super(-1, 550, 600); 
            this.metricKey = metricKey;
            this.buildNumb = buildNumb;
        }


        protected DataSetBuilder<String, Integer> createDataSet() {
            DataSetBuilder<String, Integer> dataSetBuilder =
                    new DataSetBuilder<String, Integer>();


            MetricData metricData = report.getMetricDataCollectionHelper().getMetricData(metricKey);

            List<DataPoint> dpCollection = metricData.getDataPoints();

            int dpCollectionSize = dpCollection.size();
            for ( int i = 0 ; i < dpCollectionSize; i++ ){

                long value = dpCollection.get(i).getValue();
                 LOGGER.log(Level.FINEST, "**** metricKey " + metricKey + " value is " + value );


                dataSetBuilder.add(value, report.getReportName(), i+1);
            }

            return dataSetBuilder;


        }

        protected JFreeChart createGraph() {
            final CategoryDataset dataset = createDataSet().build();

            String[] stringArray = null;

            if (metricKey !=null) {
                stringArray = metricKey.split(":");
            }

            String metricName = "";
            if ( stringArray != null  && stringArray.length == 2 ) {
                metricName = stringArray[1];
            }

            final JFreeChart barChart = ChartFactory.createBarChart(metricName, 
                                                                  "Time Interval",
                                                                  null, 
                                                                  dataset, 
                                                                  PlotOrientation.VERTICAL,
                                                                  false, 
                                                                  true, 
                                                                  false 
                    );

            barChart.setBackgroundPaint(Color.yellow);
            barChart.getCategoryPlot().getRenderer().setSeriesPaint(0,  Color.DARK_GRAY);

            return barChart;
        }
    }
}
