/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditAttribute
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.ao.dao;

import com.atlassian.audit.entity.AuditAttribute;
import java.util.List;
import javax.annotation.Nonnull;

public interface AttributesSerializer {
    public List<AuditAttribute> deserialize(@Nonnull String var1);

    public String serialize(@Nonnull Iterable<AuditAttribute> var1);
}

