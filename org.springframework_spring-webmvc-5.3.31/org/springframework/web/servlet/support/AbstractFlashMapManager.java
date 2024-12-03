/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.StringUtils
 *  org.springframework.web.util.UrlPathHelper
 */
package org.springframework.web.servlet.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UrlPathHelper;

public abstract class AbstractFlashMapManager
implements FlashMapManager {
    private static final Object DEFAULT_FLASH_MAPS_MUTEX = new Object();
    protected final Log logger = LogFactory.getLog(this.getClass());
    private int flashMapTimeout = 180;
    private UrlPathHelper urlPathHelper = UrlPathHelper.defaultInstance;

    public void setFlashMapTimeout(int flashMapTimeout) {
        this.flashMapTimeout = flashMapTimeout;
    }

    public int getFlashMapTimeout() {
        return this.flashMapTimeout;
    }

    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
        Assert.notNull((Object)urlPathHelper, (String)"UrlPathHelper must not be null");
        this.urlPathHelper = urlPathHelper;
    }

    public UrlPathHelper getUrlPathHelper() {
        return this.urlPathHelper;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nullable
    public final FlashMap retrieveAndUpdate(HttpServletRequest request, HttpServletResponse response) {
        List<FlashMap> allFlashMaps = this.retrieveFlashMaps(request);
        if (CollectionUtils.isEmpty(allFlashMaps)) {
            return null;
        }
        List<FlashMap> mapsToRemove = this.getExpiredFlashMaps(allFlashMaps);
        FlashMap match = this.getMatchingFlashMap(allFlashMaps, request);
        if (match != null) {
            mapsToRemove.add(match);
        }
        if (!mapsToRemove.isEmpty()) {
            Object mutex = this.getFlashMapsMutex(request);
            if (mutex != null) {
                Object object = mutex;
                synchronized (object) {
                    allFlashMaps = this.retrieveFlashMaps(request);
                    if (allFlashMaps != null) {
                        allFlashMaps.removeAll(mapsToRemove);
                        this.updateFlashMaps(allFlashMaps, request, response);
                    }
                }
            } else {
                allFlashMaps.removeAll(mapsToRemove);
                this.updateFlashMaps(allFlashMaps, request, response);
            }
        }
        return match;
    }

    private List<FlashMap> getExpiredFlashMaps(List<FlashMap> allMaps) {
        ArrayList<FlashMap> result = new ArrayList<FlashMap>();
        for (FlashMap map : allMaps) {
            if (!map.isExpired()) continue;
            result.add(map);
        }
        return result;
    }

    @Nullable
    private FlashMap getMatchingFlashMap(List<FlashMap> allMaps, HttpServletRequest request) {
        ArrayList<FlashMap> result = new ArrayList<FlashMap>();
        for (FlashMap flashMap : allMaps) {
            if (!this.isFlashMapForRequest(flashMap, request)) continue;
            result.add(flashMap);
        }
        if (!result.isEmpty()) {
            Collections.sort(result);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)("Found " + result.get(0)));
            }
            return (FlashMap)result.get(0);
        }
        return null;
    }

    protected boolean isFlashMapForRequest(FlashMap flashMap, HttpServletRequest request) {
        String requestUri;
        String expectedPath = flashMap.getTargetRequestPath();
        if (expectedPath != null && !(requestUri = this.getUrlPathHelper().getOriginatingRequestUri(request)).equals(expectedPath) && !requestUri.equals(expectedPath + "/")) {
            return false;
        }
        MultiValueMap<String, String> actualParams = this.getOriginatingRequestParams(request);
        MultiValueMap<String, String> expectedParams = flashMap.getTargetRequestParams();
        for (Map.Entry entry : expectedParams.entrySet()) {
            List actualValues = (List)actualParams.get(entry.getKey());
            if (actualValues == null) {
                return false;
            }
            for (String expectedValue : (List)entry.getValue()) {
                if (actualValues.contains(expectedValue)) continue;
                return false;
            }
        }
        return true;
    }

    private MultiValueMap<String, String> getOriginatingRequestParams(HttpServletRequest request) {
        String query = this.getUrlPathHelper().getOriginatingQueryString(request);
        return ServletUriComponentsBuilder.fromPath((String)"/").query(query).build().getQueryParams();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void saveOutputFlashMap(FlashMap flashMap, HttpServletRequest request, HttpServletResponse response) {
        if (CollectionUtils.isEmpty((Map)flashMap)) {
            return;
        }
        String path = this.decodeAndNormalizePath(flashMap.getTargetRequestPath(), request);
        flashMap.setTargetRequestPath(path);
        flashMap.startExpirationPeriod(this.getFlashMapTimeout());
        Object mutex = this.getFlashMapsMutex(request);
        if (mutex != null) {
            Object object = mutex;
            synchronized (object) {
                CopyOnWriteArrayList<FlashMap> allFlashMaps = this.retrieveFlashMaps(request);
                allFlashMaps = allFlashMaps != null ? allFlashMaps : new CopyOnWriteArrayList<FlashMap>();
                allFlashMaps.add(flashMap);
                this.updateFlashMaps(allFlashMaps, request, response);
            }
        } else {
            ArrayList<FlashMap> allFlashMaps = this.retrieveFlashMaps(request);
            allFlashMaps = allFlashMaps != null ? allFlashMaps : new ArrayList<FlashMap>(1);
            allFlashMaps.add(flashMap);
            this.updateFlashMaps(allFlashMaps, request, response);
        }
    }

    @Nullable
    private String decodeAndNormalizePath(@Nullable String path, HttpServletRequest request) {
        if (path != null && !path.isEmpty() && (path = this.getUrlPathHelper().decodeRequestString(request, path)).charAt(0) != '/') {
            String requestUri = this.getUrlPathHelper().getRequestUri(request);
            path = requestUri.substring(0, requestUri.lastIndexOf(47) + 1) + path;
            path = StringUtils.cleanPath((String)path);
        }
        return path;
    }

    @Nullable
    protected abstract List<FlashMap> retrieveFlashMaps(HttpServletRequest var1);

    protected abstract void updateFlashMaps(List<FlashMap> var1, HttpServletRequest var2, HttpServletResponse var3);

    @Nullable
    protected Object getFlashMapsMutex(HttpServletRequest request) {
        return DEFAULT_FLASH_MAPS_MUTEX;
    }
}

