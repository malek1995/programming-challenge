package de.bcxp.challenge;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.opencsv.CSVReader;


import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class MyCSVReader implements MyReader {
    private static final Logger logger = LoggerFactory.getLogger(MyCSVReader.class);
    private List<String[]> fileContent;
    private List<String> labels;

    public MyCSVReader(String fileName, char separator) throws IOException, CsvException {
        fileContent = readContent(fileName, separator);
        if (fileContent.size() < 2) {
            throw new RuntimeException("The given file is empty");
        }
        // using Arrays.asList instead of List.of just to use the method set
        labels = Arrays.asList(fileContent.get(0));
        // by reading the file countries, there was some extra bytes at the beginning,
        // these can be removed using the following line
        String str = StringUtils.removeStart(labels.get(0), "\uFEFF");
        labels.set(0, str);
        fileContent.remove(0);
    }

    @Override
    public List<String> readRow(String rowName) {
        for (String[] row : fileContent) {
            if (row[0].equals(rowName)) {
                return List.of(row);
            }
        }
        logger.error("No row with the name {} was found", rowName);
        return null;
    }

    @Override
    public List<String> readCol(String colName) {
        int index = labels.indexOf(colName);
        if (index < 0) {
            logger.error("No colum with the name {} was found", colName);
            return null;
        }
        ArrayList<String> col = new ArrayList<>();
        for (String[] row : fileContent) {
            col.add(row[index]);
        }
        return col;
    }

    @Override
    public String readValue(String rowName, String colName) {
        List<String> row = this.readRow(rowName);
        if (row == null) {
            logger.error("The row with the name {} can not be read or not exist", rowName);
            return null;
        }
        int index = labels.indexOf(colName);
        if (index < 0) {
            logger.error("No colum with the name {} was founded", colName);
            return null;
        }
        return row.get(index);
    }

    private List<String[]> readContent(String fileName, char separator) throws IOException, CsvException {
        FileReader fileReader = new FileReader(fileName);
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(separator)
                .build();
        CSVReader csvReader = new CSVReaderBuilder(fileReader)
                .withCSVParser(parser)
                .build();
        List<String[]> fileContent = csvReader.readAll();
        fileReader.close();
        csvReader.close();
        return fileContent;
    }

    public List<String> getLabels() {
        return labels;
    }
}
