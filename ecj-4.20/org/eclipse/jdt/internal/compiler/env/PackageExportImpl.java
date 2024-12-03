/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.env;

import org.eclipse.jdt.internal.compiler.env.IModule;

public class PackageExportImpl
implements IModule.IPackageExport {
    public char[] pack;
    public char[][] exportedTo;

    @Override
    public char[] name() {
        return this.pack;
    }

    @Override
    public char[][] targets() {
        return this.exportedTo;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.pack);
        buffer.append(" to ");
        if (this.exportedTo != null) {
            int i = 0;
            while (i < this.exportedTo.length) {
                if (i > 0) {
                    buffer.append(", ");
                }
                char[] cs = this.exportedTo[i];
                buffer.append(cs);
                ++i;
            }
        }
        buffer.append(';');
        return buffer.toString();
    }
}

