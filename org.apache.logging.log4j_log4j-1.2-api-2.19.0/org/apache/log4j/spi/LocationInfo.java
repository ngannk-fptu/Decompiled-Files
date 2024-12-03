/*
 * Decompiled with CFR 0.152.
 */
package org.apache.log4j.spi;

import java.io.Serializable;
import java.util.Objects;

public class LocationInfo
implements Serializable {
    public static final String NA = "?";
    static final long serialVersionUID = -1325822038990805636L;
    private final StackTraceElement stackTraceElement;
    public String fullInfo;

    public LocationInfo(StackTraceElement stackTraceElement) {
        this.stackTraceElement = Objects.requireNonNull(stackTraceElement, "stackTraceElement");
        this.fullInfo = stackTraceElement.toString();
    }

    public LocationInfo(String file, String declaringClass, String methodName, String line) {
        this(new StackTraceElement(declaringClass, methodName, file, Integer.parseInt(line)));
    }

    public LocationInfo(Throwable throwable, String fqnOfCallingClass) {
        String declaringClass = null;
        String methodName = null;
        String file = null;
        String line = null;
        if (throwable != null && fqnOfCallingClass != null) {
            StackTraceElement[] elements = throwable.getStackTrace();
            String prevClass = NA;
            for (int i = elements.length - 1; i >= 0; --i) {
                String thisClass = elements[i].getClassName();
                if (fqnOfCallingClass.equals(thisClass)) {
                    int lineNo;
                    int caller = i + 1;
                    if (caller >= elements.length) break;
                    declaringClass = prevClass;
                    methodName = elements[caller].getMethodName();
                    file = elements[caller].getFileName();
                    if (file == null) {
                        file = NA;
                    }
                    line = (lineNo = elements[caller].getLineNumber()) < 0 ? NA : String.valueOf(lineNo);
                    StringBuilder builder = new StringBuilder();
                    builder.append(declaringClass);
                    builder.append(".");
                    builder.append(methodName);
                    builder.append("(");
                    builder.append(file);
                    builder.append(":");
                    builder.append(line);
                    builder.append(")");
                    this.fullInfo = builder.toString();
                    break;
                }
                prevClass = thisClass;
            }
        }
        this.stackTraceElement = new StackTraceElement(declaringClass, methodName, file, Integer.parseInt(line));
        this.fullInfo = this.stackTraceElement.toString();
    }

    public String getClassName() {
        return this.stackTraceElement.getClassName();
    }

    public String getFileName() {
        return this.stackTraceElement.getFileName();
    }

    public String getLineNumber() {
        return Integer.toString(this.stackTraceElement.getLineNumber());
    }

    public String getMethodName() {
        return this.stackTraceElement.getMethodName();
    }
}

