/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.util.persistence.hibernate.batch.HibernateOperation
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 */
package com.atlassian.confluence.impl.user.crowd.hibernate.batch.operation;

import com.atlassian.crowd.util.persistence.hibernate.batch.HibernateOperation;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public class RemoveOperation
implements HibernateOperation<Session> {
    public void performOperation(Object object, Session session) {
        try {
            session.delete(object);
        }
        catch (HibernateException e) {
            throw new RuntimeException("could not delete [ " + object + " ]", e);
        }
    }
}

