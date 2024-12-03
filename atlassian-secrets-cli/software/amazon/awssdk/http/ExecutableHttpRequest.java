/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http;

import java.io.IOException;
import java.util.concurrent.Callable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.Abortable;
import software.amazon.awssdk.http.HttpExecuteResponse;

@SdkPublicApi
public interface ExecutableHttpRequest
extends Callable<HttpExecuteResponse>,
Abortable {
    @Override
    public HttpExecuteResponse call() throws IOException;
}

