/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.obr;

import java.net.URL;
import org.osgi.service.obr.Repository;
import org.osgi.service.obr.Resolver;
import org.osgi.service.obr.Resource;

public interface RepositoryAdmin {
    public Resource[] discoverResources(String var1);

    public Resolver resolver();

    public Repository addRepository(URL var1) throws Exception;

    public boolean removeRepository(URL var1);

    public Repository[] listRepositories();

    public Resource getResource(String var1);
}

