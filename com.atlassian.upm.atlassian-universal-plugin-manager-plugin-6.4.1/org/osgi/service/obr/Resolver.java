/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.obr;

import org.osgi.service.obr.Requirement;
import org.osgi.service.obr.Resource;

public interface Resolver {
    public void add(Resource var1);

    public Requirement[] getUnsatisfiedRequirements();

    public Resource[] getOptionalResources();

    public Requirement[] getReason(Resource var1);

    public Resource[] getResources(Requirement var1);

    public Resource[] getRequiredResources();

    public Resource[] getAddedResources();

    public boolean resolve();

    public void deploy(boolean var1);
}

