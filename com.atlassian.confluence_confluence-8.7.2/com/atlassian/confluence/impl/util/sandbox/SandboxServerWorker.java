/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.SandboxTask
 *  com.atlassian.confluence.util.sandbox.SandboxTaskContext
 */
package com.atlassian.confluence.impl.util.sandbox;

import com.atlassian.confluence.impl.util.sandbox.SandboxMessage;
import com.atlassian.confluence.impl.util.sandbox.SandboxMessageType;
import com.atlassian.confluence.impl.util.sandbox.SandboxServerContext;
import com.atlassian.confluence.util.sandbox.SandboxTask;
import com.atlassian.confluence.util.sandbox.SandboxTaskContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;

public final class SandboxServerWorker {
    private final Map<UUID, ClassLoader> classLoaderByUid = new HashMap<UUID, ClassLoader>();
    private final SandboxServerContext context;
    private final Function<UUID, ClassLoader> classloaderLookup;

    public SandboxServerWorker(SandboxServerContext context, Function<UUID, ClassLoader> classloaderLookup) {
        this.context = Objects.requireNonNull(context);
        this.classloaderLookup = Objects.requireNonNull(classloaderLookup);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void processNextMessage() {
        SandboxMessage message = this.context.receiveMessage();
        if (message.getType() == SandboxMessageType.APPLICATION_REQUEST) {
            SandboxMessage.ApplicationPayload payload = (SandboxMessage.ApplicationPayload)message.getPayload();
            UUID classLoaderUid = UUID.fromString(payload.getClassLoaderUid());
            this.log(Level.FINE, "Classloader UID for this request is " + classLoaderUid);
            ClassLoader classLoaderForThisRequest = this.classLoaderByUid.computeIfAbsent(classLoaderUid, this.classloaderLookup);
            Thread.currentThread().setContextClassLoader(classLoaderForThisRequest);
            try {
                this.log(Level.FINE, "got " + SandboxMessageType.APPLICATION_REQUEST + " " + payload);
                SandboxTask<Object, Object> task = this.createTask(payload.getClassName(), classLoaderForThisRequest);
                Object input = task.inputSerializer().deserialize(payload.getData());
                Object output = task.apply((SandboxTaskContext)this.context, input);
                this.log(Level.FINE, "request completed");
                this.context.sendMessage(new SandboxMessage(SandboxMessageType.APPLICATION_RESPONSE, SandboxMessage.ApplicationPayload.withUnspecifiedClassloader(payload.getClassName(), task.outputSerializer().serialize(output))));
            }
            finally {
                Thread.currentThread().setContextClassLoader(null);
            }
        }
    }

    private SandboxTask<Object, Object> createTask(String taskClassName, ClassLoader classLoader) {
        Class<?> taskClass;
        try {
            taskClass = Class.forName(taskClassName, true, classLoader);
        }
        catch (ClassNotFoundException e) {
            this.log(Level.SEVERE, e);
            throw new RuntimeException(e);
        }
        if (!SandboxTask.class.isAssignableFrom(taskClass)) {
            String errorMessage = "Class " + taskClassName + " doesn't implement " + SandboxTask.class.getSimpleName();
            this.log(Level.SEVERE, errorMessage);
            throw new RuntimeException(errorMessage);
        }
        try {
            return (SandboxTask)SandboxTask.class.cast(taskClass.newInstance());
        }
        catch (Throwable e) {
            this.log(Level.SEVERE, e);
            throw new RuntimeException(e);
        }
    }

    private void log(Level level, Object message) {
        this.context.log(level, message);
    }
}

