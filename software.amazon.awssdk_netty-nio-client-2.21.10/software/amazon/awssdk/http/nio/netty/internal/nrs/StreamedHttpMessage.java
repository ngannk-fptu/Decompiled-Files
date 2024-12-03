/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.handler.codec.http.HttpContent
 *  io.netty.handler.codec.http.HttpMessage
 *  org.reactivestreams.Publisher
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal.nrs;

import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMessage;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public interface StreamedHttpMessage
extends HttpMessage,
Publisher<HttpContent> {
}

