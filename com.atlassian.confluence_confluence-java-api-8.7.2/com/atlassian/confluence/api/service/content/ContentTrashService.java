/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.service.content;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.validation.ValidationResult;

public interface ContentTrashService {
    public static final String TRASH_DATE_METADATA_KEY = "trashdate";
    public static final String TRASH_CREATED_BY_USERNAME_METADATA_KEY = "createdByUsername";
    public static final String TRASH_CREATED_BY_FULL_NAME_METADATA_KEY = "createdByFullName";
    public static final String TRASH_DELETED_BY_USERNAME_METADATA_KEY = "deletedByUsername";
    public static final String TRASH_DELETED_BY_FULL_NAME_METADATA_KEY = "deletedByFullName";

    public void trash(Content var1);

    public Content restore(Content var1);

    public void purge(Content var1);

    public Validator validator();

    public static interface Validator {
        public ValidationResult validateTrash(Content var1);

        public ValidationResult validateRestore(Content var1);

        public ValidationResult validatePurge(Content var1);
    }
}

