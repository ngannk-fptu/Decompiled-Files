/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  javax.servlet.http.HttpSession
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserInterfaceState {
    private static final Logger log = LoggerFactory.getLogger(UserInterfaceState.class);
    public static final int DEFAULT_MAX_RECENTLY_UPDATED_PAGE_COUNT = 20;
    public static final int LIMIT_MAX_RECENTLY_UPDATED_PAGE_COUNT = 50;
    public static final int STEP_SIZE_RECENTLY_UPDATED_PAGE_COUNT = 10;
    public static final int LIMIT_MIN_RECENTLY_UPDATED_PAGE_COUNT = 10;
    private User user;
    private Boolean attachmentsShowing;
    private Boolean childrenShowing;
    private int recentChangesSize;
    private UserAccessor userAccessor;

    public UserInterfaceState(User user) {
        this.user = user;
        this.userAccessor = this.getUserAccessor();
    }

    private UserAccessor getUserAccessor() {
        if (this.userAccessor == null) {
            this.userAccessor = (UserAccessor)ContainerManager.getComponent((String)"userAccessor");
        }
        return this.userAccessor;
    }

    public UserInterfaceState(User user, UserAccessor userAccessor) {
        this.user = user;
        this.userAccessor = userAccessor;
    }

    public Boolean getAttachmentsShowing() {
        if (this.attachmentsShowing == null) {
            this.attachmentsShowing = this.getBooleanFromSessionOrUserPrefs("confluence.show.attachments", "confluence.user.runtime.show-attachments");
        }
        return this.attachmentsShowing;
    }

    public void setAttachmentsShowing(Boolean attachmentsShowing) {
        this.setBooleanInSessionAndUserPrefs(attachmentsShowing, "confluence.show.attachments", "confluence.user.runtime.show-attachments");
    }

    public Boolean getChildrenShowing() {
        if (this.childrenShowing == null) {
            this.childrenShowing = this.getBooleanFromSessionOrUserPrefs("confluence.show.children", "confluence.user.runtime.show-children");
        }
        return this.childrenShowing;
    }

    public void setChildrenShowing(Boolean childrenShowing) {
        this.setBooleanInSessionAndUserPrefs(childrenShowing, "confluence.show.children", "confluence.user.runtime.show-children");
    }

    public void setRecentChangesSize(int recentChangesSize) {
        this.setIntInSessionAndUserPrefs(recentChangesSize, "confluence.recentchanges.size", "confluence.user.runtime.recent-changes.size");
    }

    public void setDashboardSpacesTab(String dashboardTab) {
        this.setStringInSessionAndUserPrefs(dashboardTab, "confluence.dashboard.spaces.selected.tab", "confluence.user.dashboard.spaces.selected.tab");
    }

    public String getDashboardSpacesTab() {
        return this.getStringFromSessionOrUserPrefs("confluence.dashboard.spaces.selected.tab", "confluence.user.dashboard.spaces.selected.tab");
    }

    public void setDashboardSpacesSelectedTeam(String selectedTeam) {
        this.setStringInSessionAndUserPrefs(selectedTeam, "confluence.dashboard.spaces.selected.team", "confluence.user.dashboard.spaces.selected.team");
    }

    public String getDashboardSpacesSelectedTeam() {
        return this.getStringFromSessionOrUserPrefs("confluence.dashboard.spaces.selected.team", "confluence.user.dashboard.spaces.selected.team");
    }

    public int getRecentChangesSize() {
        if (this.recentChangesSize == 0) {
            this.recentChangesSize = this.getIntegerFromSessionOrUserPrefs("confluence.recentchanges.size", "confluence.user.runtime.recent-changes.size");
        }
        return this.recentChangesSize;
    }

    private void setBooleanInSessionAndUserPrefs(Boolean value, String sessionKey, String userPrefsKey) {
        this.putInSession(sessionKey, value);
        this.setBooleanUserPref(userPrefsKey, value);
    }

    private Boolean getBooleanFromSessionOrUserPrefs(String sessionKey, String userPrefsKey) {
        Boolean b = (Boolean)this.getFromSession(sessionKey);
        if (b == null) {
            b = this.user != null ? Boolean.valueOf(this.userAccessor.getPropertySet(this.user).getBoolean(userPrefsKey)) : Boolean.TRUE;
        }
        return b;
    }

    private void setBooleanUserPref(String key, Boolean value) {
        if (this.user != null) {
            this.userAccessor.getPropertySet(this.user).setBoolean(key, value.booleanValue());
        }
    }

    private void setIntInSessionAndUserPrefs(int value, String sessionKey, String userPrefsKey) {
        this.putInSession(sessionKey, value);
        this.setIntegerUserPref(userPrefsKey, value);
    }

    private int getIntegerFromSessionOrUserPrefs(String sessionKey, String userPrefsKey) {
        Integer i = (Integer)this.getFromSession(sessionKey);
        if (i == null && this.user != null) {
            return this.userAccessor.getPropertySet(this.user).getInt(userPrefsKey);
        }
        return i == null ? 0 : i;
    }

    private void setIntegerUserPref(String key, int value) {
        if (this.user != null) {
            this.userAccessor.getPropertySet(this.user).setInt(key, value);
        }
    }

    private String getStringFromSessionOrUserPrefs(String sessionKey, String userPrefsKey) {
        String s = (String)this.getFromSession(sessionKey);
        if (s == null && this.user != null) {
            return this.userAccessor.getPropertySet(this.user).getString(userPrefsKey);
        }
        return s;
    }

    private void setStringUserPref(String key, String value) {
        if (this.user != null) {
            this.userAccessor.getPropertySet(this.user).setString(key, value);
        }
    }

    private void setStringInSessionAndUserPrefs(String value, String sessionKey, String userPrefsKey) {
        this.putInSession(sessionKey, value);
        this.setStringUserPref(userPrefsKey, value);
    }

    private Object getFromSession(String sessionKey) {
        HttpSession session = ServletContextThreadLocal.getRequest().getSession();
        if (session != null) {
            return session.getAttribute(sessionKey);
        }
        log.info("Unable to retrieve preference {} from session: session not available", (Object)sessionKey);
        return null;
    }

    private void putInSession(String sessionKey, Object value) {
        HttpSession session = ServletContextThreadLocal.getRequest().getSession();
        if (session != null) {
            session.setAttribute(sessionKey, value);
        } else {
            log.warn("Unable to save preference {} in session: session not available", (Object)sessionKey);
        }
    }

    public int getMaxRecentChangesSize() {
        return this.ensureValidMaxRecentChangesSize(this.getRecentChangesSize());
    }

    public void setMaxRecentChangesSize(int i) {
        int sanitisedMaxChangesSize = this.ensureValidMaxRecentChangesSize(i);
        if (sanitisedMaxChangesSize == i) {
            this.setRecentChangesSize(i);
        }
    }

    private int ensureValidMaxRecentChangesSize(int i) {
        if (i < 10) {
            i = 20;
        } else if (i > 50) {
            i = 20;
        }
        return this.roundToNearest(i, 10);
    }

    private int roundToNearest(int count, int stepSize) {
        int remainder = count % stepSize;
        return Math.max(count - remainder, stepSize);
    }
}

