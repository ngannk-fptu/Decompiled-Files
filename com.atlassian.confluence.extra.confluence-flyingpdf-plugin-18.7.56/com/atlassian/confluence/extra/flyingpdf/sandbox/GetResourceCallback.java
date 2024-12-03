/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.importexport.resource.DownloadResourceManager
 *  com.atlassian.confluence.importexport.resource.DownloadResourceNotFoundException
 *  com.atlassian.confluence.importexport.resource.DownloadResourceReader
 *  com.atlassian.confluence.importexport.resource.UnauthorizedDownloadResourceException
 *  com.atlassian.confluence.util.sandbox.SandboxCallback
 *  com.atlassian.confluence.util.sandbox.SandboxCallbackContext
 *  com.atlassian.confluence.util.sandbox.SandboxSerializer
 *  com.atlassian.renderer.util.UrlUtil
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.flyingpdf.sandbox;

import com.atlassian.confluence.extra.flyingpdf.sandbox.GetResourceCallbackRequest;
import com.atlassian.confluence.extra.flyingpdf.sandbox.GetResourceCallbackResponse;
import com.atlassian.confluence.importexport.resource.DownloadResourceManager;
import com.atlassian.confluence.importexport.resource.DownloadResourceNotFoundException;
import com.atlassian.confluence.importexport.resource.DownloadResourceReader;
import com.atlassian.confluence.importexport.resource.UnauthorizedDownloadResourceException;
import com.atlassian.confluence.util.sandbox.SandboxCallback;
import com.atlassian.confluence.util.sandbox.SandboxCallbackContext;
import com.atlassian.confluence.util.sandbox.SandboxSerializer;
import com.atlassian.renderer.util.UrlUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetResourceCallback
implements SandboxCallback<GetResourceCallbackRequest, GetResourceCallbackResponse> {
    private static final Logger log = LoggerFactory.getLogger(GetResourceCallback.class);

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public GetResourceCallbackResponse apply(SandboxCallbackContext context, GetResourceCallbackRequest request) {
        Optional downloadResourceManager = context.get(DownloadResourceManager.class);
        if (!downloadResourceManager.isPresent()) {
            log.error("DownloadResourceManager is not registered in callback context");
            return new GetResourceCallbackResponse(new byte[0]);
        }
        String decodedUri = request.getUri();
        String strippedUri = this.stripQueryString(decodedUri);
        try {
            DownloadResourceReader resourceReader = ((DownloadResourceManager)downloadResourceManager.get()).getResourceReader(request.getUsername(), strippedUri, UrlUtil.getQueryParameters((String)decodedUri));
            try (InputStream inputStream = resourceReader.getStreamForReading();){
                if (inputStream == null) {
                    log.warn("There was a problem with fetching attachment for {}", (Object)request.getUri());
                    GetResourceCallbackResponse getResourceCallbackResponse2 = new GetResourceCallbackResponse(new byte[0]);
                    return getResourceCallbackResponse2;
                }
                GetResourceCallbackResponse getResourceCallbackResponse = new GetResourceCallbackResponse(IOUtils.toByteArray((InputStream)inputStream));
                return getResourceCallbackResponse;
            }
        }
        catch (DownloadResourceNotFoundException | UnauthorizedDownloadResourceException | IOException e) {
            return new GetResourceCallbackResponse(new byte[0]);
        }
    }

    public SandboxSerializer<GetResourceCallbackRequest> inputSerializer() {
        return GetResourceCallbackRequest.serializer();
    }

    public SandboxSerializer<GetResourceCallbackResponse> outputSerializer() {
        return GetResourceCallbackResponse.serializer();
    }

    private String stripQueryString(String uri) {
        int queryIndex = uri.indexOf(63);
        if (queryIndex > 0) {
            uri = uri.substring(0, queryIndex);
        }
        return uri;
    }
}

