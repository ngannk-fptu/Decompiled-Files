/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.blueprint.reflect;

import java.util.List;
import org.osgi.service.blueprint.reflect.NonNullMetadata;

public interface ComponentMetadata
extends NonNullMetadata {
    public static final int ACTIVATION_EAGER = 1;
    public static final int ACTIVATION_LAZY = 2;

    public String getId();

    public int getActivation();

    public List getDependsOn();
}

