/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

import javax.xml.soap.FactoryFinder;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

public abstract class SAAJMetaFactory {
    private static final String META_FACTORY_DEPRECATED_CLASS_PROPERTY = "javax.xml.soap.MetaFactory";
    private static final String DEFAULT_META_FACTORY_CLASS = "com.sun.xml.internal.messaging.saaj.soap.SAAJMetaFactoryImpl";

    static SAAJMetaFactory getInstance() throws SOAPException {
        try {
            return FactoryFinder.find(SAAJMetaFactory.class, DEFAULT_META_FACTORY_CLASS, true, META_FACTORY_DEPRECATED_CLASS_PROPERTY);
        }
        catch (Exception e) {
            throw new SOAPException("Unable to create SAAJ meta-factory: " + e.getMessage());
        }
    }

    protected SAAJMetaFactory() {
    }

    protected abstract MessageFactory newMessageFactory(String var1) throws SOAPException;

    protected abstract SOAPFactory newSOAPFactory(String var1) throws SOAPException;
}

