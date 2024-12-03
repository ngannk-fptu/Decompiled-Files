/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity;

import java.util.HashMap;
import java.util.Map;
import org.apache.velocity.context.AbstractContext;
import org.apache.velocity.context.Context;

public class VelocityContext
extends AbstractContext
implements Cloneable {
    private static final long serialVersionUID = 9033846851064645037L;
    private Map context = null;

    public VelocityContext() {
        this(null, null);
    }

    public VelocityContext(Map context) {
        this(context, null);
    }

    public VelocityContext(Context innerContext) {
        this(null, innerContext);
    }

    public VelocityContext(Map context, Context innerContext) {
        super(innerContext);
        this.context = context == null ? new HashMap() : context;
    }

    @Override
    public Object internalGet(String key) {
        return this.context.get(key);
    }

    @Override
    public Object internalPut(String key, Object value) {
        return this.context.put(key, value);
    }

    @Override
    public boolean internalContainsKey(Object key) {
        return this.context.containsKey(key);
    }

    @Override
    public Object[] internalGetKeys() {
        return this.context.keySet().toArray();
    }

    @Override
    public Object internalRemove(Object key) {
        return this.context.remove(key);
    }

    public Object clone() {
        VelocityContext clone = null;
        try {
            clone = (VelocityContext)super.clone();
            clone.context = new HashMap(this.context);
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            // empty catch block
        }
        return clone;
    }
}

