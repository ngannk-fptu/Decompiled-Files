/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.MessageFactory
 *  javax.xml.soap.SAAJMetaFactory
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPFactory
 */
package com.sun.xml.messaging.saaj.soap;

import com.sun.xml.messaging.saaj.soap.dynamic.SOAPFactoryDynamicImpl;
import com.sun.xml.messaging.saaj.soap.dynamic.SOAPMessageFactoryDynamicImpl;
import com.sun.xml.messaging.saaj.soap.ver1_1.SOAPFactory1_1Impl;
import com.sun.xml.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl;
import com.sun.xml.messaging.saaj.soap.ver1_2.SOAPFactory1_2Impl;
import com.sun.xml.messaging.saaj.soap.ver1_2.SOAPMessageFactory1_2Impl;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SAAJMetaFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

public class SAAJMetaFactoryImpl
extends SAAJMetaFactory {
    protected static final Logger log = Logger.getLogger("com.sun.xml.messaging.saaj.soap", "com.sun.xml.messaging.saaj.soap.LocalStrings");

    protected MessageFactory newMessageFactory(String protocol) throws SOAPException {
        if ("SOAP 1.1 Protocol".equals(protocol)) {
            return new SOAPMessageFactory1_1Impl();
        }
        if ("SOAP 1.2 Protocol".equals(protocol)) {
            return new SOAPMessageFactory1_2Impl();
        }
        if ("Dynamic Protocol".equals(protocol)) {
            return new SOAPMessageFactoryDynamicImpl();
        }
        log.log(Level.SEVERE, "SAAJ0569.soap.unknown.protocol", new Object[]{protocol, "MessageFactory"});
        throw new SOAPException("Unknown Protocol: " + protocol + "  specified for creating MessageFactory");
    }

    protected SOAPFactory newSOAPFactory(String protocol) throws SOAPException {
        if ("SOAP 1.1 Protocol".equals(protocol)) {
            return new SOAPFactory1_1Impl();
        }
        if ("SOAP 1.2 Protocol".equals(protocol)) {
            return new SOAPFactory1_2Impl();
        }
        if ("Dynamic Protocol".equals(protocol)) {
            return new SOAPFactoryDynamicImpl();
        }
        log.log(Level.SEVERE, "SAAJ0569.soap.unknown.protocol", new Object[]{protocol, "SOAPFactory"});
        throw new SOAPException("Unknown Protocol: " + protocol + "  specified for creating SOAPFactory");
    }
}

