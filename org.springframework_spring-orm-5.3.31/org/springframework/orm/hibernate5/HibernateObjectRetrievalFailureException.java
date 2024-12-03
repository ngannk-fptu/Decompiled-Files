/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.UnresolvableObjectException
 *  org.hibernate.WrongClassException
 */
package org.springframework.orm.hibernate5;

import org.hibernate.UnresolvableObjectException;
import org.hibernate.WrongClassException;
import org.springframework.orm.ObjectRetrievalFailureException;

public class HibernateObjectRetrievalFailureException
extends ObjectRetrievalFailureException {
    public HibernateObjectRetrievalFailureException(UnresolvableObjectException ex) {
        super(ex.getEntityName(), (Object)ex.getIdentifier(), ex.getMessage(), (Throwable)ex);
    }

    public HibernateObjectRetrievalFailureException(WrongClassException ex) {
        super(ex.getEntityName(), (Object)ex.getIdentifier(), ex.getMessage(), (Throwable)ex);
    }
}

