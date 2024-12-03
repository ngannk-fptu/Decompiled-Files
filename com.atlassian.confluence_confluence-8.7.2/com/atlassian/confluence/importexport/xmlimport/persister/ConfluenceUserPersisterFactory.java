/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.model.user.InternalUser
 */
package com.atlassian.confluence.importexport.xmlimport.persister;

import com.atlassian.confluence.impl.user.crowd.hibernate.InternalUserDao;
import com.atlassian.confluence.importexport.xmlimport.ObjectPersister;
import com.atlassian.confluence.importexport.xmlimport.ObjectPersisterFactory;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import com.atlassian.confluence.importexport.xmlimport.persister.ConfluenceUserPersister;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.model.user.InternalUser;

@Deprecated
public class ConfluenceUserPersisterFactory
implements ObjectPersisterFactory {
    private final ConfluenceUserDao confluenceUserDao;
    private final CrowdDirectoryService directoryService;
    private final InternalUserDao<InternalUser> crowdUserDao;

    public ConfluenceUserPersisterFactory(ConfluenceUserDao confluenceUserDao, CrowdDirectoryService directoryService, InternalUserDao<InternalUser> crowdUserDao) {
        this.confluenceUserDao = confluenceUserDao;
        this.directoryService = directoryService;
        this.crowdUserDao = crowdUserDao;
    }

    @Override
    public ObjectPersister createPersisterFor(ImportedObject importedObject) {
        String packageName = importedObject.getPackageName();
        String className = importedObject.getClassName();
        if ("com.atlassian.confluence.user".equals(packageName) && "ConfluenceUserImpl".equals(className)) {
            return new ConfluenceUserPersister(this.confluenceUserDao, this.directoryService, this.crowdUserDao);
        }
        return null;
    }
}

