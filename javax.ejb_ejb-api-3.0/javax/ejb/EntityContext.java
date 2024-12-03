/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import javax.ejb.EJBContext;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;

public interface EntityContext
extends EJBContext {
    public EJBLocalObject getEJBLocalObject() throws IllegalStateException;

    public EJBObject getEJBObject() throws IllegalStateException;

    public Object getPrimaryKey() throws IllegalStateException;
}

