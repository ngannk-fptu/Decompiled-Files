/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.beans.PropertyChangeListener;

public interface PropertyChangeEmitter {
    public void addPropertyChangeListener(PropertyChangeListener var1);

    public void addPropertyChangeListener(String var1, PropertyChangeListener var2);

    public void removePropertyChangeListener(PropertyChangeListener var1);

    public void removePropertyChangeListener(String var1, PropertyChangeListener var2);
}

