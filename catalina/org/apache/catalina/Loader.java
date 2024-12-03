/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

import java.beans.PropertyChangeListener;
import org.apache.catalina.Context;

public interface Loader {
    public void backgroundProcess();

    public ClassLoader getClassLoader();

    public Context getContext();

    public void setContext(Context var1);

    public boolean getDelegate();

    public void setDelegate(boolean var1);

    @Deprecated
    public boolean getReloadable();

    @Deprecated
    public void setReloadable(boolean var1);

    public void addPropertyChangeListener(PropertyChangeListener var1);

    public boolean modified();

    public void removePropertyChangeListener(PropertyChangeListener var1);
}

