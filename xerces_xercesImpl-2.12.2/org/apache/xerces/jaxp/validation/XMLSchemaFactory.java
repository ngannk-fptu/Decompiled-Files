/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp.validation;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.stream.XMLEventReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.jaxp.validation.AbstractXMLSchema;
import org.apache.xerces.jaxp.validation.DraconianErrorHandler;
import org.apache.xerces.jaxp.validation.EmptyXMLSchema;
import org.apache.xerces.jaxp.validation.JAXPValidationMessageFormatter;
import org.apache.xerces.jaxp.validation.ReadOnlyGrammarPool;
import org.apache.xerces.jaxp.validation.SimpleXMLSchema;
import org.apache.xerces.jaxp.validation.Util;
import org.apache.xerces.jaxp.validation.WeakReferenceXMLSchema;
import org.apache.xerces.jaxp.validation.XMLSchema;
import org.apache.xerces.util.DOMEntityResolverWrapper;
import org.apache.xerces.util.DOMInputSource;
import org.apache.xerces.util.ErrorHandlerWrapper;
import org.apache.xerces.util.SAXInputSource;
import org.apache.xerces.util.SAXMessageFormatter;
import org.apache.xerces.util.SecurityManager;
import org.apache.xerces.util.StAXInputSource;
import org.apache.xerces.util.XMLGrammarPoolImpl;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

