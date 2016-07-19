package com.kab.draganddroplist;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Kraskovskiy on 12.07.2016.
 */
public class Utils {
    private Utils() {}

    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        return dateFormat.format(c.getTime());
    }
}