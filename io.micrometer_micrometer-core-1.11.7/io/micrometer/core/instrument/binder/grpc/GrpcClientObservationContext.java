/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.grpc.Metadata
 *  io.grpc.MethodDescriptor$MethodType
 *  io.grpc.Status$Code
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.observation.transport.Propagator$Setter
 *  io.micrometer.observation.transport.RequestReplySenderContext
 */
package io.micrometer.core.instrument.binder.grpc;

import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.transport.Propagator;
import io.micrometer.observation.transport.RequestReplySenderContext;

public class GrpcClientObservationContext
extends RequestReplySenderContext<Metadata, Object> {
    private String serviceName;
    private String methodName;
    private String fullMethodName;
    private MethodDescriptor.MethodType methodType;
    @Nullable
    private Status.Code statusCode;
    private String authority;

    public GrpcClientObservationContext(Propagator.Setter<Metadata> setter) {
        super(setter);
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

    public String getAuthority() {
        return this.authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}

