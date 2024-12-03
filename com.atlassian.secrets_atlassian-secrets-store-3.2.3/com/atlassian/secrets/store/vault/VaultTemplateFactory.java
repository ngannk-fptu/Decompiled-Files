/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.vault.authentication.ClientAuthentication
 *  org.springframework.vault.core.VaultTemplate
 */
package com.atlassian.secrets.store.vault;

import java.net.URI;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.core.VaultTemplate;

public interface VaultTemplateFactory {
    public VaultTemplate getTemplate(URI var1, ClientAuthentication var2);
}

