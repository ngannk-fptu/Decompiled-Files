/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.xwork.HttpMethod
 *  com.google.common.collect.ImmutableSet
 *  com.opensymphony.xwork2.Action
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.config.entities.ActionConfig
 *  com.opensymphony.xwork2.interceptor.Interceptor
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.security.actions;

import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.internal.accessmode.ThreadLocalReadOnlyAccessCacheInternal;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.xwork.StrutsActionHelper;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.xwork.HttpMethod;
import com.google.common.collect.ImmutableSet;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.interceptor.Interceptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermissionCheckInterceptor
implements Interceptor {
    private static final Logger log = LoggerFactory.getLogger(PermissionCheckInterceptor.class);
    public static final String NOT_PERMITTED = "notpermitted";
    private static final String NOT_PERMITTED_PERSONAL_SPACE = "notpermittedpersonal";
    public static final String PAGE_NOT_PERMITTED = "pagenotpermitted";
    public static final String NOT_FOUND = "notfound";
    public static final String READ_ONLY = "readonly";
    private static final Set<HttpMethod> MUTATIVE_HTTP_METHODS = ImmutableSet.of((Object)HttpMethod.POST, (Object)HttpMethod.PUT, (Object)HttpMethod.DELETE);
    private static final String READ_ONLY_ACCESS_ALLOWED_ANNOTATION = "ReadOnlyAccessAllowed";
    private static final String READ_ONLY_ACCESS_BLOCKED_ANNOTATION = "ReadOnlyAccessBlocked";
    private AccessModeService accessModeService;

    public void destroy() {
    }

    public void init() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        Action action = (Action)actionInvocation.getAction();
        try {
            if (action instanceof ConfluenceActionSupport) {
                ConfluenceActionSupport confluenceAction = (ConfluenceActionSupport)action;
                Class<?> actionClass = actionInvocation.getAction().getClass();
                Package actionClassPackage = actionClass.getPackage();
                if (ContainerManager.isContainerSetup() && this.getAccessModeService().isReadOnlyAccessModeEnabled()) {
                    if (this.isAnnotated(actionClassPackage, READ_ONLY_ACCESS_BLOCKED_ANNOTATION) || this.isAnnotated(actionClass, READ_ONLY_ACCESS_BLOCKED_ANNOTATION)) {
                        String string = READ_ONLY;
                        return string;
                    }
                    Method method = this.getMethod(actionInvocation, actionClass);
                    if (this.isAnnotated(method, READ_ONLY_ACCESS_BLOCKED_ANNOTATION)) {
                        String string = READ_ONLY;
                        return string;
                    }
                    if (this.isReadOnlyAccessAllowed(actionInvocation, actionClassPackage, actionClass)) {
                        ThreadLocalReadOnlyAccessCacheInternal.enableReadOnlyAccessExemption();
                    }
                }
                if (!confluenceAction.isPermitted()) {
                    log.debug("Not permitted to execute action of class {} ", confluenceAction.getClass());
                    HttpServletRequest request = ServletActionContext.getRequest();
                    if (this.getAccessModeService().isReadOnlyAccessModeEnabled() && this.isMutativeHttpMethod(request.getMethod())) {
                        String string = ThreadLocalReadOnlyAccessCacheInternal.hasReadOnlyAccessExemption() ? actionInvocation.invoke() : READ_ONLY;
                        return string;
                    }
                    if (confluenceAction instanceof Spaced && ((Spaced)((Object)confluenceAction)).getSpace() != null && ((Spaced)((Object)confluenceAction)).getSpace().isPersonal()) {
                        String string = NOT_PERMITTED_PERSONAL_SPACE;
                        return string;
                    }
                    String string = NOT_PERMITTED;
                    return string;
                }
            }
            String string = actionInvocation.invoke();
            return string;
        }
        finally {
            ThreadLocalReadOnlyAccessCacheInternal.disableReadOnlyAccessExemption();
        }
    }

    private boolean isReadOnlyAccessAllowed(ActionInvocation actionInvocation, Package actionClassPackage, Class<?> actionClass) {
        Method method = this.getMethod(actionInvocation, actionClass);
        return StringUtils.startsWith((CharSequence)actionInvocation.getProxy().getNamespace(), (CharSequence)"/admin") || this.isAnnotated(actionClassPackage, READ_ONLY_ACCESS_ALLOWED_ANNOTATION) || this.isAnnotated(actionClass, READ_ONLY_ACCESS_ALLOWED_ANNOTATION) || this.isAnnotated(method, READ_ONLY_ACCESS_ALLOWED_ANNOTATION);
    }

    private Method getMethod(ActionInvocation actionInvocation, Class<?> actionClass) {
        ActionConfig actionConfig = actionInvocation.getProxy().getConfig();
        return StrutsActionHelper.getActionClassMethod(actionClass, actionConfig.getMethodName());
    }

    private boolean isMutativeHttpMethod(String currentHttpMethod) {
        for (HttpMethod method : MUTATIVE_HTTP_METHODS) {
            if (!method.matches(currentHttpMethod)) continue;
            return true;
        }
        return false;
    }

    private AccessModeService getAccessModeService() {
        if (this.accessModeService == null) {
            this.accessModeService = (AccessModeService)ContainerManager.getComponent((String)"accessModeService");
        }
        return this.accessModeService;
    }

    private boolean isAnnotated(@NonNull AnnotatedElement annotatedElement, @NonNull String simpleAnnotationName) {
        Annotation[] annotations = annotatedElement.getDeclaredAnnotations();
        if (annotations != null && annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (!simpleAnnotationName.equals(annotation.annotationType().getSimpleName())) continue;
                return true;
            }
        }
        return false;
    }
}

