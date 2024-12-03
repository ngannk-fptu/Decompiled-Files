/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer.internal;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.services.s3.transfer.Transfer;

@SdkInternalApi
public interface TransferStateChangeListener {
    public void transferStateChanged(Transfer var1, Transfer.TransferState var2);
}

