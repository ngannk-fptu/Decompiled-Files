/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.Striped
 */
package com.atlassian.crowd.integration.http;

import com.atlassian.crowd.integration.http.TokenLockProvider;
import com.google.common.util.concurrent.Striped;
import java.util.concurrent.locks.Lock;

public class StripedTokenLockProvider
implements TokenLockProvider {
    private final Striped<Lock> striped;

    public StripedTokenLockProvider() {
        this(32);
    }

    public StripedTokenLockProvider(int locksCount) {
        this((Striped<Lock>)Striped.lock((int)locksCount));
    }

    public StripedTokenLockProvider(Striped<Lock> striped) {
        this.striped = striped;
    }

    @Override
    public Lock getLock(String token) {
        return (Lock)this.striped.get((Object)token);
    }
}

