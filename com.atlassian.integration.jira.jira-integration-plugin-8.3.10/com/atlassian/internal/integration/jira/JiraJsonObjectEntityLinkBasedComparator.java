/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.EntityLink
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.internal.integration.jira;

import com.atlassian.applinks.api.EntityLink;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class JiraJsonObjectEntityLinkBasedComparator
implements Comparator<JSONObject> {
    private final Iterable<EntityLink> jiraLinks;

    public JiraJsonObjectEntityLinkBasedComparator(@Nonnull Iterable<EntityLink> jiraLinks) {
        this.jiraLinks = Objects.requireNonNull(jiraLinks, "jiraLinks");
    }

    @Override
    public int compare(@Nullable JSONObject object, @Nullable JSONObject otherObject) {
        JiraIssue otherIssue;
        JiraIssue issue = this.createIssueIfNotNull(object);
        if (issue == (otherIssue = this.createIssueIfNotNull(otherObject))) {
            return 0;
        }
        if (issue == null) {
            return 1;
        }
        if (otherIssue == null) {
            return -1;
        }
        if (issue.getLink() != otherIssue.getLink()) {
            if (issue.getLink() != null && issue.getLink().isPrimary()) {
                return -1;
            }
            if (otherIssue.getLink() != null && otherIssue.getLink().isPrimary()) {
                return 1;
            }
            if (issue.getLink() == null) {
                return 1;
            }
            if (otherIssue.getLink() == null) {
                return -1;
            }
        }
        return JiraJsonObjectEntityLinkBasedComparator.compareIssueKeys(issue, otherIssue);
    }

    private static int compareIssueKeys(JiraIssue issue, JiraIssue otherIssue) {
        return issue.getIssueKey().compareTo(otherIssue.getIssueKey());
    }

    private JiraIssue createIssueIfNotNull(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        try {
            String key = jsonObject.getString("key");
            String url = jsonObject.getString("url");
            EntityLink projectLink = this.getProjectLink(key, url);
            return new JiraIssue(key, projectLink);
        }
        catch (JSONException e) {
            return null;
        }
    }

    private EntityLink getProjectLink(String issueKey, String url) {
        return StreamSupport.stream(this.jiraLinks.spliterator(), false).filter(jiraLink -> issueKey.startsWith(jiraLink.getKey()) && url.startsWith(jiraLink.getDisplayUrl().toString())).findAny().orElse(null);
    }

    private static class JiraIssue {
        private final String issueKey;
        private final EntityLink link;

        JiraIssue(String issueKey, EntityLink link) {
            this.issueKey = issueKey;
            this.link = link;
        }

        String getIssueKey() {
            return this.issueKey;
        }

        EntityLink getLink() {
            return this.link;
        }
    }
}

