/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.apache.struts2.interceptor;

import javax.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface ResourceIsolationPolicy {
    public static final String SEC_FETCH_DEST_HEADER = "Sec-Fetch-Dest";
    public static final String SEC_FETCH_MODE_HEADER = "Sec-Fetch-Mode";
    public static final String SEC_FETCH_SITE_HEADER = "Sec-Fetch-Site";
    public static final String SEC_FETCH_USER_HEADER = "Sec-Fetch-User";
    public static final String VARY_HEADER = "Vary";
    public static final String DEST_AUDIO = "audio";
    public static final String DEST_AUDIOWORKLET = "audioworklet";
    public static final String DEST_DOCUMENT = "document";
    public static final String DEST_EMBED = "embed";
    public static final String DEST_EMPTY = "empty";
    public static final String DEST_FONT = "font";
    public static final String DEST_IMAGE = "image";
    public static final String DEST_MANIFEST = "manifest";
    public static final String DEST_NESTED_DOCUMENT = "nested-document";
    public static final String DEST_OBJECT = "object";
    public static final String DEST_PAINTWORKLET = "paintworklet";
    public static final String DEST_REPORT = "report";
    public static final String DEST_SCRIPT = "script";
    public static final String DEST_SERVICEWORKER = "serviceworker";
    public static final String DEST_SHAREDWORKER = "sharedworker";
    public static final String DEST_STYLE = "style";
    public static final String DEST_TRACK = "track";
    public static final String DEST_VIDEO = "video";
    public static final String DEST_WORKER = "worker";
    public static final String DEST_XSLT = "xslt";
    public static final String MODE_CORS = "cors";
    public static final String MODE_NAVIGATE = "navigate";
    public static final String MODE_NESTED_NAVIGATE = "nested-navigate";
    public static final String MODE_NO_CORS = "no-cors";
    public static final String MODE_SAME_ORIGIN = "same-origin";
    public static final String MODE_WEBSOCKET = "websocket";
    public static final String SITE_CROSS_SITE = "cross-site";
    public static final String SITE_SAME_ORIGIN = "same-origin";
    public static final String SITE_SAME_SITE = "same-site";
    public static final String SITE_NONE = "none";

    public boolean isRequestAllowed(HttpServletRequest var1);
}

