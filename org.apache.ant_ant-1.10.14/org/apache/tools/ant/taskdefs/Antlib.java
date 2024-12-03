/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.ProjectHelperRepository;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.taskdefs.AntlibDefinition;
import org.apache.tools.ant.types.resources.URLResource;

public class Antlib
extends Task
implements TaskContainer {
    public static final String TAG = "antlib";
    private ClassLoader classLoader;
    private String uri = "";
    private List<Task> tasks = new ArrayList<Task>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Antlib createAntlib(Project project, URL antlibUrl, String uri) {
        try {
            URLConnection conn = antlibUrl.openConnection();
            conn.setUseCaches(false);
            conn.connect();
        }
        catch (IOException ex) {
            throw new BuildException("Unable to find " + antlibUrl, ex);
        }
        ComponentHelper helper = ComponentHelper.getComponentHelper(project);
        helper.enterAntLib(uri);
        URLResource antlibResource = new URLResource(antlibUrl);
        try {
            UnknownElement ue;
            ProjectHelper parser = null;
            Object p = project.getReference("ant.projectHelper");
            if (p instanceof ProjectHelper && !(parser = (ProjectHelper)p).canParseAntlibDescriptor(antlibResource)) {
                parser = null;
            }
            if (parser == null) {
                ProjectHelperRepository helperRepository = ProjectHelperRepository.getInstance();
                parser = helperRepository.getProjectHelperForAntlib(antlibResource);
            }
            if (!TAG.equals((ue = parser.parseAntlibDescriptor(project, antlibResource)).getTag())) {
                throw new BuildException("Unexpected tag " + ue.getTag() + " expecting " + TAG, ue.getLocation());
            }
            Antlib antlib = new Antlib();
            antlib.setProject(project);
            antlib.setLocation(ue.getLocation());
            antlib.setTaskName(TAG);
            antlib.init();
            ue.configure(antlib);
            Antlib antlib2 = antlib;
            return antlib2;
        }
        finally {
            helper.exitAntLib();
        }
    }

    protected void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    protected void setURI(String uri) {
        this.uri = uri;
    }

    private ClassLoader getClassLoader() {
        if (this.classLoader == null) {
            this.classLoader = Antlib.class.getClassLoader();
        }
        return this.classLoader;
    }

    @Override
    public void addTask(Task nestedTask) {
        this.tasks.add(nestedTask);
    }

    @Override
    public void execute() {
        for (Task task : this.tasks) {
            UnknownElement ue = (UnknownElement)task;
            this.setLocation(ue.getLocation());
            ue.maybeConfigure();
            Object configuredObject = ue.getRealThing();
            if (configuredObject == null) continue;
            if (!(configuredObject instanceof AntlibDefinition)) {
                throw new BuildException("Invalid task in antlib %s %s does not extend %s", ue.getTag(), configuredObject.getClass(), AntlibDefinition.class.getName());
            }
            AntlibDefinition def = (AntlibDefinition)configuredObject;
            def.setURI(this.uri);
            def.setAntlibClassLoader(this.getClassLoader());
            def.init();
            def.execute();
        }
    }
}

