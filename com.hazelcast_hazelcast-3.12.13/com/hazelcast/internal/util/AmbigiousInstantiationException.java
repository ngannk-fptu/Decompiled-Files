/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util;

import com.hazelcast.core.HazelcastException;

final class AmbigiousInstantiationException
extends HazelcastException {
    public AmbigiousInstantiationException(String message) {
        super(message);
    }
}

