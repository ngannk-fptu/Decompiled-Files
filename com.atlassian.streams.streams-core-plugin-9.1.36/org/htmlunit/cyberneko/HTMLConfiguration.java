/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.htmlunit.cyberneko.HTMLComponent;
import org.htmlunit.cyberneko.HTMLElements;
import org.htmlunit.cyberneko.HTMLErrorReporter;
import org.htmlunit.cyberneko.HTMLScanner;
import org.htmlunit.cyberneko.HTMLTagBalancer;
import org.htmlunit.cyberneko.HTMLTagBalancingListener;
import org.htmlunit.cyberneko.filters.NamespaceBinder;
import org.htmlunit.cyberneko.xerces.util.ParserConfigurationSettings;
import org.htmlunit.cyberneko.xerces.xni.XMLDocumentHandler;
import org.htmlunit.cyberneko.xerces.xni.XNIException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLConfigurationException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLDocumentFilter;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLDocumentSource;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLErrorHandler;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLInputSource;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLParseException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLParserConfiguration;

public class HTMLConfiguration
extends ParserConfigurationSettings
implements XMLParserConfiguration {
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String AUGMENTATIONS = "http://cyberneko.org/html/features/augmentations";
    protected static final String REPORT_ERRORS = "http://cyberneko.org/html/features/report-errors";
    protected static final String SIMPLE_ERROR_FORMAT = "http://cyberneko.org/html/features/report-errors/simple";
    protected static final String NAMES_ELEMS = "http://cyberneko.org/html/properties/names/elems";
    protected static final String NAMES_ATTRS = "http://cyberneko.org/html/properties/names/attrs";
    protected static final String FILTERS = "http://cyberneko.org/html/properties/filters";
    protected static final String ERROR_REPORTER = "http://cyberneko.org/html/properties/error-reporter";
    protected static final String ERROR_DOMAIN = "http://cyberneko.org/html";
    private XMLDocumentHandler documentHandler_;
    private XMLErrorHandler errorHandler_;
    private boolean closeStream_;
    private final List<HTMLComponent> htmlComponents_ = new ArrayList<HTMLComponent>(2);
    private final HTMLScanner documentScanner_ = this.createDocumentScanner();
    private final HTMLTagBalancer tagBalancer_ = new HTMLTagBalancer(this);
    private final NamespaceBinder namespaceBinder_ = new NamespaceBinder(this);
    private final HTMLElements htmlElements_;

    public HTMLConfiguration() {
        this(new HTMLElements());
    }

    public HTMLConfiguration(HTMLElements htmlElements) {
        this.htmlElements_ = htmlElements;
        this.addComponent(this.documentScanner_);
        this.addComponent(this.tagBalancer_);
        this.addComponent(this.namespaceBinder_);
        String[] recognizedFeatures = new String[]{AUGMENTATIONS, NAMESPACES, REPORT_ERRORS, SIMPLE_ERROR_FORMAT};
        this.addRecognizedFeatures(recognizedFeatures);
        this.setFeature(AUGMENTATIONS, false);
        this.setFeature(NAMESPACES, true);
        this.setFeature(REPORT_ERRORS, false);
        this.setFeature(SIMPLE_ERROR_FORMAT, false);
        String[] recognizedProperties = new String[]{NAMES_ELEMS, NAMES_ATTRS, FILTERS, ERROR_REPORTER};
        this.addRecognizedProperties(recognizedProperties);
        this.setProperty(NAMES_ELEMS, "default");
        this.setProperty(NAMES_ATTRS, "lower");
        this.setProperty(ERROR_REPORTER, new ErrorReporter());
    }

    protected HTMLScanner createDocumentScanner() {
        return new HTMLScanner(this);
    }

    public void pushInputSource(XMLInputSource inputSource) {
        this.documentScanner_.pushInputSource(inputSource);
    }

    public void evaluateInputSource(XMLInputSource inputSource) {
        this.documentScanner_.evaluateInputSource(inputSource);
    }

    @Override
    public void setFeature(String featureId, boolean state) throws XMLConfigurationException {
        super.setFeature(featureId, state);
        for (HTMLComponent component : this.htmlComponents_) {
            component.setFeature(featureId, state);
        }
    }

    @Override
    public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
        XMLDocumentFilter[] filters;
        super.setProperty(propertyId, value);
        if (propertyId.equals(FILTERS) && (filters = (XMLDocumentFilter[])this.getProperty(FILTERS)) != null) {
            for (XMLDocumentFilter filter : filters) {
                if (!(filter instanceof HTMLComponent)) continue;
                this.addComponent((HTMLComponent)((Object)filter));
            }
        }
        for (HTMLComponent component : this.htmlComponents_) {
            component.setProperty(propertyId, value);
        }
    }

    @Override
    public void setDocumentHandler(XMLDocumentHandler handler) {
        this.documentHandler_ = handler;
        if (handler instanceof HTMLTagBalancingListener) {
            this.tagBalancer_.setTagBalancingListener((HTMLTagBalancingListener)((Object)handler));
        }
    }

    @Override
    public XMLDocumentHandler getDocumentHandler() {
        return this.documentHandler_;
    }

    @Override
    public void setErrorHandler(XMLErrorHandler handler) {
        this.errorHandler_ = handler;
    }

    @Override
    public XMLErrorHandler getErrorHandler() {
        return this.errorHandler_;
    }

    public HTMLElements getHtmlElements() {
        return this.htmlElements_;
    }

    public List<HTMLComponent> getHtmlComponents() {
        return this.htmlComponents_;
    }

    public HTMLScanner getDocumentScanner() {
        return this.documentScanner_;
    }

    public HTMLTagBalancer getTagBalancer() {
        return this.tagBalancer_;
    }

    public NamespaceBinder getNamespaceBinder() {
        return this.namespaceBinder_;
    }

    @Override
    public void parse(XMLInputSource source) throws XNIException, IOException {
        this.setInputSource(source);
        this.parse(true);
    }

    @Override
    public void setInputSource(XMLInputSource inputSource) throws XMLConfigurationException, IOException {
        this.reset();
        this.closeStream_ = inputSource.getByteStream() == null && inputSource.getCharacterStream() == null;
        this.documentScanner_.setInputSource(inputSource);
    }

    @Override
    public boolean parse(boolean complete) throws XNIException, IOException {
        try {
            boolean more = this.documentScanner_.scanDocument(complete);
            if (!more) {
                this.cleanup();
            }
            return more;
        }
        catch (IOException | XNIException e) {
            this.cleanup();
            throw e;
        }
    }

    @Override
    public void cleanup() {
        this.documentScanner_.cleanup(this.closeStream_);
    }

    protected void addComponent(HTMLComponent component) {
        this.htmlComponents_.add(component);
        String[] features = component.getRecognizedFeatures();
        this.addRecognizedFeatures(features);
        if (features != null) {
            for (String feature : features) {
                Boolean state = component.getFeatureDefault(feature);
                if (state == null) continue;
                this.setFeature(feature, state);
            }
        }
        String[] properties = component.getRecognizedProperties();
        this.addRecognizedProperties(properties);
        if (properties != null) {
            for (String property : properties) {
                Object value = component.getPropertyDefault(property);
                if (value == null) continue;
                this.setProperty(property, value);
            }
        }
    }

    protected void reset() throws XMLConfigurationException {
        for (HTMLComponent component : this.htmlComponents_) {
            component.reset(this);
        }
        XMLDocumentSource lastSource = this.documentScanner_;
        if (this.getFeature(NAMESPACES)) {
            lastSource.setDocumentHandler(this.namespaceBinder_);
            this.namespaceBinder_.setDocumentSource(this.tagBalancer_);
            lastSource = this.namespaceBinder_;
        }
        lastSource.setDocumentHandler(this.tagBalancer_);
        this.tagBalancer_.setDocumentSource(this.documentScanner_);
        lastSource = this.tagBalancer_;
        XMLDocumentFilter[] filters = (XMLDocumentFilter[])this.getProperty(FILTERS);
        if (filters != null) {
            for (XMLDocumentFilter filter : filters) {
                filter.setDocumentSource(lastSource);
                lastSource.setDocumentHandler(filter);
                lastSource = filter;
            }
        }
        lastSource.setDocumentHandler(this.documentHandler_);
    }

    protected class ErrorReporter
    implements HTMLErrorReporter {
        private ResourceBundle errorMessages_;

        protected ErrorReporter() {
        }

        @Override
        public String formatMessage(String key, Object[] args) {
            if (!HTMLConfiguration.this.getFeature(HTMLConfiguration.SIMPLE_ERROR_FORMAT)) {
                if (this.errorMessages_ == null) {
                    this.errorMessages_ = ResourceBundle.getBundle("org/htmlunit/cyberneko/res/ErrorMessages");
                }
                try {
                    String value = this.errorMessages_.getString(key);
                    return MessageFormat.format(value, args);
                }
                catch (MissingResourceException missingResourceException) {
                    // empty catch block
                }
            }
            return this.formatSimpleMessage(key, args);
        }

        @Override
        public void reportWarning(String key, Object[] args) throws XMLParseException {
            if (HTMLConfiguration.this.errorHandler_ != null) {
                HTMLConfiguration.this.errorHandler_.warning(HTMLConfiguration.ERROR_DOMAIN, key, this.createException(key, args));
            }
        }

        @Override
        public void reportError(String key, Object[] args) throws XMLParseException {
            if (HTMLConfiguration.this.errorHandler_ != null) {
                HTMLConfiguration.this.errorHandler_.error(HTMLConfiguration.ERROR_DOMAIN, key, this.createException(key, args));
            }
        }

        protected XMLParseException createException(String key, Object[] args) {
            String message = this.formatMessage(key, args);
            return new XMLParseException(HTMLConfiguration.this.documentScanner_, message);
        }

        protected String formatSimpleMessage(String key, Object[] args) {
            StringBuilder str = new StringBuilder();
            str.append(HTMLConfiguration.ERROR_DOMAIN);
            str.append('#');
            str.append(key);
            if (args != null && args.length > 0) {
                str.append('\t');
                for (int i = 0; i < args.length; ++i) {
                    if (i > 0) {
                        str.append('\t');
                    }
                    str.append(args[i]);
                }
            }
            return str.toString();
        }
    }
}

