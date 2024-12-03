/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.mbeans;

import org.apache.catalina.mbeans.BaseCatalinaMBean;

public class ClassNameMBean<T>
extends BaseCatalinaMBean<T> {
    public String getClassName() {
        return this.resource.getClass().getName();
    }
}

