/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.soap.FactoryFinder;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SAAJMetaFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

public abstract class MessageFactory {
    private static final String DEFAULT_MESSAGE_FACTORY = "com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl";

    public static MessageFactory newInstance() throws SOAPException {
        try {
            MessageFactory factory = FactoryFinder.find(MessageFactory.class, DEFAULT_MESSAGE_FACTORY, false);
            if (factory != null) {
                return factory;
            }
            return MessageFactory.newInstance("SOAP 1.1 Protocol");
        }
        catch (Exception ex) {
            throw new SOAPException("Unable to create message factory for SOAP: " + ex.getMessage());
        }
    }

    public static MessageFactory newInstance(String protocol) throws SOAPException {
        return SAAJMetaFactory.getInstance().newMessageFactory(protocol);
    }

    public abstract SOAPMessage createMessage() throws SOAPException;

    public abstract SOAPMessage createMessage(MimeHeaders var1, InputStream var2) throws IOException, SOAPException;
}

