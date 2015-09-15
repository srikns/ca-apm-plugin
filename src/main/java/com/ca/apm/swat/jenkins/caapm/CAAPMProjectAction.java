package com.ca.apm.swat.jenkins.caapm;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tools.ant.taskdefs.BuildNumber;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import com.ca.apm.swat.jenkins.caapm.utils.CAAPMPerformanceReport;
import com.ca.apm.swat.jenkins.caapm.utils.MetricDataCollectionHelper.MetricData;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.util.ChartUtil;
import hudson.util.DataSetBuilder;
import hudson.util.Graph;

/***
 * 
 * @author Srikant N @ CA Technology
 * Dt: Sep 2015
 *
 */


public class CAAPMProjectAction implements Action
{

    private final AbstractProject<?, ?> project;
    private final String threshold;
    private final String[] metrics;
    private String testString = "testSrikant";

    private static final Logger LOGGER = Logger.getLogger(CAAPMProjectAction.class.getName());
    private static final long serialVersionUID = 1L;

    private static final String DASHBOARD = "caapm-dashboard";

    public CAAPMProjectAction (final AbstractProject project, final String threshold,
                               final String[] metrics ) {

        this.project = project;
        this.threshold = threshold;
        this.metrics = metrics;
    }

    @Override
    public String getDisplayName()
    {
        // TODO Auto-generated method stub
        return "Cross-Build Performance Dashboards";
    }

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
        return DASHBOARD;
    }

    public boolean isTrendAvailable() {
        LOGGER.log(Level.FINE, "**** CAAPMProjectAction isTrendAvailable");
        return true;
    }

    public String getTestString() {
        LOGGER.log(Level.FINE, "**** CAAPMProjectAction testString");
        return testString;
    }

    public Collection<String> getMetricKeysFromLastGoodBuild() {

        final List<? extends AbstractBuild<?, ?>> builds = project.getBuilds();

        for (AbstractBuild<?, ?> build : builds) {
            LOGGER.log(Level.FINE, "**** CAAPMProjectAction getMetricKeysFromLastGoodBuild Called 1 " + build.number);

            CAAPMBuildAction buildAction = build.getAction(CAAPMBuildAction.class);
            LOGGER.log(Level.FINE, "**** CAAPMProjectAction getMetricKeysFromLastGoodBuildCalled 2 " + buildAction);

            if ( buildAction == null )
                continue;
            LOGGER.log(Level.FINE, "**** CAAPMProjectAction getMetricKeysFromLastGoodBuildCalled 3 -  " + buildAction.getDisplayName());

            CAAPMPerformanceReport report = buildAction.getReport();

            if ( report != null ) {
                Collection<String> keySet = report.getMetricKeySet();

                if(keySet == null) {
                    LOGGER.log(Level.FINE, "**** CAAPMProjectActiongetMetricKeysFromLastGoodBuild Called 4 -  key set is null" );
                    continue;
                }
                return keySet;
            }

        }

        return null;

    }

    public Collection<MetricData> getMetricDataFromLastGoodBuild() {

        LOGGER.log(Level.INFO, "in getMetrciDataFromLastGoodBuild ");

        final List<? extends AbstractBuild<?, ?>> builds = project.getBuilds();

        for (AbstractBuild<?, ?> build : builds) {
            LOGGER.log(Level.FINE, "**** CAAPMProjectAction getMetricDataFromLastGoodBuild Called 1 " + build.number);

            CAAPMBuildAction buildAction = build.getAction(CAAPMBuildAction.class);
            LOGGER.log(Level.FINE, "**** CAAPMProjectAction getMetricDataFromLastGoodBuild Called 2 " + buildAction);

            if ( buildAction == null )
                continue;
            LOGGER.log(Level.FINE, "**** CAAPMProjectAction getMetricDataFromLastGoodBuild Called 3 -  " + buildAction.getDisplayName());

            CAAPMPerformanceReport report = buildAction.getReport();

            if ( report != null ) {
                Collection<MetricData> metricDataCollection = report.getMetricDataCollection();

                if(metricDataCollection == null) {
                    LOGGER.log(Level.FINE, "**** CAAPMProjectAction getMetricDataFromLastGoodBuild Called 4 -  key set is null" );
                    continue;
                }
                return metricDataCollection;
            }

        }

        return null;

    }

    public CAAPMPerformanceReport getLastBuildReport() {

        final List<? extends AbstractBuild<?, ?>> builds = project.getBuilds();

        for (AbstractBuild<?, ?> build : builds) {
            LOGGER.log(Level.FINE, "**** CAAPMProjectAction get Last Build Called 1 " + build.number);

            CAAPMBuildAction buildAction = build.getAction(CAAPMBuildAction.class);
            LOGGER.log(Level.FINE, "**** CAAPMProjectAction get Last Build Called 2 " + buildAction);

            if ( buildAction == null )
                continue;
            LOGGER.log(Level.FINE, "**** CAAPMProjectAction get Last Build Called 3 -  " + buildAction.getDisplayName());

        }
        //LOGGER.log(Level.FINE, "**** CAAPMProjectAction get Last Build Called 1 -  " + project.getLastSuccessfulBuild().number);
        // LOGGER.log(Level.FINE, "**** CAAPMProjectAction get Last Build Called 2 -  " + project.getLastCompletedBuild().getAction(CAAPMBuildAction.class));
        // LOGGER.log(Level.FINE, "**** CAAPMProjectAction get Last Build Called 3 -  " + project.getLastSuccessfulBuild().getAction(CAAPMBuildAction.class).getDisplayName());

        return project.getLastSuccessfulBuild().getAction(CAAPMBuildAction.class).getReport();
    }

    public AbstractProject<?, ?> getProject() {
        return this.project;
    }

    public void doRenderFailureMsgTable  (final StaplerRequest request,
                                      final StaplerResponse response) throws IOException {
        
    }
    
    
    
    
    public HashMap<Integer,String> getBuildNumberToMessageFailureMap() {
        
        LinkedHashMap<Integer,String> buildNumberToMessageFailureMap = new LinkedHashMap<Integer, String>();
        
        LOGGER.log(Level.FINE, "**** getBuildNumberToMessageFailureMap  Called " );
        
        try {

            final List<? extends AbstractBuild<?, ?>> builds = project.getBuilds();

            int i = 0;

            for (AbstractBuild<?, ?> build : builds) {

                if (build == null ) {
                    continue;
                }

                int buildNumber = build.number;
                CAAPMBuildAction buildAction = build.getAction(CAAPMBuildAction.class);

                if ( buildAction == null ) {
                    continue;
                }

                CAAPMPerformanceReport report = buildAction.getReport();

                if ( report == null )
                    continue;


                LOGGER.log(Level.FINE, "**** getBuildNumberToMessageFailureMap Project Build number " + buildNumber + " Message " + report.getFailPassReason());

                if ( report.getFailPassReason() !=null )
                     buildNumberToMessageFailureMap.put(buildNumber, report.getFailPassReason());
                

                //Last 10 builds ONLY
                if (i++ > 11 ) {
                    break;
                }

                
            }
            
            return buildNumberToMessageFailureMap;
            
        } catch ( Exception ex ) {
            ex.printStackTrace();
            
            return null;
        }
    }
    
    
    public Collection<Integer> getBuildNumber() {
        HashMap<Integer,String> buildNumberToMessageFailureMap = getBuildNumberToMessageFailureMap();
        
        if ( buildNumberToMessageFailureMap != null ) {
            return buildNumberToMessageFailureMap.keySet();
        }
            
        return null;
    }
    
     
    public void doRenderMetricGraph  (final StaplerRequest request,
                                      final StaplerResponse response) throws IOException {

        String metricKey = request.getParameter("metricKey");
        HashMap<Integer, Long> buildNumberToARTMap = new HashMap<Integer, Long>();

        LOGGER.log(Level.FINE, "**** Project Build render Metric Graph " + metricKey );

        try {

            final List<? extends AbstractBuild<?, ?>> builds = project.getBuilds();

            int i = 0;

            for (AbstractBuild<?, ?> build : builds) {

                if (build == null ) {
                    continue;
                }

                int buildNumber = build.number;
                CAAPMBuildAction buildAction = build.getAction(CAAPMBuildAction.class);

                if ( buildAction == null ) {
                    continue;
                }

                CAAPMPerformanceReport report = buildAction.getReport();

                if ( report == null )
                    continue;


                LOGGER.log(Level.FINE, "**** Project Build number " + buildNumber + " metric name "  + metricKey );
                LOGGER.log(Level.FINE," Build ART " + report.getARTForBuild(metricKey));
                
                buildNumberToARTMap.put(buildNumber, report.getARTForBuild(metricKey));

                //Last 10 builds ONLY
                if (i++ > 11 ) {
                    break;
                }

            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

        final Graph graph = new GraphImpl(metricKey, buildNumberToARTMap );

        LOGGER.log(Level.FINE, "**** Project rendering metric graph ");
        //System.out.println(" Project rendering metric graph system out");

        graph.doPng(request, response);

    }

    
    

    
    private class GraphImpl extends Graph {
        private final String graphTitle;
        private HashMap<Integer, Long> buildNumberToARTMap;

        protected GraphImpl(final String metricKey, HashMap<Integer, Long> buildNumberToARTMap) {
            super(-1, 500, 400); 
            this.graphTitle = metricKey;
            this.buildNumberToARTMap = buildNumberToARTMap;
        }


        protected DataSetBuilder<String, Integer> createDataSet() {
            DataSetBuilder<String, Integer> dataSetBuilder =
                    new DataSetBuilder<String, Integer>();

            Set<Integer> buildNumbers = buildNumberToARTMap.keySet();
            int size = buildNumberToARTMap.size();


            for ( Iterator<Integer> iter = buildNumbers.iterator(); iter.hasNext(); ) {
                Integer buildNumber = iter.next();

                dataSetBuilder.add(buildNumberToARTMap.get(buildNumber), "Test Graph Srikant", buildNumber);
                // dataSetBuilder.add(10, "Test Graph Srikant", buildNumber);

                LOGGER.log(Level.FINE, "****#### Render Graph CA " + buildNumberToARTMap.get(buildNumber) );
            }

            return dataSetBuilder;
        }

        protected JFreeChart createGraph() {
            final CategoryDataset dataset = createDataSet().build();

            String[] stringArray = null;

            if (graphTitle !=null) {
                stringArray = graphTitle.split(":");
            }

            String metricName = "";
            if ( stringArray != null  && stringArray.length == 2 ) {
                metricName = stringArray[1];
            }

            final JFreeChart chart = ChartFactory.createLineChart(" ", 
                                                                  "Build Number", 
                                                                  null, 
                                                                  dataset,
                                                                  PlotOrientation.VERTICAL, 
                                                                  false, 
                                                                  true, 
                                                                  false 
                    );

            chart.setBackgroundPaint(Color.white);

            return chart;
        }
    }
}
