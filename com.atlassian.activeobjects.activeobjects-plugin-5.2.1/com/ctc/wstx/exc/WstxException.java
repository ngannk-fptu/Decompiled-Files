/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.exc;

import com.ctc.wstx.util.ExceptionUtil;
import com.ctc.wstx.util.StringUtil;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

public class WstxException
extends XMLStreamException {
    private static final long serialVersionUID = 1L;
    protected final String mMsg;

    public WstxException(String msg) {
        super(msg);
        this.mMsg = msg;
    }

    public WstxException(Throwable th) {
        super(th.getMessage(), th);
        this.mMsg = th.getMessage();
        ExceptionUtil.setInitCause(this, th);
    }

    public WstxException(String msg, Location loc) {
        super(msg, loc);
        this.mMsg = msg;
    }

    public WstxException(String msg, Location loc, Throwable th) {
        super(msg, loc, th);
        this.mMsg = msg;
        ExceptionUtil.setInitCause(this, th);
    }

    public String getMessage() {
        String locMsg = this.getLocationDesc();
        if (locMsg == null) {
            return super.getMessage();
        }
        StringBuffer sb = new StringBuffer(this.mMsg.length() + locMsg.length() + 20);
        sb.append(this.mMsg);
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

