/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.amazonaws.DefaultRequest
 *  com.amazonaws.SignableRequest
 *  com.amazonaws.auth.AWS4Signer
 *  com.amazonaws.auth.AWSCredentials
 *  com.amazonaws.http.HttpMethodName
 */
package org.springframework.vault.authentication;

import com.amazonaws.DefaultRequest;
import com.amazonaws.SignableRequest;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.http.HttpMethodName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.vault.VaultException;
import org.springframework.vault.authentication.AuthenticationSteps;
import org.springframework.vault.authentication.AuthenticationStepsFactory;
import org.springframework.vault.authentication.AuthenticationUtil;
import org.springframework.vault.authentication.AwsIamAuthenticationOptions;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.LoginTokenUtil;
import org.springframework.vault.authentication.VaultLoginException;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultToken;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

public class AwsIamAuthentication
implements ClientAuthentication,
AuthenticationStepsFactory {
    private static final Log logger = LogFactory.getLog(AwsIamAuthentication.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String REQUEST_BODY = "Action=GetCallerIdentity&Version=2011-06-15";
    private static final String REQUEST_BODY_BASE64_ENCODED = Base64Utils.encodeToString("Action=GetCallerIdentity&Version=2011-06-15".getBytes());
    private final AwsIamAuthenticationOptions options;
    private final RestOperations vaultRestOperations;

    public AwsIamAuthentication(AwsIamAuthenticationOptions options, RestOperations vaultRestOperations) {
        Assert.notNull((Object)options, "AwsIamAuthenticationOptions must not be null");
        Assert.notNull((Object)vaultRestOperations, "Vault RestOperations must not be null");
        this.options = options;
        this.vaultRestOperations = vaultRestOperations;
    }

    public static AuthenticationSteps createAuthenticationSteps(AwsIamAuthenticationOptions options) {
        Assert.notNull((Object)options, "AwsIamAuthenticationOptions must not be null");
        AWSCredentials credentials = options.getCredentialsProvider().getCredentials();
        return AwsIamAuthentication.createAuthenticationSteps(options, credentials);
    }

    protected static AuthenticationSteps createAuthenticationSteps(AwsIamAuthenticationOptions options, AWSCredentials credentials) {
        return AuthenticationSteps.fromSupplier(() -> AwsIamAuthentication.createRequestBody(options, credentials)).login(AuthenticationUtil.getLoginPath(options.getPath()), new String[0]);
    }

    @Override
    public VaultToken login() throws VaultException {
        return this.createTokenUsingAwsIam();
    }

    @Override
    public AuthenticationSteps getAuthenticationSteps() {
        return AwsIamAuthentication.createAuthenticationSteps(this.options, this.options.getCredentialsProvider().getCredentials());
    }

    private VaultToken createTokenUsingAwsIam() {
        Map<String, String> login = AwsIamAuthentication.createRequestBody(this.options);
        try {
            VaultResponse response = this.vaultRestOperations.postForObject(AuthenticationUtil.getLoginPath(this.options.getPath()), login, VaultResponse.class, new Object[0]);
            Assert.state(response != null && response.getAuth() != null, "Auth field must not be null");
            if (logger.isDebugEnabled()) {
                if (response.getAuth().get("metadata") instanceof Map) {
                    Map metadata = (Map)response.getAuth().get("metadata");
                    logger.debug(String.format("Login successful using AWS-IAM authentication for user id %s, ARN %s", metadata.get("client_user_id"), metadata.get("canonical_arn")));
                } else {
                    logger.debug("Login successful using AWS-IAM authentication");
                }
            }
            return LoginTokenUtil.from(response.getAuth());
        }
        catch (RestClientException e) {
            throw VaultLoginException.create("AWS-IAM", e);
        }
    }

    protected static Map<String, String> createRequestBody(AwsIamAuthenticationOptions options) {
        return AwsIamAuthentication.createRequestBody(options, options.getCredentialsProvider().getCredentials());
    }

    private static Map<String, String> createRequestBody(AwsIamAuthenticationOptions options, AWSCredentials credentials) {
        HashMap<String, String> login = new HashMap<String, String>();
        login.put("iam_http_request_method", "POST");
        login.put("iam_request_url", Base64Utils.encodeToString(options.getEndpointUri().toString().getBytes()));
        login.put("iam_request_body", REQUEST_BODY_BASE64_ENCODED);
        String headerJson = AwsIamAuthentication.getSignedHeaders(options, credentials);
        login.put("iam_request_headers", Base64Utils.encodeToString(headerJson.getBytes()));
        if (!StringUtils.isEmpty(options.getRole())) {
            login.put("role", options.getRole());
        }
        return login;
    }

    private static String getSignedHeaders(AwsIamAuthenticationOptions options, AWSCredentials credentials) {
        Map<String, String> headers = AwsIamAuthentication.createIamRequestHeaders(options);
        AWS4Signer signer = new AWS4Signer();
        DefaultRequest request = new DefaultRequest("sts");
        request.setContent((InputStream)new ByteArrayInputStream(REQUEST_BODY.getBytes()));
        request.setHeaders(headers);
        request.setHttpMethod(HttpMethodName.POST);
        request.setEndpoint(options.getEndpointUri());
        signer.setServiceName(request.getServiceName());
        signer.sign((SignableRequest)request, credentials);
        LinkedHashMap map = new LinkedHashMap();
        for (Map.Entry entry : request.getHeaders().entrySet()) {
            map.put(entry.getKey(), Collections.singletonList(entry.getValue()));
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(map);
        }
        catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot serialize headers to JSON", e);
        }
    }

    private static Map<String, String> createIamRequestHeaders(AwsIamAuthenticationOptions options) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
        headers.put("Content-Length", "" + REQUEST_BODY.length());
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        if (StringUtils.hasText(options.getServerId())) {
            headers.put("X-Vault-AWS-IAM-Server-ID", options.getServerId());
        }
        return headers;
    }
}

