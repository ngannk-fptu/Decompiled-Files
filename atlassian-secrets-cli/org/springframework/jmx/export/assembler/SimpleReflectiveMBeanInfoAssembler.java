/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.export.assembler;

import java.lang.reflect.Method;
import org.springframework.jmx.export.assembler.AbstractConfigurableMBeanInfoAssembler;

public class SimpleReflectiveMBeanInfoAssembler
extends AbstractConfigurableMBeanInfoAssembler {
    @Override
    protected boolean includeReadAttribute(Method method, String beanKey) {
        return true;
    }

    @Override
    protected boolean includeWriteAttribute(Method method, String beanKey) {
        return true;
    }

    @Override
    protected boolean includeOperation(Method method, String beanKey) {
        return true;
    }
}

