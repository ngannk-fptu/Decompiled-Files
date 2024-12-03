/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport.parser;

import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveId;
import com.atlassian.confluence.importexport.xmlimport.parser.DefaultFragmentParser;
import com.atlassian.confluence.importexport.xmlimport.parser.FragmentParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

@Deprecated
public class PrimitiveIdParser
extends DefaultFragmentParser<PrimitiveId> {
    private String name;
    private StringBuffer value = new StringBuffer();

    public PrimitiveIdParser() {
        super("id");
    }

    @Override
    protected void initialise(Attributes attributes) {
        this.name = attributes.getValue("name");
    }

    @Override
    protected FragmentParser<?> newDelegate(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        throw new SAXException("Element " + qName + " found in <id> where none expected");
    }

    @Override
    protected void delegateDone() {
        throw new IllegalStateException("This method should never be called");
    }

    @Override
    public void handleCharacters(char[] ch, int start, int length) throws SAXException {
        this.value.append(ch, start, length);
    }

    @Override
    public PrimitiveId build() {
        return new PrimitiveId(this.name, this.value.toString());
    }
}

