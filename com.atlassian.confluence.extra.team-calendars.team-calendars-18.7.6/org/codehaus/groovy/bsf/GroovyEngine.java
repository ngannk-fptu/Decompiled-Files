/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.bsf.BSFDeclaredBean
 *  org.apache.bsf.BSFEngine
 *  org.apache.bsf.BSFException
 *  org.apache.bsf.BSFManager
 *  org.apache.bsf.util.BSFEngineImpl
 *  org.apache.bsf.util.BSFFunctions
 */
package org.codehaus.groovy.bsf;

import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import java.util.Vector;
import org.apache.bsf.BSFDeclaredBean;
import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.BSFEngineImpl;
import org.apache.bsf.util.BSFFunctions;
import org.codehaus.groovy.runtime.InvokerHelper;

public class GroovyEngine
extends BSFEngineImpl {
    protected GroovyShell shell;

    private static String convertToValidJavaClassname(String inName) {
        if (inName == null) {
            return "_";
        }
        if (inName.startsWith("scriptdef_")) {
            inName = inName.substring(10);
        }
        if (inName.equals("")) {
            return "_";
        }
        StringBuilder output = new StringBuilder(inName.length());
        boolean firstChar = true;
        for (int i = 0; i < inName.length(); ++i) {
            char ch = inName.charAt(i);
            if (firstChar && !Character.isJavaIdentifierStart(ch)) {
                ch = '_';
            } else if (!firstChar && !Character.isJavaIdentifierPart(ch) && ch != '.') {
                ch = '_';
            }
            firstChar = ch == '.';
            output.append(ch);
        }
        return output.toString();
    }

    public Object apply(String source, int lineNo, int columnNo, Object funcBody, Vector paramNames, Vector arguments) throws BSFException {
        Object object = this.eval(source, lineNo, columnNo, funcBody);
        if (object instanceof Closure) {
            Closure closure = (Closure)object;
            return closure.call(arguments.toArray());
        }
        return object;
    }

    public Object call(Object object, String method, Object[] args) throws BSFException {
        return InvokerHelper.invokeMethod(object, method, args);
    }

    public Object eval(String source, int lineNo, int columnNo, Object script) throws BSFException {
        try {
            source = GroovyEngine.convertToValidJavaClassname(source);
            return this.getEvalShell().evaluate(script.toString(), source);
        }
        catch (Exception e) {
            throw new BSFException(100, "exception from Groovy: " + e, (Throwable)e);
        }
    }

    public void exec(String source, int lineNo, int columnNo, Object script) throws BSFException {
        try {
            source = GroovyEngine.convertToValidJavaClassname(source);
            this.getEvalShell().evaluate(script.toString(), source);
        }
        catch (Exception e) {
            throw new BSFException(100, "exception from Groovy: " + e, (Throwable)e);
        }
    }

    public void initialize(BSFManager mgr, String lang, Vector declaredBeans) throws BSFException {
        super.initialize(mgr, lang, declaredBeans);
        this.shell = new GroovyShell(mgr.getClassLoader());
        this.shell.setVariable("bsf", new BSFFunctions(mgr, (BSFEngine)this));
        int size = declaredBeans.size();
        for (int i = 0; i < size; ++i) {
            this.declareBean((BSFDeclaredBean)declaredBeans.elementAt(i));
        }
    }

    public void declareBean(BSFDeclaredBean bean) throws BSFException {
        this.shell.setVariable(bean.name, bean.bean);
    }

    public void undeclareBean(BSFDeclaredBean bean) throws BSFException {
        this.shell.setVariable(bean.name, null);
    }

    protected GroovyShell getEvalShell() {
        return new GroovyShell(this.shell);
    }
}

