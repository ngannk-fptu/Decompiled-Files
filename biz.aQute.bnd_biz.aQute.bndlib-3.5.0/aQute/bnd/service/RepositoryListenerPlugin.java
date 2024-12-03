/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package aQute.bnd.service;

import aQute.bnd.osgi.Jar;
import aQute.bnd.service.RepositoryPlugin;
import java.io.File;
import org.osgi.annotation.versioning.ConsumerType;

@ConsumerType
public interface RepositoryListenerPlugin {
    public void bundleAdded(RepositoryPlugin var1, Jar var2, File var3);

    public void bundleRemoved(RepositoryPlugin var1, Jar var2, File var3);

    public void repositoryRefreshed(RepositoryPlugin var1);

    public void repositoriesRefreshed();
}

