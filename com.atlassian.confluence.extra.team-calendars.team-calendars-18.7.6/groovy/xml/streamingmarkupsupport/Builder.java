/*
 * Decompiled with CFR 0.152.
 */
package groovy.xml.streamingmarkupsupport;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class Builder
extends GroovyObjectSupport {
    protected final Map namespaceMethodMap = new HashMap();

    public Builder(Map namespaceMethodMap) {
        Iterator iterator = namespaceMethodMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry e;
            Map.Entry entry = e = iterator.next();
            Object key = entry.getKey();
            List value = (List)entry.getValue();
            Closure dg = ((Closure)value.get(1)).asWritable();
            this.namespaceMethodMap.put(key, new Object[]{value.get(0), dg, Builder.fettleMethodMap(dg, (Map)value.get(2))});
        }
    }

    private static Map fettleMethodMap(Closure defaultGenerator, Map methodMap) {
        HashMap newMethodMap = new HashMap();
        for (Object o : methodMap.keySet()) {
            Object key = o;
            Object value = methodMap.get(key);
            if (value instanceof Closure) {
                newMethodMap.put(key, value);
                continue;
            }
            newMethodMap.put(key, defaultGenerator.curry((Object[])value));
        }
        return newMethodMap;
    }

    public abstract Object bind(Closure var1);

    protected static abstract class Built
    extends GroovyObjectSupport {
        protected final Closure root;
        protected final Map namespaceSpecificTags = new HashMap();

        public Built(Closure root, Map namespaceTagMap) {
            this.namespaceSpecificTags.putAll(namespaceTagMap);
            this.root = (Closure)root.clone();
            this.root.setDelegate(this);
        }
    }
}

