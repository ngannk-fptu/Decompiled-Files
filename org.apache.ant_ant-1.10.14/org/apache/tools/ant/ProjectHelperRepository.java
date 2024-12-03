/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.helper.ProjectHelper2;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.util.LoaderUtils;
import org.apache.tools.ant.util.StreamUtils;

public class ProjectHelperRepository {
    private static final String DEBUG_PROJECT_HELPER_REPOSITORY = "ant.project-helper-repo.debug";
    private static final boolean DEBUG = "true".equals(System.getProperty("ant.project-helper-repo.debug"));
    private static ProjectHelperRepository instance = new ProjectHelperRepository();
    private List<Constructor<? extends ProjectHelper>> helpers = new ArrayList<Constructor<? extends ProjectHelper>>();
    private static Constructor<ProjectHelper2> PROJECTHELPER2_CONSTRUCTOR;

    public static ProjectHelperRepository getInstance() {
        return instance;
    }

    private ProjectHelperRepository() {
        this.collectProjectHelpers();
    }

    private void collectProjectHelpers() {
        block5: {
            this.registerProjectHelper(this.getProjectHelperBySystemProperty());
            try {
                InputStream systemResource;
                ClassLoader classLoader = LoaderUtils.getContextClassLoader();
                if (classLoader != null) {
                    for (URL resource : Collections.list(classLoader.getResources("META-INF/services/org.apache.tools.ant.ProjectHelper"))) {
                        URLConnection conn = resource.openConnection();
                        conn.setUseCaches(false);
                        this.registerProjectHelper(this.getProjectHelperByService(conn.getInputStream()));
                    }
                }
                if ((systemResource = ClassLoader.getSystemResourceAsStream("META-INF/services/org.apache.tools.ant.ProjectHelper")) != null) {
                    this.registerProjectHelper(this.getProjectHelperByService(systemResource));
                }
            }
            catch (Exception e) {
                System.err.println("Unable to load ProjectHelper from service META-INF/services/org.apache.tools.ant.ProjectHelper (" + e.getClass().getName() + ": " + e.getMessage() + ")");
                if (!DEBUG) break block5;
                e.printStackTrace(System.err);
            }
        }
    }

    public void registerProjectHelper(String helperClassName) throws BuildException {
        this.registerProjectHelper(this.getHelperConstructor(helperClassName));
    }

    public void registerProjectHelper(Class<? extends ProjectHelper> helperClass) throws BuildException {
        try {
            this.registerProjectHelper(helperClass.getConstructor(new Class[0]));
        }
        catch (NoSuchMethodException e) {
            throw new BuildException("Couldn't find no-arg constructor in " + helperClass.getName());
        }
    }

    private void registerProjectHelper(Constructor<? extends ProjectHelper> helperConstructor) {
        if (helperConstructor == null) {
            return;
        }
        if (DEBUG) {
            System.out.println("ProjectHelper " + helperConstructor.getClass().getName() + " registered.");
        }
        this.helpers.add(helperConstructor);
    }

    private Constructor<? extends ProjectHelper> getProjectHelperBySystemProperty() {
        block3: {
            String helperClass = System.getProperty("org.apache.tools.ant.ProjectHelper");
            try {
                if (helperClass != null) {
                    return this.getHelperConstructor(helperClass);
                }
            }
            catch (SecurityException e) {
                System.err.println("Unable to load ProjectHelper class \"" + helperClass + " specified in system property " + "org.apache.tools.ant.ProjectHelper" + " (" + e.getMessage() + ")");
                if (!DEBUG) break block3;
                e.printStackTrace(System.err);
            }
        }
        return null;
    }

    private Constructor<? extends ProjectHelper> getProjectHelperByService(InputStream is) {
        block3: {
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String helperClassName = rd.readLine();
                rd.close();
                if (helperClassName != null && !helperClassName.isEmpty()) {
                    return this.getHelperConstructor(helperClassName);
                }
            }
            catch (Exception e) {
                System.out.println("Unable to load ProjectHelper from service META-INF/services/org.apache.tools.ant.ProjectHelper (" + e.getMessage() + ")");
                if (!DEBUG) break block3;
                e.printStackTrace(System.err);
            }
        }
        return null;
    }

    private Constructor<? extends ProjectHelper> getHelperConstructor(String helperClass) throws BuildException {
        ClassLoader classLoader = LoaderUtils.getContextClassLoader();
        try {
            Class<?> clazz = null;
            if (classLoader != null) {
                try {
                    clazz = classLoader.loadClass(helperClass);
                }
                catch (ClassNotFoundException classNotFoundException) {
                    // empty catch block
                }
            }
            if (clazz == null) {
                clazz = Class.forName(helperClass);
            }
            return clazz.asSubclass(ProjectHelper.class).getConstructor(new Class[0]);
        }
        catch (Exception e) {
            throw new BuildException(e);
        }
    }

    public ProjectHelper getProjectHelperForBuildFile(Resource buildFile) throws BuildException {
        ProjectHelper ph = StreamUtils.iteratorAsStream(this.getHelpers()).filter(helper -> helper.canParseBuildFile(buildFile)).findFirst().orElse(null);
        if (ph == null) {
            throw new BuildException("BUG: at least the ProjectHelper2 should have supported the file " + buildFile);
        }
        if (DEBUG) {
            System.out.println("ProjectHelper " + ph.getClass().getName() + " selected for the build file " + buildFile);
        }
        return ph;
    }

    public ProjectHelper getProjectHelperForAntlib(Resource antlib) throws BuildException {
        ProjectHelper ph = StreamUtils.iteratorAsStream(this.getHelpers()).filter(helper -> helper.canParseAntlibDescriptor(antlib)).findFirst().orElse(null);
        if (ph == null) {
            throw new BuildException("BUG: at least the ProjectHelper2 should have supported the file " + antlib);
        }
        if (DEBUG) {
            System.out.println("ProjectHelper " + ph.getClass().getName() + " selected for the antlib " + antlib);
        }
        return ph;
    }

    public Iterator<ProjectHelper> getHelpers() {
        Stream.Builder<Constructor<ProjectHelper2>> b = Stream.builder();
        this.helpers.forEach(b::add);
        return b.add(PROJECTHELPER2_CONSTRUCTOR).build().map(c -> {
            try {
                return (ProjectHelper)c.newInstance(new Object[0]);
            }
            catch (Exception e) {
                throw new BuildException("Failed to invoke no-arg constructor on " + c.getName());
            }
        }).map(ProjectHelper.class::cast).iterator();
    }

    static {
        try {
            PROJECTHELPER2_CONSTRUCTOR = ProjectHelper2.class.getConstructor(new Class[0]);
        }
        catch (Exception e) {
            throw new BuildException(e);
        }
    }
}

