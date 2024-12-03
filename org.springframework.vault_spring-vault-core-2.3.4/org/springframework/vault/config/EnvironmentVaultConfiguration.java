/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 *  org.springframework.web.client.RestOperations
 */
package org.springframework.vault.config;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.vault.authentication.AppIdAuthentication;
import org.springframework.vault.authentication.AppIdAuthenticationOptions;
import org.springframework.vault.authentication.AppIdUserIdMechanism;
import org.springframework.vault.authentication.AppRoleAuthentication;
import org.springframework.vault.authentication.AppRoleAuthenticationOptions;
import org.springframework.vault.authentication.AwsEc2Authentication;
import org.springframework.vault.authentication.AwsEc2AuthenticationOptions;
import org.springframework.vault.authentication.AzureMsiAuthentication;
import org.springframework.vault.authentication.AzureMsiAuthenticationOptions;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.ClientCertificateAuthentication;
import org.springframework.vault.authentication.CubbyholeAuthentication;
import org.springframework.vault.authentication.CubbyholeAuthenticationOptions;
import org.springframework.vault.authentication.IpAddressUserId;
import org.springframework.vault.authentication.KubernetesAuthentication;
import org.springframework.vault.authentication.KubernetesAuthenticationOptions;
import org.springframework.vault.authentication.KubernetesServiceAccountTokenFile;
import org.springframework.vault.authentication.MacAddressUserId;
import org.springframework.vault.authentication.StaticUserId;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;
import org.springframework.vault.support.SslConfiguration;
import org.springframework.vault.support.VaultToken;
import org.springframework.web.client.RestOperations;

