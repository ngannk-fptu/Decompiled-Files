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
import org.apache.struts2.util.MakeIterator;
import org.apache.struts2.util.MergeIteratorFilter;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="merge", tldTagClass="org.apache.struts2.views.jsp.iterator.MergeIteratorTag", description="Merge the values of a list of iterators into one iterator")
public class MergeIterator
extends ContextBean
implements Param.UnnamedParametric {
    private static final Logger LOG = LogManager.getLogger(MergeIterator.class);
    private MergeIteratorFilter mergeIteratorFilter = null;
    private List _parameters;

    public MergeIterator(ValueStack stack) {
        super(stack);
    }

    @Override
    public boolean start(Writer writer) {
        this.mergeIteratorFilter = new MergeIteratorFilter();
        this._parameters = new ArrayList();
        return super.start(writer);
    }

    @Override
    public boolean end(Writer writer, String body) {
        for (Object iteratorEntryObj : this._parameters) {
            if (!MakeIterator.isIterable(iteratorEntryObj)) {
                LOG.warn("param with value resolved as {} cannot be make as iterator, it will be ignored and hence will not appear in the merged iterator", iteratorEntryObj);
                continue;
            }
            this.mergeIteratorFilter.setSource(MakeIterator.convert(iteratorEntryObj));
        }
        this.mergeIteratorFilter.execute();
        this.putInContext(this.mergeIteratorFilter);
        this.mergeIteratorFilter = null;
        return super.end(writer, body);
    }

    @Override
    @StrutsTagAttribute(description="The name where the resultant merged iterator will be stored in the stack's context")
    public void setVar(String var) {
        super.setVar(var);
    }

    @Override
    public void addParameter(Object value) {
        this._parameters.add(value);
    }
}

