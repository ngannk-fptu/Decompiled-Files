/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewedManager
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.migration.agent;

import com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewedManager;
import com.atlassian.migration.agent.CommonBeanConfiguration;
import com.atlassian.migration.agent.service.app.PluginManager;
import com.atlassian.migration.agent.store.guardrails.queries.ActiveUsersQuery;
import com.atlassian.migration.agent.store.guardrails.queries.AllGroupsQuery;
import com.atlassian.migration.agent.store.guardrails.queries.AllUsersQuery;
import com.atlassian.migration.agent.store.guardrails.queries.BaseUsersQuery;
import com.atlassian.migration.agent.store.guardrails.queries.CurrentAttPerPageQuery;
import com.atlassian.migration.agent.store.guardrails.queries.EmbeddedAttPerPageQuery;
import com.atlassian.migration.agent.store.guardrails.queries.GroupsPerUserQuery;
import com.atlassian.migration.agent.store.guardrails.queries.InactiveUsersQuery;
import com.atlassian.migration.agent.store.guardrails.queries.JiraIssueMacroPerPageTop100Query;
import com.atlassian.migration.agent.store.guardrails.queries.ListOfAppsInstalledQuery;
import com.atlassian.migration.agent.store.guardrails.queries.MaxCommentsInPageQuery;
import com.atlassian.migration.agent.store.guardrails.queries.MaxLikeInPageQuery;
import com.atlassian.migration.agent.store.guardrails.queries.MaxNumberOfPagesInASpaceQuery;
import com.atlassian.migration.agent.store.guardrails.queries.MaxNumberOfSpaceGroupPermissionsInASpaceQuery;
import com.atlassian.migration.agent.store.guardrails.queries.MaxNumberOfSpacePermissionsInASpaceQuery;
import com.atlassian.migration.agent.store.guardrails.queries.MaxPageWidthQuery;
import com.atlassian.migration.agent.store.guardrails.queries.MaxRestrictionsInPageQuery;
import com.atlassian.migration.agent.store.guardrails.queries.MaxSizePersonalSpaceQuery;
import com.atlassian.migration.agent.store.guardrails.queries.MaxSpaceUserPermissionQuery;
import com.atlassian.migration.agent.store.guardrails.queries.MediaSizeQuery;
import com.atlassian.migration.agent.store.guardrails.queries.MembershipPerGroupOverLimitQuery;
import com.atlassian.migration.agent.store.guardrails.queries.NumberOfGroupsQuery;
import com.atlassian.migration.agent.store.guardrails.queries.NumberOfMacrosPerPageQuery;
import com.atlassian.migration.agent.store.guardrails.queries.NumberOfMediaPerPageQuery;
import com.atlassian.migration.agent.store.guardrails.queries.NumberOfSpacesPerSpaceTypeQuery;
import com.atlassian.migration.agent.store.guardrails.queries.NumberOfSpacesQuery;
import com.atlassian.migration.agent.store.guardrails.queries.NumberOfTablesPerPageQuery;
import com.atlassian.migration.agent.store.guardrails.queries.PageDepthQuery;
import com.atlassian.migration.agent.store.guardrails.queries.PagesWithRestrictionsQuery;
import com.atlassian.migration.agent.store.guardrails.queries.PersonalSpacesForInactiveUsersQuery;
import com.atlassian.migration.agent.store.guardrails.queries.SizeOfDBQuery;
import com.atlassian.migration.agent.store.guardrails.queries.SizeOfNonPersonalSpacesQuery;
import com.atlassian.migration.agent.store.guardrails.queries.SizeOfTablesPerPageQuery;
import com.atlassian.migration.agent.store.guardrails.queries.SizeOfTablesQuery;
import com.atlassian.migration.agent.store.guardrails.queries.TotalAttachmentsPerPageQuery;
import com.atlassian.migration.agent.store.guardrails.queries.TotalAttachmentsPerSpaceQuery;
import com.atlassian.migration.agent.store.guardrails.queries.TotalAttachmentsQuery;
import com.atlassian.migration.agent.store.guardrails.queries.TotalAttachmentsSizeQuery;
import com.atlassian.migration.agent.store.guardrails.queries.TotalBlogpostsQuery;
import com.atlassian.migration.agent.store.guardrails.queries.TotalPagesPerSpaceQuery;
import com.atlassian.migration.agent.store.guardrails.queries.TotalPagesPerVersionQuery;
import com.atlassian.migration.agent.store.guardrails.queries.TotalPagesQuery;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.atlassian.migration.agent.store.jpa.impl.DialectResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(value={CommonBeanConfiguration.class})
@Configuration
public class GuardrailsBeanConfiguration {
    @Bean
    public ActiveUsersQuery createActiveUsersQuery(EntityManagerTemplate tmpl) {
        return new ActiveUsersQuery(tmpl);
    }

