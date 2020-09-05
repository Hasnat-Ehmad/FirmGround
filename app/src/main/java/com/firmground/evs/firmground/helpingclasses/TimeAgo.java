package com.firmground.evs.firmground.helpingclasses;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeAgo {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            String dateString = formatter.format(new Date(Long.parseLong(String.valueOf(time))));
            return ""+dateString;
            //return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            String dateString = formatter.format(new Date(Long.parseLong(String.valueOf(time))));
            return ""+dateString;
            //return "a min ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            String dateString = formatter.format(new Date(Long.parseLong(String.valueOf(time))));
            return ""+dateString;
            //return diff / MINUTE_MILLIS + " min ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            String dateString = formatter.format(new Date(Long.parseLong(String.valueOf(time))));
            return ""+dateString;
            //return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            String dateString = formatter.format(new Date(Long.parseLong(String.valueOf(time))));
            return ""+dateString;
            //return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            String dateString = sdf.format(new Date(Long.parseLong(String.valueOf(time))));
            return ""+dateString;
        } else if (diff < 72 * HOUR_MILLIS) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            String dateString = sdf.format(new Date(Long.parseLong(String.valueOf(time))));
            return ""+dateString;
        } else if (diff < 96 * HOUR_MILLIS) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            String dateString = sdf.format(new Date(Long.parseLong(String.valueOf(time))));
            return ""+dateString;
        } else if (diff < 120 * HOUR_MILLIS) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            String dateString = sdf.format(new Date(Long.parseLong(String.valueOf(time))));
            return ""+dateString;
        }else if (diff < 144 * HOUR_MILLIS) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            String dateString = sdf.format(new Date(Long.parseLong(String.valueOf(time))));
            return ""+dateString;
        }else if (diff < 168 * HOUR_MILLIS) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            String dateString = sdf.format(new Date(Long.parseLong(String.valueOf(time))));
            return ""+dateString;
        }else if (diff < 192 * HOUR_MILLIS) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            String dateString = sdf.format(new Date(Long.parseLong(String.valueOf(time))));
            return ""+dateString;
        }else {
            SimpleDateFormat formatter_ = new SimpleDateFormat("dd/MM/yyyy");
            String dateString = formatter_.format(new Date(Long.parseLong(String.valueOf(time))));
            return dateString;
            //return diff / DAY_MILLIS + " days ago";
        }
    }
}