/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  javax.ws.rs.WebApplicationException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.rest.validators.event.impl;

import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.rest.AbstractResource;
import com.atlassian.confluence.extra.calendar3.rest.param.UpdateEventParam;
import com.atlassian.confluence.extra.calendar3.rest.validators.event.AbstractEventValidator;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.WebApplicationException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PersonFieldValidator
extends AbstractEventValidator {
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractResource.class);
    public static final String ERROR_FIELD_WHO = "who";
    public static final String ERROR_FIELD_INVALID_USER = "invaliduser";
    private UserAccessor userAccessor;
    private CalendarManager calendarManager;

    @Autowired
    public PersonFieldValidator(@ComponentImport LocaleManager localeManager, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport UserAccessor userAccessor, CalendarManager calendarManager) {
        super(localeManager, i18NBeanFactory);
        this.userAccessor = userAccessor;
        this.calendarManager = calendarManager;
    }

    @Override
    public boolean isValid(UpdateEventParam param, Map<String, List<String>> fieldErrors) throws WebApplicationException {
        String eventType = param.getEventType();
        List<String> personIdList = param.getPerson();
        if ((personIdList == null || personIdList.isEmpty()) && this.isPersonRequiredForEventType(eventType)) {
            this.addFieldError(fieldErrors, ERROR_FIELD_WHO, this.getText("calendar3.error.usernotselected"));
        } else {
            List<String> existingUserIds = this.calendarManager.getEventInviteeUserIds(param.getUid());
            HashSet<String> invalidUsersSet = new HashSet<String>();
            for (String personId : personIdList) {
                ConfluenceUser userById = this.getUserById(personId);
                if (userById == null) {
                    invalidUsersSet.add(personId);
                    continue;
                }
                if (!this.userAccessor.isDeactivated((User)userById) || existingUserIds.contains(personId)) continue;
                invalidUsersSet.add(personId);
            }
            if (!invalidUsersSet.isEmpty()) {
                if (param.getConfirmRemoveInvalidUsers()) {
                    LOG.debug("Confirmed removal of invalid users on event", invalidUsersSet);
                    List<String> newList = personIdList.stream().filter(person -> !invalidUsersSet.contains(person)).collect(Collectors.toList());
                    if (this.isPersonRequiredForEventType(eventType) && newList.isEmpty()) {
                        this.addFieldError(fieldErrors, ERROR_FIELD_WHO, this.getText("calendar3.error.usernotselected"));
                    }
                    param.setPerson(newList);
                } else {
                    this.addFieldError(fieldErrors, ERROR_FIELD_INVALID_USER, StringUtils.join(invalidUsersSet, ", "));
                }
            }
        }
        return true;
    }

    private ConfluenceUser getUserById(String userId) {
        return this.userAccessor.getUserByKey(new UserKey(userId));
    }

    private boolean isPersonRequiredForEventType(String eventType) {
        return !"other".equals(eventType) && !"custom".equals(eventType) && !this.isJiraEvent(eventType);
    }

    private boolean isJiraEvent(String type) {
        Object[] jiraCalendarTypes = new String[]{"jira-agile-sprint", "jira", "jira-project-releases"};
        return ArrayUtils.contains(jiraCalendarTypes, type);
    }
}

