/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.jsp.tagext;

import javax.servlet.jsp.tagext.TagAttributeInfo;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.TagLibraryInfo;
import javax.servlet.jsp.tagext.TagVariableInfo;
import javax.servlet.jsp.tagext.ValidationMessage;
import javax.servlet.jsp.tagext.VariableInfo;

public class TagInfo {
    public static final String BODY_CONTENT_JSP = "JSP";
    public static final String BODY_CONTENT_TAG_DEPENDENT = "tagdependent";
    public static final String BODY_CONTENT_EMPTY = "empty";
    public static final String BODY_CONTENT_SCRIPTLESS = "scriptless";
    private final String tagName;
    private final String tagClassName;
    private final String bodyContent;
    private final String infoString;
    private TagLibraryInfo tagLibrary;
    private TagExtraInfo tagExtraInfo;
    private final TagAttributeInfo[] attributeInfo;
    private final String displayName;
    private final String smallIcon;
    private final String largeIcon;
    private final TagVariableInfo[] tagVariableInfo;
    private final boolean dynamicAttributes;

    public TagInfo(String tagName, String tagClassName, String bodycontent, String infoString, TagLibraryInfo taglib, TagExtraInfo tagExtraInfo, TagAttributeInfo[] attributeInfo) {
        this.tagName = tagName;
        this.tagClassName = tagClassName;
        this.bodyContent = bodycontent;
        this.infoString = infoString;
        this.tagLibrary = taglib;
        this.tagExtraInfo = tagExtraInfo;
        this.attributeInfo = attributeInfo;
        this.displayName = null;
        this.largeIcon = null;
        this.smallIcon = null;
        this.tagVariableInfo = null;
        this.dynamicAttributes = false;
        if (tagExtraInfo != null) {
            tagExtraInfo.setTagInfo(this);
        }
    }

    public TagInfo(String tagName, String tagClassName, String bodycontent, String infoString, TagLibraryInfo taglib, TagExtraInfo tagExtraInfo, TagAttributeInfo[] attributeInfo, String displayName, String smallIcon, String largeIcon, TagVariableInfo[] tvi) {
        this.tagName = tagName;
        this.tagClassName = tagClassName;
        this.bodyContent = bodycontent;
        this.infoString = infoString;
        this.tagLibrary = taglib;
        this.tagExtraInfo = tagExtraInfo;
        this.attributeInfo = attributeInfo;
        this.displayName = displayName;
        this.smallIcon = smallIcon;
        this.largeIcon = largeIcon;
        this.tagVariableInfo = tvi;
        this.dynamicAttributes = false;
        if (tagExtraInfo != null) {
            tagExtraInfo.setTagInfo(this);
        }
    }

    public TagInfo(String tagName, String tagClassName, String bodycontent, String infoString, TagLibraryInfo taglib, TagExtraInfo tagExtraInfo, TagAttributeInfo[] attributeInfo, String displayName, String smallIcon, String largeIcon, TagVariableInfo[] tvi, boolean dynamicAttributes) {
        this.tagName = tagName;
        this.tagClassName = tagClassName;
        this.bodyContent = bodycontent;
        this.infoString = infoString;
        this.tagLibrary = taglib;
        this.tagExtraInfo = tagExtraInfo;
        this.attributeInfo = attributeInfo;
        this.displayName = displayName;
        this.smallIcon = smallIcon;
        this.largeIcon = largeIcon;
        this.tagVariableInfo = tvi;
        this.dynamicAttributes = dynamicAttributes;
        if (tagExtraInfo != null) {
            tagExtraInfo.setTagInfo(this);
        }
    }

    public String getTagName() {
        return this.tagName;
    }

    public TagAttributeInfo[] getAttributes() {
        return this.attributeInfo;
    }

    public VariableInfo[] getVariableInfo(TagData data) {
        VariableInfo[] result = null;
        TagExtraInfo tei = this.getTagExtraInfo();
        if (tei != null) {
            result = tei.getVariableInfo(data);
        }
        return result;
    }

    public boolean isValid(TagData data) {
        TagExtraInfo tei = this.getTagExtraInfo();
        if (tei == null) {
            return true;
        }
        return tei.isValid(data);
    }

    public ValidationMessage[] validate(TagData data) {
        TagExtraInfo tei = this.getTagExtraInfo();
        if (tei == null) {
            return null;
        }
        return tei.validate(data);
    }

    public void setTagExtraInfo(TagExtraInfo tei) {
        this.tagExtraInfo = tei;
    }

    public TagExtraInfo getTagExtraInfo() {
        return this.tagExtraInfo;
    }

    public String getTagClassName() {
        return this.tagClassName;
    }

    public String getBodyContent() {
        return this.bodyContent;
    }

    public String getInfoString() {
        return this.infoString;
    }

    public void setTagLibrary(TagLibraryInfo tl) {
        this.tagLibrary = tl;
    }

    public TagLibraryInfo getTagLibrary() {
        return this.tagLibrary;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getSmallIcon() {
        return this.smallIcon;
    }

    public String getLargeIcon() {
        return this.largeIcon;
    }

    public TagVariableInfo[] getTagVariableInfos() {
        return this.tagVariableInfo;
    }

    public boolean hasDynamicAttributes() {
        return this.dynamicAttributes;
    }
}

