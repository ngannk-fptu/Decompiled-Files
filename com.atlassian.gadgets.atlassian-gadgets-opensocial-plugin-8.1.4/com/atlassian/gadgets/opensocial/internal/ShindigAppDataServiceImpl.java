/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.opensocial.OpenSocialRequestContext
 *  com.atlassian.gadgets.opensocial.model.AppId
 *  com.atlassian.gadgets.opensocial.model.Person
 *  com.atlassian.gadgets.opensocial.model.PersonId
 *  com.atlassian.gadgets.opensocial.spi.AppDataService
 *  com.atlassian.gadgets.opensocial.spi.AppDataServiceException
 *  com.atlassian.gadgets.opensocial.spi.PersonService
 *  com.atlassian.gadgets.util.Uri
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.apache.shindig.auth.SecurityToken
 *  org.apache.shindig.common.util.ImmediateFuture
 *  org.apache.shindig.social.ResponseError
 *  org.apache.shindig.social.opensocial.spi.AppDataService
 *  org.apache.shindig.social.opensocial.spi.DataCollection
 *  org.apache.shindig.social.opensocial.spi.GroupId
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
import com.atlassian.gadgets.opensocial.spi.AppDataServiceException;
import com.atlassian.gadgets.opensocial.spi.PersonService;
import com.atlassian.gadgets.util.Uri;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.util.ImmediateFuture;
import org.apache.shindig.social.ResponseError;
import org.apache.shindig.social.opensocial.spi.AppDataService;
import org.apache.shindig.social.opensocial.spi.DataCollection;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.SocialSpiException;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShindigAppDataServiceImpl
implements AppDataService {
    private static final String ALL_FIELDS_KEY = "*";
    private final com.atlassian.gadgets.opensocial.spi.AppDataService appDataService;
    private final PersonService personService;
    private final TransactionTemplate txTemplate;
    private final ApplicationProperties applicationProperties;

    @Autowired
    public ShindigAppDataServiceImpl(com.atlassian.gadgets.opensocial.spi.AppDataService appDataService, PersonService personService, TransactionTemplate txTemplate, ApplicationProperties applicationProperties) {
        this.appDataService = appDataService;
        this.personService = personService;
        this.txTemplate = txTemplate;
        this.applicationProperties = applicationProperties;
    }

    public Future<DataCollection> getPersonData(Set<UserId> userIds, GroupId groupId, String appId, Set<String> fields, SecurityToken token) throws SocialSpiException {
        try {
            AppId relativeAppId = this.getRelativeAppId(appId);
            Map peopleData = (Map)this.txTemplate.execute(() -> {
                Set<PersonId> people = ShindigOpenSocialTypeAdapter.getPeopleIdsFromUserIds(this.personService, userIds, groupId, token);
                OpenSocialRequestContext openSocialRequestContext = ShindigOpenSocialTypeAdapter.convertShindigSecurityTokenToRequestContext(token);
                if (fields.contains(ALL_FIELDS_KEY) || fields.isEmpty()) {
                    return this.appDataService.getPeopleData(people, relativeAppId, openSocialRequestContext);
                }
                return this.appDataService.getPeopleData(people, relativeAppId, fields, openSocialRequestContext);
            });
            HashMap resolvedPeopleData = new HashMap();
            for (Map.Entry personData : peopleData.entrySet()) {
                if (((Map)personData.getValue()).isEmpty()) continue;
                resolvedPeopleData.put(((PersonId)personData.getKey()).toString(), personData.getValue());
            }
            return ImmediateFuture.newInstance((Object)new DataCollection(resolvedPeopleData));
        }
        catch (AppDataServiceException e) {
            throw new SocialSpiException(ResponseError.INTERNAL_ERROR, e.getMessage(), (Throwable)e);
        }
    }

    public Future<Void> deletePersonData(UserId userId, GroupId groupId, String appId, Set<String> fields, SecurityToken token) throws SocialSpiException {
        try {
            OpenSocialRequestContext openSocialRequestContext = ShindigOpenSocialTypeAdapter.convertShindigSecurityTokenToRequestContext(token);
            AppId relativeAppId = this.getRelativeAppId(appId);
            this.txTemplate.execute(() -> {
                Person person = this.personService.getPerson(userId.getUserId(token), openSocialRequestContext);
                if (person != null) {
                    if (fields.contains(ALL_FIELDS_KEY) || fields.isEmpty()) {
                        this.appDataService.deletePersonData(person.getPersonId(), relativeAppId, openSocialRequestContext);
                    } else {
                        this.appDataService.deletePersonData(person.getPersonId(), relativeAppId, fields, openSocialRequestContext);
                    }
                }
                return null;
            });
            return ImmediateFuture.newInstance(null);
        }
        catch (AppDataServiceException e) {
            throw new SocialSpiException(ResponseError.INTERNAL_ERROR, e.getMessage(), (Throwable)e);
        }
    }

    private AppId getRelativeAppId(String appId) {
        return AppId.valueOf((String)Uri.relativizeUriAgainstBase((String)this.applicationProperties.getBaseUrl(), (String)appId).toString());
    }

    public Future<Void> updatePersonData(UserId userId, GroupId groupId, String appId, Set<String> fields, Map<String, String> values, SecurityToken token) throws SocialSpiException {
        try {
            AppId relativeAppId = this.getRelativeAppId(appId);
            this.txTemplate.execute(() -> {
                OpenSocialRequestContext openSocialRequestContext = ShindigOpenSocialTypeAdapter.convertShindigSecurityTokenToRequestContext(token);
                Person person = this.personService.getPerson(userId.getUserId(token), openSocialRequestContext);
                if (person != null) {
                    this.appDataService.updatePersonData(person.getPersonId(), relativeAppId, values, openSocialRequestContext);
                }
                return null;
            });
            return ImmediateFuture.newInstance(null);
        }
        catch (AppDataServiceException e) {
            throw new SocialSpiException(ResponseError.INTERNAL_ERROR, e.getMessage(), (Throwable)e);
        }
    }
}

