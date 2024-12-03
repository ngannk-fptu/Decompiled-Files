/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security.permission;

import com.hazelcast.security.permission.AtomicLongPermission;
import com.hazelcast.security.permission.AtomicReferencePermission;
import com.hazelcast.security.permission.CachePermission;
import com.hazelcast.security.permission.CardinalityEstimatorPermission;
import com.hazelcast.security.permission.CountDownLatchPermission;
import com.hazelcast.security.permission.DurableExecutorServicePermission;
import com.hazelcast.security.permission.ExecutorServicePermission;
import com.hazelcast.security.permission.FlakeIdGeneratorPermission;
import com.hazelcast.security.permission.ListPermission;
import com.hazelcast.security.permission.LockPermission;
import com.hazelcast.security.permission.MapPermission;
import com.hazelcast.security.permission.MapReducePermission;
import com.hazelcast.security.permission.MultiMapPermission;
import com.hazelcast.security.permission.PNCounterPermission;
import com.hazelcast.security.permission.QueuePermission;
import com.hazelcast.security.permission.ReliableTopicPermission;
import com.hazelcast.security.permission.ReplicatedMapPermission;
import com.hazelcast.security.permission.RingBufferPermission;
import com.hazelcast.security.permission.SemaphorePermission;
import com.hazelcast.security.permission.SetPermission;
import com.hazelcast.security.permission.TopicPermission;
import com.hazelcast.security.permission.UserCodeDeploymentPermission;
import java.security.Permission;
import java.util.HashMap;
import java.util.Map;

public final class ActionConstants {
    public static final String ACTION_ALL = "all";
    public static final String ACTION_CREATE = "create";
    public static final String ACTION_DESTROY = "destroy";
    public static final String ACTION_MODIFY = "modify";
    public static final String ACTION_READ = "read";
    public static final String ACTION_REMOVE = "remove";
    public static final String ACTION_LOCK = "lock";
    public static final String ACTION_LISTEN = "listen";
    public static final String ACTION_RELEASE = "release";
    public static final String ACTION_ACQUIRE = "acquire";
    public static final String ACTION_PUT = "put";
    public static final String ACTION_ADD = "add";
    public static final String ACTION_INDEX = "index";
    public static final String ACTION_INTERCEPT = "intercept";
    public static final String ACTION_PUBLISH = "publish";
    public static final String ACTION_AGGREGATE = "aggregate";
    public static final String ACTION_PROJECTION = "projection";
    public static final String ACTION_USER_CODE_DEPLOY = "deploy";
    public static final String LISTENER_INSTANCE = "instance";
    public static final String LISTENER_MEMBER = "member";
    public static final String LISTENER_MIGRATION = "migration";
    private static final Map<String, PermissionFactory> PERMISSION_FACTORY_MAP = new HashMap<String, PermissionFactory>();

    private ActionConstants() {
    }

    public static Permission getPermission(String name, String serviceName, String ... actions) {
        PermissionFactory permissionFactory = PERMISSION_FACTORY_MAP.get(serviceName);
        if (permissionFactory == null) {
            throw new IllegalArgumentException("No permissions found for service: " + serviceName);
        }
        return permissionFactory.create(name, actions);
    }

    static {
        PERMISSION_FACTORY_MAP.put("hz:impl:queueService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new QueuePermission(name, actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("hz:impl:mapService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new MapPermission(name, actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("hz:impl:multiMapService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new MultiMapPermission(name, actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("hz:impl:listService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new ListPermission(name, actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("hz:impl:setService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new SetPermission(name, actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("hz:impl:atomicLongService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new AtomicLongPermission(name, actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("hz:raft:atomicLongService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new AtomicLongPermission(name, actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("hz:impl:countDownLatchService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new CountDownLatchPermission(name, actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("hz:raft:countDownLatchService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new CountDownLatchPermission(name, actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("hz:impl:semaphoreService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new SemaphorePermission(name, actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("hz:raft:semaphoreService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new SemaphorePermission(name, actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("hz:impl:topicService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new TopicPermission(name, actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("hz:impl:lockService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new LockPermission(name, actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("hz:raft:lockService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new LockPermission(name, actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("hz:impl:executorService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new ExecutorServicePermission(name, actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("hz:impl:idGeneratorService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new AtomicLongPermission("hz:atomic:idGenerator:" + name, actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("hz:impl:flakeIdGeneratorService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new FlakeIdGeneratorPermission(name, actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("hz:impl:mapReduceService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new MapReducePermission(name, actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("hz:impl:replicatedMapService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new ReplicatedMapPermission(name, actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("hz:impl:atomicReferenceService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new AtomicReferencePermission(name, actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("hz:raft:atomicRefService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new AtomicReferencePermission(name, actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("hz:impl:cacheService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new CachePermission(name, actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("hz:impl:ringbufferService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new RingBufferPermission(name, actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("hz:impl:durableExecutorService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new DurableExecutorServicePermission(name, actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("hz:impl:cardinalityEstimatorService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new CardinalityEstimatorPermission(name, actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("user-code-deployment-service", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new UserCodeDeploymentPermission(actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("hz:impl:PNCounterService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new PNCounterPermission(name, actions);
            }
        });
        PERMISSION_FACTORY_MAP.put("hz:impl:reliableTopicService", new PermissionFactory(){

            @Override
            public Permission create(String name, String ... actions) {
                return new ReliableTopicPermission(name, actions);
            }
        });
    }

    private static interface PermissionFactory {
        public Permission create(String var1, String ... var2);
    }
}

