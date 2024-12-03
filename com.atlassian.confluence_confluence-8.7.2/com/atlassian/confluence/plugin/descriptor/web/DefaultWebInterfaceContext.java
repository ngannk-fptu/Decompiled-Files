/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.confluence.util.collections.LazyMap
 *  com.atlassian.user.User
 *  com.google.common.base.Supplier
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.plugin.descriptor.web;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.labels.DisplayableLabel;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.confluence.util.collections.LazyMap;
import com.atlassian.user.User;
import com.google.common.base.Supplier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
public class DefaultWebInterfaceContext
implements WebInterfaceContext {
    private final Map<String, java.util.function.Supplier<Object>> parameterSuppliers = new HashMap<String, java.util.function.Supplier<Object>>();
    private final Map<String, Object> contextParameters = LazyMap.fromSuppliersMap(this.parameterSuppliers);

    public static DefaultWebInterfaceContext copyOf(WebInterfaceContext webInterfaceContext) {
        if (webInterfaceContext instanceof DefaultWebInterfaceContext) {
            DefaultWebInterfaceContext copy = new DefaultWebInterfaceContext();
            copy.parameterSuppliers.putAll(((DefaultWebInterfaceContext)webInterfaceContext).parameterSuppliers);
            return copy;
        }
        return DefaultWebInterfaceContext.createFrom(webInterfaceContext.toMap());
    }

    public static DefaultWebInterfaceContext createFrom(Map<String, ?> contextMap) {
        DefaultWebInterfaceContext copy = new DefaultWebInterfaceContext();
        for (Map.Entry<String, ?> entry : contextMap.entrySet()) {
            copy.setParameter(entry.getKey(), entry.getValue());
        }
        return copy;
    }

    @Override
    public @Nullable ConfluenceUser getCurrentUser() {
        User user = (User)this.contextParameters.get("user");
        return FindUserHelper.getUser(user);
    }

    public void setCurrentUser(@Nullable ConfluenceUser user) {
        this.setParameter("user", user);
    }

    @Override
    public @Nullable AbstractPage getPage() {
        return (AbstractPage)this.contextParameters.get("page");
    }

    @Override
    @Deprecated
    public @Nullable Draft getDraft() {
        return (Draft)this.contextParameters.get("draft");
    }

    @Override
    @Deprecated
    public @Nullable ContentEntityObject getContentDraft() {
        ContentEntityObject draft = (ContentEntityObject)this.contextParameters.get("contentDraft");
        if (draft == null) {
            draft = this.getDraft();
        }
        return draft;
    }

    public void setPage(@Nullable AbstractPage page) {
        this.setParameter("page", page);
    }

    @Override
    public @Nullable Space getSpace() {
        return (Space)this.contextParameters.get("space");
    }

    @Override
    public Optional<Long> getSpaceId() {
        return Optional.ofNullable((Long)this.contextParameters.get("spaceid"));
    }

    @Override
    public @Nullable String getSpaceKey() {
        return (String)this.contextParameters.get("spacekey");
    }

    public void setSpace(@Nullable Space space) {
        this.setParameter("space", space);
        if (space != null) {
            this.setParameter("spaceid", space.getId());
        }
    }

    @Override
    public @Nullable Comment getComment() {
        return (Comment)this.contextParameters.get("comment");
    }

    public void setComment(Comment comment) {
        this.setParameter("comment", comment);
    }

    @Override
    public @Nullable DisplayableLabel getLabel() {
        return (DisplayableLabel)this.contextParameters.get("label");
    }

    public void setLabel(DisplayableLabel label) {
        this.setParameter("label", label);
    }

    @Override
    public @Nullable Attachment getAttachment() {
        return (Attachment)this.contextParameters.get("attachment");
    }

    public void setAttachment(Attachment attachment) {
        this.setParameter("attachment", attachment);
    }

    @Override
    public @Nullable PersonalInformation getPersonalInformation() {
        return (PersonalInformation)this.contextParameters.get("userinfo");
    }

    public void setPersonalInformation(PersonalInformation personalInformation) {
        this.setParameter("userinfo", personalInformation);
    }

    @Override
    public @Nullable Object getParameter(String key) {
        return this.contextParameters.get(key);
    }

    @Override
    public boolean hasParameter(String key) {
        return this.contextParameters.containsKey(key);
    }

    public void setParameter(String key, @Nullable Object value) {
        this.withLazyParameter(key, () -> value);
    }

    public void setParameters(Map<String, Object> parameters) {
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            this.setParameter(entry.getKey(), entry.getValue());
        }
    }

    @Deprecated
    public void setLazyParameter(String key, Supplier<Object> supplier) {
        this.parameterSuppliers.put(key, (java.util.function.Supplier<Object>)supplier);
    }

    public void withLazyParameter(String key, java.util.function.Supplier<Object> supplier) {
        this.parameterSuppliers.put(key, supplier);
    }

    @Override
    public Map<String, Object> toMap() {
        return new HashMap<String, Object>(this.contextParameters);
    }

    @Override
    public @Nullable ConfluenceUser getTargetedUser() {
        return (ConfluenceUser)this.contextParameters.get("targetUser");
    }

    public void setTargetedUser(@Nullable ConfluenceUser user) {
        this.setParameter("targetUser", user);
    }

    @Deprecated
    public void setIsEditPageRestricted(boolean restricted) {
        this.setParameter("editPageRestricted", restricted);
    }

    @Override
    @Deprecated
    public boolean isEditPageRestricted() {
        if (!this.hasParameter("editPageRestricted")) {
            return false;
        }
        return (Boolean)this.contextParameters.get("editPageRestricted");
    }

    @Override
    public @Nullable AbstractPage getParentPage() {
        return (AbstractPage)this.contextParameters.get("parentPage");
    }

    public DefaultWebInterfaceContext putAllMissing(WebInterfaceContext source) {
        if (source instanceof DefaultWebInterfaceContext) {
            ((DefaultWebInterfaceContext)source).parameterSuppliers.forEach(this.parameterSuppliers::putIfAbsent);
        } else {
            source.toMap().forEach((key, value) -> this.parameterSuppliers.putIfAbsent((String)key, () -> value));
        }
        return this;
    }
}

