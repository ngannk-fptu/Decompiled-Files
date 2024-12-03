/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.web.socket;

import java.io.Serializable;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public final class CloseStatus
implements Serializable {
    private static final long serialVersionUID = 5199057709285570947L;
    public static final CloseStatus NORMAL = new CloseStatus(1000);
    public static final CloseStatus GOING_AWAY = new CloseStatus(1001);
    public static final CloseStatus PROTOCOL_ERROR = new CloseStatus(1002);
    public static final CloseStatus NOT_ACCEPTABLE = new CloseStatus(1003);
    public static final CloseStatus NO_STATUS_CODE = new CloseStatus(1005);
    public static final CloseStatus NO_CLOSE_FRAME = new CloseStatus(1006);
    public static final CloseStatus BAD_DATA = new CloseStatus(1007);
    public static final CloseStatus POLICY_VIOLATION = new CloseStatus(1008);
    public static final CloseStatus TOO_BIG_TO_PROCESS = new CloseStatus(1009);
    public static final CloseStatus REQUIRED_EXTENSION = new CloseStatus(1010);
    public static final CloseStatus SERVER_ERROR = new CloseStatus(1011);
    public static final CloseStatus SERVICE_RESTARTED = new CloseStatus(1012);
    public static final CloseStatus SERVICE_OVERLOAD = new CloseStatus(1013);
    public static final CloseStatus TLS_HANDSHAKE_FAILURE = new CloseStatus(1015);
    public static final CloseStatus SESSION_NOT_RELIABLE = new CloseStatus(4500);
    private final int code;
    @Nullable
    private final String reason;

    public CloseStatus(int code) {
        this(code, null);
    }

    public CloseStatus(int code, @Nullable String reason) {
        Assert.isTrue((code >= 1000 && code < 5000 ? 1 : 0) != 0, () -> "Invalid status code: " + code);
        this.code = code;
        this.reason = reason;
    }

    public int getCode() {
        return this.code;
    }

    @Nullable
    public String getReason() {
        return this.reason;
    }

    public CloseStatus withReason(String reason) {
        Assert.hasText((String)reason, (String)"Reason must not be empty");
        return new CloseStatus(this.code, reason);
    }

    public boolean equalsCode(CloseStatus other) {
        return this.code == other.code;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CloseStatus)) {
            return false;
        }
        CloseStatus otherStatus = (CloseStatus)other;
        return this.code == otherStatus.code && ObjectUtils.nullSafeEquals((Object)this.reason, (Object)otherStatus.reason);
    }

    public int hashCode() {
        return this.code * 29 + ObjectUtils.nullSafeHashCode((Object)this.reason);
    }

    public String toString() {
        return "CloseStatus[code=" + this.code + ", reason=" + this.reason + "]";
    }
}

