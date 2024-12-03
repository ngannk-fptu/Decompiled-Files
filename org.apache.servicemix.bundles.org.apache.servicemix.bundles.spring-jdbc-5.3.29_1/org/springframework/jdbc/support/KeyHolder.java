/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.InvalidDataAccessApiUsageException
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.support;

import java.util.List;
import java.util.Map;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.lang.Nullable;

public interface KeyHolder {
    @Nullable
    public Number getKey() throws InvalidDataAccessApiUsageException;

    @Nullable
    public <T> T getKeyAs(Class<T> var1) throws InvalidDataAccessApiUsageException;

    @Nullable
    public Map<String, Object> getKeys() throws InvalidDataAccessApiUsageException;

    public List<Map<String, Object>> getKeyList();
}

