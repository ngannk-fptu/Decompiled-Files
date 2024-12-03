/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.condition;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.util.StreamUtils;
import org.apache.tools.zip.ZipFile;

public class IsSigned
extends DataType
implements Condition {
    private static final String SIG_START = "META-INF/";
    private static final String SIG_END = ".SF";
    private static final int SHORT_SIG_LIMIT = 8;
    private String name;
    private File file;

    public void setFile(File file) {
        this.file = file;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static boolean isSigned(File zipFile, String name) throws IOException {
        try (ZipFile jarFile = new ZipFile(zipFile);){
            if (null == name) {
                boolean bl = StreamUtils.enumerationAsStream(jarFile.getEntries()).anyMatch(e -> e.getName().startsWith(SIG_START) && e.getName().endsWith(SIG_END));
                return bl;
            }
            name = IsSigned.replaceInvalidChars(name);
            boolean shortSig = jarFile.getEntry(SIG_START + name.toUpperCase() + SIG_END) != null;
            boolean longSig = false;
            if (name.length() > 8) {
                longSig = jarFile.getEntry(SIG_START + name.substring(0, 8).toUpperCase() + SIG_END) != null;
            }
            boolean bl = shortSig || longSig;
            return bl;
        }
    }

    @Override
    public boolean eval() {
        if (this.file == null) {
            throw new BuildException("The file attribute must be set.");
        }
        if (!this.file.exists()) {
            this.log("The file \"" + this.file.getAbsolutePath() + "\" does not exist.", 3);
            return false;
        }
        boolean r = false;
        try {
            r = IsSigned.isSigned(this.file, this.name);
        }
        catch (IOException e) {
            this.log("Got IOException reading file \"" + this.file.getAbsolutePath() + "\"" + e, 1);
        }
        if (r) {
            this.log("File \"" + this.file.getAbsolutePath() + "\" is signed.", 3);
        }
        return r;
    }

    private static String replaceInvalidChars(String name) {
        StringBuilder sb = new StringBuilder();
        boolean changes = false;
        for (char ch : name.toCharArray()) {
            if ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_".indexOf(ch) < 0) {
                sb.append("_");
                changes = true;
                continue;
            }
            sb.append(ch);
        }
        return changes ? sb.toString() : name;
    }
}

