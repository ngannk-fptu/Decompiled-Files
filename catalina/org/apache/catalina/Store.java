/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;

public interface Store {
    public Manager getManager();

    public void setManager(Manager var1);

    public int getSize() throws IOException;

    public void addPropertyChangeListener(PropertyChangeListener var1);

    public String[] keys() throws IOException;

    public Session load(String var1) throws ClassNotFoundException, IOException;

    public void remove(String var1) throws IOException;

    public void clear() throws IOException;

    public void removePropertyChangeListener(PropertyChangeListener var1);

    public void save(Session var1) throws IOException;
}

