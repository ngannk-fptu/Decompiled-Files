/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.spi;

import org.hibernate.HibernateException;

public class QuerySpaceUidNotRegisteredException
extends HibernateException {
    public QuerySpaceUidNotRegisteredException(String uid) {
        super(QuerySpaceUidNotRegisteredException.generateMessage(uid));
    }

    private static String generateMessage(String uid) {
        return "Given uid [" + uid + "] could not be resolved to a QuerySpace";
    }

    public QuerySpaceUidNotRegisteredException(String uid, Throwable cause) {
        super(QuerySpaceUidNotRegisteredException.generateMessage(uid), cause);
    }
}

