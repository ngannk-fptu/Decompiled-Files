/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.header;

import com.sun.jersey.core.header.QualityFactor;
import com.sun.jersey.core.header.Token;
import com.sun.jersey.core.header.reader.HttpHeaderReader;
import java.text.ParseException;

public class AcceptableToken
extends Token
implements QualityFactor {
    protected int quality = 1000;

    public AcceptableToken(String header) throws ParseException {
        this(HttpHeaderReader.newInstance(header));
    }

    public AcceptableToken(HttpHeaderReader reader) throws ParseException {
        reader.hasNext();
        this.token = reader.nextToken();
        if (reader.hasNext()) {
            this.quality = HttpHeaderReader.readQualityFactorParameter(reader);
        }
    }

    @Override
    public int getQuality() {
        return this.quality;
    }
}

