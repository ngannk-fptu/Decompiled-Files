/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.config.Config
 *  com.hazelcast.config.ConfigLoader
 *  com.hazelcast.config.XmlConfigBuilder
 *  com.hazelcast.core.Hazelcast
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.logging.ILogger
 *  com.hazelcast.logging.Logger
 *  com.hazelcast.util.StringUtil
 *  org.hibernate.cache.CacheException
 */
package com.hazelcast.hibernate.instance;

import com.hazelcast.config.Config;
import com.hazelcast.config.ConfigLoader;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.hibernate.CacheEnvironment;
import com.hazelcast.hibernate.instance.IHazelcastInstanceFactory;
import com.hazelcast.hibernate.instance.IHazelcastInstanceLoader;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.util.StringUtil;
import java.io.IOException;
import java.util.Properties;
import org.hibernate.cache.CacheException;

class HazelcastInstanceLoader
implements IHazelcastInstanceLoader {
    private static final ILogger LOGGER = Logger.getLogger(IHazelcastInstanceFactory.class);
    private HazelcastInstance instance;
    private Config config;
    private boolean shutDown;
    private String existingInstanceName;

    HazelcastInstanceLoader() {
    }

    @Override
    public void configure(Properties props) {
        String instanceName = CacheEnvironment.getInstanceName(props);
        if (!StringUtil.isNullOrEmptyAfterTrim((String)instanceName)) {
            LOGGER.info("Using existing HazelcastInstance [" + instanceName + "].");
            this.existingInstanceName = instanceName;
        } else {
            String configResourcePath = CacheEnvironment.getConfigFilePath(props);
            if (!StringUtil.isNullOrEmptyAfterTrim((String)configResourcePath)) {
                try {
                    this.config = ConfigLoader.load((String)configResourcePath);
                }
                catch (IOException e) {
                    LOGGER.warning("IOException: " + e.getMessage());
                }
                if (this.config == null) {
                    throw new CacheException("Could not find configuration file: " + configResourcePath);
                }
            } else {
                this.config = new XmlConfigBuilder().build();
            }
        }
        this.shutDown = CacheEnvironment.shutdownOnStop(props, instanceName == null);
    }

    @Override
    public HazelcastInstance loadInstance() throws CacheException {
        if (this.existingInstanceName != null) {
            this.instance = Hazelcast.getHazelcastInstanceByName((String)this.existingInstanceName);
            if (this.instance == null) {
                throw new CacheException("No instance with name [" + this.existingInstanceName + "] could be found.");
            }
        } else {
            this.instance = Hazelcast.newHazelcastInstance((Config)this.config);
        }
        return this.instance;
    }

    @Override
    public void unloadInstance() throws CacheException {
        if (this.instance == null) {
            return;
        }
        if (!this.shutDown) {
            LOGGER.warning("hibernate.cache.hazelcast.shutdown_on_session_factory_close property is set to 'false'. Leaving current HazelcastInstance active! (Warning: Do not disable Hazelcast hazelcast.shutdownhook.enabled property!)");
            return;
        }
        try {
            this.instance.getLifecycleService().shutdown();
            this.instance = null;
        }
        catch (Exception e) {
            throw new CacheException((Throwable)e);
        }
    }
}

