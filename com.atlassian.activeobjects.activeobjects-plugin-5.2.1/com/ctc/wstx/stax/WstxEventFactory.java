/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.stax;

import com.ctc.wstx.compat.QNameCreator;
import com.ctc.wstx.evt.SimpleStartElement;
import com.ctc.wstx.evt.WDTD;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.StartElement;
import org.codehaus.stax2.ri.Stax2EventFactoryImpl;

public final class WstxEventFactory
extends Stax2EventFactoryImpl {
    public DTD createDTD(String dtd) {
        return new WDTD(this.mLocation, dtd);
    }

    protected QName createQName(String nsURI, String localName) {
        return new QName(nsURI, localName);
    }

    protected QName createQName(String nsURI, String localName, String prefix) {
        return QNameCreator.create(nsURI, localName, prefix);
    }

    protected StartElement createStartElement(QName name, Iterator attr, Iterator ns, NamespaceContext ctxt) {
        return SimpleStartElement.construct(this.mLocation, name, attr, ns, ctxt);
    }
}

