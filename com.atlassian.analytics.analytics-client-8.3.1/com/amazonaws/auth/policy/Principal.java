/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth.policy;

public class Principal {
    public static final Principal AllUsers = new Principal("AWS", "*");
    public static final Principal AllServices = new Principal("Service", "*");
    public static final Principal AllWebProviders = new Principal("Federated", "*");
    public static final Principal All = new Principal("*", "*");
    private final String id;
    private final String provider;

    public Principal(Services service) {
        if (service == null) {
            throw new IllegalArgumentException("Null AWS service name specified");
        }
        this.id = service.getServiceId();
        this.provider = "Service";
    }

    public Principal(String accountId) {
        this("AWS", accountId);
        if (accountId == null) {
            throw new IllegalArgumentException("Null AWS account ID specified");
        }
    }

    public Principal(String provider, String id) {
        this(provider, id, provider.equals("AWS"));
    }

    public Principal(String provider, String id, boolean stripHyphen) {
        this.provider = provider;
        this.id = stripHyphen ? id.replace("-", "") : id;
    }

    public Principal(WebIdentityProviders webIdentityProvider) {
        if (webIdentityProvider == null) {
            throw new IllegalArgumentException("Null web identity provider specified");
        }
        this.id = webIdentityProvider.getWebIdentityProvider();
        this.provider = "Federated";
    }

    public String getProvider() {
        return this.provider;
    }

    public String getId() {
        return this.id;
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + this.provider.hashCode();
        hashCode = 31 * hashCode + this.id.hashCode();
        return hashCode;
    }

    public boolean equals(Object principal) {
        if (this == principal) {
            return true;
        }
        if (principal == null) {
            return false;
        }
        if (!(principal instanceof Principal)) {
            return false;
        }
        Principal other = (Principal)principal;
        return this.getProvider().equals(other.getProvider()) && this.getId().equals(other.getId());
    }

    public static enum WebIdentityProviders {
        Facebook("graph.facebook.com"),
        Google("accounts.google.com"),
        Amazon("www.amazon.com"),
        AllProviders("*");

        private String webIdentityProvider;

        private WebIdentityProviders(String webIdentityProvider) {
            this.webIdentityProvider = webIdentityProvider;
        }

        public String getWebIdentityProvider() {
            return this.webIdentityProvider;
        }

        public static WebIdentityProviders fromString(String webIdentityProvider) {
            if (webIdentityProvider != null) {
                for (WebIdentityProviders provider : WebIdentityProviders.values()) {
                    if (!provider.getWebIdentityProvider().equalsIgnoreCase(webIdentityProvider)) continue;
                    return provider;
                }
            }
            return null;
        }
    }

    public static enum Services {
        AmazonApiGateway("apigateway.amazonaws.com"),
        AWSDataPipeline("datapipeline.amazonaws.com"),
        AmazonElasticTranscoder("elastictranscoder.amazonaws.com"),
        AmazonEC2("ec2.amazonaws.com"),
        AWSOpsWorks("opsworks.amazonaws.com"),
        AWSCloudHSM("cloudhsm.amazonaws.com"),
        AllServices("*");

        private String serviceId;

        private Services(String serviceId) {
            this.serviceId = serviceId;
        }

        public String getServiceId() {
            return this.serviceId;
        }

        public static Services fromString(String serviceId) {
            if (serviceId != null) {
                for (Services s : Services.values()) {
                    if (!s.getServiceId().equalsIgnoreCase(serviceId)) continue;
                    return s;
                }
            }
            return null;
        }
    }
}

