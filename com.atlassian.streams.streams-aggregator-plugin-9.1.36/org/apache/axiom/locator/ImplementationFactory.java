/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.locator;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.axiom.locator.Feature;
import org.apache.axiom.locator.Implementation;
import org.apache.axiom.locator.Loader;
import org.apache.axiom.locator.loader.OMMetaFactoryLoader;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

final class ImplementationFactory {
    static final String DESCRIPTOR_RESOURCE = "META-INF/axiom.xml";
    private static final String NS = "http://ws.apache.org/axiom/";
    private static final QName QNAME_IMPLEMENTATIONS = new QName("http://ws.apache.org/axiom/", "implementations");
    private static final QName QNAME_IMPLEMENTATION = new QName("http://ws.apache.org/axiom/", "implementation");
    private static final QName QNAME_FEATURE = new QName("http://ws.apache.org/axiom/", "feature");
    private static final Log log = LogFactory.getLog(ImplementationFactory.class);

    private ImplementationFactory() {
    }

    static Implementation createDefaultImplementation(Loader loader, String className) {
        OMMetaFactory metaFactory;
        if (log.isDebugEnabled()) {
            log.debug((Object)("Creating default implementation for class " + className));
        }
        return (metaFactory = (OMMetaFactory)ImplementationFactory.load(loader, className)) == null ? null : new Implementation(null, metaFactory, new Feature[]{new Feature("default", Integer.MAX_VALUE)});
    }

    private static Object load(Loader loader, String className) {
        Class clazz;
        try {
            clazz = loader.load(className);
        }
        catch (ClassNotFoundException ex) {
            log.error((Object)("The class " + className + " could not be loaded"), (Throwable)ex);
            return null;
        }
        try {
            return clazz.newInstance();
        }
        catch (Exception ex) {
            log.error((Object)("The class " + className + " could not be instantiated"), (Throwable)ex);
            return null;
        }
    }

    static List parseDescriptor(Loader loader, URL url) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Loading " + url));
        }
        ArrayList<Implementation> implementations = new ArrayList<Implementation>();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Element root = dbf.newDocumentBuilder().parse(url.toString()).getDocumentElement();
            QName rootQName = ImplementationFactory.getQName(root);
            if (rootQName.equals(QNAME_IMPLEMENTATIONS)) {
                for (Node child = root.getFirstChild(); child != null; child = child.getNextSibling()) {
                    if (!(child instanceof Element)) continue;
                    QName childQName = ImplementationFactory.getQName(child);
                    if (childQName.equals(QNAME_IMPLEMENTATION)) {
                        Implementation implementation = ImplementationFactory.parseImplementation(loader, (Element)child);
                        if (implementation == null) continue;
                        implementations.add(implementation);
                        continue;
                    }
                    log.warn((Object)("Skipping unexpected element " + childQName + "; only " + QNAME_IMPLEMENTATION + " is expected"));
                }
            } else {
                log.error((Object)(url + " is not a valid implementation descriptor: unexpected root element " + rootQName + "; expected " + QNAME_IMPLEMENTATIONS));
            }
        }
        catch (ParserConfigurationException ex) {
            throw new Error(ex);
        }
        catch (IOException ex) {
            log.error((Object)("Unable to read " + url), (Throwable)ex);
        }
        catch (SAXException ex) {
            log.error((Object)("Parser error while reading " + url), (Throwable)ex);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Discovered implementations: " + implementations));
        }
        return implementations;
    }

    private static Implementation parseImplementation(Loader loader, Element implementation) {
        String name = implementation.getAttributeNS(null, "name");
        if (name.length() == 0) {
            log.error((Object)("Encountered " + QNAME_IMPLEMENTATION + " element without name attribute"));
            return null;
        }
        String loaderClassName = implementation.getAttributeNS(null, "loader");
        if (loaderClassName.length() == 0) {
            log.error((Object)("Encountered " + QNAME_IMPLEMENTATION + " element without loader attribute"));
            return null;
        }
        OMMetaFactory metaFactory = ((OMMetaFactoryLoader)ImplementationFactory.load(loader, loaderClassName)).load(null);
        if (metaFactory == null) {
            return null;
        }
        ArrayList<Feature> features = new ArrayList<Feature>();
        for (Node child = implementation.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (!(child instanceof Element)) continue;
            QName childQName = ImplementationFactory.getQName(child);
            if (childQName.equals(QNAME_FEATURE)) {
                Feature feature = ImplementationFactory.parseFeature((Element)child);
                if (feature == null) continue;
                features.add(feature);
                continue;
            }
            log.warn((Object)("Skipping unexpected element " + childQName + "; only " + QNAME_FEATURE + " is expected"));
        }
        return new Implementation(name, metaFactory, features.toArray(new Feature[features.size()]));
    }

    private static Feature parseFeature(Element feature) {
        String name = feature.getAttributeNS(null, "name");
        if (name.length() == 0) {
            log.error((Object)("Encountered " + QNAME_FEATURE + " element without name attribute"));
            return null;
        }
        String priority = feature.getAttributeNS(null, "priority");
        if (priority.length() == 0) {
            log.error((Object)("Encountered " + QNAME_FEATURE + " element without priority attribute"));
            return null;
        }
        try {
            return new Feature(name, Integer.parseInt(priority));
        }
        catch (NumberFormatException ex) {
            log.error((Object)("Invalid priority value '" + priority + "'; must be an integer"));
            return null;
        }
    }

    private static QName getQName(Node node) {
        String namespaceURI = node.getNamespaceURI();
        return namespaceURI == null ? new QName(node.getLocalName()) : new QName(namespaceURI, node.getLocalName());
    }
}

