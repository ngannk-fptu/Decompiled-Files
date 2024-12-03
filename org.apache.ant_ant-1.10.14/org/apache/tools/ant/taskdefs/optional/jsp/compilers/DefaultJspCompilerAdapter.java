/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.jsp.compilers;

import java.io.File;
import java.util.Vector;
import java.util.stream.Collectors;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.jsp.JspC;
import org.apache.tools.ant.taskdefs.optional.jsp.compilers.JspCompilerAdapter;
import org.apache.tools.ant.types.CommandlineJava;

public abstract class DefaultJspCompilerAdapter
implements JspCompilerAdapter {
    protected JspC owner;

    protected void logAndAddFilesToCompile(JspC jspc, Vector<String> compileList, CommandlineJava cmd) {
        jspc.log("Compilation " + cmd.describeJavaCommand(), 3);
        String niceSourceList = compileList.stream().peek(arg -> cmd.createArgument().setValue((String)arg)).map(arg -> String.format("    %s%n", arg)).collect(Collectors.joining(""));
        jspc.log(String.format("File%s to be compiled:%n%s", compileList.size() == 1 ? "" : "s", niceSourceList), 3);
    }

    @Override
    public void setJspc(JspC owner) {
        this.owner = owner;
    }

    public JspC getJspc() {
        return this.owner;
    }

    protected void addArg(CommandlineJava cmd, String argument) {
        if (argument != null && !argument.isEmpty()) {
            cmd.createArgument().setValue(argument);
        }
    }

    protected void addArg(CommandlineJava cmd, String argument, String value) {
        if (value != null) {
            cmd.createArgument().setValue(argument);
            cmd.createArgument().setValue(value);
        }
    }

    protected void addArg(CommandlineJava cmd, String argument, File file) {
        if (file != null) {
            cmd.createArgument().setValue(argument);
            cmd.createArgument().setFile(file);
        }
    }

    @Override
    public boolean implementsOwnDependencyChecking() {
        return false;
    }

    public Project getProject() {
        return this.getJspc().getProject();
    }
}

