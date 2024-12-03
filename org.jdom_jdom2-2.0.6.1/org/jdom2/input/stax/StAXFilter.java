/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.input.stax;

import org.jdom2.Namespace;

public interface StAXFilter {
    public boolean includeDocType();

    public boolean includeElement(int var1, String var2, Namespace var3);

    public String includeComment(int var1, String var2);

    public boolean includeEntityRef(int var1, String var2);

    public String includeCDATA(int var1, String var2);

    public String includeText(int var1, String var2);

    public boolean includeProcessingInstruction(int var1, String var2);

    public boolean pruneElement(int var1, String var2, Namespace var3);

    public String pruneComment(int var1, String var2);

    public boolean pruneEntityRef(int var1, String var2);

    public String pruneCDATA(int var1, String var2);

    public String pruneText(int var1, String var2);

    public boolean pruneProcessingInstruction(int var1, String var2);
}

