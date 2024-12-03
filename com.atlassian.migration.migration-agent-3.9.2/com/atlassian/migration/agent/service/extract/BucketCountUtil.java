/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.migration.agent.service.extract;

import com.atlassian.migration.agent.service.extract.SimpleBucketedTag;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class BucketCountUtil {
    private static final List<SimpleBucketedTag> CUSTOMERS_COUNT_BUCKETED_TAGS = ImmutableList.of((Object)new SimpleBucketedTag(0, 99), (Object)new SimpleBucketedTag(100, 999), (Object)new SimpleBucketedTag(1000, 4999), (Object)new SimpleBucketedTag(5000, 9999), (Object)new SimpleBucketedTag(10000, 19999), (Object)new SimpleBucketedTag(20000, 49999), (Object)new SimpleBucketedTag(50000, 99999), (Object)new SimpleBucketedTag(100000, Integer.MAX_VALUE));

    private BucketCountUtil() {
    }

    public static String resolveTagValue(int size, SimpleBucketedTag[] tags) {
        return BucketCountUtil.resolveTagValue(size, Arrays.stream(tags));
    }

    public static String resolveTagValue(int size, List<SimpleBucketedTag> tags) {
        return BucketCountUtil.resolveTagValue(size, tags.stream());
    }

    public static String resolveTagValue(int size, Stream<SimpleBucketedTag> tags) {
        return tags.filter(bucketTypeSize -> size >= bucketTypeSize.getLow() && size <= bucketTypeSize.getHigh()).findFirst().orElseThrow(() -> new IllegalArgumentException("Failed to find a tag for size " + size)).getLabel();
    }

    public static String resolveCountTag(int customersCount) {
        return BucketCountUtil.resolveTagValue(customersCount, CUSTOMERS_COUNT_BUCKETED_TAGS);
    }
}

