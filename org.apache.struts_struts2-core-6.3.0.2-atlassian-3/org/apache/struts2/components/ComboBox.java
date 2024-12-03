/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.components.TextField;
import org.apache.struts2.util.MakeIterator;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="combobox", tldTagClass="org.apache.struts2.views.jsp.ui.ComboBoxTag", description="Widget that fills a text box from a select")
public class ComboBox
extends TextField {
    public static final String TEMPLATE = "combobox";
    protected String list;
    protected String listKey;
    protected String listValue;
    protected String headerKey;
    protected String headerValue;
    protected String emptyOption;

    public ComboBox(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        Object value = this.findListValue();
        if (this.headerKey != null) {
            this.addParameter("headerKey", this.findString(this.headerKey));
        }
        if (this.headerValue != null) {
            this.addParameter("headerValue", this.findString(this.headerValue));
        }
        if (this.emptyOption != null) {
            this.addParameter("emptyOption", this.findValue(this.emptyOption, Boolean.class));
        }
        if (value != null) {
            if (value instanceof Collection) {
                Collection tmp = (Collection)value;
                this.addParameter("list", tmp);
                if (this.listKey != null) {
                    this.addParameter("listKey", this.listKey);
                }
                if (this.listValue != null) {
                    this.addParameter("listValue", this.listValue);
                }
            } else if (value instanceof Map) {
                Map tmp = (Map)value;
                this.addParameter("list", MakeIterator.convert(tmp));
                this.addParameter("listKey", "key");
                this.addParameter("listValue", "value");
            } else {
                Iterator i = MakeIterator.convert(value);
                this.addParameter("list", i);
                if (this.listKey != null) {
                    this.addParameter("listKey", this.listKey);
                }
                if (this.listValue != null) {
                    this.addParameter("listValue", this.listValue);
                }
            }
        }
    }

    protected Object findListValue() {
        return this.findValue(this.list, "list", "You must specify a collection/array/map/enumeration/iterator. Example: people or people.{name}");
    }

    @StrutsTagAttribute(description="Iterable source to populate from. If this is missing, the select widget is simply not displayed.", required=true)
    public void setList(String list) {
        this.list = list;
    }

    @StrutsTagAttribute(description="Decide if an empty option is to be inserted. Default false.")
    public void setEmptyOption(String emptyOption) {
        this.emptyOption = emptyOption;
    }

    @StrutsTagAttribute(description="Set the header key for the header option.")
    public void setHeaderKey(String headerKey) {
        this.headerKey = headerKey;
    }

    @StrutsTagAttribute(description="Set the header value for the header option.")
    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    @StrutsTagAttribute(description="Set the key used to retrieve the option key.")
    public void setListKey(String listKey) {
        this.listKey = listKey;
    }

    @StrutsTagAttribute(description="Set the value used to retrieve the option value.")
    public void setListValue(String listValue) {
        this.listValue = listValue;
    }
}

