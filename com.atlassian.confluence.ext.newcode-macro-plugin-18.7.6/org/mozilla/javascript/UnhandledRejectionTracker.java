/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import org.mozilla.javascript.NativePromise;

public class UnhandledRejectionTracker {
    private boolean enabled = false;
    private static final IdentityHashMap<NativePromise, NativePromise> unhandled = new IdentityHashMap(0);

    public void process(Consumer<Object> handler) {
        Iterator<NativePromise> it = unhandled.values().iterator();
        while (it.hasNext()) {
            try {
                handler.accept(it.next().getResult());
            }
            finally {
                it.remove();
            }
        }
    }

    public List<Object> enumerate() {
        ArrayList<Object> ret = new ArrayList<Object>();
        for (NativePromise result : unhandled.values()) {
            ret.add(result.getResult());
        }
        return ret;
    }

    void enable(boolean enabled) {
        this.enabled = enabled;
    }

    void promiseRejected(NativePromise p) {
        if (this.enabled) {
            unhandled.put(p, p);
        }
    }

    void promiseHandled(NativePromise p) {
        if (this.enabled) {
            unhandled.remove(p);
        }
    }
}

