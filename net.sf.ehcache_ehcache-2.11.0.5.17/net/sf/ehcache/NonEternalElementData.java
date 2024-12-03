/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import net.sf.ehcache.Element;
import net.sf.ehcache.ElementData;

public class NonEternalElementData
extends ElementData {
    private volatile int timeToLive;
    private volatile int timeToIdle;

    public NonEternalElementData() {
    }

    public NonEternalElementData(Element element) {
        super(element);
        this.timeToIdle = element.getTimeToIdle();
        this.timeToLive = element.getTimeToLive();
    }

    @Override
    public Element createElement(Object key) {
        Element element = new Element(key, this.value, this.version, this.creationTime, this.lastAccessTime, this.hitCount, this.cacheDefaultLifespan, this.timeToLive, this.timeToIdle, this.lastUpdateTime);
        return element;
    }

    @Override
    protected void writeAttributes(ObjectOutput oos) throws IOException {
        oos.writeInt(this.timeToLive);
        oos.writeInt(this.timeToIdle);
    }

    @Override
    protected void readAttributes(ObjectInput in) throws IOException {
        this.timeToLive = in.readInt();
        this.timeToIdle = in.readInt();
    }
}

