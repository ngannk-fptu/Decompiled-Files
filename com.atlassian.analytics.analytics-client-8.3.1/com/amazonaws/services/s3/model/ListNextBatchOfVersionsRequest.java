/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.VersionListing;
import java.io.Serializable;

public class ListNextBatchOfVersionsRequest
extends AmazonWebServiceRequest
implements Serializable {
    private VersionListing previousVersionListing;

    public ListNextBatchOfVersionsRequest(VersionListing previousVersionListing) {
        this.setPreviousVersionListing(previousVersionListing);
    }

    public VersionListing getPreviousVersionListing() {
        return this.previousVersionListing;
    }

    public void setPreviousVersionListing(VersionListing previousVersionListing) {
        if (previousVersionListing == null) {
            throw new IllegalArgumentException("The parameter previousVersionListing must be specified.");
        }
        this.previousVersionListing = previousVersionListing;
    }

    public ListNextBatchOfVersionsRequest withPreviousVersionListing(VersionListing previousVersionListing) {
        this.setPreviousVersionListing(previousVersionListing);
        return this;
    }

    public ListVersionsRequest toListVersionsRequest() {
        ListVersionsRequest result = (ListVersionsRequest)((AmazonWebServiceRequest)((AmazonWebServiceRequest)new ListVersionsRequest(this.previousVersionListing.getBucketName(), this.previousVersionListing.getPrefix(), this.previousVersionListing.getNextKeyMarker(), this.previousVersionListing.getNextVersionIdMarker(), this.previousVersionListing.getDelimiter(), this.previousVersionListing.getMaxKeys()).withEncodingType(this.previousVersionListing.getEncodingType()).withRequestCredentialsProvider(this.getRequestCredentialsProvider())).withGeneralProgressListener(this.getGeneralProgressListener())).withRequestMetricCollector(this.getRequestMetricCollector());
        Integer sdkClientExecutionTimeout = this.getSdkClientExecutionTimeout();
        if (sdkClientExecutionTimeout != null) {
            result.setSdkClientExecutionTimeout(sdkClientExecutionTimeout);
        }
        Integer sdkRequestTimeout = this.getSdkRequestTimeout();
        if (sdkClientExecutionTimeout != null) {
            result.setSdkRequestTimeout(sdkRequestTimeout);
        }
        return result;
    }
}

