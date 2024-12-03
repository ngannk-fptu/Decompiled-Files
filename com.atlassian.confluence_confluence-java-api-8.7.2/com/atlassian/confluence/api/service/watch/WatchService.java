/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  com.atlassian.sal.api.user.UserKey
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.api.service.watch;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.model.watch.ContentWatch;
import com.atlassian.confluence.api.model.watch.SpaceWatch;
import com.atlassian.sal.api.user.UserKey;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

@ExperimentalApi
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public interface WatchService {
    public boolean isWatchingSpace(UserKey var1, String var2, ContentType var3);

    public @NonNull SpaceWatch watchSpace(UserKey var1, String var2, List<ContentType> var3);

    public @NonNull SpaceWatch watchSpace(UserKey var1, String var2);

    public void unwatchSpace(UserKey var1, String var2, List<ContentType> var3);

    public void unwatchSpace(UserKey var1, String var2);

    public boolean isWatchingSpace(UserKey var1, String var2);

    public ContentWatch watchContent(UserKey var1, ContentId var2);

    public void unwatchContent(UserKey var1, ContentId var2);

    public boolean isWatchingContent(UserKey var1, ContentId var2);

    @Deprecated
    public Validator validator();

    @Deprecated
    public static interface Validator {
        public ValidationResult validateWatchSpace(UserKey var1, String var2);

        public ValidationResult validateWatchContent(UserKey var1, ContentId var2);
    }
}

