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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.util.IteratorFilterSupport;
import org.apache.struts2.util.IteratorGenerator;
import org.apache.struts2.util.MakeIterator;

public class SortIteratorFilter
extends IteratorFilterSupport
implements Iterator,
Action {
    private static final Logger LOG = LogManager.getLogger(IteratorGenerator.class);
    Comparator comparator;
    Iterator iterator;
    List list;
    Object source;

    public void setComparator(Comparator aComparator) {
        this.comparator = aComparator;
    }

    public List getList() {
        return this.list;
    }

    public void setSource(Object anIterator) {
        this.source = anIterator;
    }

    @Override
    public String execute() {
        if (this.source == null) {
            return "error";
        }
        try {
            if (!MakeIterator.isIterable(this.source)) {
                LOG.warn("Cannot create SortIterator for source: {}", this.source);
                return "error";
            }
            this.list = new ArrayList();
            Iterator i = MakeIterator.convert(this.source);
            while (i.hasNext()) {
                this.list.add(i.next());
            }
            Collections.sort(this.list, this.comparator);
            this.iterator = this.list.iterator();
            return "success";
        }
        catch (Exception e) {
            LOG.warn("Error creating sort iterator.", (Throwable)e);
            return "error";
        }
    }

    @Override
    public boolean hasNext() {
        return this.source == null ? false : this.iterator.hasNext();
    }

    public Object next() {
        return this.iterator.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove is not supported in SortIteratorFilter.");
    }
}

