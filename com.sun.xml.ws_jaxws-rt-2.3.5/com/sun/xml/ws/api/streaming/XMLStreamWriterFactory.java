/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.api.streaming;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.streaming.ContextClassloaderLocal;
import com.sun.xml.ws.encoding.HasEncoding;
import com.sun.xml.ws.streaming.XMLReaderException;
import com.sun.xml.ws.util.MrJarUtil;
import com.sun.xml.ws.util.xml.XMLStreamWriterFilter;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.WebServiceException;

public abstract class XMLStreamWriterFactory {
    private static final Logger LOGGER = Logger.getLogger(XMLStreamWriterFactory.class.getName());
    private static volatile ContextClassloaderLocal<XMLStreamWriterFactory> writerFactory = new ContextClassloaderLocal<XMLStreamWriterFactory>(){

        @Override
        protected XMLStreamWriterFactory initialValue() {
            XMLOutputFactory xof = null;
            if (Boolean.getBoolean(XMLStreamWriterFactory.class.getName() + ".woodstox")) {
                try {
                    xof = (XMLOutputFactory)Class.forName("com.ctc.wstx.stax.WstxOutputFactory").newInstance();
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            if (xof == null) {
                xof = XMLOutputFactory.newInstance();
            }
            XMLStreamWriterFactory f = null;
            if (!MrJarUtil.getNoPoolProperty(XMLStreamWriterFactory.class.getName())) {
                try {
                    Class<?> clazz = xof.createXMLStreamWriter(new StringWriter()).getClass();
                    if (clazz.getName().startsWith("com.sun.xml.stream.")) {
                        f = new Zephyr(xof, clazz);
                    }
                }
                catch (XMLStreamException ex) {
                    Logger.getLogger(XMLStreamWriterFactory.class.getName()).log(Level.INFO, null, ex);
                }
            }
            if (f == null && xof.getClass().getName().equals("com.ctc.wstx.stax.WstxOutputFactory")) {
                f = new NoLock(xof);
            }
            if (f == null) {
                f = new Default(xof);
            }
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "XMLStreamWriterFactory instance is = {0}", f);
            }
            return f;
        }
    };

    public abstract XMLStreamWriter doCreate(OutputStream var1);

    public abstract XMLStreamWriter doCreate(OutputStream var1, String var2);

    public abstract void doRecycle(XMLStreamWriter var1);

    public static void recycle(XMLStreamWriter r) {
        XMLStreamWriterFactory.get().doRecycle(r);
    }

    @NotNull
    public static XMLStreamWriterFactory get() {
        return writerFactory.get();
    }

    public static void set(@NotNull XMLStreamWriterFactory f) {
        if (f == null) {
            throw new IllegalArgumentException();
        }
        writerFactory.set(f);
    }

    public static XMLStreamWriter create(OutputStream out) {
        return XMLStreamWriterFactory.get().doCreate(out);
    }

    public static XMLStreamWriter create(OutputStream out, String encoding) {
        return XMLStreamWriterFactory.get().doCreate(out, encoding);
    }

    public static XMLStreamWriter createXMLStreamWriter(OutputStream out) {
        return XMLStreamWriterFactory.create(out);
    }

    public static XMLStreamWriter createXMLStreamWriter(OutputStream out, String encoding) {
        return XMLStreamWriterFactory.create(out, encoding);
    }

    public static XMLStreamWriter createXMLStreamWriter(OutputStream out, String encoding, boolean declare) {
        return XMLStreamWriterFactory.create(out, encoding);
    }

    public static class HasEncodingWriter
    extends XMLStreamWriterFilter
    implements HasEncoding {
        private final String encoding;

        HasEncodingWriter(XMLStreamWriter writer, String encoding) {
            super(writer);
            this.encoding = encoding;
        }

        @Override
        public String getEncoding() {
            return this.encoding;
        }

        public XMLStreamWriter getWriter() {
            return this.writer;
        }
    }

    public static final class NoLock
    extends XMLStreamWriterFactory {
        private final XMLOutputFactory xof;

        public NoLock(XMLOutputFactory xof) {
            this.xof = xof;
        }

        @Override
        public XMLStreamWriter doCreate(OutputStream out) {
            return this.doCreate(out, "utf-8");
        }

        @Override
        public XMLStreamWriter doCreate(OutputStream out, String encoding) {
            try {
                XMLStreamWriter writer = this.xof.createXMLStreamWriter(out, encoding);
                return new HasEncodingWriter(writer, encoding);
            }
            catch (XMLStreamException e) {
                throw new XMLReaderException("stax.cantCreate", e);
            }
        }

        @Override
        public void doRecycle(XMLStreamWriter r) {
        }
    }

