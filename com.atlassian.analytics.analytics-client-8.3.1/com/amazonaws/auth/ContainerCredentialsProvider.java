/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.ContainerCredentialsFetcher;
import com.amazonaws.auth.ContainerCredentialsRetryPolicy;
import com.amazonaws.internal.CredentialsEndpointProvider;
import com.amazonaws.retry.internal.CredentialsEndpointRetryPolicy;
import com.amazonaws.util.CollectionUtils;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ContainerCredentialsProvider
implements AWSCredentialsProvider {
    static final String ECS_CONTAINER_CREDENTIALS_PATH = "AWS_CONTAINER_CREDENTIALS_RELATIVE_URI";
    static final String CONTAINER_CREDENTIALS_FULL_URI = "AWS_CONTAINER_CREDENTIALS_FULL_URI";
    static final String CONTAINER_AUTHORIZATION_TOKEN = "AWS_CONTAINER_AUTHORIZATION_TOKEN";
    private static final Set<String> ALLOWED_FULL_URI_HOSTS = ContainerCredentialsProvider.allowedHosts();
    private static final String ECS_CREDENTIALS_ENDPOINT = "http://169.254.170.2";
    private final ContainerCredentialsFetcher credentialsFetcher;

    @Deprecated
    public ContainerCredentialsProvider() {
        this(new ECSCredentialsEndpointProvider());
    }

    public ContainerCredentialsProvider(CredentialsEndpointProvider credentialsEndpointProvider) {
        this.credentialsFetcher = new ContainerCredentialsFetcher(credentialsEndpointProvider);
    }

    @Override
    public AWSCredentials getCredentials() {
        return this.credentialsFetcher.getCredentials();
    }

    @Override
    public void refresh() {
        this.credentialsFetcher.refresh();
    }

    public Date getCredentialsExpiration() {
        return this.credentialsFetcher.getCredentialsExpiration();
    }

    private static Set<String> allowedHosts() {
        HashSet<String> hosts = new HashSet<String>();
        hosts.add("127.0.0.1");
        hosts.add("localhost");
        return Collections.unmodifiableSet(hosts);
    }

    static class FullUriCredentialsEndpointProvider
    extends CredentialsEndpointProvider {
        FullUriCredentialsEndpointProvider() {
        }

        @Override
        public URI getCredentialsEndpoint() {
            String fullUri = System.getenv(ContainerCredentialsProvider.CONTAINER_CREDENTIALS_FULL_URI);
            if (fullUri == null || fullUri.length() == 0) {
                throw new SdkClientException("The environment variable AWS_CONTAINER_CREDENTIALS_FULL_URI is empty");
            }
            URI uri = URI.create(fullUri);
            if (!ALLOWED_FULL_URI_HOSTS.contains(uri.getHost())) {
                throw new SdkClientException("The full URI (" + uri + ") contained withing environment variable " + ContainerCredentialsProvider.CONTAINER_CREDENTIALS_FULL_URI + " has an invalid host. Host can only be one of [" + CollectionUtils.join(ALLOWED_FULL_URI_HOSTS, ", ") + "]");
            }
            return uri;
        }

        @Override
        public Map<String, String> getHeaders() {
            if (System.getenv(ContainerCredentialsProvider.CONTAINER_AUTHORIZATION_TOKEN) != null) {
                return Collections.singletonMap("Authorization", System.getenv(ContainerCredentialsProvider.CONTAINER_AUTHORIZATION_TOKEN));
            }
            return new HashMap<String, String>();
        }
    }

    static class ECSCredentialsEndpointProvider
    extends CredentialsEndpointProvider {
        ECSCredentialsEndpointProvider() {
        }

        @Override
        public URI getCredentialsEndpoint() {
            String path = System.getenv(ContainerCredentialsProvider.ECS_CONTAINER_CREDENTIALS_PATH);
            if (path == null) {
                throw new SdkClientException("The environment variable AWS_CONTAINER_CREDENTIALS_RELATIVE_URI is empty");
            }
            return URI.create(ContainerCredentialsProvider.ECS_CREDENTIALS_ENDPOINT + path);
        }

        @Override
        public CredentialsEndpointRetryPolicy getRetryPolicy() {
            return ContainerCredentialsRetryPolicy.getInstance();
        }
    }
}

