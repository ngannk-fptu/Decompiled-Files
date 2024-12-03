/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.view.ViewType
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Lists
 *  com.google.inject.Inject
 *  com.google.inject.Singleton
 *  org.apache.shindig.common.ContainerConfig
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.atlassian.gadgets.renderer.internal;

import com.atlassian.gadgets.view.ViewType;
import com.atlassian.sal.api.ApplicationProperties;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import org.apache.shindig.common.ContainerConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Singleton
public class AtlassianContainerConfig
implements ContainerConfig {
    static final String MAKE_REQUEST_PATH = "/plugins/servlet/gadgets/makeRequest";
    static final String RPC_RELAY_PATH = "/plugins/servlet/gadgets/rpc-relay";
    static final String SOCIAL_PATH = "/plugins/servlet/social";
    static final String JS_URI_PATH = "/plugins/servlet/gadgets/js/%js%";
    private final ApplicationProperties applicationProperties;
    private final JSONObject config;

    @Inject
    public AtlassianContainerConfig(ApplicationProperties applicationProperties) throws JSONException {
        this.applicationProperties = applicationProperties;
        this.config = this.createConfig();
    }

    private JSONObject createConfig() throws JSONException {
        ImmutableMap.Builder configBuilder = ImmutableMap.builder();
        JSONObject createdConfig = new JSONObject();
        createdConfig.put("gadgets.container", (Object)new JSONArray((Collection)ImmutableList.of((Object)"atlassian", (Object)"default")));
        createdConfig.put("gadgets.parent", JSONObject.NULL);
        createdConfig.put("gadgets.iframeBaseUri", (Object)"/gadgets/ifr");
        createdConfig.put("gadgets.jsUriTemplate", this.jsPath());
        ImmutableMap features = ImmutableMap.builder().put((Object)"core.io", (Object)new JSONObject().put("proxyUrl", (Object)"%rawurl%").put("jsonProxyUrl", this.makeRequestPath()).put("proxyHeaders", (Object)new JSONObject((Map)ImmutableMap.of((Object)"X-Atlassian-Token", (Object)"no-check")))).put((Object)"atlassian.util", (Object)new JSONObject().put("baseUrl", this.baseUrl())).put((Object)"views", (Object)new JSONObject((Map)ImmutableMap.of((Object)ViewType.DEFAULT.getCanonicalName(), (Object)new JSONObject((Map)ImmutableMap.of((Object)"isOnlyVisible", (Object)false, (Object)"urlTemplate", (Object)"http://localhost/gadgets/profile?{var}", (Object)"aliases", (Object)new JSONArray(ViewType.DEFAULT.getAliases()))), (Object)ViewType.CANVAS.getCanonicalName(), (Object)new JSONObject((Map)ImmutableMap.of((Object)"isOnlyVisible", (Object)true, (Object)"urlTemplate", (Object)"http://localhost/gadgets/canvas?{var}", (Object)"aliases", (Object)new JSONArray(ViewType.CANVAS.getAliases())))))).put((Object)"rpc", (Object)new JSONObject().put("parentRelayUrl", this.rpcRelayPath()).put("useLegacyProtocol", false)).put((Object)"skins", (Object)new JSONObject((Map)ImmutableMap.of((Object)"properties", (Object)new JSONObject((Map)ImmutableMap.builder().put((Object)"BG_COLOR", (Object)"").put((Object)"BG_IMAGE", (Object)"").put((Object)"BG_POSITION", (Object)"").put((Object)"BG_REPEAT", (Object)"").put((Object)"FONT_COLOR", (Object)"").put((Object)"ANCHOR_COLOR", (Object)"").build())))).put((Object)"opensocial-0.8", (Object)new JSONObject().put("impl", (Object)"rpc").put("path", this.socialPath()).put("domain", (Object)"atlassian").put("enableCaja", false).put("supportedFields", (Object)new JSONObject((Map)ImmutableMap.of((Object)"person", (Object)new JSONArray((Collection)Lists.newArrayList((Object[])new String[]{"id"})), (Object)"activity", (Object)new JSONArray((Collection)Lists.newArrayList((Object[])new String[]{"id", "title"})))))).build();
        createdConfig.put("gadgets.features", (Object)new JSONObject((Map)features));
        return createdConfig;
    }

    private Object baseUrl() {
        return new Object(){

            public String toString() {
                return AtlassianContainerConfig.this.applicationProperties.getBaseUrl();
            }
        };
    }

    private Object makeRequestPath() {
        return new Object(){

            public String toString() {
                return URI.create(AtlassianContainerConfig.this.applicationProperties.getBaseUrl()).getPath() + AtlassianContainerConfig.MAKE_REQUEST_PATH;
            }
        };
    }

    private Object socialPath() {
        return new Object(){

            public String toString() {
                return URI.create(AtlassianContainerConfig.this.applicationProperties.getBaseUrl()).getPath() + AtlassianContainerConfig.SOCIAL_PATH;
            }
        };
    }

    private Object jsPath() {
        return new Object(){

            public String toString() {
                return URI.create(AtlassianContainerConfig.this.applicationProperties.getBaseUrl()).getPath() + AtlassianContainerConfig.JS_URI_PATH;
            }
        };
    }

    private Object rpcRelayPath() {
        return new Object(){

            public String toString() {
                return URI.create(AtlassianContainerConfig.this.applicationProperties.getBaseUrl()).getPath() + AtlassianContainerConfig.RPC_RELAY_PATH;
            }
        };
    }

    public Collection<String> getContainers() {
        return ImmutableList.of((Object)"default", (Object)"atlassian");
    }

    public Object getJson(String container, String parameter) {
        if (!"atlassian".equals(container) && !"default".equals(container)) {
            return null;
        }
        if (parameter == null) {
            return this.config;
        }
        JSONObject data = this.config;
        try {
            for (String param : parameter.split("/")) {
                Object next = data.get(param);
                if (!(next instanceof JSONObject)) {
                    return next;
                }
                data = (JSONObject)next;
            }
            return data;
        }
        catch (JSONException e) {
            return null;
        }
    }

    public String get(String container, String parameter) {
        Object data = this.getJson(container, parameter);
        return data == null ? null : data.toString();
    }

    public JSONObject getJsonObject(String container, String parameter) {
        Object data = this.getJson(container, parameter);
        return data instanceof JSONObject ? (JSONObject)data : null;
    }

    public JSONArray getJsonArray(String container, String parameter) {
        Object data = this.getJson(container, parameter);
        return data instanceof JSONArray ? (JSONArray)data : null;
    }

    static final class Containers {
        static final String DEFAULT = "default";
        static final String ATLASSIAN = "atlassian";

        Containers() {
        }
    }
}

