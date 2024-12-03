/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

public interface ServerSideEncryptionResult {
    public String getSSEAlgorithm();

    public void setSSEAlgorithm(String var1);

    public String getSSECustomerAlgorithm();

    public void setSSECustomerAlgorithm(String var1);

    public String getSSECustomerKeyMd5();

    public void setSSECustomerKeyMd5(String var1);

    public Boolean getBucketKeyEnabled();

    public void setBucketKeyEnabled(Boolean var1);
}

