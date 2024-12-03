/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import java.io.File;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ExitStatusException;
import org.apache.tools.ant.ExtensionPoint;
import org.apache.tools.ant.IntrospectionHelper;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelperRepository;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.TypeAdapter;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.LoaderUtils;
import org.xml.sax.AttributeList;

public class ProjectHelper {
    public static final String ANT_CORE_URI = "antlib:org.apache.tools.ant";
    public static final String ANT_CURRENT_URI = "ant:current";
    public static final String ANT_ATTRIBUTE_URI = "ant:attribute";
    @Deprecated
    public static final String ANTLIB_URI = "antlib:";
    public static final String ANT_TYPE = "ant-type";
    @Deprecated
    public static final String HELPER_PROPERTY = "org.apache.tools.ant.ProjectHelper";
    @Deprecated
    public static final String SERVICE_ID = "META-INF/services/org.apache.tools.ant.ProjectHelper";
    @Deprecated
    public static final String PROJECTHELPER_REFERENCE = "ant.projectHelper";
    public static final String USE_PROJECT_NAME_AS_TARGET_PREFIX = "USE_PROJECT_NAME_AS_TARGET_PREFIX";
    private Vector<Object> importStack = new Vector();
    private List<String[]> extensionStack = new LinkedList<String[]>();
    private static final ThreadLocal<String> targetPrefix = new ThreadLocal();
    private static final ThreadLocal<String> prefixSeparator = ThreadLocal.withInitial(() -> ".");
    private static final ThreadLocal<Boolean> inIncludeMode = ThreadLocal.withInitial(() -> Boolean.FALSE);

    public static void configureProject(Project project, File buildFile) throws BuildException {
        FileResource resource = new FileResource(buildFile);
        ProjectHelper helper = ProjectHelperRepository.getInstance().getProjectHelperForBuildFile(resource);
        project.addReference(PROJECTHELPER_REFERENCE, helper);
        helper.parse(project, buildFile);
    }

    public Vector<Object> getImportStack() {
        return this.importStack;
    }

    public List<String[]> getExtensionStack() {
        return this.extensionStack;
    }

    public static String getCurrentTargetPrefix() {
        return targetPrefix.get();
    }

    public static void setCurrentTargetPrefix(String prefix) {
        targetPrefix.set(prefix);
    }

    public static String getCurrentPrefixSeparator() {
        return prefixSeparator.get();
    }

    public static void setCurrentPrefixSeparator(String sep) {
        prefixSeparator.set(sep);
    }

    public static boolean isInIncludeMode() {
        return Boolean.TRUE.equals(inIncludeMode.get());
    }

    public static void setInIncludeMode(boolean includeMode) {
        inIncludeMode.set(includeMode);
    }

    public void parse(Project project, Object source) throws BuildException {
        throw new BuildException("ProjectHelper.parse() must be implemented in a helper plugin " + this.getClass().getName());
    }

    public static ProjectHelper getProjectHelper() {
        return ProjectHelperRepository.getInstance().getHelpers().next();
    }

    @Deprecated
    public static ClassLoader getContextClassLoader() {
        return LoaderUtils.isContextLoaderAvailable() ? LoaderUtils.getContextClassLoader() : null;
    }

    @Deprecated
    public static void configure(Object target, AttributeList attrs, Project project) throws BuildException {
        if (target instanceof TypeAdapter) {
            target = ((TypeAdapter)target).getProxy();
        }
        IntrospectionHelper ih = IntrospectionHelper.getHelper(project, target.getClass());
        int length = attrs.getLength();
        for (int i = 0; i < length; ++i) {
            String value = ProjectHelper.replaceProperties(project, attrs.getValue(i), project.getProperties());
            try {
                ih.setAttribute(project, target, attrs.getName(i).toLowerCase(Locale.ENGLISH), value);
                continue;
            }
            catch (BuildException be) {
                if (attrs.getName(i).equals("id")) continue;
                throw be;
            }
        }
    }

    public static void addText(Project project, Object target, char[] buf, int start, int count) throws BuildException {
        ProjectHelper.addText(project, target, new String(buf, start, count));
    }

    public static void addText(Project project, Object target, String text) throws BuildException {
        if (text == null) {
            return;
        }
        if (target instanceof TypeAdapter) {
            target = ((TypeAdapter)target).getProxy();
        }
        IntrospectionHelper.getHelper(project, target.getClass()).addText(project, target, text);
    }

    public static void storeChild(Project project, Object parent, Object child, String tag) {
        IntrospectionHelper ih = IntrospectionHelper.getHelper(project, parent.getClass());
        ih.storeElement(project, parent, child, tag);
    }

    @Deprecated
    public static String replaceProperties(Project project, String value) throws BuildException {
        return project.replaceProperties(value);
    }

