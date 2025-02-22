/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.base.modules;

import org.jfree.base.modules.ModuleInfo;
import org.jfree.base.modules.ModuleInitializeException;
import org.jfree.base.modules.SubSystem;

public interface Module
extends ModuleInfo {
    public ModuleInfo[] getRequiredModules();

    public ModuleInfo[] getOptionalModules();

    public void initialize(SubSystem var1) throws ModuleInitializeException;

    public void configure(SubSystem var1);

    public String getDescription();

    public String getProducer();

    public String getName();

    public String getSubSystem();
}

