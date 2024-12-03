/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3;

import com.amazonaws.services.s3.internal.FileDeletionEvent;

public interface OnFileDelete {
    public void onFileDelete(FileDeletionEvent var1);
}

