package de.bcxp.challenge;


import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public interface MyReader {

    Logger logger = LoggerFactory.getLogger(MyReader.class);

    static MyReader readFile(String filename, char separator) {
        try {
            switch (FilenameUtils.getExtension(filename)) {
                case "csv":
                    logger.info("Reading the csv file {}", filename);
                    return new MyCSVReader(filename, separator);
                // todo add other implementation
                default:
                    logger.error("File extension is unknown");
                    return null;
            }
        } catch (Exception e) {
            logger.error("Reading the file failed with the following exception", e);
            return null;
        }
    }

    List<String> readRow(String rowName);

    List<String> readCol(String colName);

    String readValue(String rowName, String colName);

    // Just used in the tests
    public List<String> getLabels();
}
