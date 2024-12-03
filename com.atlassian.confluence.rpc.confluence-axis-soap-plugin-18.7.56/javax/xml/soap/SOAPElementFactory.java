/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

public class SOAPElementFactory {
    private SOAPFactory sf;

    private SOAPElementFactory(SOAPFactory soapfactory) {
        this.sf = soapfactory;
    }

    public SOAPElement create(Name name) throws SOAPException {
        return this.sf.createElement(name);
    }

    public SOAPElement create(String localName) throws SOAPException {
        return this.sf.createElement(localName);
    }

    public SOAPElement create(String localName, String prefix, String uri) throws SOAPException {
        return this.sf.createElement(localName, prefix, uri);
    }

    public static SOAPElementFactory newInstance() throws SOAPException {
        try {
            return new SOAPElementFactory(SOAPFactory.newInstance());
        }
        catch (Exception exception) {
            throw new SOAPException("Unable to create SOAP Element Factory: " + exception.getMessage());
        }
    }
}

