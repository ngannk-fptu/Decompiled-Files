/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util.optional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.util.ScriptRunnerBase;

public class JavaxScriptRunner
extends ScriptRunnerBase {
    private ScriptEngine keptEngine;
    private CompiledScript compiledScript;
    private static final String DROP_GRAAL_SECURITY_RESTRICTIONS = "polyglot.js.allowAllAccess";
    private static final String ENABLE_NASHORN_COMPAT_IN_GRAAL = "polyglot.js.nashorn-compat";
    private static final List<String> JS_LANGUAGES = Arrays.asList("js", "javascript");

    @Override
    public String getManagerName() {
        return "javax";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean supportsLanguage() {
        if (this.keptEngine != null) {
            return true;
        }
        this.checkLanguage();
        ClassLoader origLoader = this.replaceContextLoader();
        try {
            boolean bl = this.createEngine() != null;
            return bl;
        }
        catch (Exception ex) {
            boolean bl = false;
            return bl;
        }
        finally {
            this.restoreContextLoader(origLoader);
        }
    }

    @Override
    public void executeScript(String execName) throws BuildException {
        this.evaluateScript(execName);
    }

    @Override
    public Object evaluateScript(String execName) throws BuildException {
        this.checkLanguage();
        ClassLoader origLoader = this.replaceContextLoader();
        try {
            ScriptEngine engine;
            Object bindings;
            if (this.getCompiled()) {
                String compiledScriptRefName = String.format("%s.%s.%d.%d", "org.apache.ant.scriptcache", this.getLanguage(), Objects.hashCode(this.getScript()), Objects.hashCode(this.getClass().getClassLoader()));
                if (null == this.compiledScript) {
                    this.compiledScript = (CompiledScript)this.getProject().getReference(compiledScriptRefName);
                }
                if (null == this.compiledScript) {
                    ScriptEngine engine2 = this.createEngine();
                    if (engine2 == null) {
                        throw new BuildException("Unable to create javax script engine for %s", this.getLanguage());
                    }
                    if (engine2 instanceof Compilable) {
                        this.getProject().log("compile script " + execName, 3);
                        this.compiledScript = ((Compilable)((Object)engine2)).compile(this.getScript());
                    } else {
                        this.getProject().log("script compilation not available for " + execName, 3);
                        this.compiledScript = null;
                    }
                    this.getProject().addReference(compiledScriptRefName, this.compiledScript);
                }
                if (null != this.compiledScript) {
                    bindings = new SimpleBindings();
                    this.applyBindings(((Bindings)bindings)::put);
                    this.getProject().log("run compiled script " + compiledScriptRefName, 4);
                    Object object = this.compiledScript.eval((Bindings)bindings);
                    return object;
                }
            }
            if ((engine = this.createEngine()) == null) {
                throw new BuildException("Unable to create javax script engine for " + this.getLanguage());
            }
            this.applyBindings(engine::put);
            bindings = engine.eval(this.getScript());
            return bindings;
        }
        catch (BuildException be) {
            throw JavaxScriptRunner.unwrap(be);
        }
        catch (Exception be) {
            Throwable t = be;
            Throwable te = be.getCause();
            if (te != null) {
                if (te instanceof BuildException) {
                    throw (BuildException)te;
                }
                t = te;
            }
            throw new BuildException(t);
        }
        finally {
            this.restoreContextLoader(origLoader);
        }
    }

    private void applyBindings(BiConsumer<String, Object> target) {
        Map<String, Object> source = this.getBeans();
        if ("FX".equalsIgnoreCase(this.getLanguage())) {
            source = source.entrySet().stream().collect(Collectors.toMap(e -> String.format("%s:%s", e.getKey(), e.getValue().getClass().getName()), Map.Entry::getValue));
        }
        source.forEach(target);
    }

    private ScriptEngine createEngine() {
        ScriptEngine result;
        if (this.keptEngine != null) {
            return this.keptEngine;
        }
        if (this.languageIsJavaScript()) {
            this.maybeEnableNashornCompatibility();
        }
        if ((result = new ScriptEngineManager().getEngineByName(this.getLanguage())) == null && JavaEnvUtils.isAtLeastJavaVersion("15") && this.languageIsJavaScript()) {
            this.getProject().log("Java 15 has removed Nashorn, you must provide an engine for running JavaScript yourself. GraalVM JavaScript currently is the preferred option.", 1);
        }
        this.maybeApplyGraalJsProperties(result);
        if (result != null && this.getKeepEngine()) {
            this.keptEngine = result;
        }
        return result;
    }

    private void maybeApplyGraalJsProperties(ScriptEngine engine) {
        if (engine != null && engine.getClass().getName().contains("Graal")) {
            engine.getBindings(100).put(DROP_GRAAL_SECURITY_RESTRICTIONS, (Object)true);
        }
    }

    private void maybeEnableNashornCompatibility() {
        if (this.getProject() != null) {
            System.setProperty(ENABLE_NASHORN_COMPAT_IN_GRAAL, Project.toBoolean(this.getProject().getProperty("ant.disable.graal.nashorn.compat")) ? "false" : "true");
        }
    }

    private boolean languageIsJavaScript() {
        return JS_LANGUAGES.contains(this.getLanguage());
    }

    private static BuildException unwrap(Throwable t) {
        BuildException deepest = t instanceof BuildException ? (BuildException)t : null;
        Throwable current = t;
        while (current.getCause() != null) {
            if (!((current = current.getCause()) instanceof BuildException)) continue;
            deepest = (BuildException)current;
        }
        return deepest;
    }
}

