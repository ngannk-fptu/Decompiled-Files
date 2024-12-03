/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.resources.BaseResourceCollectionWrapper;

public abstract class SizeLimitCollection
extends BaseResourceCollectionWrapper {
    private static final String BAD_COUNT = "size-limited collection count should be set to an int >= 0";
    private int count = 1;

    public synchronized void setCount(int i) {
        this.checkAttributesAllowed();
        this.count = i;
    }

    public synchronized int getCount() {
        return this.count;
    }

    @Override
    public synchronized int size() {
        return Math.min(this.getResourceCollection().size(), this.getValidCount());
    }

    protected int getValidCount() {
        int ct = this.getCount();
        if (ct < 0) {
            throw new BuildException(BAD_COUNT);
        }
        return ct;
    }
}

