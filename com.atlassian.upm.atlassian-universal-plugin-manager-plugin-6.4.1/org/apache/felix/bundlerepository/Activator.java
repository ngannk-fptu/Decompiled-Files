/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleActivator
 *  org.osgi.framework.BundleContext
 */
package org.apache.felix.bundlerepository;

import org.apache.felix.bundlerepository.Logger;
import org.apache.felix.bundlerepository.ObrCommandImpl;
import org.apache.felix.bundlerepository.RepositoryAdminImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator
implements BundleActivator {
    private transient BundleContext m_context = null;
    private transient RepositoryAdminImpl m_repoAdmin = null;
    static /* synthetic */ Class class$org$osgi$service$obr$RepositoryAdmin;
    static /* synthetic */ Class class$org$apache$felix$shell$Command;

    public void start(BundleContext context) {
        this.m_context = context;
        this.m_repoAdmin = new RepositoryAdminImpl(this.m_context, new Logger(this.m_context));
        context.registerService((class$org$osgi$service$obr$RepositoryAdmin == null ? (class$org$osgi$service$obr$RepositoryAdmin = Activator.class$("org.osgi.service.obr.RepositoryAdmin")) : class$org$osgi$service$obr$RepositoryAdmin).getName(), (Object)this.m_repoAdmin, null);
        try {
            context.registerService((class$org$apache$felix$shell$Command == null ? (class$org$apache$felix$shell$Command = Activator.class$("org.apache.felix.shell.Command")) : class$org$apache$felix$shell$Command).getName(), (Object)new ObrCommandImpl(this.m_context, this.m_repoAdmin), null);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public void stop(BundleContext context) {
        this.m_repoAdmin.dispose();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

