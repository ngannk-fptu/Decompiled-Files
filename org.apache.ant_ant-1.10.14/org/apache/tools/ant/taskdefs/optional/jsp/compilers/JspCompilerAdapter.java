/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.jsp.compilers;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.jsp.JspC;
import org.apache.tools.ant.taskdefs.optional.jsp.JspMangler;

public interface JspCompilerAdapter {
    public void setJspc(JspC var1);

    public boolean execute() throws BuildException;

    public JspMangler createMangler();

    public boolean implementsOwnDependencyChecking();
}

