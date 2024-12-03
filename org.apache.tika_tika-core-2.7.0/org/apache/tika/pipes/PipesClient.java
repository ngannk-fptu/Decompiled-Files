/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.input.UnsynchronizedByteArrayInputStream
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.tika.pipes;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.tika.pipes.FailedToStartClientException;
import org.apache.tika.pipes.FetchEmitTuple;
import org.apache.tika.pipes.PipesConfigBase;
import org.apache.tika.pipes.PipesResult;
import org.apache.tika.pipes.PipesServer;
import org.apache.tika.pipes.emitter.EmitData;
import org.apache.tika.utils.ProcessUtils;
import org.apache.tika.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipesClient
implements Closeable {
    private static final Logger LOG = LoggerFactory.getLogger(PipesClient.class);
    private static final int MAX_BYTES_BEFORE_READY = 20000;
    private static AtomicInteger CLIENT_COUNTER = new AtomicInteger(0);
    private static final long WAIT_ON_DESTROY_MS = 10000L;
    private final Object[] executorServiceLock = new Object[0];
    private final PipesConfigBase pipesConfig;
    private final int pipesClientId;
    private volatile boolean closed = false;
    private ExecutorService executorService = Executors.newFixedThreadPool(1);
    private Process process;
    private DataOutputStream output;
    private DataInputStream input;
    private int filesProcessed = 0;

    public PipesClient(PipesConfigBase pipesConfig) {
        this.pipesConfig = pipesConfig;
        this.pipesClientId = CLIENT_COUNTER.getAndIncrement();
    }

    public int getFilesProcessed() {
        return this.filesProcessed;
    }

    private boolean ping() {
        if (this.process == null || !this.process.isAlive()) {
            return false;
        }
        try {
            this.output.write(PipesServer.STATUS.PING.getByte());
            this.output.flush();
            int ping = this.input.read();
            if (ping == PipesServer.STATUS.PING.getByte()) {
                return true;
            }
        }
        catch (IOException e) {
            return false;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws IOException {
        if (this.process != null) {
            try {
                this.destroyForcibly();
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
        Object[] objectArray = this.executorServiceLock;
        synchronized (this.executorServiceLock) {
            if (this.executorService != null) {
                this.executorService.shutdownNow();
            }
            this.closed = true;
            // ** MonitorExit[var1_2] (shouldn't be in output)
            return;
        }
    }

    public PipesResult process(FetchEmitTuple t) throws IOException, InterruptedException {
        boolean restart = false;
        if (!this.ping()) {
            restart = true;
        } else if (this.pipesConfig.getMaxFilesProcessedPerProcess() > 0 && this.filesProcessed >= this.pipesConfig.getMaxFilesProcessedPerProcess()) {
            LOG.info("pipesClientId={}: restarting server after hitting max files: {}", (Object)this.pipesClientId, (Object)this.filesProcessed);
            restart = true;
        }
        if (restart) {
            boolean successfulRestart = false;
            while (!successfulRestart) {
                try {
                    this.restart();
                    successfulRestart = true;
                }
                catch (TimeoutException e) {
                    LOG.warn("pipesClientId={}: couldn't restart within {} ms (startupTimeoutMillis)", (Object)this.pipesClientId, (Object)this.pipesConfig.getStartupTimeoutMillis());
                    Thread.sleep(this.pipesConfig.getSleepOnStartupTimeoutMillis());
                }
            }
        }
        return this.actuallyProcess(t);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private PipesResult actuallyProcess(FetchEmitTuple t) throws InterruptedException {
        long start = System.currentTimeMillis();
        FutureTask<PipesResult> futureTask = new FutureTask<PipesResult>(() -> {
            UnsynchronizedByteArrayOutputStream bos = new UnsynchronizedByteArrayOutputStream();
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream((OutputStream)bos);){
                objectOutputStream.writeObject(t);
            }
            byte[] bytes = bos.toByteArray();
            this.output.write(PipesServer.STATUS.CALL.getByte());
            this.output.writeInt(bytes.length);
            this.output.write(bytes);
            this.output.flush();
            if (LOG.isTraceEnabled()) {
                LOG.trace("pipesClientId={}: timer -- write tuple: {} ms", (Object)this.pipesClientId, (Object)(System.currentTimeMillis() - start));
            }
            long readStart = System.currentTimeMillis();
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException("thread interrupt");
            }
            PipesResult result = this.readResults(t, start);
            if (LOG.isDebugEnabled()) {
                long elapsed = System.currentTimeMillis() - readStart;
                LOG.debug("finished reading result in {} ms", (Object)elapsed);
            }
            if (LOG.isTraceEnabled()) {
                LOG.trace("pipesClientId={}: timer -- read result: {} ms", (Object)this.pipesClientId, (Object)(System.currentTimeMillis() - readStart));
            }
            return result;
        });
        try {
            if (this.closed) {
                throw new IllegalArgumentException("pipesClientId=" + this.pipesClientId + ": PipesClient closed");
            }
            this.executorService.execute(futureTask);
            PipesResult pipesResult = futureTask.get(this.pipesConfig.getTimeoutMillis(), TimeUnit.MILLISECONDS);
            return pipesResult;
        }
        catch (InterruptedException e) {
            this.destroyForcibly();
            throw e;
        }
        catch (ExecutionException e) {
            LOG.error("pipesClientId=" + this.pipesClientId + ": execution exception", (Throwable)e);
            long elapsed = System.currentTimeMillis() - start;
            this.pauseThenDestroy();
            if (!this.process.isAlive() && 17 == this.process.exitValue()) {
                LOG.warn("pipesClientId={} server timeout: {} in {} ms", new Object[]{this.pipesClientId, t.getId(), elapsed});
                PipesResult pipesResult = PipesResult.TIMEOUT;
                return pipesResult;
            }
            this.process.waitFor(500L, TimeUnit.MILLISECONDS);
            if (this.process.isAlive()) {
                LOG.warn("pipesClientId={} crash: {} in {} ms with no exit code available", new Object[]{this.pipesClientId, t.getId(), elapsed});
            } else {
                LOG.warn("pipesClientId={} crash: {} in {} ms with exit code {}", new Object[]{this.pipesClientId, t.getId(), elapsed, this.process.exitValue()});
            }
            PipesResult pipesResult = PipesResult.UNSPECIFIED_CRASH;
            return pipesResult;
        }
        catch (TimeoutException e) {
            long elapsed = System.currentTimeMillis() - start;
            this.destroyForcibly();
            LOG.warn("pipesClientId={} client timeout: {} in {} ms", new Object[]{this.pipesClientId, t.getId(), elapsed});
            PipesResult pipesResult = PipesResult.TIMEOUT;
            return pipesResult;
        }
        finally {
            futureTask.cancel(true);
        }
    }

    private void pauseThenDestroy() throws InterruptedException {
        try {
            this.process.waitFor(200L, TimeUnit.MILLISECONDS);
        }
        finally {
            this.destroyForcibly();
        }
    }

    private void destroyForcibly() throws InterruptedException {
        this.process.destroyForcibly();
        this.process.waitFor(10000L, TimeUnit.MILLISECONDS);
        try {
            this.input.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        try {
            this.output.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        if (this.process.isAlive()) {
            LOG.error("Process still alive after {}ms", (Object)10000L);
        }
    }

    private PipesResult readResults(FetchEmitTuple t, long start) throws IOException {
        int statusByte = this.input.read();
        long millis = System.currentTimeMillis() - start;
        PipesServer.STATUS status = null;
        try {
            status = PipesServer.STATUS.lookup(statusByte);
        }
        catch (IllegalArgumentException e) {
            throw new IOException("problem reading response from server " + (Object)((Object)status));
        }
        switch (status) {
            case OOM: {
                LOG.warn("pipesClientId={} oom: {} in {} ms", new Object[]{this.pipesClientId, t.getId(), millis});
                return PipesResult.OOM;
            }
            case TIMEOUT: {
                LOG.warn("pipesClientId={} server response timeout: {} in {} ms", new Object[]{this.pipesClientId, t.getId(), millis});
                return PipesResult.TIMEOUT;
            }
            case EMIT_EXCEPTION: {
                LOG.warn("pipesClientId={} emit exception: {} in {} ms", new Object[]{this.pipesClientId, t.getId(), millis});
                return this.readMessage(PipesResult.STATUS.EMIT_EXCEPTION);
            }
            case EMITTER_NOT_FOUND: {
                LOG.warn("pipesClientId={} emitter not found: {} in {} ms", new Object[]{this.pipesClientId, t.getId(), millis});
                return this.readMessage(PipesResult.STATUS.NO_EMITTER_FOUND);
            }
            case FETCHER_NOT_FOUND: {
                LOG.warn("pipesClientId={} fetcher not found: {} in {} ms", new Object[]{this.pipesClientId, t.getId(), millis});
                return this.readMessage(PipesResult.STATUS.NO_FETCHER_FOUND);
            }
            case FETCHER_INITIALIZATION_EXCEPTION: {
                LOG.warn("pipesClientId={} fetcher initialization exception: {} in {} ms", new Object[]{this.pipesClientId, t.getId(), millis});
                return this.readMessage(PipesResult.STATUS.FETCHER_INITIALIZATION_EXCEPTION);
            }
            case FETCH_EXCEPTION: {
                LOG.warn("pipesClientId={} fetch exception: {} in {} ms", new Object[]{this.pipesClientId, t.getId(), millis});
                return this.readMessage(PipesResult.STATUS.FETCH_EXCEPTION);
            }
            case PARSE_SUCCESS: {
                LOG.debug("pipesClientId={} parse success: {} in {} ms", new Object[]{this.pipesClientId, t.getId(), millis});
                return this.deserializeEmitData();
            }
            case PARSE_EXCEPTION_NO_EMIT: {
                return this.readMessage(PipesResult.STATUS.PARSE_EXCEPTION_NO_EMIT);
            }
            case EMIT_SUCCESS: {
                LOG.debug("pipesClientId={} emit success: {} in {} ms", new Object[]{this.pipesClientId, t.getId(), millis});
                return PipesResult.EMIT_SUCCESS;
            }
            case EMIT_SUCCESS_PARSE_EXCEPTION: {
                return this.readMessage(PipesResult.STATUS.EMIT_SUCCESS_PARSE_EXCEPTION);
            }
            case EMPTY_OUTPUT: {
                return PipesResult.EMPTY_OUTPUT;
            }
            case READY: 
            case CALL: 
            case PING: 
            case FAILED_TO_START: {
                throw new IOException("Not expecting this status: " + (Object)((Object)status));
            }
        }
        throw new IOException("Need to handle procesing for: " + (Object)((Object)status));
    }

    private PipesResult readMessage(PipesResult.STATUS status) throws IOException {
        int length = this.input.readInt();
        byte[] bytes = new byte[length];
        this.input.readFully(bytes);
        String msg = new String(bytes, StandardCharsets.UTF_8);
        return new PipesResult(status, msg);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private PipesResult deserializeEmitData() throws IOException {
        int length = this.input.readInt();
        byte[] bytes = new byte[length];
        this.input.readFully(bytes);
        try (ObjectInputStream objectInputStream = new ObjectInputStream((InputStream)new UnsynchronizedByteArrayInputStream(bytes));){
            EmitData emitData = (EmitData)objectInputStream.readObject();
            String stack = emitData.getContainerStackTrace();
            if (StringUtils.isBlank(stack)) {
                PipesResult pipesResult = new PipesResult(emitData);
                return pipesResult;
            }
            PipesResult pipesResult = new PipesResult(emitData, stack);
            return pipesResult;
        }
        catch (ClassNotFoundException e) {
            LOG.error("class not found exception deserializing data", (Throwable)e);
            throw new RuntimeException(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void restart() throws IOException, InterruptedException, TimeoutException {
        if (this.process != null) {
            LOG.debug("process still alive; trying to destroy it");
            this.destroyForcibly();
            boolean processEnded = this.process.waitFor(30L, TimeUnit.SECONDS);
            if (!processEnded) {
                LOG.warn("pipesClientId={}: process has not yet ended", (Object)this.pipesClientId);
            }
            this.executorService.shutdownNow();
            boolean shutdown = this.executorService.awaitTermination(30L, TimeUnit.SECONDS);
            if (!shutdown) {
                LOG.warn("pipesClientId={}: executorService has not yet shutdown", (Object)this.pipesClientId);
            }
            Object[] objectArray = this.executorServiceLock;
            synchronized (this.executorServiceLock) {
                if (this.closed) {
                    throw new IllegalArgumentException("pipesClientId=" + this.pipesClientId + ": PipesClient closed");
                }
                this.executorService = Executors.newFixedThreadPool(1);
                // ** MonitorExit[var3_6] (shouldn't be in output)
                LOG.info("pipesClientId={}: restarting process", (Object)this.pipesClientId);
            }
        } else {
            LOG.info("pipesClientId={}: starting process", (Object)this.pipesClientId);
        }
        {
            ProcessBuilder pb = new ProcessBuilder(this.getCommandline());
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            try {
                this.process = pb.start();
            }
            catch (Exception e) {
                LOG.error("failed to start client", (Throwable)e);
                throw new FailedToStartClientException(e);
            }
            this.input = new DataInputStream(this.process.getInputStream());
            this.output = new DataOutputStream(this.process.getOutputStream());
            UnsynchronizedByteArrayOutputStream bos = new UnsynchronizedByteArrayOutputStream();
            FutureTask<Integer> futureTask = new FutureTask<Integer>(() -> {
                int read;
                int b = this.input.read();
                for (read = 1; read < 20000 && b != PipesServer.STATUS.READY.getByte(); ++read) {
                    if (b == -1) {
                        throw new RuntimeException(PipesClient.getMsg("pipesClientId=" + this.pipesClientId + ": Couldn't start server -- read EOF before 'ready' byte.\n process isAlive=" + this.process.isAlive(), bos));
                    }
                    bos.write(b);
                    b = this.input.read();
                }
                if (read >= 20000) {
                    throw new RuntimeException(PipesClient.getMsg("pipesClientId=" + this.pipesClientId + ": Couldn't start server: read too many bytes before 'ready' byte.\n Make absolutely certain that your logger is not writing to stdout.\n", bos));
                }
                if (bos.size() > 0) {
                    LOG.warn("pipesClientId={}: From forked process before start byte: {}", (Object)this.pipesClientId, (Object)bos.toString(StandardCharsets.UTF_8));
                }
                return 1;
            });
            long start = System.currentTimeMillis();
            this.executorService.submit(futureTask);
            try {
                futureTask.get(this.pipesConfig.getStartupTimeoutMillis(), TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e) {
                this.destroyForcibly();
                throw e;
            }
            catch (ExecutionException e) {
                LOG.error("pipesClientId=" + this.pipesClientId + ": couldn't start server", (Throwable)e);
                this.destroyForcibly();
                throw new RuntimeException(e);
            }
            catch (TimeoutException e) {
                long elapsed = System.currentTimeMillis() - start;
                LOG.error("pipesClientId={} didn't receive ready byte from server within StartupTimeoutMillis {}; ms elapsed {}; did read >{}<", new Object[]{this.pipesClientId, this.pipesConfig.getStartupTimeoutMillis(), elapsed, bos.toString(StandardCharsets.UTF_8)});
                this.destroyForcibly();
                throw e;
            }
            finally {
                futureTask.cancel(true);
            }
            return;
        }
    }

    private static String getMsg(String msg, UnsynchronizedByteArrayOutputStream bos) {
        String readSoFar = bos.toString(StandardCharsets.UTF_8);
        if (StringUtils.isBlank(readSoFar)) {
            return msg;
        }
        return msg + "So far, I've read: >" + readSoFar + "<";
    }

    private String[] getCommandline() {
        List<String> configArgs = this.pipesConfig.getForkedJvmArgs();
        boolean hasClassPath = false;
        boolean hasHeadless = false;
        boolean hasExitOnOOM = false;
        boolean hasLog4j = false;
        String origGCString = null;
        String newGCLogString = null;
        for (String arg : configArgs) {
            if (arg.startsWith("-Djava.awt.headless")) {
                hasHeadless = true;
            }
            if (arg.equals("-cp") || arg.equals("--classpath")) {
                hasClassPath = true;
            }
            if (arg.equals("-XX:+ExitOnOutOfMemoryError") || arg.equals("-XX:+CrashOnOutOfMemoryError")) {
                hasExitOnOOM = true;
            }
            if (arg.startsWith("-Dlog4j.configuration")) {
                hasLog4j = true;
            }
            if (!arg.startsWith("-Xloggc:")) continue;
            origGCString = arg;
            newGCLogString = arg.replace("${pipesClientId}", "id-" + this.pipesClientId);
        }
        if (origGCString != null && newGCLogString != null) {
            configArgs.remove(origGCString);
            configArgs.add(newGCLogString);
        }
        ArrayList<String> commandLine = new ArrayList<String>();
        String javaPath = this.pipesConfig.getJavaPath();
        commandLine.add(ProcessUtils.escapeCommandLine(javaPath));
        if (!hasClassPath) {
            commandLine.add("-cp");
            commandLine.add(System.getProperty("java.class.path"));
        }
        if (!hasHeadless) {
            commandLine.add("-Djava.awt.headless=true");
        }
        if (hasExitOnOOM) {
            LOG.warn("I notice that you have an exit/crash on OOM. If you run heavy external processes like tesseract, this setting may result in orphaned processes which could be disastrous for performance.");
        }
        if (!hasLog4j) {
            commandLine.add("-Dlog4j.configurationFile=classpath:pipes-fork-server-default-log4j2.xml");
        }
        commandLine.add("-DpipesClientId=" + this.pipesClientId);
        commandLine.addAll(configArgs);
        commandLine.add("org.apache.tika.pipes.PipesServer");
        commandLine.add(ProcessUtils.escapeCommandLine(this.pipesConfig.getTikaConfig().toAbsolutePath().toString()));
        commandLine.add(Long.toString(this.pipesConfig.getMaxForEmitBatchBytes()));
        commandLine.add(Long.toString(this.pipesConfig.getTimeoutMillis()));
        commandLine.add(Long.toString(this.pipesConfig.getShutdownClientAfterMillis()));
        LOG.debug("pipesClientId={}: commandline: {}", (Object)this.pipesClientId, commandLine);
        return commandLine.toArray(new String[0]);
    }
}

