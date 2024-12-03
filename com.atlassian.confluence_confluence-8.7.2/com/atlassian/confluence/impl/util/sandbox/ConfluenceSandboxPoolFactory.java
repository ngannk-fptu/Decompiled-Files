/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  com.atlassian.util.concurrent.LazyReference
 *  com.google.errorprone.annotations.concurrent.GuardedBy
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.confluence.impl.util.sandbox;

import com.atlassian.confluence.impl.util.sandbox.SandboxLocalProcessPool;
import com.atlassian.confluence.impl.util.sandbox.SandboxPool;
import com.atlassian.confluence.impl.util.sandbox.SandboxPoolConfiguration;
import com.atlassian.confluence.impl.util.sandbox.SandboxPoolFactory;
import com.atlassian.confluence.impl.util.sandbox.SandboxRequest;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import com.atlassian.util.concurrent.LazyReference;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.DisposableBean;

class ConfluenceSandboxPoolFactory
implements SandboxPoolFactory,
DisposableBean {
    @GuardedBy(value="this")
    private final List<SandboxPool> pools = new ArrayList<SandboxPool>();
    private final FilesystemPath homeDirectory;
    @GuardedBy(value="this")
    private boolean shutdown = false;

    public ConfluenceSandboxPoolFactory(FilesystemPath homeDirectory) {
        this.homeDirectory = Objects.requireNonNull(homeDirectory);
    }

    @Override
    public SandboxPool create(SandboxPoolConfiguration configuration) {
        return new LazySandbox(configuration);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void destroy() throws Exception {
        ConfluenceSandboxPoolFactory confluenceSandboxPoolFactory = this;
        synchronized (confluenceSandboxPoolFactory) {
            this.shutdown = true;
            this.pools.forEach(SandboxPool::shutdown);
        }
    }

    private class LazySandbox
    implements SandboxPool {
        private final LazyReference<SandboxPool> reference;
        private final SandboxPoolConfiguration configuration;

        private LazySandbox(final SandboxPoolConfiguration configuration) {
            this.configuration = configuration;
            this.reference = new LazyReference<SandboxPool>(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                protected SandboxPool create() {
                    ConfluenceSandboxPoolFactory confluenceSandboxPoolFactory = ConfluenceSandboxPoolFactory.this;
                    synchronized (confluenceSandboxPoolFactory) {
                        if (ConfluenceSandboxPoolFactory.this.shutdown) {
                            throw new IllegalArgumentException("The service is being shutdown or has been shutdown");
                        }
                        SandboxLocalProcessPool sandbox = new SandboxLocalProcessPool(ConfluenceSandboxPoolFactory.this.homeDirectory.path(new String[]{"sandbox"}), configuration);
                        ConfluenceSandboxPoolFactory.this.pools.add(sandbox);
                        return sandbox;
                    }
                }
            };
        }

        @Override
        public <T, R> R execute(SandboxRequest<T, R> request) {
            return ((SandboxPool)this.reference.get()).execute(request);
        }

        @Override
        public void shutdown() {
            ((SandboxPool)this.reference.get()).shutdown();
        }

        @Override
        public SandboxPoolConfiguration getConfiguration() {
            return this.configuration;
        }
    }
}

