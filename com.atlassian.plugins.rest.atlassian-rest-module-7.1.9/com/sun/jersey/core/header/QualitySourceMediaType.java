/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.header;

import com.sun.jersey.core.header.reader.HttpHeaderReader;
import java.text.ParseException;
import java.util.Map;
import javax.ws.rs.core.MediaType;

public class QualitySourceMediaType
extends MediaType {
    public static final String QUALITY_SOURCE_FACTOR = "qs";
    public static final int DEFAULT_QUALITY_SOURCE_FACTOR = 1000;
    private final int qs;

    public QualitySourceMediaType(String p, String s) {
        super(p, s);
        this.qs = 1000;
    }

    public QualitySourceMediaType(String p, String s, int qs, Map<String, String> parameters) {
        super(p, s, parameters);
        this.qs = qs;
    }

    public QualitySourceMediaType(MediaType mt) {
        this(mt.getType(), mt.getSubtype(), QualitySourceMediaType.getQs(mt), mt.getParameters());
    }

    public int getQualitySource() {
        return this.qs;
    }

    public static QualitySourceMediaType valueOf(HttpHeaderReader reader) throws ParseException {
        reader.hasNext();
        String type = reader.nextToken();
        reader.nextSeparator('/');
        String subType = reader.nextToken();
        int qs = 1000;
        Map<String, String> parameters = null;
        if (reader.hasNext() && (parameters = HttpHeaderReader.readParameters(reader)) != null) {
            qs = QualitySourceMediaType.getQs(parameters.get(QUALITY_SOURCE_FACTOR));
        }
        return new QualitySourceMediaType(type, subType, qs, parameters);
    }

    public static int getQualitySource(MediaType mt) {
        if (mt instanceof QualitySourceMediaType) {
            QualitySourceMediaType qsmt = (QualitySourceMediaType)mt;
            return qsmt.getQualitySource();
        }
        return QualitySourceMediaType.getQs(mt);
    }

    private static int getQs(MediaType mt) {
        try {
            return QualitySourceMediaType.getQs(mt.getParameters().get(QUALITY_SOURCE_FACTOR));
        }
        catch (ParseException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    private static int getQs(String v) throws ParseException {
        if (v == null) {
            return 1000;
        }
        try {
            int qs = (int)((double)Float.valueOf(v).floatValue() * 1000.0);
            if (qs < 0) {
                throw new ParseException("The quality source (qs) value, " + v + ", must be non-negative number", 0);
            }
            return qs;
        }
        catch (NumberFormatException ex) {
            ParseException pe = new ParseException("The quality source (qs) value, " + v + ", is not a valid value", 0);
            pe.initCause(ex);
            throw pe;
        }
    }
}

