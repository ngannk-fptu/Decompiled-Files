/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.internal;

import java.io.Serializable;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.proxy.EntityNotFoundDelegate;

public class StandardEntityNotFoundDelegate
implements EntityNotFoundDelegate {
    public static final StandardEntityNotFoundDelegate INSTANCE = new StandardEntityNotFoundDelegate();

    @Override
    public void handleEntityNotFound(String entityName, Serializable id) {
        throw new ObjectNotFoundException(id, entityName);
    }
}

