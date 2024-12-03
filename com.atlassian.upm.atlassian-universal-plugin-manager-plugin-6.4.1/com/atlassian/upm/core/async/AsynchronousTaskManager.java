/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.MapMaker
 *  io.atlassian.util.concurrent.ThreadFactories
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.upm.core.async;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.async.AsyncTask;
import com.atlassian.upm.core.async.AsyncTaskInfo;
import com.atlassian.upm.core.async.AsyncTaskStatus;
import com.atlassian.upm.core.async.AsyncTaskStatusUpdater;
import com.atlassian.upm.core.async.AsynchronousTaskStatusStore;
import com.atlassian.upm.core.rest.BaseUriBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.MapMaker;
import io.atlassian.util.concurrent.ThreadFactories;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;

public class AsynchronousTaskManager
implements DisposableBean {
    private static final int NUM_THREADS = 4;
    private static final Log logger = LogFactory.getLog(AsynchronousTaskManager.class);
    private final ApplicationProperties applicationProperties;
    private final ExecutorService executor;
    private final UserManager userManager;
    protected final BaseUriBuilder uriBuilder;
    protected final AsynchronousTaskStatusStore statusStore;
    private final ConcurrentMap<String, Option<Runnable>> activeTasks;

    public AsynchronousTaskManager(ApplicationProperties applicationProperties, ThreadLocalDelegateExecutorFactory factory, BaseUriBuilder uriBuilder, UserManager userManager, AsynchronousTaskStatusStore statusStore) {
        this(applicationProperties, factory, Executors.newFixedThreadPool(4, ThreadFactories.namedThreadFactory((String)"UpmAsynchronousTaskManager")), uriBuilder, userManager, statusStore);
    }

    public AsynchronousTaskManager(ApplicationProperties applicationProperties, ThreadLocalDelegateExecutorFactory factory, ExecutorService executor, BaseUriBuilder uriBuilder, UserManager userManager, AsynchronousTaskStatusStore statusStore) {
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.executor = Objects.requireNonNull(factory, "factory").createExecutorService(Objects.requireNonNull(executor, "executor"));
        this.uriBuilder = Objects.requireNonNull(uriBuilder, "uriBuilder");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
        this.activeTasks = new MapMaker().concurrencyLevel(4).makeMap();
        this.statusStore = Objects.requireNonNull(statusStore, "statusStore");
    }

    public AsyncTaskInfo executeAsynchronousTask(AsyncTask task) {
        return this.executeAsynchronousTask(task, this.userManager.getRemoteUserKey(), Option.none(Runnable.class));
    }

    public AsyncTaskInfo executeAsynchronousTask(AsyncTask task, Option<Runnable> canceller) {
        return this.executeAsynchronousTask(task, this.userManager.getRemoteUserKey(), canceller);
    }

    public AsyncTaskInfo executeAsynchronousTask(final AsyncTask task, UserKey userKey, Option<Runnable> canceller) {
        final String id = UUID.randomUUID().toString();
        AsyncTaskInfo taskInfo = new AsyncTaskInfo(id, task.getType(), userKey == null ? null : userKey.getStringValue(), new Date(), task.getInitialStatus());
        this.statusStore.addTask(taskInfo);
        this.activeTasks.put(id, canceller);
        this.executor.submit(new Callable<Void>(){

            @Override
            public Void call() throws Exception {
                AsynchronousTaskManager.this.executeTask(task, id);
                AsynchronousTaskManager.this.activeTasks.remove(id);
                return null;
            }
        });
        return taskInfo;
    }

    protected void executeTask(AsyncTask task, final String id) {
        AsyncTaskStatusUpdater statusUpdater = new AsyncTaskStatusUpdater(){

            @Override
            public void updateStatus(AsyncTaskStatus status) {
                AsynchronousTaskManager.this.statusStore.updateTaskStatus(id, status);
            }
        };
        try {
            AsyncTaskStatus status = task.run(statusUpdater);
            this.statusStore.updateTaskStatus(id, AsyncTaskStatus.builder(status).done(true).build());
        }
        catch (Exception e) {
            logger.warn((Object)("Unexpected exception from asynchronous task: " + e));
            logger.debug((Object)e, (Throwable)e);
            this.statusStore.updateTaskStatus(id, AsyncTaskStatus.builder().errorByCode("unexpected.exception").done(true).build());
        }
    }

    public boolean isBaseUrlValid(Option<HttpServletRequest> currentRequest) {
        URI baseUriOnWorkerThread;
        URI baseUriOnMainThread;
        try {
            baseUriOnMainThread = this.getBaseUrlAsUri();
        }
        catch (URISyntaxException e) {
            logger.warn((Object)("Application base URL is invalid: " + e));
            return false;
        }
        Callable<URI> getBaseUriOnWorkerThread = new Callable<URI>(){

            @Override
            public URI call() throws URISyntaxException {
                return AsynchronousTaskManager.this.getBaseUrlAsUri();
            }
        };
        try {
            baseUriOnWorkerThread = (URI)this.executor.invokeAny(ImmutableList.of((Object)getBaseUriOnWorkerThread));
        }
        catch (Exception e) {
            logger.error((Object)("Unable to check base URL on worker thread: " + e));
            logger.debug((Object)e, (Throwable)e);
            return false;
        }
        if (!baseUriOnMainThread.equals(baseUriOnWorkerThread)) {
            logger.warn((Object)("Configured application base URL (" + baseUriOnWorkerThread + ") does not match base URL from request (" + baseUriOnMainThread + ")"));
            return false;
        }
        for (HttpServletRequest request : currentRequest) {
            try {
                URI requestUri = new URI(request.getScheme(), null, request.getServerName(), request.getServerPort(), request.getContextPath(), null, null);
                if (baseUriOnMainThread.equals(requestUri)) continue;
                logger.warn((Object)("Configured application base URL (" + baseUriOnMainThread + ") does not match base URL from request (" + requestUri + ")"));
                return false;
            }
            catch (URISyntaxException e) {
                logger.warn((Object)("Invalid request URI - this should never happen: " + e.getInput()));
            }
        }
        return true;
    }

    private URI getBaseUrlAsUri() throws URISyntaxException {
        String urlString = this.applicationProperties.getBaseUrl();
        URI uri = new URI(urlString);
        if (uri.getPath() == null) {
            throw new URISyntaxException(urlString, "Base URL must have a path");
        }
        if (!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme())) {
            throw new URISyntaxException(urlString, "Base URL must be http or https");
        }
        if (uri.getPort() < 0) {
            int defaultPort = uri.getScheme().equalsIgnoreCase("https") ? 443 : 80;
            return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), defaultPort, uri.getPath(), null, null);
        }
        return uri;
    }

    public boolean hasPendingTasks() {
        return !Iterables.isEmpty(this.statusStore.getOngoingTasks());
    }

    public void destroy() {
        this.executor.shutdown();
        for (String taskId : this.activeTasks.keySet()) {
            this.statusStore.removeTask(taskId);
        }
    }

    public Option<Runnable> getTaskCanceller(String taskId) {
        Option<Runnable> canceller = (Option<Runnable>)this.activeTasks.get(taskId);
        return canceller == null ? Option.none(Runnable.class) : canceller;
    }
}

