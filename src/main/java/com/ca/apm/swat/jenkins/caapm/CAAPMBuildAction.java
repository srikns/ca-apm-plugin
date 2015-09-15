package com.ca.apm.swat.jenkins.caapm;


import java.awt.Color;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
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
import hudson.model.ModelObject;
import hudson.util.DataSetBuilder;
import hudson.util.Graph;

/***
 * 
 * @author Srikant N @ CA Technology
 * Dt: Sep 2015
 *
 */


public class CAAPMBuildAction implements Action {

    private final AbstractBuild<?, ?> build;
    private final CAAPMPerformanceReport report;
    private static final Logger LOGGER = Logger.getLogger(CAAPMBuildAction.class.getName());
    private static final long serialVersionUID = 1L;

    public CAAPMBuildAction (AbstractBuild<?, ?> build, CAAPMPerformanceReport report) {
        LOGGER.log(Level.ALL, "**** CAAPMBuildAction constructor called");
        this.build = build;
        this.report = report;
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
        LOGGER.log(Level.INFO, "**** CAAPMBuildAction get Build Called");
        
        return build;
    }
    
    public CAAPMPerformanceReport getReport() {
        
        LOGGER.log(Level.INFO, "**** CAAPMBuildAction get Report Called");
        
        return report;
    }

    public void doRenderMetricGraph(final StaplerRequest request,
                                    final StaplerResponse response) throws IOException  {
        String metricKey = request.getParameter("metricKey");
        final Graph graph = new GraphImpl( metricKey, build.number);

        LOGGER.log(Level.INFO, "**** CAAPM BuildAction rendering metric graph " + metricKey);

        graph.doPng(request, response);
    }
    
    private class GraphImpl extends Graph {
        private final String metricKey;
        private final int buildNumb;

        protected GraphImpl(final String metricKey, final int buildNumb ) {
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
                LOGGER.log(Level.INFO, "**** metricKey " + metricKey + " value is " + value );
                
                
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

            final JFreeChart chart = ChartFactory.createLineChart(metricName, 
                                                                  "Time Interval",
                                                                  null, 
                                                                  dataset, 
                                                                  PlotOrientation.VERTICAL,
                                                                  false, 
                                                                  true, 
                                                                  false 
                    );

            chart.setBackgroundPaint(Color.yellow);

            return chart;
        }
    }
}
