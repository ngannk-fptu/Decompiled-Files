/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.metrics;

import com.amazonaws.metrics.ServiceMetricType;
import com.amazonaws.metrics.SimpleMetricType;
import com.amazonaws.metrics.ThroughputMetricType;

public class S3ServiceMetric
extends SimpleMetricType
implements ServiceMetricType {
    static final String SERVICE_NAME_PREFIX = "S3";
    public static final S3ThroughputMetric S3DownloadThroughput = new S3ThroughputMetric(S3ServiceMetric.metricName("DownloadThroughput")){

        @Override
        public ServiceMetricType getByteCountMetricType() {
            return S3DownloadByteCount;
        }
    };
    public static final S3ServiceMetric S3DownloadByteCount = new S3ServiceMetric(S3ServiceMetric.metricName("DownloadByteCount"));
    public static final S3ThroughputMetric S3UploadThroughput = new S3ThroughputMetric(S3ServiceMetric.metricName("UploadThroughput")){

        @Override
        public ServiceMetricType getByteCountMetricType() {
            return S3UploadByteCount;
        }
    };
    public static final S3ServiceMetric S3UploadByteCount = new S3ServiceMetric(S3ServiceMetric.metricName("UploadByteCount"));
    private static final S3ServiceMetric[] values = new S3ServiceMetric[]{S3DownloadThroughput, S3DownloadByteCount, S3UploadThroughput, S3UploadByteCount};
    private final String name;

    private static final String metricName(String suffix) {
        return SERVICE_NAME_PREFIX + suffix;
    }

    private S3ServiceMetric(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String getServiceName() {
        return "Amazon S3";
    }

    public static S3ServiceMetric[] values() {
        return (S3ServiceMetric[])values.clone();
    }

    public static S3ServiceMetric valueOf(String name) {
        for (S3ServiceMetric e : S3ServiceMetric.values()) {
            if (!e.name().equals(name)) continue;
            return e;
        }
        throw new IllegalArgumentException("No S3ServiceMetric defined for the name " + name);
    }

    private static abstract class S3ThroughputMetric
    extends S3ServiceMetric
    implements ThroughputMetricType {
        private S3ThroughputMetric(String name) {
            super(name);
        }
    }
}

