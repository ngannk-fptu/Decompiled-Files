/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.SOAPException
 */
package com.sun.xml.messaging.saaj.soap.ver1_1;

import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.impl.EnvelopeImpl;
import com.sun.xml.messaging.saaj.soap.name.NameImpl;
import javax.xml.soap.SOAPException;
import org.w3c.dom.Element;

public class Envelope1_1Impl
extends EnvelopeImpl {
    public Envelope1_1Impl(SOAPDocumentImpl ownerDoc, String prefix) {
        super(ownerDoc, NameImpl.createEnvelope1_1Name(prefix));
    }

    Envelope1_1Impl(SOAPDocumentImpl ownerDoc, String prefix, boolean createHeader, boolean createBody) throws SOAPException {
        super(ownerDoc, NameImpl.createEnvelope1_1Name(prefix), createHeader, createBody);
    }

    public Envelope1_1Impl(SOAPDocumentImpl ownerDoc, Element domElement) {
        super(ownerDoc, domElement);
    }

    @Override
    protected NameImpl getBodyName(String prefix) {
        return NameImpl.createBody1_1Name(prefix);
    }

    @Override
    protected NameImpl getHeaderName(String prefix) {
        return NameImpl.createHeader1_1Name(prefix);
    }
}

