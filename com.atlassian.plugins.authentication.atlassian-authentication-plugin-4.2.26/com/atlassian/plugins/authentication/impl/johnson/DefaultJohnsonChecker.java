/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.johnson.Johnson
 *  com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.BitbucketComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.FecruComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.RefappComponent
 *  com.atlassian.plugin.spring.scanner.annotation.component.StashComponent
 *  javax.servlet.ServletContext
 */
package com.atlassian.plugins.authentication.impl.johnson;

import com.atlassian.johnson.Johnson;
import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.BitbucketComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.FecruComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.RefappComponent;
import com.atlassian.plugin.spring.scanner.annotation.component.StashComponent;
import com.atlassian.plugins.authentication.impl.johnson.JohnsonChecker;
import javax.servlet.ServletContext;

@BitbucketComponent
@StashComponent
@ConfluenceComponent
@BambooComponent
@FecruComponent
@RefappComponent
public class DefaultJohnsonChecker
implements JohnsonChecker {
    @Override
    public boolean isInstanceJohnsoned(ServletContext servletContext) {
        return Johnson.isInitialized() && Johnson.getEventContainer((ServletContext)servletContext).hasEvents();
    }
}

