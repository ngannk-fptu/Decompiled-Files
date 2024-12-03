/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.util;

import com.opensymphony.xwork2.Action;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.util.IteratorFilterSupport;

public class SubsetIteratorFilter
extends IteratorFilterSupport
implements Iterator,
Action {
    private static final Logger LOG = LogManager.getLogger(SubsetIteratorFilter.class);
    Iterator iterator;
    Object source;
    int count = -1;
    int currentCount = 0;
    Decider decider;
    int start = 0;

    public void setCount(int aCount) {
        this.count = aCount;
    }

    public void setSource(Object anIterator) {
        this.source = anIterator;
    }

    public void setStart(int aStart) {
        this.start = aStart;
    }

    public void setDecider(Decider aDecider) {
        this.decider = aDecider;
    }

    @Override
    public String execute() {
        if (this.source == null) {
            LogManager.getLogger((String)SubsetIteratorFilter.class.getName()).warn("Source is null returning empty set.");
            return "error";
        }
        this.source = this.getIterator(this.source);
        if (this.source instanceof Iterator) {
            this.iterator = (Iterator)this.source;
            for (int i = 0; i < this.start && this.iterator.hasNext(); ++i) {
                this.iterator.next();
            }
            if (this.decider != null) {
                ArrayList list = new ArrayList();
                while (this.iterator.hasNext()) {
                    Object currentElement = this.iterator.next();
                    if (!this.decide(currentElement)) continue;
                    list.add(currentElement);
                }
                this.iterator = list.iterator();
            }
        } else if (this.source.getClass().isArray()) {
            ArrayList<Object> list = new ArrayList<Object>(((Object[])this.source).length);
            Object[] objects = (Object[])this.source;
            int len = objects.length;
            if (this.count >= 0 && (len = this.start + this.count) > objects.length) {
                len = objects.length;
            }
            for (int j = this.start; j < len; ++j) {
                if (!this.decide(objects[j])) continue;
                list.add(objects[j]);
            }
            this.count = -1;
            this.iterator = list.iterator();
        }
        if (this.iterator == null) {
            throw new IllegalArgumentException("Source is not an iterator:" + this.source);
        }
        return "success";
    }

    @Override
    public boolean hasNext() {
        return this.iterator == null ? false : this.iterator.hasNext() && (this.count < 0 || this.currentCount < this.count);
    }

    public Object next() {
        ++this.currentCount;
        return this.iterator.next();
    }

    @Override
    public void remove() {
        this.iterator.remove();
    }

    protected boolean decide(Object element) {
        if (this.decider != null) {
            try {
                boolean okToAdd = this.decider.decide(element);
                return okToAdd;
            }
            catch (Exception e) {
                LOG.warn("Decider [{}] encountered an error while decide adding element [{}], element will be ignored, it will not appeared in subseted iterator", (Object)this.decider, element, (Object)e);
                return false;
            }
        }
        return true;
    }

    public static interface Decider {
        public boolean decide(Object var1) throws Exception;
    }
}

