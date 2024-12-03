/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.SessionFactory
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.persister.entity.AbstractEntityPersister
 */
package com.atlassian.confluence.impl.backuprestore.hibernate;

import com.atlassian.confluence.impl.backuprestore.hibernate.AncestorsEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.ApplicationAttributeEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.DefaultExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.DirectoryAttributeEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.DirectoryMappingOperationEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.DirectoryOperationEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;

public class ExportableEntityInfoFactory {
    private final SessionFactory sessionFactory;

    public ExportableEntityInfoFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public ExportableEntityInfo createExportableEntityInfo(AbstractEntityPersister entityPersister) {
        return new DefaultExportableEntityInfo(entityPersister, this.sessionFactory);
    }

    public ExportableEntityInfo createAncestorsEntityInfo() {
        return new AncestorsEntityInfo((SessionFactoryImplementor)this.sessionFactory);
    }

    public ExportableEntityInfo createDirectoryOperationEntityInfo() {
        return new DirectoryOperationEntityInfo((SessionFactoryImplementor)this.sessionFactory);
    }

    public ExportableEntityInfo createDirectoryMappingOperationEntityInfo() {
        return new DirectoryMappingOperationEntityInfo((SessionFactoryImplementor)this.sessionFactory);
    }

    public ExportableEntityInfo createApplicationAttributeEntityInfo() {
        return new ApplicationAttributeEntityInfo((SessionFactoryImplementor)this.sessionFactory);
    }

    public ExportableEntityInfo createDirectoryAttributeEntityInfo() {
        return new DirectoryAttributeEntityInfo((SessionFactoryImplementor)this.sessionFactory);
    }
}

