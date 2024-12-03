/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.realm;

import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.CredentialHandler;

public class NestedCredentialHandler
implements CredentialHandler {
    private final List<CredentialHandler> credentialHandlers = new ArrayList<CredentialHandler>();

    @Override
    public boolean matches(String inputCredentials, String storedCredentials) {
        for (CredentialHandler handler : this.credentialHandlers) {
            if (!handler.matches(inputCredentials, storedCredentials)) continue;
            return true;
        }
        return false;
    }

    @Override
    public String mutate(String inputCredentials) {
        if (this.credentialHandlers.isEmpty()) {
            return null;
        }
        return this.credentialHandlers.get(0).mutate(inputCredentials);
    }

    public void addCredentialHandler(CredentialHandler handler) {
        this.credentialHandlers.add(handler);
    }

    public CredentialHandler[] getCredentialHandlers() {
        return this.credentialHandlers.toArray(new CredentialHandler[0]);
    }
}

