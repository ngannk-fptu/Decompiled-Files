/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.atlassian.user.configuration;

import com.atlassian.user.configuration.CacheConfiguration;
import com.atlassian.user.configuration.ConfigurationException;
import com.atlassian.user.configuration.RepositoryAccessor;
import com.atlassian.user.configuration.RepositoryConfiguration;
import com.atlassian.user.configuration.RepositoryProcessor;
import com.atlassian.user.repository.RepositoryIdentifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DefaultRepositoryConfiguration
implements RepositoryConfiguration {
    protected final Logger logger = Logger.getLogger(this.getClass());
    private final RepositoryIdentifier identifier;
    private final RepositoryProcessor processor;
    private final Map<String, Object> components;
    private final Map componentClassNames;
    private CacheConfiguration cacheConfiguration;

    public DefaultRepositoryConfiguration(RepositoryIdentifier identifier, RepositoryProcessor processor, Map<String, String> components, Map componentClassNames) {
        this.identifier = identifier;
        this.processor = processor;
        this.components = new HashMap<String, String>(components);
        this.componentClassNames = componentClassNames;
    }

    @Override
    public void addComponent(String componentName, Object component) {
        if (this.hasComponent(componentName)) {
            this.logger.info((Object)("Overwriting existing component with name [" + componentName + "]"));
        }
        this.components.put(componentName, component);
    }

    @Override
    public Object getComponent(String componentName) {
        return this.components.get(componentName);
    }

    @Override
    public String getStringComponent(String componentName) {
        return (String)this.getComponent(componentName);
    }

    @Override
    public boolean hasComponent(String componentName) {
        return this.components.containsKey(componentName) && this.components.get(componentName) != null;
    }

    @Override
    public String getComponentClassName(String componentName) {
        return (String)this.componentClassNames.get(componentName);
    }

    @Override
    public RepositoryAccessor configure() throws ConfigurationException {
        return this.processor.process(this);
    }

    @Override
    public RepositoryIdentifier getIdentifier() {
        return this.identifier;
    }

    @Override
    public boolean hasClassForComponent(String componentName) {
        return this.componentClassNames.containsKey(componentName);
    }

    @Override
    public Set getComponentNames() {
        return Collections.unmodifiableSet(this.components.keySet());
    }

    @Override
    public void setCacheConfiguration(CacheConfiguration cacheConfiguration) {
        this.cacheConfiguration = cacheConfiguration;
    }

    @Override
    public boolean isCachingEnabled() {
        return this.cacheConfiguration != null;
    }

    @Override
    public CacheConfiguration getCacheConfiguration() {
        return this.cacheConfiguration;
    }
}

