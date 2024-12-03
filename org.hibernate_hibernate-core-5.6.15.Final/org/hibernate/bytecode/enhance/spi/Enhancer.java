/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.enhance.spi;

import org.hibernate.bytecode.enhance.spi.EnhancementException;

public interface Enhancer {
    public byte[] enhance(String var1, byte[] var2) throws EnhancementException;
}

