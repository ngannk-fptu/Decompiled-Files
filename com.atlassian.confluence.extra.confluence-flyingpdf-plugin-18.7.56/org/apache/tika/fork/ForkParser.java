/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.fork;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import org.apache.tika.config.Field;
import org.apache.tika.exception.TikaException;
import org.apache.tika.fork.ForkClient;
import org.apache.tika.fork.MetadataContentHandler;
import org.apache.tika.fork.ParserFactoryFactory;
import org.apache.tika.fork.TimeoutLimits;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.AbstractRecursiveParserWrapperHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class ForkParser
extends AbstractParser
implements Closeable {
    private static final long serialVersionUID = -4962742892274663950L;
    private final ClassLoader loader;
    private final Parser parser;
    private final Path tikaBin;
    private final ParserFactoryFactory parserFactoryFactory;
    private List<String> java = Arrays.asList("java", "-Xmx32m", "-Djava.awt.headless=true");
    @Field
    private int poolSize = 5;
    private int currentlyInUse = 0;
    private final Queue<ForkClient> pool = new LinkedList<ForkClient>();
    @Field
    private long serverPulseMillis = 1000L;
    @Field
    private long serverParseTimeoutMillis = 60000L;
    @Field
    private long serverWaitTimeoutMillis = 60000L;
    @Field
    private int maxFilesProcessedPerClient = -1;

    public ForkParser(Path tikaBin, ParserFactoryFactory factoryFactory) {
        this.loader = null;
        this.parser = null;
        this.tikaBin = tikaBin;
        this.parserFactoryFactory = factoryFactory;
    }

    public ForkParser(Path tikaBin, ParserFactoryFactory parserFactoryFactory, ClassLoader classLoader) {
        this.parser = null;
        this.loader = classLoader;
        this.tikaBin = tikaBin;
        this.parserFactoryFactory = parserFactoryFactory;
    }

    public ForkParser(ClassLoader loader, Parser parser) {
        if (parser instanceof ForkParser) {
            throw new IllegalArgumentException("The underlying parser of a ForkParser should not be a ForkParser, but a specific implementation.");
        }
        this.tikaBin = null;
        this.parserFactoryFactory = null;
        this.loader = loader;
        this.parser = parser;
    }

    public ForkParser(ClassLoader loader) {
        this(loader, new AutoDetectParser());
    }

    public ForkParser() {
        this(ForkParser.class.getClassLoader());
    }

    public synchronized int getPoolSize() {
        return this.poolSize;
    }

    public synchronized void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    @Deprecated
    public String getJavaCommand() {
        StringBuilder sb = new StringBuilder();
        for (String part : this.getJavaCommandAsList()) {
            sb.append(part).append(' ');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public List<String> getJavaCommandAsList() {
        return Collections.unmodifiableList(this.java);
    }

    public void setJavaCommand(List<String> java) {
        this.java = new ArrayList<String>(java);
    }

    @Deprecated
    public void setJavaCommand(String java) {
        this.setJavaCommand(Arrays.asList(java.split(" ")));
    }

    @Override
    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return this.parser.getSupportedTypes(context);
    }

    @Override
    public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
        Throwable t;
        if (stream == null) {
            throw new NullPointerException("null stream");
        }
        boolean alive = false;
        ForkClient client = this.acquireClient();
        try {
            ContentHandler tee = handler instanceof AbstractRecursiveParserWrapperHandler ? handler : new TeeContentHandler(handler, new MetadataContentHandler(metadata));
            t = client.call("parse", stream, tee, metadata, context);
            alive = true;
        }
        catch (TikaException te) {
            alive = true;
            throw te;
        }
        catch (IOException e) {
            throw new TikaException("Failed to communicate with a forked parser process. The process has most likely crashed due to some error like running out of memory. A new process will be started for the next parsing request.", e);
        }
        finally {
            this.releaseClient(client, alive);
        }
        if (t instanceof IOException) {
            throw (IOException)t;
        }
        if (t instanceof SAXException) {
            throw (SAXException)t;
        }
        if (t instanceof TikaException) {
            throw (TikaException)t;
        }
        if (t != null) {
            throw new TikaException("Unexpected error in forked server process", t);
        }
    }

    @Override
    public synchronized void close() {
        for (ForkClient client : this.pool) {
            client.close();
        }
        this.pool.clear();
        this.poolSize = 0;
    }

    private synchronized ForkClient acquireClient() throws IOException, TikaException {
        while (true) {
            ForkClient client;
            if ((client = this.pool.poll()) == null && this.currentlyInUse < this.poolSize) {
                client = this.newClient();
            }
            if (client != null && !client.ping()) {
                client.close();
                client = null;
            }
            if (client != null) {
                ++this.currentlyInUse;
                return client;
            }
            if (this.currentlyInUse < this.poolSize) continue;
            try {
                this.wait();
            }
            catch (InterruptedException e) {
                throw new TikaException("Interrupted while waiting for a fork parser", e);
            }
        }
    }

    private ForkClient newClient() throws IOException, TikaException {
        TimeoutLimits timeoutLimits = new TimeoutLimits(this.serverPulseMillis, this.serverParseTimeoutMillis, this.serverWaitTimeoutMillis);
        if (this.loader == null && this.parser == null && this.tikaBin != null && this.parserFactoryFactory != null) {
            return new ForkClient(this.tikaBin, this.parserFactoryFactory, this.java, timeoutLimits);
        }
        if (this.loader != null && this.parser != null && this.tikaBin == null && this.parserFactoryFactory == null) {
            return new ForkClient(this.loader, this.parser, this.java, timeoutLimits);
        }
        if (this.loader != null && this.parser == null && this.tikaBin != null && this.parserFactoryFactory != null) {
            return new ForkClient(this.tikaBin, this.parserFactoryFactory, this.loader, this.java, timeoutLimits);
        }
        throw new IllegalStateException("Unexpected combination of state items");
    }

    private synchronized void releaseClient(ForkClient client, boolean alive) {
        --this.currentlyInUse;
        if (this.currentlyInUse + this.pool.size() < this.poolSize && alive) {
            if (this.maxFilesProcessedPerClient > 0 && client.getFilesProcessed() >= this.maxFilesProcessedPerClient) {
                client.close();
            } else {
                this.pool.offer(client);
            }
            this.notifyAll();
        } else {
            client.close();
        }
    }

    public void setServerPulseMillis(long serverPulseMillis) {
        this.serverPulseMillis = serverPulseMillis;
    }

    public void setServerParseTimeoutMillis(long serverParseTimeoutMillis) {
        this.serverParseTimeoutMillis = serverParseTimeoutMillis;
    }

    public void setServerWaitTimeoutMillis(long serverWaitTimeoutMillis) {
        this.serverWaitTimeoutMillis = serverWaitTimeoutMillis;
    }

    public void setMaxFilesProcessedPerServer(int maxFilesProcessedPerClient) {
        this.maxFilesProcessedPerClient = maxFilesProcessedPerClient;
    }
}

