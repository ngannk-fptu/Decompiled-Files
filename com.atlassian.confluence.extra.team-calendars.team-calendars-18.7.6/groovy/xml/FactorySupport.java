/*
 * Decompiled with CFR 0.152.
 */
package groovy.xml;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

public class FactorySupport {
    static Object createFactory(PrivilegedExceptionAction action) throws ParserConfigurationException {
        Object factory;
        try {
            factory = AccessController.doPrivileged(action);
        }
        catch (PrivilegedActionException pae) {
            Exception e = pae.getException();
            if (e instanceof ParserConfigurationException) {
                throw (ParserConfigurationException)e;
            }
            throw new RuntimeException(e);
        }
        return factory;
    }

    public static DocumentBuilderFactory createDocumentBuilderFactory() throws ParserConfigurationException {
        return (DocumentBuilderFactory)FactorySupport.createFactory(new PrivilegedExceptionAction(){

            public Object run() throws ParserConfigurationException {
                return DocumentBuilderFactory.newInstance();
            }
        });
    }

    public static SAXParserFactory createSaxParserFactory() throws ParserConfigurationException {
        return (SAXParserFactory)FactorySupport.createFactory(new PrivilegedExceptionAction(){

            public Object run() throws ParserConfigurationException {
                return SAXParserFactory.newInstance();
            }
        });
    }
}

