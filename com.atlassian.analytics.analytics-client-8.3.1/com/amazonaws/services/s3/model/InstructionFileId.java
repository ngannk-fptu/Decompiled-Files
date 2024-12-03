/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.annotation.Immutable;
import com.amazonaws.services.s3.model.S3ObjectId;

@Immutable
public final class InstructionFileId
extends S3ObjectId {
    public static final String DEFAULT_INSTRUCTION_FILE_SUFFIX = "instruction";
    @Deprecated
    public static final String DEFAULT_INSTURCTION_FILE_SUFFIX = "instruction";
    public static final String DOT = ".";

    InstructionFileId(String bucket, String key, String versionId) {
        super(bucket, key, versionId);
    }

    @Override
    public InstructionFileId instructionFileId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public InstructionFileId instructionFileId(String suffix) {
        throw new UnsupportedOperationException();
    }
}

