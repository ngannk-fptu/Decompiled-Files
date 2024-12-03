/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.ExpandoMetaClass;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassRegistry;
import org.codehaus.groovy.reflection.ClassInfo;

public class ExpandoMetaClassCreationHandle
extends MetaClassRegistry.MetaClassCreationHandle {
    public static final ExpandoMetaClassCreationHandle instance = new ExpandoMetaClassCreationHandle();

    @Override
    protected MetaClass createNormalMetaClass(Class theClass, MetaClassRegistry registry) {
        if (theClass != ExpandoMetaClass.class) {
            return new ExpandoMetaClass(theClass, true, true);
        }
        return super.createNormalMetaClass(theClass, registry);
    }

    public void registerModifiedMetaClass(ExpandoMetaClass emc) {
        Class klazz = emc.getJavaClass();
        GroovySystem.getMetaClassRegistry().setMetaClass(klazz, emc);
    }

    public boolean hasModifiedMetaClass(ExpandoMetaClass emc) {
        return emc.getClassInfo().getModifiedExpando() != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void enable() {
        MetaClassRegistry metaClassRegistry;
        MetaClassRegistry metaClassRegistry2 = metaClassRegistry = GroovySystem.getMetaClassRegistry();
        synchronized (metaClassRegistry2) {
            if (metaClassRegistry.getMetaClassCreationHandler() != instance) {
                ClassInfo.clearModifiedExpandos();
                metaClassRegistry.setMetaClassCreationHandle(instance);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void disable() {
        MetaClassRegistry metaClassRegistry;
        MetaClassRegistry metaClassRegistry2 = metaClassRegistry = GroovySystem.getMetaClassRegistry();
        synchronized (metaClassRegistry2) {
            if (metaClassRegistry.getMetaClassCreationHandler() == instance) {
                ClassInfo.clearModifiedExpandos();
                metaClassRegistry.setMetaClassCreationHandle(new MetaClassRegistry.MetaClassCreationHandle());
            }
        }
    }
}

