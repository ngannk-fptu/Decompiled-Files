/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.protobuf.util.JsonFormat$Parser
 *  com.google.protobuf.util.JsonFormat$Printer
 */
package org.springframework.http.converter.protobuf;

import com.google.protobuf.util.JsonFormat;
import org.springframework.http.converter.protobuf.ExtensionRegistryInitializer;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.lang.Nullable;

public class ProtobufJsonFormatHttpMessageConverter
extends ProtobufHttpMessageConverter {
    public ProtobufJsonFormatHttpMessageConverter() {
        this((JsonFormat.Parser)null, (JsonFormat.Printer)null, (ExtensionRegistryInitializer)null);
    }

    public ProtobufJsonFormatHttpMessageConverter(@Nullable JsonFormat.Parser parser, @Nullable JsonFormat.Printer printer) {
        this(parser, printer, null);
    }

    public ProtobufJsonFormatHttpMessageConverter(@Nullable JsonFormat.Parser parser, @Nullable JsonFormat.Printer printer, @Nullable ExtensionRegistryInitializer registryInitializer) {
        super(new ProtobufHttpMessageConverter.ProtobufJavaUtilSupport(parser, printer), registryInitializer);
    }
}

