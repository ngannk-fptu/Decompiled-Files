/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 */
package org.apache.struts2.views.jsp.iterator;

import java.util.Comparator;
import javax.servlet.jsp.JspException;
import org.apache.struts2.util.MakeIterator;
import org.apache.struts2.util.SortIteratorFilter;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.jsp.StrutsBodyTagSupport;

@StrutsTag(name="sort", tldTagClass="org.apache.struts2.views.jsp.iterator.SortIteratorTag", description="Sort a List using a Comparator both passed in as the tag attribute.")
public class SortIteratorTag
extends StrutsBodyTagSupport {
    private static final long serialVersionUID = -7835719609764092235L;
    String comparatorAttr;
    String sourceAttr;
    String var;
    SortIteratorFilter sortIteratorFilter = null;

    @StrutsTagAttribute(required=true, type="java.util.Comparator", description="The comparator to use")
    public void setComparator(String comparator) {
        this.comparatorAttr = comparator;
    }

    @StrutsTagAttribute(description="The iterable source to sort")
    public void setSource(String source) {
        this.sourceAttr = source;
    }

    @StrutsTagAttribute(description="The name to store the resultant iterator into page context, if such name is supplied")
    public void setVar(String var) {
        this.var = var;
    }

    @Override
    @StrutsTagAttribute(description="Whether to clear all tag state during doEndTag() processing", type="Boolean", defaultValue="false", required=false)
    public void setPerformClearTagStateForTagPoolingServers(boolean performClearTagStateForTagPoolingServers) {
        super.setPerformClearTagStateForTagPoolingServers(performClearTagStateForTagPoolingServers);
    }

    public int doStartTag() throws JspException {
        Object srcToSort = this.sourceAttr == null ? this.findValue("top") : this.findValue(this.sourceAttr);
        if (!MakeIterator.isIterable(srcToSort)) {
            throw new JspException("source [" + srcToSort + "] is not iteratable");
        }
        Object comparatorObj = this.findValue(this.comparatorAttr);
        if (!(comparatorObj instanceof Comparator)) {
            throw new JspException("comparator [" + comparatorObj + "] does not implements Comparator interface");
        }
        Comparator c = (Comparator)this.findValue(this.comparatorAttr);
        this.sortIteratorFilter = new SortIteratorFilter();
        this.sortIteratorFilter.setComparator(c);
        this.sortIteratorFilter.setSource(srcToSort);
        this.sortIteratorFilter.execute();
        this.getStack().push(this.sortIteratorFilter);
        if (this.var != null && this.var.length() > 0) {
            this.pageContext.setAttribute(this.var, (Object)this.sortIteratorFilter);
        }
        return 1;
    }

    @Override
    public int doEndTag() throws JspException {
        int returnVal = super.doEndTag();
        this.getStack().pop();
        this.sortIteratorFilter = null;
        return returnVal;
    }

    @Override
    protected void clearTagStateForTagPoolingServers() {
        if (!this.getPerformClearTagStateForTagPoolingServers()) {
            return;
        }
        super.clearTagStateForTagPoolingServers();
        this.comparatorAttr = null;
        this.sourceAttr = null;
        this.var = null;
        this.sortIteratorFilter = null;
    }
}

