package no.difi.datahotel.converter;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CsvConverterTest {

    private static String newLine = System.getProperty("line.separator");

    @Test
    public void testComma() throws IOException, ParseException {
        CommandLine cmd = new BasicParser().parse(CsvConverter.options, new String[] {"-d", ",", "-c", "UTF-8"});
        String input = "Land,Country\nNorge,Norway\nSverige,Sweeden\n";
        String output = "\"Land\";\"Country\"" + newLine + "\"Norge\";\"Norway\"" +  newLine + "\"Sverige\";\"Sweeden\"" + newLine;

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        CsvConverter.run(cmd, byteArrayInputStream, byteArrayOutputStream);

        Assert.assertEquals(output, byteArrayOutputStream.toString());
    }

    @Test
    public void testSame() throws IOException, ParseException {
        CommandLine cmd = new BasicParser().parse(CsvConverter.options, new String[] {});
        String string = "\"Land\";\"Country\"" + newLine + "\"Norge\";\"Norway\"" + newLine + "\"Sverige\";\"Sweeden\"" + newLine;

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(string.getBytes());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        CsvConverter.run(cmd, byteArrayInputStream, byteArrayOutputStream);

        Assert.assertEquals(string, byteArrayOutputStream.toString());
    }

    @Test
    public void testRemoveDuplicates() throws IOException, ParseException {
        CommandLine cmd = new BasicParser().parse(CsvConverter.options, new String[] {"-c", "UTF-8", "--removeDuplicates"});
        String input = "\"Land\";\"Country\"" + newLine + "\"Norge\";\"Norway\"" +  newLine + "\"Sverige\";\"Sweeden\"" + newLine + "\"Sverige\";\"Sweeden\"" + newLine;
        String output = "\"Land\";\"Country\"" + newLine + "\"Norge\";\"Norway\"" +  newLine + "\"Sverige\";\"Sweeden\"" + newLine;

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        CsvConverter.run(cmd, byteArrayInputStream, byteArrayOutputStream);

        Assert.assertEquals(output, byteArrayOutputStream.toString());
    }
}
