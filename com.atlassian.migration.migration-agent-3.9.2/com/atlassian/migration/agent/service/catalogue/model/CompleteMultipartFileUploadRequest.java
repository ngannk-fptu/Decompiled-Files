/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.catalogue.model;

import com.atlassian.migration.agent.service.catalogue.model.UploadFilePartS3Response;
import java.util.List;
import lombok.Generated;

public class CompleteMultipartFileUploadRequest {
    private final List<UploadFilePartS3Response> parts;

    @Generated
    public CompleteMultipartFileUploadRequest(List<UploadFilePartS3Response> parts) {
        this.parts = parts;
    }

    @Generated
    public List<UploadFilePartS3Response> getParts() {
        return this.parts;
    }
}

