/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.fugue.Either
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.security.query;

import com.atlassian.confluence.core.persistence.schema.api.SchemaInformationService;
import com.atlassian.confluence.impl.security.access.AccessDenied;
import com.atlassian.confluence.impl.security.access.SpacePermissionAccessMapper;
import com.atlassian.confluence.impl.security.access.SpacePermissionSubjectType;
import com.atlassian.confluence.impl.security.query.SpacePermissionQueryBuilder;
import com.atlassian.confluence.impl.security.query.SpacePermissionQueryBuilderImpl;
import com.atlassian.confluence.impl.security.query.SpacePermissionQueryManager;
import com.atlassian.confluence.security.access.AccessStatus;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.fugue.Either;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DefaultSpacePermissionQueryManager
implements SpacePermissionQueryManager {
    private static final Supplier<List<String>> EMPTY_LIST_SUPPLIER = Suppliers.ofInstance(Collections.emptyList());
    private final ConfluenceAccessManager confluenceAccessManager;
    private final SpacePermissionAccessMapper spacePermissionAccessMapper;
    private final CrowdService crowdService;
    private final SchemaInformationService schemaInformationService;

    public DefaultSpacePermissionQueryManager(ConfluenceAccessManager confluenceAccessManager, SpacePermissionAccessMapper spacePermissionAccessMapper, CrowdService crowdService, SchemaInformationService schemaInformationService) {
        this.confluenceAccessManager = confluenceAccessManager;
        this.spacePermissionAccessMapper = spacePermissionAccessMapper;
        this.crowdService = crowdService;
        this.schemaInformationService = schemaInformationService;
    }

    @Override
    public Either<AccessDenied, SpacePermissionQueryBuilder> createSpacePermissionQueryBuilder(@Nullable ConfluenceUser user, @NonNull String permissionType) {
        AccessStatus accessStatus = this.confluenceAccessManager.getUserAccessStatus(user);
        Either<AccessDenied, Set<SpacePermissionSubjectType>> spacePermissionSubjectTypes = this.spacePermissionAccessMapper.getPermissionCheckSubjectTypes(accessStatus, permissionType);
        return spacePermissionSubjectTypes.map(permCategorySet -> new SpacePermissionQueryBuilderImpl(user, (Set<SpacePermissionSubjectType>)permCategorySet, this.userGroupNamesSupplier(user), permissionType, this.schemaInformationService.getDialect()));
    }

    private Supplier<List<String>> userGroupNamesSupplier(ConfluenceUser user) {
        if (user == null) {
            return EMPTY_LIST_SUPPLIER;
        }
        return Suppliers.memoize(() -> this.getGroupNames(user));
    }

    private List<String> getGroupNames(@NonNull ConfluenceUser user) {
        String userName = user.getName();
        if (userName == null) {
            return Collections.emptyList();
        }
        Iterable result = this.crowdService.search((Query)QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group()).parentsOf(EntityDescriptor.user()).withName(userName).returningAtMost(-1));
        return ImmutableList.copyOf((Iterable)Iterables.filter((Iterable)result, StringUtils::isNotBlank));
    }
}

