/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.api.service.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.content.VersionRestoreParameters;
import com.atlassian.confluence.api.service.finder.ManyFetcher;
import com.atlassian.confluence.api.service.finder.SingleFetcher;

@ExperimentalApi
public interface ContentVersionService {
    public VersionFinder find(Expansion ... var1);

    public Validator validator();

    public void delete(ContentId var1, int var2);

    public Version restore(ContentId var1, VersionRestoreParameters var2, Expansion ... var3);

    public static interface VersionFinder
    extends ParameterVersionFinder,
    SingleFetcher<Version> {
        public SingleFetcher<Version> withIdAndVersion(ContentId var1, int var2);
    }

    public static interface ParameterVersionFinder
    extends ManyFetcher<Version> {
        public ParameterVersionFinder withId(ContentId var1);
    }

    public static interface Validator {
        public ValidationResult validateDelete(ContentId var1, int var2);

        public ValidationResult validateRestore(ContentId var1, VersionRestoreParameters var2);

        public ValidationResult validateGet(ContentId var1);
    }
}

