/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.StreamingOutput
 *  lombok.Generated
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.annotations.VisibleForTesting;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import lombok.Generated;

public class Responses {
    private Responses() {
    }

    public static Response okStreamingFile(Path file) {
        return Response.ok((Object)new FileStreamingOutput(file), (String)Responses.getFileType(file)).header("Content-Disposition", (Object)("attachment; filename=" + file.getFileName().toString())).build();
    }

    private static String getFileType(Path file) {
        try {
            return Files.probeContentType(file);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @VisibleForTesting
    public static class FileStreamingOutput
    implements StreamingOutput {
        private final Path file;

        FileStreamingOutput(Path file) {
            this.file = file;
        }

        public void write(OutputStream output) throws IOException, WebApplicationException {
            Files.copy(this.file, output);
        }

        @Generated
        public Path getFile() {
            return this.file;
        }
    }
}

