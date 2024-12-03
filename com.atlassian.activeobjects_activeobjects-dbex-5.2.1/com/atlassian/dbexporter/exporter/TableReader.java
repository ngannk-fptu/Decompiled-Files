/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter.exporter;

import com.atlassian.dbexporter.DatabaseInformation;
import com.atlassian.dbexporter.EntityNameProcessor;
import com.atlassian.dbexporter.Table;

public interface TableReader {
    public Iterable<Table> read(DatabaseInformation var1, EntityNameProcessor var2);
}

