/*
 * Decompiled with CFR 0.152.
 */
package org.owasp.validator.html.scan;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;
import org.htmlunit.cyberneko.filters.DefaultFilter;
import org.htmlunit.cyberneko.xerces.util.XMLAttributesImpl;
import org.htmlunit.cyberneko.xerces.xni.Augmentations;
import org.htmlunit.cyberneko.xerces.xni.QName;
import org.htmlunit.cyberneko.xerces.xni.XMLAttributes;
import org.htmlunit.cyberneko.xerces.xni.XMLString;
import org.htmlunit.cyberneko.xerces.xni.XNIException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLDocumentFilter;
import org.owasp.validator.css.CssScanner;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.InternalPolicy;
import org.owasp.validator.html.ScanException;
import org.owasp.validator.html.model.Attribute;
import org.owasp.validator.html.model.Tag;
import org.owasp.validator.html.util.ErrorMessageUtil;
import org.owasp.validator.html.util.HTMLEntityEncoder;

public class MagicSAXFilter
extends DefaultFilter
implements XMLDocumentFilter {
    private final Stack<Ops> operations = new Stack();
    private final List<String> errorMessages = new ArrayList<String>();
    private StringBuffer cssContent = null;
    private XMLAttributes cssAttributes = null;
    private CssScanner cssScanner = null;
    private InternalPolicy policy;
    private ResourceBundle messages;
    private boolean isNofollowAnchors;
    private boolean isNoopenerAndNoreferrerAnchors;
    private boolean isValidateParamAsEmbed;
    private boolean inCdata = false;
    private boolean preserveComments;
    private int maxInputSize;
    private boolean shouldParseImportedStyles;
    private XMLAttributes currentTagAttributes;
    private QName currentTagName;
    private Stack<String> elements = new Stack();
    private static final Pattern conditionalDirectives = Pattern.compile("<?!?\\[\\s*(?:end)?if[^]]*\\]>?");

    public MagicSAXFilter(ResourceBundle messages) {
        this.messages = messages;
    }

    public void reset(InternalPolicy instance) {
        this.policy = instance;
        this.isNofollowAnchors = this.policy.isNofollowAnchors();
        this.isNoopenerAndNoreferrerAnchors = this.policy.isNoopenerAndNoreferrerAnchors();
        this.isValidateParamAsEmbed = this.policy.isValidateParamAsEmbed();
        this.preserveComments = this.policy.isPreserveComments();
        this.maxInputSize = this.policy.getMaxInputSize();
        this.shouldParseImportedStyles = this.policy.isEmbedStyleSheets();
        this.operations.clear();
        this.errorMessages.clear();
        this.cssContent = null;
        this.cssAttributes = null;
        this.cssScanner = null;
        this.inCdata = false;
    }

    @Override
    public void characters(XMLString text, Augmentations augs) throws XNIException {
        this.updateEmptyTagStatus(augs);
        if (!this.elements.isEmpty() && this.elements.peek() == "head") {
            return;
        }
        Ops topOp = this.peekTop();
        if (topOp != Ops.REMOVE) {
            if (topOp == Ops.CSS) {
                this.cssContent.append(text.toString());
            } else {
                if (this.inCdata) {
                    String encoded = HTMLEntityEncoder.htmlEntityEncode(text.toString());
                    this.addError("error.cdata.found", new Object[]{encoded});
                }
                super.characters(text, augs);
            }
        }
    }

    @Override
    public void comment(XMLString text, Augmentations augs) throws XNIException {
        String value;
        if (this.preserveComments && (value = text.toString()) != null) {
            value = conditionalDirectives.matcher(value).replaceAll("");
            super.comment(new XMLString(value.toCharArray(), 0, value.length()), augs);
        }
    }

    @Override
    public void doctypeDecl(String root, String publicId, String systemId, Augmentations augs) throws XNIException {
    }

    @Override
    public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        this.updateEmptyTagStatus(augs);
        boolean allowedEmpty = false;
        Tag tag = this.policy.getTagByLowercaseName(element.localpart.toLowerCase());
        if (tag == null && this.isValidateParamAsEmbed && "param".equals(element.localpart.toLowerCase())) {
            tag = this.policy.getTagByLowercaseName("embed");
        }
        if (tag != null) {
            allowedEmpty = this.policy.getAllowedEmptyTags().matches(tag.getName().toLowerCase());
        }
        if (!allowedEmpty) {
            return;
        }
        this.startElement(element, attributes, augs);
        this.endElement(element, augs);
    }

    private Ops peekTop() {
        return this.operations.empty() ? null : this.operations.peek();
    }

    private XMLString makeEndTag(String tagName) {
        String endTag = "</" + tagName + ">";
        return new XMLString(endTag.toCharArray(), 0, endTag.length());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void endElement(QName element, Augmentations augs) throws XNIException {
        if (!this.elements.isEmpty()) {
            this.elements.pop();
        }
        if (!this.operations.empty() && Ops.REMOVEEMPTY.equals((Object)this.operations.peek())) {
            this.operations.pop();
            if (!this.operations.empty()) {
                this.operations.pop();
            }
            this.currentTagAttributes = null;
            this.currentTagName = null;
            return;
        }
        Ops topOp = this.peekTop();
        if (Ops.REMOVE == topOp) {
            this.operations.pop();
            return;
        } else if (Ops.FILTER == topOp) {
            this.operations.pop();
            return;
        } else if (Ops.ENCODE == topOp) {
            this.operations.pop();
            super.characters(this.makeEndTag(element.rawname), augs);
            return;
        } else if (Ops.CSS == topOp) {
            this.operations.pop();
            CssScanner cssScanner = this.makeCssScanner();
            try {
                CleanResults results = cssScanner.scanStyleSheet(this.cssContent.toString(), this.maxInputSize);
                this.errorMessages.addAll(results.getErrorMessages());
                if (results.getCleanHTML() == null) return;
                if (results.getCleanHTML().equals("")) {
                    return;
                }
                super.startElement(element, this.cssAttributes, augs);
                String cleanHtml = results.getCleanHTML();
                super.characters(new XMLString(cleanHtml.toCharArray(), 0, cleanHtml.length()), augs);
                super.endElement(element, augs);
                return;
            }
            catch (ScanException e) {
                this.addError("error.css.tag.malformed", new Object[]{HTMLEntityEncoder.htmlEntityEncode(this.cssContent.toString())});
                return;
            }
            finally {
                this.cssContent = null;
                this.cssAttributes = null;
            }
        } else {
            this.operations.pop();
            super.endElement(element, augs);
        }
    }

    private CssScanner makeCssScanner() {
        if (this.cssScanner == null) {
            this.cssScanner = new CssScanner(this.policy, this.messages, this.shouldParseImportedStyles);
        }
        return this.cssScanner;
    }

    @Override
    public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
    }

    @Override
    public void startCDATA(Augmentations augs) throws XNIException {
        String currentTagName;
        Tag policyTag;
        this.inCdata = true;
        if (!this.elements.isEmpty() && (policyTag = this.policy.getTagByLowercaseName((currentTagName = this.elements.pop()).toLowerCase())) != null && policyTag.getAllowCData() != null) {
            Set<String> allowCData = policyTag.getAllowCData();
            String parentTagname = this.elements.peek();
            if (allowCData.size() == 1 && allowCData.contains("true") || allowCData.contains(parentTagname.toLowerCase())) {
                super.startCDATA(augs);
            }
            this.elements.add(currentTagName);
        }
    }

    @Override
    public void endCDATA(Augmentations augs) throws XNIException {
        this.inCdata = false;
        super.endCDATA(augs);
    }

    @Override
    public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        Tag embedPolicy;
        this.updateEmptyTagStatus(augs);
        this.elements.push(element.localpart.toLowerCase());
        String tagNameLowerCase = element.localpart.toLowerCase();
        Tag tag = this.policy.getTagByLowercaseName(tagNameLowerCase);
        boolean masqueradingParam = false;
        String embedName = null;
        String embedValue = null;
        if (tag == null && this.isValidateParamAsEmbed && "param".equals(tagNameLowerCase) && (embedPolicy = this.policy.getEmbedTag()) != null && embedPolicy.isAction("validate")) {
            tag = embedPolicy;
            masqueradingParam = true;
            embedName = attributes.getValue("name");
            embedValue = attributes.getValue("value");
            XMLAttributesImpl masqueradingAttrs = new XMLAttributesImpl();
            masqueradingAttrs.addAttribute(this.makeSimpleQname(embedName), "CDATA", embedValue);
            attributes = masqueradingAttrs;
        }
        boolean emptyAllowed = false;
        if (tag != null) {
            emptyAllowed = this.policy.getAllowedEmptyTags().matches(tag.getName().toLowerCase());
        }
        XMLAttributesImpl validattributes = new XMLAttributesImpl();
        Ops topOp = this.peekTop();
        if (Ops.REMOVE == topOp || Ops.CSS == topOp) {
            this.operations.push(Ops.REMOVE);
        } else if (tag == null && this.policy.isEncodeUnknownTag() || tag != null && tag.isAction("encode")) {
            String name = "<" + element.localpart + ">";
            super.characters(new XMLString(name.toCharArray(), 0, name.length()), augs);
            this.operations.push(Ops.ENCODE);
        } else if (tag == null) {
            this.addError("error.tag.notfound", new Object[]{HTMLEntityEncoder.htmlEntityEncode(element.localpart)});
            this.operations.push(Ops.FILTER);
        } else if (tag.isAction("filter")) {
            this.addError("error.tag.filtered", new Object[]{HTMLEntityEncoder.htmlEntityEncode(element.localpart)});
            this.operations.push(Ops.FILTER);
        } else if (tag.isAction("validate")) {
            boolean isStyle = "style".endsWith(element.localpart);
            boolean removeTag = false;
            boolean filterTag = false;
            for (int i = 0; i < attributes.getLength(); ++i) {
                String name = attributes.getQName(i);
                String value = attributes.getValue(i);
                String nameLower = name.toLowerCase();
                Attribute attribute = tag.getAttributeByName(nameLower);
                if (attribute == null && (attribute = this.policy.getGlobalAttributeByName(nameLower)) == null && this.policy.isAllowDynamicAttributes()) {
                    attribute = this.policy.getDynamicAttributeByName(nameLower);
                }
                if ("style".equalsIgnoreCase(name)) {
                    CssScanner styleScanner = this.makeCssScanner();
                    try {
                        CleanResults cr = styleScanner.scanInlineStyle(value, element.localpart, this.maxInputSize);
                        attributes.setValue(i, cr.getCleanHTML());
                        validattributes.addAttribute(this.makeSimpleQname(name), "CDATA", cr.getCleanHTML());
                        this.errorMessages.addAll(cr.getErrorMessages());
                    }
                    catch (ScanException e) {
                        this.addError("error.css.attribute.malformed", new Object[]{element.localpart, HTMLEntityEncoder.htmlEntityEncode(value)});
                    }
                    continue;
                }
                if (attribute != null) {
                    boolean isValid = false;
                    if (attribute.containsAllowedValue(value.toLowerCase())) {
                        if (validattributes.getIndex(name) == -1) {
                            validattributes.addAttribute(this.makeSimpleQname(name), "CDATA", value);
                        } else {
                            validattributes.setValue(validattributes.getIndex(name), value);
                        }
                        isValid = true;
                    }
                    if (!isValid && (isValid = attribute.matchesAllowedExpression(value))) {
                        if (validattributes.getIndex(name) == -1) {
                            validattributes.addAttribute(this.makeSimpleQname(name), "CDATA", value);
                        } else {
                            validattributes.setValue(validattributes.getIndex(name), value);
                        }
                    }
                    if (!isValid && "removeTag".equals(attribute.getOnInvalid())) {
                        this.addError("error.attribute.invalid.removed", new Object[]{tag.getName(), HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(value)});
                        removeTag = true;
                        continue;
                    }
                    if (!isValid && ("filterTag".equals(attribute.getOnInvalid()) || masqueradingParam)) {
                        this.addError("error.attribute.invalid.filtered", new Object[]{tag.getName(), HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(value)});
                        filterTag = true;
                        continue;
                    }
                    if (isValid) continue;
                    this.addError("error.attribute.invalid", new Object[]{tag.getName(), HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(value)});
                    continue;
                }
                this.addError("error.attribute.notfound", new Object[]{element.localpart, HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(value)});
                if (!masqueradingParam) continue;
                filterTag = true;
            }
            if (removeTag) {
                this.operations.push(Ops.REMOVE);
            } else if (isStyle) {
                this.operations.push(Ops.CSS);
                this.cssContent = new StringBuffer();
                this.cssAttributes = validattributes;
            } else if (filterTag) {
                this.operations.push(Ops.FILTER);
            } else {
                if ("a".equals(element.localpart)) {
                    String relValue;
                    Attribute attribute;
                    String currentRelValue;
                    String targetValue;
                    QName rel = this.makeSimpleQname("rel");
                    int relIndex = validattributes.getIndex(rel.prefix, rel.localpart);
                    if (relIndex != -1) {
                        validattributes.removeAttributeAt(relIndex);
                    }
                    boolean addNofollow = this.isNofollowAnchors;
                    boolean addNoopenerAndNoreferrer = false;
                    if (this.isNoopenerAndNoreferrerAnchors && (targetValue = attributes.getValue("target")) != null && targetValue.equalsIgnoreCase("_blank")) {
                        addNoopenerAndNoreferrer = true;
                    }
                    if ((currentRelValue = attributes.getValue("rel")) != null && (attribute = tag.getAttributeByName("rel")) != null && !attribute.containsAllowedValue(currentRelValue) && !attribute.matchesAllowedExpression(currentRelValue)) {
                        currentRelValue = "";
                    }
                    if (!(relValue = Attribute.mergeRelValuesInAnchor(addNofollow, addNoopenerAndNoreferrer, currentRelValue)).isEmpty()) {
                        validattributes.addAttribute(this.makeSimpleQname("rel"), "CDATA", relValue);
                    }
                }
                if (masqueradingParam) {
                    validattributes = new XMLAttributesImpl();
                    validattributes.addAttribute(this.makeSimpleQname("name"), "CDATA", embedName);
                    validattributes.addAttribute(this.makeSimpleQname("value"), "CDATA", embedValue);
                }
                this.operations.push(Ops.KEEP);
            }
        } else if (tag.isAction("truncate")) {
            this.operations.push(Ops.TRUNCATE);
        } else {
            this.addError("error.tag.removed", new Object[]{HTMLEntityEncoder.htmlEntityEncode(element.localpart)});
            this.operations.push(Ops.REMOVE);
        }
        if (Ops.TRUNCATE.equals((Object)this.operations.peek())) {
            super.startElement(element, new XMLAttributesImpl(), augs);
        } else if (Ops.KEEP.equals((Object)this.operations.peek())) {
            if (!emptyAllowed) {
                this.operations.push(Ops.REMOVEEMPTY);
                this.currentTagName = (QName)element.clone();
                this.currentTagAttributes = validattributes;
            } else {
                super.startElement(element, validattributes, augs);
            }
        }
    }

    private QName makeSimpleQname(String name) {
        return new QName("", name, name, "");
    }

    private void addError(String errorKey, Object[] objs) {
        this.errorMessages.add(ErrorMessageUtil.getMessage(this.messages, errorKey, objs));
    }

    public List<String> getErrorMessages() {
        return new ArrayList<String>(this.errorMessages);
    }

    private void updateEmptyTagStatus(Augmentations augs) {
        if (!this.operations.isEmpty() && Ops.REMOVEEMPTY.equals((Object)this.operations.peek())) {
            this.operations.pop();
            if (Ops.TRUNCATE.equals((Object)this.operations.peek()) || Ops.KEEP.equals((Object)this.operations.peek())) {
                super.startElement(this.currentTagName, this.currentTagAttributes, augs);
            }
            this.currentTagAttributes = null;
            this.currentTagName = null;
        }
    }

    private static enum Ops {
        CSS,
        FILTER,
        REMOVE,
        TRUNCATE,
        KEEP,
        ENCODE,
        REMOVEEMPTY;

    }
}

