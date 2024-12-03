/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.flyingpdf.analytic;

import com.atlassian.confluence.extra.flyingpdf.analytic.PageExportMetrics;
import java.util.Arrays;
import java.util.Collection;

class TimeStatistics {
    private final int minPageTime;
    private final int maxPageTime;
    private final int meanPageTime;
    private final int p50PageTime;
    private final int p95PageTime;
    private final int p98PageTime;
    private final int p99PageTime;
    private final int p999PageTime;

    TimeStatistics(Collection<PageExportMetrics> pages) {
        if (pages.isEmpty()) {
            this.p999PageTime = -1;
            this.p99PageTime = -1;
            this.p98PageTime = -1;
            this.p95PageTime = -1;
            this.p50PageTime = -1;
            this.meanPageTime = -1;
            this.maxPageTime = -1;
            this.minPageTime = -1;
            return;
        }
        this.minPageTime = pages.stream().mapToInt(PageExportMetrics::getTimeMs).min().orElse(-1);
        this.maxPageTime = pages.stream().mapToInt(PageExportMetrics::getTimeMs).max().orElse(-1);
        int totalTime = pages.stream().mapToInt(PageExportMetrics::getTimeMs).sum();
        this.meanPageTime = totalTime / pages.size();
        int[] timings = pages.stream().mapToInt(PageExportMetrics::getTimeMs).toArray();
        int[] quantiles = TimeStatistics.percentiles(timings, 0.5, 0.95, 0.98, 0.99, 0.999);
        this.p50PageTime = quantiles[0];
        this.p95PageTime = quantiles[1];
        this.p98PageTime = quantiles[2];
        this.p99PageTime = quantiles[3];
        this.p999PageTime = quantiles[4];
    }

    int getMinPageTime() {
        return this.minPageTime;
    }

    int getMaxPageTime() {
        return this.maxPageTime;
    }

    int getMeanPageTime() {
        return this.meanPageTime;
    }

    int getP50PageTime() {
        return this.p50PageTime;
    }

    int getP95PageTime() {
        return this.p95PageTime;
    }

    int getP98PageTime() {
        return this.p98PageTime;
    }

    int getP99PageTime() {
        return this.p99PageTime;
    }

    int getP999PageTime() {
        return this.p999PageTime;
    }

    private static int[] percentiles(int[] numbers, double ... percentiles) {
        Arrays.sort(numbers);
        int[] values = new int[percentiles.length];
        for (int i = 0; i < percentiles.length; ++i) {
            int index = Math.min(numbers.length - 1, Math.max(0, (int)(percentiles[i] * (double)numbers.length)));
            values[i] = numbers[index];
        }
        return values;
    }
}

