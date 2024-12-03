/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.Query
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.manage.dao;

import com.atlassian.confluence.security.denormalisedpermissions.DenormalisedPermissionServiceState;
import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.domain.DenormalisedServiceStateRecord;
import java.io.Serializable;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class DenormalisedServiceStateDao {
    private final HibernateTemplate hibernateTemplate;

    public DenormalisedServiceStateDao(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public DenormalisedServiceStateRecord getRecord(DenormalisedServiceStateRecord.ServiceType type) {
        DenormalisedServiceStateRecord denormalisedServiceStateRecord = (DenormalisedServiceStateRecord)this.hibernateTemplate.get(DenormalisedServiceStateRecord.class, (Serializable)((Object)type));
        if (denormalisedServiceStateRecord != null) {
            this.hibernateTemplate.refresh((Object)denormalisedServiceStateRecord);
        }
        return denormalisedServiceStateRecord;
    }

    public void createRecord(DenormalisedServiceStateRecord.ServiceType serviceType, DenormalisedPermissionServiceState state) {
        DenormalisedServiceStateRecord record = new DenormalisedServiceStateRecord();
        record.setState(state);
        record.setServiceType(serviceType);
        this.hibernateTemplate.save((Object)record);
        this.hibernateTemplate.flush();
    }

    public void saveRecord(DenormalisedServiceStateRecord stateRecord) {
        this.hibernateTemplate.save((Object)stateRecord);
    }

    public List<DenormalisedServiceStateRecord> getAllRecords() {
        return (List)this.hibernateTemplate.execute(session -> {
            String hqlQuery = "select new " + DenormalisedServiceStateRecord.class.getName() + "(r.serviceType, r.state, r.lastUpToDateTimestamp)  from DenormalisedServiceStateRecord r";
            Query query = session.createQuery(hqlQuery, DenormalisedServiceStateRecord.class);
            query.setCacheable(false);
            return query.list();
        });
    }
}

