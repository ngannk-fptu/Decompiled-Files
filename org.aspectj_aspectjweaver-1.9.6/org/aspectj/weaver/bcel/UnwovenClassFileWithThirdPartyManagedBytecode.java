/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import org.aspectj.weaver.bcel.UnwovenClassFile;

public class UnwovenClassFileWithThirdPartyManagedBytecode
extends UnwovenClassFile {
    IByteCodeProvider provider;

    public UnwovenClassFileWithThirdPartyManagedBytecode(String filename, String classname, IByteCodeProvider provider) {
        super(filename, classname, null);
        this.provider = provider;
    }

    @Override
    public byte[] getBytes() {
        return this.provider.getBytes();
    }

    public static interface IByteCodeProvider {
        public byte[] getBytes();
    }
}

