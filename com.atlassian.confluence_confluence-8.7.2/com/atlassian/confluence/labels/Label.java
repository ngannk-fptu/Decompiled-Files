/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.bean.EntityObject
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.labels;

import com.atlassian.confluence.core.Addressable;
import com.atlassian.confluence.labels.DisplayableLabel;
import com.atlassian.confluence.labels.LabelParser;
import com.atlassian.confluence.labels.LabelUrlBuilder;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.core.bean.EntityObject;
import java.io.Serializable;
import java.text.Collator;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Label
extends EntityObject
implements Comparable<Label>,
Addressable,
Serializable,
DisplayableLabel {
    private static final Logger log = LoggerFactory.getLogger(Label.class);
    private String name;
    private ConfluenceUser owningUser;
    private Namespace namespace;

    public Label() {
        this(null);
    }

    public Label(String name) {
        this(name, Namespace.GLOBAL);
    }

    public Label(String name, Namespace namespace) {
        this(name, namespace, (ConfluenceUser)null);
    }

    public Label(String name, String namespace) {
        this(name, namespace, (ConfluenceUser)null);
    }

    public Label(String name, String namespace, ConfluenceUser owner) {
        this(name, Namespace.getNamespace(namespace), owner);
    }

    @Deprecated
    public Label(String name, String namespace, String owner) {
        this(name, Namespace.getNamespace(namespace), owner);
    }

    @Deprecated
    public Label(String name, Namespace namespace, @Nullable String owner) {
        this.name = name != null ? name.toLowerCase() : null;
        this.namespace = namespace;
        if (owner != null) {
            this.owningUser = FindUserHelper.getUserByUsername(owner);
            if (this.owningUser == null) {
                throw new IllegalArgumentException("No user could be found with the username " + owner);
            }
        }
    }

    public Label(String name, Namespace namespace, @Nullable ConfluenceUser owner) {
        this.name = name != null ? name.toLowerCase() : null;
        this.namespace = namespace;
        this.owningUser = owner;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Deprecated
    public @Nullable String getOwner() {
        if (this.owningUser != null) {
            return this.owningUser.getName();
        }
        return null;
    }

    public @Nullable ConfluenceUser getOwnerUser() {
        return this.owningUser;
    }

    public Namespace getNamespace() {
        return this.namespace;
    }

    @Override
    public String getType() {
        return "label:" + this.getNamespace().toString();
    }

    private void setNamespace(Namespace namespace) {
        this.namespace = namespace;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Label label = (Label)o;
        if (!Objects.equals(this.namespace, label.namespace)) {
            return false;
        }
        if (!Objects.equals(this.name, label.name)) {
            return false;
        }
        String owner = this.getOwner();
        if (owner != null) {
            return owner.equalsIgnoreCase(label.getOwner());
        }
        return label.getOwner() == null;
    }

    public int hashCode() {
        int result = 0;
        result = 29 * result + (this.namespace != null ? this.namespace.hashCode() : 0);
        result = 29 * result + (this.name != null ? this.name.hashCode() : 0);
        result = 29 * result + (this.owningUser != null ? this.owningUser.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Label otherlabel) {
        Collator collator = Collator.getInstance();
        int typeComparison = collator.compare(this.getNamespace().toString(), otherlabel.getNamespace().toString());
        if (typeComparison != 0) {
            return typeComparison;
        }
        return collator.compare(this.getName(), otherlabel.getName());
    }

    @Override
    public String getRealTitle() {
        return this.getDisplayTitle();
    }

    @Override
    public String getDisplayTitle() {
        return LabelParser.render(this);
    }

    @Override
    public boolean isRealTitleSafeForUrl() {
        return GeneralUtil.isSafeTitleForUrl(this.getDisplayTitle());
    }

    @Override
    public String getUrlPath() {
        return LabelUrlBuilder.builder().id(this.getId()).name(this.name).namespace(this.getNamespace().getPrefix()).owner(this.getOwner()).currentUser(AuthenticatedUserThreadLocal.get()).buildDisplayUrl();
    }

    @Override
    public String getUrlPath(String spaceKey) {
        return LabelUrlBuilder.builder().id(this.getId()).name(this.name).namespace(this.getNamespace().getPrefix()).owner(this.getOwner()).currentSpaceKey(spaceKey).currentUser(AuthenticatedUserThreadLocal.get()).buildDisplayUrl();
    }

    @Deprecated
    public boolean isNew() {
        return this.getCreationDate() == null || this.getCreationDate().equals(this.getLastModificationDate());
    }

    public boolean isPersistent() {
        return this.getId() != 0L;
    }

    public boolean isVisibleTo(@Nullable String username) {
        if (this.namespace == null) {
            log.error("Label " + this.getId() + " has null namespace");
            return true;
        }
        if ("public".equals(this.namespace.getVisibility())) {
            return true;
        }
        if ("owner".equals(this.namespace.getVisibility())) {
            return username != null && username.equals(this.getOwner());
        }
        return false;
    }

    public String toStringWithOwnerPrefix() {
        return "~" + this.getOwner() + ":" + this.getName();
    }

    public String toString() {
        return this.getDisplayTitle();
    }

    public String toStringWithNamespace() {
        return this.getNamespace().toString() + ":" + this.getName();
    }

    public boolean isTeamLabel() {
        return Namespace.isTeam(this);
    }
}

