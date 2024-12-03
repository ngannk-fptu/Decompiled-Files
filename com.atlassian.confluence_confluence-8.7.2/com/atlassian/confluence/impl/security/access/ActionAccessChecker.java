/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Option
 *  com.atlassian.fugue.Pair
 *  com.atlassian.util.concurrent.Lazy
 *  com.atlassian.util.concurrent.Supplier
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.security.access;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.event.events.security.NoConfluencePermissionEvent;
import com.atlassian.confluence.security.access.AccessStatus;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.security.access.annotations.PublicAccess;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.confluence.security.access.annotations.RequiresLicensedConfluenceAccess;
import com.atlassian.confluence.security.access.annotations.RequiresLicensedOrAnonymousConfluenceAccess;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.xwork.StrutsActionHelper;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Option;
import com.atlassian.fugue.Pair;
import com.atlassian.util.concurrent.Lazy;
import com.atlassian.util.concurrent.Supplier;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
public class ActionAccessChecker {
    private static final String ANNOTATED_ELEMENTS_CACHE_NAME = ActionAccessChecker.class.getName() + ".annotatedElements";
    private final ConfluenceAccessManager confluenceAccessManager;
    private final EventPublisher eventPublisher;
    private final Supplier<Cache<Pair<Class<?>, Option<String>>, List<AnnotatedElement>>> cacheRef;
    private static final Map<Class<? extends Annotation>, Predicate<AccessStatus>> POSITIVE_ACCESS_CHECKS = ImmutableMap.builder().put(PublicAccess.class, accessStatus -> true).put(RequiresAnyConfluenceAccess.class, accessStatus -> accessStatus.canUseConfluence()).put(RequiresLicensedConfluenceAccess.class, accessStatus -> accessStatus.hasLicensedAccess()).put(RequiresLicensedOrAnonymousConfluenceAccess.class, accessStatus -> accessStatus.hasLicensedAccess() || accessStatus.hasAnonymousAccess()).build();

    public ActionAccessChecker(ConfluenceAccessManager confluenceAccessManager, EventPublisher eventPublisher, CacheFactory cacheFactory) {
        this.confluenceAccessManager = confluenceAccessManager;
        this.eventPublisher = eventPublisher;
        this.cacheRef = Lazy.supplier(() -> cacheFactory.getCache(ANNOTATED_ELEMENTS_CACHE_NAME, null, new CacheSettingsBuilder().local().build()));
    }

    public boolean isAccessPermitted(Object action, @Nullable String methodName) {
        if (action instanceof ConfluenceActionSupport) {
            ConfluenceActionSupport actionSupport = (ConfluenceActionSupport)action;
            ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
            AccessDecision decision = this.checkUserAccessFromAnnotations(action.getClass(), methodName, currentUser);
            switch (decision) {
                case GRANTED: {
                    actionSupport.useSkipAccessCheck(true);
                    return true;
                }
                case DENIED: {
                    this.eventPublisher.publish((Object)new NoConfluencePermissionEvent(this));
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    private AccessDecision checkUserAccessFromAnnotations(Class<?> actionClass, @Nullable String methodName, ConfluenceUser currentUser) {
        Supplier accessStatusSupplier = Lazy.supplier(() -> this.confluenceAccessManager.getUserAccessStatus(currentUser));
        for (AnnotatedElement annotatedElement : this.getOrderedAnnotatedElements(actionClass, methodName)) {
            AccessDecision currentAccessDecision = ActionAccessChecker.checkAccessAnnotations(annotatedElement, (Supplier<AccessStatus>)accessStatusSupplier);
            if (currentAccessDecision != AccessDecision.GRANTED && currentAccessDecision != AccessDecision.DENIED) continue;
            return currentAccessDecision;
        }
        return AccessDecision.ABSTAIN;
    }

    private List<AnnotatedElement> getOrderedAnnotatedElements(Class<?> actionClass, @Nullable String methodName) {
        return (List)((Cache)this.cacheRef.get()).get((Object)Pair.pair(actionClass, (Object)Option.option((Object)methodName)), () -> ActionAccessChecker.calculateOrderedAnnotatedElements(actionClass, methodName));
    }

    @VisibleForTesting
    static List<AnnotatedElement> calculateOrderedAnnotatedElements(Class<?> actionClass, @Nullable String methodName) {
        Package actionClassPackage = actionClass.getPackage();
        Method actionMethod = StrutsActionHelper.getActionClassMethod(actionClass, methodName);
        Class<?> actionClassDeclaringMethod = actionMethod.getDeclaringClass();
        ImmutableList.Builder resultBuilder = ImmutableList.builder();
        if (actionClass.equals(actionClassDeclaringMethod)) {
            resultBuilder.add((Object)actionMethod);
        }
        resultBuilder.add(actionClass);
        if (actionClassPackage != null) {
            resultBuilder.add((Object)actionClassPackage);
        }
        return resultBuilder.build();
    }

    @VisibleForTesting
    static AccessDecision checkAccessAnnotations(AnnotatedElement annotatedElement, Supplier<AccessStatus> accessStatusSupplier) {
        boolean foundAccessCheckAnnotation = false;
        for (Map.Entry<Class<? extends Annotation>, Predicate<AccessStatus>> accessCheckAnnotationEntry : POSITIVE_ACCESS_CHECKS.entrySet()) {
            if (!annotatedElement.isAnnotationPresent(accessCheckAnnotationEntry.getKey())) continue;
            foundAccessCheckAnnotation = true;
            Predicate<AccessStatus> accessCheckPredicate = accessCheckAnnotationEntry.getValue();
            if (!accessCheckPredicate.apply((Object)((AccessStatus)accessStatusSupplier.get()))) continue;
            return AccessDecision.GRANTED;
        }
        if (foundAccessCheckAnnotation) {
            return AccessDecision.DENIED;
        }
        return AccessDecision.ABSTAIN;
    }

    @VisibleForTesting
    protected static enum AccessDecision {
        GRANTED,
        DENIED,
        ABSTAIN;

    }
}

