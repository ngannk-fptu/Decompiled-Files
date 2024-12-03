/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.tagext.DynamicAttributes
 */
package org.springframework.web.servlet.tags.form;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.tags.form.AbstractDataBoundFormElementTag;
import org.springframework.web.servlet.tags.form.TagWriter;

public abstract class AbstractHtmlElementTag
extends AbstractDataBoundFormElementTag
implements DynamicAttributes {
    public static final String CLASS_ATTRIBUTE = "class";
    public static final String STYLE_ATTRIBUTE = "style";
    public static final String LANG_ATTRIBUTE = "lang";
    public static final String TITLE_ATTRIBUTE = "title";
    public static final String DIR_ATTRIBUTE = "dir";
    public static final String TABINDEX_ATTRIBUTE = "tabindex";
    public static final String ONCLICK_ATTRIBUTE = "onclick";
    public static final String ONDBLCLICK_ATTRIBUTE = "ondblclick";
    public static final String ONMOUSEDOWN_ATTRIBUTE = "onmousedown";
    public static final String ONMOUSEUP_ATTRIBUTE = "onmouseup";
    public static final String ONMOUSEOVER_ATTRIBUTE = "onmouseover";
    public static final String ONMOUSEMOVE_ATTRIBUTE = "onmousemove";
    public static final String ONMOUSEOUT_ATTRIBUTE = "onmouseout";
    public static final String ONKEYPRESS_ATTRIBUTE = "onkeypress";
    public static final String ONKEYUP_ATTRIBUTE = "onkeyup";
    public static final String ONKEYDOWN_ATTRIBUTE = "onkeydown";
    @Nullable
    private String cssClass;
    @Nullable
    private String cssErrorClass;
    @Nullable
    private String cssStyle;
    @Nullable
    private String lang;
    @Nullable
    private String title;
    @Nullable
    private String dir;
    @Nullable
    private String tabindex;
    @Nullable
    private String onclick;
    @Nullable
    private String ondblclick;
    @Nullable
    private String onmousedown;
    @Nullable
    private String onmouseup;
    @Nullable
    private String onmouseover;
    @Nullable
    private String onmousemove;
    @Nullable
    private String onmouseout;
    @Nullable
    private String onkeypress;
    @Nullable
    private String onkeyup;
    @Nullable
    private String onkeydown;
    @Nullable
    private Map<String, Object> dynamicAttributes;

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    @Nullable
    protected String getCssClass() {
        return this.cssClass;
    }

    public void setCssErrorClass(String cssErrorClass) {
        this.cssErrorClass = cssErrorClass;
    }

    @Nullable
    protected String getCssErrorClass() {
        return this.cssErrorClass;
    }

    public void setCssStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    @Nullable
    protected String getCssStyle() {
        return this.cssStyle;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @Nullable
    protected String getLang() {
        return this.lang;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Nullable
    protected String getTitle() {
        return this.title;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    @Nullable
    protected String getDir() {
        return this.dir;
    }

    public void setTabindex(String tabindex) {
        this.tabindex = tabindex;
    }

    @Nullable
    protected String getTabindex() {
        return this.tabindex;
    }

    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    @Nullable
    protected String getOnclick() {
        return this.onclick;
    }

    public void setOndblclick(String ondblclick) {
        this.ondblclick = ondblclick;
    }

    @Nullable
    protected String getOndblclick() {
        return this.ondblclick;
    }

    public void setOnmousedown(String onmousedown) {
        this.onmousedown = onmousedown;
    }

    @Nullable
    protected String getOnmousedown() {
        return this.onmousedown;
    }

    public void setOnmouseup(String onmouseup) {
        this.onmouseup = onmouseup;
    }

    @Nullable
    protected String getOnmouseup() {
        return this.onmouseup;
    }

    public void setOnmouseover(String onmouseover) {
        this.onmouseover = onmouseover;
    }

    @Nullable
    protected String getOnmouseover() {
        return this.onmouseover;
    }

    public void setOnmousemove(String onmousemove) {
        this.onmousemove = onmousemove;
    }

    @Nullable
    protected String getOnmousemove() {
        return this.onmousemove;
    }

    public void setOnmouseout(String onmouseout) {
        this.onmouseout = onmouseout;
    }

    @Nullable
    protected String getOnmouseout() {
        return this.onmouseout;
    }

    public void setOnkeypress(String onkeypress) {
        this.onkeypress = onkeypress;
    }

    @Nullable
    protected String getOnkeypress() {
        return this.onkeypress;
    }

    public void setOnkeyup(String onkeyup) {
        this.onkeyup = onkeyup;
    }

    @Nullable
    protected String getOnkeyup() {
        return this.onkeyup;
    }

    public void setOnkeydown(String onkeydown) {
        this.onkeydown = onkeydown;
    }

    @Nullable
    protected String getOnkeydown() {
        return this.onkeydown;
    }

    @Nullable
    protected Map<String, Object> getDynamicAttributes() {
        return this.dynamicAttributes;
    }

    public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
        if (this.dynamicAttributes == null) {
            this.dynamicAttributes = new HashMap<String, Object>();
        }
        if (!this.isValidDynamicAttribute(localName, value)) {
            throw new IllegalArgumentException("Attribute " + localName + "=\"" + value + "\" is not allowed");
        }
        this.dynamicAttributes.put(localName, value);
    }

    protected boolean isValidDynamicAttribute(String localName, Object value) {
        return true;
    }

    @Override
    protected void writeDefaultAttributes(TagWriter tagWriter) throws JspException {
        super.writeDefaultAttributes(tagWriter);
        this.writeOptionalAttributes(tagWriter);
    }

    protected void writeOptionalAttributes(TagWriter tagWriter) throws JspException {
        tagWriter.writeOptionalAttributeValue(CLASS_ATTRIBUTE, this.resolveCssClass());
        tagWriter.writeOptionalAttributeValue(STYLE_ATTRIBUTE, ObjectUtils.getDisplayString(this.evaluate("cssStyle", this.getCssStyle())));
        this.writeOptionalAttribute(tagWriter, LANG_ATTRIBUTE, this.getLang());
        this.writeOptionalAttribute(tagWriter, TITLE_ATTRIBUTE, this.getTitle());
        this.writeOptionalAttribute(tagWriter, DIR_ATTRIBUTE, this.getDir());
        this.writeOptionalAttribute(tagWriter, TABINDEX_ATTRIBUTE, this.getTabindex());
        this.writeOptionalAttribute(tagWriter, ONCLICK_ATTRIBUTE, this.getOnclick());
        this.writeOptionalAttribute(tagWriter, ONDBLCLICK_ATTRIBUTE, this.getOndblclick());
        this.writeOptionalAttribute(tagWriter, ONMOUSEDOWN_ATTRIBUTE, this.getOnmousedown());
        this.writeOptionalAttribute(tagWriter, ONMOUSEUP_ATTRIBUTE, this.getOnmouseup());
        this.writeOptionalAttribute(tagWriter, ONMOUSEOVER_ATTRIBUTE, this.getOnmouseover());
        this.writeOptionalAttribute(tagWriter, ONMOUSEMOVE_ATTRIBUTE, this.getOnmousemove());
        this.writeOptionalAttribute(tagWriter, ONMOUSEOUT_ATTRIBUTE, this.getOnmouseout());
        this.writeOptionalAttribute(tagWriter, ONKEYPRESS_ATTRIBUTE, this.getOnkeypress());
        this.writeOptionalAttribute(tagWriter, ONKEYUP_ATTRIBUTE, this.getOnkeyup());
        this.writeOptionalAttribute(tagWriter, ONKEYDOWN_ATTRIBUTE, this.getOnkeydown());
        if (!CollectionUtils.isEmpty(this.dynamicAttributes)) {
            for (Map.Entry<String, Object> entry : this.dynamicAttributes.entrySet()) {
                tagWriter.writeOptionalAttributeValue(entry.getKey(), this.getDisplayString(entry.getValue()));
            }
        }
    }

    protected String resolveCssClass() throws JspException {
        if (this.getBindStatus().isError() && StringUtils.hasText(this.getCssErrorClass())) {
            return ObjectUtils.getDisplayString(this.evaluate("cssErrorClass", this.getCssErrorClass()));
        }
        return ObjectUtils.getDisplayString(this.evaluate("cssClass", this.getCssClass()));
    }
}

