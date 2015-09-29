package org.apache.jena.fuseki;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class FusekiConfiguration implements FusekiConstants {

	private static String fusekiHome;
	private static String fusekiHostName;
        
        // [--mem | --loc=DIR] 
	private static String fusekiMode = "--mem";
        
        // [[--update] /NAME]
	private static String fusekiDSName = "/etosha";

        
	private static final Logger LOG = Logger.getLogger(FusekiConfiguration.class
			.getName());

	private Options opts;

	/**
	 * Constructor that creates the configuration
	 */
	public FusekiConfiguration() {

		opts = new Options();
		opts.addOption("home", true, "FUSEKI home directory");
		opts.addOption("host", true, "Hostname of FUSEKI instance");
	}

	public static void main(String[] args) {

		FusekiConfiguration conf = new FusekiConfiguration();

		try {
			conf.init(args);

		} 
                catch (Exception e) {
			LOG.log(Level.SEVERE, "Problem configuring FUSEKI Cluster.", e);
		}
                
	}

	private void init(String[] args) throws ParseException {

		CommandLine cliParser = new GnuParser().parse(opts, args);

		fusekiHome = cliParser.getOptionValue("home");
		fusekiHostName = cliParser.getOptionValue("host");
	}
}
