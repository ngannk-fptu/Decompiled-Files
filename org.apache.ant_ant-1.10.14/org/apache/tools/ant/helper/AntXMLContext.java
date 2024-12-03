/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.helper;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.util.FileUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;

public class AntXMLContext {
    private Project project;
    private File buildFile;
    private URL buildFileURL;
    private Vector<Target> targetVector = new Vector();
    private File buildFileParent;
    private URL buildFileParentURL;
    private String currentProjectName;
    private Locator locator;
    private Target implicitTarget = new Target();
    private Target currentTarget = null;
    private Vector<RuntimeConfigurable> wStack = new Vector();
    private boolean ignoreProjectTag = false;
    private Map<String, List<String>> prefixMapping = new HashMap<String, List<String>>();
    private Map<String, Target> currentTargets = null;

    public AntXMLContext(Project project) {
        this.project = project;
        this.implicitTarget.setProject(project);
        this.implicitTarget.setName("");
        this.targetVector.addElement(this.implicitTarget);
    }

    public void setBuildFile(File buildFile) {
        this.buildFile = buildFile;
        if (buildFile != null) {
            this.buildFileParent = new File(buildFile.getParent());
            this.implicitTarget.setLocation(new Location(buildFile.getAbsolutePath()));
            try {
                this.setBuildFile(FileUtils.getFileUtils().getFileURL(buildFile));
            }
            catch (MalformedURLException ex) {
                throw new BuildException(ex);
            }
        } else {
            this.buildFileParent = null;
        }
    }

    public void setBuildFile(URL buildFile) throws MalformedURLException {
        this.buildFileURL = buildFile;
        this.buildFileParentURL = new URL(buildFile, ".");
        if (this.implicitTarget.getLocation() == null) {
            this.implicitTarget.setLocation(new Location(buildFile.toString()));
        }
    }

    public File getBuildFile() {
        return this.buildFile;
    }

    public File getBuildFileParent() {
        return this.buildFileParent;
    }

    public URL getBuildFileURL() {
        return this.buildFileURL;
    }

    public URL getBuildFileParentURL() {
        return this.buildFileParentURL;
    }

    public Project getProject() {
        return this.project;
    }

    public String getCurrentProjectName() {
        return this.currentProjectName;
    }

    public void setCurrentProjectName(String name) {
        this.currentProjectName = name;
    }

    public RuntimeConfigurable currentWrapper() {
        if (this.wStack.size() < 1) {
            return null;
        }
        return this.wStack.elementAt(this.wStack.size() - 1);
    }

    public RuntimeConfigurable parentWrapper() {
        if (this.wStack.size() < 2) {
            return null;
        }
        return this.wStack.elementAt(this.wStack.size() - 2);
    }

    public void pushWrapper(RuntimeConfigurable wrapper) {
        this.wStack.addElement(wrapper);
    }

    public void popWrapper() {
        if (this.wStack.size() > 0) {
            this.wStack.removeElementAt(this.wStack.size() - 1);
        }
    }

    public Vector<RuntimeConfigurable> getWrapperStack() {
        return this.wStack;
    }

    public void addTarget(Target target) {
        this.targetVector.addElement(target);
        this.currentTarget = target;
    }

    public Target getCurrentTarget() {
        return this.currentTarget;
    }

    public Target getImplicitTarget() {
        return this.implicitTarget;
    }

    public void setCurrentTarget(Target target) {
        this.currentTarget = target;
    }

    public void setImplicitTarget(Target target) {
        this.implicitTarget = target;
    }

    public Vector<Target> getTargets() {
        return this.targetVector;
    }

    public void configureId(Object element, Attributes attr) {
        String id = attr.getValue("id");
        if (id != null) {
            this.project.addIdReference(id, element);
        }
    }

    public Locator getLocator() {
        return this.locator;
    }

    public void setLocator(Locator locator) {
        this.locator = locator;
    }

    public boolean isIgnoringProjectTag() {
        return this.ignoreProjectTag;
    }

    public void setIgnoreProjectTag(boolean flag) {
        this.ignoreProjectTag = flag;
    }

    public void startPrefixMapping(String prefix, String uri) {
        List list = this.prefixMapping.computeIfAbsent(prefix, k -> new ArrayList());
        list.add(uri);
    }

    public void endPrefixMapping(String prefix) {
        List<String> list = this.prefixMapping.get(prefix);
        if (list == null || list.isEmpty()) {
            return;
        }
        list.remove(list.size() - 1);
    }

    public String getPrefixMapping(String prefix) {
        List<String> list = this.prefixMapping.get(prefix);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(list.size() - 1);
    }

    public Map<String, Target> getCurrentTargets() {
        return this.currentTargets;
    }

    public void setCurrentTargets(Map<String, Target> currentTargets) {
        this.currentTargets = currentTargets;
    }
}

