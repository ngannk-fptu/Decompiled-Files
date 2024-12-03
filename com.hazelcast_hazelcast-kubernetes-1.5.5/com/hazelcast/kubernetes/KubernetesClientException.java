/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastException
 */
package com.hazelcast.kubernetes;

import com.hazelcast.core.HazelcastException;

class KubernetesClientException
extends HazelcastException {
    KubernetesClientException(String message) {
        super(message);
    }

    KubernetesClientException(String message, Throwable cause) {
        super(message, cause);
    }
}

