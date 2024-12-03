/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.dependency;

import java.util.EventListener;
import org.eclipse.gemini.blueprint.service.importer.OsgiServiceDependency;

public interface ImporterStateListener
extends EventListener {
    public void importerSatisfied(Object var1, OsgiServiceDependency var2);

    public void importerUnsatisfied(Object var1, OsgiServiceDependency var2);
}

