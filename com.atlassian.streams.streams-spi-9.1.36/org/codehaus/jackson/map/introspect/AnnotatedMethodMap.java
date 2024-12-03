/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.introspect;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.codehaus.jackson.map.introspect.MemberKey;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class AnnotatedMethodMap
implements Iterable<AnnotatedMethod> {
    protected LinkedHashMap<MemberKey, AnnotatedMethod> _methods;

    public void add(AnnotatedMethod am) {
        if (this._methods == null) {
            this._methods = new LinkedHashMap();
        }
        this._methods.put(new MemberKey(am.getAnnotated()), am);
    }

    public AnnotatedMethod remove(AnnotatedMethod am) {
        return this.remove(am.getAnnotated());
    }

    public AnnotatedMethod remove(Method m) {
        if (this._methods != null) {
            return (AnnotatedMethod)this._methods.remove(new MemberKey(m));
        }
        return null;
    }

    public boolean isEmpty() {
        return this._methods == null || this._methods.size() == 0;
    }

    public int size() {
        return this._methods == null ? 0 : this._methods.size();
    }

    public AnnotatedMethod find(String name, Class<?>[] paramTypes) {
        if (this._methods == null) {
            return null;
        }
        return this._methods.get(new MemberKey(name, paramTypes));
    }

    public AnnotatedMethod find(Method m) {
        if (this._methods == null) {
            return null;
        }
        return this._methods.get(new MemberKey(m));
    }

    @Override
    public Iterator<AnnotatedMethod> iterator() {
        if (this._methods != null) {
            return this._methods.values().iterator();
        }
        List empty = Collections.emptyList();
        return empty.iterator();
    }
}

