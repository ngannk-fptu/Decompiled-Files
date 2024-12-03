/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.codehaus.jackson.xc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import javax.activation.DataHandler;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.codehaus.jackson.node.ObjectNode;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DataHandlerJsonSerializer
extends SerializerBase<DataHandler> {
    public DataHandlerJsonSerializer() {
        super(DataHandler.class);
    }

    @Override
    public void serialize(DataHandler value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        InputStream in = value.getInputStream();
        int len = in.read(buffer);
        while (len > 0) {
            out.write(buffer, 0, len);
            len = in.read(buffer);
        }
        jgen.writeBinary(out.toByteArray());
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
        ObjectNode o = this.createSchemaNode("array", true);
        ObjectNode itemSchema = this.createSchemaNode("string");
        o.put("items", itemSchema);
        return o;
    }
}

