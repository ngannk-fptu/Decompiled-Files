/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.export.assembler;

import org.springframework.jmx.export.assembler.MBeanInfoAssembler;

public interface AutodetectCapableMBeanInfoAssembler
extends MBeanInfoAssembler {
    public boolean includeBean(Class<?> var1, String var2);
}

