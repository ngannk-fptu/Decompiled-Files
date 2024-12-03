/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package com.atlassian.pats.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateAdapter
extends XmlAdapter<String, Date> {
    private static final String CUSTOM_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSS+00:00";

    public String marshal(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CUSTOM_FORMAT_STRING);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(date);
    }

    public Date unmarshal(String date) throws ParseException {
        return new SimpleDateFormat(CUSTOM_FORMAT_STRING).parse(date);
    }
}

