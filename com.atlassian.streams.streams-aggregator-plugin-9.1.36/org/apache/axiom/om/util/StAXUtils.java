/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.om.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.om.util.StAXWriterConfiguration;
import org.apache.axiom.util.stax.XMLEventUtils;
import org.apache.axiom.util.stax.dialect.StAXDialect;
import org.apache.axiom.util.stax.dialect.StAXDialectDetector;
import org.apache.axiom.util.stax.wrapper.ImmutableXMLInputFactory;
import org.apache.axiom.util.stax.wrapper.ImmutableXMLOutputFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StAXUtils {
    private static final Log log = LogFactory.getLog(StAXUtils.class);
    private static boolean isFactoryPerClassLoader = true;
    private static final Map inputFactoryMap = Collections.synchronizedMap(new WeakHashMap());
    private static final Map outputFactoryMap = Collections.synchronizedMap(new WeakHashMap());
    private static final Map inputFactoryPerCLMap = Collections.synchronizedMap(new WeakHashMap());
    private static final Map outputFactoryPerCLMap = Collections.synchronizedMap(new WeakHashMap());

    public static XMLInputFactory getXMLInputFactory() {
        return StAXUtils.getXMLInputFactory(null, isFactoryPerClassLoader);
    }

    public static XMLInputFactory getXMLInputFactory(StAXParserConfiguration configuration) {
        return StAXUtils.getXMLInputFactory(configuration, isFactoryPerClassLoader);
    }

    public static XMLInputFactory getXMLInputFactory(boolean factoryPerClassLoaderPolicy) {
        return StAXUtils.getXMLInputFactory(null, factoryPerClassLoaderPolicy);
    }

    public static XMLInputFactory getXMLInputFactory(StAXParserConfiguration configuration, boolean factoryPerClassLoaderPolicy) {
        if (factoryPerClassLoaderPolicy) {
            return StAXUtils.getXMLInputFactory_perClassLoader(configuration);
        }
        return StAXUtils.getXMLInputFactory_singleton(configuration);
    }

    public static void releaseXMLInputFactory(XMLInputFactory factory) {
    }

    public static XMLStreamReader createXMLStreamReader(InputStream in, String encoding) throws XMLStreamException {
        return StAXUtils.createXMLStreamReader(null, in, encoding);
    }

    public static XMLStreamReader createXMLStreamReader(StAXParserConfiguration configuration, final InputStream in, final String encoding) throws XMLStreamException {
        final XMLInputFactory inputFactory = StAXUtils.getXMLInputFactory(configuration);
        try {
            XMLStreamReader reader = (XMLStreamReader)AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() throws XMLStreamException {
                    return inputFactory.createXMLStreamReader(in, encoding);
                }
            });
            if (log.isDebugEnabled()) {
                log.debug((Object)("XMLStreamReader is " + reader.getClass().getName()));
            }
            return reader;
        }
        catch (PrivilegedActionException pae) {
            throw (XMLStreamException)pae.getException();
        }
    }

    public static XMLStreamReader createXMLStreamReader(InputStream in) throws XMLStreamException {
        return StAXUtils.createXMLStreamReader(null, in);
    }

    public static XMLStreamReader createXMLStreamReader(StAXParserConfiguration configuration, final InputStream in) throws XMLStreamException {
        final XMLInputFactory inputFactory = StAXUtils.getXMLInputFactory(configuration);
        try {
            XMLStreamReader reader = (XMLStreamReader)AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() throws XMLStreamException {
                    return inputFactory.createXMLStreamReader(in);
                }
            });
            if (log.isDebugEnabled()) {
                log.debug((Object)("XMLStreamReader is " + reader.getClass().getName()));
            }
            return reader;
        }
        catch (PrivilegedActionException pae) {
            throw (XMLStreamException)pae.getException();
        }
    }

    public static XMLStreamReader createXMLStreamReader(StAXParserConfiguration configuration, final String systemId, final InputStream in) throws XMLStreamException {
        final XMLInputFactory inputFactory = StAXUtils.getXMLInputFactory(configuration);
        try {
            XMLStreamReader reader = (XMLStreamReader)AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() throws XMLStreamException {
                    return inputFactory.createXMLStreamReader(systemId, in);
                }
            });
            if (log.isDebugEnabled()) {
                log.debug((Object)("XMLStreamReader is " + reader.getClass().getName()));
            }
            return reader;
        }
        catch (PrivilegedActionException pae) {
            throw (XMLStreamException)pae.getException();
        }
    }

    public static XMLStreamReader createXMLStreamReader(Reader in) throws XMLStreamException {
        return StAXUtils.createXMLStreamReader(null, in);
    }

    public static XMLStreamReader createXMLStreamReader(StAXParserConfiguration configuration, final Reader in) throws XMLStreamException {
        final XMLInputFactory inputFactory = StAXUtils.getXMLInputFactory(configuration);
        try {
            XMLStreamReader reader = (XMLStreamReader)AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() throws XMLStreamException {
                    return inputFactory.createXMLStreamReader(in);
                }
            });
            if (log.isDebugEnabled()) {
                log.debug((Object)("XMLStreamReader is " + reader.getClass().getName()));
            }
            return reader;
        }
        catch (PrivilegedActionException pae) {
            throw (XMLStreamException)pae.getException();
        }
    }

    public static XMLOutputFactory getXMLOutputFactory() {
        return StAXUtils.getXMLOutputFactory(null, isFactoryPerClassLoader);
    }

    public static XMLOutputFactory getXMLOutputFactory(StAXWriterConfiguration configuration) {
        return StAXUtils.getXMLOutputFactory(configuration, isFactoryPerClassLoader);
    }

    public static XMLOutputFactory getXMLOutputFactory(boolean factoryPerClassLoaderPolicy) {
        return StAXUtils.getXMLOutputFactory(null, factoryPerClassLoaderPolicy);
    }

    public static XMLOutputFactory getXMLOutputFactory(StAXWriterConfiguration configuration, boolean factoryPerClassLoaderPolicy) {
        if (factoryPerClassLoaderPolicy) {
            return StAXUtils.getXMLOutputFactory_perClassLoader(configuration);
        }
        return StAXUtils.getXMLOutputFactory_singleton(configuration);
    }

    public static void setFactoryPerClassLoader(boolean value) {
        isFactoryPerClassLoader = value;
    }

    public static void releaseXMLOutputFactory(XMLOutputFactory factory) {
    }

    public static XMLStreamWriter createXMLStreamWriter(OutputStream out) throws XMLStreamException {
        return StAXUtils.createXMLStreamWriter(null, out);
    }

    public static XMLStreamWriter createXMLStreamWriter(StAXWriterConfiguration configuration, final OutputStream out) throws XMLStreamException {
        final XMLOutputFactory outputFactory = StAXUtils.getXMLOutputFactory(configuration);
        try {
            XMLStreamWriter writer = (XMLStreamWriter)AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() throws XMLStreamException {
                    return outputFactory.createXMLStreamWriter(out, "utf-8");
                }
            });
            if (log.isDebugEnabled()) {
                log.debug((Object)("XMLStreamWriter is " + writer.getClass().getName()));
            }
            return writer;
        }
        catch (PrivilegedActionException pae) {
            throw (XMLStreamException)pae.getException();
        }
    }

    public static XMLStreamWriter createXMLStreamWriter(OutputStream out, String encoding) throws XMLStreamException {
        return StAXUtils.createXMLStreamWriter(null, out, encoding);
    }

    public static XMLStreamWriter createXMLStreamWriter(StAXWriterConfiguration configuration, final OutputStream out, final String encoding) throws XMLStreamException {
        final XMLOutputFactory outputFactory = StAXUtils.getXMLOutputFactory(configuration);
        try {
            XMLStreamWriter writer = (XMLStreamWriter)AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() throws XMLStreamException {
                    return outputFactory.createXMLStreamWriter(out, encoding);
                }
            });
            if (log.isDebugEnabled()) {
                log.debug((Object)("XMLStreamWriter is " + writer.getClass().getName()));
            }
            return writer;
        }
        catch (PrivilegedActionException pae) {
            throw (XMLStreamException)pae.getException();
        }
    }

    public static XMLStreamWriter createXMLStreamWriter(Writer out) throws XMLStreamException {
        return StAXUtils.createXMLStreamWriter(null, out);
    }

    public static XMLStreamWriter createXMLStreamWriter(StAXWriterConfiguration configuration, final Writer out) throws XMLStreamException {
        final XMLOutputFactory outputFactory = StAXUtils.getXMLOutputFactory(configuration);
        try {
            XMLStreamWriter writer = (XMLStreamWriter)AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() throws XMLStreamException {
                    return outputFactory.createXMLStreamWriter(out);
                }
            });
            if (log.isDebugEnabled()) {
                log.debug((Object)("XMLStreamWriter is " + writer.getClass().getName()));
            }
            return writer;
        }
        catch (PrivilegedActionException pae) {
            throw (XMLStreamException)pae.getException();
        }
    }

    public static void reset() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static Map loadFactoryProperties(String name) {
        ClassLoader cl = StAXUtils.getContextClassLoader();
        InputStream in = cl.getResourceAsStream(name);
        if (in == null) {
            return null;
        }
        try {
            Properties rawProps = new Properties();
            HashMap<Object, Boolean> props = new HashMap<Object, Boolean>();
            rawProps.load(in);
            for (Map.Entry<Object, Object> entry : rawProps.entrySet()) {
                Object value;
                String strValue = (String)entry.getValue();
                if (strValue.equals("true")) {
                    value = Boolean.TRUE;
                } else if (strValue.equals("false")) {
                    value = Boolean.FALSE;
                } else {
                    try {
                        value = Integer.valueOf(strValue);
                    }
                    catch (NumberFormatException ex) {
                        value = strValue;
                    }
                }
                props.put(entry.getKey(), (Boolean)value);
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)("Loaded factory properties from " + name + ": " + props));
            }
            HashMap<Object, Boolean> it = props;
            return it;
        }
        catch (IOException ex) {
            log.error((Object)("Failed to read " + name), (Throwable)ex);
            Map map = null;
            return map;
        }
        finally {
            try {
                in.close();
            }
            catch (IOException ex) {}
        }
    }

    private static XMLInputFactory newXMLInputFactory(final ClassLoader classLoader, final StAXParserConfiguration configuration) {
        return (XMLInputFactory)AccessController.doPrivileged(new PrivilegedAction(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public Object run() {
                ClassLoader savedClassLoader;
                if (classLoader == null) {
                    savedClassLoader = null;
                } else {
                    savedClassLoader = Thread.currentThread().getContextClassLoader();
                    Thread.currentThread().setContextClassLoader(classLoader);
                }
                try {
                    XMLInputFactory factory = XMLInputFactory.newInstance();
                    factory.setProperty("javax.xml.stream.isCoalescing", Boolean.TRUE);
                    Map props = StAXUtils.loadFactoryProperties("XMLInputFactory.properties");
                    if (props != null) {
                        for (Map.Entry entry : props.entrySet()) {
                            factory.setProperty((String)entry.getKey(), entry.getValue());
                        }
                    }
                    StAXDialect dialect = StAXDialectDetector.getDialect(factory.getClass());
                    if (configuration != null) {
                        factory = configuration.configure(factory, dialect);
                    }
                    ImmutableXMLInputFactory immutableXMLInputFactory = new ImmutableXMLInputFactory(dialect.normalize(dialect.makeThreadSafe(factory)));
                    return immutableXMLInputFactory;
                }
                finally {
                    if (savedClassLoader != null) {
                        Thread.currentThread().setContextClassLoader(savedClassLoader);
                    }
                }
            }
        });
    }

    private static XMLInputFactory getXMLInputFactory_perClassLoader(StAXParserConfiguration configuration) {
        XMLInputFactory factory;
        ClassLoader cl = StAXUtils.getContextClassLoader();
        if (cl == null) {
            factory = StAXUtils.getXMLInputFactory_singleton(configuration);
        } else {
            Map<ClassLoader, XMLInputFactory> map;
            if (configuration == null) {
                configuration = StAXParserConfiguration.DEFAULT;
            }
            if ((map = (Map<ClassLoader, XMLInputFactory>)inputFactoryPerCLMap.get(configuration)) == null) {
                map = Collections.synchronizedMap(new WeakHashMap());
                inputFactoryPerCLMap.put(configuration, map);
                factory = null;
            } else {
                factory = (XMLInputFactory)map.get(cl);
            }
            if (factory == null) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("About to create XMLInputFactory implementation with classloader=" + cl));
                    log.debug((Object)("The classloader for javax.xml.stream.XMLInputFactory is: " + XMLInputFactory.class.getClassLoader()));
                }
                try {
                    factory = StAXUtils.newXMLInputFactory(null, configuration);
                }
                catch (ClassCastException cce) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Failed creation of XMLInputFactory implementation with classloader=" + cl));
                        log.debug((Object)("Exception is=" + cce));
                        log.debug((Object)("Attempting with classloader: " + XMLInputFactory.class.getClassLoader()));
                    }
                    factory = StAXUtils.newXMLInputFactory(XMLInputFactory.class.getClassLoader(), configuration);
                }
                if (factory != null) {
                    map.put(cl, factory);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Created XMLInputFactory = " + factory.getClass() + " with classloader=" + cl));
                        log.debug((Object)("Configuration = " + configuration));
                        log.debug((Object)("Size of XMLInputFactory map for this configuration = " + map.size()));
                        log.debug((Object)("Configurations for which factories have been cached = " + inputFactoryPerCLMap.keySet()));
                    }
                } else {
                    factory = StAXUtils.getXMLInputFactory_singleton(configuration);
                }
            }
        }
        return factory;
    }

    private static XMLInputFactory getXMLInputFactory_singleton(StAXParserConfiguration configuration) {
        XMLInputFactory f;
        if (configuration == null) {
            configuration = StAXParserConfiguration.DEFAULT;
        }
        if ((f = (XMLInputFactory)inputFactoryMap.get(configuration)) == null) {
            f = StAXUtils.newXMLInputFactory(StAXUtils.class.getClassLoader(), configuration);
            inputFactoryMap.put(configuration, f);
            if (log.isDebugEnabled() && f != null) {
                log.debug((Object)("Created singleton XMLInputFactory " + f.getClass() + " with configuration " + configuration));
            }
        }
        return f;
    }

    private static XMLOutputFactory newXMLOutputFactory(final ClassLoader classLoader, final StAXWriterConfiguration configuration) {
        return (XMLOutputFactory)AccessController.doPrivileged(new PrivilegedAction(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public Object run() {
                ClassLoader savedClassLoader;
                if (classLoader == null) {
                    savedClassLoader = null;
                } else {
                    savedClassLoader = Thread.currentThread().getContextClassLoader();
                    Thread.currentThread().setContextClassLoader(classLoader);
                }
                try {
                    XMLOutputFactory factory = XMLOutputFactory.newInstance();
                    factory.setProperty("javax.xml.stream.isRepairingNamespaces", Boolean.FALSE);
                    Map props = StAXUtils.loadFactoryProperties("XMLOutputFactory.properties");
                    if (props != null) {
                        for (Map.Entry entry : props.entrySet()) {
                            factory.setProperty((String)entry.getKey(), entry.getValue());
                        }
                    }
                    StAXDialect dialect = StAXDialectDetector.getDialect(factory.getClass());
                    if (configuration != null) {
                        factory = configuration.configure(factory, dialect);
                    }
                    ImmutableXMLOutputFactory immutableXMLOutputFactory = new ImmutableXMLOutputFactory(dialect.normalize(dialect.makeThreadSafe(factory)));
                    return immutableXMLOutputFactory;
                }
                finally {
                    if (savedClassLoader != null) {
                        Thread.currentThread().setContextClassLoader(savedClassLoader);
                    }
                }
            }
        });
    }

    private static XMLOutputFactory getXMLOutputFactory_perClassLoader(StAXWriterConfiguration configuration) {
        XMLOutputFactory factory;
        ClassLoader cl = StAXUtils.getContextClassLoader();
        if (cl == null) {
            factory = StAXUtils.getXMLOutputFactory_singleton(configuration);
        } else {
            Map<ClassLoader, XMLOutputFactory> map;
            if (configuration == null) {
                configuration = StAXWriterConfiguration.DEFAULT;
            }
            if ((map = (Map<ClassLoader, XMLOutputFactory>)outputFactoryPerCLMap.get(configuration)) == null) {
                map = Collections.synchronizedMap(new WeakHashMap());
                outputFactoryPerCLMap.put(configuration, map);
                factory = null;
            } else {
                factory = (XMLOutputFactory)map.get(cl);
            }
            if (factory == null) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("About to create XMLOutputFactory implementation with classloader=" + cl));
                    log.debug((Object)("The classloader for javax.xml.stream.XMLOutputFactory is: " + XMLOutputFactory.class.getClassLoader()));
                }
                try {
                    factory = StAXUtils.newXMLOutputFactory(null, configuration);
                }
                catch (ClassCastException cce) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Failed creation of XMLOutputFactory implementation with classloader=" + cl));
                        log.debug((Object)("Exception is=" + cce));
                        log.debug((Object)("Attempting with classloader: " + XMLOutputFactory.class.getClassLoader()));
                    }
                    factory = StAXUtils.newXMLOutputFactory(XMLOutputFactory.class.getClassLoader(), configuration);
                }
                if (factory != null) {
                    map.put(cl, factory);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Created XMLOutputFactory = " + factory.getClass() + " for classloader=" + cl));
                        log.debug((Object)("Configuration = " + configuration));
                        log.debug((Object)("Size of XMLOutFactory map for this configuration = " + map.size()));
                        log.debug((Object)("Configurations for which factories have been cached = " + outputFactoryPerCLMap.keySet()));
                    }
                } else {
                    factory = StAXUtils.getXMLOutputFactory_singleton(configuration);
                }
            }
        }
        return factory;
    }

    private static XMLOutputFactory getXMLOutputFactory_singleton(StAXWriterConfiguration configuration) {
        XMLOutputFactory f;
        if (configuration == null) {
            configuration = StAXWriterConfiguration.DEFAULT;
        }
        if ((f = (XMLOutputFactory)outputFactoryMap.get(configuration)) == null) {
            f = StAXUtils.newXMLOutputFactory(StAXUtils.class.getClassLoader(), configuration);
            outputFactoryMap.put(configuration, f);
            if (log.isDebugEnabled() && f != null) {
                log.debug((Object)("Created singleton XMLOutputFactory " + f.getClass() + " with configuration " + configuration));
            }
        }
        return f;
    }

    private static ClassLoader getContextClassLoader() {
        ClassLoader cl = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
        return cl;
    }

    public static XMLStreamReader createNetworkDetachedXMLStreamReader(InputStream in, String encoding) throws XMLStreamException {
        return StAXUtils.createXMLStreamReader(StAXParserConfiguration.STANDALONE, in, encoding);
    }

    public static XMLInputFactory getNetworkDetachedXMLInputFactory() {
        return StAXUtils.getXMLInputFactory(StAXParserConfiguration.STANDALONE);
    }

    public static XMLStreamReader createNetworkDetachedXMLStreamReader(InputStream in) throws XMLStreamException {
        return StAXUtils.createXMLStreamReader(StAXParserConfiguration.STANDALONE, in);
    }

    public static XMLStreamReader createNetworkDetachedXMLStreamReader(Reader in) throws XMLStreamException {
        return StAXUtils.createXMLStreamReader(StAXParserConfiguration.STANDALONE, in);
    }

    public static String getEventTypeString(int event) {
        return XMLEventUtils.getEventTypeString(event);
    }
}

