/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.api.nav;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.nav.NavigationService;

@Internal
public interface NavigationAware {
    public Navigation.Builder resolveNavigation(NavigationService var1);
}

