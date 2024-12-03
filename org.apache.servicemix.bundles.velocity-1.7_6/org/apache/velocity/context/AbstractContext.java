/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.context;

import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalContextBase;
import org.apache.velocity.context.InternalEventContext;

public abstract class AbstractContext
extends InternalContextBase
implements Context {
    private Context innerContext = null;

    public abstract Object internalGet(String var1);

    public abstract Object internalPut(String var1, Object var2);

    public abstract boolean internalContainsKey(Object var1);

    public abstract Object[] internalGetKeys();

    public abstract Object internalRemove(Object var1);

    public AbstractContext() {
    }

    public AbstractContext(Context inner) {
        this.innerContext = inner;
        if (this.innerContext instanceof InternalEventContext) {
            this.attachEventCartridge(((InternalEventContext)((Object)this.innerContext)).getEventCartridge());
        }
    }

    public Object put(String key, Object value) {
        if (key == null) {
            return null;
        }
        return this.internalPut(key.intern(), value);
    }

    public Object get(String key) {
        if (key == null) {
            return null;
        }
        Object o = this.internalGet(key);
        if (o == null && this.innerContext != null) {
            o = this.innerContext.get(key);
        }
        return o;
    }

    public boolean containsKey(Object key) {
        if (key == null) {
            return false;
        }
        boolean exists = this.internalContainsKey(key);
        if (!exists && this.innerContext != null) {
            exists = this.innerContext.containsKey(key);
        }
        return exists;
    }

    public Object[] getKeys() {
        return this.internalGetKeys();
    }

    public Object remove(Object key) {
        if (key == null) {
            return null;
        }
        return this.internalRemove(key);
    }

    public Context getChainedContext() {
        return this.innerContext;
    }
}

