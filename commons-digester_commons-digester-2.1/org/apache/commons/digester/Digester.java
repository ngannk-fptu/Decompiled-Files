/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.digester;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import org.apache.commons.digester.BeanPropertySetterRule;
import org.apache.commons.digester.CallMethodRule;
import org.apache.commons.digester.CallParamRule;
import org.apache.commons.digester.FactoryCreateRule;
import org.apache.commons.digester.ObjectCreateRule;
import org.apache.commons.digester.ObjectCreationFactory;
import org.apache.commons.digester.ObjectParamRule;
import org.apache.commons.digester.ParserFeatureSetterFactory;
import org.apache.commons.digester.PathCallParamRule;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.RuleSet;
import org.apache.commons.digester.Rules;
import org.apache.commons.digester.RulesBase;
import org.apache.commons.digester.SetNestedPropertiesRule;
import org.apache.commons.digester.SetNextRule;
import org.apache.commons.digester.SetPropertiesRule;
import org.apache.commons.digester.SetPropertyRule;
import org.apache.commons.digester.SetRootRule;
import org.apache.commons.digester.SetTopRule;
import org.apache.commons.digester.StackAction;
import org.apache.commons.digester.Substitutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Digester
extends DefaultHandler {
    protected StringBuffer bodyText = new StringBuffer();
    protected Stack<StringBuffer> bodyTexts = new Stack();
    protected Stack<List<Rule>> matches = new Stack();
    protected ClassLoader classLoader = null;
    protected boolean configured = false;
    protected EntityResolver entityResolver;
    protected HashMap<String, URL> entityValidator = new HashMap();
    protected ErrorHandler errorHandler = null;
    protected SAXParserFactory factory = null;
    @Deprecated
    protected String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    protected Locator locator = null;
    protected String match = "";
    protected boolean namespaceAware = false;
    protected HashMap<String, Stack<String>> namespaces = new HashMap();
    protected boolean xincludeAware = false;
    protected Stack<Object> params = new Stack();
    protected SAXParser parser = null;
    protected String publicId = null;
    protected XMLReader reader = null;
    protected Object root = null;
    protected Rules rules = null;
    @Deprecated
    protected String schemaLanguage = "http://www.w3.org/2001/XMLSchema";
    @Deprecated
    protected String schemaLocation = null;
    protected Schema schema = null;
    protected Stack<Object> stack = new Stack();
    protected boolean useContextClassLoader = false;
    protected boolean validating = false;
    protected Log log = LogFactory.getLog((String)"org.apache.commons.digester.Digester");
    protected Log saxLog = LogFactory.getLog((String)"org.apache.commons.digester.Digester.sax");
    protected static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    protected Substitutor substitutor;
    private HashMap<String, Stack<Object>> stacksByName = new HashMap();
    private ContentHandler customContentHandler = null;
    private StackAction stackAction = null;
    protected List<InputSource> inputSources = new ArrayList<InputSource>(5);

    public Digester() {
    }

    public Digester(SAXParser parser) {
        this.parser = parser;
    }

    public Digester(XMLReader reader) {
        this.reader = reader;
    }

    public String findNamespaceURI(String prefix) {
        Stack<String> nsStack = this.namespaces.get(prefix);
        if (nsStack == null) {
            return null;
        }
        try {
            return nsStack.peek();
        }
        catch (EmptyStackException e) {
            return null;
        }
    }

    public ClassLoader getClassLoader() {
        ClassLoader classLoader;
        if (this.classLoader != null) {
            return this.classLoader;
        }
        if (this.useContextClassLoader && (classLoader = Thread.currentThread().getContextClassLoader()) != null) {
            return classLoader;
        }
        return this.getClass().getClassLoader();
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public int getCount() {
        return this.stack.size();
    }

    public String getCurrentElementName() {
        String elementName = this.match;
        int lastSlash = elementName.lastIndexOf(47);
        if (lastSlash >= 0) {
            elementName = elementName.substring(lastSlash + 1);
        }
        return elementName;
    }

    @Deprecated
    public int getDebug() {
        return 0;
    }

    @Deprecated
    public void setDebug(int debug) {
    }

    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public SAXParserFactory getFactory() {
        if (this.factory == null) {
            this.factory = SAXParserFactory.newInstance();
            this.factory.setNamespaceAware(this.namespaceAware);
            this.factory.setXIncludeAware(this.xincludeAware);
            this.factory.setValidating(this.validating);
            this.factory.setSchema(this.schema);
        }
        return this.factory;
    }

    public boolean getFeature(String feature) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        return this.getFactory().getFeature(feature);
    }

    public void setFeature(String feature, boolean value) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        this.getFactory().setFeature(feature, value);
    }

    public Log getLogger() {
        return this.log;
    }

    public void setLogger(Log log) {
        this.log = log;
    }

    public Log getSAXLogger() {
        return this.saxLog;
    }

    public void setSAXLogger(Log saxLog) {
        this.saxLog = saxLog;
    }

    public String getMatch() {
        return this.match;
    }

    public boolean getNamespaceAware() {
        return this.namespaceAware;
    }

    public void setNamespaceAware(boolean namespaceAware) {
        this.namespaceAware = namespaceAware;
    }

    public boolean getXIncludeAware() {
        return this.xincludeAware;
    }

    public void setXIncludeAware(boolean xincludeAware) {
        this.xincludeAware = xincludeAware;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getPublicId() {
        return this.publicId;
    }

    public String getRuleNamespaceURI() {
        return this.getRules().getNamespaceURI();
    }

    public void setRuleNamespaceURI(String ruleNamespaceURI) {
        this.getRules().setNamespaceURI(ruleNamespaceURI);
    }

    public SAXParser getParser() {
        if (this.parser != null) {
            return this.parser;
        }
        try {
            if (this.validating && this.schemaLocation != null) {
                Properties properties = new Properties();
                properties.put("SAXParserFactory", this.getFactory());
                if (this.schemaLocation != null) {
                    properties.put("schemaLocation", this.schemaLocation);
                    properties.put("schemaLanguage", this.schemaLanguage);
                }
                this.parser = ParserFeatureSetterFactory.newSAXParser(properties);
            } else {
                this.parser = this.getFactory().newSAXParser();
            }
        }
        catch (Exception e) {
            this.log.error((Object)"Digester.getParser: ", (Throwable)e);
            return null;
        }
        return this.parser;
    }

    public Object getProperty(String property) throws SAXNotRecognizedException, SAXNotSupportedException {
        return this.getParser().getProperty(property);
    }

    public void setProperty(String property, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        this.getParser().setProperty(property, value);
    }

    @Deprecated
    public XMLReader getReader() {
        try {
            return this.getXMLReader();
        }
        catch (SAXException e) {
            this.log.error((Object)"Cannot get XMLReader", (Throwable)e);
            return null;
        }
    }

    public Rules getRules() {
        if (this.rules == null) {
            this.rules = new RulesBase();
            this.rules.setDigester(this);
        }
        return this.rules;
    }

    public void setRules(Rules rules) {
        this.rules = rules;
        this.rules.setDigester(this);
    }

    @Deprecated
    public String getSchema() {
        return this.schemaLocation;
    }

    @Deprecated
    public void setSchema(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    @Deprecated
    public String getSchemaLanguage() {
        return this.schemaLanguage;
    }

    @Deprecated
    public void setSchemaLanguage(String schemaLanguage) {
        this.schemaLanguage = schemaLanguage;
    }

    public Schema getXMLSchema() {
        return this.schema;
    }

    public void setXMLSchema(Schema schema) {
        this.schema = schema;
    }

    public boolean getUseContextClassLoader() {
        return this.useContextClassLoader;
    }

    public void setUseContextClassLoader(boolean use) {
        this.useContextClassLoader = use;
    }

    public boolean getValidating() {
        return this.validating;
    }

    public void setValidating(boolean validating) {
        this.validating = validating;
    }

    public XMLReader getXMLReader() throws SAXException {
        if (this.reader == null) {
            this.reader = this.getParser().getXMLReader();
        }
        this.reader.setDTDHandler(this);
        this.reader.setContentHandler(this);
        if (this.entityResolver == null) {
            this.reader.setEntityResolver(this);
        } else {
            this.reader.setEntityResolver(this.entityResolver);
        }
        this.reader.setErrorHandler(this);
        return this.reader;
    }

    public Substitutor getSubstitutor() {
        return this.substitutor;
    }

    public void setSubstitutor(Substitutor substitutor) {
        this.substitutor = substitutor;
    }

    public ContentHandler getCustomContentHandler() {
        return this.customContentHandler;
    }

    public void setCustomContentHandler(ContentHandler handler) {
        this.customContentHandler = handler;
    }

    public void setStackAction(StackAction stackAction) {
        this.stackAction = stackAction;
    }

    public StackAction getStackAction() {
        return this.stackAction;
    }

    public Map<String, String> getCurrentNamespaces() {
        if (!this.namespaceAware) {
            this.log.warn((Object)"Digester is not namespace aware");
        }
        HashMap<String, String> currentNamespaces = new HashMap<String, String>();
        for (Map.Entry<String, Stack<String>> nsEntry : this.namespaces.entrySet()) {
            try {
                currentNamespaces.put(nsEntry.getKey(), nsEntry.getValue().peek());
            }
            catch (RuntimeException e) {
                this.log.error((Object)e.getMessage(), (Throwable)e);
                throw e;
            }
        }
        return currentNamespaces;
    }

    @Override
    public void characters(char[] buffer, int start, int length) throws SAXException {
        if (this.customContentHandler != null) {
            this.customContentHandler.characters(buffer, start, length);
            return;
        }
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)("characters(" + new String(buffer, start, length) + ")"));
        }
        this.bodyText.append(buffer, start, length);
    }

    @Override
    public void endDocument() throws SAXException {
        if (this.saxLog.isDebugEnabled()) {
            if (this.getCount() > 1) {
                this.saxLog.debug((Object)("endDocument():  " + this.getCount() + " elements left"));
            } else {
                this.saxLog.debug((Object)"endDocument()");
            }
        }
        for (Rule rule : this.getRules().rules()) {
            try {
                rule.finish();
            }
            catch (Exception e) {
                this.log.error((Object)"Finish event threw exception", (Throwable)e);
                throw this.createSAXException(e);
            }
            catch (Error e) {
                this.log.error((Object)"Finish event threw error", (Throwable)e);
                throw e;
            }
        }
        this.clear();
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        int slash;
        List<Rule> rules;
        String name;
        if (this.customContentHandler != null) {
            this.customContentHandler.endElement(namespaceURI, localName, qName);
            return;
        }
        boolean debug = this.log.isDebugEnabled();
        if (debug) {
            if (this.saxLog.isDebugEnabled()) {
                this.saxLog.debug((Object)("endElement(" + namespaceURI + "," + localName + "," + qName + ")"));
            }
            this.log.debug((Object)("  match='" + this.match + "'"));
            this.log.debug((Object)("  bodyText='" + this.bodyText + "'"));
        }
        if ((name = localName) == null || name.length() < 1) {
            name = qName;
        }
        if ((rules = this.matches.pop()) != null && rules.size() > 0) {
            String bodyText = this.bodyText.toString();
            Substitutor substitutor = this.getSubstitutor();
            if (substitutor != null) {
                bodyText = substitutor.substitute(bodyText);
            }
            for (int i = 0; i < rules.size(); ++i) {
                try {
                    Rule rule = rules.get(i);
                    if (debug) {
                        this.log.debug((Object)("  Fire body() for " + rule));
                    }
                    rule.body(namespaceURI, name, bodyText);
                    continue;
                }
                catch (Exception e) {
                    this.log.error((Object)"Body event threw exception", (Throwable)e);
                    throw this.createSAXException(e);
                }
                catch (Error e) {
                    this.log.error((Object)"Body event threw error", (Throwable)e);
                    throw e;
                }
            }
        } else if (debug) {
            this.log.debug((Object)("  No rules found matching '" + this.match + "'."));
        }
        this.bodyText = this.bodyTexts.pop();
        if (debug) {
            this.log.debug((Object)("  Popping body text '" + this.bodyText.toString() + "'"));
        }
        if (rules != null) {
            for (int i = 0; i < rules.size(); ++i) {
                int j = rules.size() - i - 1;
                try {
                    Rule rule = rules.get(j);
                    if (debug) {
                        this.log.debug((Object)("  Fire end() for " + rule));
                    }
                    rule.end(namespaceURI, name);
                    continue;
                }
                catch (Exception e) {
                    this.log.error((Object)"End event threw exception", (Throwable)e);
                    throw this.createSAXException(e);
                }
                catch (Error e) {
                    this.log.error((Object)"End event threw error", (Throwable)e);
                    throw e;
                }
            }
        }
        this.match = (slash = this.match.lastIndexOf(47)) >= 0 ? this.match.substring(0, slash) : "";
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        Stack<String> stack;
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)("endPrefixMapping(" + prefix + ")"));
        }
        if ((stack = this.namespaces.get(prefix)) == null) {
            return;
        }
        try {
            stack.pop();
            if (stack.empty()) {
                this.namespaces.remove(prefix);
            }
        }
        catch (EmptyStackException e) {
            throw this.createSAXException("endPrefixMapping popped too many times");
        }
    }

    @Override
    public void ignorableWhitespace(char[] buffer, int start, int len) throws SAXException {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)("ignorableWhitespace(" + new String(buffer, start, len) + ")"));
        }
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        if (this.customContentHandler != null) {
            this.customContentHandler.processingInstruction(target, data);
            return;
        }
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)("processingInstruction('" + target + "','" + data + "')"));
        }
    }

    public Locator getDocumentLocator() {
        return this.locator;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)("setDocumentLocator(" + locator + ")"));
        }
        this.locator = locator;
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)("skippedEntity(" + name + ")"));
        }
    }

    @Override
    public void startDocument() throws SAXException {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)"startDocument()");
        }
        this.configure();
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes list) throws SAXException {
        boolean debug = this.log.isDebugEnabled();
        if (this.customContentHandler != null) {
            this.customContentHandler.startElement(namespaceURI, localName, qName, list);
            return;
        }
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)("startElement(" + namespaceURI + "," + localName + "," + qName + ")"));
        }
        this.bodyTexts.push(this.bodyText);
        if (debug) {
            this.log.debug((Object)("  Pushing body text '" + this.bodyText.toString() + "'"));
        }
        this.bodyText = new StringBuffer();
        String name = localName;
        if (name == null || name.length() < 1) {
            name = qName;
        }
        StringBuffer sb = new StringBuffer(this.match);
        if (this.match.length() > 0) {
            sb.append('/');
        }
        sb.append(name);
        this.match = sb.toString();
        if (debug) {
            this.log.debug((Object)("  New match='" + this.match + "'"));
        }
        List<Rule> rules = this.getRules().match(namespaceURI, this.match);
        this.matches.push(rules);
        if (rules != null && rules.size() > 0) {
            Substitutor substitutor = this.getSubstitutor();
            if (substitutor != null) {
                list = substitutor.substitute(list);
            }
            for (int i = 0; i < rules.size(); ++i) {
                try {
                    Rule rule = rules.get(i);
                    if (debug) {
                        this.log.debug((Object)("  Fire begin() for " + rule));
                    }
                    rule.begin(namespaceURI, name, list);
                    continue;
                }
                catch (Exception e) {
                    this.log.error((Object)"Begin event threw exception", (Throwable)e);
                    throw this.createSAXException(e);
                }
                catch (Error e) {
                    this.log.error((Object)"Begin event threw error", (Throwable)e);
                    throw e;
                }
            }
        } else if (debug) {
            this.log.debug((Object)("  No rules found matching '" + this.match + "'."));
        }
    }

    @Override
    public void startPrefixMapping(String prefix, String namespaceURI) throws SAXException {
        Stack<String> stack;
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)("startPrefixMapping(" + prefix + "," + namespaceURI + ")"));
        }
        if ((stack = this.namespaces.get(prefix)) == null) {
            stack = new Stack();
            this.namespaces.put(prefix, stack);
        }
        stack.push(namespaceURI);
    }

    @Override
    public void notationDecl(String name, String publicId, String systemId) {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)("notationDecl(" + name + "," + publicId + "," + systemId + ")"));
        }
    }

    @Override
    public void unparsedEntityDecl(String name, String publicId, String systemId, String notation) {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)("unparsedEntityDecl(" + name + "," + publicId + "," + systemId + "," + notation + ")"));
        }
    }

    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    public EntityResolver getEntityResolver() {
        return this.entityResolver;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)("resolveEntity('" + publicId + "', '" + systemId + "')"));
        }
        if (publicId != null) {
            this.publicId = publicId;
        }
        URL entityURL = null;
        if (publicId != null) {
            entityURL = this.entityValidator.get(publicId);
        }
        if (this.schemaLocation != null && entityURL == null && systemId != null) {
            entityURL = this.entityValidator.get(systemId);
        }
        if (entityURL == null) {
            if (systemId == null) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)" Cannot resolve null entity, returning null InputSource");
                }
                return null;
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)(" Trying to resolve using system ID '" + systemId + "'"));
            }
            try {
                entityURL = new URL(systemId);
            }
            catch (MalformedURLException e) {
                throw new IllegalArgumentException("Malformed URL '" + systemId + "' : " + e.getMessage());
            }
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)(" Resolving to alternate DTD '" + entityURL + "'"));
        }
        try {
            return this.createInputSourceFromURL(entityURL);
        }
        catch (Exception e) {
            throw this.createSAXException(e);
        }
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        this.log.error((Object)("Parse Error at line " + exception.getLineNumber() + " column " + exception.getColumnNumber() + ": " + exception.getMessage()), (Throwable)exception);
        if (this.errorHandler != null) {
            this.errorHandler.error(exception);
        }
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        this.log.error((Object)("Parse Fatal Error at line " + exception.getLineNumber() + " column " + exception.getColumnNumber() + ": " + exception.getMessage()), (Throwable)exception);
        if (this.errorHandler != null) {
            this.errorHandler.fatalError(exception);
        }
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        if (this.errorHandler != null) {
            this.log.warn((Object)("Parse Warning Error at line " + exception.getLineNumber() + " column " + exception.getColumnNumber() + ": " + exception.getMessage()), (Throwable)exception);
            this.errorHandler.warning(exception);
        }
    }

    @Deprecated
    public void log(String message) {
        this.log.info((Object)message);
    }

    @Deprecated
    public void log(String message, Throwable exception) {
        this.log.error((Object)message, exception);
    }

    public Object parse(File file) throws IOException, SAXException {
        if (file == null) {
            throw new IllegalArgumentException("File to parse is null");
        }
        this.configure();
        InputSource input = new InputSource(new FileInputStream(file));
        input.setSystemId(file.toURI().toURL().toString());
        this.getXMLReader().parse(input);
        this.cleanup();
        return this.root;
    }

    public Object parse(InputSource input) throws IOException, SAXException {
        if (input == null) {
            throw new IllegalArgumentException("InputSource to parse is null");
        }
        this.configure();
        this.getXMLReader().parse(input);
        this.cleanup();
        return this.root;
    }

    public Object parse(InputStream input) throws IOException, SAXException {
        if (input == null) {
            throw new IllegalArgumentException("InputStream to parse is null");
        }
        this.configure();
        InputSource is = new InputSource(input);
        this.getXMLReader().parse(is);
        this.cleanup();
        return this.root;
    }

    public Object parse(Reader reader) throws IOException, SAXException {
        if (reader == null) {
            throw new IllegalArgumentException("Reader to parse is null");
        }
        this.configure();
        InputSource is = new InputSource(reader);
        this.getXMLReader().parse(is);
        this.cleanup();
        return this.root;
    }

    public Object parse(String uri) throws IOException, SAXException {
        if (uri == null) {
            throw new IllegalArgumentException("String URI to parse is null");
        }
        this.configure();
        InputSource is = this.createInputSourceFromURL(uri);
        this.getXMLReader().parse(is);
        this.cleanup();
        return this.root;
    }

    public Object parse(URL url) throws IOException, SAXException {
        if (url == null) {
            throw new IllegalArgumentException("URL to parse is null");
        }
        this.configure();
        InputSource is = this.createInputSourceFromURL(url);
        this.getXMLReader().parse(is);
        this.cleanup();
        return this.root;
    }

    public void register(String publicId, URL entityURL) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("register('" + publicId + "', '" + entityURL + "'"));
        }
        this.entityValidator.put(publicId, entityURL);
    }

    public void register(String publicId, String entityURL) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("register('" + publicId + "', '" + entityURL + "'"));
        }
        try {
            this.entityValidator.put(publicId, new URL(entityURL));
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed URL '" + entityURL + "' : " + e.getMessage());
        }
    }

    public InputSource createInputSourceFromURL(URL url) throws MalformedURLException, IOException {
        URLConnection connection = url.openConnection();
        connection.setUseCaches(false);
        InputStream stream = connection.getInputStream();
        InputSource source = new InputSource(stream);
        source.setSystemId(url.toExternalForm());
        this.inputSources.add(source);
        return source;
    }

    public InputSource createInputSourceFromURL(String url) throws MalformedURLException, IOException {
        return this.createInputSourceFromURL(new URL(url));
    }

    public void addRule(String pattern, Rule rule) {
        rule.setDigester(this);
        this.getRules().add(pattern, rule);
    }

    public void addRuleSet(RuleSet ruleSet) {
        String oldNamespaceURI = this.getRuleNamespaceURI();
        String newNamespaceURI = ruleSet.getNamespaceURI();
        if (this.log.isDebugEnabled()) {
            if (newNamespaceURI == null) {
                this.log.debug((Object)"addRuleSet() with no namespace URI");
            } else {
                this.log.debug((Object)("addRuleSet() with namespace URI " + newNamespaceURI));
            }
        }
        this.setRuleNamespaceURI(newNamespaceURI);
        ruleSet.addRuleInstances(this);
        this.setRuleNamespaceURI(oldNamespaceURI);
    }

    public void addBeanPropertySetter(String pattern) {
        this.addRule(pattern, new BeanPropertySetterRule());
    }

    public void addBeanPropertySetter(String pattern, String propertyName) {
        this.addRule(pattern, new BeanPropertySetterRule(propertyName));
    }

    public void addCallMethod(String pattern, String methodName) {
        this.addRule(pattern, new CallMethodRule(methodName));
    }

    public void addCallMethod(String pattern, String methodName, int paramCount) {
        this.addRule(pattern, new CallMethodRule(methodName, paramCount));
    }

    public void addCallMethod(String pattern, String methodName, int paramCount, String[] paramTypes) {
        this.addRule(pattern, new CallMethodRule(methodName, paramCount, paramTypes));
    }

    public void addCallMethod(String pattern, String methodName, int paramCount, Class<?>[] paramTypes) {
        this.addRule(pattern, new CallMethodRule(methodName, paramCount, paramTypes));
    }

    public void addCallParam(String pattern, int paramIndex) {
        this.addRule(pattern, new CallParamRule(paramIndex));
    }

    public void addCallParam(String pattern, int paramIndex, String attributeName) {
        this.addRule(pattern, new CallParamRule(paramIndex, attributeName));
    }

    public void addCallParam(String pattern, int paramIndex, boolean fromStack) {
        this.addRule(pattern, new CallParamRule(paramIndex, fromStack));
    }

    public void addCallParam(String pattern, int paramIndex, int stackIndex) {
        this.addRule(pattern, new CallParamRule(paramIndex, stackIndex));
    }

    public void addCallParamPath(String pattern, int paramIndex) {
        this.addRule(pattern, new PathCallParamRule(paramIndex));
    }

    public void addObjectParam(String pattern, int paramIndex, Object paramObj) {
        this.addRule(pattern, new ObjectParamRule(paramIndex, paramObj));
    }

    public void addFactoryCreate(String pattern, String className) {
        this.addFactoryCreate(pattern, className, false);
    }

    public void addFactoryCreate(String pattern, Class<?> clazz) {
        this.addFactoryCreate(pattern, clazz, false);
    }

    public void addFactoryCreate(String pattern, String className, String attributeName) {
        this.addFactoryCreate(pattern, className, attributeName, false);
    }

    public void addFactoryCreate(String pattern, Class<?> clazz, String attributeName) {
        this.addFactoryCreate(pattern, clazz, attributeName, false);
    }

    public void addFactoryCreate(String pattern, ObjectCreationFactory creationFactory) {
        this.addFactoryCreate(pattern, creationFactory, false);
    }

    public void addFactoryCreate(String pattern, String className, boolean ignoreCreateExceptions) {
        this.addRule(pattern, new FactoryCreateRule(className, ignoreCreateExceptions));
    }

    public void addFactoryCreate(String pattern, Class<?> clazz, boolean ignoreCreateExceptions) {
        this.addRule(pattern, new FactoryCreateRule(clazz, ignoreCreateExceptions));
    }

    public void addFactoryCreate(String pattern, String className, String attributeName, boolean ignoreCreateExceptions) {
        this.addRule(pattern, new FactoryCreateRule(className, attributeName, ignoreCreateExceptions));
    }

    public void addFactoryCreate(String pattern, Class<?> clazz, String attributeName, boolean ignoreCreateExceptions) {
        this.addRule(pattern, new FactoryCreateRule(clazz, attributeName, ignoreCreateExceptions));
    }

    public void addFactoryCreate(String pattern, ObjectCreationFactory creationFactory, boolean ignoreCreateExceptions) {
        creationFactory.setDigester(this);
        this.addRule(pattern, new FactoryCreateRule(creationFactory, ignoreCreateExceptions));
    }

    public void addObjectCreate(String pattern, String className) {
        this.addRule(pattern, new ObjectCreateRule(className));
    }

    public void addObjectCreate(String pattern, Class<?> clazz) {
        this.addRule(pattern, new ObjectCreateRule(clazz));
    }

    public void addObjectCreate(String pattern, String className, String attributeName) {
        this.addRule(pattern, new ObjectCreateRule(className, attributeName));
    }

    public void addObjectCreate(String pattern, String attributeName, Class<?> clazz) {
        this.addRule(pattern, new ObjectCreateRule(attributeName, clazz));
    }

    public void addSetNestedProperties(String pattern) {
        this.addRule(pattern, new SetNestedPropertiesRule());
    }

    public void addSetNestedProperties(String pattern, String elementName, String propertyName) {
        this.addRule(pattern, new SetNestedPropertiesRule(elementName, propertyName));
    }

    public void addSetNestedProperties(String pattern, String[] elementNames, String[] propertyNames) {
        this.addRule(pattern, new SetNestedPropertiesRule(elementNames, propertyNames));
    }

    public void addSetNext(String pattern, String methodName) {
        this.addRule(pattern, new SetNextRule(methodName));
    }

    public void addSetNext(String pattern, String methodName, String paramType) {
        this.addRule(pattern, new SetNextRule(methodName, paramType));
    }

    public void addSetRoot(String pattern, String methodName) {
        this.addRule(pattern, new SetRootRule(methodName));
    }

    public void addSetRoot(String pattern, String methodName, String paramType) {
        this.addRule(pattern, new SetRootRule(methodName, paramType));
    }

    public void addSetProperties(String pattern) {
        this.addRule(pattern, new SetPropertiesRule());
    }

    public void addSetProperties(String pattern, String attributeName, String propertyName) {
        this.addRule(pattern, new SetPropertiesRule(attributeName, propertyName));
    }

    public void addSetProperties(String pattern, String[] attributeNames, String[] propertyNames) {
        this.addRule(pattern, new SetPropertiesRule(attributeNames, propertyNames));
    }

    public void addSetProperty(String pattern, String name, String value) {
        this.addRule(pattern, new SetPropertyRule(name, value));
    }

    public void addSetTop(String pattern, String methodName) {
        this.addRule(pattern, new SetTopRule(methodName));
    }

    public void addSetTop(String pattern, String methodName, String paramType) {
        this.addRule(pattern, new SetTopRule(methodName, paramType));
    }

    public void clear() {
        this.match = "";
        this.bodyTexts.clear();
        this.params.clear();
        this.publicId = null;
        this.stack.clear();
        this.stacksByName.clear();
        this.customContentHandler = null;
    }

    public Object peek() {
        try {
            return this.stack.peek();
        }
        catch (EmptyStackException e) {
            this.log.warn((Object)"Empty stack (returning null)");
            return null;
        }
    }

    public Object peek(int n) {
        int index = this.stack.size() - 1 - n;
        if (index < 0) {
            this.log.warn((Object)"Empty stack (returning null)");
            return null;
        }
        try {
            return this.stack.get(index);
        }
        catch (EmptyStackException e) {
            this.log.warn((Object)"Empty stack (returning null)");
            return null;
        }
    }

    public Object pop() {
        try {
            Object popped = this.stack.pop();
            if (this.stackAction != null) {
                popped = this.stackAction.onPop(this, null, popped);
            }
            return popped;
        }
        catch (EmptyStackException e) {
            this.log.warn((Object)"Empty stack (returning null)");
            return null;
        }
    }

    public void push(Object object) {
        if (this.stackAction != null) {
            object = this.stackAction.onPush(this, null, object);
        }
        if (this.stack.size() == 0) {
            this.root = object;
        }
        this.stack.push(object);
    }

    public void push(String stackName, Object value) {
        Stack<Object> namedStack;
        if (this.stackAction != null) {
            value = this.stackAction.onPush(this, stackName, value);
        }
        if ((namedStack = this.stacksByName.get(stackName)) == null) {
            namedStack = new Stack();
            this.stacksByName.put(stackName, namedStack);
        }
        namedStack.push(value);
    }

    public Object pop(String stackName) {
        Object result = null;
        Stack<Object> namedStack = this.stacksByName.get(stackName);
        if (namedStack == null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Stack '" + stackName + "' is empty"));
            }
            throw new EmptyStackException();
        }
        result = namedStack.pop();
        if (this.stackAction != null) {
            result = this.stackAction.onPop(this, stackName, result);
        }
        return result;
    }

    public Object peek(String stackName) {
        return this.peek(stackName, 0);
    }

    public Object peek(String stackName, int n) {
        Object result = null;
        Stack<Object> namedStack = this.stacksByName.get(stackName);
        if (namedStack == null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Stack '" + stackName + "' is empty"));
            }
            throw new EmptyStackException();
        }
        int index = namedStack.size() - 1 - n;
        if (index < 0) {
            throw new EmptyStackException();
        }
        result = namedStack.get(index);
        return result;
    }

    public boolean isEmpty(String stackName) {
        boolean result = true;
        Stack<Object> namedStack = this.stacksByName.get(stackName);
        if (namedStack != null) {
            result = namedStack.isEmpty();
        }
        return result;
    }

    public Object getRoot() {
        return this.root;
    }

    public void resetRoot() {
        this.root = null;
    }

    protected void cleanup() {
        for (InputSource source : this.inputSources) {
            try {
                source.getByteStream().close();
            }
            catch (IOException iOException) {}
        }
        this.inputSources.clear();
    }

    protected void configure() {
        if (this.configured) {
            return;
        }
        this.initialize();
        this.configured = true;
    }

    protected void initialize() {
    }

    Map<String, URL> getRegistrations() {
        return this.entityValidator;
    }

    @Deprecated
    List<Rule> getRules(String match) {
        return this.getRules().match(match);
    }

    public Object peekParams() {
        try {
            return this.params.peek();
        }
        catch (EmptyStackException e) {
            this.log.warn((Object)"Empty stack (returning null)");
            return null;
        }
    }

    public Object peekParams(int n) {
        int index = this.params.size() - 1 - n;
        if (index < 0) {
            this.log.warn((Object)"Empty stack (returning null)");
            return null;
        }
        try {
            return this.params.get(index);
        }
        catch (EmptyStackException e) {
            this.log.warn((Object)"Empty stack (returning null)");
            return null;
        }
    }

    public Object popParams() {
        try {
            if (this.log.isTraceEnabled()) {
                this.log.trace((Object)"Popping params");
            }
            return this.params.pop();
        }
        catch (EmptyStackException e) {
            this.log.warn((Object)"Empty stack (returning null)");
            return null;
        }
    }

    public void pushParams(Object object) {
        if (this.log.isTraceEnabled()) {
            this.log.trace((Object)"Pushing params");
        }
        this.params.push(object);
    }

    public SAXException createSAXException(String message, Exception e) {
        Throwable t;
        if (e != null && e instanceof InvocationTargetException && (t = ((InvocationTargetException)e).getTargetException()) != null && t instanceof Exception) {
            e = (Exception)t;
        }
        if (this.locator != null) {
            String error = "Error at line " + this.locator.getLineNumber() + " char " + this.locator.getColumnNumber() + ": " + message;
            if (e != null) {
                return new SAXParseException(error, this.locator, e);
            }
            return new SAXParseException(error, this.locator);
        }
        this.log.error((Object)"No Locator!");
        if (e != null) {
            return new SAXException(message, e);
        }
        return new SAXException(message);
    }

    public SAXException createSAXException(Exception e) {
        Throwable t;
        if (e instanceof InvocationTargetException && (t = ((InvocationTargetException)e).getTargetException()) != null && t instanceof Exception) {
            e = (Exception)t;
        }
        return this.createSAXException(e.getMessage(), e);
    }

    public SAXException createSAXException(String message) {
        return this.createSAXException(message, null);
    }
}

