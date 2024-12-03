/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer;

import com.amazonaws.services.s3.model.S3ObjectSummary;

public interface KeyFilter {
    public static final KeyFilter INCLUDE_ALL = new KeyFilter(){

        @Override
        public boolean shouldInclude(S3ObjectSummary objectSummary) {
            return true;
        }
    };

    public boolean shouldInclude(S3ObjectSummary var1);
}

