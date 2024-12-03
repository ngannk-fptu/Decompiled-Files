/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.FactoryFinder;
import javax.xml.soap.Name;
import javax.xml.soap.SAAJMetaFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import org.w3c.dom.Element;

public abstract class SOAPFactory {
    private static final String DEFAULT_SOAP_FACTORY = "com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPFactory1_1Impl";

    public SOAPElement createElement(Element domElement) throws SOAPException {
        throw new UnsupportedOperationException("createElement(org.w3c.dom.Element) must be overridden by all subclasses of SOAPFactory.");
    }

    public abstract SOAPElement createElement(Name var1) throws SOAPException;

    public SOAPElement createElement(QName qname) throws SOAPException {
        throw new UnsupportedOperationException("createElement(QName) must be overridden by all subclasses of SOAPFactory.");
    }

    public abstract SOAPElement createElement(String var1) throws SOAPException;

    public abstract SOAPElement createElement(String var1, String var2, String var3) throws SOAPException;

    public abstract Detail createDetail() throws SOAPException;

    public abstract SOAPFault createFault(String var1, QName var2) throws SOAPException;

    public abstract SOAPFault createFault() throws SOAPException;

    public abstract Name createName(String var1, String var2, String var3) throws SOAPException;

    public abstract Name createName(String var1) throws SOAPException;

    public static SOAPFactory newInstance() throws SOAPException {
        try {
            SOAPFactory factory = FactoryFinder.find(SOAPFactory.class, DEFAULT_SOAP_FACTORY, false);
            if (factory != null) {
                return factory;
            }
            return SOAPFactory.newInstance("SOAP 1.1 Protocol");
        }
        catch (Exception ex) {
            throw new SOAPException("Unable to create SOAP Factory: " + ex.getMessage());
        }
    }

    public static SOAPFactory newInstance(String protocol) throws SOAPException {
        return SAAJMetaFactory.getInstance().newSOAPFactory(protocol);
    }
}

