/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cglib.core;

import org.springframework.asm.Type;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.KeyFactoryCustomizer;

public interface Customizer
extends KeyFactoryCustomizer {
    public void customize(CodeEmitter var1, Type var2);
}

