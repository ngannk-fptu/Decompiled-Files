/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 *  org.springframework.web.client.RestClientException
 *  org.springframework.web.client.RestOperations
 */
package org.springframework.vault.authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.vault.VaultException;
import org.springframework.vault.authentication.AuthenticationSteps;
import org.springframework.vault.authentication.AuthenticationStepsFactory;
import org.springframework.vault.authentication.AuthenticationUtil;
import org.springframework.vault.authentication.AwsEc2AuthenticationOptions;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.LoginTokenUtil;
import org.springframework.vault.authentication.VaultLoginException;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultToken;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

public class AwsEc2Authentication
implements ClientAuthentication,
AuthenticationStepsFactory {
    private static final Log logger = LogFactory.getLog(AwsEc2Authentication.class);
    private static final char[] EMPTY = new char[0];
    private final AwsEc2AuthenticationOptions options;
    private final RestOperations vaultRestOperations;
    private final RestOperations awsMetadataRestOperations;
    private final AtomicReference<char[]> nonce = new AtomicReference<char[]>(EMPTY);

    public AwsEc2Authentication(RestOperations vaultRestOperations) {
        this(AwsEc2AuthenticationOptions.DEFAULT, vaultRestOperations, vaultRestOperations);
    }

    public AwsEc2Authentication(AwsEc2AuthenticationOptions options, RestOperations vaultRestOperations, RestOperations awsMetadataRestOperations) {
        Assert.notNull((Object)options, (String)"AwsEc2AuthenticationOptions must not be null");
        Assert.notNull((Object)vaultRestOperations, (String)"Vault RestOperations must not be null");
        Assert.notNull((Object)awsMetadataRestOperations, (String)"AWS Metadata RestOperations must not be null");
        this.options = options;
        this.vaultRestOperations = vaultRestOperations;
        this.awsMetadataRestOperations = awsMetadataRestOperations;
    }

    public static AuthenticationSteps createAuthenticationSteps(AwsEc2AuthenticationOptions options) {
        Assert.notNull((Object)options, (String)"AwsEc2AuthenticationOptions must not be null");
        AtomicReference<char[]> nonce = new AtomicReference<char[]>(EMPTY);
        return AwsEc2Authentication.createAuthenticationSteps(options, nonce, () -> AwsEc2Authentication.doCreateNonce(options));
    }

    protected static AuthenticationSteps createAuthenticationSteps(AwsEc2AuthenticationOptions options, AtomicReference<char[]> nonce, Supplier<char[]> nonceSupplier) {
        return AuthenticationSteps.fromHttpRequest(AuthenticationSteps.HttpRequestBuilder.get(options.getIdentityDocumentUri().toString(), new String[0]).as(String.class)).map(pkcs7 -> pkcs7.replaceAll("\\r", "")).map(pkcs7 -> pkcs7.replaceAll("\\n", "")).map(pkcs7 -> {
            HashMap<String, String> login = new HashMap<String, String>();
            if (StringUtils.hasText((String)options.getRole())) {
                login.put("role", options.getRole());
            }
            if (Objects.equals(nonce.get(), EMPTY)) {
                nonce.compareAndSet(EMPTY, (char[])nonceSupplier.get());
            }
            login.put("nonce", new String((char[])nonce.get()));
            login.put("pkcs7", (String)pkcs7);
            return login;
        }).login(AuthenticationUtil.getLoginPath(options.getPath()), new String[0]);
    }

    @Override
    public VaultToken login() throws VaultException {
        return this.createTokenUsingAwsEc2();
    }

    @Override
    public AuthenticationSteps getAuthenticationSteps() {
        return AwsEc2Authentication.createAuthenticationSteps(this.options, this.nonce, this::createNonce);
    }

    private VaultToken createTokenUsingAwsEc2() {
        Map<String, String> login = this.getEc2Login();
        try {
            VaultResponse response = (VaultResponse)this.vaultRestOperations.postForObject(AuthenticationUtil.getLoginPath(this.options.getPath()), login, VaultResponse.class, new Object[0]);
            Assert.state((response != null && response.getAuth() != null ? 1 : 0) != 0, (String)"Auth field must not be null");
            if (logger.isDebugEnabled()) {
                if (response.getAuth().get("metadata") instanceof Map) {
                    Map metadata = (Map)response.getAuth().get("metadata");
                    logger.debug((Object)String.format("Login successful using AWS-EC2 authentication for instance %s, AMI %s", metadata.get("instance_id"), metadata.get("instance_id")));
                } else {
                    logger.debug((Object)"Login successful using AWS-EC2 authentication");
                }
            }
            return LoginTokenUtil.from(response.getAuth());
        }
        catch (RestClientException e) {
            throw VaultLoginException.create("AWS-EC2", e);
        }
    }

    protected Map<String, String> getEc2Login() {
        HashMap<String, String> login = new HashMap<String, String>();
        if (StringUtils.hasText((String)this.options.getRole())) {
            login.put("role", this.options.getRole());
        }
        if (Objects.equals(this.nonce.get(), EMPTY)) {
            this.nonce.compareAndSet(EMPTY, this.createNonce());
        }
        login.put("nonce", new String(this.nonce.get()));
        try {
            String pkcs7 = (String)this.awsMetadataRestOperations.getForObject(this.options.getIdentityDocumentUri(), String.class);
            if (StringUtils.hasText((String)pkcs7)) {
                login.put("pkcs7", pkcs7.replaceAll("\\r", "").replaceAll("\\n", ""));
            }
            return login;
        }
        catch (RestClientException e) {
            throw new VaultLoginException(String.format("Cannot obtain Identity Document from %s", this.options.getIdentityDocumentUri()), e);
        }
    }

    protected char[] createNonce() {
        return AwsEc2Authentication.doCreateNonce(this.options);
    }

    private static char[] doCreateNonce(AwsEc2AuthenticationOptions options) {
        return options.getNonce().getValue();
    }
}

