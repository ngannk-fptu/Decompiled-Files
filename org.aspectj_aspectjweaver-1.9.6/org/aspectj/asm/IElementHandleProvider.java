/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.asm;

import java.io.File;
import org.aspectj.asm.IProgramElement;
import org.aspectj.bridge.ISourceLocation;

public interface IElementHandleProvider {
    public String createHandleIdentifier(ISourceLocation var1);

    public String createHandleIdentifier(File var1, int var2, int var3, int var4);

    public String createHandleIdentifier(IProgramElement var1);

    public String getFileForHandle(String var1);

    public int getLineNumberForHandle(String var1);

    public int getOffSetForHandle(String var1);

    public void initialize();
}

