/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.labels.dto;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelUrlBuilder;
import com.atlassian.confluence.labels.dto.CountableLabel;
import com.atlassian.confluence.labels.persistence.dao.LabelSearchResult;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import java.io.Serializable;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public class LiteLabelSearchResult
implements CountableLabel,
Serializable {
    private long id;
    private String name;
    private String namespace;
    private int count;
    private String lowerUserName;

    public LiteLabelSearchResult() {
    }

    public LiteLabelSearchResult(Label label) {
        this(label, 1);
    }

    public LiteLabelSearchResult(LabelSearchResult labelSearchResult) {
        this(labelSearchResult.getLabel(), labelSearchResult.getCount());
    }

    public LiteLabelSearchResult(Label label, int count) {
        this(label.getId(), label.getName(), label.getNamespace().getPrefix(), LiteLabelSearchResult.getLowerName(label.getOwnerUser()), count);
    }

    private static String getLowerName(@Nullable ConfluenceUser user) {
        return user != null ? user.getLowerName() : null;
    }

    public LiteLabelSearchResult(long id, String name, String namespace, String lowerUserName, int count) {
        this.id = id;
        this.name = name;
        this.namespace = namespace;
        this.count = count;
        this.lowerUserName = lowerUserName;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getLowerUserName() {
        return this.lowerUserName;
    }

    public void setUser(String lowerUserName) {
        this.lowerUserName = lowerUserName;
    }

    public String getUrlPath() {
        return LabelUrlBuilder.builder().id(this.getId()).name(this.name).namespace(this.namespace).owner(this.getLowerUserName()).currentUser(AuthenticatedUserThreadLocal.get()).buildDisplayUrl();
    }

    public String getUrlPath(String currentSpaceKey) {
        return LabelUrlBuilder.builder().id(this.getId()).name(this.name).namespace(this.namespace).owner(this.getLowerUserName()).currentSpaceKey(currentSpaceKey).currentUser(AuthenticatedUserThreadLocal.get()).buildDisplayUrl();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LiteLabelSearchResult that = (LiteLabelSearchResult)o;
        return Objects.equals(this.name, that.name) && Objects.equals(this.namespace, that.namespace) && Objects.equals(this.lowerUserName, that.lowerUserName);
    }

    public int hashCode() {
        return Objects.hash(this.name, this.namespace, this.lowerUserName);
    }
}

