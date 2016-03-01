package com.ca.apm.swat.jenkins.caapm.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ca.apm.swat.jenkins.caapm.CAAPMBuildAction;

/***
 * 
 * @author Srikant N @ CA Technology
 * Dt: Sep 2015
 *
 */


public class CAAPMCLWCommand
{
    MomConnection momConnection;
    
    private static final Logger LOGGER = Logger.getLogger(CAAPMCLWCommand.class.getName());
    
    String agentPath;
    String metricPath;
    int frequency;
    int timeRangeInMts;
    
    String queryString = "java -Xmx128M -Duser=Admin -Dpassword=\"\" -Dhost=192.168.81.180 " +
            "-Dport=5001 -jar CLWorkstation.jar get historical data from agents matching \".*Tomcat.*\" " +
            " and metrics matching \".*GC.*Bytes In Use.*\" for past 10 minutes with frequency of 60 seconds";
    

    
    CAAPMCLWCommand( MomConnection momConnection, String agentPath, String metricPath, int frequency, int timeRangeInMts ) {
        this.momConnection = momConnection;
        this.agentPath = agentPath;
        this.frequency = frequency;
        this.timeRangeInMts = timeRangeInMts;
    }
    
    StringBuffer executeCLWQuery() {
        
        Process process = null;
        
        try {
            
           process = Runtime.getRuntime().exec("/Users/noosr03/Documents/CA_Technical/Jenkins/Example/CAAPM_CLW/runCLW");
            int read = -1;
            
            LOGGER.log(Level.FINEST, " in CLWCommand executeCLWQuery" );
            
            byte[] b = new byte[100];
            
            StringBuffer outPut = new StringBuffer();
            
            while ( (read = process.getInputStream().read(b, 0 , 100)) != -1 ) {

                //LOGGER.log(Level.FINEST, " Query Output from " + process.getInputStream().read());
                outPut.append(new String(b));
            }
            
            //String outPut = new String(b);
            
            LOGGER.log(Level.FINEST, " Query Output from " + outPut);
            
            while ( (read = process.getErrorStream().read(b)) != -1 ) {

                LOGGER.log(Level.FINEST, " Query Error from " + new String(b));

            }  
            
            return outPut;
            
        } catch (Exception ex ) {
            ex.printStackTrace();
        }
        
        try {
            process.waitFor();

        } catch ( InterruptedException intEx ) {

            LOGGER.log(Level.FINEST, "WARN:: QueryProcessor: run: method was interrupted " );
            
            intEx.printStackTrace();                                

        }
        
        return null;
    }
}
