/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.components.ContextBean;
import org.apache.struts2.components.Param;
import org.apache.struts2.util.AppendIteratorFilter;
import org.apache.struts2.util.MakeIterator;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="append", tldTagClass="org.apache.struts2.views.jsp.iterator.AppendIteratorTag", description="Append the values of a list of iterators to one iterator")
public class AppendIterator
extends ContextBean
implements Param.UnnamedParametric {
    private static final Logger LOG = LogManager.getLogger(AppendIterator.class);
    private AppendIteratorFilter appendIteratorFilter = null;
    private List _parameters;

    public AppendIterator(ValueStack stack) {
        super(stack);
    }

    @Override
    public boolean start(Writer writer) {
        this._parameters = new ArrayList();
        this.appendIteratorFilter = new AppendIteratorFilter();
        return super.start(writer);
    }

    @Override
    public boolean end(Writer writer, String body) {
        for (Object iteratorEntryObj : this._parameters) {
            if (!MakeIterator.isIterable(iteratorEntryObj)) {
                LOG.warn("param with value resolved as {} cannot be make as iterator, it will be ignored and hence will not appear in the merged iterator", iteratorEntryObj);
                continue;
            }
            this.appendIteratorFilter.setSource(MakeIterator.convert(iteratorEntryObj));
        }
        this.appendIteratorFilter.execute();
        this.putInContext(this.appendIteratorFilter);
        this.appendIteratorFilter = null;
        return super.end(writer, body);
    }

    @Override
    public void addParameter(Object value) {
        this._parameters.add(value);
    }

    @Override
    @StrutsTagAttribute(description="The name of which if supplied will have the resultant appended iterator stored under in the stack's context")
    public void setVar(String var) {
        super.setVar(var);
    }
}

