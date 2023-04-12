package com.example.appghiam;

import java.util.Date;
import java.util.concurrent.TimeUnit;
public class TimeAgo {
    public String getTimeAgo(long duration){

        Date now = new Date();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - duration);
        long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - duration);
        long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - duration);

        if (seconds < 60){
            return  "Vừa mới lưu";
        } else if (minutes == 1){
            return "lưu một vài phút trước";
        } else if (minutes > 1 && minutes < 60) {
            return "lưu " + minutes + "phút trước";
        } else if (hours == 1) {
            return "lưu gần một giờ trước";
        } else if (hours > 1 && hours < 24) {
            return "lưu " + hours + "hour ago";
        } else if (days == 1) {
            return "lưu một ngày trước";
        } else {
            return "lưu "+ days + "ngày trước";
        }
    }
}