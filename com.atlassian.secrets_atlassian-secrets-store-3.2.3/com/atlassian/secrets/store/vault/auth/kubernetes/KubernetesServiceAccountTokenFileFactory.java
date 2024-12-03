/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.vault.authentication.KubernetesServiceAccountTokenFile
 */
package com.atlassian.secrets.store.vault.auth.kubernetes;

import org.springframework.vault.authentication.KubernetesServiceAccountTokenFile;

public interface KubernetesServiceAccountTokenFileFactory {
    public KubernetesServiceAccountTokenFile getKubernetesServiceAccountTokenFile(String var1);

    public KubernetesServiceAccountTokenFile getKubernetesServiceAccountTokenFile();
}

