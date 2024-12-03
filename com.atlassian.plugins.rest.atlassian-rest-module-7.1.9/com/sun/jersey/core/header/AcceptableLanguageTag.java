/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.header;

import com.sun.jersey.core.header.LanguageTag;
import com.sun.jersey.core.header.QualityFactor;
import com.sun.jersey.core.header.reader.HttpHeaderReader;
import java.text.ParseException;

public class AcceptableLanguageTag
extends LanguageTag
implements QualityFactor {
    protected int quality = 1000;

    public AcceptableLanguageTag(String primaryTag, String subTags) {
        super(primaryTag, subTags);
    }

    public AcceptableLanguageTag(String header) throws ParseException {
        this(HttpHeaderReader.newInstance(header));
    }

    public AcceptableLanguageTag(HttpHeaderReader reader) throws ParseException {
        reader.hasNext();
        this.tag = reader.nextToken();
        if (!this.tag.equals("*")) {
            this.parse(this.tag);
        } else {
            this.primaryTag = this.tag;
        }
        if (reader.hasNext()) {
            this.quality = HttpHeaderReader.readQualityFactorParameter(reader);
        }
    }

    @Override
    public int getQuality() {
        return this.quality;
    }
}

