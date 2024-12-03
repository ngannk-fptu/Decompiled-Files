/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.util.DefaultErrorHandler
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
package org.cyberneko.html;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;
import org.apache.xerces.util.DefaultErrorHandler;
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
import org.cyberneko.html.HTMLComponent;
import org.cyberneko.html.HTMLErrorReporter;
import org.cyberneko.html.HTMLScanner;
import org.cyberneko.html.HTMLTagBalancer;
import org.cyberneko.html.HTMLTagBalancingListener;
import org.cyberneko.html.ObjectFactory;
import org.cyberneko.html.filters.NamespaceBinder;
import org.cyberneko.html.xercesbridge.XercesBridge;

public class HTMLConfiguration
extends ParserConfigurationSettings
implements XMLPullParserConfiguration {
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String AUGMENTATIONS = "http://cyberneko.org/html/features/augmentations";
    protected static final String REPORT_ERRORS = "http://cyberneko.org/html/features/report-errors";
    protected static final String SIMPLE_ERROR_FORMAT = "http://cyberneko.org/html/features/report-errors/simple";
    protected static final String BALANCE_TAGS = "http://cyberneko.org/html/features/balance-tags";
    protected static final String NAMES_ELEMS = "http://cyberneko.org/html/properties/names/elems";
    protected static final String NAMES_ATTRS = "http://cyberneko.org/html/properties/names/attrs";
    protected static final String FILTERS = "http://cyberneko.org/html/properties/filters";
    protected static final String ERROR_REPORTER = "http://cyberneko.org/html/properties/error-reporter";
    protected static final String ERROR_DOMAIN = "http://cyberneko.org/html";
    private static final Class[] DOCSOURCE = new Class[]{XMLDocumentSource.class};
    protected XMLDocumentHandler fDocumentHandler;
    protected XMLDTDHandler fDTDHandler;
    protected XMLDTDContentModelHandler fDTDContentModelHandler;
    protected XMLErrorHandler fErrorHandler = new DefaultErrorHandler();
    protected XMLEntityResolver fEntityResolver;
    protected Locale fLocale = Locale.getDefault();
    protected boolean fCloseStream;
    protected final Vector fHTMLComponents = new Vector(2);
    protected final HTMLScanner fDocumentScanner = this.createDocumentScanner();
    protected final HTMLTagBalancer fTagBalancer = new HTMLTagBalancer();
    protected final NamespaceBinder fNamespaceBinder = new NamespaceBinder();
    protected final HTMLErrorReporter fErrorReporter = new ErrorReporter();
    protected static boolean XERCES_2_0_0 = false;
    protected static boolean XERCES_2_0_1 = false;
    protected static boolean XML4J_4_0_x = false;

    public HTMLConfiguration() {
        this.addComponent(this.fDocumentScanner);
        this.addComponent(this.fTagBalancer);
        this.addComponent(this.fNamespaceBinder);
        String VALIDATION = "http://xml.org/sax/features/validation";
        String[] recognizedFeatures = new String[]{AUGMENTATIONS, NAMESPACES, VALIDATION, REPORT_ERRORS, SIMPLE_ERROR_FORMAT, BALANCE_TAGS};
        this.addRecognizedFeatures(recognizedFeatures);
        this.setFeature(AUGMENTATIONS, false);
        this.setFeature(NAMESPACES, true);
        this.setFeature(VALIDATION, false);
        this.setFeature(REPORT_ERRORS, false);
        this.setFeature(SIMPLE_ERROR_FORMAT, false);
        this.setFeature(BALANCE_TAGS, true);
        if (XERCES_2_0_0) {
            recognizedFeatures = new String[]{"http://apache.org/xml/features/scanner/notify-builtin-refs"};
            this.addRecognizedFeatures(recognizedFeatures);
        }
        if (XERCES_2_0_0 || XERCES_2_0_1 || XML4J_4_0_x) {
            recognizedFeatures = new String[]{"http://apache.org/xml/features/validation/schema/normalized-value", "http://apache.org/xml/features/scanner/notify-char-refs"};
            this.addRecognizedFeatures(recognizedFeatures);
        }
        String[] recognizedProperties = new String[]{NAMES_ELEMS, NAMES_ATTRS, FILTERS, ERROR_REPORTER};
        this.addRecognizedProperties(recognizedProperties);
        this.setProperty(NAMES_ELEMS, "upper");
        this.setProperty(NAMES_ATTRS, "lower");
        this.setProperty(ERROR_REPORTER, this.fErrorReporter);
        if (XERCES_2_0_0) {
            String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
            recognizedProperties = new String[]{SYMBOL_TABLE};
            this.addRecognizedProperties(recognizedProperties);
            Object symbolTable = ObjectFactory.createObject("org.apache.xerces.util.SymbolTable", "org.apache.xerces.util.SymbolTable");
            this.setProperty(SYMBOL_TABLE, symbolTable);
        }
    }

    protected HTMLScanner createDocumentScanner() {
        return new HTMLScanner();
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
            HTMLComponent component = (HTMLComponent)this.fHTMLComponents.elementAt(i);
            component.setFeature(featureId, state);
        }
    }

    public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
        int i;
        XMLDocumentFilter[] filters;
        super.setProperty(propertyId, value);
        if (propertyId.equals(FILTERS) && (filters = (XMLDocumentFilter[])this.getProperty(FILTERS)) != null) {
            for (i = 0; i < filters.length; ++i) {
                XMLDocumentFilter filter = filters[i];
                if (!(filter instanceof HTMLComponent)) continue;
                this.addComponent((HTMLComponent)filter);
            }
        }
        int size = this.fHTMLComponents.size();
        for (i = 0; i < size; ++i) {
            HTMLComponent component = (HTMLComponent)this.fHTMLComponents.elementAt(i);
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
        this.fHTMLComponents.addElement(component);
        String[] features = component.getRecognizedFeatures();
        this.addRecognizedFeatures(features);
        int featureCount = features != null ? features.length : 0;
        for (int i = 0; i < featureCount; ++i) {
            Boolean state = component.getFeatureDefault(features[i]);
            if (state == null) continue;
            this.setFeature(features[i], state);
        }
        String[] properties = component.getRecognizedProperties();
        this.addRecognizedProperties(properties);
        int propertyCount = properties != null ? properties.length : 0;
        for (int i = 0; i < propertyCount; ++i) {
            Object value = component.getPropertyDefault(properties[i]);
            if (value == null) continue;
            this.setProperty(properties[i], value);
        }
    }

    protected void reset() throws XMLConfigurationException {
        XMLDocumentFilter[] filters;
        int size = this.fHTMLComponents.size();
        for (int i = 0; i < size; ++i) {
            HTMLComponent component = (HTMLComponent)this.fHTMLComponents.elementAt(i);
            component.reset((XMLComponentManager)this);
        }
        HTMLComponent lastSource = this.fDocumentScanner;
        if (this.getFeature(NAMESPACES)) {
            lastSource.setDocumentHandler((XMLDocumentHandler)this.fNamespaceBinder);
            this.fNamespaceBinder.setDocumentSource((XMLDocumentSource)this.fTagBalancer);
            lastSource = this.fNamespaceBinder;
        }
        if (this.getFeature(BALANCE_TAGS)) {
            lastSource.setDocumentHandler((XMLDocumentHandler)this.fTagBalancer);
            this.fTagBalancer.setDocumentSource((XMLDocumentSource)this.fDocumentScanner);
            lastSource = this.fTagBalancer;
        }
        if ((filters = (XMLDocumentFilter[])this.getProperty(FILTERS)) != null) {
            for (int i = 0; i < filters.length; ++i) {
                XMLDocumentFilter filter = filters[i];
                XercesBridge.getInstance().XMLDocumentFilter_setDocumentSource(filter, (XMLDocumentSource)lastSource);
                lastSource.setDocumentHandler((XMLDocumentHandler)filter);
                lastSource = filter;
            }
        }
        lastSource.setDocumentHandler(this.fDocumentHandler);
    }

    static {
        try {
            String VERSION = "org.apache.xerces.impl.Version";
            Object version = ObjectFactory.createObject(VERSION, VERSION);
            Field field = version.getClass().getField("fVersion");
            String versionStr = String.valueOf(field.get(version));
            XERCES_2_0_0 = versionStr.equals("Xerces-J 2.0.0");
            XERCES_2_0_1 = versionStr.equals("Xerces-J 2.0.1");
            XML4J_4_0_x = versionStr.startsWith("XML4J 4.0.");
        }
        catch (Throwable throwable) {
            // empty catch block
        }
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
                    this.fErrorMessages = ResourceBundle.getBundle("org/cyberneko/html/res/ErrorMessages", HTMLConfiguration.this.fLocale);
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
            StringBuffer str = new StringBuffer();
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

