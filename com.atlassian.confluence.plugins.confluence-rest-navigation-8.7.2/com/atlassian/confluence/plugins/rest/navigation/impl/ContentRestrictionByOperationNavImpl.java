/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.permissions.OperationKey
 *  com.atlassian.confluence.api.nav.Navigation$Builder
 *  com.atlassian.confluence.api.nav.Navigation$ContentRestrictionByOperationNav
 */
package com.atlassian.confluence.plugins.rest.navigation.impl;

import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.plugins.rest.navigation.impl.AbstractNav;
import com.atlassian.confluence.plugins.rest.navigation.impl.DelegatingPathBuilder;

class ContentRestrictionByOperationNavImpl
extends DelegatingPathBuilder
implements Navigation.ContentRestrictionByOperationNav {
    public ContentRestrictionByOperationNavImpl(AbstractNav base) {
        super("/restriction/byOperation", base);
    }

    public Navigation.Builder operation(OperationKey operationKey) {
        return new ContentRestrictionByOperationKeyNav(operationKey, (AbstractNav)this);
    }

    public static class ContentRestrictionByOperationKeyNav
    extends DelegatingPathBuilder {
        public ContentRestrictionByOperationKeyNav(OperationKey key, AbstractNav delegate) {
            super("/" + key.getValue(), delegate);
        }
    }
}

