/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Filter
 */
package org.eclipse.gemini.blueprint.service.importer;

import org.osgi.framework.Filter;

public interface OsgiServiceDependency {
    public Filter getServiceFilter();

    public String getBeanName();

    public boolean isMandatory();
}

