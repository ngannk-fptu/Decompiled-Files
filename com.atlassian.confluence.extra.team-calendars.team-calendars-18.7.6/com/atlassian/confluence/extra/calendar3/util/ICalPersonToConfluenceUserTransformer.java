/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.fugue.Either
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.util;

import com.atlassian.confluence.extra.calendar3.service.UserSearchRequest;
import com.atlassian.confluence.extra.calendar3.service.UserSearchService;
import com.atlassian.confluence.extra.calendar3.util.UserKeyMigratorTransformer;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.fugue.Either;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.transform.Transformer;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ICalPersonToConfluenceUserTransformer
implements Transformer<Calendar> {
    private static final Logger LOG = LoggerFactory.getLogger(ICalPersonToConfluenceUserTransformer.class);
    private final String baseUrl;
    private final UserSearchService userSearchService;

    public ICalPersonToConfluenceUserTransformer(String baseUrl, UserSearchService userSearchService) {
        this.baseUrl = baseUrl;
        this.userSearchService = userSearchService;
    }

    @Override
    public Calendar transform(Calendar calendar) {
        ArrayList<Either<String, String>> needMappingAttendees = new ArrayList<Either<String, String>>();
        try {
            LOG.debug("Loop though vEvent list to see whether we need to map to Confluence User or not");
            calendar.getComponents("VEVENT").stream().flatMap(vEvent -> {
                PropertyList<Property> eventProperties = vEvent.getProperties();
                PropertyList attendees = eventProperties.getProperties("ATTENDEE");
                PropertyList organiserProperties = eventProperties.getProperties("ORGANIZER");
                return Stream.concat(attendees.stream(), organiserProperties.stream());
            }).filter(property -> property.getParameter("X-CONFLUENCE-USER-KEY") == null).forEach(property -> {
                if (property instanceof Attendee) {
                    needMappingAttendees.add(this.getEmailOrUsernameFromAttendee((Attendee)property));
                } else {
                    needMappingAttendees.add(this.getEmailOrUsernameFromOrganiser((Organizer)property));
                }
            });
            if (needMappingAttendees.size() > 0) {
                LOG.debug("Need mapping to Confluence User from ICS. Number of item is {}", (Object)needMappingAttendees.size());
                this.mapIcalPersonToConfluenceUser(calendar, needMappingAttendees);
            }
        }
        catch (Exception ex) {
            LOG.error("Exception while trying to convert email to Confluence User", (Throwable)ex);
        }
        return calendar;
    }

    private void mapIcalPersonToConfluenceUser(Calendar calendar, List<Either<String, String>> needMappingAttendees) {
        Map<Either<String, String>, Optional<ConfluenceUser>> confluenceUserMapper = this.getPossibilityConfluenceUserMap(needMappingAttendees);
        calendar.getComponents("VEVENT").stream().forEach(vEvent -> {
            PropertyList<Property> eventProperties = vEvent.getProperties();
            PropertyList organiserProperties = eventProperties.getProperties("ORGANIZER");
            PropertyList attendeeProperties = eventProperties.getProperties("ATTENDEE");
            Stream.concat(organiserProperties.stream(), attendeeProperties.stream()).filter(Objects::nonNull).forEach(property -> {
                Either<String, String> eitherEmailOrUsername;
                String propertyType;
                if (property instanceof Attendee) {
                    propertyType = "ATTENDEE";
                    eitherEmailOrUsername = this.getEmailOrUsernameFromAttendee((Attendee)property);
                } else {
                    propertyType = "ORGANIZER";
                    eitherEmailOrUsername = this.getEmailOrUsernameFromOrganiser((Organizer)property);
                }
                Optional possibilityConfluenceUser = (Optional)confluenceUserMapper.get(eitherEmailOrUsername);
                if (possibilityConfluenceUser != null) {
                    possibilityConfluenceUser.ifPresent(confluenceUser -> {
                        eventProperties.remove((Property)property);
                        UserKeyMigratorTransformer.addProperty(this.baseUrl, (VEvent)vEvent, confluenceUser, propertyType, confluenceUser.getName());
                    });
                }
            });
        });
    }

    private Map<Either<String, String>, Optional<ConfluenceUser>> getPossibilityConfluenceUserMap(List<Either<String, String>> needMappingAttendees) {
        String searchTerms = needMappingAttendees.stream().map(emailOrUsername -> emailOrUsername.isLeft() ? (String)emailOrUsername.left().get() : (String)emailOrUsername.right().get()).collect(Collectors.joining(","));
        UserSearchRequest searchRequest = UserSearchRequest.getBuilder().withSearchTerms(searchTerms).withStartIndex(0).withMaxResult(100).build();
        Collection<ConfluenceUser> foundUsers = this.userSearchService.search(searchRequest);
        return needMappingAttendees.stream().collect(Collectors.toMap(either -> either, either -> {
            String emailOrUsernameStr = either.isLeft() ? (String)either.left().get() : (String)either.right().get();
            Set matchingUsers = foundUsers.stream().filter(Objects::nonNull).filter(confluenceUser -> emailOrUsernameStr.equals(confluenceUser.getEmail()) || emailOrUsernameStr.equals(confluenceUser.getFullName()) || emailOrUsernameStr.equals(confluenceUser.getName())).collect(Collectors.toSet());
            if (matchingUsers.size() > 0) {
                LOG.warn("Multiple user is found of either email or username {}. We are going to pick first one", either);
            }
            return matchingUsers.stream().findFirst();
        }, (confluenceUser, confluenceUser2) -> confluenceUser.isPresent() ? confluenceUser : confluenceUser2));
    }

    private Either<String, String> getEmailOrUsernameFromOrganiser(Organizer organiserProperty) {
        String mailtoStr = organiserProperty.getCalAddress().toString();
        return this.getEmailOrUsername(mailtoStr, organiserProperty);
    }

    private Either<String, String> getEmailOrUsernameFromAttendee(Attendee attendeeProperty) {
        String mailtoStr = attendeeProperty.getCalAddress().toString();
        return this.getEmailOrUsername(mailtoStr, attendeeProperty);
    }

    private Either<String, String> getEmailOrUsername(String mailtoStr, Property property) {
        String internalMailtoStr = this.mailto(mailtoStr, true);
        Object cnParam = property.getParameter("CN");
        if (cnParam == null) {
            return Either.right((Object)internalMailtoStr);
        }
        Either emailOrName = StringUtils.isEmpty(internalMailtoStr) ? Either.left((Object)((Parameter)cnParam).getName()) : Either.right((Object)internalMailtoStr);
        return emailOrName;
    }

    private String mailto(String mail, boolean strip) {
        if (mail == null) {
            return "";
        }
        Object res = mail.trim();
        if (strip) {
            if (((String)res).toLowerCase().startsWith("mailto:")) {
                res = ((String)res).substring(7);
            }
        } else if (!((String)res).toLowerCase().startsWith("mailto:")) {
            res = "mailto:" + (String)res;
        }
        return res;
    }
}

