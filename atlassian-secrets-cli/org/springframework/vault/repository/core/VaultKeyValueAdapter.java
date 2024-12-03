/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.keyvalue.core.AbstractKeyValueAdapter
 *  org.springframework.data.keyvalue.core.QueryEngine
 *  org.springframework.data.mapping.context.MappingContext
 *  org.springframework.data.util.CloseableIterator
 */
package org.springframework.vault.repository.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.data.keyvalue.core.AbstractKeyValueAdapter;
import org.springframework.data.keyvalue.core.QueryEngine;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.util.CloseableIterator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.repository.convert.MappingVaultConverter;
import org.springframework.vault.repository.convert.SecretDocument;
import org.springframework.vault.repository.convert.VaultConverter;
import org.springframework.vault.repository.core.VaultQueryEngine;
import org.springframework.vault.repository.mapping.VaultMappingContext;
import org.springframework.vault.repository.mapping.VaultPersistentEntity;
import org.springframework.vault.repository.mapping.VaultPersistentProperty;
import org.springframework.vault.support.VaultResponse;

public class VaultKeyValueAdapter
extends AbstractKeyValueAdapter {
    private final VaultOperations vaultOperations;
    private final VaultConverter vaultConverter;

    public VaultKeyValueAdapter(VaultOperations vaultOperations) {
        this(vaultOperations, new MappingVaultConverter((MappingContext<? extends VaultPersistentEntity<?>, VaultPersistentProperty>)new VaultMappingContext()));
    }

    public VaultKeyValueAdapter(VaultOperations vaultOperations, VaultConverter vaultConverter) {
        super((QueryEngine)new VaultQueryEngine());
        Assert.notNull((Object)vaultOperations, "VaultOperations must not be null");
        Assert.notNull((Object)vaultConverter, "VaultConverter must not be null");
        this.vaultOperations = vaultOperations;
        this.vaultConverter = vaultConverter;
    }

    public Object put(Object id, Object item, String keyspace) {
        SecretDocument secretDocument = new SecretDocument(id.toString());
        this.vaultConverter.write(item, secretDocument);
        this.vaultOperations.write(this.createKey(id, keyspace), secretDocument.getBody());
        return secretDocument;
    }

    public boolean contains(Object id, String keyspace) {
        return this.doList(keyspace).contains(id.toString());
    }

    @Nullable
    public Object get(Object id, String keyspace) {
        return this.get(id, keyspace, Object.class);
    }

    @Nullable
    public <T> T get(Object id, String keyspace, Class<T> type) {
        VaultResponse response = this.vaultOperations.read(this.createKey(id, keyspace));
        if (response == null) {
            return null;
        }
        SecretDocument document = SecretDocument.from(id.toString(), response);
        return (T)this.vaultConverter.read(type, document);
    }

    @Nullable
    public Object delete(Object id, String keyspace) {
        return this.delete(id, keyspace, Object.class);
    }

    @Nullable
    public <T> T delete(Object id, String keyspace, Class<T> type) {
        T entity = this.get(id, keyspace, type);
        if (entity == null) {
            return null;
        }
        this.vaultOperations.delete(this.createKey(id, keyspace));
        return entity;
    }

    public Iterable<?> getAllOf(String keyspace) {
        List<String> list = this.doList(keyspace);
        ArrayList<Object> items = new ArrayList<Object>(list.size());
        for (String id : list) {
            items.add(this.get(id, keyspace));
        }
        return items;
    }

    public CloseableIterator<Map.Entry<Object, Object>> entries(final String keyspace) {
        List<String> list = this.doList(keyspace);
        final Iterator<String> iterator = list.iterator();
        return new CloseableIterator<Map.Entry<Object, Object>>(){

            public void close() {
            }

            public boolean hasNext() {
                return iterator.hasNext();
            }

            public Map.Entry<Object, Object> next() {
                final String key = (String)iterator.next();
                return new Map.Entry<Object, Object>(){

                    @Override
                    public Object getKey() {
                        return key;
                    }

                    @Override
                    @Nullable
                    public Object getValue() {
                        return VaultKeyValueAdapter.this.get(key, keyspace);
                    }

                    @Override
                    public Object setValue(Object value) {
                        throw new UnsupportedOperationException();
                    }
                };
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public void deleteAllOf(String keyspace) {
        List<String> ids = this.doList(keyspace);
        for (String id : ids) {
            this.vaultOperations.delete(this.createKey(id, keyspace));
        }
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public long count(String keyspace) {
        List<String> list = this.doList(keyspace);
        return list.size();
    }

    public void destroy() throws Exception {
    }

    List<String> doList(String keyspace) {
        List<String> list = this.vaultOperations.list(keyspace);
        return list == null ? Collections.emptyList() : list;
    }

    private String createKey(Object id, String keyspace) {
        return String.format("%s/%s", keyspace, id);
    }

    MappingContext<? extends VaultPersistentEntity<?>, VaultPersistentProperty> getMappingContext() {
        return this.vaultConverter.getMappingContext();
    }
}

