/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.secrets.api.SecretStoreException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.vault.authentication.KubernetesAuthentication
 *  org.springframework.vault.authentication.KubernetesAuthenticationOptions
 *  org.springframework.vault.authentication.KubernetesAuthenticationOptions$KubernetesAuthenticationOptionsBuilder
 *  org.springframework.vault.client.VaultEndpoint
 *  org.springframework.web.client.RestOperations
 *  org.springframework.web.client.RestTemplate
 *  org.springframework.web.util.DefaultUriBuilderFactory
 *  org.springframework.web.util.UriTemplateHandler
 */
package com.atlassian.secrets.store.vault.auth.kubernetes;

import com.atlassian.secrets.api.SecretStoreException;
import com.atlassian.secrets.store.vault.VaultParams;
import com.atlassian.secrets.store.vault.auth.AuthenticationConfigUtils;
import com.atlassian.secrets.store.vault.auth.kubernetes.DefaultKubernetesServiceAccountTokenFileFactory;
import com.atlassian.secrets.store.vault.auth.kubernetes.KubernetesServiceAccountTokenFileFactory;
import java.net.URI;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.vault.authentication.KubernetesAuthentication;
import org.springframework.vault.authentication.KubernetesAuthenticationOptions;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;

public class KubernetesAuthenticationFactory {
    private static final Logger log = LoggerFactory.getLogger(KubernetesAuthenticationFactory.class);
    private final KubernetesServiceAccountTokenFileFactory kubernetesServiceAccountTokenFileFactory;

    public KubernetesAuthenticationFactory() {
        this(new DefaultKubernetesServiceAccountTokenFileFactory());
    }

    public KubernetesAuthenticationFactory(KubernetesServiceAccountTokenFileFactory kubernetesServiceAccountTokenFileFactory) {
        this.kubernetesServiceAccountTokenFileFactory = kubernetesServiceAccountTokenFileFactory;
    }

    public KubernetesAuthentication getAuthentication(VaultParams params) throws SecretStoreException {
        try {
            String endpoint = params.getEndpoint();
            String role = AuthenticationConfigUtils.parseRequiredValueFromEnv("SECRET_STORE_VAULT_KUBE_AUTH_ROLE", "secret.store.vault.kube.auth.role");
            String kubeAuthPath = AuthenticationConfigUtils.parseOptionalValueFromEnv("SECRET_STORE_VAULT_KUBE_AUTH_PATH", "secret.store.vault.kube.auth.path");
            String jwtPath = AuthenticationConfigUtils.parseOptionalValueFromEnv("SECRET_STORE_VAULT_KUBE_AUTH_JWT_PATH", "secret.store.vault.kube.auth.jwt.path");
            KubernetesAuthenticationOptions.KubernetesAuthenticationOptionsBuilder builder = KubernetesAuthenticationOptions.builder().role(role);
            if (jwtPath != null) {
                builder.jwtSupplier((Supplier)this.kubernetesServiceAccountTokenFileFactory.getKubernetesServiceAccountTokenFile(jwtPath));
            }
            if (kubeAuthPath != null) {
                builder.path(kubeAuthPath);
            }
            KubernetesAuthenticationOptions options = builder.build();
            VaultEndpoint vaultEndpoint = VaultEndpoint.from((URI)URI.create(endpoint));
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setUriTemplateHandler((UriTemplateHandler)new DefaultUriBuilderFactory(String.format("%s/%s/", vaultEndpoint, vaultEndpoint.getPath())));
            return new KubernetesAuthentication(options, (RestOperations)restTemplate);
        }
        catch (Exception e) {
            log.error("Problem when getting the Kubernetes Authentication: {}", (Object)e.getMessage());
            throw new SecretStoreException("Problem when getting the Kubernetes Authentication.", (Throwable)e);
        }
    }

    static class PropertyConfig {
        public static final String ROLE = "secret.store.vault.kube.auth.role";
        public static final String PATH = "secret.store.vault.kube.auth.path";
        public static final String JWT_PATH = "secret.store.vault.kube.auth.jwt.path";

        private PropertyConfig() {
        }
    }

    static class EnvConfig {
        public static final String ROLE = "SECRET_STORE_VAULT_KUBE_AUTH_ROLE";
        public static final String PATH = "SECRET_STORE_VAULT_KUBE_AUTH_PATH";
        public static final String JWT_PATH = "SECRET_STORE_VAULT_KUBE_AUTH_JWT_PATH";

        private EnvConfig() {
        }
    }
}

