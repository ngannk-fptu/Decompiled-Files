/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.JsonSpaceProperty
 *  com.atlassian.confluence.api.nav.Navigation$Builder
 *  com.atlassian.confluence.api.nav.Navigation$SpaceContentNav
 *  com.atlassian.confluence.api.nav.Navigation$SpaceNav
 */
package com.atlassian.confluence.plugins.rest.navigation.impl;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.JsonSpaceProperty;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.plugins.rest.navigation.impl.DelegatingPathBuilder;
import com.atlassian.confluence.plugins.rest.navigation.impl.RestNavigationImpl;

class SpaceNavBuilderImpl
extends DelegatingPathBuilder
implements Navigation.SpaceNav {
    public SpaceNavBuilderImpl(String spaceKey, RestNavigationImpl.BaseApiPathBuilder basePath) {
        super("/space/" + spaceKey, basePath);
    }

    public Navigation.SpaceContentNav content() {
        return new SpaceContentNavImpl(this);
    }

    public Navigation.Builder property(JsonSpaceProperty property) {
        return new DelegatingPathBuilder("/property/" + property.getKey(), this);
    }

    public static class SpaceContentNavImpl
    extends DelegatingPathBuilder
    implements Navigation.SpaceContentNav {
        SpaceContentNavImpl(SpaceNavBuilderImpl nav) {
            super("/content", nav);
        }

        public Navigation.Builder type(ContentType type) {
            return new DelegatingPathBuilder("/" + type.toString().toLowerCase(), this);
        }
    }
}

