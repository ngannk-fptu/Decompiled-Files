/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Marshaller
 *  javax.xml.bind.PropertyException
 *  javax.xml.bind.Unmarshaller
 */
package com.sun.jersey.core.provider.jaxb;

import com.sun.jersey.api.provider.jaxb.XmlHeader;
import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;
import com.sun.jersey.core.util.FeaturesAndProperties;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;

public abstract class AbstractJAXBProvider<T>
extends AbstractMessageReaderWriterProvider<T> {
    private static final Map<Class<?>, WeakReference<JAXBContext>> jaxbContexts = new WeakHashMap();
    private final Providers ps;
    private final boolean fixedMediaType;
    private final ContextResolver<JAXBContext> mtContext;
    private final ContextResolver<Unmarshaller> mtUnmarshaller;
    private final ContextResolver<Marshaller> mtMarshaller;
    private boolean formattedOutput = false;
    private boolean xmlRootElementProcessing = false;

    public AbstractJAXBProvider(Providers ps) {
        this(ps, null);
    }

    public AbstractJAXBProvider(Providers ps, MediaType mt) {
        this.ps = ps;
        boolean bl = this.fixedMediaType = mt != null;
        if (this.fixedMediaType) {
            this.mtContext = ps.getContextResolver(JAXBContext.class, mt);
            this.mtUnmarshaller = ps.getContextResolver(Unmarshaller.class, mt);
            this.mtMarshaller = ps.getContextResolver(Marshaller.class, mt);
        } else {
            this.mtContext = null;
            this.mtUnmarshaller = null;
            this.mtMarshaller = null;
        }
    }

    @Context
    public void setConfiguration(FeaturesAndProperties fp) {
        this.formattedOutput = fp.getFeature("com.sun.jersey.config.feature.Formatted");
        this.xmlRootElementProcessing = fp.getFeature("com.sun.jersey.config.feature.XmlRootElementProcessing");
    }

    protected boolean isSupported(MediaType m) {
        return true;
    }

    protected final Unmarshaller getUnmarshaller(Class type, MediaType mt) throws JAXBException {
        Unmarshaller u;
        if (this.fixedMediaType) {
            return this.getUnmarshaller(type);
        }
        ContextResolver<Unmarshaller> uncr = this.ps.getContextResolver(Unmarshaller.class, mt);
        if (uncr != null && (u = uncr.getContext(type)) != null) {
            return u;
        }
        return this.getJAXBContext(type, mt).createUnmarshaller();
    }

    private Unmarshaller getUnmarshaller(Class type) throws JAXBException {
        Unmarshaller u;
        if (this.mtUnmarshaller != null && (u = this.mtUnmarshaller.getContext(type)) != null) {
            return u;
        }
        return this.getJAXBContext(type).createUnmarshaller();
    }

    protected final Marshaller getMarshaller(Class type, MediaType mt) throws JAXBException {
        Marshaller m;
        if (this.fixedMediaType) {
            return this.getMarshaller(type);
        }
        ContextResolver<Marshaller> mcr = this.ps.getContextResolver(Marshaller.class, mt);
        if (mcr != null && (m = mcr.getContext(type)) != null) {
            return m;
        }
        m = this.getJAXBContext(type, mt).createMarshaller();
        if (this.formattedOutput) {
            m.setProperty("jaxb.formatted.output", (Object)this.formattedOutput);
        }
        return m;
    }

    private Marshaller getMarshaller(Class type) throws JAXBException {
        Marshaller u;
        if (this.mtMarshaller != null && (u = this.mtMarshaller.getContext(type)) != null) {
            return u;
        }
        Marshaller m = this.getJAXBContext(type).createMarshaller();
        if (this.formattedOutput) {
            m.setProperty("jaxb.formatted.output", (Object)this.formattedOutput);
        }
        return m;
    }

    private JAXBContext getJAXBContext(Class type, MediaType mt) throws JAXBException {
        JAXBContext c;
        ContextResolver<JAXBContext> cr = this.ps.getContextResolver(JAXBContext.class, mt);
        if (cr != null && (c = cr.getContext(type)) != null) {
            return c;
        }
        return this.getStoredJAXBContext(type);
    }

    protected JAXBContext getJAXBContext(Class type) throws JAXBException {
        JAXBContext c;
        if (this.mtContext != null && (c = this.mtContext.getContext(type)) != null) {
            return c;
        }
        return this.getStoredJAXBContext(type);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected JAXBContext getStoredJAXBContext(Class type) throws JAXBException {
        Map<Class<?>, WeakReference<JAXBContext>> map = jaxbContexts;
        synchronized (map) {
            JAXBContext c;
            WeakReference<JAXBContext> ref = jaxbContexts.get(type);
            JAXBContext jAXBContext = c = ref != null ? (JAXBContext)ref.get() : null;
            if (c == null) {
                c = JAXBContext.newInstance((Class[])new Class[]{type});
                jaxbContexts.put(type, new WeakReference<JAXBContext>(c));
            }
            return c;
        }
    }

    protected static SAXSource getSAXSource(SAXParserFactory spf, InputStream entityStream) throws JAXBException {
        try {
            return new SAXSource(spf.newSAXParser().getXMLReader(), new InputSource(entityStream));
        }
        catch (Exception ex) {
            throw new JAXBException("Error creating SAXSource", (Throwable)ex);
        }
    }

    protected boolean isFormattedOutput() {
        return this.formattedOutput;
    }

    protected boolean isXmlRootElementProcessing() {
        return this.xmlRootElementProcessing;
    }

    protected void setHeader(Marshaller m, Annotation[] annotations) throws PropertyException {
        for (Annotation a : annotations) {
            if (!(a instanceof XmlHeader)) continue;
            try {
                m.setProperty("com.sun.xml.bind.xmlHeaders", (Object)((XmlHeader)a).value());
            }
            catch (PropertyException e) {
                try {
                    m.setProperty("com.sun.xml.internal.bind.xmlHeaders", (Object)((XmlHeader)a).value());
                }
                catch (PropertyException ex) {
                    Logger.getLogger(AbstractJAXBProvider.class.getName()).log(Level.WARNING, "@XmlHeader annotation is not supported with this JAXB implementation. Please use JAXB RI if you need this feature.");
                }
            }
            break;
        }
    }
}

