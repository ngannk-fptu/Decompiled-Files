/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.servlet;

import java.util.HashMap;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public final class FlashMap
extends HashMap<String, Object>
implements Comparable<FlashMap> {
    @Nullable
    private String targetRequestPath;
    private final MultiValueMap<String, String> targetRequestParams = new LinkedMultiValueMap(3);
    private long expirationTime = -1L;

    public void setTargetRequestPath(@Nullable String path) {
        this.targetRequestPath = path;
    }

    @Nullable
    public String getTargetRequestPath() {
        return this.targetRequestPath;
    }

    public FlashMap addTargetRequestParams(@Nullable MultiValueMap<String, String> params) {
        if (params != null) {
            params.forEach((key, values) -> {
                for (String value : values) {
                    this.addTargetRequestParam((String)key, value);
                }
            });
        }
        return this;
    }

    public FlashMap addTargetRequestParam(String name, String value) {
        if (StringUtils.hasText((String)name) && StringUtils.hasText((String)value)) {
            this.targetRequestParams.add((Object)name, (Object)value);
        }
        return this;
    }

    public MultiValueMap<String, String> getTargetRequestParams() {
        return this.targetRequestParams;
    }

    public void startExpirationPeriod(int timeToLive) {
        this.expirationTime = System.currentTimeMillis() + (long)(timeToLive * 1000);
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public long getExpirationTime() {
        return this.expirationTime;
    }

    public boolean isExpired() {
        return this.expirationTime != -1L && System.currentTimeMillis() > this.expirationTime;
    }

    @Override
    public int compareTo(FlashMap other) {
        int otherUrlPath;
        int thisUrlPath = this.targetRequestPath != null ? 1 : 0;
        int n = otherUrlPath = other.targetRequestPath != null ? 1 : 0;
        if (thisUrlPath != otherUrlPath) {
            return otherUrlPath - thisUrlPath;
        }
        return other.targetRequestParams.size() - this.targetRequestParams.size();
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FlashMap)) {
            return false;
        }
        FlashMap otherFlashMap = (FlashMap)other;
        return super.equals(otherFlashMap) && ObjectUtils.nullSafeEquals((Object)this.targetRequestPath, (Object)otherFlashMap.targetRequestPath) && this.targetRequestParams.equals(otherFlashMap.targetRequestParams);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + ObjectUtils.nullSafeHashCode((Object)this.targetRequestPath);
        result = 31 * result + this.targetRequestParams.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FlashMap [attributes=" + super.toString() + ", targetRequestPath=" + this.targetRequestPath + ", targetRequestParams=" + this.targetRequestParams + "]";
    }
}

