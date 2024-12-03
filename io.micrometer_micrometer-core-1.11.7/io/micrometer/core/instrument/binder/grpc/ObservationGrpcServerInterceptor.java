/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.grpc.Metadata
 *  io.grpc.Metadata$AsciiMarshaller
 *  io.grpc.Metadata$Key
 *  io.grpc.MethodDescriptor
 *  io.grpc.MethodDescriptor$MethodType
 *  io.grpc.ServerCall
 *  io.grpc.ServerCall$Listener
 *  io.grpc.ServerCallHandler
 *  io.grpc.ServerInterceptor
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.observation.Observation
 *  io.micrometer.observation.ObservationRegistry
 *  io.micrometer.observation.transport.Propagator$Getter
 */
package io.micrometer.core.instrument.binder.grpc;

import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.binder.grpc.DefaultGrpcServerObservationConvention;
import io.micrometer.core.instrument.binder.grpc.GrpcObservationDocumentation;
import io.micrometer.core.instrument.binder.grpc.GrpcServerObservationContext;
import io.micrometer.core.instrument.binder.grpc.GrpcServerObservationConvention;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerCall;
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcServerCallListener;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.transport.Propagator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class ObservationGrpcServerInterceptor
implements ServerInterceptor {
    private static final GrpcServerObservationConvention DEFAULT_CONVENTION = new DefaultGrpcServerObservationConvention();
    private static final Map<String, Metadata.Key<String>> KEY_CACHE = new ConcurrentHashMap<String, Metadata.Key<String>>();
    private final ObservationRegistry registry;
    @Nullable
    private GrpcServerObservationConvention customConvention;

    public ObservationGrpcServerInterceptor(ObservationRegistry registry) {
        this.registry = registry;
    }

    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        Supplier<GrpcServerObservationContext> contextSupplier = () -> {
            GrpcServerObservationContext context = new GrpcServerObservationContext((Propagator.Getter<Metadata>)((Propagator.Getter)(carrier, keyName) -> {
                Metadata.Key key = KEY_CACHE.computeIfAbsent(keyName, k -> Metadata.Key.of((String)keyName, (Metadata.AsciiMarshaller)Metadata.ASCII_STRING_MARSHALLER));
                return (String)carrier.get(key);
            }));
            context.setCarrier(headers);
            MethodDescriptor methodDescriptor = call.getMethodDescriptor();
            String serviceName = methodDescriptor.getServiceName();
            String methodName = methodDescriptor.getBareMethodName();
            String fullMethodName = methodDescriptor.getFullMethodName();
            MethodDescriptor.MethodType methodType = methodDescriptor.getType();
            if (serviceName != null) {
                context.setServiceName(serviceName);
            }
            if (methodName != null) {
                context.setMethodName(methodName);
            }
            context.setFullMethodName(fullMethodName);
            context.setMethodType(methodType);
            context.setAuthority(call.getAuthority());
            return context;
        };
        Observation observation = GrpcObservationDocumentation.SERVER.observation(this.customConvention, DEFAULT_CONVENTION, contextSupplier, this.registry).start();
        if (observation.isNoop()) {
            return next.startCall(call, headers);
        }
        ObservationGrpcServerCall<ReqT, RespT> serverCall = new ObservationGrpcServerCall<ReqT, RespT>(call, observation);
        try {
            ServerCall.Listener result = next.startCall(serverCall, headers);
            return new ObservationGrpcServerCallListener(result, observation);
        }
        catch (Exception ex) {
            observation.error((Throwable)ex).stop();
            throw ex;
        }
    }

    public void setCustomConvention(@Nullable GrpcServerObservationConvention customConvention) {
        this.customConvention = customConvention;
    }
}

