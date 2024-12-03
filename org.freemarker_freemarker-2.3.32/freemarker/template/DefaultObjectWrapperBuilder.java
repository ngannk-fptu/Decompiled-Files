/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.ext.beans._BeansAPI;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.DefaultObjectWrapperConfiguration;
import freemarker.template.Version;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

public class DefaultObjectWrapperBuilder
extends DefaultObjectWrapperConfiguration {
    private static final Map<ClassLoader, Map<DefaultObjectWrapperConfiguration, WeakReference<DefaultObjectWrapper>>> INSTANCE_CACHE = new WeakHashMap<ClassLoader, Map<DefaultObjectWrapperConfiguration, WeakReference<DefaultObjectWrapper>>>();
    private static final ReferenceQueue<DefaultObjectWrapper> INSTANCE_CACHE_REF_QUEUE = new ReferenceQueue();

    public DefaultObjectWrapperBuilder(Version incompatibleImprovements) {
        super(incompatibleImprovements);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void clearInstanceCache() {
        Map<ClassLoader, Map<DefaultObjectWrapperConfiguration, WeakReference<DefaultObjectWrapper>>> map = INSTANCE_CACHE;
        synchronized (map) {
            INSTANCE_CACHE.clear();
        }
    }

    public DefaultObjectWrapper build() {
        return _BeansAPI.getBeansWrapperSubclassSingleton(this, INSTANCE_CACHE, INSTANCE_CACHE_REF_QUEUE, DefaultObjectWrapperFactory.INSTANCE);
    }

    private static class DefaultObjectWrapperFactory
    implements _BeansAPI._BeansWrapperSubclassFactory<DefaultObjectWrapper, DefaultObjectWrapperConfiguration> {
        private static final DefaultObjectWrapperFactory INSTANCE = new DefaultObjectWrapperFactory();

        private DefaultObjectWrapperFactory() {
        }

        @Override
        public DefaultObjectWrapper create(DefaultObjectWrapperConfiguration bwConf) {
            return new DefaultObjectWrapper(bwConf, true);
        }
    }
}

