/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.MediaType
 */
package com.sun.jersey.core.header;

import com.sun.jersey.core.header.QualityFactor;
import com.sun.jersey.core.header.reader.HttpHeaderReader;
import java.text.ParseException;
import java.util.Map;
import javax.ws.rs.core.MediaType;

public class AcceptableMediaType
extends MediaType
implements QualityFactor {
    private final int q;

    public AcceptableMediaType(String p, String s) {
        super(p, s);
        this.q = 1000;
    }

    public AcceptableMediaType(String p, String s, int q, Map<String, String> parameters) {
        super(p, s, parameters);
        this.q = q;
    }

    @Override
    public int getQuality() {
        return this.q;
    }

    public static AcceptableMediaType valueOf(HttpHeaderReader reader) throws ParseException {
        String v;
        reader.hasNext();
        String type = reader.nextToken();
        String subType = "*";
        if (reader.hasNextSeparator('/', false)) {
            reader.next(false);
            subType = reader.nextToken();
        }
        Map<String, String> parameters = null;
        int quality = 1000;
        if (reader.hasNext() && (parameters = HttpHeaderReader.readParameters(reader)) != null && (v = parameters.get("q")) != null) {
            quality = HttpHeaderReader.readQualityFactor(v);
        }
        return new AcceptableMediaType(type, subType, quality, parameters);
    }
}

