/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory;

import org.springframework.beans.factory.Aware;

public interface BeanNameAware
extends Aware {
    public void setBeanName(String var1);
}

