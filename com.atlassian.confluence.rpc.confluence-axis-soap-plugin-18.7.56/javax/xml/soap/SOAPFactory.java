/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

import javax.xml.soap.Detail;
import javax.xml.soap.FactoryFinder;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public abstract class SOAPFactory {
    private static final String SF_PROPERTY = "javax.xml.soap.SOAPFactory";
    private static final String DEFAULT_SF = "org.apache.axis.soap.SOAPFactoryImpl";

    public abstract SOAPElement createElement(Name var1) throws SOAPException;

    public abstract SOAPElement createElement(String var1) throws SOAPException;

    public abstract SOAPElement createElement(String var1, String var2, String var3) throws SOAPException;

    public abstract Detail createDetail() throws SOAPException;

    public abstract Name createName(String var1, String var2, String var3) throws SOAPException;

    public abstract Name createName(String var1) throws SOAPException;

    public static SOAPFactory newInstance() throws SOAPException {
        try {
            return (SOAPFactory)FactoryFinder.find(SF_PROPERTY, DEFAULT_SF);
        }
        catch (Exception exception) {
            throw new SOAPException("Unable to create SOAP Factory: " + exception.getMessage());
        }
    }
}

