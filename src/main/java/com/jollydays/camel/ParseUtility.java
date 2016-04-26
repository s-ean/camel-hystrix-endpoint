// addition to original source
package com.jollydays.camel;

public class ParseUtility {

    public static Integer tryParseInt(String string) {
        if (string == null) return null;
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Unable to parse Integer from string: " + string);
        }
    }
    public static Boolean tryParseBoolean(String string) {
        if (string == null) return null;
        try {
            return Boolean.parseBoolean(string);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Unable to parse Boolean from string: " + string);
        }
    }
}
// end of addition