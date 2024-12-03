/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.search.v2.ContentPermissionCalculator
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.util;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.search.v2.ContentPermissionCalculator;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Property;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalendarHelper {
    private final ContentPermissionCalculator contentPermissionCalculator;

    @Autowired
    public CalendarHelper(@ComponentImport ContentPermissionCalculator contentPermissionCalculator) {
        this.contentPermissionCalculator = contentPermissionCalculator;
    }

    public ConfluenceUser getUser(Property property, UserAccessor userAccessor) {
        Object userKeyParam = property.getParameter("X-CONFLUENCE-USER-KEY");
        if (userKeyParam != null) {
            String userKey = ((Content)userKeyParam).getValue();
            return userAccessor.getUserByKey(new UserKey(userKey));
        }
        return null;
    }

    public Map<String, Set<String>> parseURLParamJira(String urlParam) {
        HashMap<String, Set<String>> queryParams = new HashMap<String, Set<String>>();
        for (String queryParamPairs : StringUtils.split((String)urlParam, (String)"&?")) {
            int idx = queryParamPairs.indexOf("=");
            if (idx <= 0) continue;
            String paramName = GeneralUtil.urlDecode((String)queryParamPairs.substring(0, idx));
            Set<String> paramValues = queryParams.containsKey(paramName) ? (Set)queryParams.get(paramName) : new HashSet();
            paramValues.add(GeneralUtil.urlDecode((String)queryParamPairs.substring(idx + 1)));
            queryParams.put(paramName, paramValues);
        }
        return queryParams;
    }

    public String getEncodedCredentialsAsString(PersistedSubCalendar persistedSubCalendar, List<String> viewSpacePermittedUserList, List<String> viewSpacePermittedGroupList) {
        StringBuilder result = new StringBuilder(500);
        String calendarPermissions = this.getEncodedCalendarCredentialsAsString(persistedSubCalendar);
        String spacePermissions = this.getEncodedSpaceCredentialsAsString(viewSpacePermittedUserList, viewSpacePermittedGroupList);
        result.append(calendarPermissions);
        if (!spacePermissions.equals("") && !calendarPermissions.equals("")) {
            result.append("&");
        }
        result.append(spacePermissions);
        return result.toString();
    }

    public String getEncodedCalendarCredentialsAsString(PersistedSubCalendar persistedSubCalendar) {
        List<String> viewPermittedGroupList = persistedSubCalendar.getGroupRestrictionMap().get("VIEW");
        List<String> viewPermittedUserList = persistedSubCalendar.getUserRestrictionMap().get("VIEW");
        return Stream.concat(viewPermittedGroupList.stream().map(arg_0 -> ((ContentPermissionCalculator)this.contentPermissionCalculator).getEncodedGroupName(arg_0)), viewPermittedUserList.stream().map(user -> this.contentPermissionCalculator.getEncodedUserKey((ConfluenceUser)new StaticDummyConfluenceUser((String)user)))).collect(Collectors.joining("|"));
    }

    public String getEncodedSpaceCredentialsAsString(List<String> viewSpacePermittedUserList, List<String> viewSpacePermittedGroupList) {
        return Stream.concat(viewSpacePermittedGroupList.stream().map(arg_0 -> ((ContentPermissionCalculator)this.contentPermissionCalculator).getEncodedGroupName(arg_0)), viewSpacePermittedUserList.stream().map(user -> this.contentPermissionCalculator.getEncodedUserKey((ConfluenceUser)new StaticDummyConfluenceUser((String)user)))).collect(Collectors.joining("|"));
    }

    @VisibleForTesting
    static class StaticDummyConfluenceUser
    implements ConfluenceUser {
        private final UserKey userKey;

        public StaticDummyConfluenceUser(String userKey) {
            this.userKey = new UserKey(userKey);
        }

        public UserKey getKey() {
            return this.userKey;
        }

        public String getFullName() {
            return null;
        }

        public String getEmail() {
            return null;
        }

        public String getName() {
            return null;
        }
    }
}

