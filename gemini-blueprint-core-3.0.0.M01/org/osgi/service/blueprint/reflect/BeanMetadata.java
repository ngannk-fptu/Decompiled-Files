/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.blueprint.reflect;

import java.util.List;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.Target;

public interface BeanMetadata
extends Target,
ComponentMetadata {
    public static final String SCOPE_SINGLETON = "singleton";
    public static final String SCOPE_PROTOTYPE = "prototype";

    public String getClassName();

    public String getInitMethod();

    public String getDestroyMethod();

    public List getArguments();

    public List getProperties();

    public String getFactoryMethod();

    public Target getFactoryComponent();

    public String getScope();
}

