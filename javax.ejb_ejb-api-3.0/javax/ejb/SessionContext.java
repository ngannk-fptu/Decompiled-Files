/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.rpc.handler.MessageContext
 */
package javax.ejb;

import javax.ejb.EJBContext;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import javax.xml.rpc.handler.MessageContext;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface SessionContext
extends EJBContext {
    public EJBLocalObject getEJBLocalObject() throws IllegalStateException;

    public EJBObject getEJBObject() throws IllegalStateException;

    public MessageContext getMessageContext() throws IllegalStateException;

    public <T> T getBusinessObject(Class<T> var1) throws IllegalStateException;

    public Class getInvokedBusinessInterface() throws IllegalStateException;
}

