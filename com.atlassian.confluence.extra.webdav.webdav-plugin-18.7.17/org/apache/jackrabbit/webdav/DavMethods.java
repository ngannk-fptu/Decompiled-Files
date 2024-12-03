/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav;

import java.util.HashMap;
import java.util.Map;
import org.apache.jackrabbit.webdav.DavServletRequest;

public final class DavMethods {
    private static Map<String, Integer> methodMap = new HashMap<String, Integer>();
    private static int[] labelMethods;
    private static int[] deltaVMethods;
    public static final int DAV_OPTIONS = 1;
    public static final String METHOD_OPTIONS = "OPTIONS";
    public static final int DAV_GET = 2;
    public static final String METHOD_GET = "GET";
    public static final int DAV_HEAD = 3;
    public static final String METHOD_HEAD = "HEAD";
    public static final int DAV_POST = 4;
    public static final String METHOD_POST = "POST";
    public static final int DAV_DELETE = 5;
    public static final String METHOD_DELETE = "DELETE";
    public static final int DAV_PUT = 6;
    public static final String METHOD_PUT = "PUT";
    public static final int DAV_PROPFIND = 7;
    public static final String METHOD_PROPFIND = "PROPFIND";
    public static final int DAV_PROPPATCH = 8;
    public static final String METHOD_PROPPATCH = "PROPPATCH";
    public static final int DAV_MKCOL = 9;
    public static final String METHOD_MKCOL = "MKCOL";
    public static final int DAV_COPY = 10;
    public static final String METHOD_COPY = "COPY";
    public static final int DAV_MOVE = 11;
    public static final String METHOD_MOVE = "MOVE";
    public static final int DAV_LOCK = 12;
    public static final String METHOD_LOCK = "LOCK";
    public static final int DAV_UNLOCK = 13;
    public static final String METHOD_UNLOCK = "UNLOCK";
    public static final int DAV_ORDERPATCH = 14;
    public static final String METHOD_ORDERPATCH = "ORDERPATCH";
    public static final int DAV_SUBSCRIBE = 15;
    public static final String METHOD_SUBSCRIBE = "SUBSCRIBE";
    public static final int DAV_UNSUBSCRIBE = 16;
    public static final String METHOD_UNSUBSCRIBE = "UNSUBSCRIBE";
    public static final int DAV_POLL = 17;
    public static final String METHOD_POLL = "POLL";
    public static final int DAV_SEARCH = 18;
    public static final String METHOD_SEARCH = "SEARCH";
    public static final int DAV_REPORT = 19;
    public static final String METHOD_REPORT = "REPORT";
    public static final int DAV_VERSION_CONTROL = 20;
    public static final String METHOD_VERSION_CONTROL = "VERSION-CONTROL";
    public static final int DAV_CHECKIN = 21;
    public static final String METHOD_CHECKIN = "CHECKIN";
    public static final int DAV_CHECKOUT = 22;
    public static final String METHOD_CHECKOUT = "CHECKOUT";
    public static final int DAV_UNCHECKOUT = 23;
    public static final String METHOD_UNCHECKOUT = "UNCHECKOUT";
    public static final int DAV_LABEL = 24;
    public static final String METHOD_LABEL = "LABEL";
    public static final int DAV_MERGE = 25;
    public static final String METHOD_MERGE = "MERGE";
    public static final int DAV_UPDATE = 26;
    public static final String METHOD_UPDATE = "UPDATE";
    public static final int DAV_MKWORKSPACE = 27;
    public static final String METHOD_MKWORKSPACE = "MKWORKSPACE";
    public static final int DAV_BASELINE_CONTROL = 28;
    public static final String METHOD_BASELINE_CONTROL = "BASELINE-CONTROL";
    public static final int DAV_MKACTIVITY = 29;
    public static final String METHOD_MKACTIVITY = "MKACTIVITY";
    public static final int DAV_ACL = 30;
    public static final String METHOD_ACL = "ACL";
    public static final int DAV_REBIND = 31;
    public static final String METHOD_REBIND = "REBIND";
    public static final int DAV_UNBIND = 32;
    public static final String METHOD_UNBIND = "UNBIND";
    public static final int DAV_BIND = 33;
    public static final String METHOD_BIND = "BIND";

