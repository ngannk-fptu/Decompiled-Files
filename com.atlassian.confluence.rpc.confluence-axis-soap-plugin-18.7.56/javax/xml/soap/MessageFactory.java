/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.soap.FactoryFinder;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

public abstract class MessageFactory {
    private static final String DEFAULT_MESSAGE_FACTORY = "org.apache.axis.soap.MessageFactoryImpl";
    private static final String MESSAGE_FACTORY_PROPERTY = "javax.xml.soap.MessageFactory";

    public static MessageFactory newInstance() throws SOAPException {
        try {
            return (MessageFactory)FactoryFinder.find(MESSAGE_FACTORY_PROPERTY, DEFAULT_MESSAGE_FACTORY);
        }
        catch (Exception exception) {
            throw new SOAPException("Unable to create message factory for SOAP: " + exception.getMessage());
        }
    }

    public abstract SOAPMessage createMessage() throws SOAPException;

    public abstract SOAPMessage createMessage(MimeHeaders var1, InputStream var2) throws IOException, SOAPException;
}

