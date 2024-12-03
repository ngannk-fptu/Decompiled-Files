/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.confluence.plugins.synchrony.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.io.IOUtils;

public enum SynchronyEnv {
    Host("synchrony.host"),
    JdbcUrl("synchrony.database.url"),
    JdbcUser("synchrony.database.username"),
    JdbcPassword("synchrony.database.password"),
    JwtPublicKey("jwt.public.key"),
    JwtPrivateKey("jwt.private.key"),
    ServiceUrl("synchrony.service.url"),
    Port("synchrony.port"),
    AlephPort("synchrony.cluster.base.port"),
    AlephBind("synchrony.cluster.bind"),
    ContextPath("synchrony.context.path"),
    HazelcastGroupName("cluster.group.name"),
    HazelcastPort("cluster.listen.port"),
    HazelcastInterfaces("cluster.interfaces"),
    ClusterAuthenticationSecret("cluster.authentication.secret"),
    ClusterAuthenticationEnabled("cluster.authentication.enabled"),
    ClusterImpl("synchrony.cluster.impl"),
    ClusterJoinType("cluster.join.type"),
    ClusterJoinMulticastGroup("cluster.join.multicast.group"),
    ClusterJoinMulticastPort("cluster.join.multicast.port"),
    ClusterJoinTCPIPMembers("cluster.join.tcpip.members"),
    ClusterJoinAwsAccess("cluster.join.aws.access.key"),
    ClusterJoinAwsSecret("cluster.join.aws.secret.key"),
    ClusterJoinAwsRegion("cluster.join.aws.region"),
    ClusterJoinAwsGroup("cluster.join.aws.security.group"),
    ClusterJoinAwsTagKey("cluster.join.aws.tag.key"),
    ClusterJoinAwsTagValue("cluster.join.aws.tag.value"),
    ClusterJoinAwsHeader("cluster.join.aws.host.header"),
    ClusterJoinAwsIam("cluster.join.aws.iam"),
    DefaultLogging("synchrony.log.to.file"),
    Memory("synchrony.memory.max"),
    StackSpace("synchrony.stack.space"),
    WatchPid("synchrony.watch.pid"),
    ExtractDirectory("synchrony.extract.dir"),
    WorkingDirectory("synchrony.working.dir"),
    FeatureAuthToken("feature-auth-token"),
    AuthTokens("auth-tokens");

    private final String envName;
    private static Properties defaultProperties;

    private SynchronyEnv(String envName) {
        this.envName = envName;
    }

    public String getEnvName() {
        return this.envName;
    }

    public String getDefaultValue() {
        return SynchronyEnv.getDefaultProperties().getProperty(this.envName, "");
    }

    public static Properties getDefaultProperties() {
        if (defaultProperties == null) {
            defaultProperties = SynchronyEnv.loadDefaultProperties();
        }
        Properties properties = new Properties();
        properties.putAll((Map<?, ?>)defaultProperties);
        return properties;
    }

    private static Properties loadDefaultProperties() {
        Properties env = new Properties();
        InputStream synchronyEnvDefaults = null;
        try {
            synchronyEnvDefaults = SynchronyEnv.class.getClassLoader().getResourceAsStream("env/synchrony.properties");
            env.load(synchronyEnvDefaults);
        }
        catch (IOException iOException) {
            IOUtils.closeQuietly(synchronyEnvDefaults);
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(synchronyEnvDefaults);
            throw throwable;
        }
        IOUtils.closeQuietly((InputStream)synchronyEnvDefaults);
        return env;
    }
}

