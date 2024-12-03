/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.PropertyUtils
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.plugins.authentication.impl.rest;

import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.PropertyUtils;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.impl.rest.model.JitUserEntity;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class JitProvisionedUsersResourceService {
    private final CrowdService crowdService;

    @Inject
    public JitProvisionedUsersResourceService(@ComponentImport CrowdService crowdService) {
        this.crowdService = crowdService;
    }

    public List<JitUserEntity> findJitProvisionedUsers() {
        Iterable users = this.crowdService.search((Query)QueryBuilder.queryFor(User.class, (EntityDescriptor)EntityDescriptor.user()).with((SearchRestriction)Restriction.on((Property)PropertyUtils.ofTypeString((String)"jit_idp_id")).containing((Object)"")).returningAtMost(1000));
        return StreamSupport.stream(users.spliterator(), false).map(u -> new JitUserEntity(u.getName(), u.getDisplayName(), u.getEmailAddress())).collect(Collectors.toList());
    }
}

