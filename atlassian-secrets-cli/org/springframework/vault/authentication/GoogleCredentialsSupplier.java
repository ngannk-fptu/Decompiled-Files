/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.auth.oauth2.GoogleCredentials
 */
package org.springframework.vault.authentication;

import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.function.Supplier;

@FunctionalInterface
public interface GoogleCredentialsSupplier
extends Supplier<GoogleCredentials> {
    @Override
    default public GoogleCredentials get() {
        try {
            return this.getCredentials();
        }
        catch (IOException e) {
            throw new IllegalStateException("Cannot obtain GoogleCredentials", e);
        }
    }

    public GoogleCredentials getCredentials() throws IOException;
}

