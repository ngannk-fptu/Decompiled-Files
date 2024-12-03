/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 *  org.apache.tools.ant.BuildListener
 *  org.apache.tools.ant.DemuxInputStream
 *  org.apache.tools.ant.DemuxOutputStream
 *  org.apache.tools.ant.Location
 *  org.apache.tools.ant.NoBannerLogger
 *  org.apache.tools.ant.Project
 *  org.apache.tools.ant.ProjectHelper
 *  org.apache.tools.ant.RuntimeConfigurable
 *  org.apache.tools.ant.Target
 *  org.apache.tools.ant.Task
 *  org.apache.tools.ant.UnknownElement
 *  org.apache.tools.ant.dispatch.DispatchUtils
 *  org.apache.tools.ant.helper.AntXMLContext
 *  org.apache.tools.ant.helper.ProjectHelper2$ElementHandler
 *  org.apache.tools.ant.helper.ProjectHelper2$TargetHandler
 */
package groovy.util;

import groovy.util.AntBuilderLocator;
import groovy.util.BuilderSupport;
import groovy.xml.QName;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.DemuxInputStream;
import org.apache.tools.ant.DemuxOutputStream;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.NoBannerLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.dispatch.DispatchUtils;
import org.apache.tools.ant.helper.AntXMLContext;
import org.apache.tools.ant.helper.ProjectHelper2;
import org.codehaus.groovy.ant.FileScanner;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.AttributesImpl;

