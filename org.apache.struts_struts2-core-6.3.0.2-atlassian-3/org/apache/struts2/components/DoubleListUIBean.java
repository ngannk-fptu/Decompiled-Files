/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.Form;
import org.apache.struts2.components.ListUIBean;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

public abstract class DoubleListUIBean
extends ListUIBean {
    protected String emptyOption;
    protected String headerKey;
    protected String headerValue;
    protected String multiple;
    protected String size;
    protected String doubleList;
    protected String doubleListKey;
    protected String doubleListValue;
    protected String doubleListCssClass;
    protected String doubleListCssStyle;
    protected String doubleListTitle;
    protected String doubleName;
    protected String doubleValue;
    protected String formName;
    protected String doubleId;
    protected String doubleDisabled;
    protected String doubleMultiple;
    protected String doubleSize;
    protected String doubleHeaderKey;
    protected String doubleHeaderValue;
    protected String doubleEmptyOption;
    protected String doubleCssClass;
    protected String doubleCssStyle;
    protected String doubleOnclick;
    protected String doubleOndblclick;
    protected String doubleOnmousedown;
    protected String doubleOnmouseup;
    protected String doubleOnmouseover;
    protected String doubleOnmousemove;
    protected String doubleOnmouseout;
    protected String doubleOnfocus;
    protected String doubleOnblur;
    protected String doubleOnkeypress;
    protected String doubleOnkeydown;
    protected String doubleOnkeyup;
    protected String doubleOnselect;
    protected String doubleOnchange;
    protected String doubleAccesskey;

    public DoubleListUIBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        if (this.emptyOption != null) {
            this.addParameter("emptyOption", this.findValue(this.emptyOption, Boolean.class));
        }
        if (this.multiple != null) {
            this.addParameter("multiple", this.findValue(this.multiple, Boolean.class));
        }
        if (this.size != null) {
            this.addParameter("size", this.findString(this.size));
        }
        if (this.headerKey != null && this.headerValue != null) {
            this.addParameter("headerKey", this.findString(this.headerKey));
            this.addParameter("headerValue", this.findString(this.headerValue));
        }
        if (this.doubleMultiple != null) {
            this.addParameter("doubleMultiple", this.findValue(this.doubleMultiple, Boolean.class));
        }
        if (this.doubleSize != null) {
            this.addParameter("doubleSize", this.findString(this.doubleSize));
        }
        if (this.doubleDisabled != null) {
            this.addParameter("doubleDisabled", this.findValue(this.doubleDisabled, Boolean.class));
        }
        if (this.doubleName != null) {
            this.addParameter("doubleName", this.findString(this.doubleName));
        }
        if (this.doubleList != null) {
            this.addParameter("doubleList", this.doubleList);
        }
        Object tmpDoubleList = this.findValue(this.doubleList);
        if (this.doubleListKey != null) {
            this.addParameter("doubleListKey", this.doubleListKey);
        } else if (tmpDoubleList instanceof Map) {
            this.addParameter("doubleListKey", "key");
        }
        if (this.doubleListValue != null) {
            this.doubleListValue = this.stripExpression(this.doubleListValue);
            this.addParameter("doubleListValue", this.doubleListValue);
        } else if (tmpDoubleList instanceof Map) {
            this.addParameter("doubleListValue", "value");
        }
        if (this.doubleListCssClass != null) {
            this.addParameter("doubleListCssClass", this.findString(this.doubleListCssClass));
        }
        if (this.doubleListCssStyle != null) {
            this.addParameter("doubleListCssStyle", this.findString(this.doubleListCssStyle));
        }
        if (this.doubleListTitle != null) {
            this.addParameter("doubleListTitle", this.findString(this.doubleListTitle));
        }
        if (this.formName != null) {
            this.addParameter("formName", this.findString(this.formName));
        } else {
            Component form = this.findAncestor(Form.class);
            if (form != null) {
                this.addParameter("formName", form.getParameters().get("name"));
            }
        }
        Class valueClazz = this.getValueClassType();
        if (valueClazz != null) {
            if (this.doubleValue != null) {
                this.addParameter("doubleNameValue", this.findValue(this.doubleValue, valueClazz));
            } else if (this.doubleName != null) {
                this.addParameter("doubleNameValue", this.findValue(this.doubleName, valueClazz));
            }
        } else if (this.doubleValue != null) {
            this.addParameter("doubleNameValue", this.findValue(this.doubleValue));
        } else if (this.doubleName != null) {
            this.addParameter("doubleNameValue", this.findValue(this.doubleName));
        }
        Form form = (Form)this.findAncestor(Form.class);
        if (this.doubleId != null) {
            this.addParameter("doubleId", this.doubleId);
        } else if (form != null) {
            this.addParameter("doubleId", form.getParameters().get("id") + "_" + this.escape(this.doubleName != null ? this.findString(this.doubleName) : null));
        } else {
            this.addParameter("doubleId", this.escape(this.doubleName != null ? this.findString(this.doubleName) : null));
        }
        if (this.doubleOnclick != null) {
            this.addParameter("doubleOnclick", this.findString(this.doubleOnclick));
        }
        if (this.doubleOndblclick != null) {
            this.addParameter("doubleOndblclick", this.findString(this.doubleOndblclick));
        }
        if (this.doubleOnmousedown != null) {
            this.addParameter("doubleOnmousedown", this.findString(this.doubleOnmousedown));
        }
        if (this.doubleOnmouseup != null) {
            this.addParameter("doubleOnmouseup", this.findString(this.doubleOnmouseup));
        }
        if (this.doubleOnmouseover != null) {
            this.addParameter("doubleOnmouseover", this.findString(this.doubleOnmouseover));
        }
        if (this.doubleOnmousemove != null) {
            this.addParameter("doubleOnmousemove", this.findString(this.doubleOnmousemove));
        }
        if (this.doubleOnmouseout != null) {
            this.addParameter("doubleOnmouseout", this.findString(this.doubleOnmouseout));
        }
        if (this.doubleOnfocus != null) {
            this.addParameter("doubleOnfocus", this.findString(this.doubleOnfocus));
        }
        if (this.doubleOnblur != null) {
            this.addParameter("doubleOnblur", this.findString(this.doubleOnblur));
        }
        if (this.doubleOnkeypress != null) {
            this.addParameter("doubleOnkeypress", this.findString(this.doubleOnkeypress));
        }
        if (this.doubleOnkeydown != null) {
            this.addParameter("doubleOnkeydown", this.findString(this.doubleOnkeydown));
        }
        if (this.doubleOnselect != null) {
            this.addParameter("doubleOnselect", this.findString(this.doubleOnselect));
        }
        if (this.doubleOnchange != null) {
            this.addParameter("doubleOnchange", this.findString(this.doubleOnchange));
        }
        if (this.doubleCssClass != null) {
            this.addParameter("doubleCss", this.findString(this.doubleCssClass));
        }
        if (this.doubleCssStyle != null) {
            this.addParameter("doubleStyle", this.findString(this.doubleCssStyle));
        }
        if (this.doubleHeaderKey != null && this.doubleHeaderValue != null) {
            this.addParameter("doubleHeaderKey", this.findString(this.doubleHeaderKey));
            this.addParameter("doubleHeaderValue", this.findString(this.doubleHeaderValue));
        }
        if (this.doubleEmptyOption != null) {
            this.addParameter("doubleEmptyOption", this.findValue(this.doubleEmptyOption, Boolean.class));
        }
        if (this.doubleAccesskey != null) {
            this.addParameter("doubleAccesskey", this.findString(this.doubleAccesskey));
        }
    }

    @StrutsTagAttribute(description="The second iterable source to populate from.", required=true)
    public void setDoubleList(String doubleList) {
        this.doubleList = doubleList;
    }

    @StrutsTagAttribute(description="The key expression to use for second list")
    public void setDoubleListKey(String doubleListKey) {
        this.doubleListKey = doubleListKey;
    }

    @StrutsTagAttribute(description="The value expression to use for second list")
    public void setDoubleListValue(String doubleListValue) {
        this.doubleListValue = doubleListValue;
    }

    @StrutsTagAttribute(description="Property of second list objects to get css class from")
    public void setDoubleListCssClass(String doubleListCssClass) {
        this.doubleListCssClass = doubleListCssClass;
    }

    @StrutsTagAttribute(description="Property of second list objects to get css style from")
    public void setDoubleListCssStyle(String doubleListCssStyle) {
        this.doubleListCssStyle = doubleListCssStyle;
    }

    @StrutsTagAttribute(description="Property of second list objects to get title from")
    public void setDoubleListTitle(String doubleListTitle) {
        this.doubleListTitle = doubleListTitle;
    }

    @StrutsTagAttribute(description="The name for complete component", required=true)
    public void setDoubleName(String doubleName) {
        this.doubleName = doubleName;
    }

    @StrutsTagAttribute(description="The value expression for complete component")
    public void setDoubleValue(String doubleValue) {
        this.doubleValue = doubleValue;
    }

    @StrutsTagAttribute(description="The form name this component resides in and populates to")
    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getFormName() {
        return this.formName;
    }

    @StrutsTagAttribute(description="The css class for the second list")
    public void setDoubleCssClass(String doubleCssClass) {
        this.doubleCssClass = doubleCssClass;
    }

    public String getDoubleCssClass() {
        return this.doubleCssClass;
    }

    @StrutsTagAttribute(description="The css style for the second list")
    public void setDoubleCssStyle(String doubleCssStyle) {
        this.doubleCssStyle = doubleCssStyle;
    }

    public String getDoubleCssStyle() {
        return this.doubleCssStyle;
    }

    @StrutsTagAttribute(description="The header key for the second list")
    public void setDoubleHeaderKey(String doubleHeaderKey) {
        this.doubleHeaderKey = doubleHeaderKey;
    }

    public String getDoubleHeaderKey() {
        return this.doubleHeaderKey;
    }

    @StrutsTagAttribute(description="The header value for the second list")
    public void setDoubleHeaderValue(String doubleHeaderValue) {
        this.doubleHeaderValue = doubleHeaderValue;
    }

    public String getDoubleHeaderValue() {
        return this.doubleHeaderValue;
    }

    @StrutsTagAttribute(description="Decides if the second list will add an empty option")
    public void setDoubleEmptyOption(String doubleEmptyOption) {
        this.doubleEmptyOption = doubleEmptyOption;
    }

    public String getDoubleEmptyOption() {
        return this.doubleEmptyOption;
    }

    public String getDoubleDisabled() {
        return this.doubleDisabled;
    }

    @StrutsTagAttribute(description="Decides if a disable attribute should be added to the second list")
    public void setDoubleDisabled(String doubleDisabled) {
        this.doubleDisabled = doubleDisabled;
    }

    public String getDoubleId() {
        return this.doubleId;
    }

    @StrutsTagAttribute(description="The id of the second list")
    public void setDoubleId(String doubleId) {
        this.doubleId = doubleId;
    }

    public String getDoubleMultiple() {
        return this.doubleMultiple;
    }

    @StrutsTagAttribute(description=" Decides if multiple attribute should be set on the second list")
    public void setDoubleMultiple(String doubleMultiple) {
        this.doubleMultiple = doubleMultiple;
    }

    public String getDoubleOnblur() {
        return this.doubleOnblur;
    }

    @StrutsTagAttribute(description="Set the onblur attribute of the second list")
    public void setDoubleOnblur(String doubleOnblur) {
        this.doubleOnblur = doubleOnblur;
    }

    public String getDoubleOnchange() {
        return this.doubleOnchange;
    }

    @StrutsTagAttribute(description="Set the onchange attribute of the second list")
    public void setDoubleOnchange(String doubleOnchange) {
        this.doubleOnchange = doubleOnchange;
    }

    public String getDoubleOnclick() {
        return this.doubleOnclick;
    }

    @StrutsTagAttribute(description="Set the onclick attribute of the second list")
    public void setDoubleOnclick(String doubleOnclick) {
        this.doubleOnclick = doubleOnclick;
    }

    public String getDoubleOndblclick() {
        return this.doubleOndblclick;
    }

    @StrutsTagAttribute(description="Set the ondbclick attribute of the second list")
    public void setDoubleOndblclick(String doubleOndblclick) {
        this.doubleOndblclick = doubleOndblclick;
    }

    public String getDoubleOnfocus() {
        return this.doubleOnfocus;
    }

    @StrutsTagAttribute(description="Set the onfocus attribute of the second list")
    public void setDoubleOnfocus(String doubleOnfocus) {
        this.doubleOnfocus = doubleOnfocus;
    }

    public String getDoubleOnkeydown() {
        return this.doubleOnkeydown;
    }

    @StrutsTagAttribute(description="Set the onkeydown attribute of the second list")
    public void setDoubleOnkeydown(String doubleOnkeydown) {
        this.doubleOnkeydown = doubleOnkeydown;
    }

    public String getDoubleOnkeypress() {
        return this.doubleOnkeypress;
    }

    @StrutsTagAttribute(description="Set the onkeypress attribute of the second list")
    public void setDoubleOnkeypress(String doubleOnkeypress) {
        this.doubleOnkeypress = doubleOnkeypress;
    }

    public String getDoubleOnkeyup() {
        return this.doubleOnkeyup;
    }

    @StrutsTagAttribute(description="Set the onkeyup attribute of the second list")
    public void setDoubleOnkeyup(String doubleOnkeyup) {
        this.doubleOnkeyup = doubleOnkeyup;
    }

    public String getDoubleOnmousedown() {
        return this.doubleOnmousedown;
    }

    @StrutsTagAttribute(description="Set the onmousedown attribute of the second list")
    public void setDoubleOnmousedown(String doubleOnmousedown) {
        this.doubleOnmousedown = doubleOnmousedown;
    }

    public String getDoubleOnmousemove() {
        return this.doubleOnmousemove;
    }

    @StrutsTagAttribute(description="Set the onmousemove attribute of the second list")
    public void setDoubleOnmousemove(String doubleOnmousemove) {
        this.doubleOnmousemove = doubleOnmousemove;
    }

    public String getDoubleOnmouseout() {
        return this.doubleOnmouseout;
    }

    @StrutsTagAttribute(description="Set the onmouseout attribute of the second list")
    public void setDoubleOnmouseout(String doubleOnmouseout) {
        this.doubleOnmouseout = doubleOnmouseout;
    }

    public String getDoubleOnmouseover() {
        return this.doubleOnmouseover;
    }

    @StrutsTagAttribute(description="Set the onmouseover attribute of the second list")
    public void setDoubleOnmouseover(String doubleOnmouseover) {
        this.doubleOnmouseover = doubleOnmouseover;
    }

    public String getDoubleOnmouseup() {
        return this.doubleOnmouseup;
    }

    @StrutsTagAttribute(description="Set the onmouseup attribute of the second list")
    public void setDoubleOnmouseup(String doubleOnmouseup) {
        this.doubleOnmouseup = doubleOnmouseup;
    }

    public String getDoubleOnselect() {
        return this.doubleOnselect;
    }

    @StrutsTagAttribute(description="Set the onselect attribute of the second list")
    public void setDoubleOnselect(String doubleOnselect) {
        this.doubleOnselect = doubleOnselect;
    }

    public String getDoubleSize() {
        return this.doubleSize;
    }

    @StrutsTagAttribute(description="Set the size attribute of the second list")
    public void setDoubleSize(String doubleSize) {
        this.doubleSize = doubleSize;
    }

    public String getDoubleList() {
        return this.doubleList;
    }

    public String getDoubleListKey() {
        return this.doubleListKey;
    }

    public String getDoubleListValue() {
        return this.doubleListValue;
    }

    public String getDoubleName() {
        return this.doubleName;
    }

    public String getDoubleValue() {
        return this.doubleValue;
    }

    @StrutsTagAttribute(description="Decides of an empty option is to be inserted in the second list", type="Boolean", defaultValue="false")
    public void setEmptyOption(String emptyOption) {
        this.emptyOption = emptyOption;
    }

    @StrutsTagAttribute(description="Set the header key of the second list. Must not be empty! '-1' and '' is correct, '' is bad.")
    public void setHeaderKey(String headerKey) {
        this.headerKey = headerKey;
    }

    @StrutsTagAttribute(description=" Set the header value of the second list")
    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    @StrutsTagAttribute(description="Creates a multiple select. The tag will pre-select multiple values if the values are passed as an Array (of appropriate types) via the value attribute.")
    public void setMultiple(String multiple) {
        this.multiple = multiple;
    }

    @StrutsTagAttribute(description="Size of the element box (# of elements to show)", type="Integer")
    public void setSize(String size) {
        this.size = size;
    }

    @StrutsTagAttribute(description="Set the html accesskey attribute.")
    public void setDoubleAccesskey(String doubleAccesskey) {
        this.doubleAccesskey = doubleAccesskey;
    }
}

