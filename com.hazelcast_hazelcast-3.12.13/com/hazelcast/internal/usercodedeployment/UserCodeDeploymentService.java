/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.usercodedeployment;

import com.hazelcast.config.UserCodeDeploymentConfig;
import com.hazelcast.core.Member;
import com.hazelcast.internal.usercodedeployment.impl.ClassData;
import com.hazelcast.internal.usercodedeployment.impl.ClassDataProvider;
import com.hazelcast.internal.usercodedeployment.impl.ClassLocator;
import com.hazelcast.internal.usercodedeployment.impl.ClassSource;
import com.hazelcast.internal.usercodedeployment.impl.filter.ClassNameFilterParser;
import com.hazelcast.internal.usercodedeployment.impl.filter.MemberProviderFilterParser;
import com.hazelcast.internal.util.filter.Filter;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.NodeEngine;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public final class UserCodeDeploymentService
implements ManagedService {
    public static final String SERVICE_NAME = "user-code-deployment-service";
    private volatile boolean enabled;
    private ClassDataProvider provider;
    private ClassLocator locator;

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
        UserCodeDeploymentConfig config = nodeEngine.getConfig().getUserCodeDeploymentConfig();
        if (!config.isEnabled()) {
            return;
        }
        ClassLoader parent = nodeEngine.getConfigClassLoader().getParent();
        Filter<String> classNameFilter = ClassNameFilterParser.parseClassNameFilters(config);
        Filter<Member> memberFilter = MemberProviderFilterParser.parseMemberFilter(config.getProviderFilter());
        ConcurrentHashMap<String, ClassSource> classMap = new ConcurrentHashMap<String, ClassSource>();
        ConcurrentHashMap<String, ClassSource> clientClassMap = new ConcurrentHashMap<String, ClassSource>();
        UserCodeDeploymentConfig.ProviderMode providerMode = config.getProviderMode();
        ILogger providerLogger = nodeEngine.getLogger(ClassDataProvider.class);
        this.provider = new ClassDataProvider(providerMode, parent, classMap, clientClassMap, providerLogger);
        UserCodeDeploymentConfig.ClassCacheMode classCacheMode = config.getClassCacheMode();
        this.locator = new ClassLocator(classMap, clientClassMap, parent, classNameFilter, memberFilter, classCacheMode, nodeEngine);
        this.enabled = config.isEnabled();
    }

    public void defineClasses(List<Map.Entry<String, byte[]>> classDefinitions) {
        if (!this.enabled) {
            throw new IllegalStateException("User Code Deployment is not enabled.");
        }
        this.locator.defineClassesFromClient(classDefinitions);
    }

    public ClassData getClassDataOrNull(String className) {
        if (!this.enabled) {
            return null;
        }
        return this.provider.getClassDataOrNull(className);
    }

    public Class<?> handleClassNotFoundException(String name) throws ClassNotFoundException {
        if (!this.enabled) {
            throw new ClassNotFoundException("User Code Deployment is not enabled. Cannot find class " + name);
        }
        return this.locator.handleClassNotFoundException(name);
    }

    public Class<?> findLoadedClass(String name) {
        if (!this.enabled) {
            return null;
        }
        return this.locator.findLoadedClass(name);
    }

    @Override
    public void reset() {
    }

    @Override
    public void shutdown(boolean terminate) {
    }
}

