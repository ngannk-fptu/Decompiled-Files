/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.util.persistence.hibernate.batch.HibernateOperation
 *  org.hibernate.HibernateException
 *  org.hibernate.ReplicationMode
 *  org.hibernate.Session
 */
package com.atlassian.confluence.impl.user.crowd.hibernate.batch.operation;

import com.atlassian.crowd.util.persistence.hibernate.batch.HibernateOperation;
import org.hibernate.HibernateException;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;

public class ReplicateOperation
implements HibernateOperation<Session> {
    private final ReplicationMode replicationMode;

    public ReplicateOperation(ReplicationMode replicationMode) {
        this.replicationMode = replicationMode;
    }

    public void performOperation(Object object, Session session) {
        try {
            session.replicate(object, this.replicationMode);
        }
        catch (HibernateException e) {
            throw new RuntimeException("could not replicate [ " + object + " ]", e);
        }
    }
}

