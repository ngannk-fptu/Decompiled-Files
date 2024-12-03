/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.templates;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.abdera.i18n.templates.Context;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class HashMapContext
extends HashMap<String, Object>
implements Context {
    private static final long serialVersionUID = 2206000974505975049L;
    private boolean isiri = false;
    private boolean normalizing = false;

    public HashMapContext() {
    }

    public HashMapContext(Map<String, Object> map) {
        super(map);
    }

    public HashMapContext(Map<String, Object> map, boolean isiri) {
        super(map);
        this.isiri = isiri;
    }

    @Override
    public <T> T resolve(String var) {
        return (T)this.get(var);
    }

    @Override
    public boolean isIri() {
        return this.isiri;
    }

    @Override
    public void setIri(boolean isiri) {
        this.isiri = isiri;
    }

    @Override
    public Iterator<String> iterator() {
        return this.keySet().iterator();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.isiri ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        HashMapContext other = (HashMapContext)obj;
        return this.isiri == other.isiri;
    }

    public boolean isNormalizing() {
        return this.normalizing;
    }

    public void setNormalizing(boolean normalizing) {
        this.normalizing = normalizing;
    }
}

