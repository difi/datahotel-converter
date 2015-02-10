package no.difi.datahotel.converter;

import no.difi.datahotel.model.Metadata;
import org.apache.commons.cli.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FolderConverter {

    static Options options;

    static {
        options = new Options();
        options.addOption(new Option("help", "print this message"));

        options.addOption(OptionBuilder.withArgName("outputFolder").hasArg().withDescription("output folder").isRequired().create("f"));
        options.addOption(OptionBuilder.withArgName("name").hasArg().withDescription("dataset name").create("name"));
        options.addOption(OptionBuilder.withArgName("url").hasArg().withDescription("dataset url").create("url"));
        options.addOption(OptionBuilder.withArgName("description").hasArg().withDescription("dataset description").create("description"));
        options.addOption(OptionBuilder.withDescription("Dataset disabled").create("disabled"));
    }

    public static void main(String[] args) throws IOException, ParseException {
        CommandLine cmd = new GnuParser().parse(options, args);
        if (cmd.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("datahotel-converter", FolderConverter.options);
        } else {
            try {
                if (cmd.hasOption("name"))
                    System.out.println("=== " + fix(cmd.getOptionValue("name")) + " ===");

                File outputFolder = new File(cmd.getOptionValue('f'));
                if (!outputFolder.isDirectory())
                    outputFolder.mkdir();

                generateMeta(cmd, new File(outputFolder, "/meta.xml"));
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private static void generateMeta(CommandLine cmd, File xmlTarget) throws Exception {
        Metadata metadata = new Metadata();

        if (cmd.hasOption("name"))
            metadata.setName(fix(cmd.getOptionValue("name")));
        if (cmd.hasOption("url"))
            metadata.setUrl(cmd.getOptionValue("url"));
        if (cmd.hasOption("description"))
            metadata.setDescription(fix(cmd.getOptionValue("description")));
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

    private static String fix(String s) {
        return s.replace("_", " ");
    }
}
