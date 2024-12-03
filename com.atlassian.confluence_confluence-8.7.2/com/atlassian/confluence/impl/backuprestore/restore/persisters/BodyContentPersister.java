/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.impl.backuprestore.restore.EntityPersister;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.IdMapper;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.ObjectPersister;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.PersisterHelper;
import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStash;
import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStashFactory;
import com.atlassian.confluence.impl.backuprestore.statistics.SkippedObjectsReason;
import com.atlassian.confluence.importexport.impl.ContentUserKeyExtractor;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.sal.api.user.UserKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BodyContentPersister
implements EntityPersister {
    private static final Logger log = LoggerFactory.getLogger(BodyContentPersister.class);
    private final ImportedObjectsStash stash;
    private final ObjectPersister databasePersister;
    private final PersisterHelper persisterHelper;
    private final IdMapper idMapper;
    private final ContentUserKeyExtractor contentUserKeyExtractor;
    private static final Pattern patternWordMatch = Pattern.compile("\\w+");
    private AtomicBoolean foundInvalidBodyContents;

    public BodyContentPersister(ImportedObjectsStashFactory importedObjectsStashFactory, ObjectPersister databasePersister, PersisterHelper persisterHelper, IdMapper idMapper, ContentUserKeyExtractor contentUserKeyExtractor) {
        this.stash = importedObjectsStashFactory.createStash(BodyContentPersister.class.getSimpleName());
        this.databasePersister = databasePersister;
        this.persisterHelper = persisterHelper;
        this.idMapper = idMapper;
        this.contentUserKeyExtractor = contentUserKeyExtractor;
        this.foundInvalidBodyContents = new AtomicBoolean(false);
    }

    @Override
    public Collection<Class<?>> getSupportedClasses() {
        return Collections.singleton(BodyContent.class);
    }

    @Override
    public boolean canAccept(ImportedObjectV2 importedObject) {
        return this.getSupportedClasses().contains(importedObject.getEntityClass());
    }

    @Override
    public void persist(ImportedObjectV2 importedObject) throws BackupRestoreException {
        if (!this.canAccept(importedObject)) {
            throw new BackupRestoreException("BodyContentPersister got unacceptable object with class " + importedObject.getEntityClass());
        }
        this.stash.add(importedObject);
    }

    @Override
    public long persistNextChunkOfData() throws BackupRestoreException {
        int counter = 0;
        if (this.stash.hasMoreRecords()) {
            ArrayList allTasks = new ArrayList();
            List<ImportedObjectV2> importedObjects = this.stash.readObjects(this.persisterHelper.getBatchSize());
            counter = importedObjects.size();
            ArrayList<ImportedObjectV2> objectsReadyToPersist = new ArrayList<ImportedObjectV2>();
            for (ImportedObjectV2 importedObject : importedObjects) {
                try {
                    Map<String, String> newKeys = this.getXmlToDbUserKeyMap(importedObject);
                    if (!newKeys.isEmpty()) {
                        String newBody = this.updateUserKeysInBodyString(newKeys, importedObject.getFieldValue("body").toString());
                        ImportedObjectV2 newObject = importedObject.overridePropertyValues(importedObject.getId(), Collections.singletonMap("body", newBody));
                        objectsReadyToPersist.add(newObject);
                        continue;
                    }
                    objectsReadyToPersist.add(importedObject);
                }
                catch (Exception e) {
                    this.logInformationAboutNotPersistedObject(importedObject, e);
                }
            }
            allTasks.addAll(this.databasePersister.persistAsynchronously(objectsReadyToPersist, "BodyContent objects"));
        }
        return counter;
    }

    private Map<String, String> getXmlToDbUserKeyMap(ImportedObjectV2 importedObject) throws BackupRestoreException {
        Set<UserKey> userKeys = this.extractUserKeys(importedObject);
        HashMap<String, String> resultMap = new HashMap<String, String>();
        userKeys.forEach(userKey -> {
            String xmlUserKey = userKey.getStringValue();
            Object dbUserKey = this.idMapper.getDatabaseId(ConfluenceUserImpl.class, xmlUserKey);
            if (dbUserKey == null) {
                log.warn("Unable to find user '{}' mentioned in the body content with id '{}'.", (Object)xmlUserKey, importedObject.getId());
            } else {
                resultMap.put(xmlUserKey, dbUserKey.toString());
            }
        });
        return resultMap;
    }

    private Set<UserKey> extractUserKeys(ImportedObjectV2 entityObject) throws BackupRestoreException {
        String bodyContent = this.getBodyContent(entityObject);
        if (StringUtils.isNotEmpty((CharSequence)bodyContent)) {
            try {
                return this.contentUserKeyExtractor.extractUserKeys(bodyContent);
            }
            catch (XhtmlException | XMLStreamException e) {
                log.warn("Failed to extract user references from BodyContent with id {}", entityObject.getId());
                log.debug("Failed to extract exception.", (Throwable)e);
            }
        }
        return Collections.emptySet();
    }

    private String getBodyContent(ImportedObjectV2 entityObject) throws BackupRestoreException {
        try {
            String bodyType = entityObject.getFieldValue("bodyType").toString();
            if (bodyType.equals(String.valueOf(BodyType.XHTML.toInt())) && entityObject.getPropertyValueMap().containsKey("body")) {
                return entityObject.getFieldValue("body").toString();
            }
        }
        catch (Exception e) {
            throw new BackupRestoreException("Failed to extract body from BodyContent", e);
        }
        return null;
    }

    private String updateUserKeysInBodyString(Map<String, String> userKeyMap, String oldBody) {
        StringBuilder populatedTemplate = new StringBuilder();
        Matcher matcher = patternWordMatch.matcher(oldBody);
        int fromIndex = 0;
        while (matcher.find(fromIndex)) {
            int startIdx = matcher.start();
            if (fromIndex < startIdx) {
                populatedTemplate.append(oldBody, fromIndex, startIdx);
            }
            String userKeyEntry = matcher.group();
            populatedTemplate.append(userKeyMap.getOrDefault(userKeyEntry, userKeyEntry));
            fromIndex = matcher.end();
        }
        if (fromIndex < oldBody.length()) {
            populatedTemplate.append(oldBody, fromIndex, oldBody.length());
        }
        return populatedTemplate.toString();
    }

    protected void logInformationAboutNotPersistedObject(ImportedObjectV2 importedObject, Exception e) {
        if (!this.foundInvalidBodyContents.getAndSet(true)) {
            log.warn("BodyContentPersister detected invalid BodyContent(s) during import which were ignored.");
        }
        this.persisterHelper.logInformationAboutNotPersistedObject(importedObject, SkippedObjectsReason.INVALID_FIELDS, Collections.emptySet());
        if (log.isDebugEnabled()) {
            log.debug("Skipped BodyContent {} due to {}", new Object[]{importedObject.getId(), e.getMessage(), e});
        }
    }
}

