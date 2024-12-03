/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.util.ParserConfigurationSettings
 *  org.apache.xerces.xni.XMLDTDContentModelHandler
 *  org.apache.xerces.xni.XMLDTDHandler
 *  org.apache.xerces.xni.XMLDocumentHandler
 *  org.apache.xerces.xni.XMLLocator
 *  org.apache.xerces.xni.XNIException
 *  org.apache.xerces.xni.parser.XMLComponentManager
 *  org.apache.xerces.xni.parser.XMLConfigurationException
 *  org.apache.xerces.xni.parser.XMLDocumentFilter
 *  org.apache.xerces.xni.parser.XMLDocumentSource
 *  org.apache.xerces.xni.parser.XMLEntityResolver
 *  org.apache.xerces.xni.parser.XMLErrorHandler
 *  org.apache.xerces.xni.parser.XMLInputSource
 *  org.apache.xerces.xni.parser.XMLParseException
 *  org.apache.xerces.xni.parser.XMLPullParserConfiguration
 */
package net.sourceforge.htmlunit.cyberneko;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import net.sourceforge.htmlunit.cyberneko.HTMLComponent;
import net.sourceforge.htmlunit.cyberneko.HTMLElements;
import net.sourceforge.htmlunit.cyberneko.HTMLErrorReporter;
import net.sourceforge.htmlunit.cyberneko.HTMLScanner;
import net.sourceforge.htmlunit.cyberneko.HTMLTagBalancer;
import net.sourceforge.htmlunit.cyberneko.HTMLTagBalancingListener;
import net.sourceforge.htmlunit.cyberneko.filters.NamespaceBinder;
import org.apache.xerces.util.ParserConfigurationSettings;
import org.apache.xerces.xni.XMLDTDContentModelHandler;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParseException;
import org.apache.xerces.xni.parser.XMLPullParserConfiguration;

