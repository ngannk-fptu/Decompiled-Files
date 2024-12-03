/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal.nrs;

import io.netty.handler.codec.http.HttpRequest;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.nrs.StreamedHttpMessage;

@SdkInternalApi
public interface StreamedHttpRequest
extends HttpRequest,
StreamedHttpMessage {
}

