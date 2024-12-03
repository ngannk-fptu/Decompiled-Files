/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;

public interface DynaClass {
    public String getName();

    public DynaProperty getDynaProperty(String var1);

    public DynaProperty[] getDynaProperties();

    public DynaBean newInstance() throws IllegalAccessException, InstantiationException;
}

