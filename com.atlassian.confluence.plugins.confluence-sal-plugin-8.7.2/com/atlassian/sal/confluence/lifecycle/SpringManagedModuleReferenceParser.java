/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.google.common.base.Function
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.ServiceReference
 */
package com.atlassian.sal.confluence.lifecycle;

import com.atlassian.fugue.Option;
import com.atlassian.plugin.ModuleCompleteKey;
import com.google.common.base.Function;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

public class SpringManagedModuleReferenceParser
implements Function<ServiceReference, Option<ModuleCompleteKey>> {
    public Option<ModuleCompleteKey> apply(ServiceReference serviceReference) {
        Bundle bundle = serviceReference.getBundle();
        if (bundle == null) {
            return Option.none();
        }
        String pluginKey = bundle.getSymbolicName();
        String moduleKey = (String)serviceReference.getProperty("org.eclipse.gemini.blueprint.bean.name");
        if (moduleKey == null) {
            return Option.none();
        }
        try {
            return Option.some((Object)new ModuleCompleteKey(pluginKey, moduleKey));
        }
        catch (IllegalArgumentException e) {
            return Option.none();
        }
    }
}

