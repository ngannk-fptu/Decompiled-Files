/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  com.atlassian.sal.api.user.UserKey
 *  org.apache.commons.lang3.StringUtils
 *  org.hibernate.HibernateException
 *  org.hibernate.type.Type
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.xmlimport.persister;

import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.impl.user.crowd.hibernate.InternalUserDao;
import com.atlassian.confluence.importexport.impl.ExportDescriptor;
import com.atlassian.confluence.importexport.xmlimport.ImportProcessorContext;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import com.atlassian.confluence.importexport.xmlimport.persister.AbstractObjectPersister;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.atlassian.sal.api.user.UserKey;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class ConfluenceUserPersister
extends AbstractObjectPersister {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceUserPersister.class);
    private final ConfluenceUserDao confluenceUserDao;
    private final CrowdDirectoryService directoryService;
    private final InternalUserDao crowdUserDao;

    public ConfluenceUserPersister(ConfluenceUserDao confluenceUserDao, CrowdDirectoryService directoryService, InternalUserDao crowdUserDao) {
        this.confluenceUserDao = confluenceUserDao;
        this.directoryService = directoryService;
        this.crowdUserDao = crowdUserDao;
    }

    @Override
    public List<TransientHibernateHandle> persist(ImportProcessorContext context, ImportedObject importedObject) throws Exception {
        this.importedObject = importedObject;
        this.entityPersister = context.getPersister(ConfluenceUserImpl.class);
        ConfluenceUserImpl objectToPersist = new ConfluenceUserImpl();
        objectToPersist.setName(GeneralUtil.unescapeCDATA(Objects.toString(importedObject.getStringProperty("name"), "").trim()));
        objectToPersist.setLowerName(GeneralUtil.unescapeCDATA(Objects.toString(importedObject.getStringProperty("lowerName"), "").trim()));
        Serializable id = this.getCurrentObjectId();
        TransientHibernateHandle unfixedHandle = TransientHibernateHandle.create(ConfluenceUserImpl.class, id);
        if (!context.isPreserveIds() && StringUtils.isNotEmpty((CharSequence)objectToPersist.getName())) {
            if (this.confluenceUserDao.findByKey((UserKey)id) != null) {
                context.addExplicitIdMapping(unfixedHandle, id);
                return Collections.singletonList(unfixedHandle);
            }
            String username = objectToPersist.getName();
            if (this.isCloudImport(context)) {
                EntityQuery query = QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.user()).with((SearchRestriction)Restriction.on((Property)UserTermKeys.EMAIL).exactlyMatching((Object)username)).returningAtMost(2);
                List matchedUsernames = this.directoryService.findAllDirectories().stream().flatMap(dir -> this.crowdUserDao.search(dir.getId(), query).stream()).map(String::toLowerCase).distinct().collect(Collectors.toList());
                if (matchedUsernames.size() == 1) {
                    ConfluenceUser user = this.confluenceUserDao.findByUsername((String)matchedUsernames.get(0));
                    if (user != null) {
                        context.addExplicitIdMapping(unfixedHandle, (Serializable)user.getKey());
                        return Collections.singletonList(unfixedHandle);
                    }
                } else if (matchedUsernames.isEmpty()) {
                    ConfluenceUser user = this.confluenceUserDao.findByUsername(username);
                    if (user != null) {
                        context.addExplicitIdMapping(unfixedHandle, (Serializable)user.getKey());
                        return Collections.singletonList(unfixedHandle);
                    }
                } else {
                    log.warn("Ambiguous result while trying to match user by email address. An Unknown User will be created for email [" + username + "] entry.");
                }
            } else {
                ConfluenceUser user = this.confluenceUserDao.findByUsername(username);
                if (user != null) {
                    context.addExplicitIdMapping(unfixedHandle, (Serializable)user.getKey());
                    return Collections.singletonList(unfixedHandle);
                }
            }
        }
        context.saveObject(id, ConfluenceUserImpl.class, objectToPersist);
        return Collections.singletonList(unfixedHandle);
    }

    private boolean isCloudImport(ImportProcessorContext context) {
        return context.getExportDescriptor() != null && ExportDescriptor.Source.CLOUD.equals((Object)context.getExportDescriptor().getSource());
    }

    private Serializable getCurrentObjectId() throws HibernateException {
        Type idType = this.entityPersister.getIdentifierType();
        return (Serializable)this.persisterOperations.literalTypeFromString(idType, this.importedObject.getIdPropertyStr());
    }
}

