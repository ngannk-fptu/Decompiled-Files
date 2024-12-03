/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.ext;

import java.util.List;
import java.util.Map;
import org.apache.xml.security.stax.securityEvent.SecurityEventListener;

public interface SecurityContext
extends SecurityEventListener {
    public <T> void put(String var1, T var2);

    public <T> T get(String var1);

    public <T> T remove(String var1);

    public <T extends List> void putList(Object var1, T var2);

    public <T> void putAsList(Object var1, T var2);

    public <T> List<T> getAsList(Object var1);

    public <T, U> void putAsMap(Object var1, T var2, U var3);

    public <T, U> Map<T, U> getAsMap(Object var1);

    public void addSecurityEventListener(SecurityEventListener var1);
}

