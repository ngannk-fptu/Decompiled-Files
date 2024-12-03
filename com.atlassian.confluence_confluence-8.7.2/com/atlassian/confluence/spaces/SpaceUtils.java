/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.spaces;

import com.atlassian.confluence.spaces.Space;

@Deprecated
public class SpaceUtils {
    public static String getUsernameFromPersonalSpaceKey(String key) {
        if (!Space.isValidPersonalSpaceKey(key)) {
            throw new IllegalArgumentException(key + " is not a valid personal space key.");
        }
        return key.substring("~".length());
    }
}

