/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.StreamingOutput
 *  org.apache.commons.io.output.CloseShieldOutputStream
 *  org.codehaus.jackson.JsonEncoding
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.map.SerializationConfig$Feature
 */
package com.atlassian.diagnostics.internal.rest;

import com.atlassian.diagnostics.internal.rest.RuntimeIOException;
import java.io.IOException;
import java.io.OutputStream;
import javax.annotation.Nonnull;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import org.apache.commons.io.output.CloseShieldOutputStream;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public abstract class StreamingJsonOutput
implements StreamingOutput {
    public void write(OutputStream output) throws IOException, WebApplicationException {
        JsonGenerator generator = new ObjectMapper().disable(new SerializationConfig.Feature[]{SerializationConfig.Feature.FLUSH_AFTER_WRITE_VALUE}).getJsonFactory().createJsonGenerator((OutputStream)new CloseShieldOutputStream(output), JsonEncoding.UTF8);
        try {
            this.write(generator);
        }
        catch (RuntimeIOException e) {
            throw e.getCause();
        }
        finally {
            generator.flush();
        }
    }

    protected abstract void write(@Nonnull JsonGenerator var1) throws IOException, WebApplicationException;
}

