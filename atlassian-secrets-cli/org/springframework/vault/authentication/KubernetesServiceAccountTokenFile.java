/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication;

import java.io.File;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.vault.authentication.KubernetesJwtSupplier;
import org.springframework.vault.authentication.ResourceCredentialSupplier;

public class KubernetesServiceAccountTokenFile
extends ResourceCredentialSupplier
implements KubernetesJwtSupplier {
    public static final String DEFAULT_KUBERNETES_SERVICE_ACCOUNT_TOKEN_FILE = "/var/run/secrets/kubernetes.io/serviceaccount/token";

    public KubernetesServiceAccountTokenFile() {
        this(DEFAULT_KUBERNETES_SERVICE_ACCOUNT_TOKEN_FILE);
    }

    public KubernetesServiceAccountTokenFile(String path) {
        this(new FileSystemResource(path));
    }

    public KubernetesServiceAccountTokenFile(File file) {
        this(new FileSystemResource(file));
    }

    public KubernetesServiceAccountTokenFile(Resource resource) {
        super(resource);
    }
}

