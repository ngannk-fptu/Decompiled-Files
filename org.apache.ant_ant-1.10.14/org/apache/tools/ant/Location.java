/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import java.io.Serializable;
import java.util.Objects;
import org.apache.tools.ant.util.FileUtils;
import org.xml.sax.Locator;

public class Location
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String fileName;
    private final int lineNumber;
    private final int columnNumber;
    public static final Location UNKNOWN_LOCATION = new Location();
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();

    private Location() {
        this(null, 0, 0);
    }

    public Location(String fileName) {
        this(fileName, 0, 0);
    }

    public Location(Locator loc) {
        this(loc.getSystemId(), loc.getLineNumber(), loc.getColumnNumber());
    }

    public Location(String fileName, int lineNumber, int columnNumber) {
        this.fileName = fileName != null && fileName.startsWith("file:") ? FILE_UTILS.fromURI(fileName) : fileName;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    public String getFileName() {
        return this.fileName;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public int getColumnNumber() {
        return this.columnNumber;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (this.fileName != null) {
            buf.append(this.fileName);
            if (this.lineNumber != 0) {
                buf.append(":");
                buf.append(this.lineNumber);
            }
            buf.append(": ");
        }
        return buf.toString();
    }

    public boolean equals(Object other) {
        return this == other || other != null && other.getClass() == this.getClass() && this.toString().equals(other.toString());
    }

    public int hashCode() {
        return Objects.hash(this.fileName, this.lineNumber);
    }
}

