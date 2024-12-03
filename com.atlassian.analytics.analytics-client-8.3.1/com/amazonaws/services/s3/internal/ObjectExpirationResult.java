/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import java.util.Date;

public interface ObjectExpirationResult {
    public Date getExpirationTime();

    public void setExpirationTime(Date var1);

    public String getExpirationTimeRuleId();

    public void setExpirationTimeRuleId(String var1);
}

