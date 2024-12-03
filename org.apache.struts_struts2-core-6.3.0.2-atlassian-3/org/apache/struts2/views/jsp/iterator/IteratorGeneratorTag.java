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
import org.apache.struts2.util.IteratorGenerator;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.jsp.StrutsBodyTagSupport;

@StrutsTag(name="generator", tldTagClass="org.apache.struts2.views.jsp.iterator.IteratorGeneratorTag", description="Generate an iterator for a iterable source.")
public class IteratorGeneratorTag
extends StrutsBodyTagSupport {
    private static final long serialVersionUID = 2968037295463973936L;
    public static final String DEFAULT_SEPARATOR = ",";
    private static final Logger LOG = LogManager.getLogger(IteratorGeneratorTag.class);
    String countAttr;
    String separatorAttr;
    String valueAttr;
    String converterAttr;
    String var;
    IteratorGenerator iteratorGenerator = null;

    @StrutsTagAttribute(type="Integer", description="The max number entries to be in the iterator")
    public void setCount(String count) {
        this.countAttr = count;
    }

    @StrutsTagAttribute(required=true, description="The separator to be used in separating the <i>val</i> into entries of the iterator")
    public void setSeparator(String separator) {
        this.separatorAttr = separator;
    }

    @StrutsTagAttribute(required=true, description="The source to be parsed into an iterator")
    public void setVal(String val) {
        this.valueAttr = val;
    }

    @StrutsTagAttribute(type="org.apache.struts2.util.IteratorGenerator.Converter", description="The converter to convert the String entry parsed from <i>val</i> into an object")
    public void setConverter(String aConverter) {
        this.converterAttr = aConverter;
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
        Object value = this.findValue(this.valueAttr);
        String separator = DEFAULT_SEPARATOR;
        if (this.separatorAttr != null && this.separatorAttr.length() > 0) {
            separator = this.findString(this.separatorAttr);
        }
        int count = 0;
        if (this.countAttr != null && this.countAttr.length() > 0) {
            Object countObj = this.findValue(this.countAttr);
            if (countObj instanceof Number) {
                count = ((Number)countObj).intValue();
            } else if (countObj instanceof String) {
                try {
                    count = Integer.parseInt((String)countObj);
                }
                catch (NumberFormatException e) {
                    LOG.warn("Unable to convert count attribute [{}] to number, ignore count attribute", countObj, (Object)e);
                }
            }
        }
        IteratorGenerator.Converter converter = null;
        if (this.converterAttr != null && this.converterAttr.length() > 0) {
            converter = (IteratorGenerator.Converter)this.findValue(this.converterAttr);
        }
        this.iteratorGenerator = new IteratorGenerator();
        this.iteratorGenerator.setValues(value);
        this.iteratorGenerator.setCount(count);
        this.iteratorGenerator.setSeparator(separator);
        this.iteratorGenerator.setConverter(converter);
        this.iteratorGenerator.execute();
        this.getStack().push(this.iteratorGenerator);
        if (this.var != null && this.var.length() > 0) {
            this.getStack().getContext().put(this.var, this.iteratorGenerator);
        }
        return 1;
    }

    @Override
    public int doEndTag() throws JspException {
        this.getStack().pop();
        this.iteratorGenerator = null;
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
        this.separatorAttr = null;
        this.valueAttr = null;
        this.converterAttr = null;
        this.var = null;
        this.iteratorGenerator = null;
    }
}

