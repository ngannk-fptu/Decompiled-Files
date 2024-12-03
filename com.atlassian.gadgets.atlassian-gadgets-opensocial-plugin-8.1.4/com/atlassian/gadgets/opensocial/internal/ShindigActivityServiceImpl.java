/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.opensocial.OpenSocialRequestContext
 *  com.atlassian.gadgets.opensocial.model.Activity
 *  com.atlassian.gadgets.opensocial.model.AppId
 *  com.atlassian.gadgets.opensocial.model.Person
 *  com.atlassian.gadgets.opensocial.model.PersonId
 *  com.atlassian.gadgets.opensocial.spi.ActivityService
 *  com.atlassian.gadgets.opensocial.spi.ActivityServiceException
 *  com.atlassian.gadgets.opensocial.spi.PersonService
 *  com.atlassian.gadgets.util.Uri
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.apache.shindig.auth.SecurityToken
 *  org.apache.shindig.common.util.ImmediateFuture
 *  org.apache.shindig.social.ResponseError
 *  org.apache.shindig.social.opensocial.model.Activity
 *  org.apache.shindig.social.opensocial.spi.ActivityService
 *  org.apache.shindig.social.opensocial.spi.GroupId
 *  org.apache.shindig.social.opensocial.spi.RestfulCollection
 *  org.apache.shindig.social.opensocial.spi.SocialSpiException
 *  org.apache.shindig.social.opensocial.spi.UserId
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.opensocial.internal;

