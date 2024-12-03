/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.VectorSet;

public class Ant
extends Task {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private File dir = null;
    private String antFile = null;
    private String output = null;
    private boolean inheritAll = true;
    private boolean inheritRefs = false;
    private List<Property> properties = new Vector<Property>();
    private List<Reference> references = new Vector<Reference>();
    private Project newProject;
    private PrintStream out = null;
    private List<PropertySet> propertySets = new Vector<PropertySet>();
    private List<String> targets = new Vector<String>();
    private boolean targetAttributeSet = false;
    private boolean useNativeBasedir = false;

    public Ant() {
    }

    public Ant(Task owner) {
        this.bindToOwner(owner);
    }

    public void setUseNativeBasedir(boolean b) {
        this.useNativeBasedir = b;
    }

    public void setInheritAll(boolean value) {
        this.inheritAll = value;
    }

    public void setInheritRefs(boolean value) {
        this.inheritRefs = value;
    }

    @Override
    public void init() {
        this.newProject = this.getProject().createSubProject();
        this.newProject.setJavaVersionProperty();
    }

    private void reinit() {
        this.init();
    }

    private void initializeProject() {
        this.newProject.setInputHandler(this.getProject().getInputHandler());
        this.getProject().getBuildListeners().forEach(bl -> this.newProject.addBuildListener((BuildListener)bl));
        if (this.output != null) {
            File outfile = this.dir != null ? FILE_UTILS.resolveFile(this.dir, this.output) : this.getProject().resolveFile(this.output);
            try {
                this.out = new PrintStream(Files.newOutputStream(outfile.toPath(), new OpenOption[0]));
                DefaultLogger logger = new DefaultLogger();
                logger.setMessageOutputLevel(2);
                logger.setOutputPrintStream(this.out);
                logger.setErrorPrintStream(this.out);
                this.newProject.addBuildListener(logger);
            }
            catch (IOException ex) {
                this.log("Ant: Can't set output to " + this.output);
            }
        }
        if (this.useNativeBasedir) {
            this.addAlmostAll(this.getProject().getUserProperties(), PropertyType.USER);
        } else {
            this.getProject().copyUserProperties(this.newProject);
        }
        if (!this.inheritAll) {
            this.newProject.initProperties();
        } else {
            this.addAlmostAll(this.getProject().getProperties(), PropertyType.PLAIN);
        }
        for (PropertySet ps : this.propertySets) {
            this.addAlmostAll(ps.getProperties(), PropertyType.PLAIN);
        }
    }

    @Override
    public void handleOutput(String outputToHandle) {
        if (this.newProject != null) {
            this.newProject.demuxOutput(outputToHandle, false);
        } else {
            super.handleOutput(outputToHandle);
        }
    }

    @Override
    public int handleInput(byte[] buffer, int offset, int length) throws IOException {
        if (this.newProject != null) {
            return this.newProject.demuxInput(buffer, offset, length);
        }
        return super.handleInput(buffer, offset, length);
    }

    @Override
    public void handleFlush(String toFlush) {
        if (this.newProject != null) {
            this.newProject.demuxFlush(toFlush, false);
        } else {
            super.handleFlush(toFlush);
        }
    }

    @Override
    public void handleErrorOutput(String errorOutputToHandle) {
        if (this.newProject != null) {
            this.newProject.demuxOutput(errorOutputToHandle, true);
        } else {
            super.handleErrorOutput(errorOutputToHandle);
        }
    }

    @Override
    public void handleErrorFlush(String errorOutputToFlush) {
        if (this.newProject != null) {
            this.newProject.demuxFlush(errorOutputToFlush, true);
        } else {
            super.handleErrorFlush(errorOutputToFlush);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute() throws BuildException {
        block24: {
            File savedDir = this.dir;
            String savedAntFile = this.antFile;
            VectorSet<String> locals = new VectorSet<String>(this.targets);
            try {
                String defaultTarget;
                this.getNewProject();
                if (this.dir == null && this.inheritAll) {
                    this.dir = this.getProject().getBaseDir();
                }
                this.initializeProject();
                if (this.dir != null) {
                    if (!this.useNativeBasedir) {
                        this.newProject.setBaseDir(this.dir);
                        if (savedDir != null) {
                            this.newProject.setInheritedProperty("basedir", this.dir.getAbsolutePath());
                        }
                    }
                } else {
                    this.dir = this.getProject().getBaseDir();
                }
                this.overrideProperties();
                if (this.antFile == null) {
                    this.antFile = this.getDefaultBuildFile();
                }
                File file = FILE_UTILS.resolveFile(this.dir, this.antFile);
                this.antFile = file.getAbsolutePath();
                this.log("calling target(s) " + (locals.isEmpty() ? "[default]" : locals.toString()) + " in build file " + this.antFile, 3);
                this.newProject.setUserProperty("ant.file", this.antFile);
                String thisAntFile = this.getProject().getProperty("ant.file");
                if (thisAntFile != null && file.equals(this.getProject().resolveFile(thisAntFile)) && this.getOwningTarget() != null && this.getOwningTarget().getName().isEmpty()) {
                    if ("antcall".equals(this.getTaskName())) {
                        throw new BuildException("antcall must not be used at the top level.");
                    }
                    throw new BuildException("%s task at the top level must not invoke its own build file.", this.getTaskName());
                }
                try {
                    ProjectHelper.configureProject(this.newProject, file);
                }
                catch (BuildException ex) {
                    throw ProjectHelper.addLocationToBuildException(ex, this.getLocation());
                }
                if (locals.isEmpty() && (defaultTarget = this.newProject.getDefaultTarget()) != null) {
                    ((Vector)locals).add(defaultTarget);
                }
                if (this.newProject.getProperty("ant.file").equals(this.getProject().getProperty("ant.file")) && this.getOwningTarget() != null) {
                    String owningTargetName = this.getOwningTarget().getName();
                    if (((Vector)locals).contains(owningTargetName)) {
                        throw new BuildException("%s task calling its own parent target.", this.getTaskName());
                    }
                    Hashtable<String, Target> targetsMap = this.getProject().getTargets();
                    if (locals.stream().map(targetsMap::get).filter(Objects::nonNull).anyMatch(other -> other.dependsOn(owningTargetName))) {
                        throw new BuildException("%s task calling a target that depends on its parent target '%s'.", this.getTaskName(), owningTargetName);
                    }
                }
                this.addReferences();
                if (locals.isEmpty() || locals.size() == 1 && locals.get(0) != null && ((String)locals.get(0)).isEmpty()) break block24;
                BuildException be = null;
                try {
                    this.log("Entering " + this.antFile + "...", 3);
                    this.newProject.fireSubBuildStarted();
                    this.newProject.executeTargets(locals);
                }
                catch (BuildException ex) {
                    be = ProjectHelper.addLocationToBuildException(ex, this.getLocation());
                    throw be;
                }
                finally {
                    this.log("Exiting " + this.antFile + ".", 3);
                    this.newProject.fireSubBuildFinished(be);
                }
            }
            finally {
                this.newProject = null;
                for (Property p : this.properties) {
                    p.setProject(null);
                }
                if (this.output != null && this.out != null) {
                    FileUtils.close(this.out);
                }
                this.dir = savedDir;
                this.antFile = savedAntFile;
            }
        }
    }

    protected String getDefaultBuildFile() {
        return "build.xml";
    }

    private void overrideProperties() throws BuildException {
        HashSet<String> set = new HashSet<String>();
        for (int i = this.properties.size() - 1; i >= 0; --i) {
            Property p2 = this.properties.get(i);
            if (p2.getName() == null || p2.getName().isEmpty()) continue;
            if (set.contains(p2.getName())) {
                this.properties.remove(i);
                continue;
            }
            set.add(p2.getName());
        }
        this.properties.stream().peek(p -> p.setProject(this.newProject)).forEach(Property::execute);
        if (this.useNativeBasedir) {
            this.addAlmostAll(this.getProject().getInheritedProperties(), PropertyType.INHERITED);
        } else {
            this.getProject().copyInheritedProperties(this.newProject);
        }
    }

    private void addReferences() throws BuildException {
        HashMap<String, Object> thisReferences = new HashMap<String, Object>(this.getProject().getReferences());
        for (Reference ref : this.references) {
            String refid = ref.getRefId();
            if (refid == null) {
                throw new BuildException("the refid attribute is required for reference elements");
            }
            if (!thisReferences.containsKey(refid)) {
                this.log("Parent project doesn't contain any reference '" + refid + "'", 1);
                continue;
            }
            thisReferences.remove(refid);
            String toRefid = ref.getToRefid();
            if (toRefid == null) {
                toRefid = refid;
            }
            this.copyReference(refid, toRefid);
        }
        if (this.inheritRefs) {
            Hashtable<String, Object> newReferences = this.newProject.getReferences();
            for (String key : thisReferences.keySet()) {
                if (newReferences.containsKey(key)) continue;
                this.copyReference(key, key);
                this.newProject.inheritIDReferences(this.getProject());
            }
        }
    }

    private void copyReference(String oldKey, String newKey) {
        Object orig = this.getProject().getReference(oldKey);
        if (orig == null) {
            this.log("No object referenced by " + oldKey + ". Can't copy to " + newKey, 1);
            return;
        }
        Class<?> c = orig.getClass();
        Object copy = orig;
        try {
            Method cloneM = c.getMethod("clone", new Class[0]);
            if (cloneM != null) {
                copy = cloneM.invoke(orig, new Object[0]);
                this.log("Adding clone of reference " + oldKey, 4);
            }
        }
        catch (Exception cloneM) {
            // empty catch block
        }
        if (copy instanceof ProjectComponent) {
            ((ProjectComponent)copy).setProject(this.newProject);
        } else {
            try {
                Method setProjectM = c.getMethod("setProject", Project.class);
                if (setProjectM != null) {
                    setProjectM.invoke(copy, this.newProject);
                }
            }
            catch (NoSuchMethodException setProjectM) {
            }
            catch (Exception e2) {
                throw new BuildException("Error setting new project instance for reference with id " + oldKey, e2, this.getLocation());
            }
        }
        this.newProject.addReference(newKey, copy);
    }

    private void addAlmostAll(Map<?, ?> props, PropertyType type) {
        props.forEach((k, v) -> {
            String key = k.toString();
            if ("basedir".equals(key) || "ant.file".equals(key)) {
                return;
            }
            String value = v.toString();
            switch (type) {
                case PLAIN: {
                    if (this.newProject.getProperty(key) != null) break;
                    this.newProject.setNewProperty(key, value);
                    break;
                }
                case USER: {
                    this.newProject.setUserProperty(key, value);
                    break;
                }
                case INHERITED: {
                    this.newProject.setInheritedProperty(key, value);
                }
            }
        });
    }

    public void setDir(File dir) {
        this.dir = dir;
    }

    public void setAntfile(String antFile) {
        this.antFile = antFile;
    }

    public void setTarget(String targetToAdd) {
        if (targetToAdd.isEmpty()) {
            throw new BuildException("target attribute must not be empty");
        }
        this.targets.add(targetToAdd);
        this.targetAttributeSet = true;
    }

    public void setOutput(String outputFile) {
        this.output = outputFile;
    }

    public Property createProperty() {
        Property p = new Property(true, this.getProject());
        p.setProject(this.getNewProject());
        p.setTaskName("property");
        this.properties.add(p);
        return p;
    }

    public void addReference(Reference ref) {
        this.references.add(ref);
    }

    public void addConfiguredTarget(TargetElement t) {
        if (this.targetAttributeSet) {
            throw new BuildException("nested target is incompatible with the target attribute");
        }
        String name = t.getName();
        if (name.isEmpty()) {
            throw new BuildException("target name must not be empty");
        }
        this.targets.add(name);
    }

    public void addPropertyset(PropertySet ps) {
        this.propertySets.add(ps);
    }

    protected Project getNewProject() {
        if (this.newProject == null) {
            this.reinit();
        }
        return this.newProject;
    }

    private static enum PropertyType {
        PLAIN,
        INHERITED,
        USER;

    }

    public static class Reference
    extends org.apache.tools.ant.types.Reference {
        private String targetid = null;

        public void setToRefid(String targetid) {
            this.targetid = targetid;
        }

        public String getToRefid() {
            return this.targetid;
        }
    }

    public static class TargetElement {
        private String name;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}

