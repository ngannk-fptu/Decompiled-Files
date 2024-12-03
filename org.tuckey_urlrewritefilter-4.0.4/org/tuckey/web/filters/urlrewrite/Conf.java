/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package org.tuckey.web.filters.urlrewrite;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.tuckey.web.filters.urlrewrite.CatchElem;
import org.tuckey.web.filters.urlrewrite.ClassRule;
import org.tuckey.web.filters.urlrewrite.Condition;
import org.tuckey.web.filters.urlrewrite.ConfHandler;
import org.tuckey.web.filters.urlrewrite.NormalRule;
import org.tuckey.web.filters.urlrewrite.OutboundRule;
import org.tuckey.web.filters.urlrewrite.Rule;
import org.tuckey.web.filters.urlrewrite.RuleBase;
import org.tuckey.web.filters.urlrewrite.Run;
import org.tuckey.web.filters.urlrewrite.Runnable;
import org.tuckey.web.filters.urlrewrite.SetAttribute;
import org.tuckey.web.filters.urlrewrite.gzip.GzipFilter;
import org.tuckey.web.filters.urlrewrite.utils.Log;
import org.tuckey.web.filters.urlrewrite.utils.ModRewriteConfLoader;
import org.tuckey.web.filters.urlrewrite.utils.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXParseException;

public class Conf {
    private static Log log = Log.getLog(Conf.class);
    private final List errors = new ArrayList();
    private final List rules = new ArrayList(50);
    private final List catchElems = new ArrayList(10);
    private List outboundRules = new ArrayList(50);
    private boolean ok = false;
    private Date loadedDate = null;
    private int ruleIdCounter = 0;
    private int outboundRuleIdCounter = 0;
    private String fileName;
    private String confSystemId;
    protected boolean useQueryString;
    protected boolean useContext;
    private static final String NONE_DECODE_USING = "null";
    private static final String HEADER_DECODE_USING = "header";
    private static final String DEFAULT_DECODE_USING = "header,utf-8";
    protected String decodeUsing = "header,utf-8";
    private boolean decodeUsingEncodingHeader;
    protected String defaultMatchType = null;
    private ServletContext context;
    private boolean docProcessed = false;
    private boolean engineEnabled = true;

    public Conf() {
        this.loadedDate = new Date();
    }

    public Conf(ServletContext context, InputStream inputStream, String fileName, String systemId) {
        this(context, inputStream, fileName, systemId, false);
    }

    public Conf(ServletContext context, InputStream inputStream, String fileName, String systemId, boolean modRewriteStyleConf) {
        this.context = context;
        this.fileName = fileName;
        this.confSystemId = systemId;
        if (modRewriteStyleConf) {
            this.loadModRewriteStyle(inputStream);
        } else {
            this.loadDom(inputStream);
        }
        if (this.docProcessed) {
            this.initialise();
        }
        this.loadedDate = new Date();
    }

    protected void loadModRewriteStyle(InputStream inputStream) {
        ModRewriteConfLoader loader = new ModRewriteConfLoader();
        try {
            loader.process(inputStream, this);
            this.docProcessed = true;
        }
        catch (IOException e) {
            this.addError("Exception loading conf  " + e.getMessage(), e);
        }
    }

    public Conf(URL confUrl) {
        this.context = null;
        this.fileName = confUrl.getFile();
        this.confSystemId = confUrl.toString();
        try {
            this.loadDom(confUrl.openStream());
        }
        catch (IOException e) {
            this.addError("Exception loading conf  " + e.getMessage(), e);
        }
        if (this.docProcessed) {
            this.initialise();
        }
        this.loadedDate = new Date();
    }

    public Conf(InputStream inputStream, String conffile) {
        this(null, inputStream, conffile, conffile);
    }

