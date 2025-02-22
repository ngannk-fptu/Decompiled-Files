/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

enum ConfigSections {
    HAZELCAST("hazelcast", false),
    INSTANCE_NAME("instance-name", false),
    IMPORT("import", true),
    CONFIG_REPLACERS("config-replacers", false),
    GROUP("group", false),
    LICENSE_KEY("license-key", false),
    MANAGEMENT_CENTER("management-center", false),
    PROPERTIES("properties", false),
    WAN_REPLICATION("wan-replication", true),
    NETWORK("network", false),
    PARTITION_GROUP("partition-group", false),
    EXECUTOR_SERVICE("executor-service", true),
    DURABLE_EXECUTOR_SERVICE("durable-executor-service", true),
    SCHEDULED_EXECUTOR_SERVICE("scheduled-executor-service", true),
    EVENT_JOURNAL("event-journal", true),
    MERKLE_TREE("merkle-tree", true),
    QUEUE("queue", true),
    MAP("map", true),
    CACHE("cache", true),
    MULTIMAP("multimap", true),
    REPLICATED_MAP("replicatedmap", true),
    LIST("list", true),
    SET("set", true),
    TOPIC("topic", true),
    RELIABLE_TOPIC("reliable-topic", true),
    JOB_TRACKER("jobtracker", true),
    SEMAPHORE("semaphore", true),
    LOCK("lock", true),
    RINGBUFFER("ringbuffer", true),
    ATOMIC_LONG("atomic-long", true),
    ATOMIC_REFERENCE("atomic-reference", true),
    COUNT_DOWN_LATCH("count-down-latch", true),
    LISTENERS("listeners", false),
    SERIALIZATION("serialization", false),
    SERVICES("services", false),
    SECURITY("security", false),
    MEMBER_ATTRIBUTES("member-attributes", false),
    NATIVE_MEMORY("native-memory", false),
    QUORUM("quorum", true),
    LITE_MEMBER("lite-member", false),
    HOT_RESTART_PERSISTENCE("hot-restart-persistence", false),
    USER_CODE_DEPLOYMENT("user-code-deployment", false),
    CARDINALITY_ESTIMATOR("cardinality-estimator", true),
    RELIABLE_ID_GENERATOR("reliable-id-generator", true),
    FLAKE_ID_GENERATOR("flake-id-generator", true),
    CRDT_REPLICATION("crdt-replication", false),
    PN_COUNTER("pn-counter", true),
    ADVANCED_NETWORK("advanced-network", false),
    CP_SUBSYSTEM("cp-subsystem", false);

    final String name;
    final boolean multipleOccurrence;

    private ConfigSections(String name, boolean multipleOccurrence) {
        this.name = name;
        this.multipleOccurrence = multipleOccurrence;
    }

    public static boolean canOccurMultipleTimes(String name) {
        for (ConfigSections element : ConfigSections.values()) {
            if (!name.equals(element.name)) continue;
            return element.multipleOccurrence;
        }
        return false;
    }

    public boolean isEqual(String name) {
        return this.name.equals(name);
    }
}

