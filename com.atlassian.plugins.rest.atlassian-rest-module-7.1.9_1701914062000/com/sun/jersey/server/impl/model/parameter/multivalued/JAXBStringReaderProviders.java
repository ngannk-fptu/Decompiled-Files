/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.UnmarshalException
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 */
package com.sun.jersey.server.impl.model.parameter.multivalued;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.impl.ImplMessages;
import com.sun.jersey.server.impl.model.parameter.multivalued.ExtractorContainerException;
import com.sun.jersey.spi.StringReader;
import com.sun.jersey.spi.StringReaderProvider;
import com.sun.jersey.spi.inject.Injectable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.WeakHashMap;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;

public class JAXBStringReaderProviders {
    private static final Map<Class, JAXBContext> jaxbContexts = new WeakHashMap<Class, JAXBContext>();
    private final ContextResolver<JAXBContext> context;
    private final ContextResolver<Unmarshaller> unmarshaller;

    public JAXBStringReaderProviders(Providers ps) {
        this.context = ps.getContextResolver(JAXBContext.class, null);
        this.unmarshaller = ps.getContextResolver(Unmarshaller.class, null);
    }

    protected final Unmarshaller getUnmarshaller(Class type) throws JAXBException {
        Unmarshaller u;
        if (this.unmarshaller != null && (u = this.unmarshaller.getContext(type)) != null) {
            return u;
        }
        return this.getJAXBContext(type).createUnmarshaller();
    }

    private final JAXBContext getJAXBContext(Class type) throws JAXBException {
        JAXBContext c;
        if (this.context != null && (c = this.context.getContext(type)) != null) {
            return c;
        }
        return this.getStoredJAXBContext(type);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected JAXBContext getStoredJAXBContext(Class type) throws JAXBException {
        Map<Class, JAXBContext> map = jaxbContexts;
        synchronized (map) {
            JAXBContext c = jaxbContexts.get(type);
            if (c == null) {
                c = JAXBContext.newInstance((Class[])new Class[]{type});
                jaxbContexts.put(type, c);
            }
            return c;
        }
    }

    public static class RootElementProvider
    extends JAXBStringReaderProviders
    implements StringReaderProvider {
        private final Injectable<SAXParserFactory> spf;

        public RootElementProvider(@Context Injectable<SAXParserFactory> spf, @Context Providers ps) {
            super(ps);
            this.spf = spf;
        }

        public StringReader getStringReader(final Class type, Type genericType, Annotation[] annotations) {
            boolean supported;
            boolean bl = supported = type.getAnnotation(XmlRootElement.class) != null || type.getAnnotation(XmlType.class) != null;
            if (!supported) {
                return null;
            }
            return new StringReader(){

                public Object fromString(final String value) {
                    return AccessController.doPrivileged(new PrivilegedAction<Object>(){

                        @Override
                        public Object run() {
                            try {
                                SAXSource source = new SAXSource(((SAXParserFactory)spf.getValue()).newSAXParser().getXMLReader(), new InputSource(new java.io.StringReader(value)));
                                Unmarshaller u = this.getUnmarshaller(type);
                                if (type.isAnnotationPresent(XmlRootElement.class)) {
                                    return u.unmarshal((Source)source);
                                }
                                return u.unmarshal((Source)source, type).getValue();
                            }
                            catch (UnmarshalException ex) {
                                throw new ExtractorContainerException(ImplMessages.ERROR_UNMARSHALLING_JAXB(type), ex);
                            }
                            catch (JAXBException ex) {
                                throw new ContainerException(ImplMessages.ERROR_UNMARSHALLING_JAXB(type), ex);
                            }
                            catch (Exception ex) {
                                throw new ContainerException(ImplMessages.ERROR_UNMARSHALLING_JAXB(type), ex);
                            }
                        }
                    });
                }
            };
        }
    }
}

