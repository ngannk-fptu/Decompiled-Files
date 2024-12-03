/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.nav.Navigation
 *  com.atlassian.confluence.api.nav.Navigation$Builder
 *  javax.ws.rs.core.UriBuilder
 */
package com.atlassian.confluence.rest.api.services;

import com.atlassian.confluence.api.nav.Navigation;
import javax.ws.rs.core.UriBuilder;

public interface RestNavigation
extends Navigation {
    public RestBuilder fromUriBuilder(UriBuilder var1);

    public static interface RestBuilder
    extends Navigation.Builder {
        public UriBuilder toAbsoluteUriBuilder();
    }
}

