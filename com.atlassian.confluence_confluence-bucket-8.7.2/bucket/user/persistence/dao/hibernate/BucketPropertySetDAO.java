/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.propertyset.hibernate.HibernatePropertySetDAO
 *  com.opensymphony.module.propertyset.hibernate.PropertySetItem
 *  javax.persistence.EntityManagerFactory
 *  javax.persistence.Query
 *  org.hibernate.ObjectDeletedException
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.orm.hibernate5.HibernateTemplate
 *  org.springframework.orm.jpa.EntityManagerFactoryUtils
 *  org.springframework.transaction.annotation.Transactional
 */
package bucket.user.persistence.dao.hibernate;

import bucket.user.propertyset.BucketPropertySetItem;
import com.opensymphony.module.propertyset.hibernate.HibernatePropertySetDAO;
import com.opensymphony.module.propertyset.hibernate.PropertySetItem;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import org.hibernate.ObjectDeletedException;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class BucketPropertySetDAO
implements HibernatePropertySetDAO {
    private static final Logger log = LoggerFactory.getLogger(BucketPropertySetDAO.class);
    private HibernateTemplate hibernateTemplate;
    private SessionFactory sessionFactory;

    public void setImpl(PropertySetItem propertySetItem, boolean isUpdate) {
        if (isUpdate) {
            log.debug("Updating: {}", (Object)propertySetItem);
            this.hibernateTemplate.update((Object)propertySetItem);
        } else {
            log.debug("Creating: {}", (Object)propertySetItem);
            this.hibernateTemplate.save((Object)propertySetItem);
        }
    }

    @Transactional(readOnly=true)
    public Collection getKeys(String entityName, Long entityId, String prefix, int type) {
        org.hibernate.query.Query hibernateQuery = (org.hibernate.query.Query)this.hibernateTemplate.execute(session -> {
            org.hibernate.query.Query query;
            if (prefix != null && type > 0) {
                query = session.getNamedQuery("all_keys_with_type_like");
                query.setString("like", prefix + "%");
                query.setInteger("type", type);
            } else if (prefix != null) {
                query = session.getNamedQuery("all_keys_like");
                query.setString("like", prefix + "%");
            } else if (type > 0) {
                query = session.getNamedQuery("all_keys_with_type");
                query.setInteger("type", type);
            } else {
                query = session.getNamedQuery("all_keys");
            }
            query.setString("entityName", entityName);
            query.setLong("entityId", entityId.longValue());
            EntityManagerFactoryUtils.applyTransactionTimeout((Query)query, (EntityManagerFactory)this.sessionFactory);
            return query;
        });
        return hibernateQuery.list();
    }

    @Transactional(readOnly=true)
    public PropertySetItem findByKey(String entityName, Long entityId, String key) {
        return (BucketPropertySetItem)((Object)this.hibernateTemplate.execute(session -> {
            try {
                return (BucketPropertySetItem)((Object)((Object)session.get(BucketPropertySetItem.class, (Serializable)((Object)new BucketPropertySetItem(entityName, entityId, key)))));
            }
            catch (ObjectDeletedException e) {
                return null;
            }
        }));
    }

    public void remove(String entityName, Long entityId, String key) {
        PropertySetItem item = this.findByKey(entityName, entityId, key);
        if (item == null) {
            return;
        }
        this.hibernateTemplate.delete((Object)item);
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
        this.sessionFactory = sessionFactory;
    }
}

