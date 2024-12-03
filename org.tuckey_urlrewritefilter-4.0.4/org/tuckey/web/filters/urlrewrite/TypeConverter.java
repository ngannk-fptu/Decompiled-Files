/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite;

import org.tuckey.web.filters.urlrewrite.utils.StringUtils;

public class TypeConverter {
    protected short type;
    protected String error = null;
    public static final short TYPE_TIME = 4;
    public static final short TYPE_TIME_YEAR = 5;
    public static final short TYPE_TIME_MONTH = 6;
    public static final short TYPE_TIME_DAY_OF_MONTH = 7;
    public static final short TYPE_TIME_DAY_OF_WEEK = 8;
    public static final short TYPE_TIME_AMPM = 9;
    public static final short TYPE_TIME_HOUR_OF_DAY = 10;
    public static final short TYPE_TIME_MINUTE = 11;
    public static final short TYPE_TIME_SECOND = 12;
    public static final short TYPE_TIME_MILLISECOND = 13;
    public static final short TYPE_ATTRIBUTE = 14;
    public static final short TYPE_AUTH_TYPE = 15;
    public static final short TYPE_CHARACTER_ENCODING = 16;
    public static final short TYPE_CONTENT_LENGTH = 17;
    public static final short TYPE_CONTENT_TYPE = 18;
    public static final short TYPE_CONTEXT_PATH = 19;
    public static final short TYPE_COOKIE = 20;
    public static final short TYPE_HEADER = 1;
    public static final short TYPE_LOCAL_PORT = 39;
    public static final short TYPE_METHOD = 21;
    public static final short TYPE_PARAMETER = 22;
    public static final short TYPE_PATH_INFO = 23;
    public static final short TYPE_PATH_TRANSLATED = 24;
    public static final short TYPE_PROTOCOL = 25;
    public static final short TYPE_QUERY_STRING = 26;
    public static final short TYPE_REMOTE_ADDR = 27;
    public static final short TYPE_REMOTE_HOST = 28;
    public static final short TYPE_REMOTE_USER = 29;
    public static final short TYPE_REQUESTED_SESSION_ID = 30;
    public static final short TYPE_REQUEST_URI = 31;
    public static final short TYPE_REQUEST_URL = 32;
    public static final short TYPE_SESSION_ATTRIBUTE = 33;
    public static final short TYPE_SESSION_IS_NEW = 34;
    public static final short TYPE_SERVER_PORT = 35;
    public static final short TYPE_SERVER_NAME = 36;
    public static final short TYPE_SCHEME = 37;
    public static final short TYPE_USER_IN_ROLE = 38;
    public static final short TYPE_EXCEPTION = 40;
    public static final short TYPE_REQUESTED_SESSION_ID_FROM_COOKIE = 41;
    public static final short TYPE_REQUESTED_SESSION_ID_FROM_URL = 42;
    public static final short TYPE_REQUESTED_SESSION_ID_VALID = 43;
    public static final short TYPE_REQUEST_FILENAME = 44;
    public static final short TYPE_SERVLET_CONTEXT = 45;

    public String getType() {
        switch (this.type) {
            case 4: {
                return "time";
            }
            case 5: {
                return "year";
            }
            case 6: {
                return "month";
            }
            case 7: {
                return "dayofmonth";
            }
            case 8: {
                return "dayofweek";
            }
            case 9: {
                return "ampm";
            }
            case 10: {
                return "hourofday";
            }
            case 11: {
                return "minute";
            }
            case 12: {
                return "second";
            }
            case 13: {
                return "millisecond";
            }
            case 14: {
                return "attribute";
            }
            case 15: {
                return "auth-type";
            }
            case 16: {
                return "character-encoding";
            }
            case 17: {
                return "content-length";
            }
            case 18: {
                return "content-type";
            }
            case 19: {
                return "context-path";
            }
            case 20: {
                return "cookie";
            }
            case 1: {
                return "header";
            }
            case 39: {
                return "local-port";
            }
            case 21: {
                return "method";
            }
            case 22: {
                return "parameter";
            }
            case 23: {
                return "path-info";
            }
            case 24: {
                return "path-translated";
            }
            case 25: {
                return "protocol";
            }
            case 26: {
                return "query-string";
            }
            case 27: {
                return "remote-addr";
            }
            case 28: {
                return "remote-host";
            }
            case 29: {
                return "remote-user";
            }
            case 30: {
                return "requested-session-id";
            }
            case 41: {
                return "requested-session-id-from-cookie";
            }
            case 42: {
                return "requested-session-id-from-url";
            }
            case 43: {
                return "requested-session-id-valid";
            }
            case 31: {
                return "request-uri";
            }
            case 32: {
                return "request-url";
            }
            case 33: {
                return "session-attribute";
            }
            case 34: {
                return "session-isnew";
            }
            case 35: {
                return "port";
            }
            case 36: {
                return "server-name";
            }
            case 37: {
                return "scheme";
            }
            case 38: {
                return "user-in-role";
            }
            case 40: {
                return "exception";
            }
            case 44: {
                return "request-filename";
            }
            case 45: {
                return "context";
            }
        }
        return "";
    }

