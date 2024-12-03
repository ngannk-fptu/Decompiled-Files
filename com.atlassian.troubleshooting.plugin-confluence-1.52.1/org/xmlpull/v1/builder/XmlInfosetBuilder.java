/*
 * Decompiled with CFR 0.152.
 */
package org.xmlpull.v1.builder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;
import org.xmlpull.v1.builder.XmlBuilderException;
import org.xmlpull.v1.builder.XmlDocument;
import org.xmlpull.v1.builder.XmlElement;
import org.xmlpull.v1.builder.XmlNamespace;
import org.xmlpull.v1.builder.impl.XmlInfosetBuilderImpl;

public abstract class XmlInfosetBuilder {
    protected XmlPullParserFactory factory;

    public static XmlInfosetBuilder newInstance() throws XmlBuilderException {
        XmlInfosetBuilderImpl impl = new XmlInfosetBuilderImpl();
        try {
            impl.factory = XmlPullParserFactory.newInstance(System.getProperty("org.xmlpull.v1.XmlPullParserFactory"), null);
            impl.factory.setNamespaceAware(true);
        }
        catch (XmlPullParserException ex) {
            throw new XmlBuilderException("could not create XmlPull factory:" + ex, ex);
        }
        return impl;
    }

    public static XmlInfosetBuilder newInstance(XmlPullParserFactory factory) throws XmlBuilderException {
        if (factory == null) {
            throw new IllegalArgumentException();
        }
        XmlInfosetBuilderImpl impl = new XmlInfosetBuilderImpl();
        impl.factory = factory;
        impl.factory.setNamespaceAware(true);
        return impl;
    }

    public XmlPullParserFactory getFactory() throws XmlBuilderException {
        return this.factory;
    }

    public XmlDocument newDocument() throws XmlBuilderException {
        return this.newDocument(null, null, null);
    }

    public abstract XmlDocument newDocument(String var1, Boolean var2, String var3) throws XmlBuilderException;

    public abstract XmlElement newFragment(String var1) throws XmlBuilderException;

    public abstract XmlElement newFragment(String var1, String var2) throws XmlBuilderException;

    public abstract XmlElement newFragment(XmlNamespace var1, String var2) throws XmlBuilderException;

    public abstract XmlNamespace newNamespace(String var1) throws XmlBuilderException;

    public abstract XmlNamespace newNamespace(String var1, String var2) throws XmlBuilderException;

    public abstract XmlDocument parse(XmlPullParser var1) throws XmlBuilderException;

    public abstract Object parseItem(XmlPullParser var1) throws XmlBuilderException;

    public abstract XmlElement parseStartTag(XmlPullParser var1) throws XmlBuilderException;

    public XmlDocument parseInputStream(InputStream is) throws XmlBuilderException {
        XmlPullParser pp = null;
        try {
            pp = this.factory.newPullParser();
            pp.setInput(is, null);
        }
        catch (XmlPullParserException e) {
            throw new XmlBuilderException("could not start parsing input stream", e);
        }
        return this.parse(pp);
    }

    public XmlDocument parseInputStream(InputStream is, String encoding) throws XmlBuilderException {
        XmlPullParser pp = null;
        try {
            pp = this.factory.newPullParser();
            pp.setInput(is, encoding);
        }
        catch (XmlPullParserException e) {
            throw new XmlBuilderException("could not start parsing input stream (encoding=" + encoding + ")", e);
        }
        return this.parse(pp);
    }

    public XmlDocument parseReader(Reader reader) throws XmlBuilderException {
        XmlPullParser pp = null;
        try {
            pp = this.factory.newPullParser();
            pp.setInput(reader);
        }
        catch (XmlPullParserException e) {
            throw new XmlBuilderException("could not start parsing input from reader", e);
        }
        return this.parse(pp);
    }

    public abstract XmlDocument parseLocation(String var1) throws XmlBuilderException;

    public abstract XmlElement parseFragment(XmlPullParser var1) throws XmlBuilderException;

