/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.http;

import java.io.InputStream;
import software.amazon.awssdk.annotations.SdkPublicApi;

@FunctionalInterface
@SdkPublicApi
public interface ContentStreamProvider {
    public InputStream newStream();
}

