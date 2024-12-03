/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser;

import org.apache.tika.detect.DefaultEncodingDetector;
import org.apache.tika.detect.EncodingDetector;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;

public abstract class AbstractEncodingDetectorParser
extends AbstractParser {
    private EncodingDetector encodingDetector;

    public AbstractEncodingDetectorParser() {
        this.encodingDetector = new DefaultEncodingDetector();
    }

    public AbstractEncodingDetectorParser(EncodingDetector encodingDetector) {
        this.encodingDetector = encodingDetector;
    }

    protected EncodingDetector getEncodingDetector(ParseContext parseContext) {
        EncodingDetector fromParseContext = parseContext.get(EncodingDetector.class);
        if (fromParseContext != null) {
            return fromParseContext;
        }
        return this.getEncodingDetector();
    }

    public EncodingDetector getEncodingDetector() {
        return this.encodingDetector;
    }

    public void setEncodingDetector(EncodingDetector encodingDetector) {
        this.encodingDetector = encodingDetector;
    }
}

