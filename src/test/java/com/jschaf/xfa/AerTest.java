package com.jschaf.xfa;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XMLAssert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.time.LocalDate;

import static org.testng.Assert.*;

/**
 * Test for AER functionality.
 */
public class AerTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final ObjectMapper xmlMapper = new XmlMapper();

    @BeforeClass
    public void setUp() {
        mapper.registerModule(new JavaTimeModule()) ;
        xmlMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void roundTrip() throws IOException {
        String json = "{\"Todays_Date\": \"2012-05-08\", \"COURSE_TITLE\": \"BENNING School for Boys\"}";
        Aer aer = mapper.readValue(json, Aer.class);
        assertEquals(aer.courseTitle, "BENNING School for Boys");
        assertEquals(aer.todaysDate, LocalDate.parse("2012-05-08"));
    }

    @Test
    public void xmlOutput() throws IOException, SAXException {
        String json = "{\"Todays_Date\": \"2012-05-08\", \"COURSE_TITLE\": \"BENNING School for Boys\"}";
        Aer aer = mapper.readValue(json, Aer.class);
        String generatedXML = xmlMapper.writeValueAsString(aer);
        System.out.println(generatedXML);
        String expectedXML = "<Page1><Todays_Date>2012-05-08</Todays_Date><COURSE_TITLE>BENNING School for Boys</COURSE_TITLE></Page1>";

        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual(expectedXML, generatedXML);
    }




}