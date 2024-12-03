/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.debug;

import java.util.EventObject;

public abstract class Event
extends EventObject {
    private int type;

    public Event(Object object) {
        super(object);
    }

    public Event(Object object, int n) {
        super(object);
        this.setType(n);
    }

    public int getType() {
        return this.type;
    }

    void setType(int n) {
        this.type = n;
    }

    void setValues(int n) {
        this.setType(n);
    }
}

