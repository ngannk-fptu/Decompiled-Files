/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ObjectUtils
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  com.atlassian.user.UserManager
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.enrichment;

import com.atlassian.confluence.impl.backuprestore.backup.exporters.DatabaseExporterHelper;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.converters.enrichment.ExportObjectsEnrichment;
import com.atlassian.confluence.impl.backuprestore.backup.models.DbRawObjectData;
import com.atlassian.confluence.impl.backuprestore.backup.models.EntityObjectReadyForExport;
import com.atlassian.core.util.ObjectUtils;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserEmailEnrichment
implements ExportObjectsEnrichment {
    private static final Logger log = LoggerFactory.getLogger(UserEmailEnrichment.class);
    private final DatabaseExporterHelper helper;
    private static final String USER_EMAIL_EXPORTED = "email";
    private static final String CWD_USER_LOWER_USER_NAME = "lower_user_name";
    private static final String CWD_USER_EMAIL_ADDRESS = "email_address";
    private static final String CONFLUENCE_USER_LOWER_USER_NAME = "lowerName";
    private static final String USER_EMAIL_BY_LOW_USER_NAME = "SELECT email_address, lower_user_name FROM cwd_user WHERE lower_user_name IN (:lower_user_name)";

    public UserEmailEnrichment(DatabaseExporterHelper helper) {
        this.helper = helper;
    }

    @Override
    public void enrichElements(List<EntityObjectReadyForExport> entityObjects) {
        this.addUserEmailToEntityObjects(entityObjects);
    }

    private void addUserEmailToEntityObjects(List<EntityObjectReadyForExport> entityObjects) {
        Set<String> lowUserNames = entityObjects.stream().map(e -> e.getProperty(CONFLUENCE_USER_LOWER_USER_NAME)).filter(ObjectUtils::isNotEmpty).map(EntityObjectReadyForExport.Property::getStringValue).collect(Collectors.toSet());
        if (lowUserNames.isEmpty()) {
            entityObjects.forEach(e -> e.addProperty(new EntityObjectReadyForExport.Property(USER_EMAIL_EXPORTED.toLowerCase(), null)));
            return;
        }
        Map<String, Set<String>> emailsGroupByUserName = this.getEmailsGroupByUserName(lowUserNames);
        for (EntityObjectReadyForExport e2 : entityObjects) {
            Set emails = Optional.ofNullable(emailsGroupByUserName.get(e2.getProperty(CONFLUENCE_USER_LOWER_USER_NAME).getStringValue())).orElse(Collections.emptySet()).stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet());
            String email = null;
            if (emails.size() <= 1) {
                email = emails.stream().findFirst().orElse(null);
            } else {
                UserManager userManager = (UserManager)ContainerManager.getComponent((String)"backingUserManager");
                try {
                    User user = userManager.getUser(e2.getProperty(CONFLUENCE_USER_LOWER_USER_NAME).getStringValue());
                    email = user.getEmail();
                }
                catch (EntityException ex) {
                    log.warn("ConfluenceUserImpl entity data exporter was interrupted: {}", (Object)ex.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
            e2.addProperty(new EntityObjectReadyForExport.Property(USER_EMAIL_EXPORTED.toLowerCase(), email));
        }
    }

    private Map<String, Set<String>> getEmailsGroupByUserName(Set<String> lowUserNames) {
        ArrayList<DbRawObjectData> cwdUserRecords = new ArrayList<DbRawObjectData>();
        List partitions = Lists.partition(new ArrayList<String>(lowUserNames), (int)this.helper.getRegularBatchSize());
        for (List partition : partitions) {
            cwdUserRecords.addAll(this.helper.runQueryWithInCondition(USER_EMAIL_BY_LOW_USER_NAME, CWD_USER_LOWER_USER_NAME, partition));
        }
        return cwdUserRecords.stream().collect(Collectors.groupingBy(o -> Objects.toString(o.getObjectProperty(CWD_USER_LOWER_USER_NAME)), Collectors.mapping(o -> Objects.toString(o.getObjectProperty(CWD_USER_EMAIL_ADDRESS), null), Collectors.toSet())));
    }
}

