/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.ascii;

import com.hazelcast.internal.ascii.CommandParser;
import com.hazelcast.internal.ascii.rest.HttpDeleteCommandParser;
import com.hazelcast.internal.ascii.rest.HttpGetCommandParser;
import com.hazelcast.internal.ascii.rest.HttpHeadCommandParser;
import com.hazelcast.internal.ascii.rest.HttpPostCommandParser;
import com.hazelcast.nio.IOService;
import com.hazelcast.nio.ascii.RestApiFilter;
import com.hazelcast.nio.ascii.TextDecoder;
import com.hazelcast.nio.ascii.TextEncoder;
import com.hazelcast.nio.ascii.TextParsers;
import com.hazelcast.nio.tcp.TcpIpConnection;
import com.hazelcast.spi.annotation.PrivateApi;
import java.util.HashMap;

@PrivateApi
public class RestApiTextDecoder
extends TextDecoder {
    public static final TextParsers TEXT_PARSERS;

    public RestApiTextDecoder(TcpIpConnection connection, TextEncoder encoder, boolean rootDecoder) {
        super(connection, encoder, RestApiTextDecoder.createFilter(connection), TEXT_PARSERS, rootDecoder);
    }

    private static RestApiFilter createFilter(TcpIpConnection connection) {
        IOService ioService = connection.getEndpointManager().getNetworkingService().getIoService();
        return new RestApiFilter(ioService.getRestApiConfig(), TEXT_PARSERS);
    }

    static {
        HashMap<String, CommandParser> parsers = new HashMap<String, CommandParser>();
        parsers.put("GET", new HttpGetCommandParser());
        parsers.put("POST", new HttpPostCommandParser());
        parsers.put("PUT", new HttpPostCommandParser());
        parsers.put("DELETE", new HttpDeleteCommandParser());
        parsers.put("HEAD", new HttpHeadCommandParser());
        TEXT_PARSERS = new TextParsers(parsers);
    }
}

