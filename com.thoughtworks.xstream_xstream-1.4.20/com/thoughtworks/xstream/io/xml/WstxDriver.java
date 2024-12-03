/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ctc.wstx.stax.WstxInputFactory
 *  com.ctc.wstx.stax.WstxOutputFactory
 */
package com.thoughtworks.xstream.io.xml;

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

public class WstxDriver
extends StaxDriver {
    public WstxDriver() {
    }

    public WstxDriver(QNameMap qnameMap, XmlFriendlyNameCoder nameCoder) {
        super(qnameMap, nameCoder);
    }

    public WstxDriver(QNameMap qnameMap, NameCoder nameCoder) {
        super(qnameMap, nameCoder);
    }

    public WstxDriver(QNameMap qnameMap) {
        super(qnameMap);
    }

    public WstxDriver(XmlFriendlyNameCoder nameCoder) {
        super(nameCoder);
    }

    public WstxDriver(NameCoder nameCoder) {
        super(nameCoder);
    }

    protected XMLInputFactory createInputFactory() {
        WstxInputFactory instance = new WstxInputFactory();
        instance.setProperty("javax.xml.stream.isSupportingExternalEntities", Boolean.FALSE);
        return instance;
    }

    protected XMLOutputFactory createOutputFactory() {
        return new WstxOutputFactory();
    }
}

