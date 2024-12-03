/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.concurrent.Executor;
import org.apache.tika.exception.ZeroByteFileException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;

public class ParsingReader
extends Reader {
    private final Parser parser;
    private final Reader reader;
    private final Writer writer;
    private final InputStream stream;
    private final Metadata metadata;
    private final ParseContext context;
    private transient Throwable throwable;

    private static Metadata getMetadata(String name) {
        Metadata metadata = new Metadata();
        if (name != null && name.length() > 0) {
            metadata.set("resourceName", name);
        }
        return metadata;
    }

    public ParsingReader(InputStream stream) throws IOException {
        this(new AutoDetectParser(), stream, new Metadata(), new ParseContext());
        this.context.set(Parser.class, this.parser);
    }

    public ParsingReader(InputStream stream, String name) throws IOException {
        this(new AutoDetectParser(), stream, ParsingReader.getMetadata(name), new ParseContext());
        this.context.set(Parser.class, this.parser);
    }

    public ParsingReader(Path path) throws IOException {
        this(Files.newInputStream(path, new OpenOption[0]), path.getFileName().toString());
    }

    public ParsingReader(File file) throws FileNotFoundException, IOException {
        this(new FileInputStream(file), file.getName());
    }

    public ParsingReader(Parser parser, InputStream stream, final Metadata metadata, ParseContext context) throws IOException {
        this(parser, stream, metadata, context, new Executor(){

            @Override
            public void execute(Runnable command) {
                String name = metadata.get("resourceName");
                name = name != null ? "Apache Tika: " + name : "Apache Tika";
                Thread thread = new Thread(command, name);
                thread.setDaemon(true);
                thread.start();
            }
        });
    }

    public ParsingReader(Parser parser, InputStream stream, Metadata metadata, ParseContext context, Executor executor) throws IOException {
        this.parser = parser;
        PipedReader pipedReader = new PipedReader();
        this.reader = new BufferedReader(pipedReader);
        try {
            this.writer = new PipedWriter(pipedReader);
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
        this.stream = stream;
        this.metadata = metadata;
        this.context = context;
        executor.execute(new ParsingTask());
        this.reader.mark(1);
        this.reader.read();
        this.reader.reset();
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (this.throwable instanceof ZeroByteFileException) {
            return -1;
        }
        if (this.throwable instanceof IOException) {
            throw (IOException)this.throwable;
        }
        if (this.throwable != null) {
            IOException exception = new IOException("");
            exception.initCause(this.throwable);
            throw exception;
        }
        return this.reader.read(cbuf, off, len);
    }

    @Override
    public void close() throws IOException {
        this.reader.close();
    }

    private class ParsingTask
    implements Runnable {
        private ParsingTask() {
        }

        @Override
        public void run() {
            block7: {
                block6: {
                    try {
                        BodyContentHandler handler = new BodyContentHandler(ParsingReader.this.writer);
                        ParsingReader.this.parser.parse(ParsingReader.this.stream, handler, ParsingReader.this.metadata, ParsingReader.this.context);
                    }
                    catch (Throwable t) {
                        ParsingReader.this.throwable = t;
                    }
                    try {
                        ParsingReader.this.stream.close();
                    }
                    catch (Throwable t) {
                        if (ParsingReader.this.throwable != null) break block6;
                        ParsingReader.this.throwable = t;
                    }
                }
                try {
                    ParsingReader.this.writer.close();
                }
                catch (Throwable t) {
                    if (ParsingReader.this.throwable != null) break block7;
                    ParsingReader.this.throwable = t;
                }
            }
        }
    }
}

