/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.voorhees;

import com.atlassian.voorhees.ErrorMapper;
import com.atlassian.voorhees.I18nAdapter;
import com.atlassian.voorhees.JsonError;
import java.io.Serializable;

public class DefaultErrorMapper
implements ErrorMapper {
    public static final int GENERIC_ERROR_CODE = 500;
    private final I18nAdapter i18nAdapter;

    public DefaultErrorMapper(I18nAdapter i18nAdapter) {
        this.i18nAdapter = i18nAdapter;
    }

    @Override
    public JsonError mapError(String methodName, Throwable throwable) {
        return new JsonError(500, this.i18nAdapter.getText("voorhees.something.went.wrong", new Serializable[]{throwable.toString()}), (Object)throwable);
    }
}

