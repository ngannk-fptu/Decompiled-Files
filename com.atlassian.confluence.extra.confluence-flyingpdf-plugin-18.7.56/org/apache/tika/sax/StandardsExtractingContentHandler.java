/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.apache.tika.sax.StandardReference;
import org.apache.tika.sax.StandardsText;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class StandardsExtractingContentHandler
extends ContentHandlerDecorator {
    public static final String STANDARD_REFERENCES = "standard_references";
    private final Metadata metadata;
    private final StringBuilder stringBuilder;
    private int maxBufferLength = 100000;
    private double threshold = 0.0;

    public StandardsExtractingContentHandler(ContentHandler handler, Metadata metadata) {
        super(handler);
        this.metadata = metadata;
        this.stringBuilder = new StringBuilder();
    }

    protected StandardsExtractingContentHandler() {
        this(new DefaultHandler(), new Metadata());
    }

    public double getThreshold() {
        return this.threshold;
    }

    public void setThreshold(double score) {
        this.threshold = score;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        try {
            if (this.maxBufferLength > -1) {
                int remaining = this.maxBufferLength - this.stringBuilder.length();
                length = remaining > length ? length : remaining;
            }
            String text = new String(Arrays.copyOfRange(ch, start, start + length));
            this.stringBuilder.append(text);
            super.characters(ch, start, length);
        }
        catch (SAXException e) {
            this.handleException(e);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        ArrayList<StandardReference> standards = StandardsText.extractStandardReferences(this.stringBuilder.toString(), this.threshold);
        for (StandardReference standardReference : standards) {
            this.metadata.add(STANDARD_REFERENCES, standardReference.toString());
        }
    }

    public void setMaxBufferLength(int maxBufferLength) {
        this.maxBufferLength = maxBufferLength;
    }
}

