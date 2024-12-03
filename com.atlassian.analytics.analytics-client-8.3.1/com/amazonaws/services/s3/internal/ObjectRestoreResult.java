/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import java.util.Date;

public interface ObjectRestoreResult {
    public Date getRestoreExpirationTime();

    public void setRestoreExpirationTime(Date var1);

    public void setOngoingRestore(boolean var1);

    public Boolean getOngoingRestore();
}