    public XmlElement parseFragmentFromInputStream(InputStream is) throws XmlBuilderException {
        XmlPullParser pp = null;
        try {
            pp = this.factory.newPullParser();
            pp.setInput(is, null);
            try {
                pp.nextTag();
            }
            catch (IOException e) {
                throw new XmlBuilderException("IO error when starting to parse input stream", e);
            }
        }
        catch (XmlPullParserException e) {
            throw new XmlBuilderException("could not start parsing input stream", e);
        }
        return this.parseFragment(pp);
    }

    public XmlElement parseFragementFromInputStream(InputStream is, String encoding) throws XmlBuilderException {
        XmlPullParser pp = null;
        try {
            pp = this.factory.newPullParser();
            pp.setInput(is, encoding);
            try {
                pp.nextTag();
            }
            catch (IOException e) {
                throw new XmlBuilderException("IO error when starting to parse input stream (encoding=" + encoding + ")", e);
            }
        }
        catch (XmlPullParserException e) {
            throw new XmlBuilderException("could not start parsing input stream (encoding=" + encoding + ")", e);
        }
        return this.parseFragment(pp);
    }

    public XmlElement parseFragmentFromReader(Reader reader) throws XmlBuilderException {
        XmlPullParser pp = null;
        try {
            pp = this.factory.newPullParser();
            pp.setInput(reader);
            try {
                pp.nextTag();
            }
            catch (IOException e) {
                throw new XmlBuilderException("IO error when starting to parse from reader", e);
            }
        }
        catch (XmlPullParserException e) {
            throw new XmlBuilderException("could not start parsing input from reader", e);
        }
        return this.parseFragment(pp);
    }

    public void skipSubTree(XmlPullParser pp) throws XmlBuilderException {
        try {
            pp.require(2, null, null);
            int level = 1;
            while (level > 0) {
                int eventType = pp.next();
                if (eventType == 3) {
                    --level;
                    continue;
                }
                if (eventType != 2) continue;
                ++level;
            }
        }
        catch (XmlPullParserException e) {
            throw new XmlBuilderException("could not skip subtree", e);
        }
        catch (IOException e) {
            throw new XmlBuilderException("IO error when skipping subtree", e);
        }
    }

    public abstract void serializeStartTag(XmlElement var1, XmlSerializer var2) throws XmlBuilderException;

    public abstract void serializeEndTag(XmlElement var1, XmlSerializer var2) throws XmlBuilderException;

    public abstract void serialize(Object var1, XmlSerializer var2) throws XmlBuilderException;

    public abstract void serializeItem(Object var1, XmlSerializer var2) throws XmlBuilderException;

    public void serializeToOutputStream(Object item, OutputStream os) throws XmlBuilderException {
        this.serializeToOutputStream(item, os, "UTF8");
    }

    public void serializeToOutputStream(Object item, OutputStream os, String encoding) throws XmlBuilderException {
        XmlSerializer ser = null;
        try {
            ser = this.factory.newSerializer();
            ser.setOutput(os, encoding);
        }
        catch (Exception e) {
            throw new XmlBuilderException("could not serialize node to output stream (encoding=" + encoding + ")", e);
        }
        this.serialize(item, ser);
        try {
            ser.flush();
        }
        catch (IOException e) {
            throw new XmlBuilderException("could not flush output", e);
        }
    }

    public void serializeToWriter(Object item, Writer writer) throws XmlBuilderException {
        XmlSerializer ser = null;
        try {
            ser = this.factory.newSerializer();
            ser.setOutput(writer);
        }
        catch (Exception e) {
            throw new XmlBuilderException("could not serialize node to writer", e);
        }
        this.serialize(item, ser);
        try {
            ser.flush();
        }
        catch (IOException e) {
            throw new XmlBuilderException("could not flush output", e);
        }
    }

    public String serializeToString(Object item) throws XmlBuilderException {
        StringWriter sw = new StringWriter();
        this.serializeToWriter(item, sw);
        return sw.toString();
    }
}

