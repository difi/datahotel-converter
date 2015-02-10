package no.difi.datahotel.converter;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import no.difi.datahotel.model.Field;
import no.difi.datahotel.model.Fields;
import no.difi.datahotel.model.Metadata;
import org.apache.commons.cli.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class CsvConverter {

    private static final String GROUPABLE = "groupable";
    private static final String SEARCHABLE = "searchable";

    static Options options;

    static {
        options = new Options();
        options.addOption(new Option("help", "print this message"));

        // CSV
        options.addOption(OptionBuilder.withArgName("delimiter").hasArg().withDescription("delimiter used in input" ).create('d'));
        options.addOption(OptionBuilder.withArgName("charset").hasArg().withDescription("charset used in input" ).create('c'));
        options.addOption(OptionBuilder.withArgName("groupable").hasArg().withDescription("fields").create(GROUPABLE));
        options.addOption(OptionBuilder.withArgName("searchable").hasArg().withDescription("fields").create(SEARCHABLE));

        // IO
        options.addOption(OptionBuilder.withArgName("file").hasArg().withDescription("input file").create("i"));
        options.addOption(OptionBuilder.withArgName("file").hasArg().withDescription("output file").create("o"));
        options.addOption(OptionBuilder.withArgName("outputFolder").hasArg().withDescription("output folder").create("f"));
        options.addOption(OptionBuilder.withDescription("Write fields").create("fields"));
        options.addOption(OptionBuilder.withDescription("Write meta").create("meta"));

        // Dataset
        options.addOption(OptionBuilder.withArgName("name").hasArg().withDescription("dataset name").create("name"));
        options.addOption(OptionBuilder.withArgName("url").hasArg().withDescription("dataset url").create("url"));
        options.addOption(OptionBuilder.withArgName("description").hasArg().withDescription("dataset description").create("description"));
        options.addOption(OptionBuilder.withDescription("Dataset disabled").create("disabled"));

        // Fields
        options.addOption(OptionBuilder.withArgName("field=shortname").hasArgs().withDescription("set field shortname").create("fshortname"));
        options.addOption(OptionBuilder.withArgName("field=name").hasArgs().withDescription("set field name").create("fname"));
        options.addOption(OptionBuilder.withArgName("field=description").hasArgs().withDescription("set field description").create("fdescription"));
    }

    public static void main(String[] args) throws IOException, ParseException {
        CommandLine cmd = new BasicParser().parse(options, args);
        if (cmd.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("datahotel-converter", CsvConverter.options);
        } else {
            if (cmd.hasOption("name"))
                System.out.println("=== " + cmd.getOptionValue("name") + " ===");

            InputStream inputStream = cmd.hasOption('i') ? new FileInputStream(cmd.getOptionValue('i')) : System.in;

            OutputStream outputStream;
            File outputFolder = null, outputFile = null;
            if (cmd.hasOption('f')) {
                outputFolder = new File(cmd.getOptionValue('f'));
                if (!outputFolder.isDirectory())
                    outputFolder.mkdir();

                outputFile = new File(outputFolder, "/dataset.csv.tmp");
                outputStream = new FileOutputStream(outputFile);
            } else
                outputStream = cmd.hasOption('o') ? new FileOutputStream(cmd.getOptionValue('o')) : System.out;

            run(cmd, inputStream, outputStream);

            outputStream.close();
            inputStream.close();

            if (outputFolder != null) {
                try {
                    generateFields(cmd, outputFile, new File(outputFolder, "/fields.xml"));
                    outputFile.renameTo(new File(outputFolder, "/dataset.csv"));
                    generateMeta(cmd, new File(outputFolder, "/meta.xml"));
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
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

        int counter = 0, cols = 0;

        String[] line;
        while (csvReader.readRecord()) {
            line = csvReader.getValues();
            if (counter++ == 0) {
                cols = line.length;

                if (line[0].charAt(0) == 65279)
                    line[0] = line[0].substring(1);

                Map<String, String> fShortName = parseOptionArgs(cmd, "fshortname");

                for (int i = 0; i < line.length; i++) {
                    line[i] = line[i].replace(" ", "_").replace(".", "_");
                    if (fShortName.containsKey(line[i]))
                        line[i] = fShortName.get(line[i]);
                }
            } else if (line.length != cols) {
                System.err.println("Invalid amount of columns at line " + counter);
                continue;
            }

            csvWriter.writeRecord(line);
            csvWriter.flush();
        }

        csvWriter.close();
        csvReader.close();
    }

    private static void generateFields(CommandLine cmd, File csvSource, File xmlTarget) throws Exception {
        InputStream inputStream = new FileInputStream(csvSource);

        CsvReader csvReader = new CsvReader(inputStream, ';', Charset.forName("UTF-8"));
        csvReader.setTextQualifier('"');
        csvReader.setEscapeMode(CsvWriter.ESCAPE_MODE_BACKSLASH);

        csvReader.readHeaders();
        String[] header = csvReader.getHeaders();
        csvReader.close();

        inputStream.close();

        List<String> groupable = cmd.hasOption(GROUPABLE) ? Arrays.asList(cmd.getOptionValue(GROUPABLE).split(",")) : new ArrayList<String>();
        List<String> searchable = cmd.hasOption(SEARCHABLE) ? Arrays.asList(cmd.getOptionValue(SEARCHABLE).split(",")) : new ArrayList<String>();

        Map<String, String> fName = parseOptionArgs(cmd, "fname");
        Map<String, String> fDescription = parseOptionArgs(cmd, "fdescription");
        Map<String, String> fShortName = parseOptionArgs(cmd, "fshortname");

        Fields fields = new Fields();
        for (String h : header) {
            Field field = new Field();
            field.setName(fName.containsKey(h) ? fName.get(h) : h);
            field.setShortName(fShortName.containsKey(h) ? fShortName.get(h) : h);
            if (fDescription.containsKey(h))
                field.setContent(fDescription.get(h));
            field.setGroupable(groupable.contains(h));
            field.setSearchable(searchable.contains(h));
            fields.getFields().add(field);
        }

        writeXml(fields, xmlTarget);
    }

    private static void generateMeta(CommandLine cmd, File xmlTarget) throws Exception {
        Metadata metadata = new Metadata();
        metadata.setUpdated(System.currentTimeMillis());

        if (cmd.hasOption("name"))
            metadata.setName(cmd.getOptionValue("name"));
        if (cmd.hasOption("url"))
            metadata.setUrl(cmd.getOptionValue("url"));
        if (cmd.hasOption("description"))
            metadata.setDescription(cmd.getOptionValue("description"));
        metadata.setActive(!cmd.hasOption("disabled"));

        writeXml(metadata, xmlTarget);
    }

    private static void writeXml(Object o, File xmlTarget) throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(o.getClass());

        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        FileOutputStream out = new FileOutputStream(xmlTarget);
        m.marshal(o, out);
        out.close();
    }

    private static Map<String, String> parseOptionArgs(CommandLine cmd, String option) {
        Map<String, String> result = new HashMap<String, String>();
        if (cmd.hasOption(option))
            for (String opt : cmd.getOptionValues(option))
                result.put(opt.substring(0, opt.indexOf("=")), opt.substring(opt.indexOf("=") + 1));
        return result;
    }
}
