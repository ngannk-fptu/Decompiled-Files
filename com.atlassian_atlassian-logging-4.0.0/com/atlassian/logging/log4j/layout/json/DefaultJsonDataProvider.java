/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.logging.log4j.layout.json;

import com.atlassian.logging.log4j.layout.json.JsonContextData;
import com.atlassian.logging.log4j.layout.json.JsonDataProvider;
import com.atlassian.logging.log4j.layout.json.JsonLayoutHelper;
import com.atlassian.logging.log4j.layout.json.JsonStaticData;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class DefaultJsonDataProvider
implements JsonDataProvider {
    private static final long HOSTNAME_TTL = 30000L;
    private static final String DEFAULT_HOSTNAME = "unknown";
    private volatile String cachedHostName = null;
    private volatile long cachedHostNameValidTill = 0L;

    @Override
    public JsonStaticData getStaticData() {
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        return JsonStaticData.builder().setProductName(this.getSysProp(SysPropKey.PRODUCT_NAME)).setProcessId(this.getProcessId(jvmName)).setServiceId(this.getSysProp(SysPropKey.SERVICE_ID)).setEnvironment(this.getSysProp(SysPropKey.ENVIRONMENT)).setDataCenter(this.getSysProp(SysPropKey.DATA_CENTER)).setRack(this.getSysProp(SysPropKey.RACK)).build();
    }

    protected long getProcessId(String jvmName) {
        int separatorIndex = jvmName.indexOf(64);
        if (separatorIndex < 1) {
            return 0L;
        }
        try {
            return Long.parseLong(jvmName.substring(0, separatorIndex));
        }
        catch (NumberFormatException e) {
            return 0L;
        }
    }

    private String getSysProp(SysPropKey key) {
        return System.getProperty(key.getKey());
    }

    @Override
    public JsonContextData getContextData(JsonLayoutHelper.LogEvent event) {
        return JsonContextData.builder().setRequestId(this.getMdc(event, MdcKey.REQUEST_ID)).setSessionId(this.getMdc(event, MdcKey.SESSION_ID)).setUserKey(this.getMdc(event, MdcKey.USER_KEY)).build();
    }

    private String getMdc(JsonLayoutHelper.LogEvent event, MdcKey key) {
        return (String)event.getThreadContextMap().getValue(key.getKey());
    }

    @Override
    public Map<String, Object> getExtraData(JsonLayoutHelper.LogEvent event) {
        HashMap<String, Object> properties = new HashMap<String, Object>();
        event.getThreadContextMap().forEach((key, value) -> {
            if (!MdcKey.getKeysSet().contains(key)) {
                properties.put((String)key, value);
            }
        });
        properties.putAll(this.getExtraStaticData());
        return properties;
    }

    @Nonnull
    protected Map<String, String> getExtraStaticData() {
        return Collections.emptyMap();
    }

    @Override
    public String getHostName() {
        if (System.currentTimeMillis() > this.cachedHostNameValidTill) {
            long now = this.cachedHostNameValidTill;
            String newHostName = this.resolveLocalHostName();
            if (now == this.cachedHostNameValidTill) {
                this.cachedHostNameValidTill = System.currentTimeMillis() + 30000L;
                this.cachedHostName = newHostName;
            }
        }
        return this.cachedHostName;
    }

    private String resolveLocalHostName() {
        try {
            return InetAddress.getLocalHost().getCanonicalHostName();
        }
        catch (UnknownHostException uhe) {
            System.err.println("Cannot resolve localhost");
            uhe.printStackTrace(System.err);
            return DEFAULT_HOSTNAME;
        }
    }

    private static enum SysPropKey {
        PRODUCT_NAME("STUDIO_COMPONENT_APP"),
        ENVIRONMENT("studio.env"),
        DATA_CENTER("unicorn.dc"),
        RACK("unicorn.rack"),
        SERVICE_ID("atlassian.logging.service.id");

        private final String key;

        private SysPropKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }
    }

    public static enum MdcKey {
        REQUEST_ID("requestId"),
        SESSION_ID("sessionId"),
        USER_KEY("userKey");

        private final String key;

        private MdcKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }

        public static Set<String> getKeysSet() {
            return Arrays.stream(MdcKey.values()).map(MdcKey::getKey).collect(Collectors.toSet());
        }
    }
}

