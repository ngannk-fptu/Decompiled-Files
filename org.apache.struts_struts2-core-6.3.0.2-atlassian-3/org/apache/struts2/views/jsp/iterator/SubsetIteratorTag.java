/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.jsp.JspException
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.views.jsp.iterator;

import javax.servlet.jsp.JspException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.util.SubsetIteratorFilter;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.jsp.StrutsBodyTagSupport;

@StrutsTag(name="subset", tldTagClass="org.apache.struts2.views.jsp.iterator.SubsetIteratorTag", description="Takes an iterator and outputs a subset of it.")
public class SubsetIteratorTag
extends StrutsBodyTagSupport {
    private static final long serialVersionUID = -6252696081713080102L;
    private static final Logger LOG = LogManager.getLogger(SubsetIteratorTag.class);
    String countAttr;
    String sourceAttr;
    String startAttr;
    String deciderAttr;
    String var;
    SubsetIteratorFilter subsetIteratorFilter = null;

    @StrutsTagAttribute(type="Integer", description="Indicate the number of entries to be in the resulting subset iterator")
    public void setCount(String count) {
        this.countAttr = count;
    }

    @StrutsTagAttribute(description="Indicate the source of which the resulting subset iterator is to be derived base on")
    public void setSource(String source) {
        this.sourceAttr = source;
    }

    @StrutsTagAttribute(type="Integer", description="Indicate the starting index (eg. first entry is 0) of entries in the source to be available as the first entry in the resulting subset iterator")
    public void setStart(String start) {
        this.startAttr = start;
    }

    @StrutsTagAttribute(type="org.apache.struts2.util.SubsetIteratorFilter.Decider", description="Extension to plug-in a decider to determine if that particular entry is to be included in the resulting subset iterator")
    public void setDecider(String decider) {
        this.deciderAttr = decider;
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
        Object source = null;
        source = this.sourceAttr == null || this.sourceAttr.length() == 0 ? this.findValue("top") : this.findValue(this.sourceAttr);
        int count = -1;
        if (this.countAttr != null && this.countAttr.length() > 0) {
            Object countObj = this.findValue(this.countAttr);
            if (countObj instanceof Number) {
                count = ((Number)countObj).intValue();
            } else if (countObj instanceof String) {
                try {
                    count = Integer.parseInt((String)countObj);
                }
                catch (NumberFormatException e) {
                    LOG.warn("unable to convert count attribute [{}] to number, ignore count attribute", countObj, (Object)e);
                }
            }
        }
        int start = 0;
        if (this.startAttr != null && this.startAttr.length() > 0) {
            Object startObj = this.findValue(this.startAttr);
            if (startObj instanceof Integer) {
                start = (Integer)startObj;
            } else if (startObj instanceof Float) {
                start = ((Float)startObj).intValue();
            } else if (startObj instanceof Long) {
                start = ((Long)startObj).intValue();
            } else if (startObj instanceof Double) {
                start = ((Double)startObj).intValue();
            } else if (startObj instanceof String) {
                try {
                    start = Integer.parseInt((String)startObj);
                }
                catch (NumberFormatException e) {
                    LOG.warn("unable to convert count attribute [{}] to number, ignore count attribute", startObj, (Object)e);
                }
            }
        }
        SubsetIteratorFilter.Decider decider = null;
        if (this.deciderAttr != null && this.deciderAttr.length() > 0) {
            Object deciderObj = this.findValue(this.deciderAttr);
            if (!(deciderObj instanceof SubsetIteratorFilter.Decider)) {
                throw new JspException("decider found from stack [" + deciderObj + "] does not implement " + SubsetIteratorFilter.Decider.class);
            }
            decider = (SubsetIteratorFilter.Decider)deciderObj;
        }
        this.subsetIteratorFilter = new SubsetIteratorFilter();
        this.subsetIteratorFilter.setCount(count);
        this.subsetIteratorFilter.setDecider(decider);
        this.subsetIteratorFilter.setSource(source);
        this.subsetIteratorFilter.setStart(start);
        this.subsetIteratorFilter.execute();
        this.getStack().push(this.subsetIteratorFilter);
        if (this.var != null && this.var.length() > 0) {
            this.pageContext.setAttribute(this.var, (Object)this.subsetIteratorFilter);
        }
        return 1;
    }

    @Override
    public int doEndTag() throws JspException {
        this.getStack().pop();
        this.subsetIteratorFilter = null;
        this.clearTagStateForTagPoolingServers();
        return 6;
    }

    @Override
    protected void clearTagStateForTagPoolingServers() {
        if (!this.getPerformClearTagStateForTagPoolingServers()) {
            return;
        }
        super.clearTagStateForTagPoolingServers();
        this.countAttr = null;
        this.sourceAttr = null;
        this.startAttr = null;
        this.deciderAttr = null;
        this.var = null;
        this.subsetIteratorFilter = null;
    }
}

