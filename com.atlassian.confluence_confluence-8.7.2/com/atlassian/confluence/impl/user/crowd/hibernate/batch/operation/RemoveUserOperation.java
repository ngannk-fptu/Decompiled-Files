/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.model.user.InternalUser
 *  com.atlassian.crowd.model.user.InternalUserAttribute
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.crowd.util.persistence.hibernate.batch.HibernateOperation
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 */
package com.atlassian.confluence.impl.user.crowd.hibernate.batch.operation;

import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.model.user.InternalUser;
import com.atlassian.crowd.model.user.InternalUserAttribute;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.util.persistence.hibernate.batch.HibernateOperation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public class RemoveUserOperation
implements HibernateOperation<Session> {
    private final long directoryId;
    private final Set<String> usersHavingLocalGroup;
    private static final String DELETED_EXTERNALLY_SUFFIX = " [X]";
    private static final String ATTRIBUTE_NAME = "DISABLED_BY_LDAP_SYNC";

    public RemoveUserOperation(long directoryId, List<String> usersHavingLocalGroup) {
        this.directoryId = directoryId;
        this.usersHavingLocalGroup = new HashSet<String>(usersHavingLocalGroup);
    }

    public void performOperation(Object o, Session session) {
        String userName = (String)o;
        try {
            InternalUser user = (InternalUser)session.createNamedQuery("crowd.user_findByName", InternalUser.class).setParameter("directoryId", (Object)this.directoryId).setParameter("lowerName", (Object)IdentifierUtils.toLowerCase((String)userName)).uniqueResult();
            if (this.usersHavingLocalGroup.contains(userName)) {
                if (user.isActive()) {
                    UserTemplate userTemplate = new UserTemplate((User)user);
                    userTemplate.setActive(false);
                    userTemplate.setDisplayName(userTemplate.getDisplayName() + DELETED_EXTERNALLY_SUFFIX);
                    user.updateDetailsFrom((User)userTemplate);
                    InternalUserAttribute internalUserAttribute = new InternalUserAttribute(user, ATTRIBUTE_NAME, "true");
                    session.saveOrUpdate((Object)internalUserAttribute);
                    user.getAttributes().add(internalUserAttribute);
                    session.saveOrUpdate((Object)user);
                }
            } else {
                session.createQuery("delete from HibernateMembership membership where membership.userMember.id = :userId").setParameter("userId", (Object)user.getId()).executeUpdate();
                session.createQuery("delete from InternalUserAttribute a where a.user.id = :userId").setParameter("userId", (Object)user.getId()).executeUpdate();
                session.delete((Object)user);
            }
        }
        catch (HibernateException e) {
            throw new RuntimeException("could not delete user [ " + userName + " ]", e);
        }
    }
}

