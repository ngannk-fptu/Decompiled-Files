/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.grpc.Metadata
 *  io.grpc.MethodDescriptor$MethodType
 *  io.grpc.Status$Code
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.observation.transport.Propagator$Getter
 *  io.micrometer.observation.transport.RequestReplyReceiverContext
 */
package io.micrometer.core.instrument.binder.grpc;

import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.transport.Propagator;
import io.micrometer.observation.transport.RequestReplyReceiverContext;

public class GrpcServerObservationContext
extends RequestReplyReceiverContext<Metadata, Object> {
    private String serviceName;
    private String methodName;
    private String fullMethodName;
    private MethodDescriptor.MethodType methodType;
    @Nullable
    private Status.Code statusCode;
    @Nullable
    private String authority;

    public GrpcServerObservationContext(Propagator.Getter<Metadata> getter) {
        super(getter);
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getFullMethodName() {
        return this.fullMethodName;
    }

    public void setFullMethodName(String fullMethodName) {
        this.fullMethodName = fullMethodName;
    }

    public MethodDescriptor.MethodType getMethodType() {
        return this.methodType;
    }

    public void setMethodType(MethodDescriptor.MethodType methodType) {
        this.methodType = methodType;
    }

    @Nullable
    public Status.Code getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(Status.Code statusCode) {
        this.statusCode = statusCode;
    }

    @Nullable
    public String getAuthority() {
        return this.authority;
    }

    public void setAuthority(@Nullable String authority) {
        this.authority = authority;
    }
}

