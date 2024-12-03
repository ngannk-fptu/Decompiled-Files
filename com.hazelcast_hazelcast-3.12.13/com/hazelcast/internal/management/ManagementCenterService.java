/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management;

import com.hazelcast.cache.impl.JCacheDetector;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.ManagementCenterConfig;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.instance.OutOfMemoryErrorDispatcher;
import com.hazelcast.internal.ascii.rest.HttpCommand;
import com.hazelcast.internal.json.Json;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.management.ClientBwListConfigHandler;
import com.hazelcast.internal.management.ConsoleCommandHandler;
import com.hazelcast.internal.management.ManagementCenterConnectionFactory;
import com.hazelcast.internal.management.TimedMemberState;
import com.hazelcast.internal.management.TimedMemberStateFactory;
import com.hazelcast.internal.management.events.Event;
import com.hazelcast.internal.management.events.EventBatch;
import com.hazelcast.internal.management.operation.UpdateManagementCenterUrlOperation;
import com.hazelcast.internal.management.request.AsyncConsoleRequest;
import com.hazelcast.internal.management.request.ChangeClusterStateRequest;
import com.hazelcast.internal.management.request.ChangeWanStateRequest;
import com.hazelcast.internal.management.request.ClearWanQueuesRequest;
import com.hazelcast.internal.management.request.ClusterPropsRequest;
import com.hazelcast.internal.management.request.ConsoleCommandRequest;
import com.hazelcast.internal.management.request.ConsoleRequest;
import com.hazelcast.internal.management.request.ExecuteScriptRequest;
import com.hazelcast.internal.management.request.ForceStartNodeRequest;
import com.hazelcast.internal.management.request.GetCacheEntryRequest;
import com.hazelcast.internal.management.request.GetClusterStateRequest;
import com.hazelcast.internal.management.request.GetMapEntryRequest;
import com.hazelcast.internal.management.request.GetMemberSystemPropertiesRequest;
import com.hazelcast.internal.management.request.MapConfigRequest;
import com.hazelcast.internal.management.request.MemberConfigRequest;
import com.hazelcast.internal.management.request.PromoteMemberRequest;
import com.hazelcast.internal.management.request.RunGcRequest;
import com.hazelcast.internal.management.request.ShutdownClusterRequest;
import com.hazelcast.internal.management.request.ThreadDumpRequest;
import com.hazelcast.internal.management.request.TriggerPartialStartRequest;
import com.hazelcast.internal.management.request.WanCheckConsistencyRequest;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.util.Clock;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.JsonUtil;
import com.hazelcast.util.ThreadUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ManagementCenterService {
    private static final int HTTP_SUCCESS = 200;
    private static final int HTTP_NOT_MODIFIED = 304;
    private static final int CONNECTION_TIMEOUT_MILLIS = 5000;
    private static final long SLEEP_BETWEEN_POLL_MILLIS = 1000L;
    private static final long DEFAULT_UPDATE_INTERVAL = 3000L;
    private static final long EVENT_SEND_INTERVAL_MILLIS = 1000L;
    private final HazelcastInstanceImpl instance;
    private final TaskPollThread taskPollThread;
    private final StateSendThread stateSendThread;
    private final PrepareStateThread prepareStateThread;
    private final EventSendThread eventSendThread;
    private final ILogger logger;
    private final ConsoleCommandHandler commandHandler;
    private final ManagementCenterConfig managementCenterConfig;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final TimedMemberStateFactory timedMemberStateFactory;
    private final ManagementCenterConnectionFactory connectionFactory;
    private final AtomicReference<TimedMemberState> timedMemberState = new AtomicReference();
    private final BlockingQueue<Event> events = new LinkedBlockingQueue<Event>();
    private volatile String managementCenterUrl;
    private volatile boolean urlChanged;
    private volatile boolean manCenterConnectionLost;
    private volatile boolean taskPollFailed;
    private volatile boolean eventSendFailed;

    public ManagementCenterService(HazelcastInstanceImpl instance) {
        this.instance = instance;
        this.logger = instance.node.getLogger(ManagementCenterService.class);
        this.managementCenterConfig = this.getManagementCenterConfig();
        this.managementCenterUrl = this.getManagementCenterUrl();
        this.commandHandler = new ConsoleCommandHandler(instance);
        this.taskPollThread = new TaskPollThread();
        this.stateSendThread = new StateSendThread();
        this.prepareStateThread = new PrepareStateThread();
        this.eventSendThread = new EventSendThread();
        this.timedMemberStateFactory = instance.node.getNodeExtension().createTimedMemberStateFactory(instance);
        this.connectionFactory = instance.node.getNodeExtension().getManagementCenterConnectionFactory();
        if (this.managementCenterConfig.isEnabled()) {
            this.instance.getCluster().addMembershipListener(new MemberListenerImpl());
            this.start();
        }
    }

    private String getManagementCenterUrl() {
        return this.managementCenterConfig.getUrl();
    }

    private ManagementCenterConfig getManagementCenterConfig() {
        ManagementCenterConfig config = this.instance.node.config.getManagementCenterConfig();
        if (config == null) {
            throw new IllegalStateException("ManagementCenterConfig can't be null!");
        }
        return config;
    }

    static String cleanupUrl(String url) {
        if (url == null) {
            return null;
        }
        return url.endsWith("/") ? url : url + '/';
    }

    private void start() {
        if (this.managementCenterUrl == null) {
            this.logger.warning("Can't start Hazelcast Management Center Service: web-server URL is null!");
            return;
        }
        if (!this.isRunning.compareAndSet(false, true)) {
            return;
        }
        this.timedMemberStateFactory.init();
        try {
            if (this.connectionFactory != null) {
                this.connectionFactory.init(this.managementCenterConfig.getMutualAuthConfig());
            }
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
        this.taskPollThread.start();
        this.prepareStateThread.start();
        this.stateSendThread.start();
        this.eventSendThread.start();
        this.logger.info("Hazelcast will connect to Hazelcast Management Center on address: \n" + this.managementCenterUrl);
    }

    public void shutdown() {
        if (!this.isRunning.compareAndSet(true, false)) {
            return;
        }
        this.logger.info("Shutting down Hazelcast Management Center Service");
        try {
            this.interruptThread(this.stateSendThread);
            this.interruptThread(this.taskPollThread);
            this.interruptThread(this.prepareStateThread);
            this.interruptThread(this.eventSendThread);
        }
        catch (Throwable ignored) {
            EmptyStatement.ignore(ignored);
        }
    }

    public byte[] clusterWideUpdateManagementCenterUrl(String newUrl) {
        try {
            Set<Member> memberList = this.instance.node.clusterService.getMembers();
            for (Member member : memberList) {
                this.send(member.getAddress(), new UpdateManagementCenterUrlOperation(newUrl));
            }
            return HttpCommand.RES_204;
        }
        catch (Throwable throwable) {
            this.logger.warning("New Management Center url cannot be assigned.", throwable);
            return HttpCommand.RES_500;
        }
    }

    public void updateManagementCenterUrl(String newUrl) {
        if (newUrl == null) {
            return;
        }
        if (newUrl.equals(this.managementCenterUrl)) {
            return;
        }
        this.managementCenterUrl = newUrl;
        if (!this.isRunning()) {
            this.start();
        }
        this.urlChanged = true;
        this.logger.info("Management Center URL has changed. Hazelcast will connect to Management Center on address:\n" + this.managementCenterUrl);
    }

    private void interruptThread(Thread thread) {
        if (thread != null) {
            thread.interrupt();
        }
    }

    public InternalCompletableFuture<Object> callOnAddress(Address address, Operation operation) {
        InternalOperationService operationService = this.instance.node.nodeEngine.getOperationService();
        return operationService.invokeOnTarget("hz:impl:mapService", operation, address);
    }

    public InternalCompletableFuture<Object> callOnThis(Operation operation) {
        return this.callOnAddress(this.instance.node.getThisAddress(), operation);
    }

    public JsonObject syncCallOnThis(Operation operation) {
        InternalCompletableFuture<Object> future = this.callOnThis(operation);
        JsonObject result = new JsonObject();
        try {
            Object operationResult = future.get();
            if (operationResult == null) {
                result.add("result", "success");
            } else {
                result.add("result", operationResult.toString());
            }
        }
        catch (ExecutionException e) {
            result.add("result", e.getMessage());
            result.add("stackTrace", ExceptionUtil.toString(e));
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            result.add("result", e.getMessage());
            result.add("stackTrace", ExceptionUtil.toString(e));
        }
        return result;
    }

    public InternalCompletableFuture<Object> callOnMember(Member member, Operation operation) {
        return this.callOnAddress(member.getAddress(), operation);
    }

    public static Object resolveFuture(Future<Object> future) {
        try {
            return future.get();
        }
        catch (Throwable t) {
            return ExceptionUtil.toString(t);
        }
    }

    public void send(Address address, Operation operation) {
        InternalOperationService operationService = this.instance.node.nodeEngine.getOperationService();
        operationService.createInvocationBuilder("hz:impl:mapService", operation, address).invoke();
    }

    public HazelcastInstanceImpl getHazelcastInstance() {
        return this.instance;
    }

    public ConsoleCommandHandler getCommandHandler() {
        return this.commandHandler;
    }

    public void log(Event event) {
        if (this.managementCenterConfig.isEnabled() && this.isRunning()) {
            this.events.add(event);
        }
    }

    private boolean isRunning() {
        return this.isRunning.get();
    }

    private boolean post(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        if (responseCode != 200 && !this.manCenterConnectionLost) {
            this.logger.warning("Failed to send response, responseCode:" + responseCode + " url:" + connection.getURL());
        }
        return responseCode == 200;
    }

    private HttpURLConnection openJsonConnection(URL url) throws IOException {
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("Opening connection to Management Center:" + url);
        }
        HttpURLConnection connection = (HttpURLConnection)(this.connectionFactory != null ? this.connectionFactory.openConnection(url) : url.openConnection());
        connection.setDoOutput(true);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        return connection;
    }

    private static void sleepIfPossible(long updateIntervalMs, long elapsedMs) throws InterruptedException {
        long sleepTimeMs = updateIntervalMs - elapsedMs;
        if (sleepTimeMs > 0L) {
            Thread.sleep(sleepTimeMs);
        }
    }

    private void log(String msg, Throwable t) {
        if (this.logger.isFinestEnabled()) {
            this.logger.finest(msg, t);
        } else {
            this.logger.info(msg);
        }
    }

    public class MemberListenerImpl
    implements MembershipListener {
        @Override
        public void memberAdded(MembershipEvent membershipEvent) {
            try {
                Member member = membershipEvent.getMember();
                if (member != null && ((ManagementCenterService)ManagementCenterService.this).instance.node.isMaster() && ManagementCenterService.this.urlChanged) {
                    UpdateManagementCenterUrlOperation operation = new UpdateManagementCenterUrlOperation(ManagementCenterService.this.managementCenterUrl);
                    ManagementCenterService.resolveFuture(ManagementCenterService.this.callOnMember(member, operation));
                }
            }
            catch (Exception e) {
                ManagementCenterService.this.logger.warning("Web server url cannot be send to the newly joined member", e);
            }
        }

        @Override
        public void memberRemoved(MembershipEvent membershipEvent) {
        }

        @Override
        public void memberAttributeChanged(MemberAttributeEvent memberAttributeEvent) {
        }
    }

    private final class TaskPollThread
    extends Thread {
        private final Map<Integer, Class<? extends ConsoleRequest>> consoleRequests;
        private final ExecutionService executionService;

        TaskPollThread() {
            super(ThreadUtil.createThreadName(ManagementCenterService.this.instance.getName(), "MC.Task.Poller"));
            this.consoleRequests = new HashMap<Integer, Class<? extends ConsoleRequest>>();
            this.executionService = ((ManagementCenterService)ManagementCenterService.this).instance.node.getNodeEngine().getExecutionService();
            this.register(new ThreadDumpRequest());
            this.register(new ExecuteScriptRequest());
            this.register(new ConsoleCommandRequest());
            this.register(new RunGcRequest());
            this.register(new GetMapEntryRequest());
            if (JCacheDetector.isJCacheAvailable(((ManagementCenterService)ManagementCenterService.this).instance.node.getNodeEngine().getConfigClassLoader(), ManagementCenterService.this.logger)) {
                this.register(new GetCacheEntryRequest());
            } else {
                ManagementCenterService.this.logger.finest("javax.cache api is not detected on classpath.Skip registering GetCacheEntryRequest...");
            }
            this.register(new TriggerPartialStartRequest());
            this.registerConfigRequests();
            this.registerClusterManagementRequests();
            this.registerWanRequests();
        }

        private void registerConfigRequests() {
            this.register(new GetMemberSystemPropertiesRequest());
            this.register(new MapConfigRequest());
            this.register(new MemberConfigRequest());
        }

        private void registerClusterManagementRequests() {
            this.register(new ClusterPropsRequest());
            this.register(new GetClusterStateRequest());
            this.register(new ChangeClusterStateRequest());
            this.register(new ShutdownClusterRequest());
            this.register(new PromoteMemberRequest());
            this.register(new ForceStartNodeRequest());
        }

        private void registerWanRequests() {
            this.register(new ChangeWanStateRequest());
            this.register(new ClearWanQueuesRequest());
            this.register(new WanCheckConsistencyRequest());
        }

        public void register(ConsoleRequest consoleRequest) {
            Class<?> reqClass = this.consoleRequests.put(consoleRequest.getType(), consoleRequest.getClass());
            if (reqClass != null) {
                throw new IllegalArgumentException("Request ID is already registered by " + reqClass);
            }
        }

        private HttpURLConnection openPostResponseConnection() throws IOException {
            URL url = this.newPostResponseUrl();
            if (ManagementCenterService.this.logger.isFinestEnabled()) {
                ManagementCenterService.this.logger.finest("Opening sendResponse connection:" + url);
            }
            HttpURLConnection connection = (HttpURLConnection)(ManagementCenterService.this.connectionFactory != null ? ManagementCenterService.this.connectionFactory.openConnection(url) : url.openConnection());
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            return connection;
        }

        private URL newPostResponseUrl() throws MalformedURLException {
            return new URL(ManagementCenterService.cleanupUrl(ManagementCenterService.this.managementCenterUrl) + "putResponse.do");
        }

        @Override
        public void run() {
            block3: {
                try {
                    while (ManagementCenterService.this.isRunning()) {
                        this.processTask();
                        this.sleep();
                    }
                }
                catch (Throwable throwable) {
                    if (throwable instanceof InterruptedException) break block3;
                    OutOfMemoryErrorDispatcher.inspectOutOfMemoryError(throwable);
                    ManagementCenterService.this.logger.warning("Problem on Hazelcast Management Center Service while polling for a task.", throwable);
                }
            }
        }

        private void sleep() throws InterruptedException {
            Thread.sleep(1000L);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void processTask() {
            InputStreamReader reader;
            InputStream inputStream;
            block7: {
                inputStream = null;
                reader = null;
                try {
                    boolean success;
                    inputStream = this.openTaskInputStream();
                    reader = new InputStreamReader(inputStream, "UTF-8");
                    JsonObject request = JsonValue.readFrom(reader).asObject();
                    if (request.isEmpty()) break block7;
                    JsonObject innerRequest = JsonUtil.getObject(request, "request");
                    int type = JsonUtil.getInt(innerRequest, "type");
                    int taskId = JsonUtil.getInt(request, "taskId");
                    Class<? extends ConsoleRequest> requestClass = this.consoleRequests.get(type);
                    if (requestClass == null) {
                        throw new RuntimeException("Failed to find a request for requestType: " + type);
                    }
                    ConsoleRequest task = requestClass.newInstance();
                    task.fromJson(JsonUtil.getObject(innerRequest, "request"));
                    if (task instanceof AsyncConsoleRequest) {
                        this.executionService.execute("hz:async", new AsyncConsoleRequestTask(taskId, task));
                        success = true;
                    } else {
                        success = this.processTaskAndSendResponse(taskId, task);
                    }
                    if (!ManagementCenterService.this.taskPollFailed || !success) break block7;
                    ManagementCenterService.this.logger.info("Management Center task polling successful.");
                    ManagementCenterService.this.taskPollFailed = false;
                }
                catch (Exception e) {
                    block8: {
                        try {
                            if (ManagementCenterService.this.taskPollFailed) break block8;
                            ManagementCenterService.this.taskPollFailed = true;
                            ManagementCenterService.this.log("Failed to pull tasks from Management Center", e);
                        }
                        catch (Throwable throwable) {
                            IOUtil.closeResource(reader);
                            IOUtil.closeResource(inputStream);
                            throw throwable;
                        }
                    }
                    IOUtil.closeResource(reader);
                    IOUtil.closeResource(inputStream);
                }
            }
            IOUtil.closeResource(reader);
            IOUtil.closeResource(inputStream);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private boolean processTaskAndSendResponse(int taskId, ConsoleRequest task) throws Exception {
            HttpURLConnection connection = this.openPostResponseConnection();
            OutputStream outputStream = connection.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
            try {
                JsonObject root = new JsonObject();
                root.add("taskId", taskId);
                root.add("type", task.getType());
                task.writeResponse(ManagementCenterService.this, root);
                root.writeTo(writer);
                writer.flush();
                outputStream.flush();
                boolean bl = ManagementCenterService.this.post(connection);
                return bl;
            }
            finally {
                IOUtil.closeResource(writer);
                IOUtil.closeResource(outputStream);
            }
        }

        private InputStream openTaskInputStream() throws IOException {
            URLConnection connection = this.openGetTaskConnection();
            return connection.getInputStream();
        }

        private URLConnection openGetTaskConnection() throws IOException {
            URL url = this.newGetTaskUrl();
            if (ManagementCenterService.this.logger.isFinestEnabled()) {
                ManagementCenterService.this.logger.finest("Opening getTask connection:" + url);
            }
            HttpURLConnection connection = (HttpURLConnection)(ManagementCenterService.this.connectionFactory != null ? ManagementCenterService.this.connectionFactory.openConnection(url) : url.openConnection());
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            return connection;
        }

        private URL newGetTaskUrl() throws IOException {
            GroupConfig groupConfig = ManagementCenterService.this.instance.getConfig().getGroupConfig();
            Address localAddress = ((ManagementCenterService)ManagementCenterService.this).instance.node.getClusterService().getLocalMember().getAddress();
            String urlString = ManagementCenterService.cleanupUrl(ManagementCenterService.this.managementCenterUrl) + "getTask.do?member=" + localAddress.getHost() + ":" + localAddress.getPort() + "&cluster=" + URLEncoder.encode(groupConfig.getName(), "UTF-8");
            return new URL(urlString);
        }

        private class AsyncConsoleRequestTask
        implements Runnable {
            private final int taskId;
            private final ConsoleRequest task;

            public AsyncConsoleRequestTask(int taskId, ConsoleRequest task) {
                this.taskId = taskId;
                this.task = task;
            }

            @Override
            public void run() {
                try {
                    TaskPollThread.this.processTaskAndSendResponse(this.taskId, this.task);
                }
                catch (Exception e) {
                    ManagementCenterService.this.logger.warning("Problem while handling task: " + this.task, e);
                }
            }
        }
    }

    private final class StateSendThread
    extends Thread {
        private final long updateIntervalMs;
        private final ClientBwListConfigHandler bwListConfigHandler;
        private String lastConfigETag;

        private StateSendThread() {
            super(ThreadUtil.createThreadName(ManagementCenterService.this.instance.getName(), "MC.State.Sender"));
            this.updateIntervalMs = this.calcUpdateInterval();
            this.bwListConfigHandler = new ClientBwListConfigHandler(((ManagementCenterService)ManagementCenterService.this).instance.node.clientEngine);
        }

        private long calcUpdateInterval() {
            long updateInterval = ManagementCenterService.this.managementCenterConfig.getUpdateInterval();
            return updateInterval > 0L ? TimeUnit.SECONDS.toMillis(updateInterval) : 3000L;
        }

        @Override
        public void run() {
            block3: {
                try {
                    while (ManagementCenterService.this.isRunning()) {
                        long startMs = Clock.currentTimeMillis();
                        this.sendStateAndReadConfig();
                        long endMs = Clock.currentTimeMillis();
                        ManagementCenterService.sleepIfPossible(this.updateIntervalMs, endMs - startMs);
                    }
                }
                catch (Throwable throwable) {
                    OutOfMemoryErrorDispatcher.inspectOutOfMemoryError(throwable);
                    if (throwable instanceof InterruptedException) break block3;
                    ManagementCenterService.this.logger.warning("Exception occurred while calculating stats", throwable);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void sendStateAndReadConfig() throws MalformedURLException {
            OutputStreamWriter writer;
            OutputStream outputStream;
            block5: {
                URL url = this.newCollectorUrl();
                outputStream = null;
                writer = null;
                try {
                    HttpURLConnection connection = ManagementCenterService.this.openJsonConnection(url);
                    if (this.lastConfigETag != null) {
                        connection.setRequestProperty("If-None-Match", this.lastConfigETag);
                    }
                    outputStream = connection.getOutputStream();
                    writer = new OutputStreamWriter(outputStream, "UTF-8");
                    JsonObject root = new JsonObject();
                    TimedMemberState memberState = (TimedMemberState)ManagementCenterService.this.timedMemberState.get();
                    if (memberState == null) break block5;
                    root.add("timedMemberState", memberState.toJson());
                    root.writeTo(writer);
                    writer.flush();
                    outputStream.flush();
                    this.processResponse(connection);
                }
                catch (Exception e) {
                    block6: {
                        try {
                            if (ManagementCenterService.this.manCenterConnectionLost) break block6;
                            ManagementCenterService.this.manCenterConnectionLost = true;
                            ManagementCenterService.this.log("Failed to connect to: " + url, e);
                            this.bwListConfigHandler.handleLostConnection();
                        }
                        catch (Throwable throwable) {
                            IOUtil.closeResource(writer);
                            IOUtil.closeResource(outputStream);
                            throw throwable;
                        }
                    }
                    IOUtil.closeResource(writer);
                    IOUtil.closeResource(outputStream);
                }
            }
            IOUtil.closeResource(writer);
            IOUtil.closeResource(outputStream);
        }

        private void processResponse(HttpURLConnection connection) throws Exception {
            boolean okResponse;
            int responseCode = connection.getResponseCode();
            boolean bl = okResponse = responseCode == 200 || responseCode == 304;
            if (!okResponse && !ManagementCenterService.this.manCenterConnectionLost) {
                ManagementCenterService.this.logger.warning("Failed to send response, responseCode:" + responseCode + " url:" + connection.getURL());
            }
            if (ManagementCenterService.this.manCenterConnectionLost && okResponse) {
                ManagementCenterService.this.logger.info("Connection to Management Center restored.");
                ManagementCenterService.this.manCenterConnectionLost = false;
            } else if (!okResponse) {
                ManagementCenterService.this.manCenterConnectionLost = true;
            }
            if (responseCode == 200) {
                this.readAndApplyConfig(connection);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void readAndApplyConfig(HttpURLConnection connection) throws Exception {
            InputStream inputStream = null;
            InputStreamReader reader = null;
            try {
                inputStream = connection.getInputStream();
                reader = new InputStreamReader(inputStream, "UTF-8");
                JsonObject response = Json.parse(reader).asObject();
                this.lastConfigETag = connection.getHeaderField("ETag");
                this.bwListConfigHandler.handleConfig(response);
            }
            catch (Throwable throwable) {
                IOUtil.closeResource(reader);
                IOUtil.closeResource(inputStream);
                throw throwable;
            }
            IOUtil.closeResource(reader);
            IOUtil.closeResource(inputStream);
        }

        private URL newCollectorUrl() throws MalformedURLException {
            String url = ManagementCenterService.cleanupUrl(ManagementCenterService.this.managementCenterUrl) + "collector.do";
            return new URL(url);
        }
    }

    private final class PrepareStateThread
    extends Thread {
        private final long updateIntervalMs;

        private PrepareStateThread() {
            super(ThreadUtil.createThreadName(ManagementCenterService.this.instance.getName(), "MC.State.Sender"));
            this.updateIntervalMs = this.calcUpdateInterval();
        }

        private long calcUpdateInterval() {
            long updateInterval = ManagementCenterService.this.managementCenterConfig.getUpdateInterval();
            return updateInterval > 0L ? TimeUnit.SECONDS.toMillis(updateInterval) : 3000L;
        }

        @Override
        public void run() {
            block3: {
                try {
                    while (ManagementCenterService.this.isRunning()) {
                        ManagementCenterService.this.timedMemberState.set(ManagementCenterService.this.timedMemberStateFactory.createTimedMemberState());
                        this.sleep();
                    }
                }
                catch (Throwable throwable) {
                    OutOfMemoryErrorDispatcher.inspectOutOfMemoryError(throwable);
                    if (throwable instanceof InterruptedException) break block3;
                    ManagementCenterService.this.logger.warning("Hazelcast Management Center Service will be shutdown due to exception.", throwable);
                    ManagementCenterService.this.shutdown();
                }
            }
        }

        private void sleep() throws InterruptedException {
            Thread.sleep(this.updateIntervalMs);
        }
    }

    private final class EventSendThread
    extends Thread {
        private EventSendThread() {
            super(ThreadUtil.createThreadName(ManagementCenterService.this.instance.getName(), "MC.Event.Sender"));
        }

        @Override
        public void run() {
            block3: {
                try {
                    while (ManagementCenterService.this.isRunning()) {
                        long startMs = Clock.currentTimeMillis();
                        this.sendEvents();
                        long endMs = Clock.currentTimeMillis();
                        ManagementCenterService.sleepIfPossible(1000L, endMs - startMs);
                    }
                }
                catch (Throwable throwable) {
                    OutOfMemoryErrorDispatcher.inspectOutOfMemoryError(throwable);
                    if (throwable instanceof InterruptedException) break block3;
                    ManagementCenterService.this.logger.warning("Exception occurred while sending events", throwable);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void sendEvents() throws MalformedURLException {
            block8: {
                ArrayList<Event> eventList = new ArrayList<Event>();
                if (ManagementCenterService.this.events.drainTo(eventList) == 0) {
                    return;
                }
                URL url = new URL(ManagementCenterService.cleanupUrl(ManagementCenterService.this.managementCenterUrl) + "events.do");
                OutputStream outputStream = null;
                OutputStreamWriter writer = null;
                try {
                    String groupName = ManagementCenterService.this.instance.getConfig().getGroupConfig().getName();
                    String address = ((ManagementCenterService)ManagementCenterService.this).instance.node.address.getHost() + ":" + ((ManagementCenterService)ManagementCenterService.this).instance.node.address.getPort();
                    JsonObject batch = new EventBatch(groupName, address, eventList).toJson();
                    HttpURLConnection connection = ManagementCenterService.this.openJsonConnection(url);
                    outputStream = connection.getOutputStream();
                    writer = new OutputStreamWriter(outputStream, "UTF-8");
                    batch.writeTo(writer);
                    writer.flush();
                    outputStream.flush();
                    boolean success = ManagementCenterService.this.post(connection);
                    if (ManagementCenterService.this.eventSendFailed && success) {
                        ManagementCenterService.this.logger.info("Sent events to Management Center successfully.");
                        ManagementCenterService.this.eventSendFailed = false;
                    }
                    IOUtil.closeResource(writer);
                }
                catch (Exception e) {
                    if (!ManagementCenterService.this.eventSendFailed) {
                        ManagementCenterService.this.eventSendFailed = true;
                        ManagementCenterService.this.log("Failed to send events to Management Center.", e);
                    }
                    break block8;
                }
                finally {
                    IOUtil.closeResource(writer);
                    IOUtil.closeResource(outputStream);
                }
                IOUtil.closeResource(outputStream);
            }
        }
    }
}

