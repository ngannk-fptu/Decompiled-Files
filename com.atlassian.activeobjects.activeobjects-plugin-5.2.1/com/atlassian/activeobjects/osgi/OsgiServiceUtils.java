/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.ServiceRegistration
 */
package com.atlassian.activeobjects.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;

public interface OsgiServiceUtils {
    public <S, O extends S> ServiceRegistration registerService(Bundle var1, Class<S> var2, O var3);
}

