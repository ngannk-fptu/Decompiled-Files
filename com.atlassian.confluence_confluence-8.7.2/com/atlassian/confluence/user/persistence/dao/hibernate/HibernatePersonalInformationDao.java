/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.ImmutableList
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.persistence.dao.hibernate;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.hibernate.ConfluenceHibernateObjectDao;
import com.atlassian.confluence.core.persistence.hibernate.HibernateObjectDao;
import com.atlassian.confluence.impl.content.render.prefetch.PersonalInformationBulkDao;
import com.atlassian.confluence.impl.content.render.prefetch.hibernate.HibernatePrefetchHelper;
import com.atlassian.confluence.internal.user.persistence.PersonalInformationDaoInternal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.persistence.dao.hibernate.UserKeyUserType;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class HibernatePersonalInformationDao
extends ConfluenceHibernateObjectDao<PersonalInformation>
implements PersonalInformationDaoInternal,
PersonalInformationBulkDao {
    private static final Logger log = LoggerFactory.getLogger(HibernatePersonalInformationDao.class);

    @Override
    public PersonalInformation getByUser(@Nullable ConfluenceUser user) {
        if (user == null) {
            return null;
        }
        List<PersonalInformation> personalInfoList = this.getAllByUser(user);
        if (personalInfoList == null || personalInfoList.isEmpty()) {
            return null;
        }
        if (personalInfoList.size() > 1) {
            log.debug("found more than one personal information object for user : " + user.getName());
        }
        return personalInfoList.get(0);
    }

    @Override
    public List<PersonalInformation> getAllByUser(@Nullable ConfluenceUser user) {
        if (user == null) {
            return Collections.emptyList();
        }
        List list = this.findNamedQueryStringParam("confluence.personalinformation_findByUsername", "user", user, HibernateObjectDao.Cacheability.CACHEABLE);
        return list;
    }

    @Override
    public PersonalInformation getById(long id) {
        return this.getByClassId(id);
    }

    @Override
    public @NonNull List<Long> findIdsWithAssociatedUser() {
        return this.findNamedQuery("confluence.personalinformation_findIdsWithAssociatedUser");
    }

    @Override
    protected PersonalInformation getByClassId(long id) {
        ContentEntityObject ceo = (ContentEntityObject)this.getHibernateTemplate().execute(session -> (ContentEntityObject)session.get(ContentEntityObject.class, (Serializable)Long.valueOf(id)));
        if (!(ceo instanceof PersonalInformation)) {
            return null;
        }
        return (PersonalInformation)ceo;
    }

    @Override
    public Class<PersonalInformation> getPersistentClass() {
        return PersonalInformation.class;
    }

    @Override
    public Collection<PersonalInformation> bulkFetchPersonalInformation(Collection<UserKey> userKeys) {
        Collection personalInfos = HibernatePrefetchHelper.partitionedQuery(userKeys, 100, this::bulkQueryPersonalInformation);
        HashMap map = new HashMap();
        personalInfos.forEach(info -> map.putIfAbsent(info.getUser().getKey(), info));
        return ImmutableList.copyOf(map.values());
    }

    private Collection<PersonalInformation> bulkQueryPersonalInformation(Collection<UserKey> userKeys) {
        return this.getHibernateTemplate().findByNamedQueryAndNamedParam("confluence.personalinformation_findByUserKeys", "userKeys", HibernatePersonalInformationDao.toStrings(userKeys));
    }

    private static Collection<String> toStrings(Collection<UserKey> userKeys) {
        return userKeys.stream().map(UserKeyUserType::getStringValue).collect(Collectors.toSet());
    }
}

