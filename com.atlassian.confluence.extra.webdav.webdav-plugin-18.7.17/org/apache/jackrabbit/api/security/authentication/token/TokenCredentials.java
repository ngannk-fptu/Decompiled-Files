/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.apache.jackrabbit.api.security.authentication.token;

import java.util.HashMap;
import javax.jcr.Credentials;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TokenCredentials
implements Credentials {
    private final String token;
    private final HashMap<String, String> attributes = new HashMap();

    public TokenCredentials(@NotNull String token) throws IllegalArgumentException {
        if (token == null || token.length() == 0) {
            throw new IllegalArgumentException("Invalid token '" + token + "'");
        }
        this.token = token;
    }

    @NotNull
    public String getToken() {
        return this.token;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setAttribute(@NotNull String name, @Nullable String value) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (value == null) {
            this.removeAttribute(name);
            return;
        }
        HashMap<String, String> hashMap = this.attributes;
        synchronized (hashMap) {
            this.attributes.put(name, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public String getAttribute(@NotNull String name) {
        HashMap<String, String> hashMap = this.attributes;
        synchronized (hashMap) {
            return this.attributes.get(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeAttribute(@NotNull String name) {
        HashMap<String, String> hashMap = this.attributes;
        synchronized (hashMap) {
            this.attributes.remove(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NotNull
    public String[] getAttributeNames() {
        HashMap<String, String> hashMap = this.attributes;
        synchronized (hashMap) {
            return this.attributes.keySet().toArray(new String[0]);
        }
    }
}

