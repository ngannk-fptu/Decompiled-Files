/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.HtmlUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.util;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.HtmlUtil;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.XParameter;
import net.fortuna.ical4j.model.property.XProperty;
import net.fortuna.ical4j.transform.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserKeyMigratorTransformer
implements Transformer<Calendar> {
    private static final Logger LOG = LoggerFactory.getLogger(UserKeyMigratorTransformer.class);
    public static final String PROPERTY_MIGRATED_FOR_USER_KEY = "X-MIGRATED-FOR-USER-KEY";
    private final String baseUrl;
    private final UserAccessor userAccessor;

    public UserKeyMigratorTransformer(String baseUrl, UserAccessor userAccessor) {
        this.baseUrl = baseUrl;
        this.userAccessor = userAccessor;
    }

    @Override
    public Calendar transform(Calendar calendar) {
        ComponentList events = calendar.getComponents("VEVENT");
        if (!events.isEmpty()) {
            for (VEvent anEvent : events) {
                PropertyList organiserProperties;
                PropertyList<Property> eventProperties = anEvent.getProperties();
                PropertyList attendeeProperties = eventProperties.getProperties("ATTENDEE");
                if (!attendeeProperties.isEmpty()) {
                    for (Property attendeeProperty : attendeeProperties) {
                        ParameterList attendeePropertyParameters = attendeeProperty.getParameters();
                        Object confluenceUserParameter = attendeePropertyParameters.getParameter("X-CONFLUENCE-USER");
                        if (confluenceUserParameter == null) continue;
                        eventProperties.remove(attendeeProperty);
                        this.addProperty(anEvent, "ATTENDEE", ((Content)confluenceUserParameter).getValue());
                    }
                }
                if ((organiserProperties = eventProperties.getProperties("ORGANIZER")).isEmpty()) continue;
                for (Property organiserProperty : organiserProperties) {
                    ParameterList organiserPropertyParameters = organiserProperty.getParameters();
                    Object confluenceUserParameter = organiserPropertyParameters.getParameter("X-CONFLUENCE-USER");
                    if (confluenceUserParameter == null) continue;
                    eventProperties.remove(organiserProperty);
                    this.addProperty(anEvent, "ORGANIZER", ((Content)confluenceUserParameter).getValue());
                }
            }
            this.markCalendarAsMigratedForUserKey(calendar);
        }
        return calendar;
    }

    private void markCalendarAsMigratedForUserKey(Calendar calendar) {
        PropertyList<Property> calendarProperties = calendar.getProperties();
        Property migratedForUserKeyProperty = (Property)calendarProperties.getProperty(PROPERTY_MIGRATED_FOR_USER_KEY);
        if (migratedForUserKeyProperty != null) {
            calendarProperties.remove(migratedForUserKeyProperty);
        }
        calendarProperties.add(new XProperty(PROPERTY_MIGRATED_FOR_USER_KEY, new ParameterList(), Boolean.TRUE.toString()));
    }

    protected void addProperty(VEvent vEvent, String propertyName, String userName) {
        ConfluenceUser theUser = this.userAccessor.getUserByName(userName);
        UserKeyMigratorTransformer.addProperty(this.baseUrl, vEvent, theUser, propertyName, userName);
    }

    public static void addProperty(String baseUrl, VEvent vEvent, ConfluenceUser theUser, String propertyName, String userName) {
        if (theUser != null) {
            try {
                vEvent.getProperties().add(UserKeyMigratorTransformer.createCustomUserProperty(baseUrl, propertyName, theUser.getKey().toString(), userName));
            }
            catch (Exception cannotCreateProperty) {
                LOG.error(String.format("Unable to create property %s for user %s", propertyName, userName), (Throwable)cannotCreateProperty);
            }
        } else {
            LOG.warn("Unable to get user key for user {}", (Object)userName);
        }
    }

    public static Property createCustomUserProperty(String baseUrl, String propertyName, String userId, String userName) {
        ParameterList parameterList = new ParameterList();
        parameterList.add(new XParameter("X-CONFLUENCE-USER-KEY", userId));
        return new XProperty(propertyName, parameterList, String.format("%s/display/~%s", baseUrl, HtmlUtil.urlEncode((String)userName)));
    }
}