public final class XMLSchemaFactory
extends SchemaFactory {
    private static final String JAXP_SOURCE_FEATURE_PREFIX = "http://javax.xml.transform";
    private static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
    private static final String USE_GRAMMAR_POOL_ONLY = "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only";
    private static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    private final XMLSchemaLoader fXMLSchemaLoader = new XMLSchemaLoader();
    private ErrorHandler fErrorHandler;
    private LSResourceResolver fLSResourceResolver;
    private final DOMEntityResolverWrapper fDOMEntityResolverWrapper;
    private final ErrorHandlerWrapper fErrorHandlerWrapper = new ErrorHandlerWrapper(DraconianErrorHandler.getInstance());
    private SecurityManager fSecurityManager;
    private final XMLGrammarPoolWrapper fXMLGrammarPoolWrapper;
    private boolean fUseGrammarPoolOnly;

    public XMLSchemaFactory() {
        this.fDOMEntityResolverWrapper = new DOMEntityResolverWrapper();
        this.fXMLGrammarPoolWrapper = new XMLGrammarPoolWrapper();
        this.fXMLSchemaLoader.setFeature(SCHEMA_FULL_CHECKING, true);
        this.fXMLSchemaLoader.setProperty(XMLGRAMMAR_POOL, this.fXMLGrammarPoolWrapper);
        this.fXMLSchemaLoader.setEntityResolver(this.fDOMEntityResolverWrapper);
        this.fXMLSchemaLoader.setErrorHandler(this.fErrorHandlerWrapper);
        this.fUseGrammarPoolOnly = true;
    }

    @Override
    public boolean isSchemaLanguageSupported(String string) {
        if (string == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "SchemaLanguageNull", null));
        }
        if (string.length() == 0) {
            throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "SchemaLanguageLengthZero", null));
        }
        return string.equals("http://www.w3.org/2001/XMLSchema") || string.equals(Constants.W3C_XML_SCHEMA10_NS_URI);
    }

    @Override
    public LSResourceResolver getResourceResolver() {
        return this.fLSResourceResolver;
    }

    @Override
    public void setResourceResolver(LSResourceResolver lSResourceResolver) {
        this.fLSResourceResolver = lSResourceResolver;
        this.fDOMEntityResolverWrapper.setEntityResolver(lSResourceResolver);
        this.fXMLSchemaLoader.setEntityResolver(this.fDOMEntityResolverWrapper);
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return this.fErrorHandler;
    }

    @Override
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.fErrorHandler = errorHandler;
        this.fErrorHandlerWrapper.setErrorHandler(errorHandler != null ? errorHandler : DraconianErrorHandler.getInstance());
        this.fXMLSchemaLoader.setErrorHandler(this.fErrorHandlerWrapper);
    }

    @Override
    public Schema newSchema(Source[] sourceArray) throws SAXException {
        Source source;
        Object object;
        int n;
        XMLGrammarPoolImplExtension xMLGrammarPoolImplExtension = new XMLGrammarPoolImplExtension();
        this.fXMLGrammarPoolWrapper.setGrammarPool(xMLGrammarPoolImplExtension);
        XMLInputSource[] xMLInputSourceArray = new XMLInputSource[sourceArray.length];
        for (n = 0; n < sourceArray.length; ++n) {
            String string;
            Object object2;
            object = sourceArray[n];
            if (object instanceof StreamSource) {
                source = (StreamSource)object;
                object2 = ((StreamSource)source).getPublicId();
                string = ((StreamSource)source).getSystemId();
                InputStream inputStream = ((StreamSource)source).getInputStream();
                Reader reader = ((StreamSource)source).getReader();
                XMLInputSource xMLInputSource = new XMLInputSource((String)object2, string, null);
                xMLInputSource.setByteStream(inputStream);
                xMLInputSource.setCharacterStream(reader);
                xMLInputSourceArray[n] = xMLInputSource;
                continue;
            }
            if (object instanceof SAXSource) {
                source = (SAXSource)object;
                object2 = ((SAXSource)source).getInputSource();
                if (object2 == null) {
                    throw new SAXException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "SAXSourceNullInputSource", null));
                }
                xMLInputSourceArray[n] = new SAXInputSource(((SAXSource)source).getXMLReader(), (InputSource)object2);
                continue;
            }
            if (object instanceof DOMSource) {
                source = (DOMSource)object;
                object2 = ((DOMSource)source).getNode();
                string = ((DOMSource)source).getSystemId();
                xMLInputSourceArray[n] = new DOMInputSource((Node)object2, string);
                continue;
            }
            if (object instanceof StAXSource) {
                source = (StAXSource)object;
                object2 = ((StAXSource)source).getXMLEventReader();
                if (object2 != null) {
                    xMLInputSourceArray[n] = new StAXInputSource((XMLEventReader)object2);
                    continue;
                }
                xMLInputSourceArray[n] = new StAXInputSource(((StAXSource)source).getXMLStreamReader());
                continue;
            }
            if (object == null) {
                throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "SchemaSourceArrayMemberNull", null));
            }
            throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "SchemaFactorySourceUnrecognized", new Object[]{object.getClass().getName()}));
        }
        try {
            this.fXMLSchemaLoader.loadGrammar(xMLInputSourceArray);
        }
        catch (XNIException xNIException) {
            throw Util.toSAXException(xNIException);
        }
        catch (IOException iOException) {
            object = new SAXParseException(iOException.getMessage(), null, iOException);
            if (this.fErrorHandler != null) {
                this.fErrorHandler.error((SAXParseException)object);
            }
            throw object;
        }
        this.fXMLGrammarPoolWrapper.setGrammarPool(null);
        n = xMLGrammarPoolImplExtension.getGrammarCount();
        object = null;
        if (this.fUseGrammarPoolOnly) {
            if (n > 1) {
                object = new XMLSchema(new ReadOnlyGrammarPool(xMLGrammarPoolImplExtension));
            } else if (n == 1) {
                source = xMLGrammarPoolImplExtension.retrieveInitialGrammarSet("http://www.w3.org/2001/XMLSchema");
                object = new SimpleXMLSchema((Grammar)((Object)source[0]));
            } else {
                object = new EmptyXMLSchema();
            }
        } else {
            object = new XMLSchema(new ReadOnlyGrammarPool(xMLGrammarPoolImplExtension), false);
        }
        this.propagateFeatures((AbstractXMLSchema)object);
        return object;
    }

    @Override
    public Schema newSchema() throws SAXException {
        WeakReferenceXMLSchema weakReferenceXMLSchema = new WeakReferenceXMLSchema();
        this.propagateFeatures(weakReferenceXMLSchema);
        return weakReferenceXMLSchema;
    }

    public Schema newSchema(XMLGrammarPool xMLGrammarPool) throws SAXException {
        XMLSchema xMLSchema = this.fUseGrammarPoolOnly ? new XMLSchema(new ReadOnlyGrammarPool(xMLGrammarPool)) : new XMLSchema(xMLGrammarPool, false);
        this.propagateFeatures(xMLSchema);
        return xMLSchema;
    }

    @Override
    public boolean getFeature(String string) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (string == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "FeatureNameNull", null));
        }
        if (string.startsWith(JAXP_SOURCE_FEATURE_PREFIX) && (string.equals("http://javax.xml.transform.stream.StreamSource/feature") || string.equals("http://javax.xml.transform.sax.SAXSource/feature") || string.equals("http://javax.xml.transform.dom.DOMSource/feature") || string.equals("http://javax.xml.transform.stax.StAXSource/feature"))) {
            return true;
        }
        if (string.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            return this.fSecurityManager != null;
        }
        if (string.equals(USE_GRAMMAR_POOL_ONLY)) {
            return this.fUseGrammarPoolOnly;
        }
        try {
            return this.fXMLSchemaLoader.getFeature(string);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            String string2 = xMLConfigurationException.getIdentifier();
            if (xMLConfigurationException.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "feature-not-recognized", new Object[]{string2}));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "feature-not-supported", new Object[]{string2}));
        }
    }

    @Override
    public Object getProperty(String string) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (string == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "ProperyNameNull", null));
        }
        if (string.equals(SECURITY_MANAGER)) {
            return this.fSecurityManager;
        }
        if (string.equals(XMLGRAMMAR_POOL)) {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-supported", new Object[]{string}));
        }
        try {
            return this.fXMLSchemaLoader.getProperty(string);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            String string2 = xMLConfigurationException.getIdentifier();
            if (xMLConfigurationException.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-recognized", new Object[]{string2}));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-supported", new Object[]{string2}));
        }
    }

    @Override
    public void setFeature(String string, boolean bl) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (string == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "FeatureNameNull", null));
        }
        if (string.startsWith(JAXP_SOURCE_FEATURE_PREFIX) && (string.equals("http://javax.xml.transform.stream.StreamSource/feature") || string.equals("http://javax.xml.transform.sax.SAXSource/feature") || string.equals("http://javax.xml.transform.dom.DOMSource/feature") || string.equals("http://javax.xml.transform.stax.StAXSource/feature"))) {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "feature-read-only", new Object[]{string}));
        }
        if (string.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            this.fSecurityManager = bl ? new SecurityManager() : null;
            this.fXMLSchemaLoader.setProperty(SECURITY_MANAGER, this.fSecurityManager);
            return;
        }
        if (string.equals(USE_GRAMMAR_POOL_ONLY)) {
            this.fUseGrammarPoolOnly = bl;
            return;
        }
        try {
            this.fXMLSchemaLoader.setFeature(string, bl);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            String string2 = xMLConfigurationException.getIdentifier();
            if (xMLConfigurationException.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "feature-not-recognized", new Object[]{string2}));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "feature-not-supported", new Object[]{string2}));
        }
    }

    @Override
    public void setProperty(String string, Object object) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (string == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "ProperyNameNull", null));
        }
        if (string.equals(SECURITY_MANAGER)) {
            this.fSecurityManager = (SecurityManager)object;
            this.fXMLSchemaLoader.setProperty(SECURITY_MANAGER, this.fSecurityManager);
            return;
        }
        if (string.equals(XMLGRAMMAR_POOL)) {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-supported", new Object[]{string}));
        }
        try {
            this.fXMLSchemaLoader.setProperty(string, object);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            String string2 = xMLConfigurationException.getIdentifier();
            if (xMLConfigurationException.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-recognized", new Object[]{string2}));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fXMLSchemaLoader.getLocale(), "property-not-supported", new Object[]{string2}));
        }
    }

    private void propagateFeatures(AbstractXMLSchema abstractXMLSchema) {
        abstractXMLSchema.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", this.fSecurityManager != null);
        String[] stringArray = this.fXMLSchemaLoader.getRecognizedFeatures();
        for (int i = 0; i < stringArray.length; ++i) {
            boolean bl = this.fXMLSchemaLoader.getFeature(stringArray[i]);
            abstractXMLSchema.setFeature(stringArray[i], bl);
        }
    }

    static class XMLGrammarPoolWrapper
    implements XMLGrammarPool {
        private XMLGrammarPool fGrammarPool;

        XMLGrammarPoolWrapper() {
        }

        @Override
        public Grammar[] retrieveInitialGrammarSet(String string) {
            return this.fGrammarPool.retrieveInitialGrammarSet(string);
        }

        @Override
        public void cacheGrammars(String string, Grammar[] grammarArray) {
            this.fGrammarPool.cacheGrammars(string, grammarArray);
        }

        @Override
        public Grammar retrieveGrammar(XMLGrammarDescription xMLGrammarDescription) {
            return this.fGrammarPool.retrieveGrammar(xMLGrammarDescription);
        }

        @Override
        public void lockPool() {
            this.fGrammarPool.lockPool();
        }

        @Override
        public void unlockPool() {
            this.fGrammarPool.unlockPool();
        }

        @Override
        public void clear() {
            this.fGrammarPool.clear();
        }

        void setGrammarPool(XMLGrammarPool xMLGrammarPool) {
            this.fGrammarPool = xMLGrammarPool;
        }

        XMLGrammarPool getGrammarPool() {
            return this.fGrammarPool;
        }
    }

    static class XMLGrammarPoolImplExtension
    extends XMLGrammarPoolImpl {
        public XMLGrammarPoolImplExtension() {
        }

        public XMLGrammarPoolImplExtension(int n) {
            super(n);
        }

        int getGrammarCount() {
            return this.fGrammarCount;
        }
    }
}

