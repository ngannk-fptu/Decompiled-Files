/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.ext;

import java.security.Permission;
import java.security.ProtectionDomain;
import org.osgi.framework.Bundle;

public interface SecurityProvider {
    public boolean hasBundlePermission(ProtectionDomain var1, Permission var2, boolean var3);

    public Object getSignerMatcher(Bundle var1, int var2);

    public void checkBundle(Bundle var1) throws Exception;
}

