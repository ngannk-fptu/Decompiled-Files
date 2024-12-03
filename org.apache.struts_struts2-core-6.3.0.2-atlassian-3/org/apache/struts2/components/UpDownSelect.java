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
import org.apache.struts2.components.Select;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="updownselect", tldTagClass="org.apache.struts2.views.jsp.ui.UpDownSelectTag", description="Create a Select component with buttons to move the elements in the select component up and down")
public class UpDownSelect
extends Select {
    private static final Logger LOG = LogManager.getLogger(UpDownSelect.class);
    public static final String TEMPLATE = "updownselect";
    protected String allowMoveUp;
    protected String allowMoveDown;
    protected String allowSelectAll;
    protected String moveUpLabel;
    protected String moveDownLabel;
    protected String selectAllLabel;

    @Override
    public String getDefaultTemplate() {
        return TEMPLATE;
    }

    public UpDownSelect(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    public void evaluateParams() {
        Form ancestorForm;
        super.evaluateParams();
        if (StringUtils.isBlank((CharSequence)this.size)) {
            this.addParameter("size", "5");
        }
        if (StringUtils.isBlank((CharSequence)this.multiple)) {
            this.addParameter("multiple", Boolean.TRUE);
        }
        if (this.allowMoveUp != null) {
            this.addParameter("allowMoveUp", this.findValue(this.allowMoveUp, Boolean.class));
        }
        if (this.allowMoveDown != null) {
            this.addParameter("allowMoveDown", this.findValue(this.allowMoveDown, Boolean.class));
        }
        if (this.allowSelectAll != null) {
            this.addParameter("allowSelectAll", this.findValue(this.allowSelectAll, Boolean.class));
        }
        if (this.moveUpLabel != null) {
            this.addParameter("moveUpLabel", this.findString(this.moveUpLabel));
        }
        if (this.moveDownLabel != null) {
            this.addParameter("moveDownLabel", this.findString(this.moveDownLabel));
        }
        if (this.selectAllLabel != null) {
            this.addParameter("selectAllLabel", this.findString(this.selectAllLabel));
        }
        if ((ancestorForm = (Form)this.findAncestor(Form.class)) != null) {
            this.enableAncestorFormCustomOnsubmit();
            LinkedHashMap<Object, Object> m = (LinkedHashMap<Object, Object>)ancestorForm.getParameters().get("updownselectIds");
            if (m == null) {
                m = new LinkedHashMap<Object, Object>();
            }
            m.put(this.getParameters().get("id"), this.getParameters().get("headerKey"));
            ancestorForm.getParameters().put("updownselectIds", m);
        } else if (LOG.isWarnEnabled()) {
            LOG.warn("no ancestor form found for updownselect " + this + ", therefore autoselect of all elements upon form submission will not work ");
        }
    }

    public String getAllowMoveUp() {
        return this.allowMoveUp;
    }

    @StrutsTagAttribute(description="Whether move up button should be displayed", type="Boolean", defaultValue="true")
    public void setAllowMoveUp(String allowMoveUp) {
        this.allowMoveUp = allowMoveUp;
    }

    public String getAllowMoveDown() {
        return this.allowMoveDown;
    }

    @StrutsTagAttribute(description="Whether move down button should be displayed", type="Boolean", defaultValue="true")
    public void setAllowMoveDown(String allowMoveDown) {
        this.allowMoveDown = allowMoveDown;
    }

    public String getAllowSelectAll() {
        return this.allowSelectAll;
    }

    @StrutsTagAttribute(description="Whether or not select all button should be displayed", type="Boolean", defaultValue="true")
    public void setAllowSelectAll(String allowSelectAll) {
        this.allowSelectAll = allowSelectAll;
    }

    public String getMoveUpLabel() {
        return this.moveUpLabel;
    }

    @StrutsTagAttribute(description="Text to display on the move up button", defaultValue="^")
    public void setMoveUpLabel(String moveUpLabel) {
        this.moveUpLabel = moveUpLabel;
    }

    public String getMoveDownLabel() {
        return this.moveDownLabel;
    }

    @StrutsTagAttribute(description="Text to display on the move down button", defaultValue="v")
    public void setMoveDownLabel(String moveDownLabel) {
        this.moveDownLabel = moveDownLabel;
    }

    public String getSelectAllLabel() {
        return this.selectAllLabel;
    }

    @StrutsTagAttribute(description="Text to display on the select all button", defaultValue="*")
    public void setSelectAllLabel(String selectAllLabel) {
        this.selectAllLabel = selectAllLabel;
    }
}

