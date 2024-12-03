/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.MetaClass;
import groovy.lang.MetaClassRegistry;
import java.util.EventObject;

public class MetaClassRegistryChangeEvent
extends EventObject {
    private final Class clazz;
    private final Object instance;
    private final MetaClass metaClass;
    private final MetaClass oldMetaClass;

    public MetaClassRegistryChangeEvent(Object source, Object instance, Class clazz, MetaClass oldMetaClass, MetaClass newMetaClass) {
        super(source);
        this.clazz = clazz;
        this.metaClass = newMetaClass;
        this.oldMetaClass = oldMetaClass;
        this.instance = instance;
    }

    public Class getClassToUpdate() {
        return this.clazz;
    }

    public MetaClass getNewMetaClass() {
        return this.metaClass;
    }

    public MetaClass getOldMetaClass() {
        return this.oldMetaClass;
    }

    public boolean isPerInstanceMetaClassChange() {
        return this.instance != null;
    }

    public Object getInstance() {
        return this.instance;
    }

    public MetaClassRegistry getRegistry() {
        return (MetaClassRegistry)this.source;
    }
}

