/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.spi;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public interface ClassTransformer {
    public byte[] transform(ClassLoader var1, String var2, Class<?> var3, ProtectionDomain var4, byte[] var5) throws IllegalClassFormatException;
}

