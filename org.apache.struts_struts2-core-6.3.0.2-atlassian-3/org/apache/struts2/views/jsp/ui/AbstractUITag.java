/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.tagext.DynamicAttributes
 */
package org.apache.struts2.views.jsp.ui;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;
import org.apache.struts2.components.UIBean;
import org.apache.struts2.views.jsp.ComponentTagSupport;

public abstract class AbstractUITag
extends ComponentTagSupport
implements DynamicAttributes {
    protected String cssClass;
    protected String cssErrorClass;
    protected String cssStyle;
    protected String cssErrorStyle;
    protected String title;
    protected String disabled;
    protected String label;
    protected String labelSeparator;
    protected String labelPosition;
    protected String requiredPosition;
    protected String errorPosition;
    protected String name;
    protected String requiredLabel;
    protected String tabindex;
    protected String value;
    protected String template;
    protected String theme;
    protected String templateDir;
    protected String onclick;
    protected String ondblclick;
    protected String onmousedown;
    protected String onmouseup;
    protected String onmouseover;
    protected String onmousemove;
    protected String onmouseout;
    protected String onfocus;
    protected String onblur;
    protected String onkeypress;
    protected String onkeydown;
    protected String onkeyup;
    protected String onselect;
    protected String onchange;
    protected String accesskey;
    protected String id;
    protected String key;
    protected String tooltip;
    protected String tooltipConfig;
    protected String javascriptTooltip;
    protected String tooltipDelay;
    protected String tooltipCssClass;
    protected String tooltipIconPath;
    protected Map<String, String> dynamicAttributes = new HashMap<String, String>();

    @Override
    protected void populateParams() {
        super.populateParams();
        UIBean uiBean = (UIBean)this.component;
        uiBean.setCssClass(this.cssClass);
        uiBean.setCssStyle(this.cssStyle);
        uiBean.setCssErrorClass(this.cssErrorClass);
        uiBean.setCssErrorStyle(this.cssErrorStyle);
        uiBean.setTitle(this.title);
        uiBean.setDisabled(this.disabled);
        uiBean.setLabel(this.label);
        uiBean.setLabelSeparator(this.labelSeparator);
        uiBean.setLabelPosition(this.labelPosition);
        uiBean.setRequiredPosition(this.requiredPosition);
        uiBean.setErrorPosition(this.errorPosition);
        uiBean.setName(this.name);
        uiBean.setRequiredLabel(this.requiredLabel);
        uiBean.setTabindex(this.tabindex);
        uiBean.setValue(this.value);
        uiBean.setTemplate(this.template);
        uiBean.setTheme(this.theme);
        uiBean.setTemplateDir(this.templateDir);
        uiBean.setOnclick(this.onclick);
        uiBean.setOndblclick(this.ondblclick);
        uiBean.setOnmousedown(this.onmousedown);
        uiBean.setOnmouseup(this.onmouseup);
        uiBean.setOnmouseover(this.onmouseover);
        uiBean.setOnmousemove(this.onmousemove);
        uiBean.setOnmouseout(this.onmouseout);
        uiBean.setOnfocus(this.onfocus);
        uiBean.setOnblur(this.onblur);
        uiBean.setOnkeypress(this.onkeypress);
        uiBean.setOnkeydown(this.onkeydown);
        uiBean.setOnkeyup(this.onkeyup);
        uiBean.setOnselect(this.onselect);
        uiBean.setOnchange(this.onchange);
        uiBean.setTooltip(this.tooltip);
        uiBean.setTooltipConfig(this.tooltipConfig);
        uiBean.setJavascriptTooltip(this.javascriptTooltip);
        uiBean.setTooltipCssClass(this.tooltipCssClass);
        uiBean.setTooltipDelay(this.tooltipDelay);
        uiBean.setTooltipIconPath(this.tooltipIconPath);
        uiBean.setAccesskey(this.accesskey);
        uiBean.setKey(this.key);
        uiBean.setId(this.id);
        uiBean.setDynamicAttributes(this.dynamicAttributes);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    @Deprecated
    public void setClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public void setCssStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    public void setStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    public void setCssErrorClass(String cssErrorClass) {
        this.cssErrorClass = cssErrorClass;
    }

    public void setCssErrorStyle(String cssErrorStyle) {
        this.cssErrorStyle = cssErrorStyle;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setLabelPosition(String labelPosition) {
        this.labelPosition = labelPosition;
    }

    public void setRequiredPosition(String requiredPosition) {
        this.requiredPosition = requiredPosition;
    }

    public void setErrorPosition(String errorPosition) {
        this.errorPosition = errorPosition;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRequiredLabel(String requiredLabel) {
        this.requiredLabel = requiredLabel;
    }

    public void setTabindex(String tabindex) {
        this.tabindex = tabindex;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setTemplateDir(String templateDir) {
        this.templateDir = templateDir;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    public void setOndblclick(String ondblclick) {
        this.ondblclick = ondblclick;
    }

    public void setOnmousedown(String onmousedown) {
        this.onmousedown = onmousedown;
    }

    public void setOnmouseup(String onmouseup) {
        this.onmouseup = onmouseup;
    }

    public void setOnmouseover(String onmouseover) {
        this.onmouseover = onmouseover;
    }

    public void setOnmousemove(String onmousemove) {
        this.onmousemove = onmousemove;
    }

    public void setOnmouseout(String onmouseout) {
        this.onmouseout = onmouseout;
    }

    public void setOnfocus(String onfocus) {
        this.onfocus = onfocus;
    }

    public void setOnblur(String onblur) {
        this.onblur = onblur;
    }

    public void setOnkeypress(String onkeypress) {
        this.onkeypress = onkeypress;
    }

    public void setOnkeydown(String onkeydown) {
        this.onkeydown = onkeydown;
    }

    public void setOnkeyup(String onkeyup) {
        this.onkeyup = onkeyup;
    }

    public void setOnselect(String onselect) {
        this.onselect = onselect;
    }

    public void setOnchange(String onchange) {
        this.onchange = onchange;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public void setTooltipConfig(String tooltipConfig) {
        this.tooltipConfig = tooltipConfig;
    }

    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setJavascriptTooltip(String javascriptTooltip) {
        this.javascriptTooltip = javascriptTooltip;
    }

    public void setTooltipCssClass(String tooltipCssClass) {
        this.tooltipCssClass = tooltipCssClass;
    }

    public void setTooltipDelay(String tooltipDelay) {
        this.tooltipDelay = tooltipDelay;
    }

    public void setTooltipIconPath(String tooltipIconPath) {
        this.tooltipIconPath = tooltipIconPath;
    }

    public void setLabelSeparator(String labelSeparator) {
        this.labelSeparator = labelSeparator;
    }

    public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
        this.dynamicAttributes.put(localName, String.valueOf(value));
    }

    @Override
    public void setPerformClearTagStateForTagPoolingServers(boolean performClearTagStateForTagPoolingServers) {
        super.setPerformClearTagStateForTagPoolingServers(performClearTagStateForTagPoolingServers);
    }

    @Override
    protected void clearTagStateForTagPoolingServers() {
        if (!this.getPerformClearTagStateForTagPoolingServers()) {
            return;
        }
        super.clearTagStateForTagPoolingServers();
        this.cssClass = null;
        this.cssErrorClass = null;
        this.cssStyle = null;
        this.cssErrorStyle = null;
        this.title = null;
        this.disabled = null;
        this.label = null;
        this.labelSeparator = null;
        this.labelPosition = null;
        this.requiredPosition = null;
        this.errorPosition = null;
        this.name = null;
        this.requiredLabel = null;
        this.tabindex = null;
        this.value = null;
        this.template = null;
        this.theme = null;
        this.templateDir = null;
        this.onclick = null;
        this.ondblclick = null;
        this.onmousedown = null;
        this.onmouseup = null;
        this.onmouseover = null;
        this.onmousemove = null;
        this.onmouseout = null;
        this.onfocus = null;
        this.onblur = null;
        this.onkeypress = null;
        this.onkeydown = null;
        this.onkeyup = null;
        this.onselect = null;
        this.onchange = null;
        this.accesskey = null;
        this.id = null;
        this.key = null;
        this.tooltip = null;
        this.tooltipConfig = null;
        this.javascriptTooltip = null;
        this.tooltipDelay = null;
        this.tooltipCssClass = null;
        this.tooltipIconPath = null;
        this.dynamicAttributes.clear();
    }
}

