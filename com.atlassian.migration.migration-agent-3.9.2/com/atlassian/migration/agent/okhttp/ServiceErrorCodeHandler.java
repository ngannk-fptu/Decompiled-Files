/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  okhttp3.Response
 *  okhttp3.ResponseBody
 */
package com.atlassian.migration.agent.okhttp;

import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.okhttp.DefaultHttpServiceErrorHandler;
import com.atlassian.migration.agent.okhttp.ErrorResponse;
import com.atlassian.migration.agent.okhttp.HttpServiceErrorHandler;
import com.atlassian.migration.agent.okhttp.HttpServiceException;
import com.atlassian.migration.agent.okhttp.MediaTypes;
import java.io.IOException;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ServiceErrorCodeHandler
implements HttpServiceErrorHandler {
    private static final HttpServiceErrorHandler FALLBACK_ERROR_HANDLER = new DefaultHttpServiceErrorHandler();

    @Override
    public void accept(Response response) {
        ResponseBody body = response.body();
        if (body == null || !MediaTypes.APPLICATION_JSON_TYPE.equals((Object)body.contentType())) {
            FALLBACK_ERROR_HANDLER.accept(response);
            return;
        }
        try {
            ErrorResponse errorResponse = (ErrorResponse)Jsons.OBJECT_MAPPER.readValue(body.charStream(), ErrorResponse.class);
            this.errorResponseToException(errorResponse, response.code());
        }
        catch (IOException e) {
            FALLBACK_ERROR_HANDLER.accept(response);
        }
    }

    private void errorResponseToException(ErrorResponse errorResponse, int statusCode) {
        throw new HttpServiceException(errorResponse.message, statusCode, errorResponse.code);
    }
}

