/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.vcache.internal;

import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class NameValidator {
    private static final Pattern LEGAL_PATTERN = Pattern.compile("^[\\w\\./\\-_\\$]+$");

    public static boolean isValidProductIdentifier(@Nullable String name) {
        return name != null && LEGAL_PATTERN.matcher(name).matches();
    }

    public static String requireValidProductIdentifier(@Nullable String name) {
        if (!NameValidator.isValidProductIdentifier(name)) {
            throw new IllegalArgumentException("Invalid product identifier: " + name);
        }
        return name;
    }

    public static boolean isValidPartitionIdentifier(@Nullable String name) {
        return name != null && LEGAL_PATTERN.matcher(name).matches();
    }

    public static String requireValidPartitionIdentifier(@Nullable String name) {
        if (!NameValidator.isValidPartitionIdentifier(name)) {
            throw new IllegalArgumentException("Invalid partition identifier: " + name);
        }
        return name;
    }

    public static boolean isValidKeyName(@Nullable String name) {
        return name != null && LEGAL_PATTERN.matcher(name).matches();
    }

    public static String requireValidKeyName(@Nullable String name) {
        if (!NameValidator.isValidKeyName(name)) {
            throw new IllegalArgumentException("Invalid key name: " + name);
        }
        return name;
    }

    public static boolean isValidCacheName(String name) {
        return LEGAL_PATTERN.matcher(name).matches();
    }

    public static String requireValidCacheName(String name) {
        if (!NameValidator.isValidCacheName(name)) {
            throw new IllegalArgumentException("Invalid cache name: " + name);
        }
        return name;
    }
}

