package de.bcxp.challenge;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Example JUnit 5 test case.
 */
class AppTest {

    private static final String weatherFile = "weather.csv";
    private static final String countriesFile = "countries.csv";
    private static final char commaSeparator = ',';
    private static final char semicolonSeparator = ';';
    private static MyReader myReaderOfWeather;
    private static MyReader myReaderOfCountries;
    private static URL resourceOfWeather;
    private static URL resourceOfCountries;


    @BeforeAll
    static void setUp() {
        resourceOfWeather = App.class.getResource(weatherFile);
        myReaderOfWeather = MyReader.readFile(resourceOfWeather.getPath().replaceFirst("/", ""),
                commaSeparator);
        resourceOfCountries = App.class.getResource(countriesFile);
        myReaderOfCountries = MyReader.readFile(resourceOfCountries.getPath().replaceFirst("/", ""),
                semicolonSeparator);
    }

    @Test
    void fileNotFoundTest() {
        String dummyFileName = "text.txt";
        assertNull(MyReader.readFile(dummyFileName, commaSeparator));

        URL dummyResource = App.class.getResource(dummyFileName);
        assertNull(dummyResource);

        URL resource = App.class.getResource(weatherFile);
        assertNotNull(resource);
        assertNotNull(MyReader.readFile(resource.getPath().replaceFirst("/", ""), commaSeparator));
    }

    @Test
    void readValueTest() {
        // Data from weather file
        final String[] labelsOfWeather = {"Day", "MxT", "MnT", "AvT", "AvDP", "1HrP TPcpn", "PDir", "AvSp",
                "Dir", "MxS", "SkyC", "MxR", "Mn", "R AvSLP"};
        final String RAvSLPOnDay6 = "1012.7";
        final String AvTOnDay13 = "65";
        final String MxTOnDay22 = "90";
        final String MnTOnDay11 = "59";

        // Tests for readValue on weather file
        assertEquals(RAvSLPOnDay6, myReaderOfWeather.readValue("6", labelsOfWeather[13]));
        assertEquals(AvTOnDay13, myReaderOfWeather.readValue("13", labelsOfWeather[3]));
        assertEquals(MxTOnDay22, myReaderOfWeather.readValue("22", labelsOfWeather[1]));
        assertEquals(MnTOnDay11, myReaderOfWeather.readValue("11", labelsOfWeather[2]));

        // Tests for comparing the labels
        assertEquals(labelsOfWeather[0], myReaderOfWeather.getLabels().get(0));
        assertEquals(labelsOfWeather[6], myReaderOfWeather.getLabels().get(6));

        // Data from countries file
        final String[] labelsOfCountries = {"Name", "Capital", "Accession", "Population",
                "Area (km²)", "GDP (US$ M)", "HDI", "MEPs"};

        final String countryOfRow1 = "Austria";
        final String capitalOfCroatia = "Zagreb";
        final String accessionOfCzech = "2004";
        final String populationOfGermany = "83120520";
        final String areaOfSweden = "449964";

        // Tests for readValue on countries file
        assertEquals(countryOfRow1, myReaderOfCountries.readCol(labelsOfCountries[0]).get(0));
        assertEquals(capitalOfCroatia, myReaderOfCountries.readValue("Croatia", labelsOfCountries[1]));
        assertEquals(accessionOfCzech, myReaderOfCountries.readValue("Czech Republic", labelsOfCountries[2]));
        assertEquals(populationOfGermany, myReaderOfCountries.readValue("Germany", labelsOfCountries[3]));
        assertEquals(areaOfSweden, myReaderOfCountries.readValue("Sweden", labelsOfCountries[4]));

        // Tests for comparing the labels
        assertEquals(labelsOfCountries[6], myReaderOfCountries.getLabels().get(6));
        assertEquals(labelsOfCountries[5], myReaderOfCountries.getLabels().get(5));
    }

    @Test
    void colAndRowTest() {
        assertNull(myReaderOfWeather.readCol("TOcpn"));
        assertNull(myReaderOfWeather.readRow("33"));
        assertNull(myReaderOfCountries.readCol("Area"));
        assertNull(myReaderOfCountries.readRow("Berlin"));
    }

    @Test
    void wrongSeparatorTest() {
        MyReader myReaderOfWeatherWithWrongSeparator = MyReader.readFile(
                resourceOfWeather.getPath().replaceFirst("/", ""), semicolonSeparator);
        assertEquals(1, myReaderOfWeatherWithWrongSeparator.getLabels().size());

        MyReader myReaderOfCountriesWithWrongSeparator = MyReader.readFile(
                resourceOfCountries.getPath().replaceFirst("/", ""), commaSeparator);
        assertEquals(1, myReaderOfCountriesWithWrongSeparator.getLabels().size());
    }

}