import com.atlassian.gadgets.opensocial.OpenSocialRequestContext;
import com.atlassian.gadgets.opensocial.internal.ShindigOpenSocialTypeAdapter;
import com.atlassian.gadgets.opensocial.model.AppId;
import com.atlassian.gadgets.opensocial.model.Person;
import com.atlassian.gadgets.opensocial.model.PersonId;
import com.atlassian.gadgets.opensocial.spi.ActivityServiceException;
import com.atlassian.gadgets.opensocial.spi.PersonService;
import com.atlassian.gadgets.util.Uri;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.util.ImmediateFuture;
import org.apache.shindig.social.ResponseError;
import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.spi.ActivityService;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.RestfulCollection;
import org.apache.shindig.social.opensocial.spi.SocialSpiException;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShindigActivityServiceImpl
implements ActivityService {
    private final com.atlassian.gadgets.opensocial.spi.ActivityService activityService;
    private final ApplicationProperties applicationProperties;
    private final PersonService personService;
    private final TransactionTemplate txTemplate;

    @Autowired
    public ShindigActivityServiceImpl(PersonService personService, com.atlassian.gadgets.opensocial.spi.ActivityService activityService, TransactionTemplate txTemplate, ApplicationProperties applicationProperties) {
        this.activityService = Objects.requireNonNull(activityService);
        this.applicationProperties = Objects.requireNonNull(applicationProperties);
        this.personService = Objects.requireNonNull(personService);
        this.txTemplate = Objects.requireNonNull(txTemplate);
    }

    public Future<RestfulCollection<Activity>> getActivities(Set<UserId> userIds, GroupId groupId, String appId, Set<String> fields, SecurityToken token) throws SocialSpiException {
        try {
            AppId relativeAppId = this.getRelativeAppId(appId);
            List activities = (List)this.txTemplate.execute(() -> {
                Set<PersonId> people = ShindigOpenSocialTypeAdapter.getPeopleIdsFromUserIds(this.personService, userIds, groupId, token);
                return this.activityService.getActivities(people, relativeAppId, ShindigOpenSocialTypeAdapter.convertShindigSecurityTokenToRequestContext(token));
            });
            List shindigActivities = activities.stream().map(activity -> ShindigOpenSocialTypeAdapter.convertActivityToShindigActivity(activity, fields)).collect(Collectors.toList());
            return ImmediateFuture.newInstance((Object)new RestfulCollection(shindigActivities));
        }
        catch (ActivityServiceException e) {
            throw new SocialSpiException(ResponseError.INTERNAL_ERROR, e.getMessage(), (Throwable)e);
        }
    }

    public Future<RestfulCollection<Activity>> getActivities(UserId userId, GroupId groupId, String appId, Set<String> fields, Set<String> activityIds, SecurityToken token) throws SocialSpiException {
        try {
            OpenSocialRequestContext openSocialRequestContext = ShindigOpenSocialTypeAdapter.convertShindigSecurityTokenToRequestContext(token);
            AppId relativeAppId = this.getRelativeAppId(appId);
            List activities = (List)this.txTemplate.execute(() -> {
                Person person = this.personService.getPerson(userId.getUserId(token), openSocialRequestContext);
                if (person == null) {
                    return Collections.emptyList();
                }
                return this.activityService.getActivities(person.getPersonId(), relativeAppId, activityIds, openSocialRequestContext);
            });
            List shindigActivities = activities.stream().map(activity -> ShindigOpenSocialTypeAdapter.convertActivityToShindigActivity(activity, fields)).collect(Collectors.toList());
            return ImmediateFuture.newInstance((Object)new RestfulCollection(shindigActivities));
        }
        catch (ActivityServiceException e) {
            throw new SocialSpiException(ResponseError.INTERNAL_ERROR, e.getMessage(), (Throwable)e);
        }
    }

    public Future<Activity> getActivity(UserId userId, GroupId groupId, String appId, Set<String> fields, String activityId, SecurityToken token) throws SocialSpiException {
        try {
            AppId relativeAppId = this.getRelativeAppId(appId);
            com.atlassian.gadgets.opensocial.model.Activity activity = (com.atlassian.gadgets.opensocial.model.Activity)this.txTemplate.execute(() -> {
                OpenSocialRequestContext openSocialRequestContext = ShindigOpenSocialTypeAdapter.convertShindigSecurityTokenToRequestContext(token);
                Person person = this.personService.getPerson(userId.getUserId(token), openSocialRequestContext);
                if (person == null) {
                    return null;
                }
                return this.activityService.getActivity(person.getPersonId(), relativeAppId, activityId, openSocialRequestContext);
            });
            return ImmediateFuture.newInstance((Object)ShindigOpenSocialTypeAdapter.convertActivityToShindigActivity(activity, fields));
        }
        catch (ActivityServiceException e) {
            throw new SocialSpiException(ResponseError.INTERNAL_ERROR, e.getMessage(), (Throwable)e);
        }
    }

    public Future<Void> deleteActivities(UserId userId, GroupId groupId, String appId, Set<String> activityIds, SecurityToken token) throws SocialSpiException {
        try {
            OpenSocialRequestContext openSocialRequestContext = ShindigOpenSocialTypeAdapter.convertShindigSecurityTokenToRequestContext(token);
            AppId relativeAppId = this.getRelativeAppId(appId);
            this.txTemplate.execute(() -> {
                Person person = this.personService.getPerson(userId.getUserId(token), openSocialRequestContext);
                if (person != null) {
                    this.activityService.deleteActivities(person.getPersonId(), relativeAppId, activityIds, openSocialRequestContext);
                }
                return null;
            });
            return ImmediateFuture.newInstance(null);
        }
        catch (ActivityServiceException e) {
            throw new SocialSpiException(ResponseError.INTERNAL_ERROR, e.getMessage(), (Throwable)e);
        }
    }

    public Future<Void> createActivity(UserId userId, GroupId groupId, String appId, Set<String> fields, Activity activity, SecurityToken token) throws SocialSpiException {
        try {
            OpenSocialRequestContext openSocialRequestContext = ShindigOpenSocialTypeAdapter.convertShindigSecurityTokenToRequestContext(token);
            AppId relativeAppId = this.getRelativeAppId(appId);
            this.txTemplate.execute(() -> {
                Person person = this.personService.getPerson(userId.getUserId(token), openSocialRequestContext);
                if (person != null) {
                    com.atlassian.gadgets.opensocial.model.Activity gadgetsActivity = ShindigOpenSocialTypeAdapter.convertShindigActivityToActivity(activity);
                    this.activityService.createActivity(person.getPersonId(), relativeAppId, gadgetsActivity, openSocialRequestContext);
                }
                return null;
            });
            return ImmediateFuture.newInstance(null);
        }
        catch (ActivityServiceException e) {
            throw new SocialSpiException(ResponseError.INTERNAL_ERROR, e.getMessage(), (Throwable)e);
        }
    }

    private AppId getRelativeAppId(String appId) {
        return AppId.valueOf((String)Uri.relativizeUriAgainstBase((String)this.applicationProperties.getBaseUrl(), (String)appId).toString());
    }
}

