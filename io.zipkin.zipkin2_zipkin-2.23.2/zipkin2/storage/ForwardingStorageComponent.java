/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.storage;

import java.io.IOException;
import zipkin2.CheckResult;
import zipkin2.storage.AutocompleteTags;
import zipkin2.storage.ServiceAndSpanNames;
import zipkin2.storage.SpanConsumer;
import zipkin2.storage.SpanStore;
import zipkin2.storage.StorageComponent;
import zipkin2.storage.Traces;

public abstract class ForwardingStorageComponent
extends StorageComponent {
    protected ForwardingStorageComponent() {
    }

    protected abstract StorageComponent delegate();

    @Override
    public SpanConsumer spanConsumer() {
        return this.delegate().spanConsumer();
    }

    @Override
    public Traces traces() {
        return this.delegate().traces();
    }

    @Override
    public SpanStore spanStore() {
        return this.delegate().spanStore();
    }

    @Override
    public AutocompleteTags autocompleteTags() {
        return this.delegate().autocompleteTags();
    }

    @Override
    public ServiceAndSpanNames serviceAndSpanNames() {
        return this.delegate().serviceAndSpanNames();
    }

    @Override
    public CheckResult check() {
        return this.delegate().check();
    }

    @Override
    public boolean isOverCapacity(Throwable e) {
        return this.delegate().isOverCapacity(e);
    }

    @Override
    public void close() throws IOException {
        this.delegate().close();
    }

    public String toString() {
        return this.delegate().toString();
    }
}

