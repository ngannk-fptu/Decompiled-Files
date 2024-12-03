/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.store.vault.auth.kubernetes;

import com.atlassian.secrets.store.vault.auth.kubernetes.KubernetesServiceAccountTokenFileFactory;
import org.springframework.vault.authentication.KubernetesServiceAccountTokenFile;

public class DefaultKubernetesServiceAccountTokenFileFactory
implements KubernetesServiceAccountTokenFileFactory {
    @Override
    public KubernetesServiceAccountTokenFile getKubernetesServiceAccountTokenFile(String jwtPath) {
        return new KubernetesServiceAccountTokenFile(jwtPath);
    }

    @Override
    public KubernetesServiceAccountTokenFile getKubernetesServiceAccountTokenFile() {
        return new KubernetesServiceAccountTokenFile();
    }
}

