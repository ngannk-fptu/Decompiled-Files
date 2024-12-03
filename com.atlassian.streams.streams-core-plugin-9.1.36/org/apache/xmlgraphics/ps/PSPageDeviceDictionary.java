/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps;

import java.util.Iterator;
import java.util.Map;
import org.apache.xmlgraphics.ps.PSDictionary;

public class PSPageDeviceDictionary
extends PSDictionary {
    private static final long serialVersionUID = 845943256485806509L;
    private boolean flushOnRetrieval;
    private PSDictionary unRetrievedContentDictionary;

    @Override
    public Object put(Object key, Object value) {
        Object previousValue = super.put(key, value);
        if (this.flushOnRetrieval && (previousValue == null || !previousValue.equals(value))) {
            this.unRetrievedContentDictionary.put(key, value);
        }
        return previousValue;
    }

    @Override
    public void putAll(Map m) {
        Iterator iterator = m.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry x;
            Map.Entry e = x = iterator.next();
            this.put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        super.clear();
        if (this.unRetrievedContentDictionary != null) {
            this.unRetrievedContentDictionary.clear();
        }
    }

    @Override
    public boolean isEmpty() {
        if (this.flushOnRetrieval) {
            return this.unRetrievedContentDictionary.isEmpty();
        }
        return super.isEmpty();
    }

    public void setFlushOnRetrieval(boolean flushOnRetrieval) {
        this.flushOnRetrieval = flushOnRetrieval;
        if (flushOnRetrieval) {
            this.unRetrievedContentDictionary = new PSDictionary();
        }
    }

    public String getContent() {
        String content;
        if (this.flushOnRetrieval) {
            content = this.unRetrievedContentDictionary.toString();
            this.unRetrievedContentDictionary.clear();
        } else {
            content = super.toString();
        }
        return content;
    }
}

