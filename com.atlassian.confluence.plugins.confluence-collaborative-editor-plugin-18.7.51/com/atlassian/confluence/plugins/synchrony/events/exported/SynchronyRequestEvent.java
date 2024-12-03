/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.plugins.synchrony.events.exported;

import com.atlassian.confluence.plugins.synchrony.service.http.SynchronyChangeRequest;
import com.atlassian.event.api.AsynchronousPreferred;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.minidev.json.JSONObject;

@AsynchronousPreferred
public class SynchronyRequestEvent {
    private static final String SANITISED_STR = "***sanitised***";
    private final long contentId;
    private final String type;
    private final String url;
    private final boolean successful;
    private final Map<String, Object> params;

    public SynchronyRequestEvent(long contentId, SynchronyChangeRequest synchronyChangeRequest, boolean isSuccessful) {
        this.contentId = contentId;
        this.type = "change";
        this.successful = isSuccessful;
        this.url = SynchronyRequestEvent.sanitiseUrl(synchronyChangeRequest.getHttpRequest().getURI());
        this.params = SynchronyRequestEvent.sanitiseRequestData(synchronyChangeRequest);
    }

    private static String sanitiseUrl(URI uri) {
        StringBuilder sb = new StringBuilder("");
        if (uri.getScheme() != null) {
            sb.append(uri.getScheme() + "://");
        }
        sb.append(SANITISED_STR);
        if (uri.getQuery() != null) {
            sb.append('?').append(uri.getQuery());
        }
        return sb.toString();
    }

    private static Map<String, Object> sanitiseRequestData(SynchronyChangeRequest synchronyChangeRequest) {
        HashMap<String, Object> params = new HashMap<String, Object>(synchronyChangeRequest.getData());
        if (params.containsKey("merges")) {
            try {
                HashMap<String, Object> meta = new HashMap<String, Object>((JSONObject)((JSONObject)((JSONObject)params.get("merges")).get("master")).get("meta"));
                meta.replace("user", SANITISED_STR);
                params.put("merges", Collections.singletonMap("master", Collections.singletonMap("meta", meta)));
            }
            catch (Exception e) {
                params.put("merges", Collections.singletonMap("error", String.valueOf(e.getMessage())));
            }
        }
        params.replace("html", SANITISED_STR);
        return params;
    }

    public long getContentId() {
        return this.contentId;
    }

    public String getType() {
        return this.type;
    }

    public String getUrl() {
        return this.url;
    }

    public Map<String, Object> getParams() {
        return this.params;
    }

    public boolean isSuccessful() {
        return this.successful;
    }
}

