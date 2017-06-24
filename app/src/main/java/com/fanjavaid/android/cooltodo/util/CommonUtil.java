package com.fanjavaid.android.cooltodo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fanjavaid on 6/23/17.
 */

public class CommonUtil {
    public static String formatDate(String date) throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date parsingDate = sf.parse(date);

        SimpleDateFormat sfNewFormat = new SimpleDateFormat("dd MMM yyyy");
        String newDateFormat = sfNewFormat.format(parsingDate);

        return newDateFormat;
    }
}
