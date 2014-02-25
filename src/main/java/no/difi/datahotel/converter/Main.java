package no.difi.datahotel.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            List<String> arguments = new ArrayList<String>();
            for (int i = 1; i < args.length; i++)
                arguments.add(args[i]);

            if (args.length == 0) {
                System.out.println("Available converters: csv");
            } else if (args[0].equals("csv")) {
                CsvConverter.main(arguments.toArray(new String[]{}));
            } else {
                throw new Exception("Converter not found.");
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getSimpleName() + ": " + e.getMessage() + " (" + e.getStackTrace()[1].getClassName() + ":" + e.getStackTrace()[1].getLineNumber());
            System.exit(1);
        }
    }

}
