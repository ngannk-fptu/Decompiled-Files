/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  reactor.netty.http.HttpResources
 *  reactor.netty.resources.ConnectionProvider
 *  reactor.netty.resources.LoopResources
 */
package org.springframework.http.client.reactive;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.netty.http.HttpResources;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

public class ReactorResourceFactory
implements InitializingBean,
DisposableBean {
    private boolean useGlobalResources = true;
    @Nullable
    private Consumer<HttpResources> globalResourcesConsumer;
    private Supplier<ConnectionProvider> connectionProviderSupplier = () -> ConnectionProvider.create((String)"webflux", (int)500);
    @Nullable
    private ConnectionProvider connectionProvider;
    private Supplier<LoopResources> loopResourcesSupplier = () -> LoopResources.create((String)"webflux-http");
    @Nullable
    private LoopResources loopResources;
    private boolean manageConnectionProvider = false;
    private boolean manageLoopResources = false;
    private Duration shutdownQuietPeriod = Duration.ofSeconds(LoopResources.DEFAULT_SHUTDOWN_QUIET_PERIOD);
    private Duration shutdownTimeout = Duration.ofSeconds(LoopResources.DEFAULT_SHUTDOWN_TIMEOUT);

    public void setUseGlobalResources(boolean useGlobalResources) {
        this.useGlobalResources = useGlobalResources;
    }

    public boolean isUseGlobalResources() {
        return this.useGlobalResources;
    }

    public void addGlobalResourcesConsumer(Consumer<HttpResources> consumer) {
        this.useGlobalResources = true;
        this.globalResourcesConsumer = this.globalResourcesConsumer != null ? this.globalResourcesConsumer.andThen(consumer) : consumer;
    }

    public void setConnectionProviderSupplier(Supplier<ConnectionProvider> supplier) {
        this.connectionProviderSupplier = supplier;
    }

    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public ConnectionProvider getConnectionProvider() {
        Assert.state((this.connectionProvider != null ? 1 : 0) != 0, (String)"ConnectionProvider not initialized yet");
        return this.connectionProvider;
    }

    public void setLoopResourcesSupplier(Supplier<LoopResources> supplier) {
        this.loopResourcesSupplier = supplier;
    }

    public void setLoopResources(LoopResources loopResources) {
        this.loopResources = loopResources;
    }

    public LoopResources getLoopResources() {
        Assert.state((this.loopResources != null ? 1 : 0) != 0, (String)"LoopResources not initialized yet");
        return this.loopResources;
    }

    public void setShutdownQuietPeriod(Duration shutdownQuietPeriod) {
        Assert.notNull((Object)shutdownQuietPeriod, (String)"shutdownQuietPeriod should not be null");
        this.shutdownQuietPeriod = shutdownQuietPeriod;
    }

    public void setShutdownTimeout(Duration shutdownTimeout) {
        Assert.notNull((Object)shutdownTimeout, (String)"shutdownTimeout should not be null");
        this.shutdownTimeout = shutdownTimeout;
    }

    public void afterPropertiesSet() {
        if (this.useGlobalResources) {
            Assert.isTrue((this.loopResources == null && this.connectionProvider == null ? 1 : 0) != 0, (String)"'useGlobalResources' is mutually exclusive with explicitly configured resources");
            HttpResources httpResources = HttpResources.get();
            if (this.globalResourcesConsumer != null) {
                this.globalResourcesConsumer.accept(httpResources);
            }
            this.connectionProvider = httpResources;
            this.loopResources = httpResources;
        } else {
            if (this.loopResources == null) {
                this.manageLoopResources = true;
                this.loopResources = this.loopResourcesSupplier.get();
            }
            if (this.connectionProvider == null) {
                this.manageConnectionProvider = true;
                this.connectionProvider = this.connectionProviderSupplier.get();
            }
        }
    }

    public void destroy() {
        if (this.useGlobalResources) {
            HttpResources.disposeLoopsAndConnectionsLater((Duration)this.shutdownQuietPeriod, (Duration)this.shutdownTimeout).block();
        } else {
            try {
                ConnectionProvider provider = this.connectionProvider;
                if (provider != null && this.manageConnectionProvider) {
                    provider.disposeLater().block();
                }
            }
            catch (Throwable provider) {
                // empty catch block
            }
            try {
                LoopResources resources = this.loopResources;
                if (resources != null && this.manageLoopResources) {
                    resources.disposeLater(this.shutdownQuietPeriod, this.shutdownTimeout).block();
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }
}

