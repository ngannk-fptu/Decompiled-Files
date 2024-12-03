/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.util.ArrayList;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.algorithms.SignatureAlgorithm;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.keyresolver.KeyResolver;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.utils.ClassLoaderUtils;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.utils.I18n;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Init {
    public static final String CONF_NS = "http://www.xmlsecurity.org/NS/#configuration";
    private static final Logger LOG = LoggerFactory.getLogger(Init.class);
    private static boolean alreadyInitialized = false;

    public static final synchronized boolean isInitialized() {
        return alreadyInitialized;
    }

    public static synchronized void init() {
        if (alreadyInitialized) {
            return;
        }
        InputStream is = AccessController.doPrivileged(() -> {
            String cfile = System.getProperty("org.apache.xml.security.resource.config");
            if (cfile == null) {
                return null;
            }
            return ClassLoaderUtils.getResourceAsStream(cfile, Init.class);
        });
        if (is == null) {
            Init.dynamicInit();
        } else {
            Init.fileInit(is);
            try {
                is.close();
            }
            catch (IOException ex) {
                LOG.warn(ex.getMessage());
            }
        }
        alreadyInitialized = true;
    }

    private static void dynamicInit() {
        I18n.init("en", "US");
        LOG.debug("Registering default algorithms");
        try {
            ElementProxy.registerDefaultPrefixes();
        }
        catch (XMLSecurityException ex) {
            LOG.error(ex.getMessage(), (Throwable)ex);
        }
        Transform.registerDefaultAlgorithms();
        SignatureAlgorithm.registerDefaultAlgorithms();
        JCEMapper.registerDefaultAlgorithms();
        Canonicalizer.registerDefaultAlgorithms();
        ResourceResolver.registerDefaultResolvers();
        KeyResolver.registerDefaultResolvers();
    }

    private static void fileInit(InputStream is) {
        try {
            Node config;
            Document doc = XMLUtils.read(is, true);
            for (config = doc.getFirstChild(); config != null && !"Configuration".equals(config.getLocalName()); config = config.getNextSibling()) {
            }
            if (config == null) {
                LOG.error("Error in reading configuration file - Configuration element not found");
                return;
            }
            for (Node el = config.getFirstChild(); el != null; el = el.getNextSibling()) {
                Element[] nl;
                String description;
                ArrayList<String> classNames;
                Element[] resolverElem;
                Element[] algorithms;
                Node algorithmsNode;
                Object[] exArgs;
                String javaClass;
                String uri;
                if (1 != el.getNodeType()) continue;
                String tag = el.getLocalName();
                if ("ResourceBundles".equals(tag)) {
                    Element resource = (Element)el;
                    Element[] langAttr = resource.getAttributeNodeNS(null, "defaultLanguageCode");
                    Attr countryAttr = resource.getAttributeNodeNS(null, "defaultCountryCode");
                    String languageCode = langAttr == null ? null : langAttr.getNodeValue();
                    String countryCode = countryAttr == null ? null : countryAttr.getNodeValue();
                    I18n.init(languageCode, countryCode);
                }
                if ("CanonicalizationMethods".equals(tag)) {
                    Element[] list;
                    for (Element element : list = XMLUtils.selectNodes(el.getFirstChild(), CONF_NS, "CanonicalizationMethod")) {
                        uri = element.getAttributeNS(null, "URI");
                        javaClass = element.getAttributeNS(null, "JAVACLASS");
                        try {
                            Canonicalizer.register(uri, javaClass);
                            LOG.debug("Canonicalizer.register({}, {})", (Object)uri, (Object)javaClass);
                        }
                        catch (ClassNotFoundException e) {
                            exArgs = new Object[]{uri, javaClass};
                            LOG.error(I18n.translate("algorithm.classDoesNotExist", exArgs));
                        }
                    }
                }
                if ("TransformAlgorithms".equals(tag)) {
                    Element[] tranElem;
                    for (Element element : tranElem = XMLUtils.selectNodes(el.getFirstChild(), CONF_NS, "TransformAlgorithm")) {
                        uri = element.getAttributeNS(null, "URI");
                        javaClass = element.getAttributeNS(null, "JAVACLASS");
                        try {
                            Transform.register(uri, javaClass);
                            LOG.debug("Transform.register({}, {})", (Object)uri, (Object)javaClass);
                        }
                        catch (ClassNotFoundException e) {
                            exArgs = new Object[]{uri, javaClass};
                            LOG.error(I18n.translate("algorithm.classDoesNotExist", exArgs));
                        }
                        catch (NoClassDefFoundError ex) {
                            LOG.warn("Not able to found dependencies for algorithm, I'll keep working.");
                        }
                    }
                }
                if ("JCEAlgorithmMappings".equals(tag) && (algorithmsNode = ((Element)el).getElementsByTagName("Algorithms").item(0)) != null) {
                    for (Element element : algorithms = XMLUtils.selectNodes(algorithmsNode.getFirstChild(), CONF_NS, "Algorithm")) {
                        String id = element.getAttributeNS(null, "URI");
                        JCEMapper.register(id, new JCEMapper.Algorithm(element));
                    }
                }
                if ("SignatureAlgorithms".equals(tag)) {
                    Element[] sigElems;
                    algorithms = sigElems = XMLUtils.selectNodes(el.getFirstChild(), CONF_NS, "SignatureAlgorithm");
                    int n = algorithms.length;
                    for (int i = 0; i < n; ++i) {
                        Element sigElem = algorithms[i];
                        uri = sigElem.getAttributeNS(null, "URI");
                        javaClass = sigElem.getAttributeNS(null, "JAVACLASS");
                        try {
                            SignatureAlgorithm.register(uri, javaClass);
                            LOG.debug("SignatureAlgorithm.register({}, {})", (Object)uri, (Object)javaClass);
                            continue;
                        }
                        catch (ClassNotFoundException e) {
                            exArgs = new Object[]{uri, javaClass};
                            LOG.error(I18n.translate("algorithm.classDoesNotExist", exArgs));
                        }
                    }
                }
                if ("ResourceResolvers".equals(tag)) {
                    resolverElem = XMLUtils.selectNodes(el.getFirstChild(), CONF_NS, "Resolver");
                    classNames = new ArrayList<String>(resolverElem.length);
                    for (Element element : resolverElem) {
                        javaClass = element.getAttributeNS(null, "JAVACLASS");
                        description = element.getAttributeNS(null, "DESCRIPTION");
                        if (description != null && description.length() > 0) {
                            LOG.debug("Register Resolver: {}: {}", (Object)javaClass, (Object)description);
                        } else {
                            LOG.debug("Register Resolver: {}: For unknown purposes", (Object)javaClass);
                        }
                        classNames.add(javaClass);
                    }
                    ResourceResolver.registerClassNames(classNames);
                }
                if ("KeyResolver".equals(tag)) {
                    resolverElem = XMLUtils.selectNodes(el.getFirstChild(), CONF_NS, "Resolver");
                    classNames = new ArrayList(resolverElem.length);
                    for (Element element : resolverElem) {
                        javaClass = element.getAttributeNS(null, "JAVACLASS");
                        description = element.getAttributeNS(null, "DESCRIPTION");
                        if (description != null && description.length() > 0) {
                            LOG.debug("Register Resolver: {}: {}", (Object)javaClass, (Object)description);
                        } else {
                            LOG.debug("Register Resolver: {}: For unknown purposes", (Object)javaClass);
                        }
                        classNames.add(javaClass);
                    }
                    KeyResolver.registerClassNames(classNames);
                }
                if (!"PrefixMappings".equals(tag)) continue;
                LOG.debug("Now I try to bind prefixes:");
                for (Element element : nl = XMLUtils.selectNodes(el.getFirstChild(), CONF_NS, "PrefixMapping")) {
                    String namespace = element.getAttributeNS(null, "namespace");
                    String prefix = element.getAttributeNS(null, "prefix");
                    LOG.debug("Now I try to bind {} to {}", (Object)prefix, (Object)namespace);
                    ElementProxy.setDefaultPrefix(namespace, prefix);
                }
            }
        }
        catch (Exception e) {
            LOG.error("Bad: ", (Throwable)e);
        }
    }
}

