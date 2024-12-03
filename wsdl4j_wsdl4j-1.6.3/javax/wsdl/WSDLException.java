/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl;

public class WSDLException
extends Exception {
    public static final long serialVersionUID = 1L;
    public static final String INVALID_WSDL = "INVALID_WSDL";
    public static final String PARSER_ERROR = "PARSER_ERROR";
    public static final String OTHER_ERROR = "OTHER_ERROR";
    public static final String CONFIGURATION_ERROR = "CONFIGURATION_ERROR";
    public static final String UNBOUND_PREFIX = "UNBOUND_PREFIX";
    public static final String NO_PREFIX_SPECIFIED = "NO_PREFIX_SPECIFIED";
    private String faultCode = null;
    private Throwable targetThrowable = null;
    private String location = null;

    public WSDLException(String faultCode, String msg, Throwable t) {
        super(msg, t);
        this.setFaultCode(faultCode);
    }

    public WSDLException(String faultCode, String msg) {
        this(faultCode, msg, null);
    }

    public void setFaultCode(String faultCode) {
        this.faultCode = faultCode;
    }

    public String getFaultCode() {
        return this.faultCode;
    }

    public void setTargetException(Throwable targetThrowable) {
        this.targetThrowable = targetThrowable;
    }

    public Throwable getTargetException() {
        if (this.targetThrowable == null) {
            return this.getCause();
        }
        return this.targetThrowable;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return this.location;
    }

    public String getMessage() {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("WSDLException");
        if (this.location != null) {
            try {
                strBuf.append(" (at " + this.location + ")");
            }
            catch (IllegalArgumentException e) {
                // empty catch block
            }
        }
        if (this.faultCode != null) {
            strBuf.append(": faultCode=" + this.faultCode);
        }
        String thisMsg = super.getMessage();
        String targetMsg = null;
        String targetName = null;
        if (this.getTargetException() != null) {
            targetMsg = this.getTargetException().getMessage();
            targetName = this.getTargetException().getClass().getName();
        }
        if (!(thisMsg == null || targetMsg != null && thisMsg.equals(targetMsg))) {
            strBuf.append(": " + thisMsg);
        }
        if (targetName != null) {
            strBuf.append(": " + targetName);
        }
        if (targetMsg != null) {
            strBuf.append(": " + targetMsg);
        }
        return strBuf.toString();
    }
}

