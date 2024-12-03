/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.json.marshal.Jsonable$JsonMappingException
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  com.atlassian.whisper.plugin.api.Message
 *  com.atlassian.whisper.plugin.api.MessagesService
 *  javax.inject.Inject
 *  org.codehaus.jettison.json.JSONException
 *  org.codehaus.jettison.json.JSONObject
 */
package com.atlassian.whisper.plugin.impl;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import com.atlassian.whisper.plugin.api.Message;
import com.atlassian.whisper.plugin.api.MessagesService;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class WhisperMessagesDataProvider
implements WebResourceDataProvider {
    private final MessagesService messagesService;

    @Inject
    public WhisperMessagesDataProvider(MessagesService messagesService) {
        this.messagesService = messagesService;
    }

    public Jsonable get() {
        return writer -> {
            try {
                this.getJsonData().write(writer);
            }
            catch (JSONException e) {
                throw new Jsonable.JsonMappingException((Throwable)e);
            }
        };
    }

    private JSONObject getJsonData() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("messages", () -> WhisperMessagesDataProvider.messagesAsJson(this.messagesService.getMessagesForCurrentUser()));
        return json;
    }

    public static String messagesAsJson(Set<Message> messages) {
        return messages.stream().map(Message::getContent).collect(Collectors.joining(",", "[", "]"));
    }
}

