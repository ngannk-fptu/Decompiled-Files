/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.eclipse.gemini.blueprint.util.OsgiServiceReferenceUtils
 *  org.eclipse.gemini.blueprint.util.OsgiStringUtils
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.ServiceReference
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.extender.internal.dependencies.shutdown;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.util.OsgiServiceReferenceUtils;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.springframework.util.ObjectUtils;

public abstract class ShutdownSorter {
    private static final Log log = LogFactory.getLog(ShutdownSorter.class);

    public static Collection<Bundle> getBundles(Collection<Bundle> managedBundles) {
        List<Bundle> returned = null;
        try {
            returned = ShutdownSorter.unusedBundles(managedBundles);
            if (returned.isEmpty()) {
                returned = new ArrayList<Bundle>(1);
                returned.add(ShutdownSorter.findBundleBasedOnServices(managedBundles));
            }
            List<Bundle> list = returned;
            return list;
        }
        finally {
            if (returned != null) {
                managedBundles.removeAll(returned);
            }
        }
    }

    private static List<Bundle> unusedBundles(Collection<Bundle> unsortedManagedBundles) {
        ArrayList<Bundle> unused = new ArrayList<Bundle>();
        boolean trace = log.isTraceEnabled();
        for (Bundle bundle : unsortedManagedBundles) {
            try {
                Object[] services;
                String bundleToString = null;
                if (trace) {
                    bundleToString = OsgiStringUtils.nullSafeSymbolicName((Bundle)bundle);
                }
                if (ObjectUtils.isEmpty((Object[])(services = bundle.getRegisteredServices()))) {
                    if (trace) {
                        log.trace((Object)("Bundle " + bundleToString + " has no registered services; added for shutdown"));
                    }
                    unused.add(bundle);
                    continue;
                }
                boolean unusedBundle = true;
                for (Object serviceReference : services) {
                    Object[] usingBundles = serviceReference.getUsingBundles();
                    if (!ObjectUtils.isEmpty((Object[])usingBundles)) {
                        usingBundles = Arrays.stream(usingBundles).filter(b -> unsortedManagedBundles.contains(b) && !b.equals(bundle)).collect(Collectors.toList()).toArray(new Bundle[0]);
                    }
                    if (ObjectUtils.isEmpty((Object[])usingBundles)) continue;
                    if (trace) {
                        log.trace((Object)("Bundle " + bundleToString + " has registered services in use; postponing shutdown. The using bundles are " + Arrays.toString(usingBundles)));
                    }
                    unusedBundle = false;
                    break;
                }
                if (!unusedBundle) continue;
                if (trace) {
                    log.trace((Object)("Bundle " + bundleToString + " has unused registered services; added for shutdown"));
                }
                unused.add(bundle);
            }
            catch (IllegalStateException ignored) {
                unused.add(bundle);
            }
        }
        unused.sort(ReverseBundleIdSorter.INSTANCE);
        return unused;
    }

    private static Bundle findBundleBasedOnServices(Collection<Bundle> managedBundles) {
        Bundle candidate = null;
        int ranking = 0;
        boolean tie = false;
        boolean trace = log.isTraceEnabled();
        String bundleToString = null;
        for (Bundle bundle : managedBundles) {
            if (trace) {
                bundleToString = OsgiStringUtils.nullSafeSymbolicName((Bundle)bundle);
            }
            int localRanking = ShutdownSorter.getRegisteredServiceInUseLowestRanking(bundle);
            if (trace) {
                log.trace((Object)("Bundle " + bundleToString + " lowest ranking registered service is " + localRanking));
            }
            if (candidate == null) {
                candidate = bundle;
                ranking = localRanking;
                continue;
            }
            if (localRanking < ranking) {
                candidate = bundle;
                tie = false;
                ranking = localRanking;
                continue;
            }
            if (localRanking != ranking) continue;
            tie = true;
        }
        if (tie) {
            if (trace) {
                log.trace((Object)"Ranking tie; Looking for the highest service id...");
            }
            long serviceId = Long.MIN_VALUE;
            for (Bundle bundle : managedBundles) {
                if (trace) {
                    bundleToString = OsgiStringUtils.nullSafeSymbolicName((Bundle)bundle);
                }
                long localServiceId = ShutdownSorter.getHighestServiceId(bundle);
                if (trace) {
                    log.trace((Object)("Bundle " + bundleToString + " highest service id is " + localServiceId));
                }
                if (localServiceId <= serviceId) continue;
                candidate = bundle;
                serviceId = localServiceId;
            }
            if (trace) {
                log.trace((Object)("The bundle with the highest service id is " + OsgiStringUtils.nullSafeSymbolicName((Bundle)candidate)));
            }
        } else if (trace) {
            log.trace((Object)("No ranking tie. The bundle with the lowest ranking is " + OsgiStringUtils.nullSafeSymbolicName(candidate)));
        }
        return candidate;
    }

    private static int getRegisteredServiceInUseLowestRanking(Bundle bundle) {
        Object[] services = bundle.getRegisteredServices();
        int min = Integer.MAX_VALUE;
        if (!ObjectUtils.isEmpty((Object[])services)) {
            for (Object ref : services) {
                int localRank;
                if (ObjectUtils.isEmpty((Object[])ref.getUsingBundles()) || (localRank = OsgiServiceReferenceUtils.getServiceRanking((ServiceReference)ref)) >= min) continue;
                min = localRank;
            }
        }
        return min;
    }

    private static long getHighestServiceId(Bundle bundle) {
        Object[] services = bundle.getRegisteredServices();
        long max = Long.MIN_VALUE;
        if (!ObjectUtils.isEmpty((Object[])services)) {
            for (Object ref : services) {
                long id = OsgiServiceReferenceUtils.getServiceId((ServiceReference)ref);
                if (id <= max) continue;
                max = id;
            }
        }
        return max;
    }

    static class ReverseBundleIdSorter
    implements Comparator<Bundle> {
        private static Comparator<Bundle> INSTANCE = new ReverseBundleIdSorter();

        ReverseBundleIdSorter() {
        }

        @Override
        public int compare(Bundle o1, Bundle o2) {
            try {
                return (int)(o2.getBundleId() - o1.getBundleId());
            }
            catch (IllegalStateException ignored) {
                return o1 == o2 ? 0 : 1;
            }
        }
    }
}

