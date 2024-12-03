/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.LoaderUtils;

public class ScriptFixBSFPath {
    private static final String UTIL_OPTIONAL_PACKAGE = "org.apache.tools.ant.util.optional";
    private static final String BSF_PACKAGE = "org.apache.bsf";
    private static final String BSF_MANAGER = "org.apache.bsf.BSFManager";
    private static final String BSF_SCRIPT_RUNNER = "org.apache.tools.ant.util.optional.ScriptRunner";
    private static final String[] BSF_LANGUAGES = new String[]{"js", "org.mozilla.javascript.Scriptable", "javascript", "org.mozilla.javascript.Scriptable", "jacl", "tcl.lang.Interp", "netrexx", "netrexx.lang.Rexx", "nrx", "netrexx.lang.Rexx", "jython", "org.python.core.Py", "py", "org.python.core.Py", "xslt", "org.apache.xpath.objects.XObject"};
    private static final Map<String, String> BSF_LANGUAGE_MAP = new HashMap<String, String>();

    private File getClassSource(ClassLoader loader, String className) {
        return LoaderUtils.getResourceSource(loader, LoaderUtils.classNameToResource(className));
    }

    private File getClassSource(String className) {
        return this.getClassSource(this.getClass().getClassLoader(), className);
    }

    public void fixClassLoader(ClassLoader loader, String language) {
        if (loader == this.getClass().getClassLoader() || !(loader instanceof AntClassLoader)) {
            return;
        }
        ClassLoader myLoader = this.getClass().getClassLoader();
        AntClassLoader fixLoader = (AntClassLoader)loader;
        File bsfSource = this.getClassSource(BSF_MANAGER);
        boolean needMoveRunner = bsfSource == null;
        String languageClassName = BSF_LANGUAGE_MAP.get(language);
        boolean needMoveBsf = bsfSource != null && languageClassName != null && !LoaderUtils.classExists(myLoader, languageClassName) && LoaderUtils.classExists(loader, languageClassName);
        boolean bl = needMoveRunner = needMoveRunner || needMoveBsf;
        if (bsfSource == null) {
            bsfSource = this.getClassSource(loader, BSF_MANAGER);
        }
        if (bsfSource == null) {
            throw new BuildException("Unable to find BSF classes for scripting");
        }
        if (needMoveBsf) {
            fixLoader.addPathComponent(bsfSource);
            fixLoader.addLoaderPackageRoot(BSF_PACKAGE);
        }
        if (needMoveRunner) {
            fixLoader.addPathComponent(LoaderUtils.getResourceSource(fixLoader, LoaderUtils.classNameToResource(BSF_SCRIPT_RUNNER)));
            fixLoader.addLoaderPackageRoot(UTIL_OPTIONAL_PACKAGE);
        }
    }

    static {
        for (int i = 0; i < BSF_LANGUAGES.length; i += 2) {
            BSF_LANGUAGE_MAP.put(BSF_LANGUAGES[i], BSF_LANGUAGES[i + 1]);
        }
    }
}

