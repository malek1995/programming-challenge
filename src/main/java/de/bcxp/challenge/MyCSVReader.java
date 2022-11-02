package de.bcxp.challenge;

import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.opencsv.CSVReader;


import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyCSVReader implements MyReader {
    private static final Logger logger = LoggerFactory.getLogger(MyCSVReader.class);
    private List<String[]> fileContent;
    private List<String> labels;

    public MyCSVReader(String fileName) throws IOException, CsvException {
        fileContent = readContent(fileName);
        if (fileContent.size() < 2) {
            throw new RuntimeException("The given file is empty");
        }
        labels = List.of(fileContent.get(0));
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

    private List<String[]> readContent(String fileName) throws IOException, CsvException {
        FileReader fileReader = new FileReader(fileName);
        CSVReader csvReader = new CSVReader(fileReader);
        List<String[]> fileContent = csvReader.readAll();
        fileReader.close();
        csvReader.close();
        return fileContent;
    }
}
