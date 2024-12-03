/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataRetrievalFailureException
 *  org.springframework.dao.InvalidDataAccessApiUsageException
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.Nullable;

public class GeneratedKeyHolder
implements KeyHolder {
    private final List<Map<String, Object>> keyList;

    public GeneratedKeyHolder() {
        this.keyList = new ArrayList<Map<String, Object>>(1);
    }

    public GeneratedKeyHolder(List<Map<String, Object>> keyList) {
        this.keyList = keyList;
    }

    @Override
    @Nullable
    public Number getKey() throws InvalidDataAccessApiUsageException, DataRetrievalFailureException {
        return this.getKeyAs(Number.class);
    }

    @Override
    @Nullable
    public <T> T getKeyAs(Class<T> keyType) throws InvalidDataAccessApiUsageException, DataRetrievalFailureException {
        if (this.keyList.isEmpty()) {
            return null;
        }
        if (this.keyList.size() > 1 || this.keyList.get(0).size() > 1) {
            throw new InvalidDataAccessApiUsageException("The getKey method should only be used when a single key is returned. The current key entry contains multiple keys: " + this.keyList);
        }
        Iterator<Object> keyIter = this.keyList.get(0).values().iterator();
        if (keyIter.hasNext()) {
            Object key = keyIter.next();
            if (key == null || !keyType.isAssignableFrom(key.getClass())) {
                throw new DataRetrievalFailureException("The generated key type is not supported. Unable to cast [" + (key != null ? key.getClass().getName() : null) + "] to [" + keyType.getName() + "].");
            }
            return keyType.cast(key);
        }
        throw new DataRetrievalFailureException("Unable to retrieve the generated key. Check that the table has an identity column enabled.");
    }

    @Override
    @Nullable
    public Map<String, Object> getKeys() throws InvalidDataAccessApiUsageException {
        if (this.keyList.isEmpty()) {
            return null;
        }
        if (this.keyList.size() > 1) {
            throw new InvalidDataAccessApiUsageException("The getKeys method should only be used when keys for a single row are returned. The current key list contains keys for multiple rows: " + this.keyList);
        }
        return this.keyList.get(0);
    }

    @Override
    public List<Map<String, Object>> getKeyList() {
        return this.keyList;
    }
}

