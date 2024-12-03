/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.ChangedValue
 *  javax.annotation.Nullable
 */
package com.atlassian.plugins.authentication.impl.config.audit;

import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import java.util.List;
import javax.annotation.Nullable;

public interface IdpConfigMapper {
    public List<ChangedValue> mapChanges(@Nullable IdpConfig var1, @Nullable IdpConfig var2);
}

