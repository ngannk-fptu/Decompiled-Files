/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.components.UIBean;
import org.apache.struts2.util.ContainUtil;
import org.apache.struts2.util.MakeIterator;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

public abstract class ListUIBean
extends UIBean {
    protected Object list;
    protected String listKey;
    protected String listValueKey;
    protected String listValue;
    protected String listLabelKey;
    protected String listCssClass;
    protected String listCssStyle;
    protected String listTitle;
    protected boolean throwExceptionOnNullValueAttribute = false;

    protected ListUIBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    public void evaluateExtraParams() {
        Object value = null;
        if (this.list == null) {
            this.list = this.parameters.get("list");
        }
        if (this.list instanceof String) {
            value = this.findValue((String)this.list);
            if (value == null) {
                value = this.throwExceptionOnNullValueAttribute ? this.findValue(this.list == null ? (String)this.list : this.list.toString(), "list", "The requested list key '" + this.list + "' could not be resolved as a collection/array/map/enumeration/iterator type. Example: people or people.{name}") : this.findValue(this.list == null ? (String)this.list : this.list.toString());
            }
        } else {
            value = this.list;
        }
        if (value == null || value instanceof Iterable) {
            this.addParameter("list", value);
        } else if (MakeIterator.isIterable(value)) {
            this.addParameter("list", MakeIterator.convert(value));
        } else {
            this.addParameter("list", Collections.singletonList(value));
        }
        if (value instanceof Collection) {
            this.addParameter("listSize", ((Collection)value).size());
        } else if (value instanceof Map) {
            this.addParameter("listSize", ((Map)value).size());
        } else if (value != null && value.getClass().isArray()) {
            this.addParameter("listSize", Array.getLength(value));
        }
        if (this.listKey != null) {
            this.listKey = this.stripExpression(this.listKey);
            this.addParameter("listKey", this.listKey);
        } else if (value instanceof Map) {
            this.addParameter("listKey", "key");
        } else {
            this.addParameter("listKey", "top");
        }
        if (this.listValueKey != null) {
            this.listValueKey = this.stripExpression(this.listValueKey);
            this.addParameter("listValueKey", this.listValueKey);
        }
        if (this.listValue != null) {
            this.listValue = this.stripExpression(this.listValue);
            this.addParameter("listValue", this.listValue);
        } else if (value instanceof Map) {
            this.addParameter("listValue", "value");
        } else {
            this.addParameter("listValue", "top");
        }
        if (this.listLabelKey != null) {
            this.listLabelKey = this.stripExpression(this.listLabelKey);
            this.addParameter("listLabelKey", this.listLabelKey);
        }
        if (StringUtils.isNotBlank((CharSequence)this.listCssClass)) {
            this.addParameter("listCssClass", this.listCssClass);
        }
        if (StringUtils.isNotBlank((CharSequence)this.listCssStyle)) {
            this.addParameter("listCssStyle", this.listCssStyle);
        }
        if (StringUtils.isNotBlank((CharSequence)this.listTitle)) {
            this.addParameter("listTitle", this.listTitle);
        }
    }

    public boolean contains(Object obj1, Object obj2) {
        return ContainUtil.contains(obj1, obj2);
    }

    protected Class getValueClassType() {
        return null;
    }

    @StrutsTagAttribute(description="Iterable source to populate from. If the list is a Map (key, value), the Map key will become the option 'value' parameter and the Map value will become the option body.", required=true)
    public void setList(Object list) {
        this.list = list;
    }

    @StrutsTagAttribute(description="Property of list objects to get field value from")
    public void setListKey(String listKey) {
        this.listKey = listKey;
    }

    @StrutsTagAttribute(description="Property of list objects to get field value label from")
    public void setListValueKey(String listValueKey) {
        this.listValueKey = listValueKey;
    }

    @StrutsTagAttribute(description="Property of list objects to get field content from")
    public void setListValue(String listValue) {
        this.listValue = listValue;
    }

    @StrutsTagAttribute(description="Property of list objects to be used to lookup for localised version of field label")
    public void setListLabelKey(String listLabelKey) {
        this.listLabelKey = listLabelKey;
    }

    @StrutsTagAttribute(description="Property of list objects to get css class from")
    public void setListCssClass(String listCssClass) {
        this.listCssClass = listCssClass;
    }

    @StrutsTagAttribute(description="Property of list objects to get css style from")
    public void setListCssStyle(String listCssStyle) {
        this.listCssStyle = listCssStyle;
    }

    @StrutsTagAttribute(description="Property of list objects to get title from")
    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
    }

    public void setThrowExceptionOnNullValueAttribute(boolean throwExceptionOnNullValueAttribute) {
        this.throwExceptionOnNullValueAttribute = throwExceptionOnNullValueAttribute;
    }
}

