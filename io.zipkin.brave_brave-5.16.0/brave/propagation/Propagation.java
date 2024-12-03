/*
 * Decompiled with CFR 0.152.
 */
package brave.propagation;

import brave.Span;
import brave.internal.Nullable;
import brave.propagation.B3Propagation;
import brave.propagation.B3SinglePropagation;
import brave.propagation.TraceContext;
import java.util.List;

public interface Propagation<K> {
    public static final Propagation<String> B3_STRING = B3Propagation.get();
    @Deprecated
    public static final Propagation<String> B3_SINGLE_STRING = B3SinglePropagation.FACTORY.get();

    public List<K> keys();

    public <R> TraceContext.Injector<R> injector(Setter<R, K> var1);

    public <R> TraceContext.Extractor<R> extractor(Getter<R, K> var1);

    public static interface RemoteGetter<R>
    extends Getter<R, String> {
        public Span.Kind spanKind();

        @Override
        @Nullable
        public String get(R var1, String var2);
    }

    public static interface RemoteSetter<R>
    extends Setter<R, String> {
        public Span.Kind spanKind();

        @Override
        public void put(R var1, String var2, String var3);
    }

    public static interface Getter<R, K> {
        @Nullable
        public String get(R var1, K var2);
    }

    public static interface Setter<R, K> {
        public void put(R var1, K var2, String var3);
    }

    @Deprecated
    public static interface KeyFactory<K> {
        public static final KeyFactory<String> STRING = new KeyFactory<String>(){

            @Override
            public String create(String name) {
                return name;
            }

            public String toString() {
                return "StringKeyFactory{}";
            }
        };

        public K create(String var1);
    }

    public static abstract class Factory {
        public boolean supportsJoin() {
            return false;
        }

        public boolean requires128BitTraceId() {
            return false;
        }

        @Deprecated
        public abstract <K> Propagation<K> create(KeyFactory<K> var1);

        public Propagation<String> get() {
            return this.create(KeyFactory.STRING);
        }

        public TraceContext decorate(TraceContext context) {
            return context;
        }
    }
}

