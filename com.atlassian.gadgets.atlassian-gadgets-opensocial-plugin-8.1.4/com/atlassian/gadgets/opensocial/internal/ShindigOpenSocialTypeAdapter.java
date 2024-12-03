/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.opensocial.OpenSocialRequestContext
 *  com.atlassian.gadgets.opensocial.model.Activity
 *  com.atlassian.gadgets.opensocial.model.Activity$Builder
 *  com.atlassian.gadgets.opensocial.model.Activity$Field
 *  com.atlassian.gadgets.opensocial.model.ActivityId
 *  com.atlassian.gadgets.opensocial.model.AppId
 *  com.atlassian.gadgets.opensocial.model.Group
 *  com.atlassian.gadgets.opensocial.model.MediaItem
 *  com.atlassian.gadgets.opensocial.model.MediaItem$Builder
 *  com.atlassian.gadgets.opensocial.model.MediaItem$Type
 *  com.atlassian.gadgets.opensocial.model.Person
 *  com.atlassian.gadgets.opensocial.model.PersonId
 *  com.atlassian.gadgets.opensocial.spi.PersonService
 *  org.apache.shindig.auth.SecurityToken
 *  org.apache.shindig.social.core.model.ActivityImpl
 *  org.apache.shindig.social.core.model.MediaItemImpl
 *  org.apache.shindig.social.core.model.NameImpl
 *  org.apache.shindig.social.core.model.PersonImpl
 *  org.apache.shindig.social.opensocial.model.Activity
 *  org.apache.shindig.social.opensocial.model.MediaItem
 *  org.apache.shindig.social.opensocial.model.MediaItem$Type
 *  org.apache.shindig.social.opensocial.model.Name
 *  org.apache.shindig.social.opensocial.model.Person
 *  org.apache.shindig.social.opensocial.spi.GroupId
 *  org.apache.shindig.social.opensocial.spi.UserId
 */
package com.atlassian.gadgets.opensocial.internal;

import com.atlassian.gadgets.opensocial.OpenSocialRequestContext;
import com.atlassian.gadgets.opensocial.model.Activity;
import com.atlassian.gadgets.opensocial.model.ActivityId;
import com.atlassian.gadgets.opensocial.model.AppId;
import com.atlassian.gadgets.opensocial.model.Group;
import com.atlassian.gadgets.opensocial.model.MediaItem;
import com.atlassian.gadgets.opensocial.model.Person;
import com.atlassian.gadgets.opensocial.model.PersonId;
import com.atlassian.gadgets.opensocial.spi.PersonService;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.social.core.model.ActivityImpl;
import org.apache.shindig.social.core.model.MediaItemImpl;
import org.apache.shindig.social.core.model.NameImpl;
import org.apache.shindig.social.core.model.PersonImpl;
import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.MediaItem;
import org.apache.shindig.social.opensocial.model.Name;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;

class ShindigOpenSocialTypeAdapter {
    private ShindigOpenSocialTypeAdapter() {
        throw new RuntimeException(ShindigOpenSocialTypeAdapter.class.getName() + " cannot be instantiated");
    }

    static org.apache.shindig.social.opensocial.model.Person convertPersonToShindigPerson(Person person) {
        if (person == null) {
            return null;
        }
        String personId = person.getPersonId().toString();
        return new PersonImpl(personId, personId, (Name)new NameImpl(personId));
    }

    static Activity convertActivityToShindigActivity(com.atlassian.gadgets.opensocial.model.Activity from, Set<String> fields) {
        if (from == null) {
            return null;
        }
        ActivityImpl shindigActivity = new ActivityImpl();
        if (ShindigOpenSocialTypeAdapter.isToBeCopied(Activity.Field.APP_ID, fields)) {
            shindigActivity.setAppId(from.getAppId().toString());
        }
        if (ShindigOpenSocialTypeAdapter.isToBeCopied(Activity.Field.BODY, fields)) {
            shindigActivity.setBody(from.getBody());
        }
        if (ShindigOpenSocialTypeAdapter.isToBeCopied(Activity.Field.EXTERNAL_ID, fields)) {
            shindigActivity.setExternalId(from.getExternalId());
        }
        if (from.getId() != null && ShindigOpenSocialTypeAdapter.isToBeCopied(Activity.Field.ID, fields)) {
            shindigActivity.setId(from.getId().toString());
        }
        if (ShindigOpenSocialTypeAdapter.isToBeCopied(Activity.Field.LAST_UPDATED, fields)) {
            shindigActivity.setUpdated(from.getUpdated());
        }
        if (ShindigOpenSocialTypeAdapter.isToBeCopied(Activity.Field.MEDIA_ITEMS, fields)) {
            List shindigMediaItems = from.getMediaItems().stream().map(ShindigOpenSocialTypeAdapter::convertMediaItemToShindigMediaItem).collect(Collectors.toList());
            shindigActivity.setMediaItems(shindigMediaItems);
        }
        if (ShindigOpenSocialTypeAdapter.isToBeCopied(Activity.Field.POSTED_TIME, fields)) {
            shindigActivity.setPostedTime(from.getPostedTime());
        }
        if (ShindigOpenSocialTypeAdapter.isToBeCopied(Activity.Field.PRIORITY, fields)) {
            shindigActivity.setPriority(from.getPriority());
        }
        if (ShindigOpenSocialTypeAdapter.isToBeCopied(Activity.Field.STREAM_FAVICON_URL, fields)) {
            shindigActivity.setStreamFaviconUrl(from.getStreamFaviconUrl());
        }
        if (ShindigOpenSocialTypeAdapter.isToBeCopied(Activity.Field.STREAM_SOURCE_URL, fields)) {
            shindigActivity.setStreamSourceUrl(from.getStreamSourceUrl());
        }
        if (ShindigOpenSocialTypeAdapter.isToBeCopied(Activity.Field.STREAM_TITLE, fields)) {
            shindigActivity.setStreamTitle(from.getStreamTitle());
        }
        if (ShindigOpenSocialTypeAdapter.isToBeCopied(Activity.Field.STREAM_URL, fields)) {
            shindigActivity.setStreamUrl(from.getStreamUrl());
        }
        if (ShindigOpenSocialTypeAdapter.isToBeCopied(Activity.Field.URL, fields)) {
            shindigActivity.setUrl(from.getUrl());
        }
        if (ShindigOpenSocialTypeAdapter.isToBeCopied(Activity.Field.TITLE, fields)) {
            shindigActivity.setTitle(from.getTitle());
        }
        if (from.getUserId() != null && ShindigOpenSocialTypeAdapter.isToBeCopied(Activity.Field.USER_ID, fields)) {
            shindigActivity.setUserId(from.getUserId().toString());
        }
        return shindigActivity;
    }

