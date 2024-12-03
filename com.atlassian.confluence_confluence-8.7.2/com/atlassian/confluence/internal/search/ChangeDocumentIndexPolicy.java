/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 */
package com.atlassian.confluence.internal.search;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentTypeAware;
import com.atlassian.confluence.core.Versioned;
import com.atlassian.confluence.internal.search.LuceneIndependent;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.confluence.user.PersonalInformation;

@LuceneIndependent
public class ChangeDocumentIndexPolicy {
    public static PolicyCheckResult buildFor(Searchable searchable) {
        PersonalInformation personalInfo;
        Versioned latestVersion;
        if (!(searchable instanceof ContentTypeAware)) {
            return new PolicyCheckResult("Must be ContentTypeAware");
        }
        if (!ChangeDocumentIndexPolicy.isInstanceOfSupportedPersistentClass(searchable)) {
            return new PolicyCheckResult(searchable.getClass() + " is not supported");
        }
        if (searchable instanceof Versioned && !((Searchable)(latestVersion = ((Versioned)searchable).getLatestVersion())).isIndexable()) {
            return new PolicyCheckResult("Latest version must be indexable");
        }
        if (ChangeDocumentIndexPolicy.isDraft(searchable)) {
            return new PolicyCheckResult("Drafts should not be considered during change indexing.");
        }
        if (searchable instanceof Attachment && ((Attachment)searchable).isHidden()) {
            return new PolicyCheckResult("Hidden attachments are not accounted for in the change index");
        }
        if (searchable instanceof PersonalInformation && (personalInfo = (PersonalInformation)searchable).getCreationDate() != null && personalInfo.getCreationDate().equals(personalInfo.getLastModificationDate())) {
            return new PolicyCheckResult("Initial creation of user is not accounted for in the change index");
        }
        return PolicyCheckResult.PASSED;
    }

    private static boolean isInstanceOfSupportedPersistentClass(Searchable searchable) {
        return searchable instanceof Page || searchable instanceof BlogPost || searchable instanceof Comment || searchable instanceof Attachment || searchable instanceof PersonalInformation || searchable instanceof SpaceDescription || searchable instanceof CustomContentEntityObject;
    }

    private static boolean isDraft(Searchable searchable) {
        return searchable instanceof ContentEntityObject && ((ContentEntityObject)searchable).isDraft();
    }

    public static boolean shouldIndex(Searchable searchable) {
        return ChangeDocumentIndexPolicy.buildFor(searchable).passed();
    }

    public static boolean shouldUnIndex(Searchable searchable) {
        return ChangeDocumentIndexPolicy.isInstanceOfSupportedPersistentClass(searchable) && !ChangeDocumentIndexPolicy.isDraft(searchable);
    }

    public static class PolicyCheckResult {
        public static final PolicyCheckResult PASSED = new PolicyCheckResult();
        private final String errorMessage;
        private final boolean passed;

        private PolicyCheckResult(String errorMessage) {
            this.errorMessage = errorMessage;
            this.passed = false;
        }

        private PolicyCheckResult() {
            this.errorMessage = "";
            this.passed = true;
        }

        public String getErrorMessage() {
            return this.errorMessage;
        }

        public boolean passed() {
            return this.passed;
        }

        public boolean failed() {
            return !this.passed();
        }
    }
}

