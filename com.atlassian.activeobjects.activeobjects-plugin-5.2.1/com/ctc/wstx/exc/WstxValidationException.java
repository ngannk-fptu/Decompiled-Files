/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.exc;

import com.ctc.wstx.util.StringUtil;
import javax.xml.stream.Location;
import org.codehaus.stax2.validation.XMLValidationException;
import org.codehaus.stax2.validation.XMLValidationProblem;

public class WstxValidationException
extends XMLValidationException {
    private static final long serialVersionUID = 1L;

    protected WstxValidationException(XMLValidationProblem cause, String msg) {
        super(cause, msg);
    }

    protected WstxValidationException(XMLValidationProblem cause, String msg, Location loc) {
        super(cause, msg, loc);
    }

    public static WstxValidationException create(XMLValidationProblem cause) {
        Location loc = cause.getLocation();
        if (loc == null) {
            return new WstxValidationException(cause, cause.getMessage());
        }
        return new WstxValidationException(cause, cause.getMessage(), loc);
    }

    public String getMessage() {
        String locMsg = this.getLocationDesc();
        if (locMsg == null) {
            return super.getMessage();
        }
        String msg = this.getValidationProblem().getMessage();
        StringBuffer sb = new StringBuffer(msg.length() + locMsg.length() + 20);
        sb.append(msg);
        StringUtil.appendLF(sb);
        sb.append(" at ");
        sb.append(locMsg);
        return sb.toString();
    }

    public String toString() {
        return this.getClass().getName() + ": " + this.getMessage();
    }

    protected String getLocationDesc() {
        Location loc = this.getLocation();
        return loc == null ? null : loc.toString();
    }
}

