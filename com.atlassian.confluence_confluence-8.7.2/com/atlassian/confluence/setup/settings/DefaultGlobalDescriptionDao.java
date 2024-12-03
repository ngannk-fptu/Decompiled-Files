/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup.settings;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.hibernate.HibernateObjectDao;
import com.atlassian.confluence.internal.setup.settings.GlobalDescriptionDaoInternal;
import com.atlassian.confluence.setup.settings.GlobalDescription;
import java.io.Serializable;

public class DefaultGlobalDescriptionDao
extends HibernateObjectDao<GlobalDescription>
implements GlobalDescriptionDaoInternal {
    @Override
    protected GlobalDescription getByClassId(long id) {
        return (GlobalDescription)this.getHibernateTemplate().execute(session -> (ContentEntityObject)session.get(ContentEntityObject.class, (Serializable)Long.valueOf(id)));
    }

    @Override
    public Class<GlobalDescription> getPersistentClass() {
        return GlobalDescription.class;
    }

    @Override
    public GlobalDescription getGlobalDescription() {
        return (GlobalDescription)this.uniqueResult(this.findAll());
    }

    @Override
    public GlobalDescription getGlobalDescriptionById(long recordId) {
        return (GlobalDescription)this.getHibernateTemplate().get(GlobalDescription.class, (Serializable)Long.valueOf(recordId));
    }
}

