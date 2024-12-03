/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.writer.api;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.business.insights.core.writer.exception.MapWriterWriteException;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

@ExperimentalApi
public interface MapWriter
extends AutoCloseable {
    public boolean writeHeaders(@Nonnull String[] var1) throws MapWriterWriteException;

    public boolean write(@Nonnull Map<String, Object> var1) throws MapWriterWriteException;

    public int write(@Nonnull List<Map<String, Object>> var1) throws MapWriterWriteException;
}

