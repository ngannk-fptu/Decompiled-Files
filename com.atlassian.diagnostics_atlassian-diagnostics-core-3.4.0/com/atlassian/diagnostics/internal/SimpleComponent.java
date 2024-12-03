/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Component
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal;

import com.atlassian.diagnostics.Component;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.base.MoreObjects;
import javax.annotation.Nonnull;

class SimpleComponent
implements Component {
    private final String nameI18nKey;
    private final I18nResolver i18nResolver;
    private final String id;

    SimpleComponent(I18nResolver i18nResolver, String id, String nameI18nKey) {
        this.i18nResolver = i18nResolver;
        this.id = id;
        this.nameI18nKey = nameI18nKey;
    }

    @Nonnull
    public String getId() {
        return this.id;
    }

    @Nonnull
    public String getName() {
        return this.i18nResolver.getText(this.nameI18nKey);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.id).toString();
    }
}

