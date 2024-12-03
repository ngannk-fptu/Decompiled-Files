/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.FileUtils;

public class CVSPass
extends Task {
    private String cvsRoot = null;
    private File passFile = null;
    private String password = null;
    private final char[] shifts = new char[]{'\u0000', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\b', '\t', '\n', '\u000b', '\f', '\r', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u001a', '\u001b', '\u001c', '\u001d', '\u001e', '\u001f', 'r', 'x', '5', 'O', '`', 'm', 'H', 'l', 'F', '@', 'L', 'C', 't', 'J', 'D', 'W', 'o', '4', 'K', 'w', '1', '\"', 'R', 'Q', '_', 'A', 'p', 'V', 'v', 'n', 'z', 'i', ')', '9', 'S', '+', '.', 'f', '(', 'Y', '&', 'g', '-', '2', '*', '{', '[', '#', '}', '7', '6', 'B', '|', '~', ';', '/', '\\', 'G', 's', 'N', 'X', 'k', 'j', '8', '$', 'y', 'u', 'h', 'e', 'd', 'E', 'I', 'c', '?', '^', ']', '\'', '%', '=', '0', ':', 'q', ' ', 'Z', ',', 'b', '<', '3', '!', 'a', '>', 'M', 'T', 'P', 'U', '\u00df', '\u00e1', '\u00d8', '\u00bb', '\u00a6', '\u00e5', '\u00bd', '\u00de', '\u00bc', '\u008d', '\u00f9', '\u0094', '\u00c8', '\u00b8', '\u0088', '\u00f8', '\u00be', '\u00c7', '\u00aa', '\u00b5', '\u00cc', '\u008a', '\u00e8', '\u00da', '\u00b7', '\u00ff', '\u00ea', '\u00dc', '\u00f7', '\u00d5', '\u00cb', '\u00e2', '\u00c1', '\u00ae', '\u00ac', '\u00e4', '\u00fc', '\u00d9', '\u00c9', '\u0083', '\u00e6', '\u00c5', '\u00d3', '\u0091', '\u00ee', '\u00a1', '\u00b3', '\u00a0', '\u00d4', '\u00cf', '\u00dd', '\u00fe', '\u00ad', '\u00ca', '\u0092', '\u00e0', '\u0097', '\u008c', '\u00c4', '\u00cd', '\u0082', '\u0087', '\u0085', '\u008f', '\u00f6', '\u00c0', '\u009f', '\u00f4', '\u00ef', '\u00b9', '\u00a8', '\u00d7', '\u0090', '\u008b', '\u00a5', '\u00b4', '\u009d', '\u0093', '\u00ba', '\u00d6', '\u00b0', '\u00e3', '\u00e7', '\u00db', '\u00a9', '\u00af', '\u009c', '\u00ce', '\u00c6', '\u0081', '\u00a4', '\u0096', '\u00d2', '\u009a', '\u00b1', '\u0086', '\u007f', '\u00b6', '\u0080', '\u009e', '\u00d0', '\u00a2', '\u0084', '\u00a7', '\u00d1', '\u0095', '\u00f1', '\u0099', '\u00fb', '\u00ed', '\u00ec', '\u00ab', '\u00c3', '\u00f3', '\u00e9', '\u00fd', '\u00f0', '\u00c2', '\u00fa', '\u00bf', '\u009b', '\u008e', '\u0089', '\u00f5', '\u00eb', '\u00a3', '\u00f2', '\u00b2', '\u0098'};

    public CVSPass() {
        this.passFile = new File(System.getProperty("cygwin.user.home", System.getProperty("user.home")) + File.separatorChar + ".cvspass");
    }

    @Override
    public final void execute() throws BuildException {
        if (this.cvsRoot == null) {
            throw new BuildException("cvsroot is required");
        }
        if (this.password == null) {
            throw new BuildException("password is required");
        }
        this.log("cvsRoot: " + this.cvsRoot, 4);
        this.log("password: " + this.password, 4);
        this.log("passFile: " + this.passFile, 4);
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            StringBuilder buf = new StringBuilder();
            if (this.passFile.exists()) {
                reader = new BufferedReader(new FileReader(this.passFile));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(this.cvsRoot)) continue;
                    buf.append(line).append(System.lineSeparator());
                }
            }
            String pwdfile = buf.toString() + this.cvsRoot + " A" + this.mangle(this.password);
            this.log("Writing -> " + pwdfile, 4);
            writer = new BufferedWriter(new FileWriter(this.passFile));
            writer.write(pwdfile);
            writer.newLine();
        }
        catch (IOException e) {
            try {
                throw new BuildException(e);
            }
            catch (Throwable throwable) {
                FileUtils.close(reader);
                FileUtils.close(writer);
                throw throwable;
            }
        }
        FileUtils.close(reader);
        FileUtils.close(writer);
    }

    private final String mangle(String password) {
        StringBuilder buf = new StringBuilder();
        for (char ch : password.toCharArray()) {
            buf.append(this.shifts[ch]);
        }
        return buf.toString();
    }

    public void setCvsroot(String cvsRoot) {
        this.cvsRoot = cvsRoot;
    }

    public void setPassfile(File passFile) {
        this.passFile = passFile;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

