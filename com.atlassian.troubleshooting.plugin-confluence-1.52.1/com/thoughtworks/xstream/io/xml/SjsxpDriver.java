/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

public class SjsxpDriver
extends StaxDriver {
    public SjsxpDriver() {
    }

    public SjsxpDriver(QNameMap qnameMap, XmlFriendlyNameCoder nameCoder) {
        super(qnameMap, nameCoder);
    }

    public SjsxpDriver(QNameMap qnameMap) {
        super(qnameMap);
    }

    public SjsxpDriver(XmlFriendlyNameCoder nameCoder) {
        super(nameCoder);
    }

    protected XMLInputFactory createInputFactory() {
        ReflectiveOperationException exception = null;
        try {
            XMLInputFactory instance = (XMLInputFactory)Class.forName("com.sun.xml.internal.stream.XMLInputFactoryImpl").newInstance();
            instance.setProperty("javax.xml.stream.isSupportingExternalEntities", Boolean.FALSE);
            return instance;
        }
        catch (InstantiationException e) {
            exception = e;
        }
        catch (IllegalAccessException e) {
            exception = e;
        }
        catch (ClassNotFoundException e) {
            exception = e;
        }
        throw new StreamException("Cannot create SJSXP (Sun JDK 6 StAX) XMLInputFactory instance.", exception);
    }

    protected XMLOutputFactory createOutputFactory() {
        ReflectiveOperationException exception = null;
        try {
            return (XMLOutputFactory)Class.forName("com.sun.xml.internal.stream.XMLOutputFactoryImpl").newInstance();
        }
        catch (InstantiationException e) {
            exception = e;
        }
        catch (IllegalAccessException e) {
            exception = e;
        }
        catch (ClassNotFoundException e) {
            exception = e;
        }
        throw new StreamException("Cannot create SJSXP (Sun JDK 6 StAX) XMLOutputFactory instance.", exception);
    }
}

