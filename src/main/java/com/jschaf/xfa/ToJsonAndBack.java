package com.jschaf.xfa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

/**
 *
 */
public class ToJsonAndBack {

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectMapper xmlMapper = new XmlMapper();

        String json =  "{\"lastName\":\"Thorton\",\"firstName\":\"Thornton\",\"middleInital\":\"S.\",\"ssn\":\"123456789\",\"grade\":\"O3\",\"branch\":\"IN\",\"mos\":\"11A\"}";

        try {
            Student stud = mapper.readValue(json, Student.class);
            System.out.println(mapper.writeValueAsString(stud));

            System.out.println(xmlMapper.writeValueAsString(stud));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
