/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

import javax.xml.soap.FactoryFinder;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPException;

public abstract class SOAPConnectionFactory {
    private static final String DEFAULT_SOAP_CONNECTION_FACTORY = "com.sun.xml.internal.messaging.saaj.client.p2p.HttpSOAPConnectionFactory";

    public static SOAPConnectionFactory newInstance() throws SOAPException, UnsupportedOperationException {
        try {
            return FactoryFinder.find(SOAPConnectionFactory.class, DEFAULT_SOAP_CONNECTION_FACTORY, true);
        }
        catch (Exception ex) {
            throw new SOAPException("Unable to create SOAP connection factory: " + ex.getMessage());
        }
    }

    public abstract SOAPConnection createConnection() throws SOAPException;
}

