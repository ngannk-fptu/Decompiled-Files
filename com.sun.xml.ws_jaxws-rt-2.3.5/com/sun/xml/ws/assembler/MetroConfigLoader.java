/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.logging.Logger
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.Unmarshaller
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.assembler;

import com.sun.istack.NotNull;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.ResourceLoader;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.assembler.MetroConfigName;
import com.sun.xml.ws.resources.TubelineassemblyMessages;
import com.sun.xml.ws.runtime.config.MetroConfig;
import com.sun.xml.ws.runtime.config.TubeFactoryList;
import com.sun.xml.ws.runtime.config.TubelineDefinition;
import com.sun.xml.ws.runtime.config.TubelineMapping;
import com.sun.xml.ws.util.xml.XmlUtil;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.ws.WebServiceException;

class MetroConfigLoader {
    private static final String JAXWS_TUBES_JDK_XML_RESOURCE = "jaxws-tubes-default.xml";
    private static final Logger LOGGER = Logger.getLogger(MetroConfigLoader.class);
    private MetroConfigName defaultTubesConfigNames;
    private static final TubeFactoryListResolver ENDPOINT_SIDE_RESOLVER = new TubeFactoryListResolver(){

        @Override
        public TubeFactoryList getFactories(TubelineDefinition td) {
            return td != null ? td.getEndpointSide() : null;
        }
    };
    private static final TubeFactoryListResolver CLIENT_SIDE_RESOLVER = new TubeFactoryListResolver(){

        @Override
        public TubeFactoryList getFactories(TubelineDefinition td) {
            return td != null ? td.getClientSide() : null;
        }
    };
    private MetroConfig defaultConfig;
    private URL defaultConfigUrl;
    private MetroConfig appConfig;
    private URL appConfigUrl;

    MetroConfigLoader(Container container, MetroConfigName defaultTubesConfigNames) {
        this.defaultTubesConfigNames = defaultTubesConfigNames;
        ResourceLoader spiResourceLoader = null;
        if (container != null) {
            spiResourceLoader = container.getSPI(ResourceLoader.class);
        }
        this.init(container, spiResourceLoader, new MetroConfigUrlLoader(container));
    }

    private void init(Container container, ResourceLoader ... loaders) {
        MetroConfigName mcn;
        String appFileName = null;
        String defaultFileName = null;
        if (container != null && (mcn = container.getSPI(MetroConfigName.class)) != null) {
            appFileName = mcn.getAppFileName();
            defaultFileName = mcn.getDefaultFileName();
        }
        if (appFileName == null) {
            appFileName = this.defaultTubesConfigNames.getAppFileName();
        }
        if (defaultFileName == null) {
            defaultFileName = this.defaultTubesConfigNames.getDefaultFileName();
        }
        this.defaultConfigUrl = MetroConfigLoader.locateResource(defaultFileName, loaders);
        if (this.defaultConfigUrl != null) {
            LOGGER.config(TubelineassemblyMessages.MASM_0002_DEFAULT_CFG_FILE_LOCATED(defaultFileName, this.defaultConfigUrl));
        }
        this.defaultConfig = MetroConfigLoader.loadMetroConfig(this.defaultConfigUrl);
        if (this.defaultConfig == null) {
            throw (IllegalStateException)LOGGER.logSevereException((Throwable)new IllegalStateException(TubelineassemblyMessages.MASM_0003_DEFAULT_CFG_FILE_NOT_LOADED(defaultFileName)));
        }
        if (this.defaultConfig.getTubelines() == null) {
            throw (IllegalStateException)LOGGER.logSevereException((Throwable)new IllegalStateException(TubelineassemblyMessages.MASM_0004_NO_TUBELINES_SECTION_IN_DEFAULT_CFG_FILE(defaultFileName)));
        }
        if (this.defaultConfig.getTubelines().getDefault() == null) {
            throw (IllegalStateException)LOGGER.logSevereException((Throwable)new IllegalStateException(TubelineassemblyMessages.MASM_0005_NO_DEFAULT_TUBELINE_IN_DEFAULT_CFG_FILE(defaultFileName)));
        }
        this.appConfigUrl = MetroConfigLoader.locateResource(appFileName, loaders);
        if (this.appConfigUrl != null) {
            LOGGER.config(TubelineassemblyMessages.MASM_0006_APP_CFG_FILE_LOCATED(this.appConfigUrl));
            this.appConfig = MetroConfigLoader.loadMetroConfig(this.appConfigUrl);
        } else {
            LOGGER.config(TubelineassemblyMessages.MASM_0007_APP_CFG_FILE_NOT_FOUND());
            this.appConfig = null;
        }
    }

