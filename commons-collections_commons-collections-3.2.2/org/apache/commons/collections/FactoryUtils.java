/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.functors.ConstantFactory;
import org.apache.commons.collections.functors.ExceptionFactory;
import org.apache.commons.collections.functors.InstantiateFactory;
import org.apache.commons.collections.functors.PrototypeFactory;

public class FactoryUtils {
    public static Factory exceptionFactory() {
        return ExceptionFactory.INSTANCE;
    }

    public static Factory nullFactory() {
        return ConstantFactory.NULL_INSTANCE;
    }

    public static Factory constantFactory(Object constantToReturn) {
        return ConstantFactory.getInstance(constantToReturn);
    }

    public static Factory prototypeFactory(Object prototype) {
        return PrototypeFactory.getInstance(prototype);
    }

    public static Factory instantiateFactory(Class classToInstantiate) {
        return InstantiateFactory.getInstance(classToInstantiate, null, null);
    }

    public static Factory instantiateFactory(Class classToInstantiate, Class[] paramTypes, Object[] args) {
        return InstantiateFactory.getInstance(classToInstantiate, paramTypes, args);
    }
}

