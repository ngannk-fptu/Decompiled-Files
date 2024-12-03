/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.plugins.rest.common.error.jersey;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="status")
public class UncaughtExceptionEntity {
    private static final Integer INTERNAL_SERVER_ERROR_CODE = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
    @XmlElement(name="status-code")
    private final Integer code = INTERNAL_SERVER_ERROR_CODE;
    @XmlElement
    private final String message;
    @XmlElement(name="stack-trace")
    private final String stackTrace;
    private static final MediaType TEXT_PLAIN_UTF8_TYPE = MediaType.valueOf("text/plain; charset=utf-8");
    private static final List<Variant> POSSIBLE_VARIANTS = Variant.mediaTypes(MediaType.APPLICATION_XML_TYPE, MediaType.APPLICATION_JSON_TYPE, MediaType.TEXT_PLAIN_TYPE).add().build();

    public UncaughtExceptionEntity() {
        this.message = null;
        this.stackTrace = null;
    }

    public UncaughtExceptionEntity(Throwable t, String errorId) {
        this.message = t.getMessage();
        boolean shouldSeeStacktrace = System.getProperty("atlassian.rest.response.stacktraces", "0").equals("1");
        if (shouldSeeStacktrace) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            this.stackTrace = sw.toString();
        } else {
            this.stackTrace = "Please contact your admin passing attached Log''s referral number: " + errorId;
        }
    }

    public String getMessage() {
        return this.message;
    }

    public String getStackTrace() {
        return this.stackTrace;
    }

    public static MediaType variantFor(Request request) {
        MediaType t;
        Variant v = request.selectVariant(POSSIBLE_VARIANTS);
        if (v == null) {
            v = POSSIBLE_VARIANTS.get(0);
        }
        if ((t = v.getMediaType()).equals(MediaType.TEXT_PLAIN_TYPE)) {
            return TEXT_PLAIN_UTF8_TYPE;
        }
        return t;
    }

    public String toString() {
        return "code=" + this.code + ", message='" + this.message + '\'' + ", stackTrace='" + this.stackTrace;
    }
}

