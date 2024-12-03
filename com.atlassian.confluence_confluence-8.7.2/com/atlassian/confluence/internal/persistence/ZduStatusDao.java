/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.internal.persistence;

import com.atlassian.confluence.internal.ZduStatusEntity;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ZduStatusDao {
    public void deleteStatus();

    @Transactional(readOnly=true)
    public Optional<ZduStatusEntity> getStatus();

    public void setStatus(ZduStatusEntity var1);
}

