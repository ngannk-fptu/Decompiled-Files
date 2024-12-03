/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jni;

@Deprecated
public interface BIOCallback {
    public int write(byte[] var1);

    public int read(byte[] var1);

    public int puts(String var1);

    public String gets(int var1);
}

