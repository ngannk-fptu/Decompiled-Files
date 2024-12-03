/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.components.ClosingUIBean;
import org.apache.struts2.components.Form;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

public abstract class FormButton
extends ClosingUIBean {
    private static final String BUTTON_TYPE_INPUT = "input";
    private static final String BUTTON_TYPE_BUTTON = "button";
    private static final String BUTTON_TYPE_IMAGE = "image";
    protected String action;
    protected String method;
    protected String type;

    public FormButton(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        String submitType = BUTTON_TYPE_INPUT;
        if (this.type != null && (BUTTON_TYPE_BUTTON.equalsIgnoreCase(this.type) || this.supportsImageType() && BUTTON_TYPE_IMAGE.equalsIgnoreCase(this.type))) {
            submitType = this.type;
        }
        this.addParameter("type", submitType);
        if (!BUTTON_TYPE_INPUT.equals(submitType) && this.label == null) {
            this.addParameter("label", this.getParameters().get("nameValue"));
        }
        if (this.action != null || this.method != null) {
            String name;
            if (this.action != null) {
                ActionMapping mapping = new ActionMapping();
                mapping.setName(this.findString(this.action));
                if (this.method != null) {
                    mapping.setMethod(this.findString(this.method));
                }
                mapping.setExtension("");
                name = "action:" + this.actionMapper.getUriFromActionMapping(mapping);
            } else {
                name = "method:" + this.findString(this.method);
            }
            this.addParameter("name", name);
        }
    }

    @Override
    protected void populateComponentHtmlId(Form form) {
        String tmpId = "";
        if (this.id != null) {
            tmpId = this.id;
        } else {
            if (form != null && form.getParameters().get("id") != null) {
                tmpId = tmpId + form.getParameters().get("id").toString() + "_";
            }
            if (this.name != null) {
                tmpId = tmpId + this.escape(this.findString(this.name));
            } else if (this.action != null || this.method != null) {
                if (this.action != null) {
                    tmpId = tmpId + this.escape(this.findString(this.action));
                }
                if (this.method != null) {
                    tmpId = tmpId + "_" + this.escape(this.findString(this.method));
                }
            } else if (form != null) {
                tmpId = tmpId + form.getSequence();
            }
        }
        this.addParameter("id", tmpId);
        this.addParameter("escapedId", this.escape(tmpId));
    }

    protected abstract boolean supportsImageType();

    @Override
    @Inject
    public void setActionMapper(ActionMapper mapper) {
        this.actionMapper = mapper;
    }

    @StrutsTagAttribute(description="Set action attribute.")
    public void setAction(String action) {
        this.action = action;
    }

    @StrutsTagAttribute(description="Set method attribute.")
    public void setMethod(String method) {
        this.method = method;
    }

    @StrutsTagAttribute(description="The type of submit to use. Valid values are <i>input</i>, <i>button</i> and <i>image</i>.", defaultValue="input")
    public void setType(String type) {
        this.type = type;
    }
}