    TubeFactoryList getEndpointSideTubeFactories(URI endpointReference) {
        return this.getTubeFactories(endpointReference, ENDPOINT_SIDE_RESOLVER);
    }

    TubeFactoryList getClientSideTubeFactories(URI endpointReference) {
        return this.getTubeFactories(endpointReference, CLIENT_SIDE_RESOLVER);
    }

    private TubeFactoryList getTubeFactories(URI endpointReference, TubeFactoryListResolver resolver) {
        TubeFactoryList list;
        if (this.appConfig != null && this.appConfig.getTubelines() != null) {
            TubeFactoryList list2;
            for (TubelineMapping mapping : this.appConfig.getTubelines().getTubelineMappings()) {
                if (!mapping.getEndpointRef().equals(endpointReference.toString())) continue;
                list = resolver.getFactories(this.getTubeline(this.appConfig, MetroConfigLoader.resolveReference(mapping.getTubelineRef())));
                if (list == null) break;
                return list;
            }
            if (this.appConfig.getTubelines().getDefault() != null && (list2 = resolver.getFactories(this.getTubeline(this.appConfig, MetroConfigLoader.resolveReference(this.appConfig.getTubelines().getDefault())))) != null) {
                return list2;
            }
        }
        for (TubelineMapping mapping : this.defaultConfig.getTubelines().getTubelineMappings()) {
            if (!mapping.getEndpointRef().equals(endpointReference.toString())) continue;
            list = resolver.getFactories(this.getTubeline(this.defaultConfig, MetroConfigLoader.resolveReference(mapping.getTubelineRef())));
            if (list == null) break;
            return list;
        }
        return resolver.getFactories(this.getTubeline(this.defaultConfig, MetroConfigLoader.resolveReference(this.defaultConfig.getTubelines().getDefault())));
    }

    TubelineDefinition getTubeline(MetroConfig config, URI tubelineDefinitionUri) {
        if (config != null && config.getTubelines() != null) {
            for (TubelineDefinition td : config.getTubelines().getTubelineDefinitions()) {
                if (!td.getName().equals(tubelineDefinitionUri.getFragment())) continue;
                return td;
            }
        }
        return null;
    }

    private static URI resolveReference(String reference) {
        try {
            return new URI(reference);
        }
        catch (URISyntaxException ex) {
            throw (WebServiceException)LOGGER.logSevereException((Throwable)new WebServiceException(TubelineassemblyMessages.MASM_0008_INVALID_URI_REFERENCE(reference), (Throwable)ex));
        }
    }

    private static URL locateResource(String resource, ResourceLoader loader) {
        if (loader == null) {
            return null;
        }
        try {
            return loader.getResource(resource);
        }
        catch (MalformedURLException ex) {
            LOGGER.severe(TubelineassemblyMessages.MASM_0009_CANNOT_FORM_VALID_URL(resource), (Throwable)ex);
            return null;
        }
    }

    private static URL locateResource(String resource, ResourceLoader[] loaders) {
        for (ResourceLoader loader : loaders) {
            URL url = MetroConfigLoader.locateResource(resource, loader);
            if (url == null) continue;
            return url;
        }
        return null;
    }

    private static MetroConfig loadMetroConfig(@NotNull URL resourceUrl) {
        MetroConfig metroConfig;
        block8: {
            InputStream is = MetroConfigLoader.getConfigInputStream(resourceUrl);
            try {
                JAXBContext jaxbContext = MetroConfigLoader.createJAXBContext();
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                XMLInputFactory factory = XmlUtil.newXMLInputFactory(true);
                JAXBElement configElement = unmarshaller.unmarshal(factory.createXMLStreamReader(is), MetroConfig.class);
                metroConfig = (MetroConfig)configElement.getValue();
                if (is == null) break block8;
            }
            catch (Throwable jaxbContext) {
                try {
                    if (is != null) {
                        try {
                            is.close();
                        }
                        catch (Throwable unmarshaller) {
                            jaxbContext.addSuppressed(unmarshaller);
                        }
                    }
                    throw jaxbContext;
                }
                catch (Exception e) {
                    String message = TubelineassemblyMessages.MASM_0010_ERROR_READING_CFG_FILE_FROM_LOCATION(resourceUrl != null ? resourceUrl.toString() : null);
                    InternalError error = new InternalError(message);
                    LOGGER.logException((Throwable)error, (Throwable)e, Level.SEVERE);
                    throw error;
                }
            }
            is.close();
        }
        return metroConfig;
    }

