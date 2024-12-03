/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.crowd.embedded.api.FeatureFlag
 */
package com.atlassian.crowd.feature;

import com.atlassian.annotations.Internal;
import com.atlassian.crowd.embedded.api.FeatureFlag;

@Internal
public interface FeatureEnabledChecker
extends FeatureFlag {
    public boolean isAccessible();

    public void throwIfFeatureDisabledOrInaccessible();
}