    @Bean
    public AllUsersQuery createAllUsersQuery(EntityManagerTemplate tmpl) {
        return new AllUsersQuery(tmpl);
    }

    @Bean
    public GroupsPerUserQuery createGroupsPerUserQuery(EntityManagerTemplate tmpl) {
        return new GroupsPerUserQuery(tmpl);
    }

    @Bean
    public MaxNumberOfPagesInASpaceQuery maxNumberOfPagesInASpaceQuery(EntityManagerTemplate tmpl) {
        return new MaxNumberOfPagesInASpaceQuery(tmpl);
    }

    @Bean
    public TotalPagesPerSpaceQuery numberOfPagesPerSpaceQuery(EntityManagerTemplate tmpl) {
        return new TotalPagesPerSpaceQuery(tmpl);
    }

    @Bean
    public TotalPagesPerVersionQuery numberOfPagesPerVersionQuery(EntityManagerTemplate tmpl) {
        return new TotalPagesPerVersionQuery(tmpl);
    }

    @Bean
    public TotalPagesQuery totalPagesQuery(EntityManagerTemplate tmpl) {
        return new TotalPagesQuery(tmpl);
    }

    @Bean
    public BaseUsersQuery createBaseUsersQuery(EntityManagerTemplate tmpl) {
        return new BaseUsersQuery(tmpl);
    }

    @Bean
    public InactiveUsersQuery createInactiveUsersQuery(EntityManagerTemplate tmpl) {
        return new InactiveUsersQuery(tmpl);
    }

    @Bean
    public TotalAttachmentsQuery createTotalAttachmentsQuery(EntityManagerTemplate tmpl) {
        return new TotalAttachmentsQuery(tmpl);
    }

    @Bean
    public TotalAttachmentsSizeQuery createTotalAttachmentSizeQuery(EntityManagerTemplate tmpl) {
        return new TotalAttachmentsSizeQuery(tmpl);
    }

    @Bean
    public MembershipPerGroupOverLimitQuery createMembershipPerGroupOverLimitQuery(EntityManagerTemplate tmpl) {
        return new MembershipPerGroupOverLimitQuery(tmpl);
    }

    @Bean
    public MaxLikeInPageQuery createMaxLikeInPageQuery(EntityManagerTemplate tmpl) {
        return new MaxLikeInPageQuery(tmpl);
    }

    @Bean
    public MaxSpaceUserPermissionQuery createMaxSpaceUserPermissionQuery(EntityManagerTemplate tmpl) {
        return new MaxSpaceUserPermissionQuery(tmpl);
    }

    @Bean
    public JiraIssueMacroPerPageTop100Query createJiraIssueMacroTop100Query(EntityManagerTemplate tmpl) {
        return new JiraIssueMacroPerPageTop100Query(tmpl);
    }

    @Bean
    public NumberOfSpacesQuery createNumberOfSpacesQuery(EntityManagerTemplate tmpl) {
        return new NumberOfSpacesQuery(tmpl);
    }

    @Bean
    public NumberOfSpacesPerSpaceTypeQuery createNumberOfSpacesPerSpacetypeQuery(EntityManagerTemplate tmpl) {
        return new NumberOfSpacesPerSpaceTypeQuery(tmpl);
    }

    @Bean
    public NumberOfGroupsQuery createNumberOfGroupsQuery(EntityManagerTemplate tmpl) {
        return new NumberOfGroupsQuery(tmpl);
    }

    @Bean
    public PagesWithRestrictionsQuery createPagesWithRestrictionsQuery(EntityManagerTemplate tmpl) {
        return new PagesWithRestrictionsQuery(tmpl);
    }

    @Bean
    public MaxRestrictionsInPageQuery createMaxRestrictionsInPageQuery(EntityManagerTemplate tmpl) {
        return new MaxRestrictionsInPageQuery(tmpl);
    }

    @Bean
    public MaxCommentsInPageQuery createMaxCommentsInPageQuery(EntityManagerTemplate tmpl) {
        return new MaxCommentsInPageQuery(tmpl);
    }

    @Bean
    public TotalAttachmentsPerPageQuery createTotalAttachmentsPerPageQuery(EntityManagerTemplate tmpl) {
        return new TotalAttachmentsPerPageQuery(tmpl);
    }

