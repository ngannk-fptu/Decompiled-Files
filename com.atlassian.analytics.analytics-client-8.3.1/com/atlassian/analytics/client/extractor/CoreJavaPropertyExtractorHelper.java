/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.analytics.client.extractor;

import com.atlassian.analytics.client.extractor.PropertyContributor;
import com.atlassian.analytics.client.extractor.PropertyExtractorHelper;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class CoreJavaPropertyExtractorHelper
extends PropertyExtractorHelper {
    public static final ImmutableSet<String> EXCLUDE_PROPERTIES = ImmutableSet.of((Object)"source", (Object)"class", (Object)"timestamp", (Object)"token");

    public CoreJavaPropertyExtractorHelper() {
        super((Set<String>)EXCLUDE_PROPERTIES, new PropertyContributor[0]);
    }
}

