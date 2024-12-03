/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.retention.RetentionFeatureChecker
 *  com.atlassian.core.bean.EntityObject
 */
package com.atlassian.confluence.core.persistence.hibernate;

import com.atlassian.confluence.api.service.retention.RetentionFeatureChecker;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.internal.persistence.hibernate.AbstractContentEntityObjectHibernateDao;
import com.atlassian.core.bean.EntityObject;

public class ContentEntityObjectHibernateDao
extends AbstractContentEntityObjectHibernateDao<ContentEntityObject> {
    @Deprecated
    public static final long ONE_DAY = 86400000L;

    public ContentEntityObjectHibernateDao(RetentionFeatureChecker retentionFeatureChecker) {
        super(retentionFeatureChecker);
    }

    @Override
    public Class<ContentEntityObject> getPersistentClass() {
        return ContentEntityObject.class;
    }

    @Override
    public void saveRawWithoutReindex(EntityObject objectToSave) {
        this.getHibernateTemplate().saveOrUpdate((Object)objectToSave);
    }
}

