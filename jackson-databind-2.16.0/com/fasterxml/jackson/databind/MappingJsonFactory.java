/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonFactory
 *  com.fasterxml.jackson.core.ObjectCodec
 *  com.fasterxml.jackson.core.format.InputAccessor
 *  com.fasterxml.jackson.core.format.MatchStrength
 */
package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.format.InputAccessor;
import com.fasterxml.jackson.core.format.MatchStrength;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class MappingJsonFactory
extends JsonFactory {
    private static final long serialVersionUID = -1L;

    public MappingJsonFactory() {
        this(null);
    }

    public MappingJsonFactory(ObjectMapper mapper) {
        super((ObjectCodec)mapper);
        if (mapper == null) {
            this.setCodec(new ObjectMapper(this));
        }
    }

    public MappingJsonFactory(JsonFactory src, ObjectMapper mapper) {
        super(src, (ObjectCodec)mapper);
        if (mapper == null) {
            this.setCodec(new ObjectMapper(this));
        }
    }

    public final ObjectMapper getCodec() {
        return (ObjectMapper)this._objectCodec;
    }

    public JsonFactory copy() {
        this._checkInvalidCopy(MappingJsonFactory.class);
        return new MappingJsonFactory(this, null);
    }

    public String getFormatName() {
        return "JSON";
    }

    public MatchStrength hasFormat(InputAccessor acc) throws IOException {
        if (((Object)((Object)this)).getClass() == MappingJsonFactory.class) {
            return this.hasJSONFormat(acc);
        }
        return null;
    }
}

