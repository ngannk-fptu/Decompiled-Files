/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.web.context.request;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;

public abstract class AbstractRequestAttributes
implements RequestAttributes {
    protected final Map<String, Runnable> requestDestructionCallbacks = new LinkedHashMap<String, Runnable>(8);
    private volatile boolean requestActive = true;

    public void requestCompleted() {
        this.executeRequestDestructionCallbacks();
        this.updateAccessedSessionAttributes();
        this.requestActive = false;
    }

    protected final boolean isRequestActive() {
        return this.requestActive;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final void registerRequestDestructionCallback(String name, Runnable callback) {
        Assert.notNull((Object)name, (String)"Name must not be null");
        Assert.notNull((Object)callback, (String)"Callback must not be null");
        Map<String, Runnable> map = this.requestDestructionCallbacks;
        synchronized (map) {
            this.requestDestructionCallbacks.put(name, callback);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final void removeRequestDestructionCallback(String name) {
        Assert.notNull((Object)name, (String)"Name must not be null");
        Map<String, Runnable> map = this.requestDestructionCallbacks;
        synchronized (map) {
            this.requestDestructionCallbacks.remove(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void executeRequestDestructionCallbacks() {
        Map<String, Runnable> map = this.requestDestructionCallbacks;
        synchronized (map) {
            for (Runnable runnable : this.requestDestructionCallbacks.values()) {
                runnable.run();
            }
            this.requestDestructionCallbacks.clear();
        }
    }

    protected abstract void updateAccessedSessionAttributes();
}

