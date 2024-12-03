/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

public interface IUnwovenClassFile {
    public String getFilename();

    public String getClassName();

    public byte[] getBytes();

    public char[] getClassNameAsChars();
}

