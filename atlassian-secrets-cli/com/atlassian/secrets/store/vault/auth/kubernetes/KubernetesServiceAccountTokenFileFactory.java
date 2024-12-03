/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.store.vault.auth.kubernetes;

import org.springframework.vault.authentication.KubernetesServiceAccountTokenFile;

public interface KubernetesServiceAccountTokenFileFactory {
    public KubernetesServiceAccountTokenFile getKubernetesServiceAccountTokenFile(String var1);

    public KubernetesServiceAccountTokenFile getKubernetesServiceAccountTokenFile();
}

