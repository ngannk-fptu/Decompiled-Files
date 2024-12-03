/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.IntrospectionUtils
 *  org.apache.tomcat.util.IntrospectionUtils$PropertySource
 *  org.apache.tomcat.util.buf.B2CConverter
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.digester;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.digester.ArrayStack;
import org.apache.tomcat.util.digester.CallMethodRule;
import org.apache.tomcat.util.digester.CallParamRule;
import org.apache.tomcat.util.digester.DocumentProperties;
import org.apache.tomcat.util.digester.FactoryCreateRule;
import org.apache.tomcat.util.digester.ObjectCreateRule;
import org.apache.tomcat.util.digester.ObjectCreationFactory;
import org.apache.tomcat.util.digester.Rule;
import org.apache.tomcat.util.digester.RuleSet;
import org.apache.tomcat.util.digester.Rules;
import org.apache.tomcat.util.digester.RulesBase;
import org.apache.tomcat.util.digester.SetNextRule;
import org.apache.tomcat.util.digester.SetPropertiesRule;
import org.apache.tomcat.util.digester.SystemPropertySource;
import org.apache.tomcat.util.res.StringManager;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.ext.EntityResolver2;
import org.xml.sax.ext.Locator2;
import org.xml.sax.helpers.AttributesImpl;

