/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 */
package org.eclipse.gemini.blueprint.extender.internal.activator;

import org.eclipse.gemini.blueprint.extender.internal.activator.ApplicationContextConfigurationFactory;
import org.eclipse.gemini.blueprint.extender.support.ApplicationContextConfiguration;
import org.osgi.framework.Bundle;

public class DefaultApplicationContextConfigurationFactory
implements ApplicationContextConfigurationFactory {
    @Override
    public ApplicationContextConfiguration createConfiguration(Bundle bundle) {
        return new ApplicationContextConfiguration(bundle);
    }
}

