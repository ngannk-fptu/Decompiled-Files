/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.exception;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.spi.exception.RetryableException;

public class MetadataRaftGroupInitInProgressException
extends HazelcastException
implements RetryableException {
    private static final long serialVersionUID = -587586143908312910L;
}

