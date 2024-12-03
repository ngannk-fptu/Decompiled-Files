/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import java.io.IOException;

public class StackTraceElementDeserializer
extends StdScalarDeserializer<StackTraceElement> {
    private static final long serialVersionUID = 1L;
    protected final JsonDeserializer<?> _adapterDeserializer;

    @Deprecated
    public StackTraceElementDeserializer() {
        this((JsonDeserializer<?>)null);
    }

    protected StackTraceElementDeserializer(JsonDeserializer<?> ad) {
        super(StackTraceElement.class);
        this._adapterDeserializer = ad;
    }

    public static JsonDeserializer<?> construct(DeserializationContext ctxt) throws JsonMappingException {
        if (ctxt == null) {
            return new StackTraceElementDeserializer();
        }
        JsonDeserializer<Object> adapterDeser = ctxt.findNonContextualValueDeserializer(ctxt.constructType(Adapter.class));
        return new StackTraceElementDeserializer(adapterDeser);
    }

    @Override
    public StackTraceElement deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.currentToken();
        if (t == JsonToken.START_OBJECT || t == JsonToken.FIELD_NAME) {
            Adapter adapted = this._adapterDeserializer == null ? ctxt.readValue(p, Adapter.class) : (Adapter)this._adapterDeserializer.deserialize(p, ctxt);
            return this.constructValue(ctxt, adapted);
        }
        if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            p.nextToken();
            StackTraceElement value = this.deserialize(p, ctxt);
            if (p.nextToken() != JsonToken.END_ARRAY) {
                this.handleMissingEndArrayForSingle(p, ctxt);
            }
            return value;
        }
        return (StackTraceElement)ctxt.handleUnexpectedToken(this._valueClass, p);
    }

    protected StackTraceElement constructValue(DeserializationContext ctxt, Adapter adapted) {
        return this.constructValue(ctxt, adapted.className, adapted.methodName, adapted.fileName, adapted.lineNumber, adapted.moduleName, adapted.moduleVersion, adapted.classLoaderName);
    }

    @Deprecated
    protected StackTraceElement constructValue(DeserializationContext ctxt, String className, String methodName, String fileName, int lineNumber, String moduleName, String moduleVersion) {
        return this.constructValue(ctxt, className, methodName, fileName, lineNumber, moduleName, moduleVersion, null);
    }

    protected StackTraceElement constructValue(DeserializationContext ctxt, String className, String methodName, String fileName, int lineNumber, String moduleName, String moduleVersion, String classLoaderName) {
        return new StackTraceElement(className, methodName, fileName, lineNumber);
    }

    public static final class Adapter {
        public String className = "";
        public String classLoaderName;
        public String declaringClass;
        public String format;
        public String fileName = "";
        public String methodName = "";
        public int lineNumber = -1;
        public String moduleName;
        public String moduleVersion;
        public boolean nativeMethod;
    }
}

