/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aQute.bnd.annotation.Resolution
 *  aQute.bnd.annotation.spi.ServiceProvider
 *  org.codehaus.stax2.ri.Stax2EventFactoryImpl
 */
package com.ctc.wstx.stax;

import aQute.bnd.annotation.Resolution;
import aQute.bnd.annotation.spi.ServiceProvider;
import com.ctc.wstx.compat.QNameCreator;
import com.ctc.wstx.evt.SimpleStartElement;
import com.ctc.wstx.evt.WDTD;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.StartElement;
import org.codehaus.stax2.ri.Stax2EventFactoryImpl;

@ServiceProvider(value=XMLEventFactory.class, resolution=Resolution.OPTIONAL)
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

    protected StartElement createStartElement(QName name, Iterator<?> attr, Iterator<?> ns, NamespaceContext ctxt) {
        return SimpleStartElement.construct(this.mLocation, name, attr, ns, ctxt);
    }
}

