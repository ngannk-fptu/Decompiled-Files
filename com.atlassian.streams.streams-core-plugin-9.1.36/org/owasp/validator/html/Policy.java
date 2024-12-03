/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.owasp.validator.html;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.apache.commons.lang.StringUtils;
import org.owasp.validator.html.InternalPolicy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.TagMatcher;
import org.owasp.validator.html.model.AntiSamyPattern;
import org.owasp.validator.html.model.Attribute;
import org.owasp.validator.html.model.Property;
import org.owasp.validator.html.model.Tag;
import org.owasp.validator.html.scan.Constants;
import org.owasp.validator.html.util.URIUtils;
import org.owasp.validator.html.util.XMLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class Policy {
    protected static final Logger logger = LoggerFactory.getLogger(Policy.class);
    public static final Pattern ANYTHING_REGEXP = Pattern.compile(".*", 32);
    private static final String POLICY_SCHEMA_URI = "antisamy.xsd";
    protected static final String DEFAULT_POLICY_URI = "antisamy.xml";
    private static final String DEFAULT_ONINVALID = "removeAttribute";
    public static final int DEFAULT_MAX_INPUT_SIZE = 100000;
    public static final int DEFAULT_MAX_STYLESHEET_IMPORTS = 1;
    public static final String OMIT_XML_DECLARATION = "omitXmlDeclaration";
    public static final String OMIT_DOCTYPE_DECLARATION = "omitDoctypeDeclaration";
    @Deprecated
    public static final String USE_XHTML = "useXHTML";
    public static final String FORMAT_OUTPUT = "formatOutput";
    public static final String EMBED_STYLESHEETS = "embedStyleSheets";
    public static final String CONNECTION_TIMEOUT = "connectionTimeout";
    public static final String ANCHORS_NOFOLLOW = "nofollowAnchors";
    public static final String ANCHORS_NOOPENER_NOREFERRER = "noopenerAndNoreferrerAnchors";
    public static final String VALIDATE_PARAM_AS_EMBED = "validateParamAsEmbed";
    public static final String PRESERVE_SPACE = "preserveSpace";
    public static final String PRESERVE_COMMENTS = "preserveComments";
    public static final String ENTITY_ENCODE_INTL_CHARS = "entityEncodeIntlChars";
    public static final String BLOCKS_TO_ISOLATE = "blocksToIsolate";
    public static final String OVERRIDE_XERCES_SERIALISATION_BUG = "fixXercesSerialisationBug";
    public static final String ALLOW_DYNAMIC_ATTRIBUTES = "allowDynamicAttributes";
    public static final String MAX_INPUT_SIZE = "maxInputSize";
    public static final String MAX_STYLESHEET_IMPORTS = "maxStyleSheetImports";
    public static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
    public static final String EXTERNAL_PARAM_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
    public static final String DISALLOW_DOCTYPE_DECL = "http://apache.org/xml/features/disallow-doctype-decl";
    public static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    public static final String ACTION_VALIDATE = "validate";
    public static final String ACTION_FILTER = "filter";
    public static final String ACTION_TRUNCATE = "truncate";
    private final Map<String, AntiSamyPattern> commonRegularExpressions;
    protected final Map<String, Tag> tagRules;
    protected final Map<String, Property> cssRules;
    protected final Map<String, String> directives;
    private final Map<String, Attribute> globalAttributes;
    private final Map<String, Attribute> dynamicAttributes;
    private final TagMatcher allowedEmptyTagsMatcher;
    private final TagMatcher requiresClosingTagsMatcher;
    private static volatile Schema schema = null;
    private static boolean validateSchema = true;
    public static final String VALIDATIONPROPERTY = "owasp.validator.validateschema";

    private static void loadValidateSchemaProperty() {
        String validateProperty = System.getProperty(VALIDATIONPROPERTY);
        if (validateProperty != null) {
            Policy.setSchemaValidation(Boolean.parseBoolean(validateProperty));
            logger.warn("Setting AntiSamy policy schema validation to '" + Policy.getSchemaValidation() + "' because '" + VALIDATIONPROPERTY + "' system property set to: '" + validateProperty + "'. Note: this feature is temporary and will go away in AntiSamy v1.7.0 (~mid/late 2022) when validation will become mandatory.");
        } else {
            validateSchema = true;
        }
    }

    public Tag getTagByLowercaseName(String tagName) {
        return this.tagRules.get(tagName);
    }

    public Property getPropertyByName(String propertyName) {
        return this.cssRules.get(propertyName.toLowerCase());
    }

    @Deprecated
    public static boolean getSchemaValidation() {
        return validateSchema;
    }

    @Deprecated
    public static void setSchemaValidation(boolean enable) {
        validateSchema = enable;
    }

    public static Policy getInstance() throws PolicyException {
        return Policy.getInstance(Policy.class.getClassLoader().getResource(DEFAULT_POLICY_URI));
    }

    public static Policy getInstance(String filename) throws PolicyException {
        File file = new File(filename);
        return Policy.getInstance(file);
    }

    public static Policy getInstance(InputStream inputStream) throws PolicyException {
        String logMsg = "Attempting to load AntiSamy policy from an input stream.";
        if (validateSchema) {
            logger.info("Attempting to load AntiSamy policy from an input stream.");
        } else {
            logger.warn("Attempting to load AntiSamy policy from an input stream.");
        }
        return new InternalPolicy(Policy.getSimpleParseContext(Policy.getTopLevelElement(inputStream)));
    }

    public static Policy getInstance(File file) throws PolicyException {
        try {
            URI uri = file.toURI();
            return Policy.getInstance(uri.toURL());
        }
        catch (IOException e) {
            throw new PolicyException(e);
        }
    }

    public static Policy getInstance(URL url) throws PolicyException {
        String logMsg = "Attempting to load AntiSamy policy from URL: " + url.toString();
        if (validateSchema) {
            logger.info(logMsg);
        } else {
            logger.warn(logMsg);
        }
        return new InternalPolicy(Policy.getParseContext(Policy.getTopLevelElement(url), url));
    }

    protected Policy(ParseContext parseContext) {
        this.allowedEmptyTagsMatcher = new TagMatcher(parseContext.allowedEmptyTags);
        this.requiresClosingTagsMatcher = new TagMatcher(parseContext.requireClosingTags);
        this.commonRegularExpressions = Collections.unmodifiableMap(parseContext.commonRegularExpressions);
        this.tagRules = Collections.unmodifiableMap(parseContext.tagRules);
        this.cssRules = Collections.unmodifiableMap(parseContext.cssRules);
        this.directives = Collections.unmodifiableMap(parseContext.directives);
        this.globalAttributes = Collections.unmodifiableMap(parseContext.globalAttributes);
        this.dynamicAttributes = Collections.unmodifiableMap(parseContext.dynamicAttributes);
    }

    protected Policy(Policy old, Map<String, String> directives, Map<String, Tag> tagRules, Map<String, Property> cssRules) {
        this.allowedEmptyTagsMatcher = old.allowedEmptyTagsMatcher;
        this.requiresClosingTagsMatcher = old.requiresClosingTagsMatcher;
        this.commonRegularExpressions = old.commonRegularExpressions;
        this.tagRules = tagRules;
        this.cssRules = cssRules;
        this.directives = directives;
        this.globalAttributes = old.globalAttributes;
        this.dynamicAttributes = old.dynamicAttributes;
    }

    protected static ParseContext getSimpleParseContext(Element topLevelElement) throws PolicyException {
        ParseContext parseContext = new ParseContext();
        if (Policy.getByTagName(topLevelElement, "include").iterator().hasNext()) {
            throw new IllegalArgumentException("A policy file loaded with an InputStream cannot contain include references");
        }
        Policy.parsePolicy(topLevelElement, parseContext);
        return parseContext;
    }

    protected static ParseContext getParseContext(Element topLevelElement, URL baseUrl) throws PolicyException {
        ParseContext parseContext = new ParseContext();
        for (Element include : Policy.getByTagName(topLevelElement, "include")) {
            String href = XMLUtil.getAttributeValue(include, "href");
            Element includedPolicy = Policy.getPolicy(href, baseUrl);
            Policy.parsePolicy(includedPolicy, parseContext);
        }
        Policy.parsePolicy(topLevelElement, parseContext);
        return parseContext;
    }

    protected static Element getTopLevelElement(final URL baseUrl) throws PolicyException {
        InputSource source = Policy.getSourceFromUrl(baseUrl);
        return Policy.getTopLevelElement(source, new Callable<InputSource>(){

            @Override
            public InputSource call() throws PolicyException {
                return Policy.getSourceFromUrl(baseUrl);
            }
        });
    }

    @SuppressFBWarnings(value={"SECURITY"}, justification="Opening a stream to the provided URL is not a vulnerability because it points to a local JAR file.")
    protected static InputSource getSourceFromUrl(URL baseUrl) throws PolicyException {
        try {
            InputSource source = Policy.resolveEntity(baseUrl.toExternalForm(), baseUrl);
            if (source == null) {
                source = new InputSource(baseUrl.toExternalForm());
                source.setByteStream(baseUrl.openStream());
            } else {
                source.setSystemId(baseUrl.toExternalForm());
            }
            return source;
        }
        catch (IOException | SAXException e) {
            throw new PolicyException(e);
        }
    }

    private static Element getTopLevelElement(InputStream is) throws PolicyException {
        final InputSource source = new InputSource(Policy.toByteArrayStream(is));
        return Policy.getTopLevelElement(source, new Callable<InputSource>(){

            @Override
            public InputSource call() throws IOException {
                source.getByteStream().reset();
                return source;
            }
        });
    }

    protected static Element getTopLevelElement(InputSource source, Callable<InputSource> getResetSource) throws PolicyException {
        Exception thrownException = null;
        try {
            Element element = Policy.getDocumentElementFromSource(source, true);
            return element;
        }
        catch (SAXException e) {
            thrownException = e;
            if (!validateSchema) {
                try {
                    source = getResetSource.call();
                    Element theElement = Policy.getDocumentElementFromSource(source, false);
                    logger.warn("Invalid AntiSamy policy file: " + e.getMessage());
                    Element element = theElement;
                    return element;
                }
                catch (Exception e2) {
                    throw new PolicyException(e2);
                }
            }
            throw new PolicyException(e);
        }
        catch (IOException | ParserConfigurationException e) {
            thrownException = e;
            throw new PolicyException(e);
        }
        finally {
            if (!validateSchema && thrownException == null) {
                logger.warn("XML schema validation is disabled for a valid AntiSamy policy. Please reenable policy validation.");
            }
        }
    }

    private static InputStream toByteArrayStream(InputStream in) throws PolicyException {
        byte[] byteArray;
        try (InputStreamReader reader = new InputStreamReader(in, Charset.forName("UTF8"));){
            int numCharsRead;
            char[] charArray = new char[8192];
            StringBuilder builder = new StringBuilder();
            while ((numCharsRead = ((Reader)reader).read(charArray, 0, charArray.length)) != -1) {
                builder.append(charArray, 0, numCharsRead);
            }
            byteArray = builder.toString().getBytes(Charset.forName("UTF8"));
        }
        catch (IOException ioe) {
            throw new PolicyException(ioe);
        }
        return new ByteArrayInputStream(byteArray);
    }

    private static Element getDocumentElementFromSource(InputSource source, boolean schemaValidationEnabled) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(EXTERNAL_GENERAL_ENTITIES, false);
        dbf.setFeature(EXTERNAL_PARAM_ENTITIES, false);
        dbf.setFeature(DISALLOW_DOCTYPE_DECL, true);
        dbf.setFeature(LOAD_EXTERNAL_DTD, false);
        if (schemaValidationEnabled) {
            Policy.getPolicySchema();
            dbf.setNamespaceAware(true);
            dbf.setSchema(schema);
        }
        DocumentBuilder db = dbf.newDocumentBuilder();
        db.setErrorHandler(new SAXErrorHandler());
        Document dom = db.parse(source);
        return dom.getDocumentElement();
    }

    private static void parsePolicy(Element topLevelElement, ParseContext parseContext) throws PolicyException {
        if (topLevelElement == null) {
            return;
        }
        parseContext.resetParamsWhereLastConfigWins();
        Policy.parseCommonRegExps(Policy.getFirstChild(topLevelElement, "common-regexps"), parseContext.commonRegularExpressions);
        Policy.parseDirectives(Policy.getFirstChild(topLevelElement, "directives"), parseContext.directives);
        Policy.parseCommonAttributes(Policy.getFirstChild(topLevelElement, "common-attributes"), parseContext.commonAttributes, parseContext.commonRegularExpressions);
        Policy.parseGlobalAttributes(Policy.getFirstChild(topLevelElement, "global-tag-attributes"), parseContext.globalAttributes, parseContext.commonAttributes);
        Policy.parseDynamicAttributes(Policy.getFirstChild(topLevelElement, "dynamic-tag-attributes"), parseContext.dynamicAttributes, parseContext.commonAttributes);
        Policy.parseTagRules(Policy.getFirstChild(topLevelElement, "tag-rules"), parseContext.commonAttributes, parseContext.commonRegularExpressions, parseContext.tagRules);
        Policy.parseCSSRules(Policy.getFirstChild(topLevelElement, "css-rules"), parseContext.cssRules, parseContext.commonRegularExpressions);
        Policy.parseAllowedEmptyTags(Policy.getFirstChild(topLevelElement, "allowed-empty-tags"), parseContext.allowedEmptyTags);
        Policy.parseRequireClosingTags(Policy.getFirstChild(topLevelElement, "require-closing-tags"), parseContext.requireClosingTags);
    }

    @SuppressFBWarnings(value={"SECURITY"}, justification="Opening a stream to the provided URL is not a vulnerability because only local file URLs are allowed.")
    private static Element getPolicy(String href, URL baseUrl) throws PolicyException {
        Exception thrownException = null;
        try {
            Element element = Policy.getDocumentElementByUrl(href, baseUrl, true);
            return element;
        }
        catch (SAXException e) {
            thrownException = e;
            if (!validateSchema) {
                try {
                    Element theElement = Policy.getDocumentElementByUrl(href, baseUrl, false);
                    logger.warn("Invalid AntiSamy policy file: " + e.getMessage());
                    Element element = theElement;
                    return element;
                }
                catch (IOException | ParserConfigurationException | SAXException e2) {
                    throw new PolicyException(e2);
                }
            }
            throw new PolicyException(e);
        }
        catch (IOException | ParserConfigurationException e) {
            thrownException = e;
            throw new PolicyException(e);
        }
        finally {
            if (!validateSchema && thrownException == null) {
                logger.warn("XML schema validation is disabled for a valid AntiSamy policy. Please reenable policy validation.");
            }
        }
    }

    @SuppressFBWarnings(value={"SECURITY"}, justification="Opening a stream to the provided URL is not a vulnerability because only local file URLs are allowed.")
    private static Element getDocumentElementByUrl(String href, URL baseUrl, boolean schemaValidationEnabled) throws IOException, ParserConfigurationException, SAXException {
        InputSource source = null;
        if (href != null && baseUrl != null) {
            URL url;
            Policy.verifyLocalUrl(baseUrl);
            try {
                url = new URL(baseUrl, href);
                String logMsg = "Attempting to load AntiSamy policy from URL: " + url.toString();
                if (validateSchema) {
                    logger.info(logMsg);
                } else {
                    logger.warn(logMsg);
                }
                source = new InputSource(url.openStream());
                source.setSystemId(href);
            }
            catch (FileNotFoundException | MalformedURLException e) {
                try {
                    String absURL = URIUtils.resolveAsString(href, baseUrl.toString());
                    url = new URL(absURL);
                    source = new InputSource(url.openStream());
                    source.setSystemId(href);
                }
                catch (MalformedURLException absURL) {
                    // empty catch block
                }
            }
        }
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(EXTERNAL_GENERAL_ENTITIES, false);
        dbf.setFeature(EXTERNAL_PARAM_ENTITIES, false);
        dbf.setFeature(DISALLOW_DOCTYPE_DECL, true);
        dbf.setFeature(LOAD_EXTERNAL_DTD, false);
        if (schemaValidationEnabled) {
            Policy.getPolicySchema();
            dbf.setNamespaceAware(true);
            dbf.setSchema(schema);
        }
        DocumentBuilder db = dbf.newDocumentBuilder();
        db.setErrorHandler(new SAXErrorHandler());
        if (source != null) {
            Document dom = db.parse(source);
            return dom.getDocumentElement();
        }
        return null;
    }

    public Policy cloneWithDirective(String name, String value) {
        HashMap<String, String> directives = new HashMap<String, String>(this.directives);
        directives.put(name, value);
        return new InternalPolicy(this, Collections.unmodifiableMap(directives), this.tagRules, this.cssRules);
    }

    private static void parseDirectives(Element root, Map<String, String> directives) {
        for (Element ele : Policy.getByTagName(root, "directive")) {
            String name = XMLUtil.getAttributeValue(ele, "name");
            String value = XMLUtil.getAttributeValue(ele, "value");
            directives.put(name, value);
        }
    }

    private static void parseAllowedEmptyTags(Element allowedEmptyTagsListNode, List<String> allowedEmptyTags) {
        if (allowedEmptyTagsListNode != null) {
            for (Element literalNode : Policy.getGrandChildrenByTagName(allowedEmptyTagsListNode, "literal-list", "literal")) {
                String value = XMLUtil.getAttributeValue(literalNode, "value");
                if (value == null || value.length() <= 0) continue;
                allowedEmptyTags.add(value);
            }
        } else {
            allowedEmptyTags.addAll(Constants.defaultAllowedEmptyTags);
        }
    }

    private static void parseRequireClosingTags(Element requireClosingTagsListNode, List<String> requireClosingTags) {
        if (requireClosingTagsListNode != null) {
            for (Element literalNode : Policy.getGrandChildrenByTagName(requireClosingTagsListNode, "literal-list", "literal")) {
                String value = XMLUtil.getAttributeValue(literalNode, "value");
                if (value == null || value.length() <= 0) continue;
                requireClosingTags.add(value);
            }
        } else {
            requireClosingTags.addAll(Constants.defaultRequireClosingTags);
        }
    }

    private static void parseGlobalAttributes(Element root, Map<String, Attribute> globalAttributes1, Map<String, Attribute> commonAttributes) throws PolicyException {
        for (Element ele : Policy.getByTagName(root, "attribute")) {
            String name = XMLUtil.getAttributeValue(ele, "name");
            Attribute toAdd = commonAttributes.get(name.toLowerCase());
            if (toAdd != null) {
                globalAttributes1.put(name.toLowerCase(), toAdd);
                continue;
            }
            throw new PolicyException("Global attribute '" + name + "' was not defined in <common-attributes>");
        }
    }

    private static void parseDynamicAttributes(Element root, Map<String, Attribute> dynamicAttributes, Map<String, Attribute> commonAttributes) throws PolicyException {
        for (Element ele : Policy.getByTagName(root, "attribute")) {
            String name = XMLUtil.getAttributeValue(ele, "name");
            Attribute toAdd = commonAttributes.get(name.toLowerCase());
            if (toAdd != null) {
                String attrName = name.toLowerCase().substring(0, name.length() - 1);
                dynamicAttributes.put(attrName, toAdd);
                continue;
            }
            throw new PolicyException("Dynamic attribute '" + name + "' was not defined in <common-attributes>");
        }
    }

    private static void parseCommonRegExps(Element root, Map<String, AntiSamyPattern> commonRegularExpressions1) {
        for (Element ele : Policy.getByTagName(root, "regexp")) {
            String name = XMLUtil.getAttributeValue(ele, "name");
            Pattern pattern = Pattern.compile(XMLUtil.getAttributeValue(ele, "value"), 32);
            commonRegularExpressions1.put(name, new AntiSamyPattern(pattern));
        }
    }

    private static void parseCommonAttributes(Element root, Map<String, Attribute> commonAttributes1, Map<String, AntiSamyPattern> commonRegularExpressions1) {
        for (Element ele : Policy.getByTagName(root, "attribute")) {
            String onInvalid = XMLUtil.getAttributeValue(ele, "onInvalid");
            String name = XMLUtil.getAttributeValue(ele, "name");
            List<Pattern> allowedRegexps = Policy.getAllowedRegexps(commonRegularExpressions1, ele);
            List<String> allowedValues = Policy.getAllowedLiterals(ele);
            String onInvalidStr = onInvalid != null && onInvalid.length() > 0 ? onInvalid : DEFAULT_ONINVALID;
            String description = XMLUtil.getAttributeValue(ele, "description");
            Attribute attribute = new Attribute(XMLUtil.getAttributeValue(ele, "name"), allowedRegexps, allowedValues, onInvalidStr, description);
            commonAttributes1.put(name.toLowerCase(), attribute);
        }
    }

    private static List<String> getAllowedLiterals(Element ele) {
        ArrayList<String> allowedValues = new ArrayList<String>();
        for (Element literalNode : Policy.getGrandChildrenByTagName(ele, "literal-list", "literal")) {
            String value = XMLUtil.getAttributeValue(literalNode, "value");
            if (value != null && value.length() > 0) {
                allowedValues.add(value);
                continue;
            }
            if (literalNode.getNodeValue() == null) continue;
            allowedValues.add(literalNode.getNodeValue());
        }
        return allowedValues;
    }

    private static List<Pattern> getAllowedRegexps(Map<String, AntiSamyPattern> commonRegularExpressions1, Element ele) {
        ArrayList<Pattern> allowedRegExp = new ArrayList<Pattern>();
        for (Element regExpNode : Policy.getGrandChildrenByTagName(ele, "regexp-list", "regexp")) {
            String regExpName = XMLUtil.getAttributeValue(regExpNode, "name");
            String value = XMLUtil.getAttributeValue(regExpNode, "value");
            if (regExpName != null && regExpName.length() > 0) {
                allowedRegExp.add(commonRegularExpressions1.get(regExpName).getPattern());
                continue;
            }
            allowedRegExp.add(Pattern.compile(value, 32));
        }
        return allowedRegExp;
    }

    private static List<Pattern> getAllowedRegexps2(Map<String, AntiSamyPattern> commonRegularExpressions1, Element attributeNode, String tagName) throws PolicyException {
        ArrayList<Pattern> allowedRegexps = new ArrayList<Pattern>();
        for (Element regExpNode : Policy.getGrandChildrenByTagName(attributeNode, "regexp-list", "regexp")) {
            String regExpName = XMLUtil.getAttributeValue(regExpNode, "name");
            String value = XMLUtil.getAttributeValue(regExpNode, "value");
            if (regExpName != null && regExpName.length() > 0) {
                AntiSamyPattern pattern = commonRegularExpressions1.get(regExpName);
                if (pattern != null) {
                    allowedRegexps.add(pattern.getPattern());
                    continue;
                }
                throw new PolicyException("Regular expression '" + regExpName + "' was referenced as a common regexp in definition of '" + tagName + "', but does not exist in <common-regexp>");
            }
            if (value == null || value.length() <= 0) continue;
            allowedRegexps.add(Pattern.compile(value, 32));
        }
        return allowedRegexps;
    }

    private static List<Pattern> getAllowedRegexp3(Map<String, AntiSamyPattern> commonRegularExpressions1, Element ele, String name) throws PolicyException {
        ArrayList<Pattern> allowedRegExp = new ArrayList<Pattern>();
        for (Element regExpNode : Policy.getGrandChildrenByTagName(ele, "regexp-list", "regexp")) {
            String regExpName = XMLUtil.getAttributeValue(regExpNode, "name");
            String value = XMLUtil.getAttributeValue(regExpNode, "value");
            AntiSamyPattern pattern = commonRegularExpressions1.get(regExpName);
            if (pattern != null) {
                allowedRegExp.add(pattern.getPattern());
                continue;
            }
            if (value != null) {
                allowedRegExp.add(Pattern.compile(value, 32));
                continue;
            }
            throw new PolicyException("Regular expression '" + regExpName + "' was referenced as a common regexp in definition of '" + name + "', but does not exist in <common-regexp>");
        }
        return allowedRegExp;
    }

    private static void parseTagRules(Element root, Map<String, Attribute> commonAttributes1, Map<String, AntiSamyPattern> commonRegularExpressions1, Map<String, Tag> tagRules1) throws PolicyException {
        if (root == null) {
            return;
        }
        for (Element tagNode : Policy.getByTagName(root, "tag")) {
            String name = XMLUtil.getAttributeValue(tagNode, "name");
            String action = XMLUtil.getAttributeValue(tagNode, "action");
            String allowCData = XMLUtil.getAttributeValue(tagNode, "allowCData");
            NodeList attributeList = tagNode.getElementsByTagName("attribute");
            Map<String, Attribute> tagAttributes = Policy.getTagAttributes(commonAttributes1, commonRegularExpressions1, attributeList, name);
            Tag tag = new Tag(name, tagAttributes, action);
            if (StringUtils.isNotEmpty(allowCData)) {
                tag.setAllowCData(new HashSet<String>(Arrays.asList(allowCData.trim().split("\\s*,\\s*"))));
            }
            tagRules1.put(name.toLowerCase(), tag);
        }
    }

    private static Map<String, Attribute> getTagAttributes(Map<String, Attribute> commonAttributes1, Map<String, AntiSamyPattern> commonRegularExpressions1, NodeList attributeList, String tagName) throws PolicyException {
        HashMap<String, Attribute> tagAttributes = new HashMap<String, Attribute>();
        for (int j = 0; j < attributeList.getLength(); ++j) {
            Element attributeNode = (Element)attributeList.item(j);
            String attrName = XMLUtil.getAttributeValue(attributeNode, "name").toLowerCase();
            if (!attributeNode.hasChildNodes()) {
                Attribute attribute = commonAttributes1.get(attrName);
                if (attribute != null) {
                    String onInvalid = XMLUtil.getAttributeValue(attributeNode, "onInvalid");
                    String description = XMLUtil.getAttributeValue(attributeNode, "description");
                    Attribute changed = attribute.mutate(onInvalid, description);
                    commonAttributes1.put(attrName, changed);
                    tagAttributes.put(attrName, changed);
                    continue;
                }
                throw new PolicyException("Attribute '" + XMLUtil.getAttributeValue(attributeNode, "name") + "' was referenced as a common attribute in definition of '" + tagName + "', but does not exist in <common-attributes>");
            }
            List<Pattern> allowedRegexps2 = Policy.getAllowedRegexps2(commonRegularExpressions1, attributeNode, tagName);
            List<String> allowedValues2 = Policy.getAllowedLiterals(attributeNode);
            String onInvalid = XMLUtil.getAttributeValue(attributeNode, "onInvalid");
            String description = XMLUtil.getAttributeValue(attributeNode, "description");
            Attribute attribute = new Attribute(XMLUtil.getAttributeValue(attributeNode, "name"), allowedRegexps2, allowedValues2, onInvalid, description);
            tagAttributes.put(attrName, attribute);
        }
        return tagAttributes;
    }

    private static void parseCSSRules(Element root, Map<String, Property> cssRules1, Map<String, AntiSamyPattern> commonRegularExpressions1) throws PolicyException {
        for (Element ele : Policy.getByTagName(root, "property")) {
            String name = XMLUtil.getAttributeValue(ele, "name");
            String description = XMLUtil.getAttributeValue(ele, "description");
            List<Pattern> allowedRegexp3 = Policy.getAllowedRegexp3(commonRegularExpressions1, ele, name);
            ArrayList<String> allowedValue = new ArrayList<String>();
            for (Element element : Policy.getGrandChildrenByTagName(ele, "literal-list", "literal")) {
                allowedValue.add(XMLUtil.getAttributeValue(element, "value"));
            }
            ArrayList<String> shortHandRefs = new ArrayList<String>();
            for (Element shorthandNode : Policy.getGrandChildrenByTagName(ele, "shorthand-list", "shorthand")) {
                shortHandRefs.add(XMLUtil.getAttributeValue(shorthandNode, "name"));
            }
            String string = XMLUtil.getAttributeValue(ele, "onInvalid");
            String onInvalidStr = string != null && string.length() > 0 ? string : DEFAULT_ONINVALID;
            Property property = new Property(name, allowedRegexp3, allowedValue, shortHandRefs, description, onInvalidStr);
            cssRules1.put(name.toLowerCase(), property);
        }
    }

    public Attribute getGlobalAttributeByName(String name) {
        return this.globalAttributes.get(name.toLowerCase());
    }

    public Attribute getDynamicAttributeByName(String name) {
        Attribute dynamicAttribute = null;
        Set<Map.Entry<String, Attribute>> entries = this.dynamicAttributes.entrySet();
        for (Map.Entry<String, Attribute> entry : entries) {
            if (!name.startsWith(entry.getKey())) continue;
            dynamicAttribute = entry.getValue();
            break;
        }
        return dynamicAttribute;
    }

    public TagMatcher getAllowedEmptyTags() {
        return this.allowedEmptyTagsMatcher;
    }

    public TagMatcher getRequiresClosingTags() {
        return this.requiresClosingTagsMatcher;
    }

    public String getDirective(String name) {
        return this.directives.get(name);
    }

    public Set<String> getBlocksToIsolate() {
        String blocksToIsolate = this.getDirective(BLOCKS_TO_ISOLATE);
        if (StringUtils.isBlank(blocksToIsolate)) {
            return Collections.emptySet();
        }
        return new HashSet<String>(Arrays.asList(StringUtils.split(blocksToIsolate, ',')));
    }

    @SuppressFBWarnings(value={"SECURITY"}, justification="Opening a stream to the provided URL is not a vulnerability because only local file URLs are allowed.")
    public static InputSource resolveEntity(String systemId, URL baseUrl) throws IOException, SAXException {
        if (systemId != null && baseUrl != null) {
            Policy.verifyLocalUrl(baseUrl);
            try {
                URL url = new URL(baseUrl, systemId);
                InputSource source = new InputSource(url.openStream());
                source.setSystemId(systemId);
                return source;
            }
            catch (FileNotFoundException | MalformedURLException e) {
                try {
                    String absURL = URIUtils.resolveAsString(systemId, baseUrl.toString());
                    URL url = new URL(absURL);
                    InputSource source = new InputSource(url.openStream());
                    source.setSystemId(systemId);
                    return source;
                }
                catch (MalformedURLException malformedURLException) {
                    return null;
                }
            }
        }
        return null;
    }

    private static void verifyLocalUrl(URL url) throws MalformedURLException {
        switch (url.getProtocol()) {
            case "file": 
            case "jar": {
                break;
            }
            default: {
                throw new MalformedURLException("Only local files can be accessed with a policy URL. Illegal value supplied was: " + url);
            }
        }
    }

    private static Element getFirstChild(Element element, String tagName) {
        if (element == null) {
            return null;
        }
        NodeList elementsByTagName = element.getElementsByTagName(tagName);
        if (elementsByTagName != null && elementsByTagName.getLength() > 0) {
            return (Element)elementsByTagName.item(0);
        }
        return null;
    }

    private static Iterable<Element> getGrandChildrenByTagName(Element parent, String immediateChildName, String subChild) {
        NodeList elementsByTagName = parent.getElementsByTagName(immediateChildName);
        if (elementsByTagName.getLength() == 0) {
            return Collections.emptyList();
        }
        Element regExpListNode = (Element)elementsByTagName.item(0);
        return Policy.getByTagName(regExpListNode, subChild);
    }

    private static Iterable<Element> getByTagName(Element parent, String tagName) {
        if (parent == null) {
            return Collections.emptyList();
        }
        final NodeList nodes = parent.getElementsByTagName(tagName);
        return new Iterable<Element>(){

            @Override
            public Iterator<Element> iterator() {
                return new Iterator<Element>(){
                    int pos = 0;
                    int len;
                    {
                        this.len = nodes.getLength();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.pos < this.len;
                    }

                    @Override
                    public Element next() {
                        return (Element)nodes.item(this.pos++);
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("Cant remove");
                    }
                };
            }
        };
    }

    public AntiSamyPattern getCommonRegularExpressions(String name) {
        return this.commonRegularExpressions.get(name);
    }

    private static void getPolicySchema() throws SAXException {
        if (schema == null) {
            InputStream schemaStream = Policy.class.getClassLoader().getResourceAsStream(POLICY_SCHEMA_URI);
            StreamSource schemaSource = new StreamSource(schemaStream);
            schema = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema").newSchema(schemaSource);
        }
    }

    static {
        Policy.loadValidateSchemaProperty();
    }

    static class SAXErrorHandler
    implements ErrorHandler {
        SAXErrorHandler() {
        }

        @Override
        public void error(SAXParseException arg0) throws SAXException {
            throw arg0;
        }

        @Override
        public void fatalError(SAXParseException arg0) throws SAXException {
            throw arg0;
        }

        @Override
        public void warning(SAXParseException arg0) throws SAXException {
            throw arg0;
        }
    }

    protected static class ParseContext {
        Map<String, AntiSamyPattern> commonRegularExpressions = new HashMap<String, AntiSamyPattern>();
        Map<String, Attribute> commonAttributes = new HashMap<String, Attribute>();
        Map<String, Tag> tagRules = new HashMap<String, Tag>();
        Map<String, Property> cssRules = new HashMap<String, Property>();
        Map<String, String> directives = new HashMap<String, String>();
        Map<String, Attribute> globalAttributes = new HashMap<String, Attribute>();
        Map<String, Attribute> dynamicAttributes = new HashMap<String, Attribute>();
        List<String> allowedEmptyTags = new ArrayList<String>();
        List<String> requireClosingTags = new ArrayList<String>();

        protected ParseContext() {
        }

        public void resetParamsWhereLastConfigWins() {
            this.allowedEmptyTags.clear();
            this.requireClosingTags.clear();
        }
    }
}

