/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.BaseApiEnum
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.mobile.notification;

import com.atlassian.confluence.api.model.BaseApiEnum;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public final class PushNotificationStatus
extends BaseApiEnum {
    public static final PushNotificationStatus ENABLED = new PushNotificationStatus("enabled");
    public static final PushNotificationStatus DISABLED = new PushNotificationStatus("disabled");
    private static final List<PushNotificationStatus> BUILT_IN = Collections.unmodifiableList(Arrays.asList(ENABLED, DISABLED));

    public PushNotificationStatus(String value) {
        super(value);
    }

    public static PushNotificationStatus valueOf(@Nullable String value) {
        return BUILT_IN.stream().filter(category -> category.serialise().equals(value)).findFirst().orElse(null);
    }
}