    protected synchronized void loadDom(InputStream inputStream) {
        DocumentBuilder parser;
        if (inputStream == null) {
            log.error("inputstream is null");
            return;
        }
        ConfHandler handler = new ConfHandler(this.confSystemId);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        log.debug("XML builder factory is: " + factory.getClass().getName());
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);
        try {
            parser = factory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            log.error("Unable to setup XML parser for reading conf", e);
            return;
        }
        log.debug("XML Parser: " + parser.getClass().getName());
        parser.setErrorHandler(handler);
        parser.setEntityResolver(handler);
        try {
            log.debug("about to parse conf");
            Document doc = parser.parse(inputStream, this.confSystemId);
            this.processConfDoc(doc);
        }
        catch (SAXParseException e) {
            this.addError("Parse error on line " + e.getLineNumber() + " " + e.getMessage(), e);
        }
        catch (Exception e) {
            this.addError("Exception loading conf  " + e.getMessage(), e);
        }
    }

    protected void processConfDoc(Document doc) {
        Element rootElement = doc.getDocumentElement();
        if ("true".equalsIgnoreCase(Conf.getAttrValue(rootElement, "use-query-string"))) {
            this.setUseQueryString(true);
        }
        if ("true".equalsIgnoreCase(Conf.getAttrValue(rootElement, "use-context"))) {
            log.debug("use-context set to true");
            this.setUseContext(true);
        }
        this.setDecodeUsing(Conf.getAttrValue(rootElement, "decode-using"));
        this.setDefaultMatchType(Conf.getAttrValue(rootElement, "default-match-type"));
        NodeList rootElementList = rootElement.getChildNodes();
        for (int i = 0; i < rootElementList.getLength(); ++i) {
            Node toNode;
            RuleBase rule;
            Element ruleElement;
            Node node = rootElementList.item(i);
            if (node.getNodeType() == 1 && ((Element)node).getTagName().equals("rule")) {
                ruleElement = (Element)node;
                rule = new NormalRule();
                this.processRuleBasics(ruleElement, rule);
                Conf.procesConditions(ruleElement, rule);
                Conf.processRuns(ruleElement, rule);
                toNode = ruleElement.getElementsByTagName("to").item(0);
                rule.setTo(Conf.getNodeValue(toNode));
                ((NormalRule)rule).setToType(Conf.getAttrValue(toNode, "type"));
                ((NormalRule)rule).setToContextStr(Conf.getAttrValue(toNode, "context"));
                rule.setToLast(Conf.getAttrValue(toNode, "last"));
                ((NormalRule)rule).setQueryStringAppend(Conf.getAttrValue(toNode, "qsappend"));
                if ("true".equalsIgnoreCase(Conf.getAttrValue(toNode, "encode"))) {
                    ((NormalRule)rule).setEncodeToUrl(true);
                }
                Conf.processSetAttributes(ruleElement, rule);
                this.addRule((Rule)((Object)rule));
                continue;
            }
            if (node.getNodeType() == 1 && ((Element)node).getTagName().equals("class-rule")) {
                ruleElement = (Element)node;
                ClassRule classRule = new ClassRule();
                if ("false".equalsIgnoreCase(Conf.getAttrValue(ruleElement, "enabled"))) {
                    classRule.setEnabled(false);
                }
                if ("false".equalsIgnoreCase(Conf.getAttrValue(ruleElement, "last"))) {
                    classRule.setLast(false);
                }
                classRule.setClassStr(Conf.getAttrValue(ruleElement, "class"));
                classRule.setMethodStr(Conf.getAttrValue(ruleElement, "method"));
                this.addRule(classRule);
                continue;
            }
            if (node.getNodeType() == 1 && ((Element)node).getTagName().equals("outbound-rule")) {
                ruleElement = (Element)node;
                rule = new OutboundRule();
                this.processRuleBasics(ruleElement, rule);
                if ("true".equalsIgnoreCase(Conf.getAttrValue(ruleElement, "encodefirst"))) {
                    ((OutboundRule)rule).setEncodeFirst(true);
                }
                Conf.procesConditions(ruleElement, rule);
                Conf.processRuns(ruleElement, rule);
                toNode = ruleElement.getElementsByTagName("to").item(0);
                rule.setTo(Conf.getNodeValue(toNode));
                rule.setToLast(Conf.getAttrValue(toNode, "last"));
                if ("false".equalsIgnoreCase(Conf.getAttrValue(toNode, "encode"))) {
                    ((OutboundRule)rule).setEncodeToUrl(false);
                }
                Conf.processSetAttributes(ruleElement, rule);
                this.addOutboundRule((OutboundRule)rule);
                continue;
            }
            if (node.getNodeType() != 1 || !((Element)node).getTagName().equals("catch")) continue;
            Element catchXMLElement = (Element)node;
            CatchElem catchElem = new CatchElem();
            catchElem.setClassStr(Conf.getAttrValue(catchXMLElement, "class"));
            Conf.processRuns(catchXMLElement, catchElem);
            this.catchElems.add(catchElem);
        }
        this.docProcessed = true;
    }

    private void processRuleBasics(Element ruleElement, RuleBase rule) {
        String ruleMatchType;
        if ("false".equalsIgnoreCase(Conf.getAttrValue(ruleElement, "enabled"))) {
            rule.setEnabled(false);
        }
        if (StringUtils.isBlank(ruleMatchType = Conf.getAttrValue(ruleElement, "match-type"))) {
            ruleMatchType = this.defaultMatchType;
        }
        rule.setMatchType(ruleMatchType);
        Node nameNode = ruleElement.getElementsByTagName("name").item(0);
        rule.setName(Conf.getNodeValue(nameNode));
        Node noteNode = ruleElement.getElementsByTagName("note").item(0);
        rule.setNote(Conf.getNodeValue(noteNode));
        Node fromNode = ruleElement.getElementsByTagName("from").item(0);
        rule.setFrom(Conf.getNodeValue(fromNode));
        if ("true".equalsIgnoreCase(Conf.getAttrValue(fromNode, "casesensitive"))) {
            rule.setFromCaseSensitive(true);
        }
    }

    private static void processSetAttributes(Element ruleElement, RuleBase rule) {
        NodeList setNodes = ruleElement.getElementsByTagName("set");
        for (int j = 0; j < setNodes.getLength(); ++j) {
            Node setNode = setNodes.item(j);
            if (setNode == null) continue;
            SetAttribute setAttribute = new SetAttribute();
            setAttribute.setValue(Conf.getNodeValue(setNode));
            setAttribute.setType(Conf.getAttrValue(setNode, "type"));
            setAttribute.setName(Conf.getAttrValue(setNode, "name"));
            rule.addSetAttribute(setAttribute);
        }
    }

    private static void processRuns(Element ruleElement, Runnable runnable) {
        NodeList runNodes = ruleElement.getElementsByTagName("run");
        for (int j = 0; j < runNodes.getLength(); ++j) {
            Node runNode = runNodes.item(j);
            if (runNode == null) continue;
            Run run = new Run();
            Conf.processInitParams(runNode, run);
            run.setClassStr(Conf.getAttrValue(runNode, "class"));
            run.setMethodStr(Conf.getAttrValue(runNode, "method"));
            run.setJsonHandler("true".equalsIgnoreCase(Conf.getAttrValue(runNode, "jsonhandler")));
            run.setNewEachTime("true".equalsIgnoreCase(Conf.getAttrValue(runNode, "neweachtime")));
            runnable.addRun(run);
        }
        NodeList gzipNodes = ruleElement.getElementsByTagName("gzip");
        for (int j = 0; j < gzipNodes.getLength(); ++j) {
            Node runNode = gzipNodes.item(j);
            if (runNode == null) continue;
            Run run = new Run();
            run.setClassStr(GzipFilter.class.getName());
            run.setMethodStr("doFilter(ServletRequest, ServletResponse, FilterChain)");
            Conf.processInitParams(runNode, run);
            runnable.addRun(run);
        }
    }

    private static void processInitParams(Node runNode, Run run) {
        if (runNode.getNodeType() == 1) {
            Element runElement = (Element)runNode;
            NodeList initParamsNodeList = runElement.getElementsByTagName("init-param");
            for (int k = 0; k < initParamsNodeList.getLength(); ++k) {
                Node initParamNode = initParamsNodeList.item(k);
                if (initParamNode == null || initParamNode.getNodeType() != 1) continue;
                Element initParamElement = (Element)initParamNode;
                Node paramNameNode = initParamElement.getElementsByTagName("param-name").item(0);
                Node paramValueNode = initParamElement.getElementsByTagName("param-value").item(0);
                run.addInitParam(Conf.getNodeValue(paramNameNode), Conf.getNodeValue(paramValueNode));
            }
        }
    }

    private static void procesConditions(Element ruleElement, RuleBase rule) {
        NodeList conditionNodes = ruleElement.getElementsByTagName("condition");
        for (int j = 0; j < conditionNodes.getLength(); ++j) {
            Node conditionNode = conditionNodes.item(j);
            if (conditionNode == null) continue;
            Condition condition = new Condition();
            condition.setValue(Conf.getNodeValue(conditionNode));
            condition.setType(Conf.getAttrValue(conditionNode, "type"));
            condition.setName(Conf.getAttrValue(conditionNode, "name"));
            condition.setNext(Conf.getAttrValue(conditionNode, "next"));
            condition.setCaseSensitive("true".equalsIgnoreCase(Conf.getAttrValue(conditionNode, "casesensitive")));
            condition.setOperator(Conf.getAttrValue(conditionNode, "operator"));
            rule.addCondition(condition);
        }
    }

    private static String getNodeValue(Node node) {
        if (node == null) {
            return null;
        }
        NodeList nodeList = node.getChildNodes();
        if (nodeList == null) {
            return null;
        }
        Node child = nodeList.item(0);
        if (child == null) {
            return null;
        }
        if (child.getNodeType() == 3) {
            String value = ((Text)child).getData();
            return value.trim();
        }
        return null;
    }

    private static String getAttrValue(Node n, String attrName) {
        if (n == null) {
            return null;
        }
        NamedNodeMap attrs = n.getAttributes();
        if (attrs == null) {
            return null;
        }
        Node attr = attrs.getNamedItem(attrName);
        if (attr == null) {
            return null;
        }
        String val = attr.getNodeValue();
        if (val == null) {
            return null;
        }
        return val.trim();
    }

    public void initialise() {
        int i;
        if (log.isDebugEnabled()) {
            log.debug("now initialising conf");
        }
        this.initDecodeUsing(this.decodeUsing);
        boolean rulesOk = true;
        for (i = 0; i < this.rules.size(); ++i) {
            Rule rule = (Rule)this.rules.get(i);
            if (rule.initialise(this.context)) continue;
            rulesOk = false;
        }
        for (i = 0; i < this.outboundRules.size(); ++i) {
            OutboundRule outboundRule = (OutboundRule)this.outboundRules.get(i);
            if (outboundRule.initialise(this.context)) continue;
            rulesOk = false;
        }
        for (i = 0; i < this.catchElems.size(); ++i) {
            CatchElem catchElem = (CatchElem)this.catchElems.get(i);
            if (catchElem.initialise(this.context)) continue;
            rulesOk = false;
        }
        if (rulesOk) {
            this.ok = true;
        }
        if (log.isDebugEnabled()) {
            log.debug("conf status " + this.ok);
        }
    }

    private void initDecodeUsing(String decodeUsingSetting) {
        if ((decodeUsingSetting = StringUtils.trimToNull(decodeUsingSetting)) == null) {
            decodeUsingSetting = DEFAULT_DECODE_USING;
        }
        if (decodeUsingSetting.equalsIgnoreCase(HEADER_DECODE_USING)) {
            this.decodeUsingEncodingHeader = true;
            decodeUsingSetting = null;
        } else if (decodeUsingSetting.startsWith("header,")) {
            this.decodeUsingEncodingHeader = true;
            decodeUsingSetting = decodeUsingSetting.substring("header,".length());
        }
        if (NONE_DECODE_USING.equalsIgnoreCase(decodeUsingSetting)) {
            decodeUsingSetting = null;
        }
        if (decodeUsingSetting != null) {
            try {
                URLDecoder.decode("testUrl", decodeUsingSetting);
                this.decodeUsing = decodeUsingSetting;
            }
            catch (UnsupportedEncodingException e) {
                this.addError("unsupported 'decodeusing' " + decodeUsingSetting + " see Java SDK docs for supported encodings");
            }
        } else {
            this.decodeUsing = null;
        }
    }

    public void destroy() {
        for (int i = 0; i < this.rules.size(); ++i) {
            Rule rule = (Rule)this.rules.get(i);
            rule.destroy();
        }
    }

    public void addRule(Rule rule) {
        rule.setId(this.ruleIdCounter++);
        this.rules.add(rule);
    }

    public void addOutboundRule(OutboundRule outboundRule) {
        outboundRule.setId(this.outboundRuleIdCounter++);
        this.outboundRules.add(outboundRule);
    }

    public List getErrors() {
        return this.errors;
    }

    public List getRules() {
        return this.rules;
    }

    public List getOutboundRules() {
        return this.outboundRules;
    }

    public boolean isOk() {
        return this.ok;
    }

    private void addError(String errorMsg, Exception e) {
        this.errors.add(errorMsg);
        log.error(errorMsg, e);
    }

    private void addError(String errorMsg) {
        this.errors.add(errorMsg);
    }

    public Date getLoadedDate() {
        return (Date)this.loadedDate.clone();
    }

    public String getFileName() {
        return this.fileName;
    }

    public boolean isUseQueryString() {
        return this.useQueryString;
    }

    public void setUseQueryString(boolean useQueryString) {
        this.useQueryString = useQueryString;
    }

    public boolean isUseContext() {
        return this.useContext;
    }

    public void setUseContext(boolean useContext) {
        this.useContext = useContext;
    }

    public String getDecodeUsing() {
        return this.decodeUsing;
    }

    public void setDecodeUsing(String decodeUsing) {
        this.decodeUsing = decodeUsing;
    }

    public void setDefaultMatchType(String defaultMatchType) {
        this.defaultMatchType = "wildcard".equalsIgnoreCase(defaultMatchType) ? "wildcard" : "regex";
    }

    public String getDefaultMatchType() {
        return this.defaultMatchType;
    }

    public List getCatchElems() {
        return this.catchElems;
    }

    public boolean isDecodeUsingCustomCharsetRequired() {
        return this.decodeUsing != null;
    }

    public boolean isEngineEnabled() {
        return this.engineEnabled;
    }

    public void setEngineEnabled(boolean engineEnabled) {
        this.engineEnabled = engineEnabled;
    }

    public boolean isLoadedFromFile() {
        return this.fileName != null;
    }

    public boolean isDecodeUsingEncodingHeader() {
        return this.decodeUsingEncodingHeader;
    }
}

