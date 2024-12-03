/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.util.Map;
import org.apache.avro.AvroMissingFieldException;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.data.RecordBuilder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.SchemaStore;
import org.apache.avro.specific.AvroGenerated;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.avro.specific.SpecificRecordBuilderBase;

@AvroGenerated
public class EventMessage
extends SpecificRecordBase
implements SpecificRecord {
    private static final long serialVersionUID = 2904540686475323276L;
    public static final Schema SCHEMA$ = new Schema.Parser().parse("{\"type\":\"record\",\"name\":\"EventMessage\",\"namespace\":\"com.atlassian.analytics\",\"doc\":\"An analytics event.\",\"fields\":[{\"name\":\"server\",\"type\":\"string\",\"default\":\"null\",\"order\":\"ignore\"},{\"name\":\"clientTime\",\"type\":\"long\",\"default\":0,\"alias\":[\"serverTime\"]},{\"name\":\"sen\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"product\",\"type\":\"string\",\"default\":\"null\"},{\"name\":\"subProduct\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"version\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"name\",\"type\":\"string\",\"default\":\"null\"},{\"name\":\"user\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"session\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"sourceIP\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"receivedTime\",\"type\":\"long\",\"default\":0},{\"name\":\"atlPath\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"appAccess\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"requestCorrelationId\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"properties\",\"type\":[{\"type\":\"map\",\"values\":[\"string\",\"int\",\"long\",\"float\",\"double\",\"boolean\",\"null\"]},\"null\"],\"default\":{}}],\"version\":\"1.6\"}");
    private static final SpecificData MODEL$ = new SpecificData();
    private static final BinaryMessageEncoder<EventMessage> ENCODER = new BinaryMessageEncoder(MODEL$, SCHEMA$);
    private static final BinaryMessageDecoder<EventMessage> DECODER = new BinaryMessageDecoder(MODEL$, SCHEMA$);
    private CharSequence server;
    private long clientTime;
    private CharSequence sen;
    private CharSequence product;
    private CharSequence subProduct;
    private CharSequence version;
    private CharSequence name;
    private CharSequence user;
    private CharSequence session;
    private CharSequence sourceIP;
    private long receivedTime;
    private CharSequence atlPath;
    private CharSequence appAccess;
    private CharSequence requestCorrelationId;
    private Map<CharSequence, Object> properties;
    private static final DatumWriter<EventMessage> WRITER$ = MODEL$.createDatumWriter(SCHEMA$);
    private static final DatumReader<EventMessage> READER$ = MODEL$.createDatumReader(SCHEMA$);

    public static Schema getClassSchema() {
        return SCHEMA$;
    }

    public static BinaryMessageEncoder<EventMessage> getEncoder() {
        return ENCODER;
    }

    public static BinaryMessageDecoder<EventMessage> getDecoder() {
        return DECODER;
    }

    public static BinaryMessageDecoder<EventMessage> createDecoder(SchemaStore resolver) {
        return new BinaryMessageDecoder<EventMessage>(MODEL$, SCHEMA$, resolver);
    }

    public ByteBuffer toByteBuffer() throws IOException {
        return ENCODER.encode(this);
    }

    public static EventMessage fromByteBuffer(ByteBuffer b) throws IOException {
        return (EventMessage)DECODER.decode(b);
    }

    public EventMessage() {
    }

    public EventMessage(CharSequence server, Long clientTime, CharSequence sen, CharSequence product, CharSequence subProduct, CharSequence version, CharSequence name, CharSequence user, CharSequence session, CharSequence sourceIP, Long receivedTime, CharSequence atlPath, CharSequence appAccess, CharSequence requestCorrelationId, Map<CharSequence, Object> properties) {
        this.server = server;
        this.clientTime = clientTime;
        this.sen = sen;
        this.product = product;
        this.subProduct = subProduct;
        this.version = version;
        this.name = name;
        this.user = user;
        this.session = session;
        this.sourceIP = sourceIP;
        this.receivedTime = receivedTime;
        this.atlPath = atlPath;
        this.appAccess = appAccess;
        this.requestCorrelationId = requestCorrelationId;
        this.properties = properties;
    }

    @Override
    public SpecificData getSpecificData() {
        return MODEL$;
    }

    @Override
    public Schema getSchema() {
        return SCHEMA$;
    }

    @Override
    public Object get(int field$) {
        switch (field$) {
            case 0: {
                return this.server;
            }
            case 1: {
                return this.clientTime;
            }
            case 2: {
                return this.sen;
            }
            case 3: {
                return this.product;
            }
            case 4: {
                return this.subProduct;
            }
            case 5: {
                return this.version;
            }
            case 6: {
                return this.name;
            }
            case 7: {
                return this.user;
            }
            case 8: {
                return this.session;
            }
            case 9: {
                return this.sourceIP;
            }
            case 10: {
                return this.receivedTime;
            }
            case 11: {
                return this.atlPath;
            }
            case 12: {
                return this.appAccess;
            }
            case 13: {
                return this.requestCorrelationId;
            }
            case 14: {
                return this.properties;
            }
        }
        throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }

    @Override
    public void put(int field$, Object value$) {
        switch (field$) {
            case 0: {
                this.server = (CharSequence)value$;
                break;
            }
            case 1: {
                this.clientTime = (Long)value$;
                break;
            }
            case 2: {
                this.sen = (CharSequence)value$;
                break;
            }
            case 3: {
                this.product = (CharSequence)value$;
                break;
            }
            case 4: {
                this.subProduct = (CharSequence)value$;
                break;
            }
            case 5: {
                this.version = (CharSequence)value$;
                break;
            }
            case 6: {
                this.name = (CharSequence)value$;
                break;
            }
            case 7: {
                this.user = (CharSequence)value$;
                break;
            }
            case 8: {
                this.session = (CharSequence)value$;
                break;
            }
            case 9: {
                this.sourceIP = (CharSequence)value$;
                break;
            }
            case 10: {
                this.receivedTime = (Long)value$;
                break;
            }
            case 11: {
                this.atlPath = (CharSequence)value$;
                break;
            }
            case 12: {
                this.appAccess = (CharSequence)value$;
                break;
            }
            case 13: {
                this.requestCorrelationId = (CharSequence)value$;
                break;
            }
            case 14: {
                this.properties = (Map)value$;
                break;
            }
            default: {
                throw new IndexOutOfBoundsException("Invalid index: " + field$);
            }
        }
    }

    public CharSequence getServer() {
        return this.server;
    }

    public void setServer(CharSequence value) {
        this.server = value;
    }

    public long getClientTime() {
        return this.clientTime;
    }

    public void setClientTime(long value) {
        this.clientTime = value;
    }

    public CharSequence getSen() {
        return this.sen;
    }

    public void setSen(CharSequence value) {
        this.sen = value;
    }

    public CharSequence getProduct() {
        return this.product;
    }

    public void setProduct(CharSequence value) {
        this.product = value;
    }

    public CharSequence getSubProduct() {
        return this.subProduct;
    }

    public void setSubProduct(CharSequence value) {
        this.subProduct = value;
    }

    public CharSequence getVersion() {
        return this.version;
    }

    public void setVersion(CharSequence value) {
        this.version = value;
    }

    public CharSequence getName() {
        return this.name;
    }

    public void setName(CharSequence value) {
        this.name = value;
    }

    public CharSequence getUser() {
        return this.user;
    }

    public void setUser(CharSequence value) {
        this.user = value;
    }

    public CharSequence getSession() {
        return this.session;
    }

    public void setSession(CharSequence value) {
        this.session = value;
    }

    public CharSequence getSourceIP() {
        return this.sourceIP;
    }

    public void setSourceIP(CharSequence value) {
        this.sourceIP = value;
    }

    public long getReceivedTime() {
        return this.receivedTime;
    }

    public void setReceivedTime(long value) {
        this.receivedTime = value;
    }

    public CharSequence getAtlPath() {
        return this.atlPath;
    }

    public void setAtlPath(CharSequence value) {
        this.atlPath = value;
    }

    public CharSequence getAppAccess() {
        return this.appAccess;
    }

    public void setAppAccess(CharSequence value) {
        this.appAccess = value;
    }

    public CharSequence getRequestCorrelationId() {
        return this.requestCorrelationId;
    }

    public void setRequestCorrelationId(CharSequence value) {
        this.requestCorrelationId = value;
    }

    public Map<CharSequence, Object> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<CharSequence, Object> value) {
        this.properties = value;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(Builder other) {
        if (other == null) {
            return new Builder();
        }
        return new Builder(other);
    }

    public static Builder newBuilder(EventMessage other) {
        if (other == null) {
            return new Builder();
        }
        return new Builder(other);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        WRITER$.write(this, SpecificData.getEncoder(out));
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        READER$.read(this, SpecificData.getDecoder(in));
    }

    @AvroGenerated
    public static class Builder
    extends SpecificRecordBuilderBase<EventMessage>
    implements RecordBuilder<EventMessage> {
        private CharSequence server;
        private long clientTime;
        private CharSequence sen;
        private CharSequence product;
        private CharSequence subProduct;
        private CharSequence version;
        private CharSequence name;
        private CharSequence user;
        private CharSequence session;
        private CharSequence sourceIP;
        private long receivedTime;
        private CharSequence atlPath;
        private CharSequence appAccess;
        private CharSequence requestCorrelationId;
        private Map<CharSequence, Object> properties;

        private Builder() {
            super(SCHEMA$, MODEL$);
        }

        private Builder(Builder other) {
            super(other);
            if (Builder.isValidValue(this.fields()[0], other.server)) {
                this.server = this.data().deepCopy(this.fields()[0].schema(), other.server);
                this.fieldSetFlags()[0] = other.fieldSetFlags()[0];
            }
            if (Builder.isValidValue(this.fields()[1], other.clientTime)) {
                this.clientTime = this.data().deepCopy(this.fields()[1].schema(), other.clientTime);
                this.fieldSetFlags()[1] = other.fieldSetFlags()[1];
            }
            if (Builder.isValidValue(this.fields()[2], other.sen)) {
                this.sen = this.data().deepCopy(this.fields()[2].schema(), other.sen);
                this.fieldSetFlags()[2] = other.fieldSetFlags()[2];
            }
            if (Builder.isValidValue(this.fields()[3], other.product)) {
                this.product = this.data().deepCopy(this.fields()[3].schema(), other.product);
                this.fieldSetFlags()[3] = other.fieldSetFlags()[3];
            }
            if (Builder.isValidValue(this.fields()[4], other.subProduct)) {
                this.subProduct = this.data().deepCopy(this.fields()[4].schema(), other.subProduct);
                this.fieldSetFlags()[4] = other.fieldSetFlags()[4];
            }
            if (Builder.isValidValue(this.fields()[5], other.version)) {
                this.version = this.data().deepCopy(this.fields()[5].schema(), other.version);
                this.fieldSetFlags()[5] = other.fieldSetFlags()[5];
            }
            if (Builder.isValidValue(this.fields()[6], other.name)) {
                this.name = this.data().deepCopy(this.fields()[6].schema(), other.name);
                this.fieldSetFlags()[6] = other.fieldSetFlags()[6];
            }
            if (Builder.isValidValue(this.fields()[7], other.user)) {
                this.user = this.data().deepCopy(this.fields()[7].schema(), other.user);
                this.fieldSetFlags()[7] = other.fieldSetFlags()[7];
            }
            if (Builder.isValidValue(this.fields()[8], other.session)) {
                this.session = this.data().deepCopy(this.fields()[8].schema(), other.session);
                this.fieldSetFlags()[8] = other.fieldSetFlags()[8];
            }
            if (Builder.isValidValue(this.fields()[9], other.sourceIP)) {
                this.sourceIP = this.data().deepCopy(this.fields()[9].schema(), other.sourceIP);
                this.fieldSetFlags()[9] = other.fieldSetFlags()[9];
            }
            if (Builder.isValidValue(this.fields()[10], other.receivedTime)) {
                this.receivedTime = this.data().deepCopy(this.fields()[10].schema(), other.receivedTime);
                this.fieldSetFlags()[10] = other.fieldSetFlags()[10];
            }
            if (Builder.isValidValue(this.fields()[11], other.atlPath)) {
                this.atlPath = this.data().deepCopy(this.fields()[11].schema(), other.atlPath);
                this.fieldSetFlags()[11] = other.fieldSetFlags()[11];
            }
            if (Builder.isValidValue(this.fields()[12], other.appAccess)) {
                this.appAccess = this.data().deepCopy(this.fields()[12].schema(), other.appAccess);
                this.fieldSetFlags()[12] = other.fieldSetFlags()[12];
            }
            if (Builder.isValidValue(this.fields()[13], other.requestCorrelationId)) {
                this.requestCorrelationId = this.data().deepCopy(this.fields()[13].schema(), other.requestCorrelationId);
                this.fieldSetFlags()[13] = other.fieldSetFlags()[13];
            }
            if (Builder.isValidValue(this.fields()[14], other.properties)) {
                this.properties = this.data().deepCopy(this.fields()[14].schema(), other.properties);
                this.fieldSetFlags()[14] = other.fieldSetFlags()[14];
            }
        }

        private Builder(EventMessage other) {
            super(SCHEMA$, MODEL$);
            if (Builder.isValidValue(this.fields()[0], other.server)) {
                this.server = this.data().deepCopy(this.fields()[0].schema(), other.server);
                this.fieldSetFlags()[0] = true;
            }
            if (Builder.isValidValue(this.fields()[1], other.clientTime)) {
                this.clientTime = this.data().deepCopy(this.fields()[1].schema(), other.clientTime);
                this.fieldSetFlags()[1] = true;
            }
            if (Builder.isValidValue(this.fields()[2], other.sen)) {
                this.sen = this.data().deepCopy(this.fields()[2].schema(), other.sen);
                this.fieldSetFlags()[2] = true;
            }
            if (Builder.isValidValue(this.fields()[3], other.product)) {
                this.product = this.data().deepCopy(this.fields()[3].schema(), other.product);
                this.fieldSetFlags()[3] = true;
            }
            if (Builder.isValidValue(this.fields()[4], other.subProduct)) {
                this.subProduct = this.data().deepCopy(this.fields()[4].schema(), other.subProduct);
                this.fieldSetFlags()[4] = true;
            }
            if (Builder.isValidValue(this.fields()[5], other.version)) {
                this.version = this.data().deepCopy(this.fields()[5].schema(), other.version);
                this.fieldSetFlags()[5] = true;
            }
            if (Builder.isValidValue(this.fields()[6], other.name)) {
                this.name = this.data().deepCopy(this.fields()[6].schema(), other.name);
                this.fieldSetFlags()[6] = true;
            }
            if (Builder.isValidValue(this.fields()[7], other.user)) {
                this.user = this.data().deepCopy(this.fields()[7].schema(), other.user);
                this.fieldSetFlags()[7] = true;
            }
            if (Builder.isValidValue(this.fields()[8], other.session)) {
                this.session = this.data().deepCopy(this.fields()[8].schema(), other.session);
                this.fieldSetFlags()[8] = true;
            }
            if (Builder.isValidValue(this.fields()[9], other.sourceIP)) {
                this.sourceIP = this.data().deepCopy(this.fields()[9].schema(), other.sourceIP);
                this.fieldSetFlags()[9] = true;
            }
            if (Builder.isValidValue(this.fields()[10], other.receivedTime)) {
                this.receivedTime = this.data().deepCopy(this.fields()[10].schema(), other.receivedTime);
                this.fieldSetFlags()[10] = true;
            }
            if (Builder.isValidValue(this.fields()[11], other.atlPath)) {
                this.atlPath = this.data().deepCopy(this.fields()[11].schema(), other.atlPath);
                this.fieldSetFlags()[11] = true;
            }
            if (Builder.isValidValue(this.fields()[12], other.appAccess)) {
                this.appAccess = this.data().deepCopy(this.fields()[12].schema(), other.appAccess);
                this.fieldSetFlags()[12] = true;
            }
            if (Builder.isValidValue(this.fields()[13], other.requestCorrelationId)) {
                this.requestCorrelationId = this.data().deepCopy(this.fields()[13].schema(), other.requestCorrelationId);
                this.fieldSetFlags()[13] = true;
            }
            if (Builder.isValidValue(this.fields()[14], other.properties)) {
                this.properties = this.data().deepCopy(this.fields()[14].schema(), other.properties);
                this.fieldSetFlags()[14] = true;
            }
        }

        public CharSequence getServer() {
            return this.server;
        }

        public Builder setServer(CharSequence value) {
            this.validate(this.fields()[0], value);
            this.server = value;
            this.fieldSetFlags()[0] = true;
            return this;
        }

        public boolean hasServer() {
            return this.fieldSetFlags()[0];
        }

        public Builder clearServer() {
            this.server = null;
            this.fieldSetFlags()[0] = false;
            return this;
        }

        public long getClientTime() {
            return this.clientTime;
        }

        public Builder setClientTime(long value) {
            this.validate(this.fields()[1], value);
            this.clientTime = value;
            this.fieldSetFlags()[1] = true;
            return this;
        }

        public boolean hasClientTime() {
            return this.fieldSetFlags()[1];
        }

        public Builder clearClientTime() {
            this.fieldSetFlags()[1] = false;
            return this;
        }

        public CharSequence getSen() {
            return this.sen;
        }

        public Builder setSen(CharSequence value) {
            this.validate(this.fields()[2], value);
            this.sen = value;
            this.fieldSetFlags()[2] = true;
            return this;
        }

        public boolean hasSen() {
            return this.fieldSetFlags()[2];
        }

        public Builder clearSen() {
            this.sen = null;
            this.fieldSetFlags()[2] = false;
            return this;
        }

        public CharSequence getProduct() {
            return this.product;
        }

        public Builder setProduct(CharSequence value) {
            this.validate(this.fields()[3], value);
            this.product = value;
            this.fieldSetFlags()[3] = true;
            return this;
        }

        public boolean hasProduct() {
            return this.fieldSetFlags()[3];
        }

        public Builder clearProduct() {
            this.product = null;
            this.fieldSetFlags()[3] = false;
            return this;
        }

        public CharSequence getSubProduct() {
            return this.subProduct;
        }

        public Builder setSubProduct(CharSequence value) {
            this.validate(this.fields()[4], value);
            this.subProduct = value;
            this.fieldSetFlags()[4] = true;
            return this;
        }

        public boolean hasSubProduct() {
            return this.fieldSetFlags()[4];
        }

        public Builder clearSubProduct() {
            this.subProduct = null;
            this.fieldSetFlags()[4] = false;
            return this;
        }

        public CharSequence getVersion() {
            return this.version;
        }

        public Builder setVersion(CharSequence value) {
            this.validate(this.fields()[5], value);
            this.version = value;
            this.fieldSetFlags()[5] = true;
            return this;
        }

        public boolean hasVersion() {
            return this.fieldSetFlags()[5];
        }

        public Builder clearVersion() {
            this.version = null;
            this.fieldSetFlags()[5] = false;
            return this;
        }

        public CharSequence getName() {
            return this.name;
        }

        public Builder setName(CharSequence value) {
            this.validate(this.fields()[6], value);
            this.name = value;
            this.fieldSetFlags()[6] = true;
            return this;
        }

        public boolean hasName() {
            return this.fieldSetFlags()[6];
        }

        public Builder clearName() {
            this.name = null;
            this.fieldSetFlags()[6] = false;
            return this;
        }

        public CharSequence getUser() {
            return this.user;
        }

        public Builder setUser(CharSequence value) {
            this.validate(this.fields()[7], value);
            this.user = value;
            this.fieldSetFlags()[7] = true;
            return this;
        }

        public boolean hasUser() {
            return this.fieldSetFlags()[7];
        }

        public Builder clearUser() {
            this.user = null;
            this.fieldSetFlags()[7] = false;
            return this;
        }

        public CharSequence getSession() {
            return this.session;
        }

        public Builder setSession(CharSequence value) {
            this.validate(this.fields()[8], value);
            this.session = value;
            this.fieldSetFlags()[8] = true;
            return this;
        }

        public boolean hasSession() {
            return this.fieldSetFlags()[8];
        }

        public Builder clearSession() {
            this.session = null;
            this.fieldSetFlags()[8] = false;
            return this;
        }

        public CharSequence getSourceIP() {
            return this.sourceIP;
        }

        public Builder setSourceIP(CharSequence value) {
            this.validate(this.fields()[9], value);
            this.sourceIP = value;
            this.fieldSetFlags()[9] = true;
            return this;
        }

        public boolean hasSourceIP() {
            return this.fieldSetFlags()[9];
        }

        public Builder clearSourceIP() {
            this.sourceIP = null;
            this.fieldSetFlags()[9] = false;
            return this;
        }

        public long getReceivedTime() {
            return this.receivedTime;
        }

        public Builder setReceivedTime(long value) {
            this.validate(this.fields()[10], value);
            this.receivedTime = value;
            this.fieldSetFlags()[10] = true;
            return this;
        }

        public boolean hasReceivedTime() {
            return this.fieldSetFlags()[10];
        }

        public Builder clearReceivedTime() {
            this.fieldSetFlags()[10] = false;
            return this;
        }

        public CharSequence getAtlPath() {
            return this.atlPath;
        }

        public Builder setAtlPath(CharSequence value) {
            this.validate(this.fields()[11], value);
            this.atlPath = value;
            this.fieldSetFlags()[11] = true;
            return this;
        }

        public boolean hasAtlPath() {
            return this.fieldSetFlags()[11];
        }

        public Builder clearAtlPath() {
            this.atlPath = null;
            this.fieldSetFlags()[11] = false;
            return this;
        }

        public CharSequence getAppAccess() {
            return this.appAccess;
        }

        public Builder setAppAccess(CharSequence value) {
            this.validate(this.fields()[12], value);
            this.appAccess = value;
            this.fieldSetFlags()[12] = true;
            return this;
        }

        public boolean hasAppAccess() {
            return this.fieldSetFlags()[12];
        }

        public Builder clearAppAccess() {
            this.appAccess = null;
            this.fieldSetFlags()[12] = false;
            return this;
        }

        public CharSequence getRequestCorrelationId() {
            return this.requestCorrelationId;
        }

        public Builder setRequestCorrelationId(CharSequence value) {
            this.validate(this.fields()[13], value);
            this.requestCorrelationId = value;
            this.fieldSetFlags()[13] = true;
            return this;
        }

        public boolean hasRequestCorrelationId() {
            return this.fieldSetFlags()[13];
        }

        public Builder clearRequestCorrelationId() {
            this.requestCorrelationId = null;
            this.fieldSetFlags()[13] = false;
            return this;
        }

        public Map<CharSequence, Object> getProperties() {
            return this.properties;
        }

        public Builder setProperties(Map<CharSequence, Object> value) {
            this.validate(this.fields()[14], value);
            this.properties = value;
            this.fieldSetFlags()[14] = true;
            return this;
        }

        public boolean hasProperties() {
            return this.fieldSetFlags()[14];
        }

        public Builder clearProperties() {
            this.properties = null;
            this.fieldSetFlags()[14] = false;
            return this;
        }

        @Override
        public EventMessage build() {
            try {
                EventMessage record = new EventMessage();
                record.server = this.fieldSetFlags()[0] ? this.server : (CharSequence)this.defaultValue(this.fields()[0]);
                record.clientTime = this.fieldSetFlags()[1] ? this.clientTime : (Long)this.defaultValue(this.fields()[1]);
                record.sen = this.fieldSetFlags()[2] ? this.sen : (CharSequence)this.defaultValue(this.fields()[2]);
                record.product = this.fieldSetFlags()[3] ? this.product : (CharSequence)this.defaultValue(this.fields()[3]);
                record.subProduct = this.fieldSetFlags()[4] ? this.subProduct : (CharSequence)this.defaultValue(this.fields()[4]);
                record.version = this.fieldSetFlags()[5] ? this.version : (CharSequence)this.defaultValue(this.fields()[5]);
                record.name = this.fieldSetFlags()[6] ? this.name : (CharSequence)this.defaultValue(this.fields()[6]);
                record.user = this.fieldSetFlags()[7] ? this.user : (CharSequence)this.defaultValue(this.fields()[7]);
                record.session = this.fieldSetFlags()[8] ? this.session : (CharSequence)this.defaultValue(this.fields()[8]);
                record.sourceIP = this.fieldSetFlags()[9] ? this.sourceIP : (CharSequence)this.defaultValue(this.fields()[9]);
                record.receivedTime = this.fieldSetFlags()[10] ? this.receivedTime : (Long)this.defaultValue(this.fields()[10]);
                record.atlPath = this.fieldSetFlags()[11] ? this.atlPath : (CharSequence)this.defaultValue(this.fields()[11]);
                record.appAccess = this.fieldSetFlags()[12] ? this.appAccess : (CharSequence)this.defaultValue(this.fields()[12]);
                record.requestCorrelationId = this.fieldSetFlags()[13] ? this.requestCorrelationId : (CharSequence)this.defaultValue(this.fields()[13]);
                record.properties = this.fieldSetFlags()[14] ? this.properties : (Map)this.defaultValue(this.fields()[14]);
                return record;
            }
            catch (AvroMissingFieldException e) {
                throw e;
            }
            catch (Exception e) {
                throw new AvroRuntimeException(e);
            }
        }
    }
}

