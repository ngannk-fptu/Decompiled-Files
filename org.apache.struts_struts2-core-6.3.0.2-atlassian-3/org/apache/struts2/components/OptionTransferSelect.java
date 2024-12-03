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
import org.apache.struts2.components.DoubleListUIBean;
import org.apache.struts2.components.Form;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="optiontransferselect", tldTagClass="org.apache.struts2.views.jsp.ui.OptionTransferSelectTag", description="Renders an input form")
public class OptionTransferSelect
extends DoubleListUIBean {
    private static final Logger LOG = LogManager.getLogger(OptionTransferSelect.class);
    private static final String TEMPLATE = "optiontransferselect";
    protected String allowAddToLeft;
    protected String allowAddToRight;
    protected String allowAddAllToLeft;
    protected String allowAddAllToRight;
    protected String allowSelectAll;
    protected String allowUpDownOnLeft;
    protected String allowUpDownOnRight;
    protected String leftTitle;
    protected String rightTitle;
    protected String buttonCssClass;
    protected String buttonCssStyle;
    protected String addToLeftLabel;
    protected String addToRightLabel;
    protected String addAllToLeftLabel;
    protected String addAllToRightLabel;
    protected String selectAllLabel;
    protected String leftUpLabel;
    protected String leftDownlabel;
    protected String rightUpLabel;
    protected String rightDownLabel;
    protected String addToLeftOnclick;
    protected String addToRightOnclick;
    protected String addAllToLeftOnclick;
    protected String addAllToRightOnclick;
    protected String selectAllOnclick;
    protected String upDownOnLeftOnclick;
    protected String upDownOnRightOnclick;

    public OptionTransferSelect(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        Object doubleValue = null;
        if (this.doubleList != null) {
            doubleValue = this.findValue(this.doubleList);
            this.addParameter("doubleList", doubleValue);
        }
        if (StringUtils.isBlank((CharSequence)this.size)) {
            this.addParameter("size", "15");
        }
        if (StringUtils.isBlank((CharSequence)this.doubleSize)) {
            this.addParameter("doubleSize", "15");
        }
        if (StringUtils.isBlank((CharSequence)this.multiple)) {
            this.addParameter("multiple", Boolean.TRUE);
        }
        if (StringUtils.isBlank((CharSequence)this.doubleMultiple)) {
            this.addParameter("doubleMultiple", Boolean.TRUE);
        }
        if (StringUtils.isNotBlank((CharSequence)this.buttonCssClass)) {
            this.addParameter("buttonCssClass", this.buttonCssClass);
        }
        if (StringUtils.isNotBlank((CharSequence)this.buttonCssStyle)) {
            this.addParameter("buttonCssStyle", this.buttonCssStyle);
        }
        this.addParameter("allowSelectAll", this.allowSelectAll != null ? this.findValue(this.allowSelectAll, Boolean.class) : Boolean.TRUE);
        this.addParameter("allowAddToLeft", this.allowAddToLeft != null ? this.findValue(this.allowAddToLeft, Boolean.class) : Boolean.TRUE);
        this.addParameter("allowAddToRight", this.allowAddToRight != null ? this.findValue(this.allowAddToRight, Boolean.class) : Boolean.TRUE);
        this.addParameter("allowAddAllToLeft", this.allowAddAllToLeft != null ? this.findValue(this.allowAddAllToLeft, Boolean.class) : Boolean.TRUE);
        this.addParameter("allowAddAllToRight", this.allowAddAllToRight != null ? this.findValue(this.allowAddAllToRight, Boolean.class) : Boolean.TRUE);
        this.addParameter("allowUpDownOnLeft", this.allowUpDownOnLeft != null ? this.findValue(this.allowUpDownOnLeft, Boolean.class) : Boolean.TRUE);
        this.addParameter("allowUpDownOnRight", this.allowUpDownOnRight != null ? this.findValue(this.allowUpDownOnRight, Boolean.class) : Boolean.TRUE);
        if (this.leftTitle != null) {
            this.addParameter("leftTitle", this.findValue(this.leftTitle, String.class));
        }
        if (this.rightTitle != null) {
            this.addParameter("rightTitle", this.findValue(this.rightTitle, String.class));
        }
        this.addParameter("addToLeftLabel", this.addToLeftLabel != null ? this.findValue(this.addToLeftLabel, String.class) : "<-");
        this.addParameter("addToRightLabel", this.addToRightLabel != null ? this.findValue(this.addToRightLabel, String.class) : "->");
        this.addParameter("addAllToLeftLabel", this.addAllToLeftLabel != null ? this.findValue(this.addAllToLeftLabel, String.class) : "<<--");
        this.addParameter("addAllToRightLabel", this.addAllToRightLabel != null ? this.findValue(this.addAllToRightLabel, String.class) : "-->>");
        this.addParameter("selectAllLabel", this.selectAllLabel != null ? this.findValue(this.selectAllLabel, String.class) : "<*>");
        this.addParameter("leftUpLabel", this.leftUpLabel != null ? this.findValue(this.leftUpLabel, String.class) : "^");
        this.addParameter("leftDownLabel", this.leftDownlabel != null ? this.findValue(this.leftDownlabel, String.class) : "v");
        this.addParameter("rightUpLabel", this.rightUpLabel != null ? this.findValue(this.rightUpLabel, String.class) : "^");
        this.addParameter("rightDownLabel", this.rightDownLabel != null ? this.findValue(this.rightDownLabel, String.class) : "v");
        this.addParameter("selectAllOnclick", this.selectAllOnclick != null ? this.findValue(this.selectAllOnclick, String.class) : "");
        this.addParameter("addToLeftOnclick", this.addToLeftOnclick != null ? this.findValue(this.addToLeftOnclick, String.class) : "");
        this.addParameter("addToRightOnclick", this.addToRightOnclick != null ? this.findValue(this.addToRightOnclick, String.class) : "");
        this.addParameter("addAllToLeftOnclick", this.addAllToLeftOnclick != null ? this.findValue(this.addAllToLeftOnclick, String.class) : "");
        this.addParameter("addAllToRightOnclick", this.addAllToRightOnclick != null ? this.findValue(this.addAllToRightOnclick, String.class) : "");
        this.addParameter("upDownOnLeftOnclick", this.upDownOnLeftOnclick != null ? this.findValue(this.upDownOnLeftOnclick, String.class) : "");
        this.addParameter("upDownOnRightOnclick", this.upDownOnRightOnclick != null ? this.findValue(this.upDownOnRightOnclick, String.class) : "");
        Form formAncestor = (Form)this.findAncestor(Form.class);
        if (formAncestor != null) {
            this.enableAncestorFormCustomOnsubmit();
            LinkedHashMap<String, String> formOptiontransferselectIds = (LinkedHashMap<String, String>)formAncestor.getParameters().get("optiontransferselectIds");
            LinkedHashMap<String, String> formOptiontransferselectDoubleIds = (LinkedHashMap<String, String>)formAncestor.getParameters().get("optiontransferselectDoubleIds");
            if (formOptiontransferselectIds == null) {
                formOptiontransferselectIds = new LinkedHashMap<String, String>();
            }
            if (formOptiontransferselectDoubleIds == null) {
                formOptiontransferselectDoubleIds = new LinkedHashMap<String, String>();
            }
            String tmpId = (String)this.getParameters().get("id");
            String tmpHeaderKey = (String)this.getParameters().get("headerKey");
            if (tmpId != null && !formOptiontransferselectIds.containsKey(tmpId)) {
                formOptiontransferselectIds.put(tmpId, tmpHeaderKey);
            }
            String tmpDoubleId = (String)this.getParameters().get("doubleId");
            String tmpDoubleHeaderKey = (String)this.getParameters().get("doubleHeaderKey");
            if (tmpDoubleId != null && !formOptiontransferselectDoubleIds.containsKey(tmpDoubleId)) {
                formOptiontransferselectDoubleIds.put(tmpDoubleId, tmpDoubleHeaderKey);
            }
            formAncestor.getParameters().put("optiontransferselectIds", formOptiontransferselectIds);
            formAncestor.getParameters().put("optiontransferselectDoubleIds", formOptiontransferselectDoubleIds);
        } else if (LOG.isWarnEnabled()) {
            LOG.warn("form enclosing optiontransferselect " + this + " not found, auto select upon form submit of optiontransferselect will not work");
        }
    }

    public String getAddAllToLeftLabel() {
        return this.addAllToLeftLabel;
    }

    @StrutsTagAttribute(description="Set Add To Left button label")
    public void setAddAllToLeftLabel(String addAllToLeftLabel) {
        this.addAllToLeftLabel = addAllToLeftLabel;
    }

    public String getAddAllToRightLabel() {
        return this.addAllToRightLabel;
    }

    @StrutsTagAttribute(description="Set Add All To Right button label")
    public void setAddAllToRightLabel(String addAllToRightLabel) {
        this.addAllToRightLabel = addAllToRightLabel;
    }

    public String getAddToLeftLabel() {
        return this.addToLeftLabel;
    }

    @StrutsTagAttribute(description="Set Add To Left button label")
    public void setAddToLeftLabel(String addToLeftLabel) {
        this.addToLeftLabel = addToLeftLabel;
    }

    public String getAddToRightLabel() {
        return this.addToRightLabel;
    }

    @StrutsTagAttribute(description="Set Add To Right button label")
    public void setAddToRightLabel(String addToRightLabel) {
        this.addToRightLabel = addToRightLabel;
    }

    public String getAllowAddAllToLeft() {
        return this.allowAddAllToLeft;
    }

    @StrutsTagAttribute(description="Enable Add All To Left button")
    public void setAllowAddAllToLeft(String allowAddAllToLeft) {
        this.allowAddAllToLeft = allowAddAllToLeft;
    }

    public String getAllowAddAllToRight() {
        return this.allowAddAllToRight;
    }

    @StrutsTagAttribute(description="Enable Add All To Right button")
    public void setAllowAddAllToRight(String allowAddAllToRight) {
        this.allowAddAllToRight = allowAddAllToRight;
    }

    public String getAllowAddToLeft() {
        return this.allowAddToLeft;
    }

    @StrutsTagAttribute(description="Enable Add To Left button")
    public void setAllowAddToLeft(String allowAddToLeft) {
        this.allowAddToLeft = allowAddToLeft;
    }

    public String getAllowAddToRight() {
        return this.allowAddToRight;
    }

    @StrutsTagAttribute(description="Enable Add To Right button")
    public void setAllowAddToRight(String allowAddToRight) {
        this.allowAddToRight = allowAddToRight;
    }

    public String getLeftTitle() {
        return this.leftTitle;
    }

    @StrutsTagAttribute(description="Enable up / down on the left side")
    public void setAllowUpDownOnLeft(String allowUpDownOnLeft) {
        this.allowUpDownOnLeft = allowUpDownOnLeft;
    }

    public String getAllowUpDownOnLeft() {
        return this.allowUpDownOnLeft;
    }

    @StrutsTagAttribute(description="Enable up / down on the right side")
    public void setAllowUpDownOnRight(String allowUpDownOnRight) {
        this.allowUpDownOnRight = allowUpDownOnRight;
    }

    public String getAllowUpDownOnRight() {
        return this.allowUpDownOnRight;
    }

    @StrutsTagAttribute(description="Set Left title")
    public void setLeftTitle(String leftTitle) {
        this.leftTitle = leftTitle;
    }

    public String getRightTitle() {
        return this.rightTitle;
    }

    @StrutsTagAttribute(description="Set Right title")
    public void setRightTitle(String rightTitle) {
        this.rightTitle = rightTitle;
    }

    @StrutsTagAttribute(description="Enable Select All button")
    public void setAllowSelectAll(String allowSelectAll) {
        this.allowSelectAll = allowSelectAll;
    }

    public String getAllowSelectAll() {
        return this.allowSelectAll;
    }

    @StrutsTagAttribute(description="Set Select All button label")
    public void setSelectAllLabel(String selectAllLabel) {
        this.selectAllLabel = selectAllLabel;
    }

    public String getSelectAllLabel() {
        return this.selectAllLabel;
    }

    @StrutsTagAttribute(description="Set buttons css class")
    public void setButtonCssClass(String buttonCssClass) {
        this.buttonCssClass = buttonCssClass;
    }

    public String getButtonCssClass() {
        return this.buttonCssClass;
    }

    @StrutsTagAttribute(description="Set button css style")
    public void setButtonCssStyle(String buttonCssStyle) {
        this.buttonCssStyle = buttonCssStyle;
    }

    public String getButtonCssStyle() {
        return this.buttonCssStyle;
    }

    @StrutsTagAttribute(description="Up label for the left side")
    public void setLeftUpLabel(String leftUpLabel) {
        this.leftUpLabel = leftUpLabel;
    }

    public String getLeftUpLabel() {
        return this.leftUpLabel;
    }

    @StrutsTagAttribute(description="Down label for the left side.")
    public void setLeftDownLabel(String leftDownLabel) {
        this.leftDownlabel = leftDownLabel;
    }

    public String getLeftDownLabel() {
        return this.leftDownlabel;
    }

    @StrutsTagAttribute(description="Up label for the right side.")
    public void setRightUpLabel(String rightUpLabel) {
        this.rightUpLabel = rightUpLabel;
    }

    public String getRightUpLabel() {
        return this.rightUpLabel;
    }

    @StrutsTagAttribute(description="Down label for the left side.")
    public void setRightDownLabel(String rightDownlabel) {
        this.rightDownLabel = rightDownlabel;
    }

    public String getRightDownLabel() {
        return this.rightDownLabel;
    }

    public String getAddAllToLeftOnclick() {
        return this.addAllToLeftOnclick;
    }

    @StrutsTagAttribute(description="Javascript to run after Add All To Left button pressed")
    public void setAddAllToLeftOnclick(String addAllToLeftOnclick) {
        this.addAllToLeftOnclick = addAllToLeftOnclick;
    }

    public String getAddAllToRightOnclick() {
        return this.addAllToRightOnclick;
    }

    @StrutsTagAttribute(description="Javascript to run after Add All To Right button pressed")
    public void setAddAllToRightOnclick(String addAllToRightOnclick) {
        this.addAllToRightOnclick = addAllToRightOnclick;
    }

    public String getAddToLeftOnclick() {
        return this.addToLeftOnclick;
    }

    @StrutsTagAttribute(description="Javascript to run after Add To Left button pressed")
    public void setAddToLeftOnclick(String addToLeftOnclick) {
        this.addToLeftOnclick = addToLeftOnclick;
    }

    public String getAddToRightOnclick() {
        return this.addToRightOnclick;
    }

    @StrutsTagAttribute(description="Javascript to run after Add To Right button pressed")
    public void setAddToRightOnclick(String addToRightOnclick) {
        this.addToRightOnclick = addToRightOnclick;
    }

    @StrutsTagAttribute(description="Javascript to run after up / down on the left side buttons pressed")
    public void setUpDownOnLeftOnclick(String upDownOnLeftOnclick) {
        this.upDownOnLeftOnclick = upDownOnLeftOnclick;
    }

    public String getUpDownOnLeftOnclick() {
        return this.upDownOnLeftOnclick;
    }

    @StrutsTagAttribute(description="Javascript to run after up / down on the right side buttons pressed")
    public void setUpDownOnRightOnclick(String upDownOnRightOnclick) {
        this.upDownOnRightOnclick = upDownOnRightOnclick;
    }

    public String getUpDownOnRightOnclick() {
        return this.upDownOnRightOnclick;
    }

    @StrutsTagAttribute(description="Javascript to run after Select All button pressed")
    public void setSelectAllOnclick(String selectAllOnclick) {
        this.selectAllOnclick = selectAllOnclick;
    }

    public String getSelectAllOnclick() {
        return this.selectAllOnclick;
    }
}

