/*
 * Decompiled with CFR 0.152.
 */
package net.customware.confluence.plugin.toc;

import java.io.IOException;

public interface OutputHandler {
    public String appendStyle(Appendable var1) throws IOException;

    public void appendIncLevel(Appendable var1) throws IOException;

    public void appendDecLevel(Appendable var1) throws IOException;

    public void appendPrefix(Appendable var1) throws IOException;

    public void appendPostfix(Appendable var1) throws IOException;

    public void appendSeparator(Appendable var1) throws IOException;

    public void appendHeading(Appendable var1, String var2) throws IOException;
}

