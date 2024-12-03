/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.regions;

import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.RegionImpl;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.regions.Regions;
import com.amazonaws.util.ValidationUtils;
import java.lang.reflect.Constructor;
import java.util.Collection;

public class Region {
    private final RegionImpl regionImpl;

    public Region(RegionImpl regionImpl) {
        ValidationUtils.assertNotNull(regionImpl, "region implementation");
        this.regionImpl = regionImpl;
    }

    public static Region getRegion(Regions region) {
        return RegionUtils.getRegion(region.getName());
    }

    public String getName() {
        return this.regionImpl.getName();
    }

    public String getDomain() {
        return this.regionImpl.getDomain();
    }

    public String getPartition() {
        return this.regionImpl.getPartition();
    }

    public String getServiceEndpoint(String endpointPrefix) {
        return this.regionImpl.getServiceEndpoint(endpointPrefix);
    }

    public boolean isServiceSupported(String serviceName) {
        return this.regionImpl.isServiceSupported(serviceName);
    }

    public boolean hasHttpsEndpoint(String serviceName) {
        return this.regionImpl.hasHttpsEndpoint(serviceName);
    }

    public boolean hasHttpEndpoint(String serviceName) {
        return this.regionImpl.hasHttpEndpoint(serviceName);
    }

    public Collection<String> getAvailableEndpoints() {
        return this.regionImpl.getAvailableEndpoints();
    }

    @Deprecated
    public <T extends AmazonWebServiceClient> T createClient(Class<T> serviceClass, AWSCredentialsProvider credentials, ClientConfiguration config) {
        try {
            AmazonWebServiceClient client;
            if (credentials == null && config == null) {
                Constructor<T> constructor = serviceClass.getConstructor(new Class[0]);
                client = (AmazonWebServiceClient)constructor.newInstance(new Object[0]);
            } else if (credentials == null) {
                Constructor<T> constructor = serviceClass.getConstructor(ClientConfiguration.class);
                client = (AmazonWebServiceClient)constructor.newInstance(config);
            } else if (config == null) {
                Constructor<T> constructor = serviceClass.getConstructor(AWSCredentialsProvider.class);
                client = (AmazonWebServiceClient)constructor.newInstance(credentials);
            } else {
                Constructor<T> constructor = serviceClass.getConstructor(AWSCredentialsProvider.class, ClientConfiguration.class);
                client = (AmazonWebServiceClient)constructor.newInstance(credentials, config);
            }
            client.setRegion(this);
            return (T)client;
        }
        catch (Exception e) {
            throw new RuntimeException("Couldn't instantiate instance of " + serviceClass, e);
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Region)) {
            return false;
        }
        Region region = (Region)obj;
        return this.getName().equals(region.getName());
    }

    public int hashCode() {
        return this.getName().hashCode();
    }

    public String toString() {
        return this.getName();
    }
}