public class HTMLConfiguration
extends ParserConfigurationSettings
implements XMLPullParserConfiguration {
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String AUGMENTATIONS = "http://cyberneko.org/html/features/augmentations";
    protected static final String REPORT_ERRORS = "http://cyberneko.org/html/features/report-errors";
    protected static final String SIMPLE_ERROR_FORMAT = "http://cyberneko.org/html/features/report-errors/simple";
    protected static final String NAMES_ELEMS = "http://cyberneko.org/html/properties/names/elems";
    protected static final String NAMES_ATTRS = "http://cyberneko.org/html/properties/names/attrs";
    protected static final String FILTERS = "http://cyberneko.org/html/properties/filters";
    protected static final String ERROR_REPORTER = "http://cyberneko.org/html/properties/error-reporter";
    protected static final String ERROR_DOMAIN = "http://cyberneko.org/html";
    protected XMLDocumentHandler fDocumentHandler;
    protected XMLDTDHandler fDTDHandler;
    protected XMLDTDContentModelHandler fDTDContentModelHandler;
    protected XMLErrorHandler fErrorHandler;
    protected XMLEntityResolver fEntityResolver;
    protected Locale fLocale = Locale.getDefault();
    protected boolean fCloseStream;
    protected final List<HTMLComponent> fHTMLComponents = new ArrayList<HTMLComponent>(2);
    protected final HTMLScanner fDocumentScanner = this.createDocumentScanner();
    protected final HTMLTagBalancer fTagBalancer = new HTMLTagBalancer(this);
    protected final NamespaceBinder fNamespaceBinder = new NamespaceBinder(this);
    protected final HTMLErrorReporter fErrorReporter = new ErrorReporter();
    public final HTMLElements htmlElements_;

    public HTMLConfiguration() {
        this(new HTMLElements());
    }

    public HTMLConfiguration(HTMLElements htmlElements) {
        this.htmlElements_ = htmlElements;
        this.addComponent(this.fDocumentScanner);
        this.addComponent(this.fTagBalancer);
        this.addComponent(this.fNamespaceBinder);
        String VALIDATION = "http://xml.org/sax/features/validation";
        String[] recognizedFeatures = new String[]{AUGMENTATIONS, NAMESPACES, "http://xml.org/sax/features/validation", REPORT_ERRORS, SIMPLE_ERROR_FORMAT};
        this.addRecognizedFeatures(recognizedFeatures);
        this.setFeature(AUGMENTATIONS, false);
        this.setFeature(NAMESPACES, true);
        this.setFeature("http://xml.org/sax/features/validation", false);
        this.setFeature(REPORT_ERRORS, false);
        this.setFeature(SIMPLE_ERROR_FORMAT, false);
        String[] recognizedProperties = new String[]{NAMES_ELEMS, NAMES_ATTRS, FILTERS, ERROR_REPORTER};
        this.addRecognizedProperties(recognizedProperties);
        this.setProperty(NAMES_ELEMS, "default");
        this.setProperty(NAMES_ATTRS, "lower");
        this.setProperty(ERROR_REPORTER, this.fErrorReporter);
    }

    protected HTMLScanner createDocumentScanner() {
        return new HTMLScanner(this);
    }

    public void pushInputSource(XMLInputSource inputSource) {
        this.fDocumentScanner.pushInputSource(inputSource);
    }

    public void evaluateInputSource(XMLInputSource inputSource) {
        this.fDocumentScanner.evaluateInputSource(inputSource);
    }

    public void setFeature(String featureId, boolean state) throws XMLConfigurationException {
        super.setFeature(featureId, state);
        int size = this.fHTMLComponents.size();
        for (int i = 0; i < size; ++i) {
            HTMLComponent component = this.fHTMLComponents.get(i);
            component.setFeature(featureId, state);
        }
    }

    public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
        XMLDocumentFilter[] filters;
        super.setProperty(propertyId, value);
        if (propertyId.equals(FILTERS) && (filters = (XMLDocumentFilter[])this.getProperty(FILTERS)) != null) {
            for (XMLDocumentFilter filter : filters) {
                if (!(filter instanceof HTMLComponent)) continue;
                this.addComponent((HTMLComponent)filter);
            }
        }
        int size = this.fHTMLComponents.size();
        for (int i = 0; i < size; ++i) {
            HTMLComponent component = this.fHTMLComponents.get(i);
            component.setProperty(propertyId, value);
        }
    }

    public void setDocumentHandler(XMLDocumentHandler handler) {
        this.fDocumentHandler = handler;
        if (handler instanceof HTMLTagBalancingListener) {
            this.fTagBalancer.setTagBalancingListener((HTMLTagBalancingListener)handler);
        }
    }

    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }

    public void setDTDHandler(XMLDTDHandler handler) {
        this.fDTDHandler = handler;
    }

    public XMLDTDHandler getDTDHandler() {
        return this.fDTDHandler;
    }

    public void setDTDContentModelHandler(XMLDTDContentModelHandler handler) {
        this.fDTDContentModelHandler = handler;
    }

    public XMLDTDContentModelHandler getDTDContentModelHandler() {
        return this.fDTDContentModelHandler;
    }

    public void setErrorHandler(XMLErrorHandler handler) {
        this.fErrorHandler = handler;
    }

    public XMLErrorHandler getErrorHandler() {
        return this.fErrorHandler;
    }

    public void setEntityResolver(XMLEntityResolver resolver) {
        this.fEntityResolver = resolver;
    }

    public XMLEntityResolver getEntityResolver() {
        return this.fEntityResolver;
    }

    public void setLocale(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        this.fLocale = locale;
    }

    public Locale getLocale() {
        return this.fLocale;
    }

    public void parse(XMLInputSource source) throws XNIException, IOException {
        this.setInputSource(source);
        this.parse(true);
    }

    public void setInputSource(XMLInputSource inputSource) throws XMLConfigurationException, IOException {
        this.reset();
        this.fCloseStream = inputSource.getByteStream() == null && inputSource.getCharacterStream() == null;
        this.fDocumentScanner.setInputSource(inputSource);
    }

    public boolean parse(boolean complete) throws XNIException, IOException {
        try {
            boolean more = this.fDocumentScanner.scanDocument(complete);
            if (!more) {
                this.cleanup();
            }
            return more;
        }
        catch (XNIException e) {
            this.cleanup();
            throw e;
        }
        catch (IOException e) {
            this.cleanup();
            throw e;
        }
    }

    public void cleanup() {
        this.fDocumentScanner.cleanup(this.fCloseStream);
    }

    protected void addComponent(HTMLComponent component) {
        this.fHTMLComponents.add(component);
        String[] features = component.getRecognizedFeatures();
        this.addRecognizedFeatures(features);
        if (features != null) {
            int featureCount = features.length;
            for (int i = 0; i < featureCount; ++i) {
                Boolean state = component.getFeatureDefault(features[i]);
                if (state == null) continue;
                this.setFeature(features[i], state);
            }
        }
        String[] properties = component.getRecognizedProperties();
        this.addRecognizedProperties(properties);
        if (properties != null) {
            int propertyCount = properties.length;
            for (int i = 0; i < propertyCount; ++i) {
                Object value = component.getPropertyDefault(properties[i]);
                if (value == null) continue;
                this.setProperty(properties[i], value);
            }
        }
    }

    protected void reset() throws XMLConfigurationException {
        int size = this.fHTMLComponents.size();
        for (int i = 0; i < size; ++i) {
            HTMLComponent component = this.fHTMLComponents.get(i);
            component.reset((XMLComponentManager)this);
        }
        HTMLComponent lastSource = this.fDocumentScanner;
        if (this.getFeature(NAMESPACES)) {
            lastSource.setDocumentHandler((XMLDocumentHandler)this.fNamespaceBinder);
            this.fNamespaceBinder.setDocumentSource((XMLDocumentSource)this.fTagBalancer);
            lastSource = this.fNamespaceBinder;
        }
        lastSource.setDocumentHandler((XMLDocumentHandler)this.fTagBalancer);
        this.fTagBalancer.setDocumentSource((XMLDocumentSource)this.fDocumentScanner);
        lastSource = this.fTagBalancer;
        XMLDocumentFilter[] filters = (XMLDocumentFilter[])this.getProperty(FILTERS);
        if (filters != null) {
            for (XMLDocumentFilter filter : filters) {
                filter.setDocumentSource((XMLDocumentSource)lastSource);
                lastSource.setDocumentHandler((XMLDocumentHandler)filter);
                lastSource = filter;
            }
        }
        lastSource.setDocumentHandler(this.fDocumentHandler);
    }

    protected class ErrorReporter
    implements HTMLErrorReporter {
        protected Locale fLastLocale;
        protected ResourceBundle fErrorMessages;

        protected ErrorReporter() {
        }

        @Override
        public String formatMessage(String key, Object[] args) {
            if (!HTMLConfiguration.this.getFeature(HTMLConfiguration.SIMPLE_ERROR_FORMAT)) {
                if (!HTMLConfiguration.this.fLocale.equals(this.fLastLocale)) {
                    this.fErrorMessages = null;
                    this.fLastLocale = HTMLConfiguration.this.fLocale;
                }
                if (this.fErrorMessages == null) {
                    this.fErrorMessages = ResourceBundle.getBundle("net/sourceforge/htmlunit/cyberneko/res/ErrorMessages", HTMLConfiguration.this.fLocale);
                }
                try {
                    String value = this.fErrorMessages.getString(key);
                    String message = MessageFormat.format(value, args);
                    return message;
                }
                catch (MissingResourceException missingResourceException) {
                    // empty catch block
                }
            }
            return this.formatSimpleMessage(key, args);
        }

        @Override
        public void reportWarning(String key, Object[] args) throws XMLParseException {
            if (HTMLConfiguration.this.fErrorHandler != null) {
                HTMLConfiguration.this.fErrorHandler.warning(HTMLConfiguration.ERROR_DOMAIN, key, this.createException(key, args));
            }
        }

        @Override
        public void reportError(String key, Object[] args) throws XMLParseException {
            if (HTMLConfiguration.this.fErrorHandler != null) {
                HTMLConfiguration.this.fErrorHandler.error(HTMLConfiguration.ERROR_DOMAIN, key, this.createException(key, args));
            }
        }

        protected XMLParseException createException(String key, Object[] args) {
            String message = this.formatMessage(key, args);
            return new XMLParseException((XMLLocator)HTMLConfiguration.this.fDocumentScanner, message);
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
                    str.append(String.valueOf(args[i]));
                }
            }
            return str.toString();
        }
    }
}

