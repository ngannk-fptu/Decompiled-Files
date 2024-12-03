/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.MetaClassRegistryChangeEvent;
import java.util.EventListener;

public interface MetaClassRegistryChangeEventListener
extends EventListener {
    public void updateConstantMetaClass(MetaClassRegistryChangeEvent var1);
}

