/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.common.util;

import java.util.Base64;

public final class Base64Util {
    private static final Base64.Encoder ENCODER_NO_PADDING = Base64.getUrlEncoder().withoutPadding();

    private Base64Util() {
    }

    public static Base64.Encoder urlSafeEncoderWithoutPadding() {
        return ENCODER_NO_PADDING;
    }
}

