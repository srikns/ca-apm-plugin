CA-APM Jenkins Plugin
====================

ca-apm Jenkins plugin project


ca-apm jenkins plugin
---------------------
Developed by Srikant Noorani @ CA Inc Dec 2015
This is NOT an officially supported CA product but is provided on an as is basis. 
Pls collaborated and improve

See https://cawiki.ca.com/display/SASWAT/CA+-+APM+Jenkins+Plugin (CA internal) for an overview of the Tixchange
project.

Plugin Setup
---------------
Follows the standard Jenkins plugin process.
See https://cawiki.ca.com/display/SASWAT/CA+-+APM+Jenkins+Plugin (CA internal) for information on
setting up and running thise on a local box.


Description
-----------------
This plugin allows users to collect CA-APM performance data from EM from within Jenkins


Here is the list of features ( pls configuration section below for details)
Ability to configure multiple fail conditions
	Metric A less than or greater than a constant value OR
	Metric A in comparison to Metric B
Ability to send notification via Email (if smtp server is configured)
Ability to view KPI's per build or cross build dashboard in Jenkins
Ability to decorate ATC 10.1 node using custom attribute API
Ability to just notify via email: If "justEmail" is checked the build will NOT be marked as fail even if the conditions are met but only a notification will be sent
ability to fetch multiple metrics using regex.
Ability Jump to Webview in context (for both metric and time range) from build or x-build dashboard


To Run
-------
Clone the project and run mvn compile. Read up the wiki for more info
Restart tomcat.
