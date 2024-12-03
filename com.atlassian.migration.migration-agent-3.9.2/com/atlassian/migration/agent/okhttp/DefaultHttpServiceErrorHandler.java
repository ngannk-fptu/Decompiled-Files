/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  okhttp3.Response
 *  okhttp3.ResponseBody
 *  okio.Buffer
 */
package com.atlassian.migration.agent.okhttp;

import com.atlassian.migration.agent.okhttp.HttpServiceErrorHandler;
import com.atlassian.migration.agent.okhttp.HttpServiceException;
import com.atlassian.migration.agent.okhttp.IOHttpException;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.annotation.Nullable;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class DefaultHttpServiceErrorHandler
implements HttpServiceErrorHandler {
    @Override
    public void accept(Response response) {
        int statusCode = response.code();
        ResponseBody body = response.body();
        String serverMessage = this.getBodyString(body);
        String message = statusCode >= 400 && statusCode < 500 ? "Bad request." : "Internal error in downstream service.";
        throw new HttpServiceException(String.format("%s Status code: %d, message: %s, headers: %s", message, statusCode, serverMessage, response.headers()), statusCode);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String getBodyString(@Nullable ResponseBody body) {
        String rtn = "";
        if (body != null) {
            try {
                rtn = body.string();
            }
            catch (IllegalStateException illegalStateException) {
                try (Buffer buffer = null;){
                    buffer = body.source().buffer().clone();
                    rtn = buffer.readString(Charset.defaultCharset());
                }
            }
            catch (IOException e) {
                throw new IOHttpException("Failed to read response body", e);
            }
        }
        return rtn;
    }
}

