/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.ServiceReference
 */
package org.eclipse.gemini.blueprint.service.importer;

import org.osgi.framework.ServiceReference;

public interface ServiceReferenceProxy
extends ServiceReference {
    public ServiceReference getTargetServiceReference();
}

