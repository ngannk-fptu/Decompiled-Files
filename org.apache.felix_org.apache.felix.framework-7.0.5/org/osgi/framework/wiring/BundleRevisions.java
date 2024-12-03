/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.framework.wiring;

import java.util.List;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.BundleReference;
import org.osgi.framework.wiring.BundleRevision;

@ProviderType
public interface BundleRevisions
extends BundleReference {
    public List<BundleRevision> getRevisions();
}

