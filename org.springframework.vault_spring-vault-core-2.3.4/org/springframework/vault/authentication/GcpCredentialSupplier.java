/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.api.client.googleapis.auth.oauth2.GoogleCredential
 */
package org.springframework.vault.authentication;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import java.io.IOException;
import java.util.function.Supplier;

@FunctionalInterface
public interface GcpCredentialSupplier
extends Supplier<GoogleCredential> {
    @Override
    default public GoogleCredential get() {
        try {
            return this.getCredential();
        }
        catch (IOException e) {
            throw new IllegalStateException("Cannot obtain GoogleCredential", e);
        }
    }

    public GoogleCredential getCredential() throws IOException;
}

