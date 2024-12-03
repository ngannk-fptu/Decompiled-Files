/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.BitbucketComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.FecruComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.RefappComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.StashComponent
 */
package com.atlassian.plugins.authentication.impl.util;

import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.BitbucketComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.FecruComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.RefappComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.StashComponent;
import com.atlassian.plugins.authentication.impl.util.LegacyAuthenticationMethodsDataProvider;

@BitbucketComponent
@StashComponent
@JiraComponent
@BambooComponent
@FecruComponent
@RefappComponent
public class DefaultLegacyAuthenticationMethodsDataProvider
implements LegacyAuthenticationMethodsDataProvider {
    @Override
    public boolean hasLegacyAuthenticationMethodsConfigured() {
        return false;
    }
}

