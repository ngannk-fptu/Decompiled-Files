/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.importexport.xmlimport.parser;

import com.atlassian.confluence.importexport.xmlimport.parser.FragmentParser;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

@Deprecated
public abstract class DefaultFragmentParser<T>
extends DefaultHandler
implements FragmentParser<T> {
    private final String elementName;
    private FragmentParser<?> delegate;
    private boolean done = false;

    public DefaultFragmentParser(String elementName) {
        this.elementName = elementName;
    }

    @Override
    public final void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (this.getDelegate() == null) {
            if (this.elementName.equals(qName)) {
                this.initialise(attributes);
            } else {
                this.setDelegate(this.newDelegate(uri, localName, qName, attributes));
            }
        }
        if (this.getDelegate() != null) {
            this.getDelegate().startElement(uri, localName, qName, attributes);
        }
    }

    @Override
    public final void characters(char[] ch, int start, int length) throws SAXException {
        if (this.delegate != null) {
            this.delegate.characters(ch, start, length);
        } else {
            this.handleCharacters(ch, start, length);
        }
    }

    @Override
    public final void endElement(String uri, String localName, String qName) throws SAXException {
        if (this.getDelegate() != null) {
            this.getDelegate().endElement(uri, localName, qName);
            if (this.getDelegate().isDone()) {
                this.delegateDone();
            }
        } else if (this.elementName.equals(qName)) {
            this.done = true;
        }
    }

    protected void handleCharacters(char[] ch, int start, int length) throws SAXException {
        String chars = new String(ch, start, length);
        if (!StringUtils.isBlank((CharSequence)chars)) {
            throw new SAXException("Non-whitespace found where none expected in <" + this.elementName + ">: " + chars);
        }
    }

    protected FragmentParser<?> getDelegate() {
        return this.delegate;
    }

    protected void setDelegate(FragmentParser<?> delegate) {
        this.delegate = delegate;
    }

    protected abstract void initialise(Attributes var1);

    protected abstract FragmentParser<?> newDelegate(String var1, String var2, String var3, Attributes var4) throws SAXException;

    protected abstract void delegateDone();

    @Override
    public boolean isDone() {
        return this.done;
    }
}

