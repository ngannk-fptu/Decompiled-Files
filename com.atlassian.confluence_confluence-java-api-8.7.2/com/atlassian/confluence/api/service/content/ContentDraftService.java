/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 */
package com.atlassian.confluence.api.service.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.BaseApiEnum;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.validation.MergeValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import java.util.function.Function;
import org.codehaus.jackson.annotate.JsonCreator;

@ExperimentalApi
public interface ContentDraftService {
    public Content publishNewDraft(Content var1, Expansion ... var2);

    public Content publishEditDraft(Content var1, ConflictPolicy var2);

    public void deleteDraft(ContentId var1);

    public DraftValidator validator();

    public static final class DraftErrorCodes {
        public static final String MISSING_CONTENT_REFERENCE_TRANSLATION = "Reference to content in the content body needs to be set for publishing draft";
        public static final String MISSING_CONTENT_ID_TRANSLATION = "Could not publish content without content id";
        public static final String MISSING_PERMISSION_KEY = "not.permitted.description";
        public static final String MISSING_PERMISSION_TRANSLATION = "You don't have permission to view or edit this draft";
        public static final Function<Object, String> CONTENT_NOT_FOUND_TRANSLATION = o -> String.format("Could not find draft content with id of %s", o);
        public static final Function<Object, String> INVALID_POLICY_TRANSLATION = o -> String.format("ABORT conflict policy required, but %s was found.", o);
        public static final Function<Object, String> INVALID_CONTENT_STATUS_TRANSLATION = o -> String.format("Current content status required, but %s was found.", o);
        public static final String MISSING_SPACE_KEY_TRANSLATION = "Space key is required.";
        public static final String MISSING_CONTENT_TYPE_TRANSLATION = "Content type is required.";
        public static final Function<Object, String> CONTENT_WAS_TRASHED_TRANSLATION = o -> String.format("Content with id: %s was trashed.", o);
    }

    public static class ConflictPolicy
    extends BaseApiEnum {
        public static final ConflictPolicy ABORT = new ConflictPolicy("abort");

        public ConflictPolicy(String value) {
            super(value);
        }

        @Override
        public String getValue() {
            return this.value;
        }

        @JsonCreator
        public static ConflictPolicy valueOf(String str) {
            if (str == null || str.isEmpty()) {
                return null;
            }
            return new ConflictPolicy(str);
        }
    }

    public static interface DraftValidator {
        public MergeValidationResult validateContentForPageCreate(Content var1);

        public MergeValidationResult validateContentForPageUpdate(Content var1, ConflictPolicy var2);

        public ValidationResult validateDelete(ContentId var1);
    }
}

