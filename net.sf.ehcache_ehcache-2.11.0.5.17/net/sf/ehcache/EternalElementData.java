/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache;

import java.io.ObjectInput;
import java.io.ObjectOutput;
import net.sf.ehcache.Element;
import net.sf.ehcache.ElementData;

public class EternalElementData
extends ElementData {
    public EternalElementData() {
    }

    public EternalElementData(Element element) {
        super(element);
    }

    @Override
    public Element createElement(Object key) {
        Element element = new Element(key, this.value, this.version, this.creationTime, this.lastAccessTime, this.hitCount, this.cacheDefaultLifespan, 0, 0, this.lastUpdateTime);
        return element;
    }

    @Override
    protected void writeAttributes(ObjectOutput oos) {
    }

    @Override
    protected void readAttributes(ObjectInput in) {
    }
}

