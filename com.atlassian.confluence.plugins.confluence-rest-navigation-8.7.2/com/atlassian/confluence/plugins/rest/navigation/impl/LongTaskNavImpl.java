/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.longtasks.LongTaskId
 *  com.atlassian.confluence.api.nav.Navigation$LongTaskNav
 */
package com.atlassian.confluence.plugins.rest.navigation.impl;

import com.atlassian.confluence.api.model.longtasks.LongTaskId;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.plugins.rest.navigation.impl.DelegatingPathBuilder;
import com.atlassian.confluence.plugins.rest.navigation.impl.RestNavigationImpl;

class LongTaskNavImpl
extends DelegatingPathBuilder
implements Navigation.LongTaskNav {
    public LongTaskNavImpl(LongTaskId id, RestNavigationImpl.BaseApiPathBuilder basePath) {
        super("/longtask/" + id.serialise(), basePath);
    }
}

