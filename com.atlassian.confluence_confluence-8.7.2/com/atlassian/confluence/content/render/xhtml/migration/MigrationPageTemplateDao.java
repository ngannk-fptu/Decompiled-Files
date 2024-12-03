/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.bean.EntityObject
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.persistence.dao.hibernate.HibernatePageTemplateDao;
import com.atlassian.core.bean.EntityObject;

public class MigrationPageTemplateDao
extends HibernatePageTemplateDao {
    @Override
    protected void updateModificationData(EntityObject objectToSave) {
        if (objectToSave.getCreationDate() == null) {
            objectToSave.setCreationDate(objectToSave.getCurrentDate());
        }
    }

    @Override
    protected void updateEntityModificationData(PageTemplate objectToSave) {
        this.updateModificationData(objectToSave);
    }
}

