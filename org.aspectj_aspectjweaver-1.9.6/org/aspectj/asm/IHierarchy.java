/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.asm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.aspectj.asm.IProgramElement;
import org.aspectj.asm.internal.ProgramElement;
import org.aspectj.bridge.ISourceLocation;

public interface IHierarchy
extends Serializable {
    public static final IProgramElement NO_STRUCTURE = new ProgramElement(null, "<build to view structure>", IProgramElement.Kind.ERROR, null);

    public IProgramElement getElement(String var1);

    public IProgramElement getRoot();

    public void setRoot(IProgramElement var1);

    public void addToFileMap(String var1, IProgramElement var2);

    public boolean removeFromFileMap(String var1);

    public void setFileMap(HashMap<String, IProgramElement> var1);

    public Object findInFileMap(Object var1);

    public Set<Map.Entry<String, IProgramElement>> getFileMapEntrySet();

    public boolean isValid();

    public IProgramElement findElementForHandle(String var1);

    public IProgramElement findElementForHandleOrCreate(String var1, boolean var2);

    public IProgramElement findElementForSignature(IProgramElement var1, IProgramElement.Kind var2, String var3);

    public IProgramElement findElementForLabel(IProgramElement var1, IProgramElement.Kind var2, String var3);

    public IProgramElement findElementForType(String var1, String var2);

    public IProgramElement findElementForSourceFile(String var1);

    public IProgramElement findElementForSourceLine(ISourceLocation var1);

    public IProgramElement findElementForSourceLine(String var1, int var2);

    public IProgramElement findElementForOffSet(String var1, int var2, int var3);

    public String getConfigFile();

    public void setConfigFile(String var1);

    public void flushTypeMap();

    public void flushHandleMap();

    public void updateHandleMap(Set<String> var1);

    public IProgramElement findCloserMatchForLineNumber(IProgramElement var1, int var2);

    public IProgramElement findNodeForSourceFile(IProgramElement var1, String var2);
}

