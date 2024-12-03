/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.JsonProperties;
import org.apache.avro.Schema;
import org.apache.avro.SchemaParseException;

public class Protocol
extends JsonProperties {
    public static final long VERSION = 1L;
    private static final Set<String> MESSAGE_RESERVED = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("doc", "response", "request", "errors", "one-way")));
    private static final Set<String> FIELD_RESERVED = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("name", "type", "doc", "default", "aliases")));
    private String name;
    private String namespace;
    private String doc;
    private Schema.Names types = new Schema.Names();
    private final Map<String, Message> messages = new LinkedHashMap<String, Message>();
    private byte[] md5;
    public static final Schema SYSTEM_ERROR = Schema.create(Schema.Type.STRING);
    public static final Schema SYSTEM_ERRORS = Schema.createUnion(Collections.singletonList(SYSTEM_ERROR));
    private static final Set<String> PROTOCOL_RESERVED = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("namespace", "protocol", "doc", "messages", "types", "errors")));

    private Protocol() {
        super(PROTOCOL_RESERVED);
    }

    public Protocol(Protocol p) {
        this(p.getName(), p.getDoc(), p.getNamespace());
        this.putAll(p);
    }

    public Protocol(String name, String doc, String namespace) {
        super(PROTOCOL_RESERVED);
        this.setName(name, namespace);
        this.doc = doc;
    }

    public Protocol(String name, String namespace) {
        this(name, null, namespace);
    }

    private void setName(String name, String namespace) {
        int lastDot = name.lastIndexOf(46);
        if (lastDot < 0) {
            this.name = name;
            this.namespace = namespace;
        } else {
            this.name = name.substring(lastDot + 1);
            this.namespace = name.substring(0, lastDot);
        }
        if (this.namespace != null && this.namespace.isEmpty()) {
            this.namespace = null;
        }
        this.types.space(this.namespace);
    }

    public String getName() {
        return this.name;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getDoc() {
        return this.doc;
    }

    public Collection<Schema> getTypes() {
        return this.types.values();
    }

    public Schema getType(String name) {
        return this.types.get(name);
    }

    public void setTypes(Collection<Schema> newTypes) {
        this.types = new Schema.Names();
        for (Schema s : newTypes) {
            this.types.add(s);
        }
    }

    public Map<String, Message> getMessages() {
        return this.messages;
    }

    @Deprecated
    public Message createMessage(String name, String doc, Schema request) {
        return new Message(name, doc, Collections.emptyMap(), request);
    }

    public Message createMessage(Message m, Schema request) {
        return new Message(m.name, m.doc, m, request);
    }

    public <T> Message createMessage(String name, String doc, JsonProperties propMap, Schema request) {
        return new Message(name, doc, propMap, request);
    }

    public <T> Message createMessage(String name, String doc, Map<String, ?> propMap, Schema request) {
        return new Message(name, doc, propMap, request);
    }

    @Deprecated
    public Message createMessage(String name, String doc, Schema request, Schema response, Schema errors) {
        return new TwoWayMessage(name, doc, new LinkedHashMap(), request, response, errors);
    }

    public Message createMessage(Message m, Schema request, Schema response, Schema errors) {
        return new TwoWayMessage(m.getName(), m.getDoc(), m, request, response, errors);
    }

    public <T> Message createMessage(String name, String doc, JsonProperties propMap, Schema request, Schema response, Schema errors) {
        return new TwoWayMessage(name, doc, propMap, request, response, errors);
    }

    public <T> Message createMessage(String name, String doc, Map<String, ?> propMap, Schema request, Schema response, Schema errors) {
        return new TwoWayMessage(name, doc, propMap, request, response, errors);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Protocol)) {
            return false;
        }
        Protocol that = (Protocol)o;
        return this.name.equals(that.name) && this.namespace.equals(that.namespace) && this.types.equals(that.types) && this.messages.equals(that.messages) && this.propsEqual(that);
    }

    public int hashCode() {
        return this.name.hashCode() + this.namespace.hashCode() + this.types.hashCode() + this.messages.hashCode() + this.propsHashCode();
    }

    public String toString() {
        return this.toString(false);
    }

    public String toString(boolean pretty) {
        try {
            StringWriter writer = new StringWriter();
            JsonGenerator gen = Schema.FACTORY.createGenerator(writer);
            if (pretty) {
                gen.useDefaultPrettyPrinter();
            }
            this.toJson(gen);
            gen.flush();
            return writer.toString();
        }
        catch (IOException e) {
            throw new AvroRuntimeException(e);
        }
    }

    void toJson(JsonGenerator gen) throws IOException {
        this.types.space(this.namespace);
        gen.writeStartObject();
        gen.writeStringField("protocol", this.name);
        if (this.namespace != null) {
            gen.writeStringField("namespace", this.namespace);
        }
        if (this.doc != null) {
            gen.writeStringField("doc", this.doc);
        }
        this.writeProps(gen);
        gen.writeArrayFieldStart("types");
        Schema.Names resolved = new Schema.Names(this.namespace);
        for (Schema schema : this.types.values()) {
            if (resolved.contains(schema)) continue;
            schema.toJson(resolved, gen);
        }
        gen.writeEndArray();
        gen.writeObjectFieldStart("messages");
        for (Map.Entry entry : this.messages.entrySet()) {
            gen.writeFieldName((String)entry.getKey());
            ((Message)entry.getValue()).toJson(gen);
        }
        gen.writeEndObject();
        gen.writeEndObject();
    }

    public byte[] getMD5() {
        if (this.md5 == null) {
            try {
                this.md5 = MessageDigest.getInstance("MD5").digest(this.toString().getBytes(StandardCharsets.UTF_8));
            }
            catch (Exception e) {
                throw new AvroRuntimeException(e);
            }
        }
        return this.md5;
    }

    public static Protocol parse(File file) throws IOException {
        return Protocol.parse(Schema.FACTORY.createParser(file));
    }

    public static Protocol parse(InputStream stream) throws IOException {
        return Protocol.parse(Schema.FACTORY.createParser(stream));
    }

    public static Protocol parse(String string, String ... more) {
        StringBuilder b = new StringBuilder(string);
        for (String part : more) {
            b.append(part);
        }
        return Protocol.parse(b.toString());
    }

    public static Protocol parse(String string) {
        try {
            return Protocol.parse(Schema.FACTORY.createParser(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8))));
        }
        catch (IOException e) {
            throw new AvroRuntimeException(e);
        }
    }

    private static Protocol parse(JsonParser parser) {
        try {
            Protocol protocol = new Protocol();
            protocol.parse((JsonNode)Schema.MAPPER.readTree(parser));
            return protocol;
        }
        catch (IOException e) {
            throw new SchemaParseException(e);
        }
    }

    private void parse(JsonNode json) {
        this.parseNameAndNamespace(json);
        this.parseTypes(json);
        this.parseMessages(json);
        this.parseDoc(json);
        this.parseProps(json);
    }

    private void parseNameAndNamespace(JsonNode json) {
        JsonNode nameNode = json.get("protocol");
        if (nameNode == null) {
            throw new SchemaParseException("No protocol name specified: " + json);
        }
        JsonNode namespaceNode = json.get("namespace");
        String namespace = namespaceNode == null ? null : namespaceNode.textValue();
        this.setName(nameNode.textValue(), namespace);
    }

    private void parseDoc(JsonNode json) {
        this.doc = this.parseDocNode(json);
    }

    private String parseDocNode(JsonNode json) {
        JsonNode nameNode = json.get("doc");
        if (nameNode == null) {
            return null;
        }
        return nameNode.textValue();
    }

    private void parseTypes(JsonNode json) {
        JsonNode defs = json.get("types");
        if (defs == null) {
            return;
        }
        if (!defs.isArray()) {
            throw new SchemaParseException("Types not an array: " + defs);
        }
        for (JsonNode type : defs) {
            if (!type.isObject()) {
                throw new SchemaParseException("Type not an object: " + type);
            }
            Schema.parse(type, this.types);
        }
    }

    private void parseProps(JsonNode json) {
        Iterator<String> i = json.fieldNames();
        while (i.hasNext()) {
            String p = i.next();
            if (PROTOCOL_RESERVED.contains(p)) continue;
            this.addProp(p, json.get(p));
        }
    }

    private void parseMessages(JsonNode json) {
        JsonNode defs = json.get("messages");
        if (defs == null) {
            return;
        }
        Iterator<String> i = defs.fieldNames();
        while (i.hasNext()) {
            String prop = i.next();
            this.messages.put(prop, this.parseMessage(prop, defs.get(prop)));
        }
    }

    private Message parseMessage(String messageName, JsonNode json) {
        String doc = this.parseDocNode(json);
        LinkedHashMap<String, JsonNode> mProps = new LinkedHashMap<String, JsonNode>();
        Iterator<String> i = json.fieldNames();
        while (i.hasNext()) {
            String p = i.next();
            if (MESSAGE_RESERVED.contains(p)) continue;
            mProps.put(p, json.get(p));
        }
        JsonNode requestNode = json.get("request");
        if (requestNode == null || !requestNode.isArray()) {
            throw new SchemaParseException("No request specified: " + json);
        }
        ArrayList<Schema.Field> fields = new ArrayList<Schema.Field>();
        for (JsonNode field : requestNode) {
            JsonNode fieldNameNode = field.get("name");
            if (fieldNameNode == null) {
                throw new SchemaParseException("No param name: " + field);
            }
            JsonNode fieldTypeNode = field.get("type");
            if (fieldTypeNode == null) {
                throw new SchemaParseException("No param type: " + field);
            }
            String name = fieldNameNode.textValue();
            String fieldDoc = null;
            JsonNode fieldDocNode = field.get("doc");
            if (fieldDocNode != null) {
                fieldDoc = fieldDocNode.textValue();
            }
            Schema.Field newField = new Schema.Field(name, Schema.parse(fieldTypeNode, this.types), fieldDoc, field.get("default"), true, Schema.Field.Order.ASCENDING);
            Set<String> aliases = Schema.parseAliases(field);
            if (aliases != null) {
                for (String alias : aliases) {
                    newField.addAlias(alias);
                }
            }
            Iterator<String> i2 = field.fieldNames();
            while (i2.hasNext()) {
                String prop = i2.next();
                if (FIELD_RESERVED.contains(prop)) continue;
                newField.addProp(prop, field.get(prop));
            }
            fields.add(newField);
        }
        Schema request = Schema.createRecord(fields);
        boolean oneWay = false;
        JsonNode oneWayNode = json.get("one-way");
        if (oneWayNode != null) {
            if (!oneWayNode.isBoolean()) {
                throw new SchemaParseException("one-way must be boolean: " + json);
            }
            oneWay = oneWayNode.booleanValue();
        }
        JsonNode responseNode = json.get("response");
        if (!oneWay && responseNode == null) {
            throw new SchemaParseException("No response specified: " + json);
        }
        JsonNode decls = json.get("errors");
        if (oneWay) {
            if (decls != null) {
                throw new SchemaParseException("one-way can't have errors: " + json);
            }
            if (responseNode != null && Schema.parse(responseNode, this.types).getType() != Schema.Type.NULL) {
                throw new SchemaParseException("One way response must be null: " + json);
            }
            return new Message(messageName, doc, mProps, request);
        }
        Schema response = Schema.parse(responseNode, this.types);
        ArrayList<Schema> errs = new ArrayList<Schema>();
        errs.add(SYSTEM_ERROR);
        if (decls != null) {
            if (!decls.isArray()) {
                throw new SchemaParseException("Errors not an array: " + json);
            }
            for (JsonNode decl : decls) {
                String name = decl.textValue();
                Schema schema = this.types.get(name);
                if (schema == null) {
                    throw new SchemaParseException("Undefined error: " + name);
                }
                if (!schema.isError()) {
                    throw new SchemaParseException("Not an error: " + name);
                }
                errs.add(schema);
            }
        }
        return new TwoWayMessage(messageName, doc, mProps, request, response, Schema.createUnion(errs));
    }

    public static void main(String[] args) throws Exception {
        System.out.println(Protocol.parse(new File(args[0])));
    }

    private class TwoWayMessage
    extends Message {
        private Schema response;
        private Schema errors;

        private TwoWayMessage(String name, String doc, Map<String, ?> propMap, Schema request, Schema response, Schema errors) {
            super(name, doc, propMap, request);
            this.response = response;
            this.errors = errors;
        }

        private TwoWayMessage(String name, String doc, JsonProperties propMap, Schema request, Schema response, Schema errors) {
            super(name, doc, propMap, request);
            this.response = response;
            this.errors = errors;
        }

        @Override
        public Schema getResponse() {
            return this.response;
        }

        @Override
        public Schema getErrors() {
            return this.errors;
        }

        @Override
        public boolean isOneWay() {
            return false;
        }

        @Override
        public boolean equals(Object o) {
            if (!super.equals(o)) {
                return false;
            }
            if (!(o instanceof TwoWayMessage)) {
                return false;
            }
            TwoWayMessage that = (TwoWayMessage)o;
            return this.response.equals(that.response) && this.errors.equals(that.errors);
        }

        @Override
        public int hashCode() {
            return super.hashCode() + this.response.hashCode() + this.errors.hashCode();
        }

        @Override
        void toJson1(JsonGenerator gen) throws IOException {
            gen.writeFieldName("response");
            this.response.toJson(Protocol.this.types, gen);
            List<Schema> errs = this.errors.getTypes();
            if (errs.size() > 1) {
                Schema union = Schema.createUnion(errs.subList(1, errs.size()));
                gen.writeFieldName("errors");
                union.toJson(Protocol.this.types, gen);
            }
        }
    }

    public class Message
    extends JsonProperties {
        private String name;
        private String doc;
        private Schema request;

        private Message(String name, String doc, JsonProperties propMap, Schema request) {
            super(MESSAGE_RESERVED);
            this.name = name;
            this.doc = doc;
            this.request = request;
            if (propMap != null) {
                this.addAllProps(propMap);
            }
        }

        private Message(String name, String doc, Map<String, ?> propMap, Schema request) {
            super(MESSAGE_RESERVED, propMap);
            this.name = name;
            this.doc = doc;
            this.request = request;
        }

        public String getName() {
            return this.name;
        }

        public Schema getRequest() {
            return this.request;
        }

        public Schema getResponse() {
            return Schema.create(Schema.Type.NULL);
        }

        public Schema getErrors() {
            return Schema.createUnion(Collections.emptyList());
        }

        public boolean isOneWay() {
            return true;
        }

        public String toString() {
            try {
                StringWriter writer = new StringWriter();
                JsonGenerator gen = Schema.FACTORY.createGenerator(writer);
                this.toJson(gen);
                gen.flush();
                return writer.toString();
            }
            catch (IOException e) {
                throw new AvroRuntimeException(e);
            }
        }

        void toJson(JsonGenerator gen) throws IOException {
            gen.writeStartObject();
            if (this.doc != null) {
                gen.writeStringField("doc", this.doc);
            }
            this.writeProps(gen);
            gen.writeFieldName("request");
            this.request.fieldsToJson(Protocol.this.types, gen);
            this.toJson1(gen);
            gen.writeEndObject();
        }

        void toJson1(JsonGenerator gen) throws IOException {
            gen.writeStringField("response", "null");
            gen.writeBooleanField("one-way", true);
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Message)) {
                return false;
            }
            Message that = (Message)o;
            return this.name.equals(that.name) && this.request.equals(that.request) && this.propsEqual(that);
        }

        public int hashCode() {
            return this.name.hashCode() + this.request.hashCode() + this.propsHashCode();
        }

        public String getDoc() {
            return this.doc;
        }
    }
}

