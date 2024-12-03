/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.view.RenderResult
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  org.apache.http.HttpEntity
 *  org.apache.http.client.methods.HttpPut
 *  org.apache.http.entity.ContentType
 *  org.apache.http.entity.StringEntity
 */
package com.atlassian.confluence.plugins.synchrony.service.http;

import com.atlassian.confluence.content.render.xhtml.view.RenderResult;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.HtmlUtil;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minidev.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

public class SynchronyChangeRequest {
    final String url;
    final String token;
    final JSONObject data;

    private SynchronyChangeRequest(String url, String token, JSONObject data) {
        this.url = url;
        this.token = token;
        this.data = data;
    }

    public HttpPut getHttpRequest() {
        HttpPut put = new HttpPut(this.url);
        put.addHeader("x-token", this.token);
        put.addHeader("Content-Type", "application/json");
        put.setEntity((HttpEntity)new StringEntity(this.data.toJSONString(), ContentType.APPLICATION_JSON));
        return put;
    }

    public JSONObject getData() {
        return this.data;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SynchronyChangeRequest that = (SynchronyChangeRequest)o;
        return Objects.equals(this.url, that.url) && Objects.equals(this.token, that.token) && Objects.equals(this.data, that.data);
    }

    public int hashCode() {
        return Objects.hash(this.url, this.token, this.data);
    }

    public static class Builder {
        private String url;
        private String token;
        private Map<String, Object> data = new HashMap<String, Object>();

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder html(String html) {
            this.data.put("html", html);
            return this;
        }

        public Builder rev(String rev) {
            this.data.put("rev", rev);
            return this;
        }

        public Builder ancestor(String ancestorRev) {
            this.data.put("ancestor", ancestorRev);
            return this;
        }

        public Builder merges(ConfluenceUser user, Integer version, String type, String trigger) {
            this.data.put("merges", this.buildMergesJson(user, version, type, trigger));
            return this;
        }

        public Builder generateRev(String generateRev) {
            this.data.put("generate-rev", generateRev);
            return this;
        }

        public Builder generateReset(boolean generateReset) {
            this.data.put("generate-reset", generateReset);
            return this;
        }

        @VisibleForTesting
        public static String createEditorDom(String title, RenderResult editorFormat) {
            if (!editorFormat.isSuccessful()) {
                throw new IllegalStateException("Editor format fatal render error");
            }
            return "<body data-title='" + HtmlUtil.htmlEncode((String)title) + "'>" + editorFormat.getRender() + "</body>";
        }

        private JSONObject buildMergesJson(ConfluenceUser user, Integer version, String type, String trigger) {
            JSONObject metaAttributes = new JSONObject();
            if (type != null) {
                metaAttributes.put("type", type);
            }
            if (trigger != null) {
                metaAttributes.put("trigger", trigger);
            }
            if (version != null) {
                metaAttributes.put("confVersion", version.toString());
            }
            metaAttributes.put("user", this.getUserFullName(user));
            JSONObject meta = new JSONObject((Map<String, ?>)ImmutableMap.of((Object)"meta", (Object)metaAttributes));
            return new JSONObject((Map<String, ?>)ImmutableMap.of((Object)"master", (Object)meta));
        }

        private String getUserFullName(ConfluenceUser user) {
            return user != null ? user.getFullName() : "";
        }

        public SynchronyChangeRequest build() {
            return new SynchronyChangeRequest(this.url, this.token, new JSONObject(this.data));
        }
    }
}

