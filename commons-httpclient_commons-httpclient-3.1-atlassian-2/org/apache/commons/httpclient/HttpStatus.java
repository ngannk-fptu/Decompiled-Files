/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient;

public class HttpStatus {
    private static final String[][] REASON_PHRASES = new String[][]{new String[0], new String[3], new String[8], new String[8], new String[25], new String[8]};
    public static final int SC_CONTINUE = 100;
    public static final int SC_SWITCHING_PROTOCOLS = 101;
    public static final int SC_PROCESSING = 102;
    public static final int SC_OK = 200;
    public static final int SC_CREATED = 201;
    public static final int SC_ACCEPTED = 202;
    public static final int SC_NON_AUTHORITATIVE_INFORMATION = 203;
    public static final int SC_NO_CONTENT = 204;
    public static final int SC_RESET_CONTENT = 205;
    public static final int SC_PARTIAL_CONTENT = 206;
    public static final int SC_MULTI_STATUS = 207;
    public static final int SC_MULTIPLE_CHOICES = 300;
    public static final int SC_MOVED_PERMANENTLY = 301;
    public static final int SC_MOVED_TEMPORARILY = 302;
    public static final int SC_SEE_OTHER = 303;
    public static final int SC_NOT_MODIFIED = 304;
    public static final int SC_USE_PROXY = 305;
    public static final int SC_TEMPORARY_REDIRECT = 307;
    public static final int SC_BAD_REQUEST = 400;
    public static final int SC_UNAUTHORIZED = 401;
    public static final int SC_PAYMENT_REQUIRED = 402;
    public static final int SC_FORBIDDEN = 403;
    public static final int SC_NOT_FOUND = 404;
    public static final int SC_METHOD_NOT_ALLOWED = 405;
    public static final int SC_NOT_ACCEPTABLE = 406;
    public static final int SC_PROXY_AUTHENTICATION_REQUIRED = 407;
    public static final int SC_REQUEST_TIMEOUT = 408;
    public static final int SC_CONFLICT = 409;
    public static final int SC_GONE = 410;
    public static final int SC_LENGTH_REQUIRED = 411;
    public static final int SC_PRECONDITION_FAILED = 412;
    public static final int SC_REQUEST_TOO_LONG = 413;
    public static final int SC_REQUEST_URI_TOO_LONG = 414;
    public static final int SC_UNSUPPORTED_MEDIA_TYPE = 415;
    public static final int SC_REQUESTED_RANGE_NOT_SATISFIABLE = 416;
    public static final int SC_EXPECTATION_FAILED = 417;
    public static final int SC_INSUFFICIENT_SPACE_ON_RESOURCE = 419;
    public static final int SC_METHOD_FAILURE = 420;
    public static final int SC_UNPROCESSABLE_ENTITY = 422;
    public static final int SC_LOCKED = 423;
    public static final int SC_FAILED_DEPENDENCY = 424;
    public static final int SC_INTERNAL_SERVER_ERROR = 500;
    public static final int SC_NOT_IMPLEMENTED = 501;
    public static final int SC_BAD_GATEWAY = 502;
    public static final int SC_SERVICE_UNAVAILABLE = 503;
    public static final int SC_GATEWAY_TIMEOUT = 504;
    public static final int SC_HTTP_VERSION_NOT_SUPPORTED = 505;
    public static final int SC_INSUFFICIENT_STORAGE = 507;

    public static String getStatusText(int statusCode) {
        if (statusCode < 0) {
            throw new IllegalArgumentException("status code may not be negative");
        }
        int classIndex = statusCode / 100;
        int codeIndex = statusCode - classIndex * 100;
        if (classIndex < 1 || classIndex > REASON_PHRASES.length - 1 || codeIndex < 0 || codeIndex > REASON_PHRASES[classIndex].length - 1) {
            return null;
        }
        return REASON_PHRASES[classIndex][codeIndex];
    }

    private static void addStatusCodeMap(int statusCode, String reasonPhrase) {
        int classIndex = statusCode / 100;
        HttpStatus.REASON_PHRASES[classIndex][statusCode - classIndex * 100] = reasonPhrase;
    }

    static {
        HttpStatus.addStatusCodeMap(200, "OK");
        HttpStatus.addStatusCodeMap(201, "Created");
        HttpStatus.addStatusCodeMap(202, "Accepted");
        HttpStatus.addStatusCodeMap(204, "No Content");
        HttpStatus.addStatusCodeMap(301, "Moved Permanently");
        HttpStatus.addStatusCodeMap(302, "Moved Temporarily");
        HttpStatus.addStatusCodeMap(304, "Not Modified");
        HttpStatus.addStatusCodeMap(400, "Bad Request");
        HttpStatus.addStatusCodeMap(401, "Unauthorized");
        HttpStatus.addStatusCodeMap(403, "Forbidden");
        HttpStatus.addStatusCodeMap(404, "Not Found");
        HttpStatus.addStatusCodeMap(500, "Internal Server Error");
        HttpStatus.addStatusCodeMap(501, "Not Implemented");
        HttpStatus.addStatusCodeMap(502, "Bad Gateway");
        HttpStatus.addStatusCodeMap(503, "Service Unavailable");
        HttpStatus.addStatusCodeMap(100, "Continue");
        HttpStatus.addStatusCodeMap(307, "Temporary Redirect");
        HttpStatus.addStatusCodeMap(405, "Method Not Allowed");
        HttpStatus.addStatusCodeMap(409, "Conflict");
        HttpStatus.addStatusCodeMap(412, "Precondition Failed");
        HttpStatus.addStatusCodeMap(413, "Request Too Long");
        HttpStatus.addStatusCodeMap(414, "Request-URI Too Long");
        HttpStatus.addStatusCodeMap(415, "Unsupported Media Type");
        HttpStatus.addStatusCodeMap(300, "Multiple Choices");
        HttpStatus.addStatusCodeMap(303, "See Other");
        HttpStatus.addStatusCodeMap(305, "Use Proxy");
        HttpStatus.addStatusCodeMap(402, "Payment Required");
        HttpStatus.addStatusCodeMap(406, "Not Acceptable");
        HttpStatus.addStatusCodeMap(407, "Proxy Authentication Required");
        HttpStatus.addStatusCodeMap(408, "Request Timeout");
        HttpStatus.addStatusCodeMap(101, "Switching Protocols");
        HttpStatus.addStatusCodeMap(203, "Non Authoritative Information");
        HttpStatus.addStatusCodeMap(205, "Reset Content");
        HttpStatus.addStatusCodeMap(206, "Partial Content");
        HttpStatus.addStatusCodeMap(504, "Gateway Timeout");
        HttpStatus.addStatusCodeMap(505, "Http Version Not Supported");
        HttpStatus.addStatusCodeMap(410, "Gone");
        HttpStatus.addStatusCodeMap(411, "Length Required");
        HttpStatus.addStatusCodeMap(416, "Requested Range Not Satisfiable");
        HttpStatus.addStatusCodeMap(417, "Expectation Failed");
        HttpStatus.addStatusCodeMap(102, "Processing");
        HttpStatus.addStatusCodeMap(207, "Multi-Status");
        HttpStatus.addStatusCodeMap(422, "Unprocessable Entity");
        HttpStatus.addStatusCodeMap(419, "Insufficient Space On Resource");
        HttpStatus.addStatusCodeMap(420, "Method Failure");
        HttpStatus.addStatusCodeMap(423, "Locked");
        HttpStatus.addStatusCodeMap(507, "Insufficient Storage");
        HttpStatus.addStatusCodeMap(424, "Failed Dependency");
    }
}

