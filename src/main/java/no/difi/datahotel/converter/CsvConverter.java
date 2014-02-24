package no.difi.datahotel.converter;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import org.apache.commons.cli.*;

import java.io.*;
import java.nio.charset.Charset;

public class CsvConverter {

    static Options options;

    static {
        options = new Options();
        options.addOption(new Option("help", "print this message"));
        options.addOption(OptionBuilder.withArgName("delimiter").hasArg().withDescription("delimiter used in input" ).create('d'));
        options.addOption(OptionBuilder.withArgName("charset").hasArg().withDescription("charset used in input" ).create('c'));
        options.addOption(OptionBuilder.withArgName("file").hasArg().withDescription("input file").create("i"));
        options.addOption(OptionBuilder.withArgName("file").hasArg().withDescription("output file").create("o"));
    }

    public static void main(String[] args) throws IOException, ParseException {
        CommandLine cmd = new BasicParser().parse(options, args);
        if (cmd.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("datahotel-converter", CsvConverter.options);
        } else {
            InputStream inputStream = cmd.hasOption('i') ? new FileInputStream(cmd.getOptionValue('i')) : System.in;
            OutputStream outputStream = cmd.hasOption('o') ? new FileOutputStream(cmd.getOptionValue('o')) : System.out;

            run(cmd, inputStream, outputStream);

            outputStream.close();
            inputStream.close();
        }
    }

    public static void run(CommandLine cmd, InputStream inputStream, OutputStream outputStream) throws IOException {
        char delimiter = cmd.hasOption('d') ? cmd.getOptionValue('d').charAt(0) : ';';
        Charset charset = Charset.forName(cmd.hasOption('c') ? cmd.getOptionValue('c') : "UTF-8");
        CsvReader csvReader = new CsvReader(inputStream, delimiter, charset);

        CsvWriter csvWriter = new CsvWriter(outputStream, ';', Charset.forName("UTF-8"));
        csvWriter.setForceQualifier(true);
        csvWriter.setTextQualifier('"');
        csvWriter.setEscapeMode(CsvWriter.ESCAPE_MODE_BACKSLASH);

        String[] line;
        while (csvReader.readRecord()) {
            line = csvReader.getValues();
            csvWriter.writeRecord(line);
            csvWriter.flush();
        }

        csvWriter.close();
        csvReader.close();
    }

}
