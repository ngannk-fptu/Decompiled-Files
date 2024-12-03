/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.bridge;

import java.io.File;
import java.io.Serializable;
import org.aspectj.bridge.SourceLocation;

public interface ISourceLocation
extends Serializable {
    public static final int MAX_LINE = 0x3FFFFFFF;
    public static final int MAX_COLUMN = 0x3FFFFFFF;
    public static final File NO_FILE = new File("ISourceLocation.NO_FILE");
    public static final int NO_COLUMN = -2147483647;
    public static final ISourceLocation EMPTY = new SourceLocation(NO_FILE, 0, 0, 0);

    public File getSourceFile();

    public int getLine();

    public int getColumn();

    public int getOffset();

    public int getEndLine();

    public String getContext();

    public String getSourceFileName();
}

