/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.runtime.output;

interface OutputBuffer {
    public String close();

    public OutputBuffer append(char var1);

    public OutputBuffer append(String var1);

    public OutputBuffer append(char[] var1, int var2, int var3);
}

