/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.beanutils.DynaBean
 *  org.apache.commons.beanutils.DynaClass
 *  org.apache.commons.beanutils.DynaProperty
 */
package org.apache.commons.configuration2.builder.combined;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

class MultiWrapDynaClass
implements DynaClass {
    private static final DynaProperty[] EMPTY_PROPS = new DynaProperty[0];
    private final Collection<DynaProperty> properties = new LinkedList<DynaProperty>();
    private final Map<String, DynaProperty> namedProperties = new HashMap<String, DynaProperty>();

    public MultiWrapDynaClass(Collection<? extends DynaClass> wrappedCls) {
        this.initProperties(wrappedCls);
    }

    public String getName() {
        return null;
    }

    public DynaProperty getDynaProperty(String name) {
        return this.namedProperties.get(name);
    }

    public DynaProperty[] getDynaProperties() {
        return this.properties.toArray(EMPTY_PROPS);
    }

    public DynaBean newInstance() throws IllegalAccessException, InstantiationException {
        throw new UnsupportedOperationException("Cannot create an instance of MultiWrapDynaBean!");
    }

    private void initProperties(Collection<? extends DynaClass> wrappedCls) {
        wrappedCls.forEach(cls -> Stream.of(cls.getDynaProperties()).forEach(p -> {
            this.properties.add((DynaProperty)p);
            this.namedProperties.put(p.getName(), (DynaProperty)p);
        }));
    }
}

