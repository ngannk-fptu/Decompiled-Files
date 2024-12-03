/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.password.encoder;

import com.atlassian.crowd.password.encoder.PasswordEncoder;

public interface UpgradeablePasswordEncoder
extends PasswordEncoder {
    public boolean isUpgradeRequired(String var1);
}

