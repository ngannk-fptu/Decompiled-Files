/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.util.AttributeSource
 */
package org.apache.lucene.analysis.sinks;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import org.apache.lucene.analysis.sinks.TeeSinkTokenFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeSource;

public class DateRecognizerSinkFilter
extends TeeSinkTokenFilter.SinkFilter {
    public static final String DATE_TYPE = "date";
    protected DateFormat dateFormat;
    protected CharTermAttribute termAtt;

    public DateRecognizerSinkFilter() {
        this(DateFormat.getDateInstance(2, Locale.ROOT));
    }

    public DateRecognizerSinkFilter(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public boolean accept(AttributeSource source) {
        if (this.termAtt == null) {
            this.termAtt = (CharTermAttribute)source.addAttribute(CharTermAttribute.class);
        }
        try {
            Date date = this.dateFormat.parse(this.termAtt.toString());
            if (date != null) {
                return true;
            }
        }
        catch (ParseException parseException) {
            // empty catch block
        }
        return false;
    }
}

