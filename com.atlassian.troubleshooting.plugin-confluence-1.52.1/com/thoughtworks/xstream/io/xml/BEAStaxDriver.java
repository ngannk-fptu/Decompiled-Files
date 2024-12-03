/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bea.xml.stream.MXParserFactory
 *  com.bea.xml.stream.XMLOutputFactoryBase
 */
package com.thoughtworks.xstream.io.xml;

import com.bea.xml.stream.MXParserFactory;
import com.bea.xml.stream.XMLOutputFactoryBase;
import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

public class BEAStaxDriver
extends StaxDriver {
    public BEAStaxDriver() {
    }

    public BEAStaxDriver(QNameMap qnameMap, XmlFriendlyNameCoder nameCoder) {
        super(qnameMap, nameCoder);
    }

    public BEAStaxDriver(QNameMap qnameMap, NameCoder nameCoder) {
        super(qnameMap, nameCoder);
    }

    public BEAStaxDriver(QNameMap qnameMap) {
        super(qnameMap);
    }

    public BEAStaxDriver(XmlFriendlyNameCoder nameCoder) {
        super(nameCoder);
    }

    public BEAStaxDriver(NameCoder nameCoder) {
        super(nameCoder);
    }

    protected XMLInputFactory createInputFactory() {
        MXParserFactory instance = new MXParserFactory();
        instance.setProperty("javax.xml.stream.isSupportingExternalEntities", Boolean.FALSE);
        return instance;
    }

    protected XMLOutputFactory createOutputFactory() {
        return new XMLOutputFactoryBase();
    }
}

