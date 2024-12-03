/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.ServiceReference
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.util;

import java.util.Comparator;
import org.eclipse.gemini.blueprint.service.importer.ServiceReferenceProxy;
import org.eclipse.gemini.blueprint.service.importer.support.internal.util.ServiceReferenceComparator;
import org.eclipse.gemini.blueprint.util.OsgiPlatformDetector;
import org.osgi.framework.ServiceReference;

public abstract class ServiceComparatorUtil {
    protected static final boolean OSGI_41 = OsgiPlatformDetector.isR41();
    protected static final Comparator COMPARATOR = OsgiPlatformDetector.isR41() ? null : new ServiceReferenceComparator();

    public static int compare(ServiceReference left, Object right) {
        if (right instanceof ServiceReferenceProxy) {
            right = ((ServiceReferenceProxy)right).getTargetServiceReference();
        }
        if (left == null && right == null) {
            return 0;
        }
        if (left == null || right == null) {
            throw new ClassCastException("Cannot compare null with a non-null object");
        }
        return OSGI_41 ? left.compareTo(right) : COMPARATOR.compare(left, right);
    }
}

