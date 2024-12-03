/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.group.InternalGroup
 *  com.atlassian.crowd.model.user.InternalUser
 *  org.hibernate.SessionFactory
 *  org.hibernate.type.LongType
 *  org.hibernate.type.Type
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.impl.user.crowd.hibernate;

import com.atlassian.confluence.core.persistence.hibernate.SessionHelper;
import com.atlassian.confluence.impl.user.crowd.hibernate.InternalMembershipDao;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.model.group.InternalGroup;
import com.atlassian.crowd.model.user.InternalUser;
import org.hibernate.SessionFactory;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import org.springframework.orm.hibernate5.HibernateTemplate;

public final class HibernateInternalMembershipDao
implements InternalMembershipDao {
    private final HibernateTemplate hibernateTemplate;

    public HibernateInternalMembershipDao(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public void removeAllGroupRelationships(InternalGroup group) {
        this.hibernateTemplate.executeWithNativeSession(session -> SessionHelper.delete(session, "from HibernateMembership membership where membership.parentGroup.id = :groupId", new Object[]{group.getId()}, new Type[]{LongType.INSTANCE}));
        this.hibernateTemplate.executeWithNativeSession(session -> SessionHelper.delete(session, "from HibernateMembership membership where membership.groupMember.id = :groupId", new Object[]{group.getId()}, new Type[]{LongType.INSTANCE}));
    }

    @Override
    public void removeAllUserRelationships(InternalUser user) {
        this.hibernateTemplate.executeWithNativeSession(session -> SessionHelper.delete(session, "from HibernateMembership membership where membership.userMember.id = :userId", new Object[]{user.getId()}, new Type[]{LongType.INSTANCE}));
    }

    @Override
    public void removeAllRelationships(Directory directory) {
        this.hibernateTemplate.executeWithNativeSession(session -> SessionHelper.delete(session, "from HibernateMembership membership where membership.parentGroup.directory.id = :directoryId", new Object[]{directory.getId()}, new Type[]{LongType.INSTANCE}));
    }

    @Override
    public void rename(String oldUsername, InternalUser user) {
    }
}

