/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.blueprint.reflect;

import java.util.Collection;
import org.osgi.service.blueprint.reflect.ComponentMetadata;

public interface ServiceReferenceMetadata
extends ComponentMetadata {
    public static final int AVAILABILITY_MANDATORY = 1;
    public static final int AVAILABILITY_OPTIONAL = 2;

    public int getAvailability();

    public String getInterface();

    public String getComponentName();

    public String getFilter();

    public Collection getReferenceListeners();
}

