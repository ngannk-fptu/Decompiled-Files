/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.LoaderUtils;
import org.apache.tools.ant.util.ReflectUtil;
import org.apache.tools.ant.util.ScriptFixBSFPath;
import org.apache.tools.ant.util.ScriptManager;
import org.apache.tools.ant.util.ScriptRunnerBase;

public class ScriptRunnerCreator {
    private static final Map<ScriptManager, ScriptRunnerFactory> RUNNER_FACTORIES;
    private static final String UTIL_OPT = "org.apache.tools.ant.util.optional";
    private static final String BSF_PACK = "org.apache.bsf";
    private static final String BSF_MANAGER = "org.apache.bsf.BSFManager";
    private static final String BSF_RUNNER = "org.apache.tools.ant.util.optional.ScriptRunner";
    private static final String JAVAX_MANAGER = "javax.script.ScriptEngineManager";
    private static final String JAVAX_RUNNER = "org.apache.tools.ant.util.optional.JavaxScriptRunner";
    private Project project;

    public ScriptRunnerCreator(Project project) {
        this.project = project;
    }

    @Deprecated
    public synchronized ScriptRunnerBase createRunner(String manager, String language, ClassLoader classLoader) {
        return this.createRunner(ScriptManager.valueOf(manager), language, classLoader);
    }

    public synchronized ScriptRunnerBase createRunner(ScriptManager manager, String language, ClassLoader classLoader) {
        if (language == null) {
            throw new BuildException("script language must be specified");
        }
        if (manager == null) {
            throw new BuildException("Unsupported language prefix " + (Object)((Object)manager));
        }
        EnumSet<ScriptManager> managers = manager == ScriptManager.auto ? EnumSet.complementOf(EnumSet.of(ScriptManager.auto)) : EnumSet.of(manager);
        return managers.stream().map(RUNNER_FACTORIES::get).map(f -> f.getRunner(this.project, language, classLoader)).filter(Objects::nonNull).findFirst().orElseThrow(() -> new BuildException(managers.stream().map(RUNNER_FACTORIES::get).map(f -> f.managerClass).collect(Collectors.joining("|", "Unable to load script engine manager (", ")"))));
    }

    static {
        EnumMap<ScriptManager, ScriptRunnerFactory> m = new EnumMap<ScriptManager, ScriptRunnerFactory>(ScriptManager.class);
        m.put(ScriptManager.bsf, new ScriptRunnerFactory(BSF_MANAGER, BSF_RUNNER){

            @Override
            boolean validateManager(Project project, String language, ClassLoader scriptLoader) {
                if (scriptLoader.getResource(LoaderUtils.classNameToResource(ScriptRunnerCreator.BSF_MANAGER)) == null) {
                    return false;
                }
                new ScriptFixBSFPath().fixClassLoader(scriptLoader, language);
                return true;
            }
        });
        m.put(ScriptManager.javax, new ScriptRunnerFactory(JAVAX_MANAGER, JAVAX_RUNNER));
        RUNNER_FACTORIES = Collections.unmodifiableMap(m);
    }

    private static class ScriptRunnerFactory {
        final String managerClass;
        final String runnerClass;

        ScriptRunnerFactory(String managerClass, String runnerClass) {
            this.managerClass = managerClass;
            this.runnerClass = runnerClass;
        }

        boolean validateManager(Project project, String language, ClassLoader scriptLoader) {
            try {
                Class.forName(this.managerClass, true, scriptLoader);
                return true;
            }
            catch (Exception ex) {
                return false;
            }
        }

        ScriptRunnerBase getRunner(Project project, String language, ClassLoader scriptLoader) {
            ScriptRunnerBase runner;
            if (!this.validateManager(project, language, scriptLoader)) {
                return null;
            }
            try {
                runner = Class.forName(this.runnerClass, true, scriptLoader).asSubclass(ScriptRunnerBase.class).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                runner.setProject(project);
            }
            catch (Exception ex) {
                throw ReflectUtil.toBuildException(ex);
            }
            runner.setLanguage(language);
            runner.setScriptClassLoader(scriptLoader);
            return runner;
        }
    }
}

