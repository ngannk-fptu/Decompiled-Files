/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.context.extractor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.terracotta.context.ContextElement;
import org.terracotta.context.extractor.AttributeGetter;

class LazyContextElement
implements ContextElement {
    public final Class identifier;
    public final Map<? extends String, AttributeGetter<? extends Object>> attributes;

    public LazyContextElement(Class identifier, Map<? extends String, AttributeGetter<? extends Object>> attributes) {
        this.identifier = identifier;
        this.attributes = new HashMap<String, AttributeGetter<? extends Object>>(attributes);
    }

    @Override
    public Class identifier() {
        return this.identifier;
    }

    @Override
    public Map<String, Object> attributes() {
        HashMap<String, Object> realized = new HashMap<String, Object>();
        for (Map.Entry<? extends String, AttributeGetter<? extends Object>> e : this.attributes.entrySet()) {
            realized.put(e.getKey(), e.getValue().get());
        }
        return Collections.unmodifiableMap(realized);
    }

    public String toString() {
        return this.identifier() + " " + this.attributes();
    }
}

