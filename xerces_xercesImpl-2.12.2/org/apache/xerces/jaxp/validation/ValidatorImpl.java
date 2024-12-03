/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp.validation;

import java.io.IOException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;
import org.apache.xerces.jaxp.validation.DOMValidatorHelper;
import org.apache.xerces.jaxp.validation.JAXPValidationMessageFormatter;
import org.apache.xerces.jaxp.validation.StAXValidatorHelper;
import org.apache.xerces.jaxp.validation.StreamValidatorHelper;
import org.apache.xerces.jaxp.validation.ValidatorHandlerImpl;
import org.apache.xerces.jaxp.validation.XMLSchemaValidatorComponentManager;
import org.apache.xerces.jaxp.validation.XSGrammarPoolContainer;
import org.apache.xerces.util.SAXMessageFormatter;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xs.AttributePSVI;
import org.apache.xerces.xs.ElementPSVI;
import org.apache.xerces.xs.PSVIProvider;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

final class ValidatorImpl
extends Validator
implements PSVIProvider {
    private static final String JAXP_SOURCE_RESULT_FEATURE_PREFIX = "http://javax.xml.transform";
    private static final String CURRENT_ELEMENT_NODE = "http://apache.org/xml/properties/dom/current-element-node";
    private final XMLSchemaValidatorComponentManager fComponentManager;
    private ValidatorHandlerImpl fSAXValidatorHelper;
    private DOMValidatorHelper fDOMValidatorHelper;
    private StAXValidatorHelper fStAXValidatorHelper;
    private StreamValidatorHelper fStreamValidatorHelper;
    private boolean fConfigurationChanged = false;
    private boolean fErrorHandlerChanged = false;
    private boolean fResourceResolverChanged = false;

    public ValidatorImpl(XSGrammarPoolContainer xSGrammarPoolContainer) {
        this.fComponentManager = new XMLSchemaValidatorComponentManager(xSGrammarPoolContainer);
        this.setErrorHandler(null);
        this.setResourceResolver(null);
    }

    @Override
    public void validate(Source source, Result result) throws SAXException, IOException {
        if (source instanceof SAXSource) {
            if (this.fSAXValidatorHelper == null) {
                this.fSAXValidatorHelper = new ValidatorHandlerImpl(this.fComponentManager);
            }
            this.fSAXValidatorHelper.validate(source, result);
        } else if (source instanceof DOMSource) {
            if (this.fDOMValidatorHelper == null) {
                this.fDOMValidatorHelper = new DOMValidatorHelper(this.fComponentManager);
            }
            this.fDOMValidatorHelper.validate(source, result);
        } else if (source instanceof StAXSource) {
            if (this.fStAXValidatorHelper == null) {
                this.fStAXValidatorHelper = new StAXValidatorHelper(this.fComponentManager);
            }
            this.fStAXValidatorHelper.validate(source, result);
        } else if (source instanceof StreamSource) {
            if (this.fStreamValidatorHelper == null) {
                this.fStreamValidatorHelper = new StreamValidatorHelper(this.fComponentManager);
            }
            this.fStreamValidatorHelper.validate(source, result);
        } else {
            if (source == null) {
                throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceParameterNull", null));
            }
            throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceNotAccepted", new Object[]{source.getClass().getName()}));
        }
    }

    @Override
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.fErrorHandlerChanged = errorHandler != null;
        this.fComponentManager.setErrorHandler(errorHandler);
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return this.fComponentManager.getErrorHandler();
    }

    @Override
    public void setResourceResolver(LSResourceResolver lSResourceResolver) {
        this.fResourceResolverChanged = lSResourceResolver != null;
        this.fComponentManager.setResourceResolver(lSResourceResolver);
    }

    @Override
    public LSResourceResolver getResourceResolver() {
        return this.fComponentManager.getResourceResolver();
    }

    @Override
    public boolean getFeature(String string) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (string == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "FeatureNameNull", null));
        }
        if (string.startsWith(JAXP_SOURCE_RESULT_FEATURE_PREFIX) && (string.equals("http://javax.xml.transform.stream.StreamSource/feature") || string.equals("http://javax.xml.transform.sax.SAXSource/feature") || string.equals("http://javax.xml.transform.dom.DOMSource/feature") || string.equals("http://javax.xml.transform.stax.StAXSource/feature") || string.equals("http://javax.xml.transform.stream.StreamResult/feature") || string.equals("http://javax.xml.transform.sax.SAXResult/feature") || string.equals("http://javax.xml.transform.dom.DOMResult/feature") || string.equals("http://javax.xml.transform.stax.StAXResult/feature"))) {
            return true;
        }
        try {
            return this.fComponentManager.getFeature(string);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            String string2 = xMLConfigurationException.getIdentifier();
            if (xMLConfigurationException.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "feature-not-recognized", new Object[]{string2}));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "feature-not-supported", new Object[]{string2}));
        }
    }

    @Override
    public void setFeature(String string, boolean bl) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (string == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "FeatureNameNull", null));
        }
        if (string.startsWith(JAXP_SOURCE_RESULT_FEATURE_PREFIX) && (string.equals("http://javax.xml.transform.stream.StreamSource/feature") || string.equals("http://javax.xml.transform.sax.SAXSource/feature") || string.equals("http://javax.xml.transform.dom.DOMSource/feature") || string.equals("http://javax.xml.transform.stax.StAXSource/feature") || string.equals("http://javax.xml.transform.stream.StreamResult/feature") || string.equals("http://javax.xml.transform.sax.SAXResult/feature") || string.equals("http://javax.xml.transform.dom.DOMResult/feature") || string.equals("http://javax.xml.transform.stax.StAXResult/feature"))) {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "feature-read-only", new Object[]{string}));
        }
        try {
            this.fComponentManager.setFeature(string, bl);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            String string2 = xMLConfigurationException.getIdentifier();
            if (xMLConfigurationException.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "feature-not-recognized", new Object[]{string2}));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "feature-not-supported", new Object[]{string2}));
        }
        this.fConfigurationChanged = true;
    }

    @Override
    public Object getProperty(String string) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (string == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "ProperyNameNull", null));
        }
        if (CURRENT_ELEMENT_NODE.equals(string)) {
            return this.fDOMValidatorHelper != null ? this.fDOMValidatorHelper.getCurrentElement() : null;
        }
        try {
            return this.fComponentManager.getProperty(string);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            String string2 = xMLConfigurationException.getIdentifier();
            if (xMLConfigurationException.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "property-not-recognized", new Object[]{string2}));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "property-not-supported", new Object[]{string2}));
        }
    }

    @Override
    public void setProperty(String string, Object object) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (string == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "ProperyNameNull", null));
        }
        if (CURRENT_ELEMENT_NODE.equals(string)) {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "property-read-only", new Object[]{string}));
        }
        try {
            this.fComponentManager.setProperty(string, object);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            String string2 = xMLConfigurationException.getIdentifier();
            if (xMLConfigurationException.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "property-not-recognized", new Object[]{string2}));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "property-not-supported", new Object[]{string2}));
        }
        this.fConfigurationChanged = true;
    }

    @Override
    public void reset() {
        if (this.fConfigurationChanged) {
            this.fComponentManager.restoreInitialState();
            this.setErrorHandler(null);
            this.setResourceResolver(null);
            this.fConfigurationChanged = false;
            this.fErrorHandlerChanged = false;
            this.fResourceResolverChanged = false;
        } else {
            if (this.fErrorHandlerChanged) {
                this.setErrorHandler(null);
                this.fErrorHandlerChanged = false;
            }
            if (this.fResourceResolverChanged) {
                this.setResourceResolver(null);
                this.fResourceResolverChanged = false;
            }
        }
    }

    @Override
    public ElementPSVI getElementPSVI() {
        return this.fSAXValidatorHelper != null ? this.fSAXValidatorHelper.getElementPSVI() : null;
    }

    @Override
    public AttributePSVI getAttributePSVI(int n) {
        return this.fSAXValidatorHelper != null ? this.fSAXValidatorHelper.getAttributePSVI(n) : null;
    }

    @Override
    public AttributePSVI getAttributePSVIByName(String string, String string2) {
        return this.fSAXValidatorHelper != null ? this.fSAXValidatorHelper.getAttributePSVIByName(string, string2) : null;
    }
}

