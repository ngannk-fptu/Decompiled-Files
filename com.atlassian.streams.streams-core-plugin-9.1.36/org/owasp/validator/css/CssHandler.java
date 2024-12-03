/*
 * Decompiled with CFR 0.152.
 */
package org.owasp.validator.css;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import org.owasp.validator.css.CssValidator;
import org.owasp.validator.html.InternalPolicy;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.ScanException;
import org.owasp.validator.html.util.ErrorMessageUtil;
import org.owasp.validator.html.util.HTMLEntityEncoder;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;

public class CssHandler
implements DocumentHandler {
    private StringBuffer styleSheet = new StringBuffer();
    private final CssValidator validator;
    private final InternalPolicy policy;
    private final Collection<String> errorMessages;
    private ResourceBundle messages;
    private final LinkedList<URI> importedStyleSheets;
    private final String tagName;
    private final boolean isInline;
    private boolean selectorOpen = false;

    @Deprecated
    public CssHandler(Policy policy, LinkedList<URI> embeddedStyleSheets, List<String> errorMessages, ResourceBundle messages) {
        this(policy, embeddedStyleSheets, errorMessages, null, messages);
    }

    public CssHandler(Policy policy, List<String> errorMessages, ResourceBundle messages) {
        this(policy, null, errorMessages, null, messages);
    }

    public CssHandler(Policy policy, List<String> errorMessages, ResourceBundle messages, String tagName) {
        this(policy, null, errorMessages, tagName, messages);
    }

    @Deprecated
    public CssHandler(Policy policy, LinkedList<URI> embeddedStyleSheets, List<String> errorMessages, String tagName, ResourceBundle messages) {
        assert (policy instanceof InternalPolicy) : policy.getClass();
        this.policy = (InternalPolicy)policy;
        this.errorMessages = errorMessages;
        this.messages = messages;
        this.validator = new CssValidator(policy);
        this.importedStyleSheets = embeddedStyleSheets != null ? embeddedStyleSheets : new LinkedList();
        this.tagName = tagName;
        this.isInline = tagName != null;
    }

    public String getCleanStylesheet() {
        return this.styleSheet.toString();
    }

    public LinkedList<URI> getImportedStylesheetsURIList() {
        return this.importedStyleSheets;
    }

    public void emptyStyleSheet() {
        this.styleSheet.delete(0, this.styleSheet.length());
    }

    public Collection<String> getErrorMessages() {
        return new ArrayList<String>(this.errorMessages);
    }

    @Override
    public void comment(String text) throws CSSException {
        this.errorMessages.add(ErrorMessageUtil.getMessage(this.messages, "error.comment.removed", new Object[]{HTMLEntityEncoder.htmlEntityEncode(text)}));
    }

    @Override
    public void ignorableAtRule(String atRule) throws CSSException {
        if (this.tagName != null) {
            this.errorMessages.add(ErrorMessageUtil.getMessage(this.messages, "error.css.tag.rule.notfound", new Object[]{HTMLEntityEncoder.htmlEntityEncode(this.tagName), HTMLEntityEncoder.htmlEntityEncode(atRule)}));
        } else {
            this.errorMessages.add(ErrorMessageUtil.getMessage(this.messages, "error.css.stylesheet.rule.notfound", new Object[]{HTMLEntityEncoder.htmlEntityEncode(atRule)}));
        }
    }

    @Override
    public void importStyle(String uri, SACMediaList media, String defaultNamespaceURI) throws CSSException {
        if (!this.policy.isEmbedStyleSheets()) {
            this.errorMessages.add(ErrorMessageUtil.getMessage(this.messages, "error.css.import.disabled", new Object[0]));
            return;
        }
        try {
            if (uri == null) {
                this.errorMessages.add(ErrorMessageUtil.getMessage(this.messages, "error.css.import.url.invalid", new Object[0]));
                return;
            }
            URI importedStyleSheet = new URI(uri);
            importedStyleSheet.normalize();
            if (!this.policy.getCommonRegularExpressions("offsiteURL").matches(importedStyleSheet.toString()) && !this.policy.getCommonRegularExpressions("onsiteURL").matches(importedStyleSheet.toString())) {
                this.errorMessages.add(ErrorMessageUtil.getMessage(this.messages, "error.css.import.url.invalid", new Object[]{HTMLEntityEncoder.htmlEntityEncode(uri)}));
                return;
            }
            if (!importedStyleSheet.isAbsolute()) {
                if (this.tagName != null) {
                    this.errorMessages.add(ErrorMessageUtil.getMessage(this.messages, "error.css.tag.relative", new Object[]{HTMLEntityEncoder.htmlEntityEncode(this.tagName), HTMLEntityEncoder.htmlEntityEncode(uri)}));
                } else {
                    this.errorMessages.add(ErrorMessageUtil.getMessage(this.messages, "error.css.stylesheet.relative", new Object[]{HTMLEntityEncoder.htmlEntityEncode(uri)}));
                }
                return;
            }
            this.importedStyleSheets.add(importedStyleSheet);
        }
        catch (URISyntaxException use) {
            this.errorMessages.add(ErrorMessageUtil.getMessage(this.messages, "error.css.import.url.invalid", new Object[]{HTMLEntityEncoder.htmlEntityEncode(uri)}));
        }
    }

    @Override
    public void namespaceDeclaration(String prefix, String uri) throws CSSException {
    }

    @Override
    public void startDocument(InputSource arg0) throws CSSException {
    }

    @Override
    public void endDocument(InputSource source) throws CSSException {
    }

    @Override
    public void startFontFace() throws CSSException {
    }

    @Override
    public void endFontFace() throws CSSException {
    }

    @Override
    public void startMedia(SACMediaList media) throws CSSException {
    }

    @Override
    public void endMedia(SACMediaList media) throws CSSException {
    }

    @Override
    public void startPage(String name, String pseudoPage) throws CSSException {
    }

    @Override
    public void endPage(String name, String pseudoPage) throws CSSException {
    }

    @Override
    public void startSelector(SelectorList selectors) throws CSSException {
        int selectorCount = 0;
        for (int i = 0; i < selectors.getLength(); ++i) {
            Selector selector = selectors.item(i);
            if (selector == null) continue;
            String selectorName = selector.toString();
            boolean isValidSelector = false;
            try {
                isValidSelector = this.validator.isValidSelector(selectorName, selector);
            }
            catch (ScanException se) {
                if (this.tagName != null) {
                    this.errorMessages.add(ErrorMessageUtil.getMessage(this.messages, "error.css.tag.selector.notfound", new Object[]{HTMLEntityEncoder.htmlEntityEncode(selector.toString())}));
                }
                this.errorMessages.add(ErrorMessageUtil.getMessage(this.messages, "error.css.stylesheet.selector.notfound", new Object[]{HTMLEntityEncoder.htmlEntityEncode(this.tagName), HTMLEntityEncoder.htmlEntityEncode(selector.toString())}));
            }
            if (isValidSelector) {
                if (selectorCount > 0) {
                    this.styleSheet.append(',');
                    this.styleSheet.append(' ');
                }
                this.styleSheet.append(selectorName);
                ++selectorCount;
                continue;
            }
            if (this.tagName != null) {
                this.errorMessages.add(ErrorMessageUtil.getMessage(this.messages, "error.css.tag.selector.disallowed", new Object[]{HTMLEntityEncoder.htmlEntityEncode(this.tagName), HTMLEntityEncoder.htmlEntityEncode(selector.toString())}));
                continue;
            }
            this.errorMessages.add(ErrorMessageUtil.getMessage(this.messages, "error.css.stylesheet.selector.disallowed", new Object[]{HTMLEntityEncoder.htmlEntityEncode(selector.toString())}));
        }
        if (selectorCount > 0) {
            this.styleSheet.append(' ');
            this.styleSheet.append('{');
            this.styleSheet.append('\n');
            this.selectorOpen = true;
        }
    }

    @Override
    public void endSelector(SelectorList selectors) throws CSSException {
        if (this.selectorOpen) {
            this.styleSheet.append('}');
            this.styleSheet.append('\n');
        }
        this.selectorOpen = false;
    }

    @Override
    public void property(String name, LexicalUnit value, boolean important) throws CSSException {
        if (!this.selectorOpen && !this.isInline) {
            return;
        }
        if (this.validator.isValidProperty(name, value)) {
            if (!this.isInline) {
                this.styleSheet.append('\t');
            }
            this.styleSheet.append(name);
            this.styleSheet.append(':');
            while (value != null) {
                this.styleSheet.append(' ');
                this.styleSheet.append(this.validator.lexicalValueToString(value));
                value = value.getNextLexicalUnit();
            }
            if (important) {
                this.styleSheet.append(" !important");
            }
            this.styleSheet.append(';');
            if (!this.isInline) {
                this.styleSheet.append('\n');
            }
        } else if (this.tagName != null) {
            this.errorMessages.add(ErrorMessageUtil.getMessage(this.messages, "error.css.tag.property.invalid", new Object[]{HTMLEntityEncoder.htmlEntityEncode(this.tagName), HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(this.validator.lexicalValueToString(value))}));
        } else {
            this.errorMessages.add(ErrorMessageUtil.getMessage(this.messages, "error.css.stylesheet.property.invalid", new Object[]{HTMLEntityEncoder.htmlEntityEncode(name), HTMLEntityEncoder.htmlEntityEncode(this.validator.lexicalValueToString(value))}));
        }
    }
}

