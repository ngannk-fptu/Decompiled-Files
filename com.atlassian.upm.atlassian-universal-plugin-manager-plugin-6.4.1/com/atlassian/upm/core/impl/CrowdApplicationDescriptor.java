/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.property.PropertyManager
 *  com.google.common.base.Supplier
 */
package com.atlassian.upm.core.impl;

import com.atlassian.crowd.manager.property.PropertyManager;
import com.atlassian.upm.core.impl.AbstractApplicationDescriptor;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.google.common.base.Supplier;

public class CrowdApplicationDescriptor
extends AbstractApplicationDescriptor {
    public CrowdApplicationDescriptor(UpmAppManager upmAppManager, PropertyManager propertyManager) {
        super(upmAppManager, (Supplier<Integer>)((Supplier)() -> ((PropertyManager)propertyManager).getCurrentLicenseResourceTotal()));
    }
}

