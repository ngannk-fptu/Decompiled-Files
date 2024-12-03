/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.repository.convert;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.support.VaultResponse;

public class SecretDocument {
    @Nullable
    private String id;
    private final Map<String, Object> body;

    public SecretDocument() {
        this(null, new LinkedHashMap<String, Object>());
    }

    public SecretDocument(Map<String, Object> body) {
        this(null, body);
    }

    public SecretDocument(@Nullable String id, Map<String, Object> body) {
        Assert.notNull(body, "Body must not be null");
        this.id = id;
        this.body = body;
    }

    public SecretDocument(String id) {
        this(id, new LinkedHashMap<String, Object>());
    }

    public static SecretDocument from(@Nullable String id, VaultResponse vaultResponse) {
        return new SecretDocument(id, (Map)vaultResponse.getData());
    }

    @Nullable
    public String getId() {
        return this.id;
    }

    public void setId(@Nullable String id) {
        this.id = id;
    }

    public Map<String, Object> getBody() {
        return this.body;
    }

    @Nullable
    public Object get(String key) {
        return this.body.get(key);
    }

    public void put(String key, Object value) {
        this.body.put(key, value);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SecretDocument)) {
            return false;
        }
        SecretDocument that = (SecretDocument)o;
        return Objects.equals(this.id, that.id) && Objects.equals(this.body, that.body);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.body);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.getClass().getSimpleName());
        sb.append(" [id='").append(this.id).append('\'');
        sb.append(", body=").append(this.body);
        sb.append(']');
        return sb.toString();
    }
}

