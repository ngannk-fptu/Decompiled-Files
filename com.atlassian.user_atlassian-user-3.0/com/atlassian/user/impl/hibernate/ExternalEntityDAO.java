/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.hibernate;

import com.atlassian.user.ExternalEntity;

public interface ExternalEntityDAO {
    public ExternalEntity getExternalEntity(String var1);

    public void saveExternalEntity(ExternalEntity var1);

    public void removeExternalEntity(String var1);

    public ExternalEntity createExternalEntity(String var1);
}

