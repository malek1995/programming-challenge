package de.bcxp.challenge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;

public class TaskManager {

    private static final Logger logger = LoggerFactory.getLogger(TaskManager.class);
    private static final String weatherFile = "weather.csv";
    private static final String designationMaxTempInFile = "MxT";
    private static final String designationMinTempInFile = "MnT";

    public String weatherTask() {
        URL resource = App.class.getResource(weatherFile);
        MyReader myReader = MyReader.readFile(resource.getPath().replaceFirst("/", ""));
        if (myReader == null) {
            logger.error("The file {} could not be read or the path is wrong", weatherFile);
            return null;
        }
        // get all days from the file content, which is already created in MyCSVReader
        List<String> dayCol = myReader.readCol("Day");
        String dayWithMaxTempSpread = "Unknown";
        double maxTempSpread = -1;
        for (String day : dayCol) {
            // calculate the temperature spread for every day
            double tempSpread = calcTempSpread(myReader, day);
            if (tempSpread > maxTempSpread) {
                dayWithMaxTempSpread = day;
                maxTempSpread = tempSpread;
            }
        }
        return dayWithMaxTempSpread;
    }

    private double calcTempSpread(MyReader myReader, String day) {
        try {
            double minTemp = Double.parseDouble(myReader.readValue(day, designationMinTempInFile));
            double maxTemp = Double.parseDouble(myReader.readValue(day, designationMaxTempInFile));
            return maxTemp - minTemp;
        } catch (NumberFormatException e) {
            logger.warn("Can't calculate the temperature spread for day {}, because of an invalid input", day);
        }
        return -1;
    }
}