    public static final class Zephyr
    extends XMLStreamWriterFactory {
        private final XMLOutputFactory xof;
        private final ThreadLocal<XMLStreamWriter> pool = new ThreadLocal();
        private final Method resetMethod;
        private final Method setOutputMethod;
        private final Class zephyrClass;

        public static XMLStreamWriterFactory newInstance(XMLOutputFactory xof) {
            try {
                Class<?> clazz = xof.createXMLStreamWriter(new StringWriter()).getClass();
                if (!clazz.getName().startsWith("com.sun.xml.stream.")) {
                    return null;
                }
                return new Zephyr(xof, clazz);
            }
            catch (XMLStreamException e) {
                return null;
            }
        }

        private Zephyr(XMLOutputFactory xof, Class clazz) {
            this.xof = xof;
            this.zephyrClass = clazz;
            this.setOutputMethod = Zephyr.getMethod(clazz, "setOutput", StreamResult.class, String.class);
            this.resetMethod = Zephyr.getMethod(clazz, "reset", new Class[0]);
        }

        private static Method getMethod(final Class<?> c, final String methodname, final Class<?> ... params) {
            return AccessController.doPrivileged(new PrivilegedAction<Method>(){

                @Override
                public Method run() {
                    try {
                        return c.getMethod(methodname, params);
                    }
                    catch (NoSuchMethodException e) {
                        throw new NoSuchMethodError(e.getMessage());
                    }
                }
            });
        }

        @Nullable
        private XMLStreamWriter fetch() {
            XMLStreamWriter sr = this.pool.get();
            if (sr == null) {
                return null;
            }
            this.pool.set(null);
            return sr;
        }

        @Override
        public XMLStreamWriter doCreate(OutputStream out) {
            return this.doCreate(out, "UTF-8");
        }

        @Override
        public XMLStreamWriter doCreate(OutputStream out, String encoding) {
            XMLStreamWriter xsw = this.fetch();
            if (xsw != null) {
                try {
                    this.resetMethod.invoke((Object)xsw, new Object[0]);
                    this.setOutputMethod.invoke((Object)xsw, new StreamResult(out), encoding);
                }
                catch (IllegalAccessException e) {
                    throw new XMLReaderException("stax.cantCreate", e);
                }
                catch (InvocationTargetException e) {
                    throw new XMLReaderException("stax.cantCreate", e);
                }
            }
            try {
                xsw = this.xof.createXMLStreamWriter(out, encoding);
            }
            catch (XMLStreamException e) {
                throw new XMLReaderException("stax.cantCreate", e);
            }
            return new HasEncodingWriter(xsw, encoding);
        }

        @Override
        public void doRecycle(XMLStreamWriter r) {
            if (r instanceof HasEncodingWriter) {
                r = ((HasEncodingWriter)r).getWriter();
            }
            if (this.zephyrClass.isInstance(r)) {
                try {
                    r.close();
                }
                catch (XMLStreamException e) {
                    throw new WebServiceException((Throwable)e);
                }
                this.pool.set(r);
            }
            if (r instanceof RecycleAware) {
                ((RecycleAware)((Object)r)).onRecycled();
            }
        }
    }

    public static final class Default
    extends XMLStreamWriterFactory {
        private final XMLOutputFactory xof;

        public Default(XMLOutputFactory xof) {
            this.xof = xof;
        }

        @Override
        public XMLStreamWriter doCreate(OutputStream out) {
            return this.doCreate(out, "UTF-8");
        }

        @Override
        public synchronized XMLStreamWriter doCreate(OutputStream out, String encoding) {
            try {
                XMLStreamWriter writer = this.xof.createXMLStreamWriter(out, encoding);
                return new HasEncodingWriter(writer, encoding);
            }
            catch (XMLStreamException e) {
                throw new XMLReaderException("stax.cantCreate", e);
            }
        }

        @Override
        public void doRecycle(XMLStreamWriter r) {
        }
    }

    public static interface RecycleAware {
        public void onRecycled();
    }
}

