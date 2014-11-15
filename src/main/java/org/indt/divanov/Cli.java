package org.indt.divanov;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class Cli {
	public static final String OPTION_HELP = "help";
	public static final String OPTION_PDF = "pdf";

	private String[] args = null;
	private Options options = initOptions();

	public Cli(String[] args) {
		this.args = args;
	}

	public Options initOptions() {
		Options options = new Options();
		options.addOption("h", OPTION_HELP, false, "print this info");
		options.addOption("p", OPTION_PDF, true, "Name of pdf file to process");
		return options;
	}

	public void parse() {
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			help();
			System.exit(1);
		}

		if (cmd.hasOption(OPTION_HELP)) {
			help();
			System.exit(0);
		}

		if (cmd.hasOption(OPTION_PDF)) {
			System.out.println("Processing " + cmd.getOptionValue("v"));
		} else {
			help();
			System.exit(1);
		}
	}

	private void help() {
		HelpFormatter formater = new HelpFormatter();
		formater.printHelp("pdf-marker.jar", options);
	}
}
