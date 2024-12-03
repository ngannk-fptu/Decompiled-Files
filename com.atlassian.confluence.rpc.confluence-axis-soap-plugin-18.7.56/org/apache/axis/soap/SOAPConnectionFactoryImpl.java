/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.soap;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import org.apache.axis.soap.SOAPConnectionImpl;

public class SOAPConnectionFactoryImpl
extends SOAPConnectionFactory {
    public SOAPConnection createConnection() throws SOAPException {
        return new SOAPConnectionImpl();
    }
}

