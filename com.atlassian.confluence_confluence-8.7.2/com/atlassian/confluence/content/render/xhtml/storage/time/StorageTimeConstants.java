/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.time;

import javax.xml.namespace.QName;

public class StorageTimeConstants {
    public static final String TIME_ELEMENT_NAME = new String("time");
    public static final String DATETIME_ATTRIBUTE_NAME = new String("datetime");
    public static final QName TIME_ELEMENT = new QName("http://atlassian.com/content", TIME_ELEMENT_NAME, "");
    public static final QName DATETIME_ATTRIBUTE = new QName("", DATETIME_ATTRIBUTE_NAME, "");
    public static final String DATE_FUTURE_CSS_CLASS = new String("date-future");
    public static final String DATE_UPCOMING_CSS_CLASS = new String("date-upcoming");
    public static final String DATE_PAST_CSS_CLASS = new String("date-past");
}

