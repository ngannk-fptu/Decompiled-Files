/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityNotFoundException
 */
package org.springframework.orm.jpa;

import javax.persistence.EntityNotFoundException;
import org.springframework.orm.ObjectRetrievalFailureException;

public class JpaObjectRetrievalFailureException
extends ObjectRetrievalFailureException {
    public JpaObjectRetrievalFailureException(EntityNotFoundException ex) {
        super(ex.getMessage(), (Throwable)ex);
    }
}

