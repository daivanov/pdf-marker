package org.indt.divanov;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class Cli {
	public static final String OPTION_HELP = "help";
	public static final String OPTION_PDF_IN = "in";
	public static final String OPTION_PDF_OUT = "out";

	private Options options = initOptions();

	public Cli() {
	}

	public Options initOptions() {
		Options options = new Options();
		options.addOption("h", OPTION_HELP, false, "print this info");
		options.addOption(OPTION_PDF_IN, true, "Name of pdf file to process");
		options.addOption(OPTION_PDF_OUT, true, "Name of pdf file to produce");
		return options;
	}

	public void parse(String[] args) {
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

		if (cmd.hasOption(OPTION_PDF_IN) && cmd.hasOption(OPTION_PDF_OUT)) {
			try {
				PdfProcessor.mark(
						cmd.getOptionValue(OPTION_PDF_IN),
						cmd.getOptionValue(OPTION_PDF_OUT));
			} catch(Exception e) {
				System.err.println(e.getMessage());
				System.exit(2);
			}
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
