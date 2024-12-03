/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.util;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

public class XMLReaderAdapter
implements Locator,
XMLReader {
    private static final Object[] NULLPARAMS = new Object[0];
    private static final String EMPTYSTRING = null;
    private AttributesImpl atts = new AttributesImpl();
    private ContentHandler contentHandler = new DefaultHandler();
    private Object object;
    private String root = null;
    private String systemId;

    public XMLReaderAdapter(Object object) {
        this(object, null);
    }

    public XMLReaderAdapter(Object object, String root) {
        this.object = object;
        this.root = root;
    }

    @Override
    public int getColumnNumber() {
        return -1;
    }

    @Override
    public void setContentHandler(ContentHandler handler) {
        this.contentHandler = handler;
    }

    @Override
    public ContentHandler getContentHandler() {
        return this.contentHandler;
    }

    @Override
    public void setDTDHandler(DTDHandler handler) {
    }

    @Override
    public DTDHandler getDTDHandler() {
        return null;
    }

    @Override
    public void setEntityResolver(EntityResolver resolver) {
    }

    @Override
    public EntityResolver getEntityResolver() {
        return null;
    }

    @Override
    public void setErrorHandler(ErrorHandler handler) {
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return null;
    }

    @Override
    public void setFeature(String featureId, boolean on) throws SAXNotRecognizedException, SAXNotSupportedException {
    }

    @Override
    public boolean getFeature(String featureId) throws SAXNotRecognizedException {
        return false;
    }

    @Override
    public int getLineNumber() {
        return -1;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public void setProperty(String propertyId, Object property) throws SAXNotRecognizedException, SAXNotSupportedException {
    }

    @Override
    public Object getProperty(String name) throws SAXNotRecognizedException {
        throw new SAXNotRecognizedException(name);
    }

    @Override
    public String getPublicId() {
        return null;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    @Override
    public String getSystemId() {
        return this.systemId;
    }

    @Override
    public void parse(InputSource source) throws IOException, SAXException {
        this.parse();
    }

    @Override
    public void parse(String s) throws IOException, SAXException {
        this.parse();
    }

    public void parse() throws IOException, SAXException {
        if (this.object == null) {
            throw new SAXException("no object to reflected defined");
        }
        if (this.contentHandler == null) {
            throw new SAXException("no content handler defined");
        }
        this.contentHandler.setDocumentLocator(this);
        this.contentHandler.startDocument();
        this.walk(this.object, this.root == null ? "data" : this.root, false);
        this.contentHandler.endDocument();
    }

    private String getTagName(String tagName) {
        boolean lastWasLower = false;
        int bufIndex = 0;
        int oIndex = tagName.length();
        StringBuffer buff = new StringBuffer(tagName);
        for (int i = 0; i < oIndex; ++i) {
            char c = tagName.charAt(i);
            if (i > 0) {
                if (lastWasLower && Character.isUpperCase(c)) {
                    buff.setCharAt(bufIndex, Character.toLowerCase(c));
                    buff.insert(bufIndex, '-');
                    ++bufIndex;
                }
            } else {
                buff.setCharAt(0, Character.toLowerCase(c));
                lastWasLower = true;
            }
            ++bufIndex;
        }
        return buff.toString();
    }

    private void walk(Object object, String name, boolean useKey) throws SAXException {
        if (!useKey) {
            this.contentHandler.startElement(EMPTYSTRING, name, EMPTYSTRING, this.atts);
        } else {
            AttributesImpl atts = new AttributesImpl();
            atts.addAttribute(EMPTYSTRING, EMPTYSTRING, "key", "key", name);
            this.contentHandler.startElement(EMPTYSTRING, "item", EMPTYSTRING, atts);
        }
        try {
            if (object != null) {
                Class<String> c = object.getClass();
                if (c.isAssignableFrom(String.class)) {
                    String value = (String)object;
                    this.contentHandler.characters(value.toCharArray(), 0, value.length());
                } else {
                    PropertyDescriptor[] props = Introspector.getBeanInfo(c, Object.class).getPropertyDescriptors();
                    boolean accessorsFound = false;
                    for (int i = 0; i < props.length; ++i) {
                        Object value;
                        Method m = props[i].getReadMethod();
                        String s = m.getName();
                        if ((!s.startsWith("get") || s.length() <= 3) && (!s.startsWith("is") || s.length() <= 2)) continue;
                        String attributeName = this.getTagName(s.substring(s.startsWith("is") ? 2 : 3));
                        Class<?> rt = m.getReturnType();
                        if (attributeName.equals("class") || m.getParameterTypes().length != 0 || (value = m.invoke(object, NULLPARAMS)) == null) continue;
                        accessorsFound = true;
                        if (Collection.class.isAssignableFrom(value.getClass())) {
                            Collection cole = (Collection)value;
                            this.contentHandler.startElement(EMPTYSTRING, attributeName, EMPTYSTRING, this.atts);
                            for (Object obj : cole) {
                                this.walk(obj, "item", false);
                            }
                            this.contentHandler.endElement(EMPTYSTRING, attributeName, EMPTYSTRING);
                            continue;
                        }
                        if (Enumeration.class.isAssignableFrom(value.getClass())) {
                            Enumeration e = (Enumeration)value;
                            this.contentHandler.startElement(EMPTYSTRING, attributeName, EMPTYSTRING, this.atts);
                            while (e.hasMoreElements()) {
                                Object obj = e.nextElement();
                                this.walk(obj, "item", false);
                            }
                            this.contentHandler.endElement(EMPTYSTRING, attributeName, EMPTYSTRING);
                            continue;
                        }
                        if (Vector.class.isAssignableFrom(value.getClass())) {
                            Vector vec = (Vector)value;
                            this.contentHandler.startElement(EMPTYSTRING, attributeName, EMPTYSTRING, this.atts);
                            for (int ix = 0; ix < vec.size(); ++ix) {
                                Object obj;
                                obj = vec.elementAt(ix);
                                this.walk(obj, "item", false);
                            }
                            this.contentHandler.endElement(EMPTYSTRING, attributeName, EMPTYSTRING);
                            continue;
                        }
                        if (Map.class.isAssignableFrom(value.getClass())) {
                            this.contentHandler.startElement(EMPTYSTRING, attributeName, EMPTYSTRING, this.atts);
                            Map map = (Map)value;
                            for (Map.Entry entry : map.entrySet()) {
                                this.walk(entry.getValue(), (String)entry.getKey(), true);
                            }
                            this.contentHandler.endElement(EMPTYSTRING, attributeName, EMPTYSTRING);
                            continue;
                        }
                        this.walk(value, attributeName, false);
                    }
                    if (!accessorsFound) {
                        this.contentHandler.characters(object.toString().toCharArray(), 0, object.toString().length());
                    }
                }
            }
        }
        catch (Exception e) {
            throw new SAXException(e);
        }
        if (!useKey) {
            this.contentHandler.endElement(EMPTYSTRING, name, EMPTYSTRING);
        } else {
            this.contentHandler.endElement(EMPTYSTRING, "item", EMPTYSTRING);
        }
    }
}

