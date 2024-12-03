/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.rest;

import com.hazelcast.internal.ascii.TextCommandService;
import com.hazelcast.internal.ascii.rest.HttpCommand;
import com.hazelcast.internal.ascii.rest.HttpCommandProcessor;
import com.hazelcast.internal.ascii.rest.HttpDeleteCommand;
import com.hazelcast.internal.ascii.rest.RestValue;
import com.hazelcast.util.StringUtil;

public class HttpDeleteCommandProcessor
extends HttpCommandProcessor<HttpDeleteCommand> {
    public HttpDeleteCommandProcessor(TextCommandService textCommandService) {
        super(textCommandService);
    }

    @Override
    public void handle(HttpDeleteCommand command) {
        try {
            String uri = command.getURI();
            if (uri.startsWith("/hazelcast/rest/maps/")) {
                this.handleMap(command, uri);
            } else if (uri.startsWith("/hazelcast/rest/queues/")) {
                this.handleQueue(command, uri);
            } else {
                command.send404();
            }
        }
        catch (IndexOutOfBoundsException e) {
            command.send400();
        }
        catch (Exception e) {
            command.send500();
        }
        this.textCommandService.sendResponse(command);
    }

    private void handleMap(HttpDeleteCommand command, String uri) {
        int indexEnd = uri.indexOf(47, "/hazelcast/rest/maps/".length());
        if (indexEnd == -1) {
            String mapName = uri.substring("/hazelcast/rest/maps/".length());
            this.textCommandService.deleteAll(mapName);
            command.send200();
        } else {
            String mapName = uri.substring("/hazelcast/rest/maps/".length(), indexEnd);
            String key = uri.substring(indexEnd + 1);
            this.textCommandService.delete(mapName, key);
            command.send200();
        }
    }

    private void handleQueue(HttpDeleteCommand command, String uri) {
        String secondStr;
        int seconds;
        int indexEnd = uri.indexOf(47, "/hazelcast/rest/queues/".length());
        String queueName = uri.substring("/hazelcast/rest/queues/".length(), indexEnd);
        Object value = this.textCommandService.poll(queueName, seconds = (secondStr = uri.length() > indexEnd + 1 ? uri.substring(indexEnd + 1) : null) == null ? 0 : Integer.parseInt(secondStr));
        if (value == null) {
            command.send204();
        } else if (value instanceof byte[]) {
            command.setResponse(null, (byte[])value);
        } else if (value instanceof RestValue) {
            RestValue restValue = (RestValue)value;
            command.setResponse(restValue.getContentType(), restValue.getValue());
        } else if (value instanceof String) {
            command.setResponse(HttpCommand.CONTENT_TYPE_PLAIN_TEXT, StringUtil.stringToBytes((String)value));
        } else {
            command.setResponse(null, this.textCommandService.toByteArray(value));
        }
    }

    @Override
    public void handleRejection(HttpDeleteCommand command) {
        this.handle(command);
    }
}

