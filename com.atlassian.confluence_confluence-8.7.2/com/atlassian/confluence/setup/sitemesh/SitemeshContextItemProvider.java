/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.profiling.UtilTimerStack
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 *  org.springframework.util.Assert
 */
package com.atlassian.confluence.setup.sitemesh;

import com.atlassian.confluence.setup.velocity.VelocityContextItemProvider;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.profiling.UtilTimerStack;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.util.Assert;

public final class SitemeshContextItemProvider
implements VelocityContextItemProvider {
    private static final String USER_KEY = "user";
    private static final String USER_HISTORY_KEY = "userHistory";
    private static final String UTIL_TIMER_STACK = "utilTimerStack";
    private static final String USER_AVATAR_URI_REFERENCE_KEY = "userAvatarUriReference";
    private static final UtilTimerStack UTIL_TIMER_STACK_INSTANCE = new UtilTimerStack();
    private final Map<String, Object> contextMap;
    private static final LazyComponentReference<UserAccessor> userAccessor = new LazyComponentReference("userAccessor");

    public static VelocityContextItemProvider getProvider(HttpServletRequest request) {
        return new SitemeshContextItemProvider(request);
    }

    private SitemeshContextItemProvider(HttpServletRequest request) {
        Assert.notNull((Object)request, (String)"request must not be null");
        this.contextMap = SitemeshContextItemProvider.getMap(request);
    }

    private static Map<String, Object> getMap(HttpServletRequest request) {
        if (!GeneralUtil.isSetupComplete()) {
            return Collections.emptyMap();
        }
        HashMap<String, Object> contextMap = new HashMap<String, Object>(4);
        ConfluenceUser currentUser = SitemeshContextItemProvider.getCurrentUser(request);
        contextMap.put(USER_KEY, currentUser);
        contextMap.put(USER_AVATAR_URI_REFERENCE_KEY, SitemeshContextItemProvider.getUserProfilePicture(currentUser).getUriReference());
        HttpSession session = request.getSession();
        if (session.getAttribute("confluence.user.history") != null) {
            contextMap.put(USER_HISTORY_KEY, session.getAttribute("confluence.user.history"));
        }
        contextMap.put(UTIL_TIMER_STACK, UTIL_TIMER_STACK_INSTANCE);
        return Collections.unmodifiableMap(contextMap);
    }

    @Override
    public Map<String, Object> getContextMap() {
        return this.contextMap;
    }

    private static ConfluenceUser getCurrentUser(HttpServletRequest request) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user == null && ContainerManager.isContainerSetup()) {
            user = SitemeshContextItemProvider.getUserAccessor().getUserByName(request.getRemoteUser());
        }
        return user;
    }

    private static ProfilePictureInfo getUserProfilePicture(ConfluenceUser currentUser) {
        return SitemeshContextItemProvider.getUserAccessor().getUserProfilePicture(currentUser);
    }

    private static UserAccessor getUserAccessor() {
        return (UserAccessor)userAccessor.get();
    }
}

