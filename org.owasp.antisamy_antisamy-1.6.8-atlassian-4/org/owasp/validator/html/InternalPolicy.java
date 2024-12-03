/*
 * Decompiled with CFR 0.152.
 */
package org.owasp.validator.html;

import java.util.Map;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.model.Property;
import org.owasp.validator.html.model.Tag;

public class InternalPolicy
extends Policy {
    private final int maxInputSize = this.determineMaxInputSize();
    private final boolean isNofollowAnchors = this.isTrue("nofollowAnchors");
    private final boolean isNoopenerAndNoreferrerAnchors = this.isTrue("noopenerAndNoreferrerAnchors");
    private final boolean isValidateParamAsEmbed = this.isTrue("validateParamAsEmbed");
    private final boolean formatOutput = this.isTrue("formatOutput");
    private final boolean preserveSpace = this.isTrue("preserveSpace");
    private final boolean omitXmlDeclaration = this.isTrue("omitXmlDeclaration");
    private final boolean omitDoctypeDeclaration = this.isTrue("omitDoctypeDeclaration");
    private final boolean entityEncodeIntlCharacters = this.isTrue("entityEncodeIntlChars");
    private final boolean useXhtml = this.isTrue("useXHTML");
    private final Tag embedTag = this.getTagByLowercaseName("embed");
    private final Tag styleTag;
    private final String onUnknownTag = this.getDirective("onUnknownTag");
    private final boolean preserveComments;
    private final boolean embedStyleSheets;
    private final boolean isEncodeUnknownTag = "encode".equals(this.onUnknownTag);
    private final boolean allowDynamicAttributes;

    protected InternalPolicy(Policy.ParseContext parseContext) {
        super(parseContext);
        this.preserveComments = this.isTrue("preserveComments");
        this.styleTag = this.getTagByLowercaseName("style");
        this.embedStyleSheets = this.isTrue("embedStyleSheets");
        this.allowDynamicAttributes = this.isTrue("allowDynamicAttributes");
        if (!this.isNoopenerAndNoreferrerAnchors) {
            logger.warn("The directive \"noopenerAndNoreferrerAnchors\" is not enabled by default. It is recommended to enable it to prevent reverse tabnabbing attacks.");
        }
    }

    protected InternalPolicy(Policy old, Map<String, String> directives, Map<String, Tag> tagRules, Map<String, Property> cssRules) {
        super(old, directives, tagRules, cssRules);
        this.preserveComments = this.isTrue("preserveComments");
        this.styleTag = this.getTagByLowercaseName("style");
        this.embedStyleSheets = this.isTrue("embedStyleSheets");
        this.allowDynamicAttributes = this.isTrue("allowDynamicAttributes");
        if (!this.isNoopenerAndNoreferrerAnchors) {
            logger.warn("The directive \"noopenerAndNoreferrerAnchors\" is not enabled by default. It is recommended to enable it to prevent reverse tabnabbing attacks.");
        }
    }

    public Tag getEmbedTag() {
        return this.embedTag;
    }

    public Tag getStyleTag() {
        return this.styleTag;
    }

    public boolean isEmbedStyleSheets() {
        return this.embedStyleSheets;
    }

    public boolean isPreserveComments() {
        return this.preserveComments;
    }

    public int getMaxInputSize() {
        return this.maxInputSize;
    }

    public boolean isEntityEncodeIntlCharacters() {
        return this.entityEncodeIntlCharacters;
    }

    public boolean isNofollowAnchors() {
        return this.isNofollowAnchors;
    }

    public boolean isNoopenerAndNoreferrerAnchors() {
        return this.isNoopenerAndNoreferrerAnchors;
    }

    public boolean isValidateParamAsEmbed() {
        return this.isValidateParamAsEmbed;
    }

    public boolean isFormatOutput() {
        return this.formatOutput;
    }

    public boolean isPreserveSpace() {
        return this.preserveSpace;
    }

    public boolean isOmitXmlDeclaration() {
        return this.omitXmlDeclaration;
    }

    @Deprecated
    public boolean isUseXhtml() {
        return this.useXhtml;
    }

    public boolean isOmitDoctypeDeclaration() {
        return this.omitDoctypeDeclaration;
    }

    private boolean isTrue(String anchorsNofollow) {
        return "true".equals(this.getDirective(anchorsNofollow));
    }

    public String getOnUnknownTag() {
        return this.onUnknownTag;
    }

    public boolean isEncodeUnknownTag() {
        return this.isEncodeUnknownTag;
    }

    public boolean isAllowDynamicAttributes() {
        return this.allowDynamicAttributes;
    }

    public int determineMaxInputSize() {
        int maxInputSize = 100000;
        try {
            maxInputSize = Integer.parseInt(this.getDirective("maxInputSize"));
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        return maxInputSize;
    }
}

