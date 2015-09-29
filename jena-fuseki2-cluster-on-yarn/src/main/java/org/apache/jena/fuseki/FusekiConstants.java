package org.apache.jena.fuseki;

/**
 * How to run a single FUSEKI Server?
 * 
 * https://jena.apache.org/documentation/fuseki2/fuseki-run.html
 * 
 * fuseki-server [--mem | --loc=DIR] [[--update] /NAME]
 * 
 * @author kamir
 */
public interface FusekiConstants {
	
	public static final String FUSEKI_DIST_PATH = "hdfs://127.0.0.1:8020/apps/fuseki/dist/fuseki-2.3.gz";
	public static final String FUSEKI_SYMLINK = "fuseki";
	public static final String FUSEKI_VERSION = "2.3";
        
	public static final String FUSEKI_CONTAINER_LOG_DIR = "/var/log/hadoop/yarn";
	public static final String FUSEKI_CLUSTER_ON_YARN_APP = "FusekiApp.jar";
	public static final String COMMAND_CHAIN = " && ";

}
