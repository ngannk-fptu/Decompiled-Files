/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package aQute.bnd.service.action;

import aQute.bnd.build.Project;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface Action {
    public void execute(Project var1, String var2) throws Exception;

    public void execute(Project var1, Object ... var2) throws Exception;
}

