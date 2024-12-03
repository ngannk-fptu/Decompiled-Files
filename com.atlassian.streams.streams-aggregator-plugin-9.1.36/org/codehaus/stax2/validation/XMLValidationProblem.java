/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.validation;

import javax.xml.stream.Location;
import org.codehaus.stax2.validation.XMLValidationException;
import org.codehaus.stax2.validation.XMLValidator;

public class XMLValidationProblem {
    public static final int SEVERITY_WARNING = 1;
    public static final int SEVERITY_ERROR = 2;
    public static final int SEVERITY_FATAL = 3;
    protected Location mLocation;
    protected final String mMessage;
    protected final int mSeverity;
    protected String mType;
    protected XMLValidator mReporter;

    public XMLValidationProblem(Location location, String string) {
        this(location, string, 2);
    }

    public XMLValidationProblem(Location location, String string, int n) {
        this(location, string, n, null);
    }

    public XMLValidationProblem(Location location, String string, int n, String string2) {
        this.mLocation = location;
        this.mMessage = string;
        this.mSeverity = n;
        this.mType = string2;
    }

    public XMLValidationException toException() {
        return XMLValidationException.createException(this);
    }

    public void setType(String string) {
        this.mType = string;
    }

    public void setLocation(Location location) {
        this.mLocation = location;
    }

    public void setReporter(XMLValidator xMLValidator) {
        this.mReporter = xMLValidator;
    }

    public Location getLocation() {
        return this.mLocation;
    }

    public String getMessage() {
        return this.mMessage;
    }

    public int getSeverity() {
        return this.mSeverity;
    }

    public String getType() {
        return this.mType;
    }

    public XMLValidator getReporter() {
        return this.mReporter;
    }
}

