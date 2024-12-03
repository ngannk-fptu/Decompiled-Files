/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.thirdparty.jackson.core.async;

import java.io.IOException;
import java.nio.ByteBuffer;
import software.amazon.awssdk.thirdparty.jackson.core.async.NonBlockingInputFeeder;

public interface ByteBufferFeeder
extends NonBlockingInputFeeder {
    public void feedInput(ByteBuffer var1) throws IOException;
}

