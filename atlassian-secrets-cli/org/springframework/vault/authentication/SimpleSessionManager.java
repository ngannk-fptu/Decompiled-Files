/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication;

import java.util.Optional;
import org.springframework.util.Assert;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.SessionManager;
import org.springframework.vault.support.VaultToken;

public class SimpleSessionManager
implements SessionManager {
    private final ClientAuthentication clientAuthentication;
    private final Object lock = new Object();
    private volatile Optional<VaultToken> token = Optional.empty();

    public SimpleSessionManager(ClientAuthentication clientAuthentication) {
        Assert.notNull((Object)clientAuthentication, "ClientAuthentication must not be null");
        this.clientAuthentication = clientAuthentication;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public VaultToken getSessionToken() {
        if (!this.token.isPresent()) {
            Object object = this.lock;
            synchronized (object) {
                if (!this.token.isPresent()) {
                    this.token = Optional.of(this.clientAuthentication.login());
                }
            }
        }
        return this.token.orElseThrow(() -> new IllegalStateException("Cannot obtain VaultToken"));
    }
}

