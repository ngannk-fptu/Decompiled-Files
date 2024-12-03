/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.rest.representations;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Change;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.core.rest.representations.BaseRepresentationFactory;
import com.atlassian.upm.core.rest.representations.ChangesRequiringRestartRepresentation;
import com.atlassian.upm.core.rest.representations.PluginCollectionRepresentation;
import com.atlassian.upm.core.rest.representations.PluginModuleRepresentation;
import com.atlassian.upm.core.rest.representations.PluginRepresentation;
import com.atlassian.upm.core.rest.representations.PluginSummaryRepresentation;
import com.atlassian.upm.core.rest.representations.VendorRepresentation;
import com.atlassian.upm.core.rest.resources.RequestContext;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface BasePluginRepresentationFactory
extends BaseRepresentationFactory {
    public VendorRepresentation createVendorRepresentation(Plugin var1);

    public PluginCollectionRepresentation createInstalledPluginCollectionRepresentation(Locale var1, List<Plugin> var2, Map<String, UpmAppManager.ApplicationDescriptorModuleInfo> var3, RequestContext var4);

    public PluginRepresentation createPluginRepresentation(Plugin var1);

    public PluginSummaryRepresentation createPluginSummaryRepresentation(Plugin var1, Option<UpmAppManager.ApplicationDescriptorModuleInfo> var2);

    public PluginModuleRepresentation createPluginModuleRepresentation(Plugin.Module var1);

    public ChangesRequiringRestartRepresentation createChangesRequiringRestartRepresentation(Iterable<Change> var1);
}

