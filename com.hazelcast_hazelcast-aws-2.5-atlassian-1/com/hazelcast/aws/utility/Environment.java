/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.aws.utility;

public class Environment {
    public String getEnvVar(String name) {
        return System.getenv(name);
    }
}

