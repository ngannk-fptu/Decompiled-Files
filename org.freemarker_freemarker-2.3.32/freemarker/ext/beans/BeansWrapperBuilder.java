/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperConfiguration;
import freemarker.ext.beans._BeansAPI;
import freemarker.template.Version;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

public class BeansWrapperBuilder
extends BeansWrapperConfiguration {
    private static final Map<ClassLoader, Map<BeansWrapperConfiguration, WeakReference<BeansWrapper>>> INSTANCE_CACHE = new WeakHashMap<ClassLoader, Map<BeansWrapperConfiguration, WeakReference<BeansWrapper>>>();
    private static final ReferenceQueue<BeansWrapper> INSTANCE_CACHE_REF_QUEUE = new ReferenceQueue();

    public BeansWrapperBuilder(Version incompatibleImprovements) {
        super(incompatibleImprovements);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void clearInstanceCache() {
        Map<ClassLoader, Map<BeansWrapperConfiguration, WeakReference<BeansWrapper>>> map = INSTANCE_CACHE;
        synchronized (map) {
            INSTANCE_CACHE.clear();
        }
    }

    static Map<ClassLoader, Map<BeansWrapperConfiguration, WeakReference<BeansWrapper>>> getInstanceCache() {
        return INSTANCE_CACHE;
    }

    public BeansWrapper build() {
        return _BeansAPI.getBeansWrapperSubclassSingleton(this, INSTANCE_CACHE, INSTANCE_CACHE_REF_QUEUE, BeansWrapperFactory.INSTANCE);
    }

    private static class BeansWrapperFactory
    implements _BeansAPI._BeansWrapperSubclassFactory<BeansWrapper, BeansWrapperConfiguration> {
        private static final BeansWrapperFactory INSTANCE = new BeansWrapperFactory();

        private BeansWrapperFactory() {
        }

        @Override
        public BeansWrapper create(BeansWrapperConfiguration bwConf) {
            return new BeansWrapper(bwConf, true);
        }
    }
}

