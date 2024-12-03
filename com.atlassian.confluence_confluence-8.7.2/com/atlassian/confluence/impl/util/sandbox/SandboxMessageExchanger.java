/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.SandboxCallback
 *  com.atlassian.confluence.util.sandbox.SandboxCallbackContext
 *  com.atlassian.confluence.util.sandbox.SandboxException
 *  com.atlassian.confluence.util.sandbox.SandboxTask
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.util.sandbox;

import com.atlassian.confluence.impl.util.sandbox.ClassLoadersRegistry;
import com.atlassian.confluence.impl.util.sandbox.SandboxClassFinder;
import com.atlassian.confluence.impl.util.sandbox.SandboxMessage;
import com.atlassian.confluence.impl.util.sandbox.SandboxMessageType;
import com.atlassian.confluence.impl.util.sandbox.SandboxRequest;
import com.atlassian.confluence.util.sandbox.SandboxCallback;
import com.atlassian.confluence.util.sandbox.SandboxCallbackContext;
import com.atlassian.confluence.util.sandbox.SandboxException;
import com.atlassian.confluence.util.sandbox.SandboxTask;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class SandboxMessageExchanger<T, R> {
    private static final Logger log = LoggerFactory.getLogger(SandboxMessageExchanger.class);
    private final SandboxTask<T, R> task;
    private final UUID taskClassloaderUid;
    private final SandboxClassFinder classFinder;
    private final SandboxCallbackContext callbackContext;

    static <T, R> SandboxMessageExchanger<T, R> createFrom(SandboxRequest<T, R> request, ClassLoadersRegistry classLoadersRegistry) {
        SandboxTask<T, R> task = request.getTask();
        ClassLoader taskClassClassLoader = task.getClass().getClassLoader();
        return new SandboxMessageExchanger<T, R>(task, classLoadersRegistry.classLoaderUid(taskClassClassLoader), new SandboxClassFinder((List<ClassLoader>)ImmutableList.of((Object)taskClassClassLoader, (Object)SandboxMessageExchanger.class.getClassLoader())), request.getCallbackContext());
    }

    SandboxMessageExchanger(SandboxTask<T, R> task, UUID taskClassloaderUid, SandboxClassFinder classFinder, SandboxCallbackContext callbackContext) {
        this.task = Objects.requireNonNull(task);
        this.taskClassloaderUid = Objects.requireNonNull(taskClassloaderUid);
        this.classFinder = Objects.requireNonNull(classFinder);
        this.callbackContext = Objects.requireNonNull(callbackContext);
    }

    SandboxMessage createInitialMessage(T input) {
        return new SandboxMessage(SandboxMessageType.APPLICATION_REQUEST, SandboxMessage.ApplicationPayload.withSpecifiedClassloader(this.taskClassloaderUid, this.task.getClass().getName(), this.task.inputSerializer().serialize(input)));
    }

    @Nonnull
    Response<R> handleMessage(SandboxMessage message) throws ReflectiveOperationException {
        if (message.getType() == SandboxMessageType.APPLICATION_RESPONSE) {
            SandboxMessage.ApplicationPayload payload = (SandboxMessage.ApplicationPayload)message.getPayload();
            return Response.result(this.task.outputSerializer().deserialize(payload.getData()));
        }
        if (message.getType() == SandboxMessageType.APPLICATION_REQUEST) {
            SandboxMessage.ApplicationPayload payload = (SandboxMessage.ApplicationPayload)message.getPayload();
            log.debug("Got callback for {} {}", (Object)message.getType(), (Object)payload.getClassName());
            Class<SandboxCallback> callbackClass = this.classFinder.loadClass(payload.getClassName()).asSubclass(SandboxCallback.class);
            SandboxCallback callback = callbackClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            Object callbackInput = callback.inputSerializer().deserialize(payload.getData());
            try {
                Object callbackOutput = callback.apply(this.callbackContext, callbackInput);
                log.debug("Callback completed");
                return Response.create(new SandboxMessage(SandboxMessageType.APPLICATION_RESPONSE, SandboxMessage.ApplicationPayload.withSpecifiedClassloader(this.taskClassloaderUid, callbackOutput.getClass().getName(), callback.outputSerializer().serialize(callbackOutput))));
            }
            catch (RuntimeException t) {
                throw new SandboxException("Callback invocation has failed", (Throwable)t);
            }
        }
        if (message.getType() == SandboxMessageType.FIND_CLASS_REQUEST) {
            String name = (String)message.getPayload();
            log.debug("Got a request for {} {}", (Object)message.getType(), (Object)name);
            return Response.create(new SandboxMessage(SandboxMessageType.FIND_CLASS_RESPONSE, this.classFinder.findClass(name)));
        }
        if (message.getType() == SandboxMessageType.FIND_RESOURCE_REQUEST) {
            String name = (String)message.getPayload();
            log.debug("Got a request for {} {}", (Object)message.getType(), (Object)name);
            return Response.create(new SandboxMessage(SandboxMessageType.FIND_RESOURCE_RESPONSE, this.classFinder.findResource(name)));
        }
        if (message.getType() == SandboxMessageType.FIND_RESOURCES_REQUEST) {
            String name = (String)message.getPayload();
            log.debug("Got a request for {} {}", (Object)message.getType(), (Object)name);
            return Response.create(new SandboxMessage(SandboxMessageType.FIND_RESOURCES_RESPONSE, this.classFinder.findResources(name)));
        }
        return Response.empty();
    }

    static class Response<R> {
        private final R result;
        private final SandboxMessage message;

        static <R> Response<R> result(R result) {
            return new Response<R>(result, null);
        }

        static <R> Response<R> create(SandboxMessage message) {
            return new Response<Object>(null, message);
        }

        static <R> Response<R> empty() {
            return new Response<Object>(null, null);
        }

        private Response(@Nullable R result, @Nullable SandboxMessage message) {
            this.result = result;
            this.message = message;
        }

        <X extends Exception> Optional<R> resultOrReply(Sender<X> sender) throws X {
            if (this.message != null) {
                sender.send(this.message);
                return Optional.empty();
            }
            if (this.result != null) {
                return Optional.of(this.result);
            }
            throw new IllegalStateException("Neither result nor response message has been produced");
        }

        @FunctionalInterface
        static interface Sender<X extends Exception> {
            public void send(SandboxMessage var1) throws X;
        }
    }
}

