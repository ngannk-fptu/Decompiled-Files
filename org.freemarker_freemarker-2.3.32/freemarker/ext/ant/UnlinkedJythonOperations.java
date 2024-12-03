/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 */
package freemarker.ext.ant;

import java.io.File;
import java.util.Map;
import org.apache.tools.ant.BuildException;

interface UnlinkedJythonOperations {
    public void execute(String var1, Map var2) throws BuildException;

    public void execute(File var1, Map var2) throws BuildException;
}

