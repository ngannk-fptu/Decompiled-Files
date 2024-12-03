/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.plugins.rest.module;

import com.atlassian.plugins.rest.module.InvalidVersionException;
import java.util.function.Function;
import javax.annotation.Nullable;

public class ApiVersion
extends com.atlassian.plugins.rest.common.version.ApiVersion {
    public static final String NONE_STRING = "none";
    public static final ApiVersion NONE = new ApiVersion("none");

    private static InvalidVersionException invalidVersionException(String version) {
        return new InvalidVersionException(version);
    }

    public ApiVersion(String version) {
        super(version, new Function<String, RuntimeException>(){

            @Override
            @Nullable
            public RuntimeException apply(String input) {
                return ApiVersion.invalidVersionException(input);
            }
        });
    }
}

