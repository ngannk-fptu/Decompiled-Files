/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import java.util.LinkedHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.components.Form;
import org.apache.struts2.components.ListUIBean;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="inputtransferselect", tldTagClass="org.apache.struts2.views.jsp.ui.InputTransferSelectTag", description="Renders an input form")
public class InputTransferSelect
extends ListUIBean {
    private static final Logger LOG = LogManager.getLogger(InputTransferSelect.class);
    private static final String TEMPLATE = "inputtransferselect";
    protected String size;
    protected String multiple;
    protected String allowRemoveAll;
    protected String allowUpDown;
    protected String leftTitle;
    protected String rightTitle;
    protected String buttonCssClass;
    protected String buttonCssStyle;
    protected String addLabel;
    protected String removeLabel;
    protected String removeAllLabel;
    protected String upLabel;
    protected String downLabel;
    protected String headerKey;
    protected String headerValue;

    public InputTransferSelect(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    public void evaluateExtraParams() {
        Form formAncestor;
        super.evaluateExtraParams();
        if (StringUtils.isBlank((CharSequence)this.size)) {
            this.addParameter("size", "5");
        }
        if (StringUtils.isBlank((CharSequence)this.multiple)) {
            this.addParameter("multiple", Boolean.TRUE);
        }
        this.addParameter("allowUpDown", this.allowUpDown != null ? this.findValue(this.allowUpDown, Boolean.class) : Boolean.TRUE);
        this.addParameter("allowRemoveAll", this.allowRemoveAll != null ? this.findValue(this.allowRemoveAll, Boolean.class) : Boolean.TRUE);
        if (this.leftTitle != null) {
            this.addParameter("leftTitle", this.findValue(this.leftTitle, String.class));
        }
        if (this.rightTitle != null) {
            this.addParameter("rightTitle", this.findValue(this.rightTitle, String.class));
        }
        if (StringUtils.isNotBlank((CharSequence)this.buttonCssClass)) {
            this.addParameter("buttonCssClass", this.buttonCssClass);
        }
        if (StringUtils.isNotBlank((CharSequence)this.buttonCssStyle)) {
            this.addParameter("buttonCssStyle", this.buttonCssStyle);
        }
        this.addParameter("addLabel", this.addLabel != null ? this.findValue(this.addLabel, String.class) : "->");
        this.addParameter("removeLabel", this.removeLabel != null ? this.findValue(this.removeLabel, String.class) : "<-");
        this.addParameter("removeAllLabel", this.removeAllLabel != null ? this.findValue(this.removeAllLabel, String.class) : "<<--");
        this.addParameter("upLabel", this.upLabel != null ? this.findValue(this.upLabel, String.class) : "^");
        this.addParameter("downLabel", this.downLabel != null ? this.findValue(this.downLabel, String.class) : "v");
        if (this.headerKey != null && this.headerValue != null) {
            this.addParameter("headerKey", this.findString(this.headerKey));
            this.addParameter("headerValue", this.findString(this.headerValue));
        }
        if ((formAncestor = (Form)this.findAncestor(Form.class)) != null) {
            this.enableAncestorFormCustomOnsubmit();
            LinkedHashMap<String, String> formInputtransferselectIds = (LinkedHashMap<String, String>)formAncestor.getParameters().get("inputtransferselectIds");
            if (formInputtransferselectIds == null) {
                formInputtransferselectIds = new LinkedHashMap<String, String>();
            }
            String tmpId = (String)this.getParameters().get("id");
            String tmpHeaderKey = (String)this.getParameters().get("headerKey");
            if (tmpId != null && !formInputtransferselectIds.containsKey(tmpId)) {
                formInputtransferselectIds.put(tmpId, tmpHeaderKey);
            }
            formAncestor.getParameters().put("inputtransferselectIds", formInputtransferselectIds);
        } else if (LOG.isWarnEnabled()) {
            LOG.warn("form enclosing inputtransferselect " + this + " not found, auto select upon form submit of inputtransferselect will not work");
        }
    }

    public String getSize() {
        return this.size;
    }

    @StrutsTagAttribute(description="the size of the select box")
    public void setSize(String size) {
        this.size = size;
    }

    public String getMultiple() {
        return this.multiple;
    }

    @StrutsTagAttribute(description="Determine whether or not multiple entries are shown")
    public void setMultiple(String multiple) {
        this.multiple = multiple;
    }

    public String getAllowRemoveAll() {
        return this.allowRemoveAll;
    }

    @StrutsTagAttribute(description="Determine whether the remove all button will display")
    public void setAllowRemoveAll(String allowRemoveAll) {
        this.allowRemoveAll = allowRemoveAll;
    }

    public String getAllowUpDown() {
        return this.allowUpDown;
    }

    @StrutsTagAttribute(description="Determine whether items in the list can be reordered")
    public void setAllowUpDown(String allowUpDown) {
        this.allowUpDown = allowUpDown;
    }

    public String getLeftTitle() {
        return this.leftTitle;
    }

    @StrutsTagAttribute(description="the left hand title")
    public void setLeftTitle(String leftTitle) {
        this.leftTitle = leftTitle;
    }

    public String getRightTitle() {
        return this.rightTitle;
    }

    @StrutsTagAttribute(description="the right hand title")
    public void setRightTitle(String rightTitle) {
        this.rightTitle = rightTitle;
    }

    public String getButtonCssClass() {
        return this.buttonCssClass;
    }

    @StrutsTagAttribute(description="the css class used for rendering buttons")
    public void setButtonCssClass(String buttonCssClass) {
        this.buttonCssClass = buttonCssClass;
    }

    public String getButtonCssStyle() {
        return this.buttonCssStyle;
    }

    @StrutsTagAttribute(description="the css style used for rendering buttons")
    public void setButtonCssStyle(String buttonCssStyle) {
        this.buttonCssStyle = buttonCssStyle;
    }

    public String getAddLabel() {
        return this.addLabel;
    }

    @StrutsTagAttribute(description="the label used for the add button")
    public void setAddLabel(String addLabel) {
        this.addLabel = addLabel;
    }

    public String getRemoveLabel() {
        return this.removeLabel;
    }

    @StrutsTagAttribute(description="the label used for the remove button")
    public void setRemoveLabel(String removeLabel) {
        this.removeLabel = removeLabel;
    }

    public String getRemoveAllLabel() {
        return this.removeAllLabel;
    }

    @StrutsTagAttribute(description="the label used for the remove all button")
    public void setRemoveAllLabel(String removeAllLabel) {
        this.removeAllLabel = removeAllLabel;
    }

    public String getUpLabel() {
        return this.upLabel;
    }

    @StrutsTagAttribute(description="the label used for the up button")
    public void setUpLabel(String upLabel) {
        this.upLabel = upLabel;
    }

    public String getDownLabel() {
        return this.downLabel;
    }

    @StrutsTagAttribute(description="the label used for the down button")
    public void setDownLabel(String downLabel) {
        this.downLabel = downLabel;
    }

    public String getHeaderKey() {
        return this.headerKey;
    }

    @StrutsTagAttribute(description="the header key of the select box")
    public void setHeaderKey(String headerKey) {
        this.headerKey = headerKey;
    }

    public String getHeaderValue() {
        return this.headerValue;
    }

    @StrutsTagAttribute(description="the header value of the select box")
    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }
}

