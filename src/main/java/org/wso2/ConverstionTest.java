package org.wso2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConverstionTest {

    public static void main(String[] args) {

        String hexString = "10";

        int parsedHexValue = Integer.parseInt(hexString, 16);

        System.out.println(hexString + "  -->  " + parsedHexValue);

        System.out.println(parsedHexValue);
        System.out.println((char)parsedHexValue);

        String log = "[0xab][0xf][[[[0xf]abc";

        String patternString = "\\[0x.*?\\]";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(log);

        int count = 0;
        while(matcher.find()) {
            count++;
            System.out.println("found: " + count + " : "
                    + matcher.start() + " - " + matcher.end());
        }



    }

}
