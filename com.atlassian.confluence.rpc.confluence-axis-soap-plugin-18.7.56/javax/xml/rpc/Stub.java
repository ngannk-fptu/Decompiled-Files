/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.rpc;

import java.util.Iterator;

public interface Stub {
    public static final String USERNAME_PROPERTY = "javax.xml.rpc.security.auth.username";
    public static final String PASSWORD_PROPERTY = "javax.xml.rpc.security.auth.password";
    public static final String ENDPOINT_ADDRESS_PROPERTY = "javax.xml.rpc.service.endpoint.address";
    public static final String SESSION_MAINTAIN_PROPERTY = "javax.xml.rpc.session.maintain";

    public void _setProperty(String var1, Object var2);

    public Object _getProperty(String var1);

    public Iterator _getPropertyNames();
}

