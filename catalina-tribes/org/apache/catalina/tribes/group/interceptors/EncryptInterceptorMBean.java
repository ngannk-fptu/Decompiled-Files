/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.group.interceptors;

public interface EncryptInterceptorMBean {
    public int getOptionFlag();

    public void setOptionFlag(int var1);

    public void setEncryptionAlgorithm(String var1);

    public String getEncryptionAlgorithm();

    public void setEncryptionKey(byte[] var1);

    public byte[] getEncryptionKey();

    public void setProviderName(String var1);

    public String getProviderName();
}

