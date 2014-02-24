package no.difi.datahotel.converter;

import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            List<String> arguments = Arrays.asList(args);
            if (arguments.size() == 0) {
                System.out.println("Available converters: csv");
            } else if (arguments.get(0).equals("csv")) {
                arguments.remove(0);
                CsvConverter.main(arguments.toArray(new String[]{}));
            } else {
                throw new Exception("Converter not found.");
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getSimpleName() + ": " + e.getMessage());
            System.exit(1);
        }
    }

}
