/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser.external;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.IOUtils;
import org.apache.tika.io.NullOutputStream;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class ExternalParser
extends AbstractParser {
    private static final long serialVersionUID = -1079128990650687037L;
    public static final String INPUT_FILE_TOKEN = "${INPUT}";
    public static final String OUTPUT_FILE_TOKEN = "${OUTPUT}";
    private Set<MediaType> supportedTypes = Collections.emptySet();
    private Map<Pattern, String> metadataPatterns = null;
    private String[] command = new String[]{"cat"};
    private LineConsumer ignoredLineConsumer = LineConsumer.NULL;

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
            this.parse(TikaInputStream.get(stream, tmp), xhtml, metadata, tmp);
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
            if (cmd[i].indexOf(INPUT_FILE_TOKEN) != -1) {
                cmd[i] = cmd[i].replace(INPUT_FILE_TOKEN, stream.getFile().getPath());
                inputToStdIn = false;
            }
            if (cmd[i].indexOf(OUTPUT_FILE_TOKEN) == -1) continue;
            output = tmp.createTemporaryFile();
            outputFromStdOut = false;
            cmd[i] = cmd[i].replace(OUTPUT_FILE_TOKEN, output.getPath());
        }
        Process process = null;
        try {
            process = cmd.length == 1 ? Runtime.getRuntime().exec(cmd[0]) : Runtime.getRuntime().exec(cmd);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (inputToStdIn) {
                this.sendInput(process, stream);
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
            catch (InterruptedException interruptedException) {}
        }
        if (!outputFromStdOut) {
            this.extractOutput(new FileInputStream(output), xhtml);
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

    private void sendInput(final Process process, final InputStream stream) {
        Thread t = new Thread(){

            @Override
            public void run() {
                OutputStream stdin = process.getOutputStream();
                try {
                    IOUtils.copy(stream, stdin);
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
        };
        t.start();
        try {
            t.join();
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    private static void ignoreStream(InputStream stream) {
        ExternalParser.ignoreStream(stream, true);
    }

    private static Thread ignoreStream(final InputStream stream, boolean waitForDeath) {
        Thread t = new Thread(){

            @Override
            public void run() {
                try {
                    IOUtils.copy(stream, (OutputStream)new NullOutputStream());
                }
                catch (IOException iOException) {
                }
                finally {
                    IOUtils.closeQuietly(stream);
                }
            }
        };
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

    private void extractMetadata(final InputStream stream, final Metadata metadata) {
        Thread t = new Thread(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        boolean consumed = false;
                        for (Pattern p : ExternalParser.this.metadataPatterns.keySet()) {
                            Matcher m = p.matcher(line);
                            if (!m.find()) continue;
                            consumed = true;
                            if (ExternalParser.this.metadataPatterns.get(p) != null && !((String)ExternalParser.this.metadataPatterns.get(p)).equals("")) {
                                metadata.add((String)ExternalParser.this.metadataPatterns.get(p), m.group(1));
                                continue;
                            }
                            metadata.add(m.group(1), m.group(2));
                        }
                        if (consumed) continue;
                        ExternalParser.this.ignoredLineConsumer.consume(line);
                    }
                }
                catch (IOException iOException) {
                }
                finally {
                    IOUtils.closeQuietly(reader);
                    IOUtils.closeQuietly(stream);
                }
            }
        };
        t.start();
        try {
            t.join();
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    public static boolean check(String checkCmd, int ... errorValue) {
        return ExternalParser.check(new String[]{checkCmd}, errorValue);
    }

    public static boolean check(String[] checkCmd, int ... errorValue) {
        if (errorValue.length == 0) {
            errorValue = new int[]{127};
        }
        try {
            Process process = Runtime.getRuntime().exec(checkCmd);
            Thread stdErrSuckerThread = ExternalParser.ignoreStream(process.getErrorStream(), false);
            Thread stdOutSuckerThread = ExternalParser.ignoreStream(process.getInputStream(), false);
            stdErrSuckerThread.join();
            stdOutSuckerThread.join();
            int result = process.waitFor();
            for (int err : errorValue) {
                if (result != err) continue;
                return false;
            }
            return true;
        }
        catch (IOException e) {
            return false;
        }
        catch (InterruptedException ie) {
            return false;
        }
        catch (SecurityException se) {
            return false;
        }
        catch (Error err) {
            if (err.getMessage() != null && (err.getMessage().contains("posix_spawn") || err.getMessage().contains("UNIXProcess"))) {
                return false;
            }
            throw err;
        }
    }

    public static interface LineConsumer
    extends Serializable {
        public static final LineConsumer NULL = new LineConsumer(){

            @Override
            public void consume(String line) {
            }
        };

        public void consume(String var1);
    }
}

