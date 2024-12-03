/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.ServiceReference
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.util;

import java.io.Serializable;
import java.util.Comparator;
import org.eclipse.gemini.blueprint.service.importer.ImportedOsgiServiceProxy;
import org.eclipse.gemini.blueprint.util.OsgiServiceReferenceUtils;
import org.osgi.framework.ServiceReference;

public class ServiceReferenceComparator
implements Comparator,
Serializable {
    private static final long serialVersionUID = 7552328574956669890L;
    private static final int hashCode = ServiceReferenceComparator.class.hashCode() * 13;

    public int compare(Object o1, Object o2) {
        ServiceReference ref2;
        ServiceReference ref1;
        if (o1 == null || o2 == null) {
            if (o1 == o2) {
                return 0;
            }
            throw new ClassCastException("Cannot compare null with a non-null object");
        }
        if (o1 instanceof ServiceReference) {
            ref1 = (ServiceReference)o1;
        } else {
            ImportedOsgiServiceProxy obj1 = (ImportedOsgiServiceProxy)o1;
            ref1 = obj1.getServiceReference();
        }
        if (o2 instanceof ServiceReference) {
            ref2 = (ServiceReference)o2;
        } else {
            ImportedOsgiServiceProxy obj2 = (ImportedOsgiServiceProxy)o2;
            ref2 = obj2.getServiceReference();
        }
        return this.compare(ref1, ref2);
    }

    private int compare(ServiceReference ref1, ServiceReference ref2) {
        int rank2;
        int rank1 = OsgiServiceReferenceUtils.getServiceRanking(ref1);
        int result = rank1 - (rank2 = OsgiServiceReferenceUtils.getServiceRanking(ref2));
        if (result == 0) {
            long id1 = OsgiServiceReferenceUtils.getServiceId(ref1);
            long id2 = OsgiServiceReferenceUtils.getServiceId(ref2);
            return (int)(id2 - id1);
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof ServiceReferenceComparator;
    }

    public int hashCode() {
        return hashCode;
    }
}

