/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.observation;

import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.Observation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface ObservationHandler<T extends Observation.Context> {
    default public void onStart(T context) {
    }

    default public void onError(T context) {
    }

    default public void onEvent(Observation.Event event, T context) {
    }

    default public void onScopeOpened(T context) {
    }

    default public void onScopeClosed(T context) {
    }

    default public void onScopeReset(T context) {
    }

    default public void onStop(T context) {
    }

    public boolean supportsContext(Observation.Context var1);

    public static class AllMatchingCompositeObservationHandler
    implements CompositeObservationHandler {
        private final List<ObservationHandler<Observation.Context>> handlers;

        @SafeVarargs
        public AllMatchingCompositeObservationHandler(ObservationHandler<? extends Observation.Context> ... handlers) {
            this(Arrays.asList(handlers));
        }

        public AllMatchingCompositeObservationHandler(List<? extends ObservationHandler<? extends Observation.Context>> handlers) {
            ArrayList<ObservationHandler<Observation.Context>> castedHandlers = new ArrayList<ObservationHandler<Observation.Context>>(handlers.size());
            for (ObservationHandler<? extends Observation.Context> observationHandler : handlers) {
                castedHandlers.add(observationHandler);
            }
            this.handlers = castedHandlers;
        }

        @Override
        public List<ObservationHandler<Observation.Context>> getHandlers() {
            return this.handlers;
        }

        @Override
        public void onStart(Observation.Context context) {
            for (ObservationHandler<Observation.Context> handler : this.handlers) {
                if (!handler.supportsContext(context)) continue;
                handler.onStart(context);
            }
        }

        @Override
        public void onError(Observation.Context context) {
            for (ObservationHandler<Observation.Context> handler : this.handlers) {
                if (!handler.supportsContext(context)) continue;
                handler.onError(context);
            }
        }

        @Override
        public void onEvent(Observation.Event event, Observation.Context context) {
            for (ObservationHandler<Observation.Context> handler : this.handlers) {
                if (!handler.supportsContext(context)) continue;
                handler.onEvent(event, context);
            }
        }

        @Override
        public void onScopeOpened(Observation.Context context) {
            for (ObservationHandler<Observation.Context> handler : this.handlers) {
                if (!handler.supportsContext(context)) continue;
                handler.onScopeOpened(context);
            }
        }

        @Override
        public void onScopeClosed(Observation.Context context) {
            for (ObservationHandler<Observation.Context> handler : this.handlers) {
                if (!handler.supportsContext(context)) continue;
                handler.onScopeClosed(context);
            }
        }

        @Override
        public void onScopeReset(Observation.Context context) {
            for (ObservationHandler<Observation.Context> handler : this.handlers) {
                if (!handler.supportsContext(context)) continue;
                handler.onScopeReset(context);
            }
        }

        @Override
        public void onStop(Observation.Context context) {
            for (ObservationHandler<Observation.Context> handler : this.handlers) {
                if (!handler.supportsContext(context)) continue;
                handler.onStop(context);
            }
        }

        @Override
        public boolean supportsContext(Observation.Context context) {
            for (ObservationHandler<Observation.Context> handler : this.handlers) {
                if (!handler.supportsContext(context)) continue;
                return true;
            }
            return false;
        }
    }

    public static class FirstMatchingCompositeObservationHandler
    implements CompositeObservationHandler {
        private final List<ObservationHandler<Observation.Context>> handlers;

        @SafeVarargs
        public FirstMatchingCompositeObservationHandler(ObservationHandler<? extends Observation.Context> ... handlers) {
            this(Arrays.asList(handlers));
        }

        public FirstMatchingCompositeObservationHandler(List<? extends ObservationHandler<? extends Observation.Context>> handlers) {
            ArrayList<ObservationHandler<Observation.Context>> castedHandlers = new ArrayList<ObservationHandler<Observation.Context>>(handlers.size());
            for (ObservationHandler<? extends Observation.Context> observationHandler : handlers) {
                castedHandlers.add(observationHandler);
            }
            this.handlers = castedHandlers;
        }

        @Override
        public List<ObservationHandler<Observation.Context>> getHandlers() {
            return this.handlers;
        }

        @Override
        public void onStart(Observation.Context context) {
            ObservationHandler<Observation.Context> handler = this.getFirstApplicableHandler(context);
            if (handler != null) {
                handler.onStart(context);
            }
        }

        @Override
        public void onError(Observation.Context context) {
            ObservationHandler<Observation.Context> handler = this.getFirstApplicableHandler(context);
            if (handler != null) {
                handler.onError(context);
            }
        }

        @Override
        public void onEvent(Observation.Event event, Observation.Context context) {
            ObservationHandler<Observation.Context> handler = this.getFirstApplicableHandler(context);
            if (handler != null) {
                handler.onEvent(event, context);
            }
        }

        @Override
        public void onScopeOpened(Observation.Context context) {
            ObservationHandler<Observation.Context> handler = this.getFirstApplicableHandler(context);
            if (handler != null) {
                handler.onScopeOpened(context);
            }
        }

        @Override
        public void onScopeClosed(Observation.Context context) {
            ObservationHandler<Observation.Context> handler = this.getFirstApplicableHandler(context);
            if (handler != null) {
                handler.onScopeClosed(context);
            }
        }

        @Override
        public void onScopeReset(Observation.Context context) {
            ObservationHandler<Observation.Context> handler = this.getFirstApplicableHandler(context);
            if (handler != null) {
                handler.onScopeReset(context);
            }
        }

        @Override
        public void onStop(Observation.Context context) {
            ObservationHandler<Observation.Context> handler = this.getFirstApplicableHandler(context);
            if (handler != null) {
                handler.onStop(context);
            }
        }

        @Override
        public boolean supportsContext(Observation.Context context) {
            ObservationHandler<Observation.Context> handler = this.getFirstApplicableHandler(context);
            return handler != null;
        }

        @Nullable
        private ObservationHandler<Observation.Context> getFirstApplicableHandler(Observation.Context context) {
            for (ObservationHandler<Observation.Context> handler : this.handlers) {
                if (!handler.supportsContext(context)) continue;
                return handler;
            }
            return null;
        }
    }

    public static interface CompositeObservationHandler
    extends ObservationHandler<Observation.Context> {
        public List<ObservationHandler<Observation.Context>> getHandlers();
    }
}

