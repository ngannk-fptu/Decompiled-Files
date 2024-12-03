/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.util.sandbox.Sandbox
 *  com.atlassian.confluence.util.sandbox.SandboxException
 *  com.atlassian.confluence.util.sandbox.SandboxSerializer
 *  com.atlassian.confluence.util.sandbox.SandboxTask
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.conversion.sandbox.SandboxConversionRequest
 *  com.atlassian.plugins.conversion.sandbox.SandboxConversionResponse
 *  com.atlassian.plugins.conversion.sandbox.SandboxConversionTask
 *  javax.annotation.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.conversion.impl.sandbox;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.plugins.conversion.impl.sandbox.SandboxHolder;
import com.atlassian.confluence.util.sandbox.Sandbox;
import com.atlassian.confluence.util.sandbox.SandboxException;
import com.atlassian.confluence.util.sandbox.SandboxSerializer;
import com.atlassian.confluence.util.sandbox.SandboxTask;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.conversion.sandbox.SandboxConversionRequest;
import com.atlassian.plugins.conversion.sandbox.SandboxConversionResponse;
import com.atlassian.plugins.conversion.sandbox.SandboxConversionTask;
import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RemoteDocumentConversionSandbox
implements Sandbox {
    private static final String REMOTE_DOCUMENT_CONVERSION_SANDBOX = "remote-document-conversion-sandbox";
    private final ClusterManager clusterManager;

    @Autowired
    public RemoteDocumentConversionSandbox(@ComponentImport ClusterManager clusterManager) {
        this.clusterManager = Objects.requireNonNull(clusterManager);
    }

    public <T, R> R execute(SandboxTask<T, R> sandboxTask, T t) {
        return this.execute(sandboxTask, t, null);
    }

    public <T, R> R execute(SandboxTask<T, R> sandboxTask, T t, Duration duration) {
        try {
            ServiceTask task = new ServiceTask(sandboxTask.inputSerializer().serialize(t), duration);
            return (R)this.clusterManager.submitToKeyOwner((Callable)task, REMOTE_DOCUMENT_CONVERSION_SANDBOX, t).thenApply(arg_0 -> ((SandboxSerializer)sandboxTask.outputSerializer()).deserialize(arg_0)).toCompletableFuture().get();
        }
        catch (InterruptedException | ExecutionException e) {
            throw new SandboxException("Remote sandbox invocation has been failed", (Throwable)e);
        }
    }

    static class ServiceTask
    implements Callable<byte[]>,
    Serializable {
        private static final long serialVersionUID = -2809130899981234162L;
        private final byte[] request;
        private final Duration timeLimit;

        ServiceTask(byte[] request, @Nullable Duration timeLimit) {
            this.request = Objects.requireNonNull(request);
            this.timeLimit = timeLimit;
        }

        @Override
        public byte[] call() {
            Sandbox sandbox = Objects.requireNonNull(SandboxHolder.getInstance().getSandbox());
            SandboxConversionTask sandboxTask = new SandboxConversionTask();
            SandboxConversionResponse response = this.timeLimit != null ? (SandboxConversionResponse)sandbox.execute((SandboxTask)sandboxTask, (Object)((SandboxConversionRequest)sandboxTask.inputSerializer().deserialize(this.request)), this.timeLimit) : (SandboxConversionResponse)sandbox.execute((SandboxTask)sandboxTask, (Object)((SandboxConversionRequest)sandboxTask.inputSerializer().deserialize(this.request)));
            return sandboxTask.outputSerializer().serialize((Object)response);
        }
    }
}