public class Digester
extends DefaultHandler2 {
    protected static IntrospectionUtils.PropertySource[] propertySources;
    private static boolean propertySourcesSet;
    protected static final StringManager sm;
    private static final HashSet<String> generatedClasses;
    private static GeneratedCodeLoader generatedCodeLoader;
    protected IntrospectionUtils.PropertySource[] source;
    protected StringBuilder bodyText = new StringBuilder();
    protected ArrayStack<StringBuilder> bodyTexts = new ArrayStack();
    protected ArrayStack<List<Rule>> matches = new ArrayStack(10);
    protected ClassLoader classLoader = null;
    protected boolean configured = false;
    protected EntityResolver entityResolver;
    protected HashMap<String, String> entityValidator = new HashMap();
    protected ErrorHandler errorHandler = null;
    protected SAXParserFactory factory = null;
    protected Locator locator = null;
    protected String match = "";
    protected boolean namespaceAware = false;
    protected HashMap<String, ArrayStack<String>> namespaces = new HashMap();
    protected ArrayStack<Object> params = new ArrayStack();
    protected SAXParser parser = null;
    protected String publicId = null;
    protected XMLReader reader = null;
    protected Object root = null;
    protected Rules rules = null;
    protected ArrayStack<Object> stack = new ArrayStack();
    protected boolean useContextClassLoader = false;
    protected boolean validating = false;
    protected boolean rulesValidation = false;
    protected Map<Class<?>, List<String>> fakeAttributes = null;
    protected Log log = LogFactory.getLog(Digester.class);
    protected Log saxLog = LogFactory.getLog((String)"org.apache.tomcat.util.digester.Digester.sax");
    protected StringBuilder code = null;
    protected ArrayList<Object> known = new ArrayList();

    public static void setPropertySource(IntrospectionUtils.PropertySource propertySource) {
        if (!propertySourcesSet) {
            propertySources = new IntrospectionUtils.PropertySource[1];
            Digester.propertySources[0] = propertySource;
            propertySourcesSet = true;
        }
    }

    public static void setPropertySource(IntrospectionUtils.PropertySource[] propertySources) {
        if (!propertySourcesSet) {
            Digester.propertySources = propertySources;
            propertySourcesSet = true;
        }
    }

    public static void addGeneratedClass(String className) {
        generatedClasses.add(className);
    }

    public static String[] getGeneratedClasses() {
        return generatedClasses.toArray(new String[0]);
    }

    public static boolean isGeneratedCodeLoaderSet() {
        return generatedCodeLoader != null;
    }

    public static void setGeneratedCodeLoader(GeneratedCodeLoader generatedCodeLoader) {
        if (Digester.generatedCodeLoader == null) {
            Digester.generatedCodeLoader = generatedCodeLoader;
        }
    }

    public static Object loadGeneratedClass(String className) {
        if (generatedCodeLoader != null) {
            return generatedCodeLoader.loadGeneratedCode(className);
        }
        return null;
    }

    public Digester() {
        propertySourcesSet = true;
        ArrayList<Object> sourcesList = new ArrayList<Object>();
        boolean systemPropertySourceFound = false;
        if (propertySources != null) {
            for (IntrospectionUtils.PropertySource source : propertySources) {
                if (source instanceof SystemPropertySource) {
                    systemPropertySourceFound = true;
                }
                sourcesList.add(source);
            }
        }
        if (!systemPropertySourceFound) {
            sourcesList.add(new SystemPropertySource());
        }
        this.source = sourcesList.toArray(new IntrospectionUtils.PropertySource[0]);
    }

    public static void replaceSystemProperties() {
        Log log = LogFactory.getLog(Digester.class);
        if (propertySources != null) {
            Properties properties = System.getProperties();
            Set<String> names = properties.stringPropertyNames();
            for (String name : names) {
                String value = System.getProperty(name);
                if (value == null) continue;
                try {
                    String newValue = IntrospectionUtils.replaceProperties((String)value, null, (IntrospectionUtils.PropertySource[])propertySources, null);
                    if (value.equals(newValue)) continue;
                    System.setProperty(name, newValue);
                }
                catch (Exception e) {
                    log.warn((Object)sm.getString("digester.failedToUpdateSystemProperty", new Object[]{name, value}), (Throwable)e);
                }
            }
        }
    }

    public void startGeneratingCode() {
        this.code = new StringBuilder();
    }

    public void endGeneratingCode() {
        this.code = null;
        this.known.clear();
    }

    public StringBuilder getGeneratedCode() {
        return this.code;
    }

    public void setKnown(Object object) {
        this.known.add(object);
    }

    public String toVariableName(Object object) {
        boolean found = false;
        int pos = 0;
        if (this.known.size() > 0) {
            for (int i = this.known.size() - 1; i >= 0; --i) {
                if (this.known.get(i) != object) continue;
                pos = i;
                found = true;
                break;
            }
        }
        if (!found) {
            pos = this.known.size();
            this.known.add(object);
        }
        return "tc_" + object.getClass().getSimpleName() + "_" + String.valueOf(pos);
    }

    public String findNamespaceURI(String prefix) {
        ArrayStack<String> stack = this.namespaces.get(prefix);
        if (stack == null) {
            return null;
        }
        try {
            return stack.peek();
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

    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public SAXParserFactory getFactory() throws SAXNotRecognizedException, SAXNotSupportedException, ParserConfigurationException {
        if (this.factory == null) {
            this.factory = SAXParserFactory.newInstance();
            this.factory.setNamespaceAware(this.namespaceAware);
            if (this.namespaceAware) {
                this.factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            }
            this.factory.setValidating(this.validating);
            if (this.validating) {
                this.factory.setFeature("http://xml.org/sax/features/validation", true);
                this.factory.setFeature("http://apache.org/xml/features/validation/schema", true);
            }
        }
        return this.factory;
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

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getPublicId() {
        return this.publicId;
    }

    public SAXParser getParser() {
        if (this.parser != null) {
            return this.parser;
        }
        try {
            this.parser = this.getFactory().newSAXParser();
        }
        catch (Exception e) {
            this.log.error((Object)sm.getString("digester.createParserError"), (Throwable)e);
            return null;
        }
        return this.parser;
    }

    public Object getProperty(String property) throws SAXNotRecognizedException, SAXNotSupportedException {
        return this.getParser().getProperty(property);
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

    public boolean getRulesValidation() {
        return this.rulesValidation;
    }

    public void setRulesValidation(boolean rulesValidation) {
        this.rulesValidation = rulesValidation;
    }

    public Map<Class<?>, List<String>> getFakeAttributes() {
        return this.fakeAttributes;
    }

    public boolean isFakeAttribute(Object object, String name) {
        if (this.fakeAttributes == null) {
            return false;
        }
        List<String> result = this.fakeAttributes.get(object.getClass());
        if (result == null) {
            result = this.fakeAttributes.get(Object.class);
        }
        if (result == null) {
            return false;
        }
        return result.contains(name);
    }

    public void setFakeAttributes(Map<Class<?>, List<String>> fakeAttributes) {
        this.fakeAttributes = fakeAttributes;
    }

    public XMLReader getXMLReader() throws SAXException {
        if (this.reader == null) {
            this.reader = this.getParser().getXMLReader();
        }
        this.reader.setDTDHandler(this);
        this.reader.setContentHandler(this);
        EntityResolver entityResolver = this.getEntityResolver();
        if (entityResolver == null) {
            entityResolver = this;
        }
        entityResolver = entityResolver instanceof EntityResolver2 ? new EntityResolver2Wrapper((EntityResolver2)entityResolver, this.source, this.classLoader) : new EntityResolverWrapper(entityResolver, this.source, this.classLoader);
        this.reader.setEntityResolver(entityResolver);
        this.reader.setProperty("http://xml.org/sax/properties/lexical-handler", this);
        this.reader.setErrorHandler(this);
        return this.reader;
    }

    @Override
    public void characters(char[] buffer, int start, int length) throws SAXException {
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
        while (this.getCount() > 1) {
            this.pop();
        }
        for (Rule rule : this.getRules().rules()) {
            try {
                rule.finish();
            }
            catch (Exception e) {
                this.log.error((Object)sm.getString("digester.error.finish"), (Throwable)e);
                throw this.createSAXException(e);
            }
            catch (Error e) {
                this.log.error((Object)sm.getString("digester.error.finish"), (Throwable)e);
                throw e;
            }
        }
        this.clear();
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        int slash;
        List<Rule> rules;
        boolean debug = this.log.isDebugEnabled();
        if (debug) {
            if (this.saxLog.isDebugEnabled()) {
                this.saxLog.debug((Object)("endElement(" + namespaceURI + "," + localName + "," + qName + ")"));
            }
            this.log.debug((Object)("  match='" + this.match + "'"));
            this.log.debug((Object)("  bodyText='" + this.bodyText + "'"));
        }
        this.bodyText = this.updateBodyText(this.bodyText);
        String name = localName;
        if (name == null || name.length() < 1) {
            name = qName;
        }
        if ((rules = this.matches.pop()) != null && rules.size() > 0) {
            String bodyText = this.bodyText.toString().intern();
            for (Rule value : rules) {
                try {
                    Rule rule = value;
                    if (debug) {
                        this.log.debug((Object)("  Fire body() for " + rule));
                    }
                    rule.body(namespaceURI, name, bodyText);
                }
                catch (Exception e) {
                    this.log.error((Object)sm.getString("digester.error.body"), (Throwable)e);
                    throw this.createSAXException(e);
                }
                catch (Error e) {
                    this.log.error((Object)sm.getString("digester.error.body"), (Throwable)e);
                    throw e;
                }
            }
        } else {
            if (debug) {
                this.log.debug((Object)sm.getString("digester.noRulesFound", new Object[]{this.match}));
            }
            if (this.rulesValidation) {
                this.log.warn((Object)sm.getString("digester.noRulesFound", new Object[]{this.match}));
            }
        }
        this.bodyText = this.bodyTexts.pop();
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
                    this.log.error((Object)sm.getString("digester.error.end"), (Throwable)e);
                    throw this.createSAXException(e);
                }
                catch (Error e) {
                    this.log.error((Object)sm.getString("digester.error.end"), (Throwable)e);
                    throw e;
                }
            }
        }
        this.match = (slash = this.match.lastIndexOf(47)) >= 0 ? this.match.substring(0, slash) : "";
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        ArrayStack<String> stack;
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
            throw this.createSAXException(sm.getString("digester.emptyStackError"));
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
        String enc;
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)"startDocument()");
        }
        if (this.locator instanceof Locator2 && this.root instanceof DocumentProperties.Charset && (enc = ((Locator2)this.locator).getEncoding()) != null) {
            try {
                ((DocumentProperties.Charset)this.root).setCharset(B2CConverter.getCharset((String)enc));
            }
            catch (UnsupportedEncodingException e) {
                this.log.warn((Object)sm.getString("digester.encodingInvalid", new Object[]{enc}), (Throwable)e);
            }
        }
        this.configure();
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes list) throws SAXException {
        boolean debug = this.log.isDebugEnabled();
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)("startElement(" + namespaceURI + "," + localName + "," + qName + ")"));
        }
        list = this.updateAttributes(list);
        this.bodyTexts.push(this.bodyText);
        this.bodyText = new StringBuilder();
        String name = localName;
        if (name == null || name.length() < 1) {
            name = qName;
        }
        StringBuilder sb = new StringBuilder(this.match);
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
            for (Rule value : rules) {
                try {
                    Rule rule = value;
                    if (debug) {
                        this.log.debug((Object)("  Fire begin() for " + rule));
                    }
                    rule.begin(namespaceURI, name, list);
                }
                catch (Exception e) {
                    this.log.error((Object)sm.getString("digester.error.begin"), (Throwable)e);
                    throw this.createSAXException(e);
                }
                catch (Error e) {
                    this.log.error((Object)sm.getString("digester.error.begin"), (Throwable)e);
                    throw e;
                }
            }
        } else if (debug) {
            this.log.debug((Object)sm.getString("digester.noRulesFound", new Object[]{this.match}));
        }
    }

    @Override
    public void startPrefixMapping(String prefix, String namespaceURI) throws SAXException {
        ArrayStack<String> stack;
        if (this.saxLog.isDebugEnabled()) {
            this.saxLog.debug((Object)("startPrefixMapping(" + prefix + "," + namespaceURI + ")"));
        }
        if ((stack = this.namespaces.get(prefix)) == null) {
            stack = new ArrayStack();
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
    public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws SAXException, IOException {
        String entityURL;
        block13: {
            if (this.saxLog.isDebugEnabled()) {
                this.saxLog.debug((Object)("resolveEntity('" + publicId + "', '" + systemId + "', '" + baseURI + "')"));
            }
            entityURL = null;
            if (publicId != null) {
                entityURL = this.entityValidator.get(publicId);
            }
            if (entityURL == null) {
                if (systemId == null) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug((Object)(" Cannot resolve entity: '" + publicId + "'"));
                    }
                    return null;
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)(" Trying to resolve using system ID '" + systemId + "'"));
                }
                entityURL = systemId;
                if (baseURI != null) {
                    try {
                        URI uri = new URI(systemId);
                        if (!uri.isAbsolute()) {
                            entityURL = new URI(baseURI).resolve(uri).toString();
                        }
                    }
                    catch (URISyntaxException e) {
                        if (!this.log.isDebugEnabled()) break block13;
                        this.log.debug((Object)("Invalid URI '" + baseURI + "' or '" + systemId + "'"));
                    }
                }
            }
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)(" Resolving to alternate DTD '" + entityURL + "'"));
        }
        try {
            return new InputSource(entityURL);
        }
        catch (Exception e) {
            throw this.createSAXException(e);
        }
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        this.setPublicId(publicId);
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        this.log.error((Object)sm.getString("digester.parseError", new Object[]{exception.getLineNumber(), exception.getColumnNumber()}), (Throwable)exception);
        if (this.errorHandler != null) {
            this.errorHandler.error(exception);
        }
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        this.log.error((Object)sm.getString("digester.parseErrorFatal", new Object[]{exception.getLineNumber(), exception.getColumnNumber()}), (Throwable)exception);
        if (this.errorHandler != null) {
            this.errorHandler.fatalError(exception);
        }
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        this.log.error((Object)sm.getString("digester.parseWarning", new Object[]{exception.getLineNumber(), exception.getColumnNumber(), exception}));
        if (this.errorHandler != null) {
            this.errorHandler.warning(exception);
        }
    }

    public Object parse(File file) throws IOException, SAXException {
        this.configure();
        InputSource input = new InputSource(new FileInputStream(file));
        input.setSystemId("file://" + file.getAbsolutePath());
        this.getXMLReader().parse(input);
        return this.root;
    }

    public Object parse(InputSource input) throws IOException, SAXException {
        this.configure();
        this.getXMLReader().parse(input);
        return this.root;
    }

    public Object parse(InputStream input) throws IOException, SAXException {
        this.configure();
        InputSource is = new InputSource(input);
        this.getXMLReader().parse(is);
        return this.root;
    }

    public void register(String publicId, String entityURL) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("register('" + publicId + "', '" + entityURL + "'"));
        }
        this.entityValidator.put(publicId, entityURL);
    }

    public void addRule(String pattern, Rule rule) {
        rule.setDigester(this);
        this.getRules().add(pattern, rule);
    }

    public void addRuleSet(RuleSet ruleSet) {
        ruleSet.addRuleInstances(this);
    }

    public void addCallMethod(String pattern, String methodName) {
        this.addRule(pattern, new CallMethodRule(methodName));
    }

    public void addCallMethod(String pattern, String methodName, int paramCount) {
        this.addRule(pattern, new CallMethodRule(methodName, paramCount));
    }

    public void addCallParam(String pattern, int paramIndex) {
        this.addRule(pattern, new CallParamRule(paramIndex));
    }

    public void addFactoryCreate(String pattern, ObjectCreationFactory creationFactory, boolean ignoreCreateExceptions) {
        creationFactory.setDigester(this);
        this.addRule(pattern, new FactoryCreateRule(creationFactory, ignoreCreateExceptions));
    }

    public void addObjectCreate(String pattern, String className) {
        this.addRule(pattern, new ObjectCreateRule(className));
    }

    public void addObjectCreate(String pattern, String className, String attributeName) {
        this.addRule(pattern, new ObjectCreateRule(className, attributeName));
    }

    public void addSetNext(String pattern, String methodName, String paramType) {
        this.addRule(pattern, new SetNextRule(methodName, paramType));
    }

    public void addSetProperties(String pattern) {
        this.addRule(pattern, new SetPropertiesRule());
    }

    public void addSetProperties(String pattern, String[] excludes) {
        this.addRule(pattern, new SetPropertiesRule(excludes));
    }

    public void clear() {
        this.match = "";
        this.bodyTexts.clear();
        this.params.clear();
        this.publicId = null;
        this.stack.clear();
        this.log = null;
        this.saxLog = null;
        this.configured = false;
    }

    public void reset() {
        this.root = null;
        this.setErrorHandler(null);
        this.clear();
    }

    public Object peek() {
        try {
            return this.stack.peek();
        }
        catch (EmptyStackException e) {
            this.log.warn((Object)sm.getString("digester.emptyStack"));
            return null;
        }
    }

    public Object peek(int n) {
        try {
            return this.stack.peek(n);
        }
        catch (EmptyStackException e) {
            this.log.warn((Object)sm.getString("digester.emptyStack"));
            return null;
        }
    }

    public Object pop() {
        try {
            return this.stack.pop();
        }
        catch (EmptyStackException e) {
            this.log.warn((Object)sm.getString("digester.emptyStack"));
            return null;
        }
    }

    public void push(Object object) {
        if (this.stack.size() == 0) {
            this.root = object;
        }
        this.stack.push(object);
    }

    public Object getRoot() {
        return this.root;
    }

    protected void configure() {
        if (this.configured) {
            return;
        }
        this.log = LogFactory.getLog((String)"org.apache.tomcat.util.digester.Digester");
        this.saxLog = LogFactory.getLog((String)"org.apache.tomcat.util.digester.Digester.sax");
        this.configured = true;
    }

    public Object peekParams() {
        try {
            return this.params.peek();
        }
        catch (EmptyStackException e) {
            this.log.warn((Object)sm.getString("digester.emptyStack"));
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
            this.log.warn((Object)sm.getString("digester.emptyStack"));
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
        if (e != null && e instanceof InvocationTargetException) {
            Throwable t = e.getCause();
            if (t instanceof ThreadDeath) {
                throw (ThreadDeath)t;
            }
            if (t instanceof VirtualMachineError) {
                throw (VirtualMachineError)t;
            }
            if (t instanceof Exception) {
                e = (Exception)t;
            }
        }
        if (this.locator != null) {
            String error = sm.getString("digester.errorLocation", new Object[]{this.locator.getLineNumber(), this.locator.getColumnNumber(), message});
            if (e != null) {
                return new SAXParseException(error, this.locator, e);
            }
            return new SAXParseException(error, this.locator);
        }
        this.log.error((Object)sm.getString("digester.noLocator"));
        if (e != null) {
            return new SAXException(message, e);
        }
        return new SAXException(message);
    }

    public SAXException createSAXException(Exception e) {
        if (e instanceof InvocationTargetException) {
            Throwable t = e.getCause();
            if (t instanceof ThreadDeath) {
                throw (ThreadDeath)t;
            }
            if (t instanceof VirtualMachineError) {
                throw (VirtualMachineError)t;
            }
            if (t instanceof Exception) {
                e = (Exception)t;
            }
        }
        return this.createSAXException(e.getMessage(), e);
    }

    public SAXException createSAXException(String message) {
        return this.createSAXException(message, null);
    }

    private Attributes updateAttributes(Attributes list) {
        if (list.getLength() == 0) {
            return list;
        }
        AttributesImpl newAttrs = new AttributesImpl(list);
        int nAttributes = newAttrs.getLength();
        for (int i = 0; i < nAttributes; ++i) {
            String value = newAttrs.getValue(i);
            try {
                newAttrs.setValue(i, IntrospectionUtils.replaceProperties((String)value, null, (IntrospectionUtils.PropertySource[])this.source, (ClassLoader)this.getClassLoader()).intern());
                continue;
            }
            catch (Exception e) {
                this.log.warn((Object)sm.getString("digester.failedToUpdateAttributes", new Object[]{newAttrs.getLocalName(i), value}), (Throwable)e);
            }
        }
        return newAttrs;
    }

    private StringBuilder updateBodyText(StringBuilder bodyText) {
        String out;
        String in = bodyText.toString();
        try {
            out = IntrospectionUtils.replaceProperties((String)in, null, (IntrospectionUtils.PropertySource[])this.source, (ClassLoader)this.getClassLoader());
        }
        catch (Exception e) {
            return bodyText;
        }
        if (out == in) {
            return bodyText;
        }
        return new StringBuilder(out);
    }

    static {
        propertySourcesSet = false;
        sm = StringManager.getManager(Digester.class);
        String classNames = System.getProperty("org.apache.tomcat.util.digester.PROPERTY_SOURCE");
        ArrayList<IntrospectionUtils.PropertySource> sourcesList = new ArrayList<IntrospectionUtils.PropertySource>();
        IntrospectionUtils.PropertySource[] sources = null;
        if (classNames != null) {
            StringTokenizer classNamesTokenizer = new StringTokenizer(classNames, ",");
            block2: while (classNamesTokenizer.hasMoreTokens()) {
                ClassLoader[] cls;
                String className = classNamesTokenizer.nextToken().trim();
                for (ClassLoader cl : cls = new ClassLoader[]{Digester.class.getClassLoader(), Thread.currentThread().getContextClassLoader()}) {
                    try {
                        Class<?> clazz = Class.forName(className, true, cl);
                        sourcesList.add((IntrospectionUtils.PropertySource)clazz.getConstructor(new Class[0]).newInstance(new Object[0]));
                        continue block2;
                    }
                    catch (Throwable t) {
                        ExceptionUtils.handleThrowable((Throwable)t);
                        LogFactory.getLog(Digester.class).error((Object)sm.getString("digester.propertySourceLoadError", new Object[]{className}), t);
                    }
                }
            }
            sources = sourcesList.toArray(new IntrospectionUtils.PropertySource[0]);
        }
        if (sources != null) {
            propertySources = sources;
            propertySourcesSet = true;
        }
        if (Boolean.getBoolean("org.apache.tomcat.util.digester.REPLACE_SYSTEM_PROPERTIES")) {
            Digester.replaceSystemProperties();
        }
        generatedClasses = new HashSet();
    }

    public static interface GeneratedCodeLoader {
        public Object loadGeneratedCode(String var1);
    }

    private static class EntityResolver2Wrapper
    extends EntityResolverWrapper
    implements EntityResolver2 {
        private final EntityResolver2 entityResolver2;

        EntityResolver2Wrapper(EntityResolver2 entityResolver, IntrospectionUtils.PropertySource[] source, ClassLoader classLoader) {
            super(entityResolver, source, classLoader);
            this.entityResolver2 = entityResolver;
        }

        @Override
        public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
            name = this.replace(name);
            baseURI = this.replace(baseURI);
            return this.entityResolver2.getExternalSubset(name, baseURI);
        }

        @Override
        public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws SAXException, IOException {
            name = this.replace(name);
            publicId = this.replace(publicId);
            baseURI = this.replace(baseURI);
            systemId = this.replace(systemId);
            return this.entityResolver2.resolveEntity(name, publicId, baseURI, systemId);
        }
    }

    private static class EntityResolverWrapper
    implements EntityResolver {
        private final EntityResolver entityResolver;
        private final IntrospectionUtils.PropertySource[] source;
        private final ClassLoader classLoader;

        EntityResolverWrapper(EntityResolver entityResolver, IntrospectionUtils.PropertySource[] source, ClassLoader classLoader) {
            this.entityResolver = entityResolver;
            this.source = source;
            this.classLoader = classLoader;
        }

        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            publicId = this.replace(publicId);
            systemId = this.replace(systemId);
            return this.entityResolver.resolveEntity(publicId, systemId);
        }

        protected String replace(String input) {
            try {
                return IntrospectionUtils.replaceProperties((String)input, null, (IntrospectionUtils.PropertySource[])this.source, (ClassLoader)this.classLoader);
            }
            catch (Exception e) {
                return input;
            }
        }
    }

    @Deprecated
    public static class EnvironmentPropertySource
    extends org.apache.tomcat.util.digester.EnvironmentPropertySource {
    }
}

