/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport.parser;

import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveProperty;
import com.atlassian.confluence.importexport.xmlimport.parser.DefaultFragmentParser;
import com.atlassian.confluence.importexport.xmlimport.parser.FragmentParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

@Deprecated
public class PrimitivePropertyParser
extends DefaultFragmentParser<PrimitiveProperty> {
    private String name;
    private String type;
    private StringBuffer value = new StringBuffer();

    public PrimitivePropertyParser(String elementName) {
        super(elementName);
    }

    @Override
    protected void initialise(Attributes attributes) {
        this.name = attributes.getValue("name");
        this.type = attributes.getValue("type");
    }

    @Override
    protected FragmentParser<?> newDelegate(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        throw new SAXException("Element " + qName + " found in <property> where none expected");
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
    public PrimitiveProperty build() {
        return new PrimitiveProperty(this.name, this.type, this.value.toString());
    }
}

