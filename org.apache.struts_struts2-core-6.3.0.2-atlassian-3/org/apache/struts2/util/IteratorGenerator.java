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
import java.util.List;
import java.util.StringTokenizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IteratorGenerator
implements Iterator,
Action {
    private static final Logger LOG = LogManager.getLogger(IteratorGenerator.class);
    List values;
    Object value;
    String separator;
    Converter converter;
    int count = 0;
    int currentCount = 0;

    public void setCount(int aCount) {
        this.count = aCount;
    }

    public boolean getHasNext() {
        return this.hasNext();
    }

    public Object getNext() {
        return this.next();
    }

    public void setSeparator(String aChar) {
        this.separator = aChar;
    }

    public void setConverter(Converter aConverter) {
        this.converter = aConverter;
    }

    public void setValues(Object aValue) {
        this.value = aValue;
    }

    @Override
    public String execute() {
        if (this.value == null) {
            return "error";
        }
        this.values = new ArrayList();
        if (this.separator != null) {
            StringTokenizer tokens = new StringTokenizer(this.value.toString(), this.separator);
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken().trim();
                if (this.converter != null) {
                    try {
                        Object convertedObj = this.converter.convert(token);
                        this.values.add(convertedObj);
                    }
                    catch (Exception e) {
                        LOG.warn("Unable to convert [{}], skipping this token, it will not appear in the generated iterator", (Object)token, (Object)e);
                    }
                    continue;
                }
                this.values.add(token);
            }
        } else {
            this.values.add(this.value.toString());
        }
        if (this.count == 0) {
            this.count = this.values.size();
        }
        return "success";
    }

    @Override
    public boolean hasNext() {
        return this.value == null ? false : this.currentCount < this.count || this.count == -1;
    }

    public Object next() {
        try {
            Object e = this.values.get(this.currentCount % this.values.size());
            return e;
        }
        finally {
            ++this.currentCount;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove is not supported in IteratorGenerator.");
    }

    public static interface Converter {
        public Object convert(String var1) throws Exception;
    }
}

