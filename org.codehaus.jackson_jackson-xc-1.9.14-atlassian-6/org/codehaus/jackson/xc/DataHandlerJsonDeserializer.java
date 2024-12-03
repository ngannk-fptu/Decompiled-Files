/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.map.DeserializationContext
 *  org.codehaus.jackson.map.deser.std.StdScalarDeserializer
 */
package org.codehaus.jackson.xc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.std.StdScalarDeserializer;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DataHandlerJsonDeserializer
extends StdScalarDeserializer<DataHandler> {
    public DataHandlerJsonDeserializer() {
        super(DataHandler.class);
    }

    public DataHandler deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final byte[] value = jp.getBinaryValue();
        return new DataHandler(new DataSource(){

            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(value);
            }

            public OutputStream getOutputStream() throws IOException {
                throw new IOException();
            }

            public String getContentType() {
                return "application/octet-stream";
            }

            public String getName() {
                return "json-binary-data";
            }
        });
    }
}

