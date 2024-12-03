/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 */
package org.eclipse.gemini.blueprint.context.support;

import java.security.AccessControlContext;
import java.security.Permission;
import java.security.ProtectionDomain;
import org.osgi.framework.Bundle;

abstract class AccessControlFactory {
    AccessControlFactory() {
    }

    static AccessControlContext createContext(Bundle bundle) {
        return new AccessControlContext(new ProtectionDomain[]{new BundleProtectionDomain(bundle)});
    }

    private static class BundleProtectionDomain
    extends ProtectionDomain {
        private final Bundle bundle;

        BundleProtectionDomain(Bundle bundle) {
            super(null, null);
            this.bundle = bundle;
        }

        @Override
        public boolean implies(Permission permission) {
            return this.bundle.hasPermission((Object)permission);
        }
    }
}

