/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 */
package org.eclipse.gemini.blueprint.io.internal.resolver;

import org.eclipse.gemini.blueprint.io.internal.resolver.ImportedBundle;
import org.osgi.framework.Bundle;

public interface DependencyResolver {
    public ImportedBundle[] getImportedBundles(Bundle var1);
}

