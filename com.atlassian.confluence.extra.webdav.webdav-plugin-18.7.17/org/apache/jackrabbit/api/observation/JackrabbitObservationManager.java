/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.api.observation;

import javax.jcr.RepositoryException;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;
import org.apache.jackrabbit.api.observation.JackrabbitEventFilter;

public interface JackrabbitObservationManager
extends ObservationManager {
    public void addEventListener(EventListener var1, JackrabbitEventFilter var2) throws RepositoryException;
}

