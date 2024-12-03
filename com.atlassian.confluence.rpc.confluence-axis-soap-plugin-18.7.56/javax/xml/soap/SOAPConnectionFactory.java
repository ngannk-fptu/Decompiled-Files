/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

import javax.xml.soap.FactoryFinder;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPException;

public abstract class SOAPConnectionFactory {
    private static final String DEFAULT_SOAP_CONNECTION_FACTORY = "org.apache.axis.soap.SOAPConnectionFactoryImpl";
    private static final String SF_PROPERTY = "javax.xml.soap.SOAPConnectionFactory";

    public static SOAPConnectionFactory newInstance() throws SOAPException, UnsupportedOperationException {
        try {
            return (SOAPConnectionFactory)FactoryFinder.find(SF_PROPERTY, DEFAULT_SOAP_CONNECTION_FACTORY);
        }
        catch (Exception exception) {
            throw new SOAPException("Unable to create SOAP connection factory: " + exception.getMessage());
        }
    }

    public abstract SOAPConnection createConnection() throws SOAPException;
}

