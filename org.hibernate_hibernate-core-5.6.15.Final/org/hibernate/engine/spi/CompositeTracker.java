/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import org.hibernate.engine.spi.CompositeOwner;

public interface CompositeTracker {
    public void $$_hibernate_setOwner(String var1, CompositeOwner var2);

    public void $$_hibernate_clearOwner(String var1);
}

