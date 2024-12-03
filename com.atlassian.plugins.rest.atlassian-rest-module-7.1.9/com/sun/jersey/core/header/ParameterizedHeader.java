/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.header;

import com.sun.jersey.core.header.reader.HttpHeaderReader;
import java.text.ParseException;
import java.util.Collections;
import java.util.Map;

public class ParameterizedHeader {
    private String value;
    private Map<String, String> parameters;

    public ParameterizedHeader(String header) throws ParseException {
        this(HttpHeaderReader.newInstance(header));
    }

    public ParameterizedHeader(HttpHeaderReader reader) throws ParseException {
        reader.hasNext();
        this.value = "";
        while (reader.hasNext() && !reader.hasNextSeparator(';', false)) {
            reader.next();
            this.value = this.value + reader.getEventValue();
        }
        if (reader.hasNext()) {
            this.parameters = HttpHeaderReader.readParameters(reader);
        }
        this.parameters = this.parameters == null ? Collections.emptyMap() : Collections.unmodifiableMap(this.parameters);
    }

    public String getValue() {
        return this.value;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }
}

