/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.bsf.BSFDeclaredBean
 *  org.apache.bsf.BSFEngine
 *  org.apache.bsf.BSFException
 *  org.apache.bsf.BSFManager
 *  org.apache.bsf.util.BSFFunctions
 */
package org.codehaus.groovy.bsf;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.bsf.BSFDeclaredBean;
import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.BSFFunctions;
import org.codehaus.groovy.bsf.GroovyEngine;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.runtime.InvokerHelper;

public class CachingGroovyEngine
extends GroovyEngine {
    private static final Logger LOG = Logger.getLogger(CachingGroovyEngine.class.getName());
    private static final Object[] EMPTY_ARGS = new Object[]{new String[0]};
    private Map<Object, Class> evalScripts;
    private Map<Object, Class> execScripts;
    private Binding context;
    private GroovyClassLoader loader;

    @Override
    public Object eval(String source, int lineNo, int columnNo, Object script) throws BSFException {
        try {
            Class scriptClass = this.evalScripts.get(script);
            if (scriptClass == null) {
                scriptClass = this.loader.parseClass(script.toString(), source);
                this.evalScripts.put(script, scriptClass);
            } else {
                LOG.fine("eval() - Using cached script...");
            }
            Script s = InvokerHelper.createScript(scriptClass, this.context);
            return s.run();
        }
        catch (Exception e) {
            throw new BSFException(100, "exception from Groovy: " + e, (Throwable)e);
        }
    }

    @Override
    public void exec(String source, int lineNo, int columnNo, Object script) throws BSFException {
        try {
            Class scriptClass = this.execScripts.get(script);
            if (scriptClass == null) {
                scriptClass = this.loader.parseClass(script.toString(), source);
                this.execScripts.put(script, scriptClass);
            } else {
                LOG.fine("exec() - Using cached version of class...");
            }
            InvokerHelper.invokeMethod(scriptClass, "main", EMPTY_ARGS);
        }
        catch (Exception e) {
            LOG.log(Level.WARNING, "BSF trace", e);
            throw new BSFException(100, "exception from Groovy: " + e, (Throwable)e);
        }
    }

    @Override
    public void initialize(BSFManager mgr, String lang, Vector declaredBeans) throws BSFException {
        super.initialize(mgr, lang, declaredBeans);
        ClassLoader parent = mgr.getClassLoader();
        if (parent == null) {
            parent = GroovyShell.class.getClassLoader();
        }
        this.setLoader(mgr, parent);
        this.execScripts = new HashMap<Object, Class>();
        this.evalScripts = new HashMap<Object, Class>();
        this.context = this.shell.getContext();
        this.context.setVariable("bsf", new BSFFunctions(mgr, (BSFEngine)this));
        int size = declaredBeans.size();
        for (int i = 0; i < size; ++i) {
            this.declareBean((BSFDeclaredBean)declaredBeans.elementAt(i));
        }
    }

    private void setLoader(final BSFManager mgr, final ClassLoader finalParent) {
        this.loader = (GroovyClassLoader)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                CompilerConfiguration configuration = new CompilerConfiguration();
                configuration.setClasspath(mgr.getClassPath());
                return new GroovyClassLoader(finalParent, configuration);
            }
        });
    }
}

