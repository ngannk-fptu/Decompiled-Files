/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.oauth2.client.api.storage;

import com.atlassian.annotations.PublicApi;
import com.atlassian.oauth2.client.api.ClientToken;
import com.atlassian.oauth2.client.api.storage.token.exception.RecoverableTokenException;
import com.atlassian.oauth2.client.api.storage.token.exception.UnrecoverableTokenException;
import java.time.Duration;

@PublicApi
public interface TokenHandler {
    public <T> T execute(String var1, ClientTokenCallback<T> var2) throws UnrecoverableTokenException, RecoverableTokenException;

    public <T> T execute(String var1, ClientTokenCallback<T> var2, Duration var3) throws UnrecoverableTokenException, RecoverableTokenException;

    public ClientToken getRefreshedToken(String var1) throws UnrecoverableTokenException, RecoverableTokenException;

    public ClientToken getRefreshedToken(String var1, Duration var2) throws UnrecoverableTokenException, RecoverableTokenException;

    public static class InvalidTokenException
    extends Exception {
        public InvalidTokenException() {
        }

        public InvalidTokenException(String message) {
            super(message);
        }

        public InvalidTokenException(String message, Throwable cause) {
            super(message, cause);
        }

        public InvalidTokenException(Throwable cause) {
            super(cause);
        }
    }

    @FunctionalInterface
    public static interface ClientTokenCallback<T> {
        public T apply(ClientToken var1) throws InvalidTokenException;
    }
}

