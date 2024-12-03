/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.manager.application.ApplicationManager
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.ApplicationType
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.constants.ApplicationTermKeys
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 */
package com.atlassian.crowd.manager.application;

import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.manager.application.ApplicationManager;
import com.atlassian.crowd.manager.application.InternalApplicationHelper;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ApplicationType;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.constants.ApplicationTermKeys;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.util.List;

public class InternalApplicationHelperImpl
implements InternalApplicationHelper {
    private final ApplicationManager applicationManager;

    public InternalApplicationHelperImpl(ApplicationManager applicationManager) {
        this.applicationManager = (ApplicationManager)Preconditions.checkNotNull((Object)applicationManager);
    }

    @Override
    public Application findCrowdConsoleApplication() throws RuntimeException {
        List crowdApplications = this.applicationManager.search(QueryBuilder.queryFor(Application.class, (EntityDescriptor)EntityDescriptor.application(), (SearchRestriction)Restriction.on((Property)ApplicationTermKeys.TYPE).exactlyMatching((Object)ApplicationType.CROWD), (int)0, (int)-1));
        return (Application)Iterables.getOnlyElement((Iterable)crowdApplications);
    }
}

