package com.jschaf.xfa;

/**
 */
class TemplateFunctions {

    public static String capitalizeFirstLetter(String s) {

        if (s.length() == 0) {
            return s;
        }
        StringBuilder stringBuilder = new StringBuilder(s.toLowerCase());
        stringBuilder.setCharAt(0, Character.toUpperCase(stringBuilder.charAt(0)));
        return stringBuilder.toString();

    }
}
