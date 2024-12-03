/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.DetailEntry
 *  javax.xml.soap.Name
 */
package com.sun.xml.messaging.saaj.soap.ver1_1;

import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.impl.DetailImpl;
import com.sun.xml.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.messaging.saaj.soap.ver1_1.DetailEntry1_1Impl;
import javax.xml.namespace.QName;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.Name;
import org.w3c.dom.Element;

public class Detail1_1Impl
extends DetailImpl {
    public Detail1_1Impl(SOAPDocumentImpl ownerDoc, String prefix) {
        super(ownerDoc, NameImpl.createDetail1_1Name(prefix));
    }

    public Detail1_1Impl(SOAPDocumentImpl ownerDoc) {
        super(ownerDoc, NameImpl.createDetail1_1Name());
    }

    public Detail1_1Impl(SOAPDocumentImpl ownerDoc, Element domElement) {
        super(ownerDoc, domElement);
    }

    @Override
    protected DetailEntry createDetailEntry(Name name) {
        return new DetailEntry1_1Impl((SOAPDocumentImpl)this.getOwnerDocument(), name);
    }

    @Override
    protected DetailEntry createDetailEntry(QName name) {
        return new DetailEntry1_1Impl((SOAPDocumentImpl)this.getOwnerDocument(), name);
    }
}

