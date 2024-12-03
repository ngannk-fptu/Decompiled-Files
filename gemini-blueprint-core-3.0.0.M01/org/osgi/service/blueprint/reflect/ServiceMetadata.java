/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.blueprint.reflect;

import java.util.Collection;
import java.util.List;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.Target;

public interface ServiceMetadata
extends ComponentMetadata {
    public static final int AUTO_EXPORT_DISABLED = 1;
    public static final int AUTO_EXPORT_INTERFACES = 2;
    public static final int AUTO_EXPORT_CLASS_HIERARCHY = 3;
    public static final int AUTO_EXPORT_ALL_CLASSES = 4;

    public Target getServiceComponent();

    public List getInterfaces();

    public int getAutoExport();

    public List getServiceProperties();

    public int getRanking();

    public Collection getRegistrationListeners();
}