    public void setType(String strType) {
        if ("time".equals(strType)) {
            this.type = (short)4;
        } else if ("year".equals(strType)) {
            this.type = (short)5;
        } else if ("month".equals(strType)) {
            this.type = (short)6;
        } else if ("dayofmonth".equals(strType)) {
            this.type = (short)7;
        } else if ("dayofweek".equals(strType)) {
            this.type = (short)8;
        } else if ("ampm".equals(strType)) {
            this.type = (short)9;
        } else if ("hourofday".equals(strType)) {
            this.type = (short)10;
        } else if ("minute".equals(strType)) {
            this.type = (short)11;
        } else if ("second".equals(strType)) {
            this.type = (short)12;
        } else if ("millisecond".equals(strType)) {
            this.type = (short)13;
        } else if ("attribute".equals(strType)) {
            this.type = (short)14;
        } else if ("auth-type".equals(strType)) {
            this.type = (short)15;
        } else if ("character-encoding".equals(strType)) {
            this.type = (short)16;
        } else if ("content-length".equals(strType)) {
            this.type = (short)17;
        } else if ("content-type".equals(strType)) {
            this.type = (short)18;
        } else if ("context-path".equals(strType)) {
            this.type = (short)19;
        } else if ("cookie".equals(strType)) {
            this.type = (short)20;
        } else if ("header".equals(strType) || StringUtils.isBlank(strType)) {
            this.type = 1;
        } else if ("local-port".equals(strType)) {
            this.type = (short)39;
        } else if ("method".equals(strType)) {
            this.type = (short)21;
        } else if ("parameter".equals(strType) || "param".equals(strType)) {
            this.type = (short)22;
        } else if ("path-info".equals(strType)) {
            this.type = (short)23;
        } else if ("path-translated".equals(strType)) {
            this.type = (short)24;
        } else if ("protocol".equals(strType)) {
            this.type = (short)25;
        } else if ("query-string".equals(strType)) {
            this.type = (short)26;
        } else if ("remote-addr".equals(strType)) {
            this.type = (short)27;
        } else if ("remote-host".equals(strType)) {
            this.type = (short)28;
        } else if ("remote-user".equals(strType)) {
            this.type = (short)29;
        } else if ("requested-session-id".equals(strType)) {
            this.type = (short)30;
        } else if ("requested-session-id-from-cookie".equals(strType)) {
            this.type = (short)41;
        } else if ("requested-session-id-from-url".equals(strType)) {
            this.type = (short)42;
        } else if ("requested-session-id-valid".equals(strType)) {
            this.type = (short)43;
        } else if ("request-uri".equals(strType)) {
            this.type = (short)31;
        } else if ("request-url".equals(strType)) {
            this.type = (short)32;
        } else if ("session-attribute".equals(strType)) {
            this.type = (short)33;
        } else if ("session-isnew".equals(strType)) {
            this.type = (short)34;
        } else if ("port".equals(strType)) {
            this.type = (short)35;
        } else if ("server-name".equals(strType)) {
            this.type = (short)36;
        } else if ("scheme".equals(strType)) {
            this.type = (short)37;
        } else if ("user-in-role".equals(strType)) {
            this.type = (short)38;
        } else if ("exception".equals(strType)) {
            this.type = (short)40;
        } else if ("request-filename".equals(strType)) {
            this.type = (short)44;
        } else if ("context".equals(strType)) {
            this.type = (short)45;
        } else {
            this.setError("Type " + strType + " is not valid");
        }
    }

    public final String getError() {
        return this.error;
    }

    protected void setError(String error) {
        this.error = error;
    }

    public int getTypeShort() {
        return this.type;
    }
}

