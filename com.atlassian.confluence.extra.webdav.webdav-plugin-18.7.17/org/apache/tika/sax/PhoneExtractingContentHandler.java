/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.CleanPhoneText;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PhoneExtractingContentHandler
extends ContentHandlerDecorator {
    private static final String PHONE_NUMBERS = "phonenumbers";
    private final Metadata metadata;
    private final StringBuilder stringBuilder;

    public PhoneExtractingContentHandler(ContentHandler handler, Metadata metadata) {
        super(handler);
        this.metadata = metadata;
        this.stringBuilder = new StringBuilder();
    }

    protected PhoneExtractingContentHandler() {
        this(new DefaultHandler(), new Metadata());
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        try {
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
        ArrayList<String> numbers = CleanPhoneText.extractPhoneNumbers(this.stringBuilder.toString());
        for (String number : numbers) {
            this.metadata.add(PHONE_NUMBERS, number);
        }
    }
}

