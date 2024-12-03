/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import javax.media.jai.PropertyChangeEmitter;
import javax.media.jai.PropertySource;

public interface WritablePropertySource
extends PropertySource,
PropertyChangeEmitter {
    public void setProperty(String var1, Object var2);

    public void removeProperty(String var1);
}

