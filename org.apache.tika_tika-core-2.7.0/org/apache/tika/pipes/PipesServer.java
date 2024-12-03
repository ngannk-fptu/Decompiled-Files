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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.EncryptedDocumentException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.extractor.DocumentSelector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.RecursiveParserWrapper;
import org.apache.tika.pipes.FetchEmitTuple;
import org.apache.tika.pipes.HandlerConfig;
import org.apache.tika.pipes.emitter.EmitData;
import org.apache.tika.pipes.emitter.EmitKey;
import org.apache.tika.pipes.emitter.Emitter;
import org.apache.tika.pipes.emitter.EmitterManager;
import org.apache.tika.pipes.emitter.TikaEmitterException;
import org.apache.tika.pipes.fetcher.FetchKey;
import org.apache.tika.pipes.fetcher.Fetcher;
import org.apache.tika.pipes.fetcher.FetcherManager;
import org.apache.tika.pipes.fetcher.RangeFetcher;
import org.apache.tika.sax.BasicContentHandlerFactory;
import org.apache.tika.sax.RecursiveParserWrapperHandler;
import org.apache.tika.utils.ExceptionUtils;
import org.apache.tika.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class PipesServer
implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(PipesServer.class);
    public static final int TIMEOUT_EXIT_CODE = 17;
    private final Object[] lock = new Object[0];
    private long checkForTimeoutMs = 1000L;
    private final Path tikaConfigPath;
    private final DataInputStream input;
    private final DataOutputStream output;
    private final long maxForEmitBatchBytes;
    private final long serverParseTimeoutMillis;
    private final long serverWaitTimeoutMillis;
    private Parser autoDetectParser;
    private Parser rMetaParser;
    private TikaConfig tikaConfig;
    private FetcherManager fetcherManager;
    private EmitterManager emitterManager;
    private volatile boolean parsing;
    private volatile long since;

    public PipesServer(Path tikaConfigPath, InputStream in, PrintStream out, long maxForEmitBatchBytes, long serverParseTimeoutMillis, long serverWaitTimeoutMillis) throws IOException, TikaException, SAXException {
        this.tikaConfigPath = tikaConfigPath;
        this.input = new DataInputStream(in);
        this.output = new DataOutputStream(out);
        this.maxForEmitBatchBytes = maxForEmitBatchBytes;
        this.serverParseTimeoutMillis = serverParseTimeoutMillis;
        this.serverWaitTimeoutMillis = serverWaitTimeoutMillis;
        this.parsing = false;
        this.since = System.currentTimeMillis();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] args) throws Exception {
        try {
            Path tikaConfig = Paths.get(args[0], new String[0]);
            long maxForEmitBatchBytes = Long.parseLong(args[1]);
            long serverParseTimeoutMillis = Long.parseLong(args[2]);
            long serverWaitTimeoutMillis = Long.parseLong(args[3]);
            PipesServer server = new PipesServer(tikaConfig, System.in, System.out, maxForEmitBatchBytes, serverParseTimeoutMillis, serverWaitTimeoutMillis);
            System.setIn((InputStream)new UnsynchronizedByteArrayInputStream(new byte[0]));
            System.setOut(System.err);
            Thread watchdog = new Thread((Runnable)server, "Tika Watchdog");
            watchdog.setDaemon(true);
            watchdog.start();
            server.processRequests();
        }
        finally {
            LOG.info("server shutting down");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    @Override
    public void run() {
        try {
            while (true) {
                Object[] objectArray = this.lock;
                // MONITORENTER : this.lock
                long elapsed = System.currentTimeMillis() - this.since;
                if (this.parsing && elapsed > this.serverParseTimeoutMillis) {
                    LOG.warn("timeout server; elapsed {}  with {}", (Object)elapsed, (Object)this.serverParseTimeoutMillis);
                    this.exit(17);
                } else if (!this.parsing && this.serverWaitTimeoutMillis > 0L && elapsed > this.serverWaitTimeoutMillis) {
                    LOG.info("closing down from inactivity");
                    this.exit(0);
                }
                // MONITOREXIT : objectArray
                Thread.sleep(this.checkForTimeoutMs);
            }
        }
        catch (InterruptedException e) {
            LOG.debug("interrupted");
            return;
        }
    }

    public void processRequests() {
        long start;
        LOG.debug("processing requests {}");
        try {
            start = System.currentTimeMillis();
            this.initializeParser();
            if (LOG.isTraceEnabled()) {
                LOG.trace("timer -- initialize parser: {} ms", (Object)(System.currentTimeMillis() - start));
            }
            LOG.debug("pipes server initialized");
        }
        catch (Throwable t) {
            LOG.error("couldn't initialize parser", t);
            try {
                this.output.writeByte(STATUS.FAILED_TO_START.getByte());
                this.output.flush();
            }
            catch (IOException e) {
                LOG.warn("couldn't notify of failure to start", (Throwable)e);
            }
            return;
        }
        try {
            this.write(STATUS.READY);
            start = System.currentTimeMillis();
            while (true) {
                int request;
                if ((request = this.input.read()) == -1) {
                    LOG.warn("received -1 from client; shutting down");
                    this.exit(1);
                } else if (request == STATUS.PING.getByte()) {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("timer -- ping: {} ms", (Object)(System.currentTimeMillis() - start));
                    }
                    this.write(STATUS.PING);
                    start = System.currentTimeMillis();
                } else if (request == STATUS.CALL.getByte()) {
                    this.parseOne();
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("timer -- parse one: {} ms", (Object)(System.currentTimeMillis() - start));
                    }
                    start = System.currentTimeMillis();
                } else {
                    throw new IllegalStateException("Unexpected request");
                }
                this.output.flush();
            }
        }
        catch (Throwable t) {
            LOG.error("main loop error (did the forking process shut down?)", t);
            this.exit(1);
            System.err.flush();
            return;
        }
    }

    private boolean metadataIsEmpty(List<Metadata> metadataList) {
        return metadataList == null || metadataList.size() == 0;
    }

    private String getContainerStacktrace(FetchEmitTuple t, List<Metadata> metadataList) {
        if (metadataList == null || metadataList.size() < 1) {
            return "";
        }
        String stack = metadataList.get(0).get(TikaCoreProperties.CONTAINER_EXCEPTION);
        return stack != null ? stack : "";
    }

    private void emit(String taskId, EmitData emitData, String parseExceptionStack) {
        Emitter emitter = null;
        try {
            emitter = this.emitterManager.getEmitter(emitData.getEmitKey().getEmitterName());
        }
        catch (IllegalArgumentException e) {
            String noEmitterMsg = this.getNoEmitterMsg(taskId);
            LOG.warn(noEmitterMsg);
            this.write(STATUS.EMITTER_NOT_FOUND, noEmitterMsg);
            return;
        }
        try {
            emitter.emit(emitData.getEmitKey().getEmitKey(), emitData.getMetadataList());
        }
        catch (IOException | TikaEmitterException e) {
            LOG.warn("emit exception", (Throwable)e);
            String msg = ExceptionUtils.getStackTrace(e);
            byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
            this.write(STATUS.EMIT_EXCEPTION, bytes);
            return;
        }
        if (StringUtils.isBlank(parseExceptionStack)) {
            this.write(STATUS.EMIT_SUCCESS);
        } else {
            this.write(STATUS.EMIT_SUCCESS_PARSE_EXCEPTION, parseExceptionStack.getBytes(StandardCharsets.UTF_8));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void parseOne() {
        Object[] objectArray = this.lock;
        synchronized (this.lock) {
            this.parsing = true;
            this.since = System.currentTimeMillis();
            // ** MonitorExit[var1_1] (shouldn't be in output)
            FetchEmitTuple t = null;
            try {
                long start = System.currentTimeMillis();
                t = this.readFetchEmitTuple();
                if (LOG.isTraceEnabled()) {
                    LOG.trace("timer -- read fetchEmitTuple: {} ms", (Object)(System.currentTimeMillis() - start));
                }
                start = System.currentTimeMillis();
                this.actuallyParse(t);
                if (LOG.isTraceEnabled()) {
                    LOG.trace("timer -- actually parsed: {} ms", (Object)(System.currentTimeMillis() - start));
                }
            }
            catch (OutOfMemoryError e) {
                this.handleOOM(t.getId(), e);
            }
            finally {
                Object[] objectArray2 = this.lock;
                synchronized (this.lock) {
                    this.parsing = false;
                    this.since = System.currentTimeMillis();
                    // ** MonitorExit[var2_6] (shouldn't be in output)
                }
            }
            return;
        }
    }

    private void actuallyParse(FetchEmitTuple t) {
        long start = System.currentTimeMillis();
        Fetcher fetcher = this.getFetcher(t);
        if (fetcher == null) {
            return;
        }
        if (LOG.isTraceEnabled()) {
            long elapsed = System.currentTimeMillis() - start;
            LOG.trace("timer -- got fetcher: {}ms", (Object)elapsed);
        }
        start = System.currentTimeMillis();
        List<Metadata> metadataList = this.parseIt(t, fetcher);
        if (LOG.isTraceEnabled()) {
            LOG.trace("timer -- to parse: {} ms", (Object)(System.currentTimeMillis() - start));
        }
        if (this.metadataIsEmpty(metadataList)) {
            this.write(STATUS.EMPTY_OUTPUT);
            return;
        }
        this.emitIt(t, metadataList);
    }

    private void emitIt(FetchEmitTuple t, List<Metadata> metadataList) {
        long start = System.currentTimeMillis();
        String stack = this.getContainerStacktrace(t, metadataList);
        this.filterMetadata(metadataList);
        if (StringUtils.isBlank(stack) || t.getOnParseException() == FetchEmitTuple.ON_PARSE_EXCEPTION.EMIT) {
            this.injectUserMetadata(t.getMetadata(), metadataList);
            EmitKey emitKey = t.getEmitKey();
            if (StringUtils.isBlank(emitKey.getEmitKey())) {
                emitKey = new EmitKey(emitKey.getEmitterName(), t.getFetchKey().getFetchKey());
                t.setEmitKey(emitKey);
            }
            EmitData emitData = new EmitData(t.getEmitKey(), metadataList, stack);
            if (this.maxForEmitBatchBytes >= 0L && emitData.getEstimatedSizeBytes() >= this.maxForEmitBatchBytes) {
                this.emit(t.getId(), emitData, stack);
                if (LOG.isTraceEnabled()) {
                    LOG.trace("timer -- emitted: {} ms", (Object)(System.currentTimeMillis() - start));
                }
            } else {
                this.write(emitData);
                if (LOG.isTraceEnabled()) {
                    LOG.trace("timer -- to write data: {} ms", (Object)(System.currentTimeMillis() - start));
                }
            }
        } else {
            this.write(STATUS.PARSE_EXCEPTION_NO_EMIT, stack);
        }
    }

    private void filterMetadata(List<Metadata> metadataList) {
        for (Metadata m : metadataList) {
            try {
                this.tikaConfig.getMetadataFilter().filter(m);
            }
            catch (TikaException e) {
                LOG.warn("failed to filter metadata", (Throwable)e);
            }
        }
    }

    private Fetcher getFetcher(FetchEmitTuple t) {
        try {
            return this.fetcherManager.getFetcher(t.getFetchKey().getFetcherName());
        }
        catch (IllegalArgumentException e) {
            String noFetcherMsg = this.getNoFetcherMsg(t.getFetchKey().getFetcherName());
            LOG.warn(noFetcherMsg);
            this.write(STATUS.FETCHER_NOT_FOUND, noFetcherMsg);
            return null;
        }
        catch (IOException | TikaException e) {
            LOG.warn("Couldn't initialize fetcher for fetch id '" + t.getId() + "'", (Throwable)e);
            this.write(STATUS.FETCHER_INITIALIZATION_EXCEPTION, ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private List<Metadata> parseIt(FetchEmitTuple t, Fetcher fetcher) {
        FetchKey fetchKey = t.getFetchKey();
        if (fetchKey.hasRange()) {
            if (!(fetcher instanceof RangeFetcher)) {
                throw new IllegalArgumentException("fetch key has a range, but the fetcher is not a range fetcher");
            }
            Metadata metadata = new Metadata();
            try (InputStream stream2 = ((RangeFetcher)fetcher).fetch(fetchKey.getFetchKey(), fetchKey.getRangeStart(), fetchKey.getRangeEnd(), metadata);){
                List<Metadata> list2 = this.parse(t, stream2, metadata);
                return list2;
            }
            catch (SecurityException e) {
                LOG.error("security exception " + t.getId(), (Throwable)e);
                throw e;
            }
            catch (IOException | TikaException e) {
                LOG.warn("fetch exception " + t.getId(), (Throwable)e);
                this.write(STATUS.FETCH_EXCEPTION, ExceptionUtils.getStackTrace(e));
                return null;
            }
        }
        Metadata metadata = new Metadata();
        try (InputStream stream = fetcher.fetch(t.getFetchKey().getFetchKey(), metadata);){
            List<Metadata> list = this.parse(t, stream, metadata);
            return list;
        }
        catch (SecurityException e) {
            LOG.error("security exception " + t.getId(), (Throwable)e);
            throw e;
        }
        catch (IOException | TikaException e) {
            LOG.warn("fetch exception " + t.getId(), (Throwable)e);
            this.write(STATUS.FETCH_EXCEPTION, ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    private String getNoFetcherMsg(String fetcherName) {
        StringBuilder sb = new StringBuilder();
        sb.append("Fetcher '").append(fetcherName).append("'");
        sb.append(" not found.");
        sb.append("\nThe configured FetcherManager supports:");
        int i = 0;
        for (String f : this.fetcherManager.getSupported()) {
            if (i++ > 0) {
                sb.append(", ");
            }
            sb.append(f);
        }
        return sb.toString();
    }

    private String getNoEmitterMsg(String emitterName) {
        StringBuilder sb = new StringBuilder();
        sb.append("Emitter '").append(emitterName).append("'");
        sb.append(" not found.");
        sb.append("\nThe configured emitterManager supports:");
        int i = 0;
        for (String e : this.emitterManager.getSupported()) {
            if (i++ > 0) {
                sb.append(", ");
            }
            sb.append(e);
        }
        return sb.toString();
    }

    private void handleOOM(String taskId, OutOfMemoryError oom) {
        this.write(STATUS.OOM);
        LOG.error("oom: " + taskId, (Throwable)oom);
        this.exit(1);
    }

    private List<Metadata> parse(FetchEmitTuple fetchEmitTuple, InputStream stream, Metadata metadata) {
        HandlerConfig handlerConfig = fetchEmitTuple.getHandlerConfig();
        if (handlerConfig.getParseMode() == HandlerConfig.PARSE_MODE.RMETA) {
            return this.parseRecursive(fetchEmitTuple, handlerConfig, stream, metadata);
        }
        return this.parseConcatenated(fetchEmitTuple, handlerConfig, stream, metadata);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private List<Metadata> parseConcatenated(FetchEmitTuple fetchEmitTuple, final HandlerConfig handlerConfig, InputStream stream, Metadata metadata) {
        BasicContentHandlerFactory contentHandlerFactory = new BasicContentHandlerFactory(handlerConfig.getType(), handlerConfig.getWriteLimit());
        ContentHandler handler = contentHandlerFactory.getNewContentHandler();
        ParseContext parseContext = new ParseContext();
        parseContext.set(DocumentSelector.class, new DocumentSelector(){
            final int maxEmbedded;
            int embedded;
            {
                this.maxEmbedded = handlerConfig.maxEmbeddedResources;
                this.embedded = 0;
            }

            @Override
            public boolean select(Metadata metadata) {
                if (this.maxEmbedded < 0) {
                    return true;
                }
                return this.embedded++ < this.maxEmbedded;
            }
        });
        String containerException = null;
        long start = System.currentTimeMillis();
        try {
            this.autoDetectParser.parse(stream, handler, metadata, parseContext);
        }
        catch (SAXException e) {
            containerException = ExceptionUtils.getStackTrace(e);
            LOG.warn("sax problem:" + fetchEmitTuple.getId(), (Throwable)e);
        }
        catch (EncryptedDocumentException e) {
            containerException = ExceptionUtils.getStackTrace(e);
            LOG.warn("encrypted document:" + fetchEmitTuple.getId(), (Throwable)e);
        }
        catch (SecurityException e) {
            LOG.warn("security exception:" + fetchEmitTuple.getId(), (Throwable)e);
            throw e;
        }
        catch (Exception e) {
            containerException = ExceptionUtils.getStackTrace(e);
            LOG.warn("parse exception: " + fetchEmitTuple.getId(), (Throwable)e);
        }
        finally {
            metadata.add(TikaCoreProperties.TIKA_CONTENT, handler.toString());
            if (containerException != null) {
                metadata.add(TikaCoreProperties.CONTAINER_EXCEPTION, containerException);
            }
            if (LOG.isTraceEnabled()) {
                LOG.trace("timer -- parse only time: {} ms", (Object)(System.currentTimeMillis() - start));
            }
        }
        return Collections.singletonList(metadata);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private List<Metadata> parseRecursive(FetchEmitTuple fetchEmitTuple, HandlerConfig handlerConfig, InputStream stream, Metadata metadata) {
        RecursiveParserWrapperHandler handler = new RecursiveParserWrapperHandler(new BasicContentHandlerFactory(handlerConfig.getType(), handlerConfig.getWriteLimit()), handlerConfig.getMaxEmbeddedResources());
        ParseContext parseContext = new ParseContext();
        long start = System.currentTimeMillis();
        try {
            this.rMetaParser.parse(stream, handler, metadata, parseContext);
        }
        catch (SAXException e) {
            LOG.warn("sax problem:" + fetchEmitTuple.getId(), (Throwable)e);
        }
        catch (EncryptedDocumentException e) {
            LOG.warn("encrypted document:" + fetchEmitTuple.getId(), (Throwable)e);
        }
        catch (SecurityException e) {
            LOG.warn("security exception:" + fetchEmitTuple.getId(), (Throwable)e);
            throw e;
        }
        catch (Exception e) {
            LOG.warn("parse exception: " + fetchEmitTuple.getId(), (Throwable)e);
        }
        finally {
            if (LOG.isTraceEnabled()) {
                LOG.trace("timer -- parse only time: {} ms", (Object)(System.currentTimeMillis() - start));
            }
        }
        return handler.getMetadataList();
    }

    private void injectUserMetadata(Metadata userMetadata, List<Metadata> metadataList) {
        for (String n : userMetadata.names()) {
            metadataList.get(0).set(n, (String)null);
            for (String val : userMetadata.getValues(n)) {
                metadataList.get(0).add(n, val);
            }
        }
    }

    private void exit(int exitCode) {
        if (exitCode != 0) {
            LOG.error("exiting: {}", (Object)exitCode);
        } else {
            LOG.info("exiting: {}", (Object)exitCode);
        }
        System.exit(exitCode);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private FetchEmitTuple readFetchEmitTuple() {
        try {
            int length = this.input.readInt();
            byte[] bytes = new byte[length];
            this.input.readFully(bytes);
            try (ObjectInputStream objectInputStream = new ObjectInputStream((InputStream)new UnsynchronizedByteArrayInputStream(bytes));){
                FetchEmitTuple fetchEmitTuple = (FetchEmitTuple)objectInputStream.readObject();
                return fetchEmitTuple;
            }
        }
        catch (IOException e) {
            LOG.error("problem reading tuple", (Throwable)e);
            this.exit(1);
            return null;
        }
        catch (ClassNotFoundException e) {
            LOG.error("can't find class?!", (Throwable)e);
            this.exit(1);
        }
        return null;
    }

    private void initializeParser() throws TikaException, IOException, SAXException {
        this.tikaConfig = new TikaConfig(this.tikaConfigPath);
        this.fetcherManager = FetcherManager.load(this.tikaConfigPath);
        this.emitterManager = EmitterManager.load(this.tikaConfigPath);
        this.autoDetectParser = new AutoDetectParser(this.tikaConfig);
        this.rMetaParser = new RecursiveParserWrapper(this.autoDetectParser);
    }

    private void write(EmitData emitData) {
        try {
            UnsynchronizedByteArrayOutputStream bos = new UnsynchronizedByteArrayOutputStream();
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream((OutputStream)bos);){
                objectOutputStream.writeObject(emitData);
            }
            this.write(STATUS.PARSE_SUCCESS, bos.toByteArray());
        }
        catch (IOException e) {
            LOG.error("problem writing emit data (forking process shutdown?)", (Throwable)e);
            this.exit(1);
        }
    }

    private void write(STATUS status, String msg) {
        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        this.write(status, bytes);
    }

    private void write(STATUS status, byte[] bytes) {
        try {
            int len = bytes.length;
            this.output.write(status.getByte());
            this.output.writeInt(len);
            this.output.write(bytes);
            this.output.flush();
        }
        catch (IOException e) {
            LOG.error("problem writing data (forking process shutdown?)", (Throwable)e);
            this.exit(1);
        }
    }

    private void write(STATUS status) {
        try {
            this.output.write(status.getByte());
            this.output.flush();
        }
        catch (IOException e) {
            LOG.error("problem writing data (forking process shutdown?)", (Throwable)e);
            this.exit(1);
        }
    }

    public static enum STATUS {
        READY,
        CALL,
        PING,
        FAILED_TO_START,
        FETCHER_NOT_FOUND,
        EMITTER_NOT_FOUND,
        FETCHER_INITIALIZATION_EXCEPTION,
        FETCH_EXCEPTION,
        PARSE_SUCCESS,
        PARSE_EXCEPTION_NO_EMIT,
        EMIT_SUCCESS,
        EMIT_SUCCESS_PARSE_EXCEPTION,
        EMIT_EXCEPTION,
        OOM,
        TIMEOUT,
        EMPTY_OUTPUT;


        byte getByte() {
            return (byte)(this.ordinal() + 1);
        }

        public static STATUS lookup(int val) {
            int i = val - 1;
            if (i < 0) {
                throw new IllegalArgumentException("byte must be > 0");
            }
            STATUS[] statuses = STATUS.values();
            if (i >= statuses.length) {
                throw new IllegalArgumentException("byte with index " + i + " must be < " + statuses.length);
            }
            return statuses[i];
        }
    }
}

