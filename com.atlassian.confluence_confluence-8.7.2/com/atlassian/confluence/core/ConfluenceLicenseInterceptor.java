/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.concurrent.Supplier
 *  com.google.common.collect.ImmutableSet
 *  com.opensymphony.xwork2.Action
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.interceptor.Interceptor
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.event.events.analytics.LicenseCheckFailedEvent;
import com.atlassian.confluence.importexport.actions.RestorePageAction;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.pages.actions.AbstractPreviewPageAction;
import com.atlassian.confluence.pages.actions.CopyPageAction;
import com.atlassian.confluence.pages.actions.CreateBlogPostAction;
import com.atlassian.confluence.pages.actions.CreatePageAction;
import com.atlassian.confluence.pages.actions.EditBlogPostAction;
import com.atlassian.confluence.pages.actions.EditPageAction;
import com.atlassian.confluence.pages.actions.MoveAttachmentAction;
import com.atlassian.confluence.pages.actions.MoveBlogPostAction;
import com.atlassian.confluence.pages.actions.MovePageAction;
import com.atlassian.confluence.pages.actions.RemoveAttachedFileAction;
import com.atlassian.confluence.pages.actions.RemoveAttachedFileVersionAction;
import com.atlassian.confluence.pages.actions.RemoveCommentAction;
import com.atlassian.confluence.pages.actions.RemoveHistoricalVersionAction;
import com.atlassian.confluence.pages.actions.RemovePageAction;
import com.atlassian.confluence.spaces.actions.AbstractCreateSpaceAction;
import com.atlassian.confluence.spaces.actions.CreatePersonalSpaceAction;
import com.atlassian.confluence.spaces.actions.EditSpaceAction;
import com.atlassian.confluence.spaces.actions.ImportPagesAction;
import com.atlassian.confluence.spaces.actions.SpaceAvailableAction;
import com.atlassian.confluence.user.actions.SignUpAction;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.Supplier;
import com.google.common.collect.ImmutableSet;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class ConfluenceLicenseInterceptor
implements Interceptor {
    private final Supplier<EventPublisher> eventPublisher = new LazyComponentReference("eventPublisher");
    private final Supplier<UserChecker> userChecker = new LazyComponentReference("userChecker");
    private final Supplier<LicenseService> licenseService = new LazyComponentReference("licenseService");
    private static final String EDITOR_ACTION_CLASSNAME = "com.atlassian.confluence.plugins.editorloader.EditorAction";
    private static final String LICENSE_EXPIRED_REASON = "licenseExpired";
    private static final String LICENSE_USERS_EXCEEDED_REASON = "licenseUsersExceeded";

    public void destroy() {
    }

    public void init() {
    }

    public String intercept(ActionInvocation actionInvocation) throws Exception {
        if (actionInvocation.getAction() instanceof ConfluenceActionSupport && this.shouldCheckForLicense(actionInvocation)) {
            if (((LicenseService)this.licenseService.get()).retrieve().isExpired()) {
                this.publishAnalyticsEvent(LICENSE_EXPIRED_REASON, actionInvocation);
                return "licenseexpired";
            }
            if (this.getUserChecker() != null && this.getUserChecker().hasTooManyUsers()) {
                this.publishAnalyticsEvent(LICENSE_USERS_EXCEEDED_REASON, actionInvocation);
                return "licenseusersexceeded";
            }
        }
        return actionInvocation.invoke();
    }

    private UserChecker getUserChecker() {
        if (!ContainerManager.isContainerSetup()) {
            return null;
        }
        return (UserChecker)this.userChecker.get();
    }

    private EventPublisher getEventPublisher() {
        if (!ContainerManager.isContainerSetup()) {
            return null;
        }
        return (EventPublisher)this.eventPublisher.get();
    }

    private boolean shouldCheckForLicense(ActionInvocation actionInvocation) {
        ImmutableSet licensedActions = ImmutableSet.of(SpaceAvailableAction.class, CreatePersonalSpaceAction.class, AbstractCreateSpaceAction.class, RemoveCommentAction.class, AbstractPreviewPageAction.class, CopyPageAction.class, (Object[])new Class[]{RestorePageAction.class, SignUpAction.class, CreatePageAction.class, EditPageAction.class, CreateBlogPostAction.class, EditBlogPostAction.class, EditSpaceAction.class, ImportPagesAction.class, MoveAttachmentAction.class, MoveBlogPostAction.class, MovePageAction.class, RemoveAttachedFileAction.class, RemoveHistoricalVersionAction.class, RemoveAttachedFileVersionAction.class, RemovePageAction.class});
        Action action = (Action)actionInvocation.getAction();
        return GeneralUtil.isSetupComplete() && licensedActions.stream().anyMatch(clazz -> clazz.isInstance(action));
    }

    private void publishAnalyticsEvent(String reason, ActionInvocation actionInvocation) {
        String actionClassName = actionInvocation.getAction().getClass().getName();
        if (this.shouldPublishAnalyticsEvent(actionClassName)) {
            this.getEventPublisher().publish((Object)new LicenseCheckFailedEvent(reason, actionClassName));
        }
    }

    private boolean shouldPublishAnalyticsEvent(String actionClassName) {
        return this.getEventPublisher() != null && !actionClassName.equals(EDITOR_ACTION_CLASSNAME);
    }
}

