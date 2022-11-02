package de.bcxp.challenge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

public class TaskManager {

    private static final Logger logger = LoggerFactory.getLogger(TaskManager.class);
    private static final String weatherFile = "weather.csv";
    private static final String designationMaxTempInFile = "MxT";
    private static final String designationMinTempInFile = "MnT";

    private static final String countriesFile = "countries.csv";
    private static final String designationArea = "Area (km²)";
    private static final String designationPopulation = "Population";

    private static final char commaSeparator = ',';
    private static final char semicolonSeparator = ';';

    public String weatherTask() {
        URL resource = App.class.getResource(weatherFile);
        MyReader myReader = MyReader.readFile(resource.getPath().replaceFirst("/", ""), commaSeparator);
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

    public String countriesTask() {
        URL resource = App.class.getResource(countriesFile);
        MyReader myReader = MyReader.readFile(resource.getPath().replaceFirst("/", ""), semicolonSeparator);
        if (myReader == null) {
            logger.error("The file {} could not be read or the path is wrong", countriesFile);
            return null;
        }

        List<String> countryNameCol = myReader.readCol("Name");
        // country with the highest number of people per square kilometre
        String countryWithMaxPopProSqKm = "Unknown";
        double maxPopProSqKm = -1;
        for (String country : countryNameCol) {
            // calculate the country’s population density
            double popProSqKm = calcPopProSqKm(myReader, country);
            if (maxPopProSqKm < popProSqKm) {
                maxPopProSqKm = popProSqKm;
                countryWithMaxPopProSqKm = country;
            }
        }
        return countryWithMaxPopProSqKm;
    }

    private double calcPopProSqKm(MyReader myReader, String country) {
        NumberFormat format = NumberFormat.getInstance();
        try {
            double area = format.parse(myReader.readValue(country, designationArea)).doubleValue();
            double population = format.parse(myReader.readValue(country, designationPopulation)).doubleValue();
            return population / area;
        } catch (NumberFormatException e) {
            logger.warn("Can't calculate the the country’s population density for {}, " +
                    "because of an invalid input", country);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return -1;
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
