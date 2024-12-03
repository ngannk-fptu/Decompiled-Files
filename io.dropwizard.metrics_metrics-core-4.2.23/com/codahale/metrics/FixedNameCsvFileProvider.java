/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.CsvFileProvider;
import java.io.File;

public class FixedNameCsvFileProvider
implements CsvFileProvider {
    @Override
    public File getFile(File directory, String metricName) {
        return new File(directory, this.sanitize(metricName) + ".csv");
    }

    protected String sanitize(String metricName) {
        return metricName.replaceFirst("^/", "").replaceAll("/", ".");
    }
}

