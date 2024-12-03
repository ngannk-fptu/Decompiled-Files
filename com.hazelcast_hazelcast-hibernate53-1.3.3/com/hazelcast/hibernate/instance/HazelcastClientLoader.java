/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.client.HazelcastClient
 *  com.hazelcast.client.config.ClientConfig
 *  com.hazelcast.client.config.XmlClientConfigBuilder
 *  com.hazelcast.core.HazelcastException
 *  com.hazelcast.core.HazelcastInstance
 *  org.hibernate.cache.CacheException
 *  org.hibernate.internal.util.config.ConfigurationHelper
 */
package com.hazelcast.hibernate.instance;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.hibernate.CacheEnvironment;
import com.hazelcast.hibernate.instance.IHazelcastInstanceLoader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import org.hibernate.cache.CacheException;
import org.hibernate.internal.util.config.ConfigurationHelper;

class HazelcastClientLoader
implements IHazelcastInstanceLoader {
    private static final int CONNECTION_ATTEMPT_LIMIT = 10;
    private HazelcastInstance client;
    private ClientConfig clientConfig;
    private String instanceName;

    HazelcastClientLoader() {
    }

    @Override
    public void configure(Properties props) {
        this.instanceName = ConfigurationHelper.getString((String)"hibernate.cache.hazelcast.native_client_instance_name", (Map)props, null);
        if (this.instanceName != null) {
            return;
        }
        String address = ConfigurationHelper.getString((String)"hibernate.cache.hazelcast.native_client_address", (Map)props, null);
        String group = ConfigurationHelper.getString((String)"hibernate.cache.hazelcast.native_client_group", (Map)props, null);
        String pass = ConfigurationHelper.getString((String)"hibernate.cache.hazelcast.native_client_password", (Map)props, null);
        String configResourcePath = CacheEnvironment.getConfigFilePath(props);
        if (configResourcePath != null) {
            try {
                this.clientConfig = new XmlClientConfigBuilder(configResourcePath).build();
            }
            catch (IOException e) {
                throw new HazelcastException("Could not load client configuration: " + configResourcePath, (Throwable)e);
            }
        } else {
            this.clientConfig = new ClientConfig();
        }
        if (group != null) {
            this.clientConfig.getGroupConfig().setName(group);
        }
        if (pass != null) {
            this.clientConfig.getGroupConfig().setPassword(pass);
        }
        if (address != null) {
            this.clientConfig.getNetworkConfig().addAddress(new String[]{address});
        }
        this.clientConfig.getNetworkConfig().setSmartRouting(true);
        this.clientConfig.getNetworkConfig().setRedoOperation(true);
        this.clientConfig.getNetworkConfig().setConnectionAttemptLimit(10);
    }

    @Override
    public HazelcastInstance loadInstance() throws CacheException {
        if (this.instanceName != null) {
            this.client = HazelcastClient.getHazelcastClientByName((String)this.instanceName);
            if (this.client == null) {
                throw new CacheException("No client with name [" + this.instanceName + "] could be found.");
            }
        } else {
            this.client = HazelcastClient.newHazelcastClient((ClientConfig)this.clientConfig);
        }
        return this.client;
    }

    @Override
    public void unloadInstance() throws CacheException {
        if (this.client == null) {
            return;
        }
        try {
            this.client.getLifecycleService().shutdown();
            this.client = null;
        }
        catch (Exception e) {
            throw new CacheException((Throwable)e);
        }
    }
}

