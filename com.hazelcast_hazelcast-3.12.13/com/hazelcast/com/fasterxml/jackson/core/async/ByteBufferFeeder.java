/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.com.fasterxml.jackson.core.async;

import com.hazelcast.com.fasterxml.jackson.core.async.NonBlockingInputFeeder;
import java.io.IOException;
import java.nio.ByteBuffer;

public interface ByteBufferFeeder
extends NonBlockingInputFeeder {
    public void feedInput(ByteBuffer var1) throws IOException;
}

