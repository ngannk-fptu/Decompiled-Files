/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.tika.parser.external2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tika.config.Field;
import org.apache.tika.config.Initializable;
import org.apache.tika.config.InitializableProblemHandler;
import org.apache.tika.config.Param;
import org.apache.tika.config.TikaTaskTimeout;
import org.apache.tika.exception.TikaConfigException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.ExternalProcess;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.EmptyParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.XHTMLContentHandler;
import org.apache.tika.utils.FileProcessResult;
import org.apache.tika.utils.ProcessUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class ExternalParser
extends AbstractParser
implements Initializable {
    public static final long DEFAULT_TIMEOUT_MS = 60000L;
    public static final String INPUT_FILE_TOKEN = "${INPUT_FILE}";
    public static final String OUTPUT_FILE_TOKEN = "${OUTPUT_FILE}";
    private static Pattern INPUT_TOKEN_MATCHER = Pattern.compile("\\$\\{INPUT_FILE}");
    private static Pattern OUTPUT_TOKEN_MATCHER = Pattern.compile("\\$\\{OUTPUT_FILE}");
    private static final Logger LOG = LoggerFactory.getLogger(ExternalParser.class);
    private Set<MediaType> supportedTypes = new HashSet<MediaType>();
    private List<String> commandLine = new ArrayList<String>();
    private Parser outputParser = EmptyParser.INSTANCE;
    private boolean returnStdout = false;
    private boolean returnStderr = true;
    private long timeoutMs = 60000L;
    private int maxStdErr = 10000;
    private int maxStdOut = 10000;

    @Override
    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return this.supportedTypes;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
        Path outFile = null;
        try {
            try (TemporaryResources tmp = new TemporaryResources();){
                TikaInputStream tis = TikaInputStream.get(stream, tmp, metadata);
                Path p = tis.getPath();
                ArrayList<String> thisCommandLine = new ArrayList<String>();
                Matcher inputMatcher = INPUT_TOKEN_MATCHER.matcher("");
                Matcher outputMatcher = OUTPUT_TOKEN_MATCHER.matcher("");
                boolean outputFileInCommandline = false;
                for (String c : this.commandLine) {
                    String updated;
                    if (inputMatcher.reset(c).find()) {
                        updated = c.replace(INPUT_FILE_TOKEN, ProcessUtils.escapeCommandLine(p.toAbsolutePath().toString()));
                        thisCommandLine.add(updated);
                        continue;
                    }
                    if (outputMatcher.reset(c).find()) {
                        outFile = Files.createTempFile("tika-external2-", "", new FileAttribute[0]);
                        updated = c.replace(OUTPUT_FILE_TOKEN, ProcessUtils.escapeCommandLine(outFile.toAbsolutePath().toString()));
                        thisCommandLine.add(updated);
                        outputFileInCommandline = true;
                        continue;
                    }
                    thisCommandLine.add(c);
                }
                FileProcessResult result = null;
                long localTimeoutMillis = TikaTaskTimeout.getTimeoutMillis(context, this.timeoutMs);
                if (outputFileInCommandline) {
                    result = ProcessUtils.execute(new ProcessBuilder(thisCommandLine), localTimeoutMillis, this.maxStdOut, this.maxStdErr);
                } else {
                    outFile = Files.createTempFile("tika-external2-", "", new FileAttribute[0]);
                    result = ProcessUtils.execute(new ProcessBuilder(thisCommandLine), localTimeoutMillis, outFile, this.maxStdErr);
                }
                metadata.set(ExternalProcess.IS_TIMEOUT, result.isTimeout());
                metadata.set(ExternalProcess.EXIT_VALUE, result.getExitValue());
                metadata.set(ExternalProcess.STD_OUT_LENGTH, result.getStdoutLength());
                metadata.set(ExternalProcess.STD_OUT_IS_TRUNCATED, result.isStdoutTruncated());
                metadata.set(ExternalProcess.STD_ERR_LENGTH, result.getStderrLength());
                metadata.set(ExternalProcess.STD_ERR_IS_TRUNCATED, result.isStderrTruncated());
                if (this.returnStdout) {
                    metadata.set(ExternalProcess.STD_OUT, result.getStdout());
                }
                if (this.returnStderr) {
                    metadata.set(ExternalProcess.STD_ERR, result.getStderr());
                }
                XHTMLContentHandler xhtml = new XHTMLContentHandler(handler, metadata);
                xhtml.startDocument();
                this.handleOutput(result, outFile, xhtml, metadata, context);
                xhtml.endDocument();
            }
            if (outFile == null) return;
        }
        catch (Throwable throwable) {
            if (outFile == null) throw throwable;
            Files.delete(outFile);
            throw throwable;
        }
        Files.delete(outFile);
    }

    private void handleOutput(FileProcessResult result, Path outFile, XHTMLContentHandler xhtml, Metadata metadata, ParseContext parseContext) throws SAXException, TikaException, IOException {
        if (this.outputParser == EmptyParser.INSTANCE) {
            if (outFile != null) {
                try (BufferedReader reader = Files.newBufferedReader(outFile);){
                    String line = reader.readLine();
                    while (line != null) {
                        xhtml.characters(line);
                        xhtml.newline();
                        line = reader.readLine();
                    }
                }
            } else {
                xhtml.characters(result.getStdout());
            }
        } else {
            if (outFile != null) {
                try (TikaInputStream is = TikaInputStream.get(outFile);){
                    this.outputParser.parse((InputStream)((Object)is), new BodyContentHandler(xhtml), metadata, parseContext);
                }
            }
            try (TikaInputStream is = TikaInputStream.get(result.getStdout().getBytes(StandardCharsets.UTF_8));){
                this.outputParser.parse((InputStream)((Object)is), new BodyContentHandler(xhtml), metadata, parseContext);
            }
        }
    }

    @Field
    public void setSupportedTypes(List<String> supportedTypes) {
        if (this.supportedTypes.size() > 0) {
            throw new IllegalStateException("can't set supportedTypes after initialization");
        }
        for (String s : supportedTypes) {
            this.supportedTypes.add(MediaType.parse(s));
        }
    }

    @Field
    public void setTimeoutMs(long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    @Field
    public void setMaxStdErr(int maxStdErr) {
        this.maxStdErr = maxStdErr;
    }

    @Field
    public void setMaxStdOut(int maxStdOut) {
        this.maxStdOut = maxStdOut;
    }

    @Field
    public void setCommandLine(List<String> commandLine) {
        this.commandLine = commandLine;
    }

    @Field
    public void setReturnStdout(boolean returnStdout) {
        this.returnStdout = returnStdout;
    }

    @Field
    public void setReturnStderr(boolean returnStderr) {
        this.returnStderr = returnStderr;
    }

    @Field
    public void setOutputParser(Parser parser) {
        this.outputParser = parser;
    }

    public Parser getOutputParser() {
        return this.outputParser;
    }

    @Override
    public void initialize(Map<String, Param> params) throws TikaConfigException {
    }

    @Override
    public void checkInitialization(InitializableProblemHandler problemHandler) throws TikaConfigException {
        if (this.supportedTypes.size() == 0) {
            throw new TikaConfigException("supportedTypes size must be > 0");
        }
        if (this.commandLine.isEmpty()) {
            throw new TikaConfigException("commandLine is empty?!");
        }
        if (this.outputParser == EmptyParser.INSTANCE) {
            LOG.debug("no parser selected for the output; contents will be written to the content handler");
        }
    }
}

