/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 *  org.apache.tools.ant.Task
 *  org.eclipse.jdt.core.ToolFactory
 *  org.eclipse.jdt.core.util.IClassFileReader
 *  org.eclipse.jdt.core.util.ICodeAttribute
 *  org.eclipse.jdt.core.util.IMethodInfo
 */
package org.eclipse.jdt.core;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.util.IClassFileReader;
import org.eclipse.jdt.core.util.ICodeAttribute;
import org.eclipse.jdt.core.util.IMethodInfo;
import org.eclipse.jdt.internal.antadapter.AntAdapterMessages;
import org.eclipse.jdt.internal.compiler.util.Util;

public final class CheckDebugAttributes
extends Task {
    private String file;
    private String property;

    public void execute() throws BuildException {
        if (this.file == null) {
            throw new BuildException(AntAdapterMessages.getString("checkDebugAttributes.file.argument.cannot.be.null"));
        }
        if (this.property == null) {
            throw new BuildException(AntAdapterMessages.getString("checkDebugAttributes.property.argument.cannot.be.null"));
        }
        try {
            boolean hasDebugAttributes = false;
            if (Util.isClassFileName(this.file)) {
                IClassFileReader classFileReader = ToolFactory.createDefaultClassFileReader((String)this.file, (int)65535);
                hasDebugAttributes = this.checkClassFile(classFileReader);
            } else {
                try (ZipFile jarFile = null;){
                    try {
                        jarFile = new ZipFile(this.file);
                    }
                    catch (ZipException e) {
                        throw new BuildException(AntAdapterMessages.getString("checkDebugAttributes.file.argument.must.be.a.classfile.or.a.jarfile"), (Throwable)e);
                    }
                }
                Enumeration<? extends ZipEntry> entries = jarFile.entries();
                while (!hasDebugAttributes && entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (!Util.isClassFileName(entry.getName())) continue;
                    IClassFileReader classFileReader = ToolFactory.createDefaultClassFileReader((String)this.file, (String)entry.getName(), (int)65535);
                    hasDebugAttributes = this.checkClassFile(classFileReader);
                }
            }
            if (hasDebugAttributes) {
                this.getProject().setUserProperty(this.property, "has debug");
            }
        }
        catch (IOException e) {
            throw new BuildException(String.valueOf(AntAdapterMessages.getString("checkDebugAttributes.ioexception.occured")) + this.file, (Throwable)e);
        }
    }

    private boolean checkClassFile(IClassFileReader classFileReader) {
        IMethodInfo[] methodInfos = classFileReader.getMethodInfos();
        int i = 0;
        int max = methodInfos.length;
        while (i < max) {
            ICodeAttribute codeAttribute = methodInfos[i].getCodeAttribute();
            if (codeAttribute != null && codeAttribute.getLineNumberAttribute() != null) {
                return true;
            }
            ++i;
        }
        return false;
    }

    public void setFile(String value) {
        this.file = value;
    }

    public void setProperty(String value) {
        this.property = value;
    }
}

