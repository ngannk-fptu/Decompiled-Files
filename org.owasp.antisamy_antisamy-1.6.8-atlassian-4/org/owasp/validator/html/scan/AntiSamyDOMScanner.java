/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sourceforge.htmlunit.cyberneko.parsers.DOMFragmentParser
 *  org.apache.batik.css.parser.ParseException
 *  org.apache.xerces.dom.DocumentImpl
 *  org.apache.xerces.xni.parser.XMLDocumentFilter
 *  org.apache.xml.serialize.HTMLSerializer
 *  org.apache.xml.serialize.OutputFormat
 */
package org.owasp.validator.html.scan;

import com.atlassian.xhtml.parsing.BlockIsolatingTagBalancer;
import com.atlassian.xhtml.parsing.SelfClosingTagPreservingHTMLTagBalancer;
import com.atlassian.xhtml.serialize.BugFixedHTMLSerializer;
import com.atlassian.xhtml.serialize.BugFixedXHTMLSerializer;
import com.atlassian.xhtml.serialize.SurrogatePairPreservingXHTMLSerializer;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sourceforge.htmlunit.cyberneko.parsers.DOMFragmentParser;
import org.apache.batik.css.parser.ParseException;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xml.serialize.HTMLSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.owasp.validator.css.CssScanner;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.owasp.validator.html.model.Attribute;
import org.owasp.validator.html.model.Tag;
import org.owasp.validator.html.scan.AbstractAntiSamyScanner;
import org.owasp.validator.html.scan.Constants;
import org.owasp.validator.html.scan.CustomDOMFragmentParser;
import org.owasp.validator.html.util.HTMLEntityEncoder;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class AntiSamyDOMScanner
extends AbstractAntiSamyScanner {
    private Document document = new DocumentImpl();
    private DocumentFragment dom = this.document.createDocumentFragment();
    private CleanResults results = null;
    private static final int maxDepth = 250;
    private static final Pattern invalidXmlCharacters = Pattern.compile("[\\u0000-\\u001F\\uD800-\\uDFFF\\uFFFE-\\uFFFF&&[^\\u0009\\u000A\\u000D]]");
    private static final Pattern conditionalDirectives = Pattern.compile("<?!?\\[\\s*(?:end)?if[^]]*\\]>?");
    private static final Queue<CachedItem> cachedItems = new ConcurrentLinkedQueue<CachedItem>();

    public AntiSamyDOMScanner(Policy policy) {
        super(policy);
    }

    public AntiSamyDOMScanner() throws PolicyException {
    }

    @Override
    public CleanResults scan(String html) throws ScanException {
        if (html == null) {
            throw new ScanException(new NullPointerException("Null html input"));
        }
        this.errorMessages.clear();
        int maxInputSize = this.policy.getMaxInputSize();
        if (maxInputSize < html.length()) {
            this.addError("error.size.toolarge", new Object[]{html.length(), maxInputSize});
            throw new ScanException((String)this.errorMessages.get(0));
        }
        this.isNofollowAnchors = this.policy.isNofollowAnchors();
        this.isNoopenerAndNoreferrerAnchors = this.policy.isNoopenerAndNoreferrerAnchors();
        this.isValidateParamAsEmbed = this.policy.isValidateParamAsEmbed();
        long startOfScan = System.currentTimeMillis();
        try {
            CachedItem cachedItem = cachedItems.poll();
            if (cachedItem == null) {
                cachedItem = new CachedItem();
            }
            DOMFragmentParser parser = cachedItem.getDomFragmentParser();
            Set<String> blocksToIsolate = this.policy.getBlocksToIsolate();
            SelfClosingTagPreservingHTMLTagBalancer balancer = null;
            balancer = blocksToIsolate.isEmpty() ? new SelfClosingTagPreservingHTMLTagBalancer(AntiSamyDOMScanner.getDomParser().getHtmlConfiguration()) : new BlockIsolatingTagBalancer(blocksToIsolate, AntiSamyDOMScanner.getDomParser().getHtmlConfiguration());
            parser.setProperty("http://cyberneko.org/html/properties/filters", (Object)new XMLDocumentFilter[]{balancer});
            parser.setProperty("http://cyberneko.org/html/properties/default-encoding", (Object)"UTF-8");
            try {
                parser.parse(new InputSource(new StringReader(html)), this.dom);
            }
            catch (Exception e) {
                throw new ScanException(e);
            }
            this.processChildren(this.dom, 0);
            String trimmedHtml = html;
            StringWriter out = new StringWriter();
            OutputFormat format = this.getOutputFormat();
            boolean fixXerces = Boolean.valueOf(this.policy.getDirective("fixXercesSerialisationBug"));
            Object serializer = this.policy.isUseXhtml() ? (fixXerces ? new BugFixedXHTMLSerializer(out, format) : new SurrogatePairPreservingXHTMLSerializer(out, format, this.policy)) : (fixXerces ? new BugFixedHTMLSerializer(out, format) : new HTMLSerializer((Writer)out, format));
            serializer.serialize(this.dom);
            final String trimmed = this.trim(trimmedHtml, out.getBuffer().toString());
            Callable<String> cleanHtml = new Callable<String>(){

                @Override
                public String call() throws Exception {
                    return trimmed;
                }
            };
            this.results = new CleanResults(startOfScan, cleanHtml, this.dom, (List<String>)this.errorMessages);
            cachedItems.add(cachedItem);
            return this.results;
        }
        catch (IOException | SAXException e) {
            throw new ScanException(e);
        }
    }

    static CustomDOMFragmentParser getDomParser() throws SAXNotRecognizedException, SAXNotSupportedException {
        CustomDOMFragmentParser parser = new CustomDOMFragmentParser();
        parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
        parser.setFeature("http://cyberneko.org/html/features/scanner/style/strip-cdata-delims", false);
        parser.setFeature("http://cyberneko.org/html/features/scanner/cdata-sections", true);
        try {
            parser.setFeature("http://cyberneko.org/html/features/enforce-strict-attribute-names", true);
        }
        catch (SAXNotRecognizedException sAXNotRecognizedException) {
            // empty catch block
        }
        return parser;
    }

    private void recursiveValidateTag(Node node, int currentStackDepth) throws ScanException {
        Tag embedTag;
        if (++currentStackDepth > 250) {
            throw new ScanException("Too many nested tags");
        }
        if (node instanceof Comment) {
            this.processCommentNode(node);
            return;
        }
        boolean isElement = node instanceof Element;
        NodeList eleChildNodes = node.getChildNodes();
        if (isElement && eleChildNodes.getLength() == 0 && this.removeDisallowedEmpty(node)) {
            return;
        }
        if (node instanceof Text && 4 == node.getNodeType()) {
            this.stripCData(node);
            return;
        }
        if (node instanceof ProcessingInstruction) {
            this.removePI(node);
        }
        if (!isElement) {
            return;
        }
        Element ele = (Element)node;
        Node parentNode = ele.getParentNode();
        String tagName = ele.getNodeName();
        String tagNameLowerCase = tagName.toLowerCase();
        Tag tagRule = this.policy.getTagByLowercaseName(tagNameLowerCase);
        boolean masqueradingParam = this.isMasqueradingParam(tagRule, embedTag = this.policy.getEmbedTag(), tagNameLowerCase);
        if (masqueradingParam) {
            tagRule = Constants.BASIC_PARAM_TAG_RULE;
        }
        if (tagRule == null && this.policy.isEncodeUnknownTag() || tagRule != null && tagRule.isAction("encode")) {
            this.encodeTag(currentStackDepth, ele, tagName, eleChildNodes);
        } else if (tagRule == null || tagRule.isAction("filter")) {
            this.actionFilter(currentStackDepth, ele, tagName, tagRule, eleChildNodes);
        } else if (tagRule.isAction("validate")) {
            this.actionValidate(currentStackDepth, ele, parentNode, tagName, tagNameLowerCase, tagRule, masqueradingParam, embedTag, eleChildNodes);
        } else if (tagRule.isAction("truncate")) {
            this.actionTruncate(ele, tagName, eleChildNodes);
        } else {
            this.addError("error.tag.removed", new Object[]{HTMLEntityEncoder.htmlEntityEncode(tagName)});
            this.removeNode(ele);
        }
    }

    private boolean isMasqueradingParam(Tag tagRule, Tag embedTag, String tagNameLowerCase) {
        if (tagRule == null && this.isValidateParamAsEmbed && "param".equals(tagNameLowerCase)) {
            return embedTag != null && embedTag.isAction("validate");
        }
        return false;
    }

    private void encodeTag(int currentStackDepth, Element ele, String tagName, NodeList eleChildNodes) throws ScanException {
        this.addError("error.tag.encoded", new Object[]{HTMLEntityEncoder.htmlEntityEncode(tagName)});
        this.processChildren(eleChildNodes, currentStackDepth);
        this.encodeAndPromoteChildren(ele);
    }

    private void actionFilter(int currentStackDepth, Element ele, String tagName, Tag tag, NodeList eleChildNodes) throws ScanException {
        if (tag == null) {
            this.addError("error.tag.notfound", new Object[]{HTMLEntityEncoder.htmlEntityEncode(tagName)});
        } else {
            this.addError("error.tag.filtered", new Object[]{HTMLEntityEncoder.htmlEntityEncode(tagName)});
        }
        this.processChildren(eleChildNodes, currentStackDepth);
        this.promoteChildren(ele);
    }

    private void actionValidate(int currentStackDepth, Element ele, Node parentNode, String tagName, String tagNameLowerCase, Tag tag, boolean masqueradingParam, Tag embedTag, NodeList eleChildNodes) throws ScanException {
        String valueValue;
        String nameValue = null;
        if (masqueradingParam && (nameValue = ele.getAttribute("name")) != null && !"".equals(nameValue)) {
            valueValue = ele.getAttribute("value");
            ele.setAttribute(nameValue, valueValue);
            ele.removeAttribute("name");
            ele.removeAttribute("value");
            tag = embedTag;
        }
        if ("style".equals(tagNameLowerCase) && this.policy.getStyleTag() != null && this.processStyleTag(ele, parentNode)) {
            return;
        }
        if (this.processAttributes(ele, tagName, tag, currentStackDepth)) {
            return;
        }
        if ("a".equals(tagNameLowerCase)) {
            Node relAttribute;
            String relValue;
            Node targetAttribute;
            boolean addNofollow = this.isNofollowAnchors;
            boolean addNoopenerAndNoreferrer = false;
            if (this.isNoopenerAndNoreferrerAnchors && (targetAttribute = ele.getAttributes().getNamedItem("target")) != null && targetAttribute.getNodeValue().equalsIgnoreCase("_blank")) {
                addNoopenerAndNoreferrer = true;
            }
            if (!(relValue = Attribute.mergeRelValuesInAnchor(addNofollow, addNoopenerAndNoreferrer, (relAttribute = ele.getAttributes().getNamedItem("rel")) == null ? "" : relAttribute.getNodeValue())).isEmpty()) {
                ele.setAttribute("rel", relValue.trim());
            }
        }
        this.processChildren(eleChildNodes, currentStackDepth);
        if (masqueradingParam && nameValue != null && !"".equals(nameValue)) {
            valueValue = ele.getAttribute(nameValue);
            ele.setAttribute("name", nameValue);
            ele.setAttribute("value", valueValue);
            ele.removeAttribute(nameValue);
        }
    }

    private boolean processStyleTag(Element ele, Node parentNode) {
        CssScanner styleScanner = new CssScanner(this.policy, messages, this.policy.isEmbedStyleSheets());
        try {
            int childNodesCount = ele.getChildNodes().getLength();
            if (childNodesCount > 0) {
                StringBuffer toScan = new StringBuffer();
                for (int i = 0; i < ele.getChildNodes().getLength(); ++i) {
                    Node childNode = ele.getChildNodes().item(i);
                    if (toScan.length() > 0) {
                        toScan.append("\n");
                    }
                    toScan.append(childNode.getTextContent());
                }
                CleanResults cr = styleScanner.scanStyleSheet(toScan.toString(), this.policy.getMaxInputSize());
                this.errorMessages.addAll(cr.getErrorMessages());
                String cleanHTML = cr.getCleanHTML();
                cleanHTML = cleanHTML == null || cleanHTML.equals("") ? "/* */" : cleanHTML;
                ele.getFirstChild().setNodeValue(cleanHTML);
                for (int i = childNodesCount - 1; i >= 1; --i) {
                    Node childNode = ele.getChildNodes().item(i);
                    ele.removeChild(childNode);
                }
            }
        }
        catch (NumberFormatException | ParseException | ScanException | DOMException e) {
            this.addError("error.css.tag.malformed", new Object[]{HTMLEntityEncoder.htmlEntityEncode(ele.getFirstChild().getNodeValue())});
            parentNode.removeChild(ele);
            return true;
        }
        return false;
    }

    private void actionTruncate(Element ele, String tagName, NodeList eleChildNodes) {
        NamedNodeMap nnmap = ele.getAttributes();
        while (nnmap.getLength() > 0) {
            this.addError("error.attribute.notfound", new Object[]{tagName, HTMLEntityEncoder.htmlEntityEncode(nnmap.item(0).getNodeName())});
            ele.removeAttribute(nnmap.item(0).getNodeName());
        }
        int j = 0;
        int length = eleChildNodes.getLength();
        for (int i = 0; i < length; ++i) {
            Node nodeToRemove = eleChildNodes.item(j);
            if (nodeToRemove.getNodeType() != 3) {
                ele.removeChild(nodeToRemove);
                continue;
            }
            ++j;
        }
    }

    private boolean processAttributes(Element ele, String tagName, Tag tag, int currentStackDepth) throws ScanException {
        NamedNodeMap attributes = ele.getAttributes();
        for (int currentAttributeIndex = 0; currentAttributeIndex < attributes.getLength(); ++currentAttributeIndex) {
            Node attribute = attributes.item(currentAttributeIndex);
            String name = attribute.getNodeName();
            String value = attribute.getNodeValue();
            Attribute attr = tag.getAttributeByName(name.toLowerCase());
            if (attr == null && (attr = this.policy.getGlobalAttributeByName(name)) == null && this.policy.isAllowDynamicAttributes()) {
                attr = this.policy.getDynamicAttributeByName(name);
            }
            if ("style".equals(name.toLowerCase()) && attr != null) {
                CssScanner styleScanner = new CssScanner(this.policy, messages, false);
                try {
                    CleanResults cr = styleScanner.scanInlineStyle(value, tagName, this.policy.getMaxInputSize());
                    attribute.setNodeValue(cr.getCleanHTML());
                    List<String> cssScanErrorMessages = cr.getErrorMessages();
                    this.errorMessages.addAll(cssScanErrorMessages);
                }
                catch (ScanException | DOMException e) {
                    this.addError("error.css.attribute.malformed", new Object[]{tagName, HTMLEntityEncoder.htmlEntityEncode(ele.getNodeValue())});
                    ele.removeAttribute(attribute.getNodeName());
                    --currentAttributeIndex;
                }
                continue;
            }
            if (attr != null) {
                if (attr.containsAllowedValue(value.toLowerCase()) || attr.matchesAllowedExpression(value)) continue;
                String onInvalidAction = attr.getOnInvalid();
                if ("removeTag".equals(onInvalidAction)) {
                    this.removeNode(ele);
                    this.addError("error.attribute.invalid.removed", new Object[]{tagName, HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(value)});
                    return true;
                }
                if ("filterTag".equals(onInvalidAction)) {
                    this.processChildren(ele, currentStackDepth);
                    this.promoteChildren(ele);
                    this.addError("error.attribute.invalid.filtered", new Object[]{tagName, HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(value)});
                    return true;
                }
                if ("encodeTag".equals(onInvalidAction)) {
                    this.processChildren(ele, currentStackDepth);
                    this.encodeAndPromoteChildren(ele);
                    this.addError("error.attribute.invalid.encoded", new Object[]{tagName, HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(value)});
                    return true;
                }
                ele.removeAttribute(attribute.getNodeName());
                --currentAttributeIndex;
                this.addError("error.attribute.invalid", new Object[]{tagName, HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(value)});
                continue;
            }
            this.addError("error.attribute.notfound", new Object[]{tagName, HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(value)});
            ele.removeAttribute(attribute.getNodeName());
            --currentAttributeIndex;
        }
        return false;
    }

    private void processChildren(Node ele, int currentStackDepth) throws ScanException {
        this.processChildren(ele.getChildNodes(), currentStackDepth);
    }

    private void processChildren(NodeList childNodes, int currentStackDepth) throws ScanException {
        for (int i = 0; i < childNodes.getLength(); ++i) {
            Node tmp = childNodes.item(i);
            this.recursiveValidateTag(tmp, currentStackDepth);
            if (tmp.getParentNode() != null) continue;
            --i;
        }
    }

    private void removePI(Node node) {
        this.addError("error.pi.found", new Object[]{HTMLEntityEncoder.htmlEntityEncode(node.getTextContent())});
        this.removeNode(node);
    }

    private void stripCData(Node node) {
        Set<String> allowCData;
        Node parent = node.getParentNode();
        Tag policyTag = this.policy.getTagByLowercaseName(parent.getNodeName().toLowerCase());
        if (policyTag != null && policyTag.getAllowCData() != null && ((allowCData = policyTag.getAllowCData()).size() == 1 && allowCData.contains("true") || parent.getParentNode() != null && allowCData.contains(parent.getParentNode().getNodeName().toLowerCase()))) {
            return;
        }
        this.addError("error.cdata.found", new Object[]{HTMLEntityEncoder.htmlEntityEncode(node.getTextContent())});
        Text text = this.document.createTextNode(node.getTextContent());
        node.getParentNode().insertBefore(text, node);
        node.getParentNode().removeChild(node);
    }

    private void processCommentNode(Node node) {
        if (!this.policy.isPreserveComments()) {
            node.getParentNode().removeChild(node);
        } else {
            String value = ((Comment)node).getData();
            if (value != null) {
                ((Comment)node).setData(conditionalDirectives.matcher(value).replaceAll(""));
            }
        }
    }

    private boolean removeDisallowedEmpty(Node node) {
        String tagName = node.getNodeName();
        if (!this.isAllowedEmptyTag(tagName)) {
            this.addError("error.tag.empty", new Object[]{HTMLEntityEncoder.htmlEntityEncode(node.getNodeName())});
            this.removeNode(node);
            return true;
        }
        return false;
    }

    private void removeNode(Node node) {
        Node parent = node.getParentNode();
        parent.removeChild(node);
        String tagName = parent.getNodeName();
        if (parent instanceof Element && parent.getChildNodes().getLength() == 0 && !this.isAllowedEmptyTag(tagName)) {
            this.removeNode(parent);
        }
    }

    private boolean isAllowedEmptyTag(String tagName) {
        return "head".equals(tagName) || this.policy.getAllowedEmptyTags().matches(tagName);
    }

    private void promoteChildren(Element ele) {
        this.promoteChildren(ele, ele.getChildNodes());
    }

    private void promoteChildren(Element ele, NodeList eleChildNodes) {
        Node parent = ele.getParentNode();
        while (eleChildNodes.getLength() > 0) {
            Node node = ele.removeChild(eleChildNodes.item(0));
            parent.insertBefore(node, ele);
        }
        if (parent != null) {
            this.removeNode(ele);
        }
    }

    private String stripNonValidXMLCharacters(String in, Matcher invalidXmlCharsMatcher) {
        if (in == null || "".equals(in)) {
            return "";
        }
        invalidXmlCharsMatcher.reset(in);
        return invalidXmlCharsMatcher.matches() ? invalidXmlCharsMatcher.replaceAll("") : in;
    }

    private void encodeAndPromoteChildren(Element ele) {
        Node parent = ele.getParentNode();
        String tagName = ele.getTagName();
        Text openingTag = parent.getOwnerDocument().createTextNode(this.toString(ele));
        parent.insertBefore(openingTag, ele);
        if (ele.hasChildNodes()) {
            Text closingTag = parent.getOwnerDocument().createTextNode("</" + tagName + ">");
            parent.insertBefore(closingTag, ele.getNextSibling());
        }
        this.promoteChildren(ele);
    }

    private String toString(Element ele) {
        StringBuilder eleAsString = new StringBuilder("<" + ele.getNodeName());
        NamedNodeMap attributes = ele.getAttributes();
        for (int i = 0; i < attributes.getLength(); ++i) {
            Node attribute = attributes.item(i);
            String name = attribute.getNodeName();
            String value = attribute.getNodeValue();
            eleAsString.append(" ");
            eleAsString.append(HTMLEntityEncoder.htmlEntityEncode(name));
            eleAsString.append("=\"");
            eleAsString.append(HTMLEntityEncoder.htmlEntityEncode(value));
            eleAsString.append("\"");
        }
        if (ele.hasChildNodes()) {
            eleAsString.append(">");
        } else {
            eleAsString.append("/>");
        }
        return eleAsString.toString();
    }

    @Override
    public CleanResults getResults() {
        return this.results;
    }

    static /* synthetic */ Pattern access$000() {
        return invalidXmlCharacters;
    }

    static class CachedItem {
        private final DOMFragmentParser parser;
        private final Matcher invalidXmlCharMatcher = AntiSamyDOMScanner.access$000().matcher("");

        CachedItem() throws SAXNotSupportedException, SAXNotRecognizedException {
            this.parser = AntiSamyDOMScanner.getDomParser();
        }

        DOMFragmentParser getDomFragmentParser() {
            return this.parser;
        }
    }
}