public class AntBuilder
extends BuilderSupport {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private Project project;
    private final AntXMLContext antXmlContext;
    private final ProjectHelper2.ElementHandler antElementHandler = new ProjectHelper2.ElementHandler();
    private final ProjectHelper2.TargetHandler antTargetHandler = new ProjectHelper2.TargetHandler();
    private final Target collectorTarget;
    private final Target implicitTarget;
    private Target definingTarget;
    private Object lastCompletedNode;
    boolean insideTask;
    private boolean saveStreams = true;
    private static Integer streamCount = 0;
    private static InputStream savedIn;
    private static PrintStream savedErr;
    private static PrintStream savedOut;
    private static DemuxInputStream demuxInputStream;
    private static DemuxOutputStream demuxOutputStream;
    private static DemuxOutputStream demuxErrorStream;
    private static InputStream savedProjectInputStream;

    public AntBuilder() {
        this(AntBuilder.createProject());
    }

    public AntBuilder(Project project) {
        this(project, new Target());
    }

    public AntBuilder(Project project, Target owningTarget) {
        this.project = project;
        this.collectorTarget = owningTarget;
        this.antXmlContext = new AntXMLContext(project);
        this.collectorTarget.setProject(project);
        this.antXmlContext.setCurrentTarget(this.collectorTarget);
        this.antXmlContext.setLocator((Locator)new AntBuilderLocator());
        this.antXmlContext.setCurrentTargets(new HashMap());
        this.implicitTarget = new Target();
        this.implicitTarget.setProject(project);
        this.implicitTarget.setName("");
        this.antXmlContext.setImplicitTarget(this.implicitTarget);
        project.addDataTypeDefinition("fileScanner", FileScanner.class);
    }

    public AntBuilder(Task parentTask) {
        this(parentTask.getProject(), parentTask.getOwningTarget());
        UnknownElement ue = new UnknownElement(parentTask.getTaskName());
        ue.setProject(parentTask.getProject());
        ue.setTaskType(parentTask.getTaskType());
        ue.setTaskName(parentTask.getTaskName());
        ue.setLocation(parentTask.getLocation());
        ue.setOwningTarget(parentTask.getOwningTarget());
        ue.setRuntimeConfigurableWrapper(parentTask.getRuntimeConfigurableWrapper());
        parentTask.getRuntimeConfigurableWrapper().setProxy((Object)ue);
        this.antXmlContext.pushWrapper(parentTask.getRuntimeConfigurableWrapper());
    }

    public Project getProject() {
        return this.project;
    }

    public AntXMLContext getAntXmlContext() {
        return this.antXmlContext;
    }

    public boolean isSaveStreams() {
        return this.saveStreams;
    }

    public void setSaveStreams(boolean saveStreams) {
        this.saveStreams = saveStreams;
    }

    protected static Project createProject() {
        Project project = new Project();
        ProjectHelper helper = ProjectHelper.getProjectHelper();
        project.addReference("ant.projectHelper", (Object)helper);
        helper.getImportStack().addElement("AntBuilder");
        NoBannerLogger logger = new NoBannerLogger();
        logger.setMessageOutputLevel(2);
        logger.setOutputPrintStream(System.out);
        logger.setErrorPrintStream(System.err);
        project.addBuildListener((BuildListener)logger);
        project.init();
        project.getBaseDir();
        return project;
    }

    @Override
    protected void setParent(Object parent, Object child) {
    }

    @Override
    protected Object doInvokeMethod(String methodName, Object name, Object args) {
        super.doInvokeMethod(methodName, name, args);
        return this.lastCompletedNode;
    }

    /*
     * Exception decompiling
     */
    @Override
    protected void nodeCompleted(Object parent, Object node) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [19[SIMPLE_IF_TAKEN]], but top level block is 4[MONITOR]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private Object performTask(Task task) {
        Object object;
        Throwable reason = null;
        try {
            Method fireTaskStarted = Project.class.getDeclaredMethod("fireTaskStarted", Task.class);
            fireTaskStarted.setAccessible(true);
            fireTaskStarted.invoke((Object)this.project, task);
            Object realThing = task;
            task.maybeConfigure();
            if (task instanceof UnknownElement) {
                realThing = ((UnknownElement)task).getRealThing();
            }
            DispatchUtils.execute((Object)task);
            object = realThing != null ? realThing : task;
        }
        catch (BuildException ex) {
            try {
                if (ex.getLocation() == Location.UNKNOWN_LOCATION) {
                    ex.setLocation(task.getLocation());
                }
                reason = ex;
                throw ex;
                catch (Exception ex2) {
                    reason = ex2;
                    BuildException be = new BuildException((Throwable)ex2);
                    be.setLocation(task.getLocation());
                    throw be;
                }
                catch (Error ex3) {
                    reason = ex3;
                    throw ex3;
                }
            }
            catch (Throwable throwable) {
                try {
                    Method fireTaskFinished = Project.class.getDeclaredMethod("fireTaskFinished", Task.class, Throwable.class);
                    fireTaskFinished.setAccessible(true);
                    fireTaskFinished.invoke((Object)this.project, task, reason);
                    throw throwable;
                }
                catch (Exception e) {
                    BuildException be = new BuildException((Throwable)e);
                    be.setLocation(task.getLocation());
                    throw be;
                }
            }
        }
        try {
            Method fireTaskFinished = Project.class.getDeclaredMethod("fireTaskFinished", Task.class, Throwable.class);
            fireTaskFinished.setAccessible(true);
            fireTaskFinished.invoke((Object)this.project, task, reason);
            return object;
        }
        catch (Exception e) {
            BuildException be = new BuildException((Throwable)e);
            be.setLocation(task.getLocation());
            throw be;
        }
    }

    @Override
    protected Object createNode(Object tagName) {
        return this.createNode(tagName, Collections.EMPTY_MAP);
    }

    @Override
    protected Object createNode(Object name, Object value) {
        Object task = this.createNode(name);
        this.setText(task, value.toString());
        return task;
    }

    @Override
    protected Object createNode(Object name, Map attributes, Object value) {
        Object task = this.createNode(name, attributes);
        this.setText(task, value.toString());
        return task;
    }

    protected static Attributes buildAttributes(Map attributes) {
        AttributesImpl attr = new AttributesImpl();
        Iterator iterator = attributes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry o;
            Map.Entry entry = o = iterator.next();
            String attributeName = (String)entry.getKey();
            String attributeValue = String.valueOf(entry.getValue());
            attr.addAttribute(null, attributeName, attributeName, "CDATA", attributeValue);
        }
        return attr;
    }

    @Override
    protected Object createNode(Object name, Map attributes) {
        Attributes attrs = AntBuilder.buildAttributes(attributes);
        String tagName = name.toString();
        String ns = "";
        if (name instanceof QName) {
            QName q = (QName)name;
            tagName = q.getLocalPart();
            ns = q.getNamespaceURI();
        }
        if ("import".equals(name)) {
            this.antXmlContext.setCurrentTarget(this.implicitTarget);
        } else {
            if ("target".equals(name) && !this.insideTask) {
                return this.onStartTarget(attrs, tagName, ns);
            }
            if ("defineTarget".equals(name) && !this.insideTask) {
                return this.onDefineTarget(attrs, "target", ns);
            }
        }
        try {
            this.antElementHandler.onStartElement(ns, tagName, tagName, attrs, this.antXmlContext);
        }
        catch (SAXParseException e) {
            this.log.log(Level.SEVERE, "Caught: " + e, e);
        }
        this.insideTask = true;
        RuntimeConfigurable wrapper = (RuntimeConfigurable)this.antXmlContext.getWrapperStack().lastElement();
        return wrapper.getProxy();
    }

    private Target onDefineTarget(Attributes attrs, String tagName, String ns) {
        Target target = new Target();
        target.setProject(this.project);
        target.setLocation(new Location(this.antXmlContext.getLocator()));
        try {
            this.antTargetHandler.onStartElement(ns, tagName, tagName, attrs, this.antXmlContext);
            Target newTarget = (Target)this.getProject().getTargets().get(attrs.getValue("name"));
            this.antXmlContext.setCurrentTarget(newTarget);
            this.definingTarget = newTarget;
            return newTarget;
        }
        catch (SAXParseException e) {
            this.log.log(Level.SEVERE, "Caught: " + e, e);
            return null;
        }
    }

    private Target onStartTarget(Attributes attrs, String tagName, String ns) {
        Target target = new Target();
        target.setProject(this.project);
        target.setLocation(new Location(this.antXmlContext.getLocator()));
        try {
            this.antTargetHandler.onStartElement(ns, tagName, tagName, attrs, this.antXmlContext);
            Target newTarget = (Target)this.getProject().getTargets().get(attrs.getValue("name"));
            Vector targets = new Vector();
            Enumeration deps = newTarget.getDependencies();
            while (deps.hasMoreElements()) {
                String targetName = (String)deps.nextElement();
                targets.add(this.project.getTargets().get(targetName));
            }
            this.getProject().executeSortedTargets(targets);
            this.antXmlContext.setCurrentTarget(newTarget);
            return newTarget;
        }
        catch (SAXParseException e) {
            this.log.log(Level.SEVERE, "Caught: " + e, e);
            return null;
        }
    }

    protected void setText(Object task, String text) {
        char[] characters = text.toCharArray();
        try {
            this.antElementHandler.characters(characters, 0, characters.length, this.antXmlContext);
        }
        catch (SAXParseException e) {
            this.log.log(Level.WARNING, "SetText failed: " + task + ". Reason: " + e, e);
        }
    }

    public Project getAntProject() {
        return this.project;
    }
}