    private static InputStream getConfigInputStream(URL resourceUrl) throws IOException {
        InputStream is;
        if (resourceUrl != null) {
            is = resourceUrl.openStream();
        } else {
            is = MetroConfigLoader.class.getResourceAsStream(JAXWS_TUBES_JDK_XML_RESOURCE);
            if (is == null) {
                throw (IllegalStateException)LOGGER.logSevereException((Throwable)new IllegalStateException(TubelineassemblyMessages.MASM_0001_DEFAULT_CFG_FILE_NOT_FOUND(JAXWS_TUBES_JDK_XML_RESOURCE)));
            }
        }
        return is;
    }

    private static JAXBContext createJAXBContext() throws Exception {
        if (MetroConfigLoader.isJDKInternal()) {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<JAXBContext>(){

                @Override
                public JAXBContext run() throws Exception {
                    return JAXBContext.newInstance((String)MetroConfig.class.getPackage().getName());
                }
            });
        }
        return JAXBContext.newInstance((String)MetroConfig.class.getPackage().getName());
    }

    private static boolean isJDKInternal() {
        return MetroConfigLoader.class.getName().startsWith("com.sun.xml.internal.ws");
    }

    private static class MetroConfigUrlLoader
    extends ResourceLoader {
        Container container;
        ResourceLoader parentLoader;

        MetroConfigUrlLoader(ResourceLoader parentLoader) {
            this.parentLoader = parentLoader;
        }

        MetroConfigUrlLoader(Container container) {
            this(container != null ? container.getSPI(ResourceLoader.class) : null);
            this.container = container;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public URL getResource(String resource) throws MalformedURLException {
            URL uRL;
            LOGGER.entering(new Object[]{resource});
            URL resourceUrl = null;
            try {
                if (this.parentLoader != null) {
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.fine(TubelineassemblyMessages.MASM_0011_LOADING_RESOURCE(resource, this.parentLoader));
                    }
                    resourceUrl = this.parentLoader.getResource(resource);
                }
                if (resourceUrl == null) {
                    resourceUrl = MetroConfigUrlLoader.loadViaClassLoaders("META-INF/" + resource);
                }
                if (resourceUrl == null && this.container != null) {
                    resourceUrl = this.loadFromServletContext(resource);
                }
                uRL = resourceUrl;
            }
            catch (Throwable throwable) {
                LOGGER.exiting(resourceUrl);
                throw throwable;
            }
            LOGGER.exiting((Object)resourceUrl);
            return uRL;
        }

        private static URL loadViaClassLoaders(String resource) {
            URL resourceUrl = MetroConfigUrlLoader.tryLoadFromClassLoader(resource, Thread.currentThread().getContextClassLoader());
            if (resourceUrl == null && (resourceUrl = MetroConfigUrlLoader.tryLoadFromClassLoader(resource, MetroConfigLoader.class.getClassLoader())) == null) {
                return ClassLoader.getSystemResource(resource);
            }
            return resourceUrl;
        }

        private static URL tryLoadFromClassLoader(String resource, ClassLoader loader) {
            return loader != null ? loader.getResource(resource) : null;
        }

        private URL loadFromServletContext(String resource) throws RuntimeException {
            block6: {
                Object context = null;
                try {
                    Class<?> contextClass = Class.forName("javax.servlet.ServletContext");
                    context = this.container.getSPI(contextClass);
                    if (context != null) {
                        if (LOGGER.isLoggable(Level.FINE)) {
                            LOGGER.fine(TubelineassemblyMessages.MASM_0012_LOADING_VIA_SERVLET_CONTEXT(resource, context));
                        }
                        try {
                            Method method = context.getClass().getMethod("getResource", String.class);
                            method.setAccessible(true);
                            Object result = method.invoke(context, "/WEB-INF/" + resource);
                            return (URL)URL.class.cast(result);
                        }
                        catch (Exception e) {
                            throw (RuntimeException)LOGGER.logSevereException((Throwable)new RuntimeException(TubelineassemblyMessages.MASM_0013_ERROR_INVOKING_SERVLET_CONTEXT_METHOD("getResource()")), (Throwable)e);
                        }
                    }
                }
                catch (ClassNotFoundException e) {
                    if (!LOGGER.isLoggable(Level.FINE)) break block6;
                    LOGGER.fine(TubelineassemblyMessages.MASM_0014_UNABLE_TO_LOAD_CLASS("javax.servlet.ServletContext"));
                }
            }
            return null;
        }
    }

    private static interface TubeFactoryListResolver {
        public TubeFactoryList getFactories(TubelineDefinition var1);
    }
}

