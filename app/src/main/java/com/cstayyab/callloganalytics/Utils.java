package com.cstayyab.callloganalytics;

public class Utils {
    /**
     * Convert Seconds to mm:ss
     *
     * @param timeInSeconds Seconds to convert in mm:ss format
     * @return String containing formatted time in mm:ss format
     */
    public static String formatSeconds(long timeInSeconds)
    {
        //int hours = (int) (timeInSeconds / 3600);
        int minutes = (int) (timeInSeconds / 60);
        int secondsLeft = (int) (timeInSeconds - (minutes * 60));
        int seconds = secondsLeft;

        String formattedTime = "";

            if (minutes < 10)
                formattedTime += "0";
            formattedTime += minutes + ":";

            if (seconds < 10)
                formattedTime += "0";
            formattedTime += seconds ;

        return formattedTime;
    }
}