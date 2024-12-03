/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 */
package com.sun.xml.messaging.saaj.soap.dynamic;

import com.sun.xml.messaging.saaj.soap.MessageFactoryImpl;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

public class SOAPMessageFactoryDynamicImpl
extends MessageFactoryImpl {
    @Override
    public SOAPMessage createMessage() throws SOAPException {
        throw new UnsupportedOperationException("createMessage() not supported for Dynamic Protocol");
    }
}

