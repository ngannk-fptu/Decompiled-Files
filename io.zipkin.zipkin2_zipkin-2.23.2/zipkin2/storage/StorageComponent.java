/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.storage;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Logger;
import zipkin2.Call;
import zipkin2.Component;
import zipkin2.internal.TracesAdapter;
import zipkin2.storage.AutocompleteTags;
import zipkin2.storage.ServiceAndSpanNames;
import zipkin2.storage.SpanConsumer;
import zipkin2.storage.SpanStore;
import zipkin2.storage.Traces;

public abstract class StorageComponent
extends Component {
    public Traces traces() {
        return new TracesAdapter(this.spanStore());
    }

    public abstract SpanStore spanStore();

    public AutocompleteTags autocompleteTags() {
        return new AutocompleteTags(){

            @Override
            public Call<List<String>> getKeys() {
                return Call.emptyList();
            }

            @Override
            public Call<List<String>> getValues(String key) {
                return Call.emptyList();
            }

            public String toString() {
                return "EmptyAutocompleteTags{}";
            }
        };
    }

    public ServiceAndSpanNames serviceAndSpanNames() {
        final SpanStore delegate = this.spanStore();
        return new ServiceAndSpanNames(){

            @Override
            public Call<List<String>> getServiceNames() {
                return delegate.getServiceNames();
            }

            @Override
            public Call<List<String>> getRemoteServiceNames(String serviceName) {
                return Call.emptyList();
            }

            @Override
            public Call<List<String>> getSpanNames(String serviceName) {
                return delegate.getSpanNames(serviceName);
            }

            public String toString() {
                return "ServiceAndSpanNames{" + delegate + "}";
            }
        };
    }

    public abstract SpanConsumer spanConsumer();

    public boolean isOverCapacity(Throwable e) {
        return e instanceof RejectedExecutionException;
    }

    public static abstract class Builder {
        public abstract Builder strictTraceId(boolean var1);

        public abstract Builder searchEnabled(boolean var1);

        public Builder autocompleteKeys(List<String> keys) {
            Logger.getLogger(this.getClass().getName()).info("autocompleteKeys not yet supported");
            return this;
        }

        public Builder autocompleteTtl(int autocompleteTtl) {
            Logger.getLogger(this.getClass().getName()).info("autocompleteTtl not yet supported");
            return this;
        }

        public Builder autocompleteCardinality(int autocompleteCardinality) {
            Logger.getLogger(this.getClass().getName()).info("autocompleteCardinality not yet supported");
            return this;
        }

        public abstract StorageComponent build();
    }
}

