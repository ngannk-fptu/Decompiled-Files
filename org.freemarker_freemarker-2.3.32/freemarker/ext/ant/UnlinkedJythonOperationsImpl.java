/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 *  org.python.util.PythonInterpreter
 */
package freemarker.ext.ant;

import freemarker.ext.ant.UnlinkedJythonOperations;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.python.util.PythonInterpreter;

public class UnlinkedJythonOperationsImpl
implements UnlinkedJythonOperations {
    @Override
    public void execute(String script, Map vars) throws BuildException {
        PythonInterpreter pi = this.createInterpreter(vars);
        pi.exec(script);
    }

    @Override
    public void execute(File file, Map vars) throws BuildException {
        PythonInterpreter pi = this.createInterpreter(vars);
        try {
            pi.execfile(file.getCanonicalPath());
        }
        catch (IOException e) {
            throw new BuildException((Throwable)e);
        }
    }

    private PythonInterpreter createInterpreter(Map vars) {
        PythonInterpreter pi = new PythonInterpreter();
        for (Map.Entry ent : vars.entrySet()) {
            pi.set((String)ent.getKey(), ent.getValue());
        }
        return pi;
    }
}

