package com.ca.apm.swat.jenkins.caapm.utils;

/***
 * 
 * @author Srikant N @ CA Technology
 * Dt: Sep 2015
 *
 */

public class MomConnection
{
    private String momHost;
    private String port;
    private String username;
    private String password;

    public MomConnection ( final String momHost, String port, final String username, final String password ) {
        
        this.momHost = momHost;
        this.port = port;
        this.username = username;
        this.password = password;
    }


    public boolean validateConnection() {
        
        return true;
    }

}
