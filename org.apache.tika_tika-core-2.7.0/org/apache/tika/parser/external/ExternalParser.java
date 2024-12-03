/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.io.output.NullOutputStream
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.tika.parser.external;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.XHTMLContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class ExternalParser
extends AbstractParser {
    private static final Logger LOG = LoggerFactory.getLogger(ExternalParser.class);
    public static final String INPUT_FILE_TOKEN = "${INPUT}";
    public static final String OUTPUT_FILE_TOKEN = "${OUTPUT}";
    private static final long serialVersionUID = -1079128990650687037L;
    private final long timeoutMs = 60000L;
    private Set<MediaType> supportedTypes = Collections.emptySet();
    private Map<Pattern, String> metadataPatterns = null;
    private String[] command = new String[]{"cat"};
    private LineConsumer ignoredLineConsumer = LineConsumer.NULL;

    private static void ignoreStream(InputStream stream) {
        ExternalParser.ignoreStream(stream, true);
    }

    private static Thread ignoreStream(InputStream stream, boolean waitForDeath) {
        Thread t = new Thread(() -> {
            try {
                IOUtils.copy((InputStream)stream, (OutputStream)NullOutputStream.NULL_OUTPUT_STREAM);
            }
            catch (IOException iOException) {
            }
            finally {
                IOUtils.closeQuietly((InputStream)stream);
            }
        });
        t.start();
        if (waitForDeath) {
            try {
                t.join();
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
        return t;
    }

    public static boolean check(String checkCmd, int ... errorValue) {
        return ExternalParser.check(new String[]{checkCmd}, errorValue);
    }

    public static boolean check(String[] checkCmd, int ... errorValue) {
        if (errorValue.length == 0) {
            errorValue = new int[]{127};
        }
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(checkCmd);
            Thread stdErrSuckerThread = ExternalParser.ignoreStream(process.getErrorStream(), false);
            Thread stdOutSuckerThread = ExternalParser.ignoreStream(process.getInputStream(), false);
            stdErrSuckerThread.join();
            stdOutSuckerThread.join();
            boolean finished = process.waitFor(60000L, TimeUnit.MILLISECONDS);
            if (!finished) {
                throw new TimeoutException();
            }
            int result = process.exitValue();
            LOG.debug("exit value for {}: {}", (Object)checkCmd[0], (Object)result);
            for (int err : errorValue) {
                if (result != err) continue;
                boolean bl = false;
                return bl;
            }
            boolean bl = true;
            return bl;
        }
        catch (IOException | InterruptedException | TimeoutException e) {
            LOG.debug("exception trying to run  " + checkCmd[0], (Throwable)e);
            boolean bl = false;
            return bl;
        }
        catch (SecurityException se) {
            throw se;
        }
        catch (Error err) {
            if (err.getMessage() != null && (err.getMessage().contains("posix_spawn") || err.getMessage().contains("UNIXProcess"))) {
                LOG.debug("(TIKA-1526): exception trying to run: " + checkCmd[0], (Throwable)err);
                boolean bl = false;
                return bl;
            }
            throw err;
        }
        finally {
            if (process != null) {
                process.destroyForcibly();
            }
        }
    }

    @Override
    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return this.getSupportedTypes();
    }

    public Set<MediaType> getSupportedTypes() {
        return this.supportedTypes;
    }

    public void setSupportedTypes(Set<MediaType> supportedTypes) {
        this.supportedTypes = Collections.unmodifiableSet(new HashSet<MediaType>(supportedTypes));
    }

    public String[] getCommand() {
        return this.command;
    }

    public void setCommand(String ... command) {
        this.command = command;
    }

    public LineConsumer getIgnoredLineConsumer() {
        return this.ignoredLineConsumer;
    }

    public void setIgnoredLineConsumer(LineConsumer ignoredLineConsumer) {
        this.ignoredLineConsumer = ignoredLineConsumer;
    }

    public Map<Pattern, String> getMetadataExtractionPatterns() {
        return this.metadataPatterns;
    }

    public void setMetadataExtractionPatterns(Map<Pattern, String> patterns) {
        this.metadataPatterns = patterns;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
        XHTMLContentHandler xhtml = new XHTMLContentHandler(handler, metadata);
        TemporaryResources tmp = new TemporaryResources();
        try {
            this.parse(TikaInputStream.get(stream, tmp, metadata), xhtml, metadata, tmp);
        }
        finally {
            tmp.dispose();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void parse(TikaInputStream stream, XHTMLContentHandler xhtml, Metadata metadata, TemporaryResources tmp) throws IOException, SAXException, TikaException {
        String[] cmd;
        boolean inputToStdIn = true;
        boolean outputFromStdOut = true;
        boolean hasPatterns = this.metadataPatterns != null && !this.metadataPatterns.isEmpty();
        File output = null;
        if (this.command.length == 1) {
            cmd = this.command[0].split(" ");
        } else {
            cmd = new String[this.command.length];
            System.arraycopy(this.command, 0, cmd, 0, this.command.length);
        }
        for (int i = 0; i < cmd.length; ++i) {
            if (cmd[i].contains(INPUT_FILE_TOKEN)) {
                cmd[i] = cmd[i].replace(INPUT_FILE_TOKEN, stream.getFile().getPath());
                inputToStdIn = false;
            }
            if (!cmd[i].contains(OUTPUT_FILE_TOKEN)) continue;
            output = tmp.createTemporaryFile();
            outputFromStdOut = false;
            cmd[i] = cmd[i].replace(OUTPUT_FILE_TOKEN, output.getPath());
        }
        Process process = null;
        try {
            process = cmd.length == 1 ? Runtime.getRuntime().exec(cmd[0]) : Runtime.getRuntime().exec(cmd);
        }
        catch (Exception e) {
            LOG.warn("problem with process exec", (Throwable)e);
        }
        try {
            if (inputToStdIn) {
                this.sendInput(process, (InputStream)((Object)stream));
            } else {
                process.getOutputStream().close();
            }
            InputStream out = process.getInputStream();
            InputStream err = process.getErrorStream();
            if (hasPatterns) {
                this.extractMetadata(err, metadata);
                if (outputFromStdOut) {
                    this.extractOutput(out, xhtml);
                } else {
                    this.extractMetadata(out, metadata);
                }
            } else {
                ExternalParser.ignoreStream(err);
                if (outputFromStdOut) {
                    this.extractOutput(out, xhtml);
                } else {
                    ExternalParser.ignoreStream(out);
                }
            }
        }
        finally {
            try {
                process.waitFor();
            }
            catch (InterruptedException out) {}
        }
        if (!outputFromStdOut) {
            try (FileInputStream fileInputStream = new FileInputStream(output);){
                this.extractOutput(fileInputStream, xhtml);
            }
        }
    }

    private void extractOutput(InputStream stream, XHTMLContentHandler xhtml) throws SAXException, IOException {
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);){
            xhtml.startDocument();
            xhtml.startElement("p");
            char[] buffer = new char[1024];
            int n = reader.read(buffer);
            while (n != -1) {
                xhtml.characters(buffer, 0, n);
                n = reader.read(buffer);
            }
            xhtml.endElement("p");
            xhtml.endDocument();
        }
    }

    private void sendInput(Process process, InputStream stream) {
        Thread t = new Thread(() -> {
            OutputStream stdin = process.getOutputStream();
            try {
                IOUtils.copy((InputStream)stream, (OutputStream)stdin);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        });
        t.start();
        try {
            t.join();
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    private void extractMetadata(InputStream stream, Metadata metadata) {
        Thread t = new Thread(() -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    boolean consumed = false;
                    for (Map.Entry<Pattern, String> entry : this.metadataPatterns.entrySet()) {
                        Matcher m = entry.getKey().matcher(line);
                        if (!m.find()) continue;
                        consumed = true;
                        if (entry.getValue() != null && !entry.getValue().equals("")) {
                            metadata.add(entry.getValue(), m.group(1));
                            continue;
                        }
                        metadata.add(m.group(1), m.group(2));
                    }
                    if (consumed) continue;
                    this.ignoredLineConsumer.consume(line);
                }
            }
            catch (IOException iOException) {
            }
            finally {
                IOUtils.closeQuietly((Reader)reader);
                IOUtils.closeQuietly((InputStream)stream);
            }
        });
        t.start();
        try {
            t.join();
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    public static interface LineConsumer
    extends Serializable {
        public static final LineConsumer NULL = line -> {};

        public void consume(String var1);
    }
}

