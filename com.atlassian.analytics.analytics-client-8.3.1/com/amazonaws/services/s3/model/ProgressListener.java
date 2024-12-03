/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.ProgressEvent;

@Deprecated
public interface ProgressListener {
    public void progressChanged(ProgressEvent var1);
}

