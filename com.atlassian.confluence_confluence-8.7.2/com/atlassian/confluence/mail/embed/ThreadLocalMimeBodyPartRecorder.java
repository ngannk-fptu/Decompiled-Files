/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.fugue.Pair
 *  com.google.common.io.ByteStreams
 *  javax.activation.DataSource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.mail.embed;

import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.mail.embed.MimeBodyPartRecorder;
import com.atlassian.confluence.mail.embed.MimeBodyPartReference;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.fugue.Pair;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import javax.activation.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadLocalMimeBodyPartRecorder
implements MimeBodyPartRecorder {
    private static final Logger log = LoggerFactory.getLogger(ThreadLocalMimeBodyPartRecorder.class);
    private static final ThreadLocal<Deque<Map<String, MimeBodyPartReference>>> recorderStack = new ThreadLocal();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Deprecated
    public <T> Pair<Maybe<T>, Iterable<MimeBodyPartReference>> record(Callable<T> callback) throws Exception {
        Collection<MimeBodyPartReference> references;
        Option maybeCallbackResult;
        Deque<Map<String, MimeBodyPartReference>> recorder = recorderStack.get();
        if (recorder == null) {
            recorder = new LinkedList<Map<String, MimeBodyPartReference>>();
            recorderStack.set(recorder);
        }
        recorder.push(new LinkedHashMap());
        try {
            T callbackResult = callback.call();
            maybeCallbackResult = callbackResult == null ? Option.none() : Option.some(callbackResult);
        }
        finally {
            references = recorder.pop().values();
            if (recorder.isEmpty()) {
                recorderStack.remove();
            }
        }
        if (log.isDebugEnabled() && !references.isEmpty()) {
            String names = references.stream().map(reference -> reference.getSource().getName()).collect(Collectors.joining(","));
            log.debug("Recorded DataSources with names [{}].", (Object)names);
        }
        return Pair.pair((Object)maybeCallbackResult, references);
    }

    @Override
    public boolean isRecording() {
        return recorderStack.get() != null;
    }

    @Override
    public Optional<MimeBodyPartReference> trackSource(DataSource source) {
        Deque<Map<String, MimeBodyPartReference>> tape = recorderStack.get();
        if (tape == null) {
            log.info("[{}] was not called within a record closure or outside of the record thread.", (Object)ThreadLocalMimeBodyPartRecorder.class.getName());
            return Optional.empty();
        }
        Map<String, MimeBodyPartReference> references = tape.peek();
        MimeBodyPartReference newReference = new MimeBodyPartReference(source);
        MimeBodyPartReference oldReference = references.put(source.getName(), newReference);
        if (oldReference != null) {
            ThreadLocalMimeBodyPartRecorder.compareDataSources(oldReference.getSource(), newReference.getSource());
        }
        if (log.isTraceEnabled()) {
            log.trace(String.format("Tracked usage of DataSource with name [%s].", source.getName()), new Throwable());
        }
        return Optional.of(newReference);
    }

    private static void compareDataSources(DataSource oldDataSource, DataSource newDataSource) {
        ThreadLocalMimeBodyPartRecorder.escapeInDevModeOrLogError(!Objects.equals(oldDataSource.getContentType(), newDataSource.getContentType()), "Got two DataSources with name [%s], but the first recorded one has content type [%s] and the second has [%s].", oldDataSource.getName(), oldDataSource.getContentType(), newDataSource.getContentType());
        if (log.isDebugEnabled()) {
            ThreadLocalMimeBodyPartRecorder.compareDataSourceBinaries(oldDataSource, newDataSource);
        }
    }

    private static void compareDataSourceBinaries(DataSource oldDataSource, DataSource newDataSource) {
        try {
            byte[] oldReferenceData = ThreadLocalMimeBodyPartRecorder.readDataSourceIntoMemory(oldDataSource);
            byte[] newReferenceData = ThreadLocalMimeBodyPartRecorder.readDataSourceIntoMemory(newDataSource);
            ThreadLocalMimeBodyPartRecorder.escapeInDevModeOrLogError(!Arrays.equals(oldReferenceData, newReferenceData), "Got two DataSources with name [%s], but their data differs.", oldDataSource.getName());
        }
        catch (IOException e) {
            log.error("Reading the data of one of the DataSources with name [{}] into memory escaped.", (Object)e.toString());
        }
    }

    private static void escapeInDevModeOrLogError(boolean expression, String message, Object ... messageArguments) {
        if (expression) {
            String formattedMessage = String.format(message, messageArguments);
            if (ConfluenceSystemProperties.isDevMode()) {
                throw new IllegalStateException(formattedMessage);
            }
            log.error(formattedMessage);
        }
    }

    private static byte[] readDataSourceIntoMemory(DataSource source) throws IOException {
        try (InputStream sourceInputStream = source.getInputStream();){
            byte[] byArray = ByteStreams.toByteArray((InputStream)sourceInputStream);
            return byArray;
        }
    }
}

