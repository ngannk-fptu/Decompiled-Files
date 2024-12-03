/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.Null;
import com.atlassian.security.auth.trustedapps.StringUtil;
import com.atlassian.security.auth.trustedapps.TrustedApplicationUtils;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class TransportErrorMessage {
    static final Parser PARSER = new Parser();
    private final Code code;
    private final String message;
    private final String[] params;

    TransportErrorMessage(Code code, String message, String[] params) {
        Null.not("code", code);
        Null.not("params", params);
        Null.not("message", message);
        for (int i = 0; i < params.length; ++i) {
            Null.not(String.valueOf(i), params[i]);
        }
        this.code = code;
        this.message = message;
        this.params = params;
    }

    TransportErrorMessage(Code code, String message) {
        this(code, message, new String[0]);
    }

    TransportErrorMessage(Code code, String message, String param) {
        this(code, message, new String[]{param});
    }

    TransportErrorMessage(Code code, String message, String one, String two) {
        this(code, message, new String[]{one, two});
    }

    TransportErrorMessage(Code code, String message, String one, String two, String three) {
        this(code, message, new String[]{one, two, three});
    }

    public Code getCode() {
        return this.code;
    }

    public String[] getParameters() {
        return (String[])this.params.clone();
    }

    public String getFormattedMessage() {
        return MessageFormat.format(this.message, this.params);
    }

    public String toString() {
        return PARSER.toString(this);
    }

    public static final class Code {
        private static final Map<String, Code> ALL = new HashMap<String, Code>();
        public static final Code UNKNOWN = new Code(Severity.ERROR, "UNKNOWN");
        public static final Code APP_UNKNOWN = new Code(Severity.ERROR, "APP_UNKNOWN");
        public static final Code SYSTEM = new Code(Severity.ERROR, "SYSTEM");
        public static final Code BAD_PROTOCOL_VERSION = new Code(Severity.ERROR, "BAD_PROTOCOL_VERSION");
        public static final Code APP_ID_NOT_FOUND = new Code(Severity.ERROR, "APP_ID_NOT_FOUND");
        public static final Code SECRET_KEY_NOT_FOUND = new Code(Severity.ERROR, "SECRET_KEY_NOT_FOUND");
        public static final Code MAGIC_NUMBER_NOT_FOUND = new Code(Severity.ERROR, "MAGIC_NUMBER_NOT_FOUND");
        public static final Code BAD_REMOTE_IP = new Code(Severity.FAIL, "BAD_REMOTE_IP");
        public static final Code BAD_XFORWARD_IP = new Code(Severity.FAIL, "BAD_XFORWARD_IP");
        public static final Code BAD_URL = new Code(Severity.FAIL, "BAD_URL");
        public static final Code OLD_CERT = new Code(Severity.FAIL, "OLD_CERT");
        public static final Code MISSING_CERT = new Code(Severity.FAIL, "MISSING_CERT");
        public static final Code BAD_MAGIC = new Code(Severity.FAIL, "BAD_MAGIC");
        public static final Code USER_UNKNOWN = new Code(Severity.ERROR, "USER_UNKNOWN");
        public static final Code PERMISSION_DENIED = new Code(Severity.ERROR, "PERMISSION_DENIED");
        public static final Code BAD_SIGNATURE = new Code(Severity.FAIL, "BAD_SIGNATURE");
        private final Severity severity;
        private final String code;

        static Code get(String code) {
            Code result = ALL.get(code);
            return result == null ? UNKNOWN : result;
        }

        private Code(Severity severity, String code) {
            Null.not("severity", severity);
            Null.not("code", code);
            this.severity = severity;
            this.code = code;
            if (ALL.containsKey(code)) {
                throw new IllegalArgumentException(code + " is already mapped as a " + this.getClass().getName());
            }
            ALL.put(code, this);
        }

        public Severity getSeverity() {
            return this.severity;
        }

        public String getCode() {
            return this.code;
        }

        public static final class Severity {
            static final Severity ERROR = new Severity("ERROR");
            static final Severity FAIL = new Severity("FAIL");
            private final String name;

            private Severity(String name) {
                this.name = name;
            }

            public String toString() {
                return this.name;
            }
        }
    }

    static class Parser {
        static final String SEPARATOR = ";\t";

        Parser() {
        }

        TransportErrorMessage parse(String inputItring) throws IllegalArgumentException {
            Null.not("inputString", inputItring);
            String[] args = inputItring.split(SEPARATOR);
            if (args.length != 3) {
                throw new IllegalArgumentException("Cannot split message into Code, Message, Parameters:" + inputItring);
            }
            Code code = Code.get(args[0]);
            String[] params = StringUtil.split(args[2]);
            return new TransportErrorMessage(code, args[1], params);
        }

        String toString(TransportErrorMessage msg) {
            return new StringBuffer(msg.code.getCode()).append(SEPARATOR).append(msg.message).append(SEPARATOR).append(StringUtil.toString(msg.params)).toString();
        }
    }

    public static class BadSignature
    extends TransportErrorMessage {
        public BadSignature(String url) {
            super(Code.BAD_SIGNATURE, "Bad signature for URL: {0}", url);
        }

        public BadSignature() {
            super(Code.BAD_SIGNATURE, "Missing signature in a v2 request");
        }
    }

    public static class PermissionDenied
    extends TransportErrorMessage {
        public PermissionDenied() {
            super(Code.PERMISSION_DENIED, "Permission Denied");
        }
    }

    public static class UserUnknown
    extends TransportErrorMessage {
        public UserUnknown(String userName) {
            super(Code.USER_UNKNOWN, "Unknown User: {0}", userName);
        }
    }

    public static class ApplicationUnknown
    extends TransportErrorMessage {
        public ApplicationUnknown(String appId) {
            super(Code.APP_UNKNOWN, "Unknown Application: {0}", appId);
        }
    }

    public static class MagicNumberNotFoundInRequest
    extends TransportErrorMessage {
        public MagicNumberNotFoundInRequest() {
            super(Code.MAGIC_NUMBER_NOT_FOUND, "Magic Number not found in request");
        }
    }

    public static class SecretKeyNotFoundInRequest
    extends TransportErrorMessage {
        public SecretKeyNotFoundInRequest() {
            super(Code.SECRET_KEY_NOT_FOUND, "Secret Key not found in request");
        }
    }

    public static class ApplicationIdNotFoundInRequest
    extends TransportErrorMessage {
        public ApplicationIdNotFoundInRequest() {
            super(Code.APP_ID_NOT_FOUND, "Application ID not found in request");
        }
    }

    public static class UnSupportedProtocolVersion
    extends TransportErrorMessage {
        public UnSupportedProtocolVersion(Integer protocolVersion) {
            super(Code.BAD_PROTOCOL_VERSION, "Unsupported protocol version: {0}. required {1}", "" + protocolVersion, "" + TrustedApplicationUtils.getProtocolVersionInUse());
        }
    }

    public static class BadProtocolVersion
    extends TransportErrorMessage {
        public BadProtocolVersion(String versionString) {
            super(Code.BAD_PROTOCOL_VERSION, "Bad protocol version: {0}", versionString);
        }
    }

    static class BadMagicNumber
    extends TransportErrorMessage {
        public BadMagicNumber(String keyName, String appId) {
            super(Code.BAD_MAGIC, "Unable to decrypt certificate {0} for application {1}", keyName, appId);
        }
    }

    static class System
    extends TransportErrorMessage {
        System(Throwable cause, String appId) {
            super(Code.SYSTEM, "Exception: {0} occurred serving request for application: {1}", cause.toString(), appId);
        }
    }
}

