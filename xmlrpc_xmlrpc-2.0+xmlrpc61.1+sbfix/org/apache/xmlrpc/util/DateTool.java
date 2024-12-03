/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTool {
    protected static final String FORMAT = "yyyyMMdd'T'HH:mm:ss";
    private DateFormat df = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");

    public synchronized String format(Date d) {
        return this.df.format(d);
    }

    public synchronized Date parse(String s) throws ParseException {
        return this.df.parse(s);
    }
}

