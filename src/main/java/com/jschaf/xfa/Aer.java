package com.jschaf.xfa;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.time.LocalDate;

/**
 * Academic Evaluation Report
 */
@JacksonXmlRootElement(localName = "Page1")
public class Aer {

    @JsonProperty(value = "Todays_Date")
    @JacksonXmlProperty(localName = "Todays_Date")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public LocalDate todaysDate;

    @JsonProperty(value = "COURSE_TITLE")
    @JacksonXmlProperty(localName = "COURSE_TITLE")
    public String courseTitle;

}
