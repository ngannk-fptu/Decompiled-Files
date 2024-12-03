/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication;

import java.net.URI;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.authentication.AzureVmEnvironment;

public class AzureMsiAuthenticationOptions {
    public static final String DEFAULT_AZURE_AUTHENTICATION_PATH = "azure";
    public static final URI DEFAULT_INSTANCE_METADATA_SERVICE_URI = URI.create("http://169.254.169.254/metadata/instance?api-version=2017-12-01");
    public static final URI DEFAULT_IDENTITY_TOKEN_SERVICE_URI = URI.create("http://169.254.169.254/metadata/identity/oauth2/token?resource=https://vault.hashicorp.com&api-version=2018-02-01");
    private final String path;
    private final String role;
    private final URI instanceMetadataServiceUri;
    private final URI identityTokenServiceUri;
    @Nullable
    private final AzureVmEnvironment vmEnvironment;

    private AzureMsiAuthenticationOptions(String path, String role, URI instanceMetadataServiceUri, URI identityTokenServiceUri, @Nullable AzureVmEnvironment vmEnvironment) {
        this.path = path;
        this.role = role;
        this.instanceMetadataServiceUri = instanceMetadataServiceUri;
        this.identityTokenServiceUri = identityTokenServiceUri;
        this.vmEnvironment = vmEnvironment;
    }

    public static AzureMsiAuthenticationOptionsBuilder builder() {
        return new AzureMsiAuthenticationOptionsBuilder();
    }

    public String getPath() {
        return this.path;
    }

    public String getRole() {
        return this.role;
    }

    @Nullable
    public AzureVmEnvironment getVmEnvironment() {
        return this.vmEnvironment;
    }

    public URI getInstanceMetadataServiceUri() {
        return this.instanceMetadataServiceUri;
    }

    public URI getIdentityTokenServiceUri() {
        return this.identityTokenServiceUri;
    }

    public static class AzureMsiAuthenticationOptionsBuilder {
        private String path = "azure";
        @Nullable
        private String role;
        @Nullable
        private AzureVmEnvironment vmEnvironment;
        private URI instanceMetadataServiceUri = DEFAULT_INSTANCE_METADATA_SERVICE_URI;
        private URI identityTokenServiceUri = DEFAULT_IDENTITY_TOKEN_SERVICE_URI;

        AzureMsiAuthenticationOptionsBuilder() {
        }

        public AzureMsiAuthenticationOptionsBuilder path(String path) {
            Assert.hasText(path, "Path must not be empty");
            this.path = path;
            return this;
        }

        public AzureMsiAuthenticationOptionsBuilder role(String role) {
            Assert.hasText(role, "Role must not be null or empty");
            this.role = role;
            return this;
        }

        public AzureMsiAuthenticationOptionsBuilder vmEnvironment(AzureVmEnvironment vmEnvironment) {
            Assert.notNull((Object)vmEnvironment, "AzureVmEnvironment must not be null");
            this.vmEnvironment = vmEnvironment;
            return this;
        }

        public AzureMsiAuthenticationOptionsBuilder instanceMetadataUri(URI instanceMetadataServiceUri) {
            Assert.notNull((Object)instanceMetadataServiceUri, "Instance metadata service URI must not be null");
            this.instanceMetadataServiceUri = instanceMetadataServiceUri;
            return this;
        }

        public AzureMsiAuthenticationOptionsBuilder identityTokenServiceUri(URI identityTokenServiceUri) {
            Assert.notNull((Object)identityTokenServiceUri, "Identity token service URI must not be null");
            this.identityTokenServiceUri = identityTokenServiceUri;
            return this;
        }

        public AzureMsiAuthenticationOptions build() {
            Assert.hasText(this.role, "Role must not be null or empty");
            return new AzureMsiAuthenticationOptions(this.path, this.role, this.instanceMetadataServiceUri, this.identityTokenServiceUri, this.vmEnvironment);
        }
    }
}

