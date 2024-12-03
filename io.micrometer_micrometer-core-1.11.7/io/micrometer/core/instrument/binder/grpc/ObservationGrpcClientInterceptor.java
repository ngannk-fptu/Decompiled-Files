/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.grpc.CallOptions
 *  io.grpc.Channel
 *  io.grpc.ClientCall
 *  io.grpc.ClientInterceptor
 *  io.grpc.Metadata
 *  io.grpc.Metadata$AsciiMarshaller
 *  io.grpc.Metadata$Key
 *  io.grpc.MethodDescriptor
 *  io.grpc.MethodDescriptor$MethodType
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.observation.Observation
 *  io.micrometer.observation.ObservationRegistry
 *  io.micrometer.observation.transport.Propagator$Setter
 */
package io.micrometer.core.instrument.binder.grpc;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.binder.grpc.DefaultGrpcClientObservationConvention;
import io.micrometer.core.instrument.binder.grpc.GrpcClientObservationContext;
import io.micrometer.core.instrument.binder.grpc.GrpcClientObservationConvention;
import io.micrometer.core.instrument.binder.grpc.GrpcObservationDocumentation;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcClientCall;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.transport.Propagator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class ObservationGrpcClientInterceptor
implements ClientInterceptor {
    private static final GrpcClientObservationConvention DEFAULT_CONVENTION = new DefaultGrpcClientObservationConvention();
    private static final Map<String, Metadata.Key<String>> KEY_CACHE = new ConcurrentHashMap<String, Metadata.Key<String>>();
    private final ObservationRegistry registry;
    @Nullable
    private GrpcClientObservationConvention customConvention;

    public ObservationGrpcClientInterceptor(ObservationRegistry registry) {
        this.registry = registry;
    }

    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        Supplier<GrpcClientObservationContext> contextSupplier = () -> {
            GrpcClientObservationContext context = new GrpcClientObservationContext((Propagator.Setter<Metadata>)((Propagator.Setter)(carrier, keyName, value) -> {
                Metadata.Key key = KEY_CACHE.computeIfAbsent(keyName, k -> Metadata.Key.of((String)keyName, (Metadata.AsciiMarshaller)Metadata.ASCII_STRING_MARSHALLER));
                carrier.removeAll(key);
                carrier.put(key, (Object)value);
            }));
            String serviceName = method.getServiceName();
            String methodName = method.getBareMethodName();
            String fullMethodName = method.getFullMethodName();
            MethodDescriptor.MethodType methodType = method.getType();
            if (serviceName != null) {
                context.setServiceName(serviceName);
            }
            if (methodName != null) {
                context.setMethodName(methodName);
            }
            context.setFullMethodName(fullMethodName);
            context.setMethodType(methodType);
            context.setAuthority(next.authority());
            return context;
        };
        Observation observation = GrpcObservationDocumentation.CLIENT.observation(this.customConvention, DEFAULT_CONVENTION, contextSupplier, this.registry);
        if (observation.isNoop()) {
            return next.newCall(method, callOptions);
        }
        return new ObservationGrpcClientCall(next.newCall(method, callOptions), observation);
    }

    public void setCustomConvention(@Nullable GrpcClientObservationConvention customConvention) {
        this.customConvention = customConvention;
    }
}

