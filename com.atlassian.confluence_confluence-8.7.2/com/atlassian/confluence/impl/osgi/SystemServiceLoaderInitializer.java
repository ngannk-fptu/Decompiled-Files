/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.xml.SecureXmlParserFactory
 *  javax.annotation.PostConstruct
 *  javax.persistence.spi.PersistenceProvider
 *  javax.persistence.spi.PersistenceProviderResolver
 *  javax.persistence.spi.PersistenceProviderResolverHolder
 *  javax.validation.Validation
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.xmlpull.v1.XmlPullParserException
 *  org.xmlpull.v1.XmlPullParserFactory
 */
package com.atlassian.confluence.impl.osgi;

import com.atlassian.security.xml.SecureXmlParserFactory;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceProviderResolver;
import javax.persistence.spi.PersistenceProviderResolverHolder;
import javax.validation.Validation;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class SystemServiceLoaderInitializer {
    private static final Logger log = LoggerFactory.getLogger(SystemServiceLoaderInitializer.class);

    @PostConstruct
    public void loadKnownServiceLoaders() {
        Object[] readerMimeTypes = ImageIO.getReaderMIMETypes();
        Object[] writerMimeTypes = ImageIO.getWriterMIMETypes();
        if (log.isDebugEnabled()) {
            log.debug("ImageIO mime type handlers loaded for reading {} and writing {}", (Object)Arrays.toString(readerMimeTypes), (Object)Arrays.toString(writerMimeTypes));
        }
        try {
            XmlPullParserFactory.newInstance();
        }
        catch (XmlPullParserException e) {
            log.error("Unable to initialize secure XmlPullParserFactory", (Throwable)e);
        }
        try {
            SecureXmlParserFactory.createSAXParserFactory();
            SecureXmlParserFactory.newDocumentBuilderFactory();
            SecureXmlParserFactory.newXmlInputFactory();
        }
        catch (ParserConfigurationException | SAXException e) {
            log.error("Unable to initialize secure SAXParserFactory", (Throwable)e);
        }
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        }
        catch (TransformerConfigurationException e) {
            log.error("Unable to initialize secure TransformerFactory", (Throwable)e);
        }
        this.preloadBeanValidation();
        PersistenceProviderResolverHolder.setPersistenceProviderResolver((PersistenceProviderResolver)new PersistenceProviderResolver(){
            private List<PersistenceProvider> persistenceProviders = PersistenceProviderResolverHolder.getPersistenceProviderResolver().getPersistenceProviders();

            public List<PersistenceProvider> getPersistenceProviders() {
                return this.persistenceProviders;
            }

            public void clearCachedProviders() {
            }
        });
    }

    private void preloadBeanValidation() {
        block2: {
            try {
                Validation.buildDefaultValidatorFactory();
            }
            catch (NoClassDefFoundError e) {
                if (!e.getMessage().contains("javax/el")) break block2;
                throw new RuntimeException("Bean validation is not able to find EL library. Check that you're using Tomcat 8+. You can't run Confluence with Tomcat 7 or earlier.", e);
            }
        }
    }
}

