/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.repository.convert;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.vault.repository.convert.SecretDocument;
import org.springframework.vault.repository.mapping.VaultPersistentProperty;

class SecretDocumentAccessor {
    private final SecretDocument document;
    private final Map<String, Object> body;

    SecretDocumentAccessor(SecretDocument document) {
        Assert.notNull((Object)document, "SecretDocument must not be null");
        this.document = document;
        this.body = document.getBody();
    }

    private SecretDocumentAccessor(SecretDocument document, Map<String, Object> body) {
        Assert.notNull((Object)document, "SecretDocument must not be null");
        Assert.notNull(body, "Body must not be null");
        this.document = document;
        this.body = body;
    }

    void put(VaultPersistentProperty prop, @Nullable Object value) {
        Assert.notNull((Object)prop, "VaultPersistentProperty must not be null");
        String fieldName = prop.getName();
        if (prop.isIdProperty()) {
            this.document.setId((String)value);
            return;
        }
        if (!fieldName.contains(".")) {
            this.body.put(fieldName, value);
            return;
        }
        Iterator<String> parts = Arrays.asList(fieldName.split("\\.")).iterator();
        Map<String, Object> document = this.body;
        while (parts.hasNext()) {
            String part = parts.next();
            if (parts.hasNext()) {
                document = SecretDocumentAccessor.getOrCreateNestedDocument(part, document);
                continue;
            }
            document.put(fieldName, value);
        }
    }

    @Nullable
    Object get(VaultPersistentProperty property) {
        String fieldName = property.getName();
        if (property.isIdProperty()) {
            return this.document.getId();
        }
        if (!fieldName.contains(".")) {
            return this.body.get(fieldName);
        }
        Iterator<String> parts = Arrays.asList(fieldName.split("\\.")).iterator();
        Map<String, Object> source = this.body;
        Object result = null;
        while (source != null && parts.hasNext()) {
            result = source.get(parts.next());
            if (!parts.hasNext()) continue;
            source = SecretDocumentAccessor.getAsMap(result);
        }
        return result;
    }

    boolean hasValue(VaultPersistentProperty property) {
        Assert.notNull((Object)property, "Property must not be null");
        if (property.isIdProperty()) {
            return StringUtils.hasText(this.document.getId());
        }
        String fieldName = property.getName();
        if (!fieldName.contains(".")) {
            return this.body.containsKey(fieldName);
        }
        String[] parts = fieldName.split("\\.");
        Map<String, Object> source = this.body;
        Object result = null;
        for (int i = 1; i < parts.length; ++i) {
            result = source.get(parts[i - 1]);
            if ((source = SecretDocumentAccessor.getAsMap(result)) != null) continue;
            return false;
        }
        return source.containsKey(parts[parts.length - 1]);
    }

    @Nullable
    private static Map<String, Object> getAsMap(Object source) {
        if (source instanceof Map) {
            return (Map)source;
        }
        return null;
    }

    private static Map<String, Object> getOrCreateNestedDocument(String key, Map<String, Object> source) {
        Object existing = source.get(key);
        if (existing instanceof Map) {
            return (Map)existing;
        }
        LinkedHashMap<String, Object> nested = new LinkedHashMap<String, Object>();
        source.put(key, nested);
        return nested;
    }

    public Map<String, Object> getBody() {
        return this.body;
    }

    public void setId(String id) {
        this.document.setId(id);
    }

    public SecretDocumentAccessor writeNested(VaultPersistentProperty property) {
        LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>)this.get(property);
        if (body == null) {
            body = new LinkedHashMap<String, Object>();
            this.put(property, body);
        }
        return new SecretDocumentAccessor(this.document, body);
    }
}

