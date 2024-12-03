/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.importexport.resource;

import com.atlassian.confluence.importexport.resource.DownloadResourceReader;
import com.atlassian.confluence.web.rangerequest.RangeRequest;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface PartialDownloadResourceReader
extends DownloadResourceReader {
    public @NonNull RangeRequest getRequestRange();
}