    @Bean
    public TotalAttachmentsPerSpaceQuery createTotalAttachmentsPerSpaceQuery(EntityManagerTemplate tmpl) {
        return new TotalAttachmentsPerSpaceQuery(tmpl);
    }

    @Bean
    public CurrentAttPerPageQuery createCurrentAttPerPageQuery(EntityManagerTemplate tmpl) {
        return new CurrentAttPerPageQuery(tmpl);
    }

    @Bean
    public EmbeddedAttPerPageQuery createEmbeddedAttPerPageQuery(EntityManagerTemplate tmpl, DialectResolver dialectResolver) {
        return new EmbeddedAttPerPageQuery(tmpl, dialectResolver);
    }

    @Bean
    public PageDepthQuery createPageDepthQuery(EntityManagerTemplate tmpl, DialectResolver dialectResolver) {
        return new PageDepthQuery(tmpl, dialectResolver);
    }

    @Bean
    public AllGroupsQuery createAllGroupsQuery(EntityManagerTemplate tmpl) {
        return new AllGroupsQuery(tmpl);
    }

    @Bean
    public MaxPageWidthQuery createMaxPageWidthQuery(EntityManagerTemplate tmpl) {
        return new MaxPageWidthQuery(tmpl);
    }

    @Bean
    public MaxNumberOfSpacePermissionsInASpaceQuery createMaxNumberOfSpacePermissionsInASpaceQuery(EntityManagerTemplate tmpl) {
        return new MaxNumberOfSpacePermissionsInASpaceQuery(tmpl);
    }

    @Bean
    public MaxNumberOfSpaceGroupPermissionsInASpaceQuery createMaxNumberOfSpaceGroupPermissionsInASpaceQuery(EntityManagerTemplate tmpl) {
        return new MaxNumberOfSpaceGroupPermissionsInASpaceQuery(tmpl);
    }

    @Bean
    public SizeOfNonPersonalSpacesQuery createSizeOfNonPersonalSpaceQuery(EntityManagerTemplate tmpl) {
        return new SizeOfNonPersonalSpacesQuery(tmpl);
    }

    @Bean
    public MaxSizePersonalSpaceQuery createMaxSizePersonalSpaceQuery(EntityManagerTemplate tmpl) {
        return new MaxSizePersonalSpaceQuery(tmpl);
    }

    @Bean
    public SizeOfTablesQuery createSizeOfTablesQuery(EntityManagerTemplate tmpl, DialectResolver dialectResolver) {
        return new SizeOfTablesQuery(tmpl, dialectResolver);
    }

    @Bean
    public SizeOfDBQuery createSizeOfDBQuery(EntityManagerTemplate tmpl, DialectResolver dialectResolver) {
        return new SizeOfDBQuery(tmpl, dialectResolver);
    }

    @Bean
    public MediaSizeQuery createMediaSizeQuery(EntityManagerTemplate tmpl) {
        return new MediaSizeQuery(tmpl);
    }

    @Bean
    PersonalSpacesForInactiveUsersQuery createPersonalSpacesForInactiveUsersQuery(EntityManagerTemplate tmpl) {
        return new PersonalSpacesForInactiveUsersQuery(tmpl);
    }

    @Bean
    public NumberOfMacrosPerPageQuery createNumberOfMacrosPerPageQuery(EntityManagerTemplate tmpl) {
        return new NumberOfMacrosPerPageQuery(tmpl);
    }

    @Bean
    public ListOfAppsInstalledQuery createListOfAppsInstalledQuery(PluginManager pluginManager) {
        return new ListOfAppsInstalledQuery(pluginManager);
    }

    @Bean
    public NumberOfTablesPerPageQuery createNumberOfTablesPerPageQuery(EntityManagerTemplate tmpl, RecentlyViewedManager recentlyViewedManager, DialectResolver dialectResolver) {
        return new NumberOfTablesPerPageQuery(tmpl, recentlyViewedManager, dialectResolver);
    }

    @Bean
    public SizeOfTablesPerPageQuery createSizeOfTablesPerPageQuery(EntityManagerTemplate tmpl, RecentlyViewedManager recentlyViewedManager, DialectResolver dialectResolver) {
        return new SizeOfTablesPerPageQuery(tmpl, recentlyViewedManager, dialectResolver);
    }

    @Bean
    public NumberOfMediaPerPageQuery createNumberOfMediaPerPageQuery(EntityManagerTemplate tmpl) {
        return new NumberOfMediaPerPageQuery(tmpl);
    }

    @Bean
    public TotalBlogpostsQuery createTotalBlogpostsQuery(EntityManagerTemplate tmpl) {
        return new TotalBlogpostsQuery(tmpl);
    }
}