@Configuration
public class EnvironmentVaultConfiguration
extends AbstractVaultConfiguration
implements ApplicationContextAware {
    private static final Log logger = LogFactory.getLog(EnvironmentVaultConfiguration.class);
    @Nullable
    private RestOperations cachedRestOperations;
    @Nullable
    private ApplicationContext applicationContext;

    @Override
    public RestOperations restOperations() {
        if (this.cachedRestOperations != null) {
            return this.cachedRestOperations;
        }
        this.cachedRestOperations = super.restOperations();
        return this.cachedRestOperations;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        super.setApplicationContext(applicationContext);
    }

    @Override
    public VaultEndpoint vaultEndpoint() {
        String uri = this.getProperty("vault.uri");
        if (uri != null) {
            return VaultEndpoint.from(URI.create(uri));
        }
        throw new IllegalStateException("Vault URI (vault.uri) is null");
    }

    @Override
    public SslConfiguration sslConfiguration() {
        SslConfiguration.KeyStoreConfiguration keyStoreConfiguration = this.getKeyStoreConfiguration("vault.ssl.key-store", "vault.ssl.key-store-password", "vault.ssl.key-store-type");
        SslConfiguration.KeyStoreConfiguration trustStoreConfiguration = this.getKeyStoreConfiguration("vault.ssl.trust-store", "vault.ssl.trust-store-password", "vault.ssl.trust-store-type");
        List<String> enabledProtocols = this.getPropertyAsList("vault.ssl.enabled-protocols");
        List<String> enabledCipherSuites = this.getPropertyAsList("vault.ssl.enabled-cipher-suites");
        return new SslConfiguration(keyStoreConfiguration, trustStoreConfiguration, enabledProtocols, enabledCipherSuites);
    }

    private SslConfiguration.KeyStoreConfiguration getKeyStoreConfiguration(String resourceProperty, String passwordProperty, String keystoreTypeProperty) {
        Resource keyStore = this.getResource(resourceProperty);
        String keyStorePassword = this.getProperty(passwordProperty);
        String keystoreType = this.getProperty(keystoreTypeProperty, "PEM");
        if (keyStore == null) {
            return SslConfiguration.KeyStoreConfiguration.unconfigured();
        }
        if (StringUtils.hasText((String)keyStorePassword)) {
            return SslConfiguration.KeyStoreConfiguration.of(keyStore, keyStorePassword.toCharArray(), keystoreType);
        }
        return SslConfiguration.KeyStoreConfiguration.of(keyStore).withStoreType(keystoreType);
    }

    @Override
    public ClientAuthentication clientAuthentication() {
        String authentication = this.getProperty("vault.authentication", AuthenticationMethod.TOKEN.name()).toUpperCase().replace('-', '_');
        AuthenticationMethod authenticationMethod = AuthenticationMethod.valueOf(authentication);
        switch (authenticationMethod) {
            case TOKEN: {
                return this.tokenAuthentication();
            }
            case APPID: {
                return this.appIdAuthentication();
            }
            case APPROLE: {
                return this.appRoleAuthentication();
            }
            case AWS_EC2: {
                return this.awsEc2Authentication();
            }
            case AZURE: {
                return this.azureMsiAuthentication();
            }
            case CERT: {
                return new ClientCertificateAuthentication(this.restOperations());
            }
            case CUBBYHOLE: {
                return this.cubbyholeAuthentication();
            }
            case KUBERNETES: {
                return this.kubeAuthentication();
            }
        }
        throw new IllegalStateException(String.format("Vault authentication method %s is not supported with %s", new Object[]{authenticationMethod, this.getClass().getSimpleName()}));
    }

    protected ClientAuthentication tokenAuthentication() {
        String token = this.getProperty("vault.token");
        Assert.hasText((String)token, (String)"Vault Token authentication: Token (vault.token) must not be empty");
        return new TokenAuthentication(token);
    }

    protected ClientAuthentication appIdAuthentication() {
        String appId = this.getProperty("vault.app-id.app-id", this.getProperty("spring.application.name"));
        String userId = this.getProperty("vault.app-id.user-id");
        String path = this.getProperty("vault.app-id.app-id-path", "app-id");
        Assert.hasText((String)appId, (String)"Vault AppId authentication: AppId (vault.app-id.app-id) must not be empty");
        Assert.hasText((String)userId, (String)"Vault AppId authentication: UserId (vault.app-id.user-id) must not be empty");
        AppIdAuthenticationOptions.AppIdAuthenticationOptionsBuilder builder = AppIdAuthenticationOptions.builder().appId(appId).userIdMechanism(this.getAppIdUserIdMechanism(userId)).path(path);
        return new AppIdAuthentication(builder.build(), this.restOperations());
    }

    protected ClientAuthentication appRoleAuthentication() {
        String roleId = this.getProperty("vault.app-role.role-id");
        String secretId = this.getProperty("vault.app-role.secret-id");
        String path = this.getProperty("vault.app-role.app-role-path", "approle");
        Assert.hasText((String)roleId, (String)"Vault AppRole authentication: RoleId (vault.app-role.role-id) must not be empty");
        AppRoleAuthenticationOptions.AppRoleAuthenticationOptionsBuilder builder = AppRoleAuthenticationOptions.builder().roleId(AppRoleAuthenticationOptions.RoleId.provided(roleId)).path(path);
        if (StringUtils.hasText((String)secretId)) {
            builder = builder.secretId(AppRoleAuthenticationOptions.SecretId.provided(secretId));
        }
        return new AppRoleAuthentication(builder.build(), this.restOperations());
    }

    protected AppIdUserIdMechanism getAppIdUserIdMechanism(String userId) {
        if (userId.equalsIgnoreCase(AppIdUserId.IP_ADDRESS.name())) {
            return new IpAddressUserId();
        }
        if (userId.equalsIgnoreCase(AppIdUserId.MAC_ADDRESS.name())) {
            return new MacAddressUserId();
        }
        return new StaticUserId(userId);
    }

    protected ClientAuthentication awsEc2Authentication() {
        String role = this.getProperty("vault.aws-ec2.role");
        String roleId = this.getProperty("vault.aws-ec2.role-id");
        String identityDocument = this.getProperty("vault.aws-ec2.identity-document");
        String path = this.getProperty("vault.aws-ec2.aws-ec2-path", "aws-ec2");
        Assert.isTrue((StringUtils.hasText((String)roleId) || StringUtils.hasText((String)role) ? 1 : 0) != 0, (String)"Vault AWS-EC2 authentication: Role (vault.aws-ec2.role) must not be empty");
        if (StringUtils.hasText((String)roleId) && StringUtils.hasText((String)role)) {
            throw new IllegalStateException("AWS-EC2 Authentication: Only one of Role (vault.aws-ec2.role) or RoleId (deprecated, vault.aws-ec2.roleId) must be provided");
        }
        if (StringUtils.hasText((String)roleId)) {
            logger.warn((Object)"AWS-EC2 Authentication: vault.aws-ec2.roleId is deprecated. Please use vault.aws-ec2.role instead.");
        }
        AwsEc2AuthenticationOptions.AwsEc2AuthenticationOptionsBuilder builder = AwsEc2AuthenticationOptions.builder().role(StringUtils.hasText((String)role) ? role : roleId).path(path);
        if (StringUtils.hasText((String)identityDocument)) {
            builder.identityDocumentUri(URI.create(identityDocument));
        }
        return new AwsEc2Authentication(builder.build(), this.restOperations(), this.restOperations());
    }

    protected ClientAuthentication azureMsiAuthentication() {
        String role = this.getProperty("vault.azure-msi.role");
        String path = this.getProperty("vault.azure-msi.azure-path", "azure");
        URI metadataServiceUri = this.getUri("vault.azure-msi.metadata-service", AzureMsiAuthenticationOptions.DEFAULT_INSTANCE_METADATA_SERVICE_URI);
        URI identityTokenServiceUri = this.getUri("vault.azure-msi.identity-token-service", AzureMsiAuthenticationOptions.DEFAULT_IDENTITY_TOKEN_SERVICE_URI);
        Assert.hasText((String)role, (String)"Vault Azure MSI authentication: Role (vault.azure-msi.role) must not be empty");
        AzureMsiAuthenticationOptions.AzureMsiAuthenticationOptionsBuilder builder = AzureMsiAuthenticationOptions.builder().role(role).path(path).instanceMetadataUri(metadataServiceUri).identityTokenServiceUri(identityTokenServiceUri);
        return new AzureMsiAuthentication(builder.build(), this.restOperations());
    }

    protected ClientAuthentication cubbyholeAuthentication() {
        String token = this.getProperty("vault.token");
        Assert.hasText((String)token, (String)"Vault Cubbyhole authentication: Initial token (vault.token) must not be empty");
        CubbyholeAuthenticationOptions.CubbyholeAuthenticationOptionsBuilder builder = CubbyholeAuthenticationOptions.builder().wrapped().initialToken(VaultToken.of(token));
        return new CubbyholeAuthentication(builder.build(), this.restOperations());
    }

    protected ClientAuthentication kubeAuthentication() {
        String role = this.getProperty("vault.kubernetes.role");
        String tokenFile = this.getProperty("vault.kubernetes.service-account-token-file", "/var/run/secrets/kubernetes.io/serviceaccount/token");
        String path = this.getProperty("vault.kubernetes.kubernetes-path", "kubernetes");
        Assert.hasText((String)role, (String)"Vault Kubernetes authentication: role must not be empty");
        KubernetesServiceAccountTokenFile jwtSupplier = new KubernetesServiceAccountTokenFile(tokenFile);
        KubernetesAuthenticationOptions.KubernetesAuthenticationOptionsBuilder builder = KubernetesAuthenticationOptions.builder().role(role).jwtSupplier(jwtSupplier).path(path);
        return new KubernetesAuthentication(builder.build(), this.restOperations());
    }

    private List<String> getPropertyAsList(String key) {
        String val = this.getEnvironment().getProperty(key);
        if (val == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(val.split(",")).map(String::trim).collect(Collectors.toList());
    }

    @Nullable
    private String getProperty(String key) {
        return this.getEnvironment().getProperty(key);
    }

    private String getProperty(String key, String defaultValue) {
        return this.getEnvironment().getProperty(key, defaultValue);
    }

    private URI getUri(String key, URI defaultValue) {
        return (URI)this.getEnvironment().getProperty(key, URI.class, (Object)defaultValue);
    }

    @Nullable
    private Resource getResource(String key) {
        String value = this.getProperty(key);
        return value != null ? this.applicationContext.getResource(value) : null;
    }

    static enum AuthenticationMethod {
        TOKEN,
        APPID,
        APPROLE,
        AWS_EC2,
        AZURE,
        CERT,
        CUBBYHOLE,
        KUBERNETES;

    }

    static enum AppIdUserId {
        IP_ADDRESS,
        MAC_ADDRESS;

    }
}

