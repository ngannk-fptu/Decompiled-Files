/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.ext.thread;

import javax.xml.namespace.QName;
import org.apache.abdera.util.Constants;

public interface ThreadConstants
extends Constants {
    public static final String THR_NS = "http://purl.org/syndication/thread/1.0";
    public static final String LN_INREPLYTO = "in-reply-to";
    public static final String LN_REF = "ref";
    public static final String LN_COUNT = "count";
    public static final String LN_WHEN = "when";
    public static final String LN_TOTAL = "total";
    public static final String THR_PREFIX = "thr";
    public static final QName IN_REPLY_TO = new QName("http://purl.org/syndication/thread/1.0", "in-reply-to", "thr");
    public static final QName THRCOUNT = new QName("http://purl.org/syndication/thread/1.0", "count", "thr");
    public static final QName THRWHEN = new QName("http://purl.org/syndication/thread/1.0", "when", "thr");
    public static final QName THRUPDATED = new QName("http://purl.org/syndication/thread/1.0", "updated", "thr");
    public static final QName THRTOTAL = new QName("http://purl.org/syndication/thread/1.0", "total", "thr");
    public static final QName THRREF = new QName("ref");
    public static final QName THRSOURCE = new QName("source");
}

