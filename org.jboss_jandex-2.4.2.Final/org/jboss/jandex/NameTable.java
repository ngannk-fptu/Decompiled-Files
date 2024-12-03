/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.util.HashMap;
import java.util.Map;
import org.jboss.jandex.DotName;
import org.jboss.jandex.FieldInternal;
import org.jboss.jandex.MethodInternal;
import org.jboss.jandex.RecordComponentInternal;
import org.jboss.jandex.StrongInternPool;
import org.jboss.jandex.Type;

class NameTable {
    private StrongInternPool<String> stringPool = new StrongInternPool();
    private StrongInternPool<Type> typePool = new StrongInternPool();
    private StrongInternPool<Type[]> typeListPool = new StrongInternPool();
    private StrongInternPool<byte[]> bytePool = new StrongInternPool();
    private StrongInternPool<MethodInternal> methodPool = new StrongInternPool();
    private StrongInternPool<FieldInternal> fieldPool = new StrongInternPool();
    private StrongInternPool<RecordComponentInternal> recordComponentPool = new StrongInternPool();
    private Map<String, DotName> names = new HashMap<String, DotName>();

    NameTable() {
    }

    DotName convertToName(String name) {
        return this.convertToName(name, '.');
    }

    DotName convertToName(String name, char delim) {
        DotName result = this.names.get(name);
        if (result != null) {
            return result;
        }
        int loc = this.lastIndexOf(name, delim);
        String local = this.intern(name.substring(loc + 1));
        DotName prefix = loc < 1 ? null : this.convertToName(this.intern(name.substring(0, loc)), delim);
        result = new DotName(prefix, local, true, loc > 0 && name.charAt(loc) == '$');
        this.names.put(name, result);
        return result;
    }

    private int lastIndexOf(String name, char delim) {
        char c;
        int pos = name.length() - 1;
        while (--pos >= 0 && (c = name.charAt(pos)) != delim && c != '$') {
        }
        if (pos >= 0 && name.charAt(pos) == '$' && (pos == 0 || name.charAt(pos - 1) == delim)) {
            --pos;
        }
        return pos;
    }

    DotName wrap(DotName prefix, String local, boolean inner) {
        DotName name = new DotName(prefix, this.intern(local), true, true);
        return this.intern(name, '.');
    }

    String intern(String string) {
        return this.stringPool.intern(string);
    }

    int positionOf(String string) {
        return this.stringPool.index().positionOf(string);
    }

    Type intern(Type type) {
        return this.typePool.intern(type);
    }

    Type[] intern(Type[] types) {
        return this.typeListPool.intern(types);
    }

    byte[] intern(byte[] bytes) {
        return this.bytePool.intern(bytes);
    }

    int positionOf(byte[] type) {
        return this.bytePool.index().positionOf(type);
    }

    MethodInternal intern(MethodInternal methodInternal) {
        return this.methodPool.intern(methodInternal);
    }

    int positionOf(MethodInternal methodInternal) {
        return this.methodPool.index().positionOf(methodInternal);
    }

    FieldInternal intern(FieldInternal fieldInternal) {
        return this.fieldPool.intern(fieldInternal);
    }

    int positionOf(FieldInternal fieldInternal) {
        return this.fieldPool.index().positionOf(fieldInternal);
    }

    RecordComponentInternal intern(RecordComponentInternal recordComponentInternal) {
        return this.recordComponentPool.intern(recordComponentInternal);
    }

    int positionOf(RecordComponentInternal recordComponentInternal) {
        return this.recordComponentPool.index().positionOf(recordComponentInternal);
    }

    StrongInternPool<String> stringPool() {
        return this.stringPool;
    }

    StrongInternPool<byte[]> bytePool() {
        return this.bytePool;
    }

    StrongInternPool<MethodInternal> methodPool() {
        return this.methodPool;
    }

    StrongInternPool<FieldInternal> fieldPool() {
        return this.fieldPool;
    }

    StrongInternPool<RecordComponentInternal> recordComponentPool() {
        return this.recordComponentPool;
    }

    DotName intern(DotName dotName, char delim) {
        String name = dotName.toString(delim);
        DotName old = this.names.get(name);
        if (old == null) {
            old = dotName;
            this.names.put(name, dotName);
        }
        return old;
    }
}

