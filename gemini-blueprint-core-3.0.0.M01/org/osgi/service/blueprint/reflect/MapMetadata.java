/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.blueprint.reflect;

import java.util.List;
import org.osgi.service.blueprint.reflect.NonNullMetadata;

public interface MapMetadata
extends NonNullMetadata {
    public String getKeyType();

    public String getValueType();

    public List getEntries();
}