    private DavMethods() {
    }

    public static int getMethodCode(String method) {
        Integer code = methodMap.get(method.toUpperCase());
        if (code != null) {
            return code;
        }
        return 0;
    }

    private static void addMethodCode(String method, int code) {
        methodMap.put(method, code);
    }

    public static boolean isCreateRequest(DavServletRequest request) {
        int methodCode = DavMethods.getMethodCode(request.getMethod());
        return methodCode == 6 || methodCode == 4 || methodCode == 9 || methodCode == 27;
    }

    public static boolean isCreateCollectionRequest(DavServletRequest request) {
        int methodCode = DavMethods.getMethodCode(request.getMethod());
        return methodCode == 9 || methodCode == 27;
    }

    public static boolean isMethodAffectedByLabel(DavServletRequest request) {
        int code = DavMethods.getMethodCode(request.getMethod());
        for (int labelMethod : labelMethods) {
            if (code != labelMethod) continue;
            return true;
        }
        return false;
    }

    public static boolean isDeltaVMethod(DavServletRequest request) {
        int code = DavMethods.getMethodCode(request.getMethod());
        for (int deltaVMethod : deltaVMethods) {
            if (code != deltaVMethod) continue;
            return true;
        }
        return false;
    }

    static {
        DavMethods.addMethodCode(METHOD_OPTIONS, 1);
        DavMethods.addMethodCode(METHOD_GET, 2);
        DavMethods.addMethodCode(METHOD_HEAD, 3);
        DavMethods.addMethodCode(METHOD_POST, 4);
        DavMethods.addMethodCode(METHOD_PUT, 6);
        DavMethods.addMethodCode(METHOD_DELETE, 5);
        DavMethods.addMethodCode(METHOD_PROPFIND, 7);
        DavMethods.addMethodCode(METHOD_PROPPATCH, 8);
        DavMethods.addMethodCode(METHOD_MKCOL, 9);
        DavMethods.addMethodCode(METHOD_COPY, 10);
        DavMethods.addMethodCode(METHOD_MOVE, 11);
        DavMethods.addMethodCode(METHOD_LOCK, 12);
        DavMethods.addMethodCode(METHOD_UNLOCK, 13);
        DavMethods.addMethodCode(METHOD_ORDERPATCH, 14);
        DavMethods.addMethodCode(METHOD_SUBSCRIBE, 15);
        DavMethods.addMethodCode(METHOD_UNSUBSCRIBE, 16);
        DavMethods.addMethodCode(METHOD_POLL, 17);
        DavMethods.addMethodCode(METHOD_SEARCH, 18);
        DavMethods.addMethodCode(METHOD_REPORT, 19);
        DavMethods.addMethodCode(METHOD_VERSION_CONTROL, 20);
        DavMethods.addMethodCode(METHOD_CHECKIN, 21);
        DavMethods.addMethodCode(METHOD_CHECKOUT, 22);
        DavMethods.addMethodCode(METHOD_UNCHECKOUT, 23);
        DavMethods.addMethodCode(METHOD_LABEL, 24);
        DavMethods.addMethodCode(METHOD_MERGE, 25);
        DavMethods.addMethodCode(METHOD_UPDATE, 26);
        DavMethods.addMethodCode(METHOD_MKWORKSPACE, 27);
        DavMethods.addMethodCode(METHOD_BASELINE_CONTROL, 28);
        DavMethods.addMethodCode(METHOD_MKACTIVITY, 29);
        DavMethods.addMethodCode(METHOD_ACL, 30);
        DavMethods.addMethodCode(METHOD_REBIND, 31);
        DavMethods.addMethodCode(METHOD_UNBIND, 32);
        DavMethods.addMethodCode(METHOD_BIND, 33);
        labelMethods = new int[]{2, 3, 1, 7, 24, 10};
        deltaVMethods = new int[]{19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29};
    }
}

