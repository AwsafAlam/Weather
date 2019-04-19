package com.awsafalam.weather;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

class Utility {
    static String getCurrentTime(String timeformat){
        Format formatter = new SimpleDateFormat(timeformat);
        return formatter.format(new Date());
    }
}
