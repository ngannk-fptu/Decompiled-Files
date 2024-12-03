/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.base.config;

import java.util.Enumeration;
import java.util.Iterator;
import org.jfree.util.Configuration;

public interface ModifiableConfiguration
extends Configuration {
    public void setConfigProperty(String var1, String var2);

    public Enumeration getConfigProperties();

    public Iterator findPropertyKeys(String var1);
}

