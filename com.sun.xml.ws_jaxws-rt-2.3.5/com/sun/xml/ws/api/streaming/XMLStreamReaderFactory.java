/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.api.streaming;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.streaming.ContextClassloaderLocal;
import com.sun.xml.ws.resources.StreamingMessages;
import com.sun.xml.ws.streaming.XMLReaderException;
import com.sun.xml.ws.util.MrJarUtil;
import com.sun.xml.ws.util.xml.XmlUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.InputSource;

public abstract class XMLStreamReaderFactory {
    private static final Logger LOGGER = Logger.getLogger(XMLStreamReaderFactory.class.getName());
    private static final String CLASS_NAME_OF_WSTXINPUTFACTORY = "com.ctc.wstx.stax.WstxInputFactory";
    private static volatile ContextClassloaderLocal<XMLStreamReaderFactory> streamReader = new ContextClassloaderLocal<XMLStreamReaderFactory>(){

        @Override
        protected XMLStreamReaderFactory initialValue() {
            XMLInputFactory xif = XMLStreamReaderFactory.getXMLInputFactory();
            XMLStreamReaderFactory f = null;
            if (!MrJarUtil.getNoPoolProperty(XMLStreamReaderFactory.class.getName())) {
                f = Zephyr.newInstance(xif);
            }
            if (f == null && xif.getClass().getName().equals(XMLStreamReaderFactory.CLASS_NAME_OF_WSTXINPUTFACTORY)) {
                f = new Woodstox(xif);
            }
            if (f == null) {
                f = new Default();
            }
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "XMLStreamReaderFactory instance is = {0}", f);
            }
            return f;
        }
    };

    private static XMLInputFactory getXMLInputFactory() {
        XMLInputFactory xif;
        block4: {
            xif = null;
            if (XMLStreamReaderFactory.getProperty(XMLStreamReaderFactory.class.getName() + ".woodstox").booleanValue()) {
                try {
                    xif = (XMLInputFactory)Class.forName(CLASS_NAME_OF_WSTXINPUTFACTORY).newInstance();
                }
                catch (Exception e) {
                    if (!LOGGER.isLoggable(Level.WARNING)) break block4;
                    LOGGER.log(Level.WARNING, StreamingMessages.WOODSTOX_CANT_LOAD(CLASS_NAME_OF_WSTXINPUTFACTORY), e);
                }
            }
        }
        if (xif == null) {
            xif = XmlUtil.newXMLInputFactory(true);
        }
        xif.setProperty("javax.xml.stream.isNamespaceAware", true);
        xif.setProperty("javax.xml.stream.supportDTD", false);
        xif.setProperty("javax.xml.stream.isCoalescing", true);
        return xif;
    }

    public static void set(XMLStreamReaderFactory f) {
        if (f == null) {
            throw new IllegalArgumentException();
        }
        streamReader.set(f);
    }

    public static XMLStreamReaderFactory get() {
        return streamReader.get();
    }

    public static XMLStreamReader create(InputSource source, boolean rejectDTDs) {
        try {
            if (source.getCharacterStream() != null) {
                return XMLStreamReaderFactory.get().doCreate(source.getSystemId(), source.getCharacterStream(), rejectDTDs);
            }
            if (source.getByteStream() != null) {
                return XMLStreamReaderFactory.get().doCreate(source.getSystemId(), source.getByteStream(), rejectDTDs);
            }
            InputStream is = new URL(source.getSystemId()).openStream();
            source.setByteStream(is);
            return XMLStreamReaderFactory.get().doCreate(source.getSystemId(), is, rejectDTDs);
        }
        catch (IOException e) {
            throw new XMLReaderException("stax.cantCreate", e);
        }
    }

    public static XMLStreamReader create(@Nullable String systemId, InputStream in, boolean rejectDTDs) {
        return XMLStreamReaderFactory.get().doCreate(systemId, in, rejectDTDs);
    }

    public static XMLStreamReader create(@Nullable String systemId, InputStream in, @Nullable String encoding, boolean rejectDTDs) {
        return encoding == null ? XMLStreamReaderFactory.create(systemId, in, rejectDTDs) : XMLStreamReaderFactory.get().doCreate(systemId, in, encoding, rejectDTDs);
    }

    public static XMLStreamReader create(@Nullable String systemId, Reader reader, boolean rejectDTDs) {
        return XMLStreamReaderFactory.get().doCreate(systemId, reader, rejectDTDs);
    }

    public static void recycle(XMLStreamReader r) {
    }

    public abstract XMLStreamReader doCreate(String var1, InputStream var2, boolean var3);

    private XMLStreamReader doCreate(String systemId, InputStream in, @NotNull String encoding, boolean rejectDTDs) {
        InputStreamReader reader;
        try {
            reader = new InputStreamReader(in, encoding);
        }
        catch (UnsupportedEncodingException ue) {
            throw new XMLReaderException("stax.cantCreate", ue);
        }
        return this.doCreate(systemId, reader, rejectDTDs);
    }

    public abstract XMLStreamReader doCreate(String var1, Reader var2, boolean var3);

    public abstract void doRecycle(XMLStreamReader var1);

    private static int buildIntegerValue(String propertyName, int defaultValue) {
        block4: {
            String propVal = System.getProperty(propertyName);
            if (propVal != null && propVal.length() > 0) {
                try {
                    Integer value = Integer.parseInt(propVal);
                    if (value > 0) {
                        return value;
                    }
                }
                catch (NumberFormatException nfe) {
                    if (!LOGGER.isLoggable(Level.WARNING)) break block4;
                    LOGGER.log(Level.WARNING, StreamingMessages.INVALID_PROPERTY_VALUE_INTEGER(propertyName, propVal, Integer.toString(defaultValue)), nfe);
                }
            }
        }
        return defaultValue;
    }

    private static long buildLongValue(String propertyName, long defaultValue) {
        block4: {
            String propVal = System.getProperty(propertyName);
            if (propVal != null && propVal.length() > 0) {
                try {
                    long value = Long.parseLong(propVal);
                    if (value > 0L) {
                        return value;
                    }
                }
                catch (NumberFormatException nfe) {
                    if (!LOGGER.isLoggable(Level.WARNING)) break block4;
                    LOGGER.log(Level.WARNING, StreamingMessages.INVALID_PROPERTY_VALUE_LONG(propertyName, propVal, Long.toString(defaultValue)), nfe);
                }
            }
        }
        return defaultValue;
    }

    private static Boolean getProperty(final String prop) {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>(){

            @Override
            public Boolean run() {
                String value = System.getProperty(prop);
                return value != null ? Boolean.valueOf(value) : Boolean.FALSE;
            }
        });
    }

    public static final class Woodstox
    extends NoLock {
        public static final String PROPERTY_MAX_ATTRIBUTES_PER_ELEMENT = "xml.ws.maximum.AttributesPerElement";
        public static final String PROPERTY_MAX_ATTRIBUTE_SIZE = "xml.ws.maximum.AttributeSize";
        public static final String PROPERTY_MAX_CHILDREN_PER_ELEMENT = "xml.ws.maximum.ChildrenPerElement";
        public static final String PROPERTY_MAX_ELEMENT_COUNT = "xml.ws.maximum.ElementCount";
        public static final String PROPERTY_MAX_ELEMENT_DEPTH = "xml.ws.maximum.ElementDepth";
        public static final String PROPERTY_MAX_CHARACTERS = "xml.ws.maximum.Characters";
        private static final int DEFAULT_MAX_ATTRIBUTES_PER_ELEMENT = 500;
        private static final int DEFAULT_MAX_ATTRIBUTE_SIZE = 524288;
        private static final int DEFAULT_MAX_CHILDREN_PER_ELEMENT = Integer.MAX_VALUE;
        private static final int DEFAULT_MAX_ELEMENT_DEPTH = 500;
        private static final long DEFAULT_MAX_ELEMENT_COUNT = Integer.MAX_VALUE;
        private static final long DEFAULT_MAX_CHARACTERS = Long.MAX_VALUE;
        private int maxAttributesPerElement = 500;
        private int maxAttributeSize = 524288;
        private int maxChildrenPerElement = Integer.MAX_VALUE;
        private int maxElementDepth = 500;
        private long maxElementCount = Integer.MAX_VALUE;
        private long maxCharacters = Long.MAX_VALUE;
        private static final String P_MAX_ATTRIBUTES_PER_ELEMENT = "com.ctc.wstx.maxAttributesPerElement";
        private static final String P_MAX_ATTRIBUTE_SIZE = "com.ctc.wstx.maxAttributeSize";
        private static final String P_MAX_CHILDREN_PER_ELEMENT = "com.ctc.wstx.maxChildrenPerElement";
        private static final String P_MAX_ELEMENT_COUNT = "com.ctc.wstx.maxElementCount";
        private static final String P_MAX_ELEMENT_DEPTH = "com.ctc.wstx.maxElementDepth";
        private static final String P_MAX_CHARACTERS = "com.ctc.wstx.maxCharacters";
        private static final String P_INTERN_NSURIS = "org.codehaus.stax2.internNsUris";
        private static final String P_RETURN_NULL_FOR_DEFAULT_NAMESPACE = "com.ctc.wstx.returnNullForDefaultNamespace";

        public Woodstox(XMLInputFactory xif) {
            super(xif);
            if (xif.isPropertySupported(P_INTERN_NSURIS)) {
                xif.setProperty(P_INTERN_NSURIS, true);
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "org.codehaus.stax2.internNsUris is {0}", true);
                }
            }
            if (xif.isPropertySupported(P_MAX_ATTRIBUTES_PER_ELEMENT)) {
                this.maxAttributesPerElement = XMLStreamReaderFactory.buildIntegerValue(PROPERTY_MAX_ATTRIBUTES_PER_ELEMENT, 500);
                xif.setProperty(P_MAX_ATTRIBUTES_PER_ELEMENT, this.maxAttributesPerElement);
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "com.ctc.wstx.maxAttributesPerElement is {0}", this.maxAttributesPerElement);
                }
            }
            if (xif.isPropertySupported(P_MAX_ATTRIBUTE_SIZE)) {
                this.maxAttributeSize = XMLStreamReaderFactory.buildIntegerValue(PROPERTY_MAX_ATTRIBUTE_SIZE, 524288);
                xif.setProperty(P_MAX_ATTRIBUTE_SIZE, this.maxAttributeSize);
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "com.ctc.wstx.maxAttributeSize is {0}", this.maxAttributeSize);
                }
            }
            if (xif.isPropertySupported(P_MAX_CHILDREN_PER_ELEMENT)) {
                this.maxChildrenPerElement = XMLStreamReaderFactory.buildIntegerValue(PROPERTY_MAX_CHILDREN_PER_ELEMENT, Integer.MAX_VALUE);
                xif.setProperty(P_MAX_CHILDREN_PER_ELEMENT, this.maxChildrenPerElement);
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "com.ctc.wstx.maxChildrenPerElement is {0}", this.maxChildrenPerElement);
                }
            }
            if (xif.isPropertySupported(P_MAX_ELEMENT_DEPTH)) {
                this.maxElementDepth = XMLStreamReaderFactory.buildIntegerValue(PROPERTY_MAX_ELEMENT_DEPTH, 500);
                xif.setProperty(P_MAX_ELEMENT_DEPTH, this.maxElementDepth);
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "com.ctc.wstx.maxElementDepth is {0}", this.maxElementDepth);
                }
            }
            if (xif.isPropertySupported(P_MAX_ELEMENT_COUNT)) {
                this.maxElementCount = XMLStreamReaderFactory.buildLongValue(PROPERTY_MAX_ELEMENT_COUNT, Integer.MAX_VALUE);
                xif.setProperty(P_MAX_ELEMENT_COUNT, this.maxElementCount);
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "com.ctc.wstx.maxElementCount is {0}", this.maxElementCount);
                }
            }
            if (xif.isPropertySupported(P_MAX_CHARACTERS)) {
                this.maxCharacters = XMLStreamReaderFactory.buildLongValue(PROPERTY_MAX_CHARACTERS, Long.MAX_VALUE);
                xif.setProperty(P_MAX_CHARACTERS, this.maxCharacters);
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "com.ctc.wstx.maxCharacters is {0}", this.maxCharacters);
                }
            }
            try {
                xif.setProperty(P_RETURN_NULL_FOR_DEFAULT_NAMESPACE, Boolean.TRUE);
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "com.ctc.wstx.returnNullForDefaultNamespace is {0}", xif.getProperty(P_RETURN_NULL_FOR_DEFAULT_NAMESPACE));
                }
            }
            catch (Throwable t) {
                LOGGER.log(Level.WARNING, "Expected property not found in Woodstox input factory: '{0}'", P_RETURN_NULL_FOR_DEFAULT_NAMESPACE);
            }
        }

        @Override
        public XMLStreamReader doCreate(String systemId, InputStream in, boolean rejectDTDs) {
            return super.doCreate(systemId, in, rejectDTDs);
        }

        @Override
        public XMLStreamReader doCreate(String systemId, Reader in, boolean rejectDTDs) {
            return super.doCreate(systemId, in, rejectDTDs);
        }
    }

    public static class NoLock
    extends XMLStreamReaderFactory {
        private final XMLInputFactory xif;

        public NoLock(XMLInputFactory xif) {
            this.xif = xif;
        }

        @Override
        public XMLStreamReader doCreate(String systemId, InputStream in, boolean rejectDTDs) {
            try {
                return this.xif.createXMLStreamReader(systemId, in);
            }
            catch (XMLStreamException e) {
                throw new XMLReaderException("stax.cantCreate", e);
            }
        }

        @Override
        public XMLStreamReader doCreate(String systemId, Reader in, boolean rejectDTDs) {
            try {
                return this.xif.createXMLStreamReader(systemId, in);
            }
            catch (XMLStreamException e) {
                throw new XMLReaderException("stax.cantCreate", e);
            }
        }

        @Override
        public void doRecycle(XMLStreamReader r) {
        }
    }

    public static final class Default
    extends XMLStreamReaderFactory {
        private final ThreadLocal<XMLInputFactory> xif = new ThreadLocal<XMLInputFactory>(){

            @Override
            public XMLInputFactory initialValue() {
                return XMLStreamReaderFactory.getXMLInputFactory();
            }
        };

        @Override
        public XMLStreamReader doCreate(String systemId, InputStream in, boolean rejectDTDs) {
            try {
                return this.xif.get().createXMLStreamReader(systemId, in);
            }
            catch (XMLStreamException e) {
                throw new XMLReaderException("stax.cantCreate", e);
            }
        }

        @Override
        public XMLStreamReader doCreate(String systemId, Reader in, boolean rejectDTDs) {
            try {
                return this.xif.get().createXMLStreamReader(systemId, in);
            }
            catch (XMLStreamException e) {
                throw new XMLReaderException("stax.cantCreate", e);
            }
        }

        @Override
        public void doRecycle(XMLStreamReader r) {
        }
    }

    private static final class Zephyr
    extends XMLStreamReaderFactory {
        private final XMLInputFactory xif;
        private final ThreadLocal<XMLStreamReader> pool = new ThreadLocal();
        private final Method setInputSourceMethod;
        private final Method resetMethod;
        private final Class zephyrClass;

        @Nullable
        public static XMLStreamReaderFactory newInstance(XMLInputFactory xif) {
            try {
                Class<?> clazz = xif.createXMLStreamReader(new StringReader("<foo/>")).getClass();
                if (!clazz.getName().startsWith("com.sun.xml.stream.")) {
                    return null;
                }
                return new Zephyr(xif, clazz);
            }
            catch (NoSuchMethodException e) {
                return null;
            }
            catch (XMLStreamException e) {
                return null;
            }
        }

        public Zephyr(XMLInputFactory xif, Class clazz) throws NoSuchMethodException {
            this.zephyrClass = clazz;
            this.setInputSourceMethod = clazz.getMethod("setInputSource", InputSource.class);
            this.resetMethod = clazz.getMethod("reset", new Class[0]);
            try {
                xif.setProperty("reuse-instance", false);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
            this.xif = xif;
        }

        @Nullable
        private XMLStreamReader fetch() {
            XMLStreamReader sr = this.pool.get();
            if (sr == null) {
                return null;
            }
            this.pool.set(null);
            return sr;
        }

        @Override
        public void doRecycle(XMLStreamReader r) {
            if (this.zephyrClass.isInstance(r)) {
                this.pool.set(r);
            }
        }

        @Override
        public XMLStreamReader doCreate(String systemId, InputStream in, boolean rejectDTDs) {
            try {
                XMLStreamReader xsr = this.fetch();
                if (xsr == null) {
                    return this.xif.createXMLStreamReader(systemId, in);
                }
                InputSource is = new InputSource(systemId);
                is.setByteStream(in);
                this.reuse(xsr, is);
                return xsr;
            }
            catch (IllegalAccessException e) {
                throw new XMLReaderException("stax.cantCreate", e);
            }
            catch (InvocationTargetException e) {
                throw new XMLReaderException("stax.cantCreate", e);
            }
            catch (XMLStreamException e) {
                throw new XMLReaderException("stax.cantCreate", e);
            }
        }

        @Override
        public XMLStreamReader doCreate(String systemId, Reader in, boolean rejectDTDs) {
            try {
                XMLStreamReader xsr = this.fetch();
                if (xsr == null) {
                    return this.xif.createXMLStreamReader(systemId, in);
                }
                InputSource is = new InputSource(systemId);
                is.setCharacterStream(in);
                this.reuse(xsr, is);
                return xsr;
            }
            catch (IllegalAccessException e) {
                throw new XMLReaderException("stax.cantCreate", e);
            }
            catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause == null) {
                    cause = e;
                }
                throw new XMLReaderException("stax.cantCreate", cause);
            }
            catch (XMLStreamException e) {
                throw new XMLReaderException("stax.cantCreate", e);
            }
        }

        private void reuse(XMLStreamReader xsr, InputSource in) throws IllegalAccessException, InvocationTargetException {
            this.resetMethod.invoke((Object)xsr, new Object[0]);
            this.setInputSourceMethod.invoke((Object)xsr, in);
        }
    }

    public static interface RecycleAware {
        public void onRecycled();
    }
}