    private static boolean isToBeCopied(Activity.Field field, Collection<String> fieldsToCopy) {
        return fieldsToCopy == null || fieldsToCopy.isEmpty() || fieldsToCopy.contains(field.toString());
    }

    static com.atlassian.gadgets.opensocial.model.Activity convertShindigActivityToActivity(Activity from) {
        if (from == null) {
            return null;
        }
        Activity.Builder builder = new Activity.Builder(from.getTitle()).body(from.getBody()).externalId(from.getExternalId()).postedTime(from.getPostedTime()).priority(from.getPriority()).streamFaviconUrl(from.getStreamFaviconUrl()).streamSourceUrl(from.getStreamSourceUrl()).streamTitle(from.getStreamTitle()).streamUrl(from.getStreamUrl()).updated(from.getUpdated()).url(from.getUrl());
        if (from.getAppId() != null) {
            builder.appId(AppId.valueOf((String)from.getAppId()));
        }
        if (from.getId() != null) {
            builder.id(ActivityId.valueOf((String)from.getId()));
        }
        if (from.getMediaItems() != null) {
            List mediaItems = from.getMediaItems().stream().map(ShindigOpenSocialTypeAdapter::convertShindigMediaItemToMediaItem).collect(Collectors.toList());
            builder.mediaItems(mediaItems);
        }
        if (from.getUserId() != null) {
            builder.userId(PersonId.valueOf((String)from.getUserId()));
        }
        return builder.build();
    }

    private static Group groupIdToGroup(GroupId groupId) {
        switch (groupId.getType()) {
            case all: {
                return Group.ALL;
            }
            case self: {
                return Group.SELF;
            }
            case friends: {
                return Group.FRIENDS;
            }
        }
        return Group.of((String)groupId.getGroupId());
    }

    static OpenSocialRequestContext convertShindigSecurityTokenToRequestContext(final SecurityToken token) {
        return new OpenSocialRequestContext(){

            public String getOwnerId() {
                return token.getOwnerId();
            }

            public String getViewerId() {
                return token.getViewerId();
            }

            public boolean isAnonymous() {
                return token.isAnonymous();
            }

            public String getActiveUrl() {
                return token.getActiveUrl();
            }
        };
    }

    static Function<Person, org.apache.shindig.social.opensocial.model.Person> personToShindigPersonFunction() {
        return PersonToShindigPerson.FUNCTION;
    }

    static Set<Person> getPeopleFromUserIds(PersonService personService, Set<UserId> userIds, GroupId groupId, SecurityToken token) {
        Group group = ShindigOpenSocialTypeAdapter.groupIdToGroup(groupId);
        OpenSocialRequestContext openSocialRequestContext = ShindigOpenSocialTypeAdapter.convertShindigSecurityTokenToRequestContext(token);
        Set personIds = userIds.stream().map(userId -> userId.getUserId(token)).collect(Collectors.toSet());
        return personService.getPeople(personIds, group, openSocialRequestContext);
    }

    static Set<PersonId> getPeopleIdsFromUserIds(PersonService personService, Set<UserId> userIds, GroupId groupId, SecurityToken token) {
        return ShindigOpenSocialTypeAdapter.getPeopleFromUserIds(personService, userIds, groupId, token).stream().map(Person::getPersonId).collect(Collectors.toSet());
    }

    private static org.apache.shindig.social.opensocial.model.MediaItem convertMediaItemToShindigMediaItem(MediaItem from) {
        if (from == null) {
            return null;
        }
        return new MediaItemImpl(from.getMimeType(), from.getType() != null ? MediaItem.Type.valueOf((String)from.getType().toString().toUpperCase()) : null, from.getUrl().toString());
    }

    private static MediaItem convertShindigMediaItemToMediaItem(org.apache.shindig.social.opensocial.model.MediaItem from) {
        if (from == null) {
            return null;
        }
        return new MediaItem.Builder(URI.create(from.getUrl())).mimeType(from.getMimeType()).type(from.getType() != null ? MediaItem.Type.valueOf((String)from.getType().toString().toUpperCase()) : null).build();
    }

    private static enum PersonToShindigPerson implements Function<Person, org.apache.shindig.social.opensocial.model.Person>
    {
        FUNCTION;


        @Override
        public org.apache.shindig.social.opensocial.model.Person apply(Person from) {
            return ShindigOpenSocialTypeAdapter.convertPersonToShindigPerson(from);
        }
    }
}

