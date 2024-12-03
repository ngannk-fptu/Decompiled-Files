/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 */
package org.eclipse.gemini.blueprint.io.internal.resolver;

import org.osgi.framework.Bundle;

public class ImportedBundle {
    private final Bundle importingBundle;
    private final String[] importedPackages;

    public ImportedBundle(Bundle importingBundle, String[] importedPackages) {
        this.importingBundle = importingBundle;
        this.importedPackages = importedPackages;
    }

    public Bundle getBundle() {
        return this.importingBundle;
    }

    public String[] getImportedPackages() {
        return this.importedPackages;
    }

    public String toString() {
        return this.importingBundle.toString();
    }
}

