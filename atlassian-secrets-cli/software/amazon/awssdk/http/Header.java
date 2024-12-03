/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http;

import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public final class Header {
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_MD5 = "Content-MD5";
    public static final String TRANSFER_ENCODING = "Transfer-Encoding";
    public static final String CHUNKED = "chunked";
    public static final String HOST = "Host";
    public static final String CONNECTION = "Connection";
    public static final String KEEP_ALIVE_VALUE = "keep-alive";
    public static final String ACCEPT = "Accept";

    private Header() {
    }
}

