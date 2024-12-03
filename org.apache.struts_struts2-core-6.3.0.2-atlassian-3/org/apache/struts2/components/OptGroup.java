/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import java.io.Writer;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.ListUIBean;
import org.apache.struts2.components.Select;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="optgroup", tldTagClass="org.apache.struts2.views.jsp.ui.OptGroupTag", description="Renders a Select Tag's OptGroup Tag")
public class OptGroup
extends Component {
    public static final String INTERNAL_LIST_UI_BEAN_LIST_PARAMETER_KEY = "optGroupInternalListUiBeanList";
    private static Logger LOG = LogManager.getLogger(OptGroup.class);
    protected HttpServletRequest req;
    protected HttpServletResponse res;
    protected ListUIBean internalUiBean;

    public OptGroup(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack);
        this.req = req;
        this.res = res;
        this.internalUiBean = new ListUIBean(stack, req, res){

            @Override
            protected String getDefaultTemplate() {
                return "empty";
            }
        };
    }

    @Inject
    public void setContainer(Container container) {
        container.inject(this.internalUiBean);
    }

    @Override
    public boolean end(Writer writer, String body) {
        Select select = (Select)this.findAncestor(Select.class);
        if (select == null) {
            LOG.error("incorrect use of OptGroup component, this component must be used within a Select component", (Throwable)new IllegalStateException("incorrect use of OptGroup component, this component must be used within a Select component"));
            return false;
        }
        this.internalUiBean.start(writer);
        this.internalUiBean.end(writer, body);
        ArrayList<ListUIBean> listUiBeans = (ArrayList<ListUIBean>)select.getParameters().get(INTERNAL_LIST_UI_BEAN_LIST_PARAMETER_KEY);
        if (listUiBeans == null) {
            listUiBeans = new ArrayList<ListUIBean>();
        }
        listUiBeans.add(this.internalUiBean);
        select.addParameter(INTERNAL_LIST_UI_BEAN_LIST_PARAMETER_KEY, listUiBeans);
        return false;
    }

    @StrutsTagAttribute(description="Set the label attribute")
    public void setLabel(String label) {
        this.internalUiBean.setLabel(label);
    }

    @StrutsTagAttribute(description="Set the disable attribute.")
    public void setDisabled(String disabled) {
        this.internalUiBean.setDisabled(disabled);
    }

    @StrutsTagAttribute(description="Set the list attribute.")
    public void setList(Object list) {
        this.internalUiBean.setList(list);
    }

    @StrutsTagAttribute(description="Set the listKey attribute.")
    public void setListKey(String listKey) {
        this.internalUiBean.setListKey(listKey);
    }

    @StrutsTagAttribute(description="Set the listValue attribute.")
    public void setListValue(String listValue) {
        this.internalUiBean.setListValue(listValue);
    }

    @StrutsTagAttribute(description="Property of list objects to get css class from")
    public void setListCssClass(String listCssClass) {
        this.internalUiBean.setListCssClass(listCssClass);
    }

    @StrutsTagAttribute(description="Property of list objects to get css style from")
    public void setListCssStyle(String listCssStyle) {
        this.internalUiBean.setListCssStyle(listCssStyle);
    }

    @StrutsTagAttribute(description="Property of list objects to get title from")
    public void setListTitle(String listTitle) {
        this.internalUiBean.setListTitle(listTitle);
    }
}

