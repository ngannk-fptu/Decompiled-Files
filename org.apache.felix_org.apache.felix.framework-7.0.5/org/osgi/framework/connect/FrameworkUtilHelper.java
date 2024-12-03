/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.framework.connect;

import java.util.Optional;
import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.framework.Bundle;

@ConsumerType
public interface FrameworkUtilHelper {
    default public Optional<Bundle> getBundle(Class<?> classFromBundle) {
        return Optional.empty();
    }
}

