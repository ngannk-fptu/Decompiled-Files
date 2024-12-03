/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.internal.rest.applink.data;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.internal.common.exception.ServiceException;
import com.atlassian.applinks.internal.rest.applink.data.AbstractRestApplinkDataProvider;
import com.atlassian.applinks.internal.status.error.ApplinkStatusException;
import com.atlassian.applinks.internal.status.support.ApplinkStatusValidationService;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

public class EditableDataProvider
extends AbstractRestApplinkDataProvider {
    public static final String EDITABLE = "editable";
    public static final String V3_EDITABLE = "v3Editable";
    private final ApplinkStatusValidationService applinkStatusValidationService;

    @Autowired
    public EditableDataProvider(ApplinkStatusValidationService applinkStatusValidationService) {
        super((Set<String>)ImmutableSet.of((Object)EDITABLE, (Object)V3_EDITABLE));
        this.applinkStatusValidationService = applinkStatusValidationService;
    }

    @Override
    @Nullable
    public Object provide(@Nonnull String key, @Nonnull ApplicationLink applink) throws ServiceException {
        if (EDITABLE.equals(key)) {
            return !applink.isSystem();
        }
        if (V3_EDITABLE.equals(key)) {
            try {
                this.applinkStatusValidationService.checkEditable(applink);
                return true;
            }
            catch (ApplinkStatusException e) {
                return false;
            }
        }
        throw new IllegalArgumentException(String.format("Unsupported key: '%s'", key));
    }
}

