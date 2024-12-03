/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes.pipesiterator.filelist;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;
import org.apache.tika.config.Field;
import org.apache.tika.config.Initializable;
import org.apache.tika.config.InitializableProblemHandler;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaConfigException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.pipes.FetchEmitTuple;
import org.apache.tika.pipes.emitter.EmitKey;
import org.apache.tika.pipes.fetcher.FetchKey;
import org.apache.tika.pipes.pipesiterator.PipesIterator;
import org.apache.tika.utils.StringUtils;

public class FileListPipesIterator
extends PipesIterator
implements Initializable {
    @Field
    private String fileList;
    @Field
    private boolean hasHeader = false;
    private Path fileListPath;

    @Override
    protected void enqueue() throws IOException, TimeoutException, InterruptedException {
        try (BufferedReader reader = Files.newBufferedReader(this.fileListPath, StandardCharsets.UTF_8);){
            if (this.hasHeader) {
                reader.readLine();
            }
            String line = reader.readLine();
            while (line != null) {
                if (!line.startsWith("#") && !StringUtils.isBlank(line)) {
                    FetchKey fetchKey = new FetchKey(this.getFetcherName(), line);
                    EmitKey emitKey = new EmitKey(this.getEmitterName(), line);
                    this.tryToAdd(new FetchEmitTuple(line, fetchKey, emitKey, new Metadata(), this.getHandlerConfig(), this.getOnParseException()));
                }
                line = reader.readLine();
            }
        }
    }

    @Field
    public void setFileList(String path) {
        this.fileList = path;
    }

    @Field
    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }

    @Override
    public void checkInitialization(InitializableProblemHandler problemHandler) throws TikaConfigException {
        TikaConfig.mustNotBeEmpty("fileList", this.fileList);
        TikaConfig.mustNotBeEmpty("fetcherName", this.getFetcherName());
        TikaConfig.mustNotBeEmpty("emitterName", this.getFetcherName());
        this.fileListPath = Paths.get(this.fileList, new String[0]);
        if (!Files.isRegularFile(this.fileListPath, new LinkOption[0])) {
            throw new TikaConfigException("file list " + this.fileList + " does not exist. Must specify an existing file");
        }
    }
}

