/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.content.AttachmentService$AttachmentFinder
 *  com.atlassian.confluence.api.service.content.ChildContentService$ChildContentFinder
 *  com.atlassian.confluence.api.service.content.ContentService$ContentFinder
 *  com.atlassian.confluence.api.service.content.ContentVersionService$VersionFinder
 *  com.atlassian.confluence.api.service.content.SpaceService$SpaceContentFinder
 *  com.atlassian.confluence.api.service.content.SpaceService$SpaceFinder
 *  com.atlassian.confluence.api.service.people.GroupService$GroupFinder
 *  com.atlassian.confluence.api.service.people.PersonService$PersonFinder
 *  com.atlassian.confluence.api.service.people.PersonService$PersonSearcher
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.aop.Advisor
 */
package com.atlassian.confluence.api.impl.service.content.finder;

import com.atlassian.confluence.api.impl.service.content.finder.FinderProxyFactory;
import com.atlassian.confluence.api.service.content.AttachmentService;
import com.atlassian.confluence.api.service.content.ChildContentService;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.content.ContentVersionService;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.api.service.people.GroupService;
import com.atlassian.confluence.api.service.people.PersonService;
import com.atlassian.confluence.impl.service.finder.content.NoopContentFinder;
import com.atlassian.confluence.impl.service.finder.content.NoopSpaceFinder;
import com.atlassian.confluence.impl.service.finder.people.NoopGroupFinder;
import com.atlassian.confluence.impl.service.finder.people.NoopPersonFinder;
import com.atlassian.confluence.impl.service.finder.people.NoopPersonSearcher;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.AopUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Advisor;

public class FinderProxyFactoryImpl
implements FinderProxyFactory {
    private static final Logger log = LoggerFactory.getLogger(FinderProxyFactoryImpl.class);
    private static final Map<Class, Object> NOOP_FINDERS = ImmutableMap.builder().put(ContentService.ContentFinder.class, (Object)new NoopContentFinder()).put(SpaceService.SpaceFinder.class, (Object)new NoopSpaceFinder()).put(GroupService.GroupFinder.class, (Object)new NoopGroupFinder()).put(PersonService.PersonFinder.class, (Object)new NoopPersonFinder()).put(PersonService.PersonSearcher.class, (Object)new NoopPersonSearcher()).build();
    private static final Set<Class> NOOP_FINDERS_EXEMPTED = ImmutableSet.of(AttachmentService.AttachmentFinder.class, ChildContentService.ChildContentFinder.class, SpaceService.SpaceContentFinder.class, ContentVersionService.VersionFinder.class);
    private final List<Advisor> advisors;
    private final ConfluenceAccessManager confluenceAccessManager;

    public FinderProxyFactoryImpl(List<Advisor> advisors, ConfluenceAccessManager confluenceAccessManager) {
        this.advisors = Objects.requireNonNull(advisors);
        this.confluenceAccessManager = Objects.requireNonNull(confluenceAccessManager);
    }

    @Override
    public <T> T createProxy(T target, Class<T> targetClass) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (!this.confluenceAccessManager.getUserAccessStatus(currentUser).canUseConfluence()) {
            Object noopFinder = NOOP_FINDERS.get(targetClass);
            if (noopFinder != null) {
                if (log.isDebugEnabled()) {
                    log.debug("User {} does not have permission to use Confluence, returning a no-op Finder", (Object)(currentUser == null ? "Anonymous" : currentUser.getKey()));
                }
                return AopUtils.createAdvisedProxy(noopFinder, targetClass, this.advisors);
            }
            if (!NOOP_FINDERS_EXEMPTED.contains(targetClass)) {
                throw new IllegalArgumentException("Finder class has no no-op implementation:" + targetClass.getName());
            }
        }
        return AopUtils.createAdvisedProxy(target, targetClass, this.advisors);
    }
}

