/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.impl.provider.header;

import com.sun.jersey.core.header.HttpDateFormat;
import com.sun.jersey.core.header.reader.HttpHeaderReader;
import com.sun.jersey.spi.HeaderDelegateProvider;
import java.text.ParseException;
import java.util.Date;

public class DateProvider
implements HeaderDelegateProvider<Date> {
    @Override
    public boolean supports(Class<?> type) {
        return Date.class.isAssignableFrom(type);
    }

    public String toString(Date header) {
        return HttpDateFormat.getPreferedDateFormat().format(header);
    }

    public Date fromString(String header) {
        try {
            return HttpHeaderReader.readDate(header);
        }
        catch (ParseException ex) {
            throw new IllegalArgumentException("Error parsing date '" + header + "'", ex);
        }
    }
}