    @Deprecated
    public static String replaceProperties(Project project, String value, Hashtable<String, Object> keys) throws BuildException {
        PropertyHelper ph = PropertyHelper.getPropertyHelper(project);
        return ph.replaceProperties(null, value, keys);
    }

    @Deprecated
    public static void parsePropertyString(String value, Vector<String> fragments, Vector<String> propertyRefs) throws BuildException {
        PropertyHelper.parsePropertyStringDefault(value, fragments, propertyRefs);
    }

    public static String genComponentName(String uri, String name) {
        if (uri == null || uri.isEmpty() || uri.equals(ANT_CORE_URI)) {
            return name;
        }
        return uri + ":" + name;
    }

    public static String extractUriFromComponentName(String componentName) {
        if (componentName == null) {
            return "";
        }
        int index = componentName.lastIndexOf(58);
        if (index == -1) {
            return "";
        }
        return componentName.substring(0, index);
    }

    public static String extractNameFromComponentName(String componentName) {
        int index = componentName.lastIndexOf(58);
        if (index == -1) {
            return componentName;
        }
        return componentName.substring(index + 1);
    }

    public static String nsToComponentName(String ns) {
        return "attribute namespace:" + ns;
    }

    public static BuildException addLocationToBuildException(BuildException ex, Location newLocation) {
        if (ex.getLocation() == null || ex.getMessage() == null) {
            return ex;
        }
        String errorMessage = String.format("The following error occurred while executing this line:%n%s%s", ex.getLocation().toString(), ex.getMessage());
        if (ex instanceof ExitStatusException) {
            int exitStatus = ((ExitStatusException)ex).getStatus();
            if (newLocation == null) {
                return new ExitStatusException(errorMessage, exitStatus);
            }
            return new ExitStatusException(errorMessage, exitStatus, newLocation);
        }
        if (newLocation == null) {
            return new BuildException(errorMessage, ex);
        }
        return new BuildException(errorMessage, ex, newLocation);
    }

    public boolean canParseAntlibDescriptor(Resource r) {
        return false;
    }

    public UnknownElement parseAntlibDescriptor(Project containingProject, Resource source) {
        throw new BuildException("can't parse antlib descriptors");
    }

    public boolean canParseBuildFile(Resource buildFile) {
        return true;
    }

    public String getDefaultBuildFile() {
        return "build.xml";
    }

    public void resolveExtensionOfAttributes(Project project) throws BuildException {
        for (String[] extensionInfo : this.getExtensionStack()) {
            String extPointName = extensionInfo[0];
            String targetName = extensionInfo[1];
            OnMissingExtensionPoint missingBehaviour = OnMissingExtensionPoint.valueOf(extensionInfo[2]);
            String prefixAndSep = extensionInfo.length > 3 ? extensionInfo[3] : null;
            Hashtable<String, Target> projectTargets = project.getTargets();
            Target extPoint = null;
            if (prefixAndSep == null) {
                extPoint = projectTargets.get(extPointName);
            } else {
                extPoint = projectTargets.get(prefixAndSep + extPointName);
                if (extPoint == null) {
                    extPoint = projectTargets.get(extPointName);
                }
            }
            if (extPoint == null) {
                String message = "can't add target " + targetName + " to extension-point " + extPointName + " because the extension-point is unknown.";
                if (missingBehaviour == OnMissingExtensionPoint.FAIL) {
                    throw new BuildException(message);
                }
                if (missingBehaviour != OnMissingExtensionPoint.WARN) continue;
                Target t = projectTargets.get(targetName);
                project.log(t, "Warning: " + message, 1);
                continue;
            }
            if (!(extPoint instanceof ExtensionPoint)) {
                throw new BuildException("referenced target " + extPointName + " is not an extension-point");
            }
            extPoint.addDependency(targetName);
        }
    }

    public static final class OnMissingExtensionPoint {
        public static final OnMissingExtensionPoint FAIL = new OnMissingExtensionPoint("fail");
        public static final OnMissingExtensionPoint WARN = new OnMissingExtensionPoint("warn");
        public static final OnMissingExtensionPoint IGNORE = new OnMissingExtensionPoint("ignore");
        private static final OnMissingExtensionPoint[] values = new OnMissingExtensionPoint[]{FAIL, WARN, IGNORE};
        private final String name;

        private OnMissingExtensionPoint(String name) {
            this.name = name;
        }

        public String name() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }

        public static OnMissingExtensionPoint valueOf(String name) {
            if (name == null) {
                throw new NullPointerException();
            }
            for (OnMissingExtensionPoint value : values) {
                if (!name.equals(value.name())) continue;
                return value;
            }
            throw new IllegalArgumentException("Unknown onMissingExtensionPoint " + name);
        }
    }
}

