/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.util.DOMElementWriter;
import org.apache.tools.ant.util.StringUtils;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XmlLogger
implements BuildLogger {
    private int msgOutputLevel = 4;
    private PrintStream outStream;
    private static DocumentBuilder builder = XmlLogger.getDocumentBuilder();
    private static final String BUILD_TAG = "build";
    private static final String TARGET_TAG = "target";
    private static final String TASK_TAG = "task";
    private static final String MESSAGE_TAG = "message";
    private static final String NAME_ATTR = "name";
    private static final String TIME_ATTR = "time";
    private static final String PRIORITY_ATTR = "priority";
    private static final String LOCATION_ATTR = "location";
    private static final String ERROR_ATTR = "error";
    private static final String STACKTRACE_TAG = "stacktrace";
    private Document doc = builder.newDocument();
    private Map<Task, TimedElement> tasks = new Hashtable<Task, TimedElement>();
    private Map<Target, TimedElement> targets = new Hashtable<Target, TimedElement>();
    private Map<Thread, Stack<TimedElement>> threadStacks = new Hashtable<Thread, Stack<TimedElement>>();
    private TimedElement buildElement = null;

    private static DocumentBuilder getDocumentBuilder() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch (Exception exc) {
            throw new ExceptionInInitializerError(exc);
        }
    }

    @Override
    public void buildStarted(BuildEvent event) {
        this.buildElement = new TimedElement();
        this.buildElement.startTime = System.currentTimeMillis();
        this.buildElement.element = this.doc.createElement(BUILD_TAG);
    }

    @Override
    public void buildFinished(BuildEvent event) {
        long totalTime = System.currentTimeMillis() - this.buildElement.startTime;
        this.buildElement.element.setAttribute(TIME_ATTR, DefaultLogger.formatTime(totalTime));
        if (event.getException() != null) {
            this.buildElement.element.setAttribute(ERROR_ATTR, event.getException().toString());
            Throwable t = event.getException();
            CDATASection errText = this.doc.createCDATASection(StringUtils.getStackTrace(t));
            Element stacktrace = this.doc.createElement(STACKTRACE_TAG);
            stacktrace.appendChild(errText);
            this.synchronizedAppend(this.buildElement.element, stacktrace);
        }
        String outFilename = this.getProperty(event, "XmlLogger.file", "log.xml");
        String xslUri = this.getProperty(event, "ant.XmlLogger.stylesheet.uri", "log.xsl");
        try (PrintStream stream = this.outStream == null ? Files.newOutputStream(Paths.get(outFilename, new String[0]), new OpenOption[0]) : this.outStream;
             OutputStreamWriter out = new OutputStreamWriter((OutputStream)stream, StandardCharsets.UTF_8);){
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            if (!xslUri.isEmpty()) {
                out.write("<?xml-stylesheet type=\"text/xsl\" href=\"" + xslUri + "\"?>\n\n");
            }
            new DOMElementWriter().write(this.buildElement.element, out, 0, "\t");
            ((Writer)out).flush();
        }
        catch (IOException exc) {
            throw new BuildException("Unable to write log file", exc);
        }
        this.buildElement = null;
    }

    private String getProperty(BuildEvent event, String propertyName, String defaultValue) {
        String rv = defaultValue;
        if (event != null && event.getProject() != null && event.getProject().getProperty(propertyName) != null) {
            rv = event.getProject().getProperty(propertyName);
        }
        return rv;
    }

    private Stack<TimedElement> getStack() {
        return this.threadStacks.computeIfAbsent(Thread.currentThread(), k -> new Stack());
    }

    @Override
    public void targetStarted(BuildEvent event) {
        Target target = event.getTarget();
        TimedElement targetElement = new TimedElement();
        targetElement.startTime = System.currentTimeMillis();
        targetElement.element = this.doc.createElement(TARGET_TAG);
        targetElement.element.setAttribute(NAME_ATTR, target.getName());
        this.targets.put(target, targetElement);
        this.getStack().push(targetElement);
    }

    @Override
    public void targetFinished(BuildEvent event) {
        Target target = event.getTarget();
        TimedElement targetElement = this.targets.get(target);
        if (targetElement != null) {
            long totalTime = System.currentTimeMillis() - targetElement.startTime;
            targetElement.element.setAttribute(TIME_ATTR, DefaultLogger.formatTime(totalTime));
            TimedElement parentElement = null;
            Stack<TimedElement> threadStack = this.getStack();
            if (!threadStack.empty()) {
                TimedElement poppedStack = threadStack.pop();
                if (poppedStack != targetElement) {
                    throw new RuntimeException("Mismatch - popped element = " + poppedStack + " finished target element = " + targetElement);
                }
                if (!threadStack.empty()) {
                    parentElement = threadStack.peek();
                }
            }
            if (parentElement == null) {
                this.synchronizedAppend(this.buildElement.element, targetElement.element);
            } else {
                this.synchronizedAppend(parentElement.element, targetElement.element);
            }
        }
        this.targets.remove(target);
    }

    @Override
    public void taskStarted(BuildEvent event) {
        TimedElement taskElement = new TimedElement();
        taskElement.startTime = System.currentTimeMillis();
        taskElement.element = this.doc.createElement(TASK_TAG);
        Task task = event.getTask();
        String name = event.getTask().getTaskName();
        if (name == null) {
            name = "";
        }
        taskElement.element.setAttribute(NAME_ATTR, name);
        taskElement.element.setAttribute(LOCATION_ATTR, event.getTask().getLocation().toString());
        this.tasks.put(task, taskElement);
        this.getStack().push(taskElement);
    }

    @Override
    public void taskFinished(BuildEvent event) {
        TimedElement poppedStack;
        Task task = event.getTask();
        TimedElement taskElement = this.tasks.get(task);
        if (taskElement == null) {
            throw new RuntimeException("Unknown task " + task + " not in " + this.tasks);
        }
        long totalTime = System.currentTimeMillis() - taskElement.startTime;
        taskElement.element.setAttribute(TIME_ATTR, DefaultLogger.formatTime(totalTime));
        Target target = task.getOwningTarget();
        TimedElement targetElement = null;
        if (target != null) {
            targetElement = this.targets.get(target);
        }
        if (targetElement == null) {
            this.synchronizedAppend(this.buildElement.element, taskElement.element);
        } else {
            this.synchronizedAppend(targetElement.element, taskElement.element);
        }
        Stack<TimedElement> threadStack = this.getStack();
        if (!threadStack.empty() && (poppedStack = threadStack.pop()) != taskElement) {
            throw new RuntimeException("Mismatch - popped element = " + poppedStack + " finished task element = " + taskElement);
        }
        this.tasks.remove(task);
    }

    private TimedElement getTaskElement(Task task) {
        TimedElement element = this.tasks.get(task);
        if (element != null) {
            return element;
        }
        HashSet<Task> knownTasks = new HashSet<Task>(this.tasks.keySet());
        for (Task t : knownTasks) {
            if (!(t instanceof UnknownElement) || ((UnknownElement)t).getTask() != task) continue;
            return this.tasks.get(t);
        }
        return null;
    }

    @Override
    public void messageLogged(BuildEvent event) {
        String name;
        int priority = event.getPriority();
        if (priority > this.msgOutputLevel) {
            return;
        }
        Element messageElement = this.doc.createElement(MESSAGE_TAG);
        switch (priority) {
            case 0: {
                name = ERROR_ATTR;
                break;
            }
            case 1: {
                name = "warn";
                break;
            }
            case 2: {
                name = "info";
                break;
            }
            default: {
                name = "debug";
            }
        }
        messageElement.setAttribute(PRIORITY_ATTR, name);
        Throwable ex = event.getException();
        if (4 <= this.msgOutputLevel && ex != null) {
            CDATASection errText = this.doc.createCDATASection(StringUtils.getStackTrace(ex));
            Element stacktrace = this.doc.createElement(STACKTRACE_TAG);
            stacktrace.appendChild(errText);
            this.synchronizedAppend(this.buildElement.element, stacktrace);
        }
        CDATASection messageText = this.doc.createCDATASection(event.getMessage());
        messageElement.appendChild(messageText);
        TimedElement parentElement = null;
        Task task = event.getTask();
        Target target = event.getTarget();
        if (task != null) {
            parentElement = this.getTaskElement(task);
        }
        if (parentElement == null && target != null) {
            parentElement = this.targets.get(target);
        }
        if (parentElement != null) {
            this.synchronizedAppend(parentElement.element, messageElement);
        } else {
            this.synchronizedAppend(this.buildElement.element, messageElement);
        }
    }

    @Override
    public void setMessageOutputLevel(int level) {
        this.msgOutputLevel = level;
    }

    @Override
    public int getMessageOutputLevel() {
        return this.msgOutputLevel;
    }

    @Override
    public void setOutputPrintStream(PrintStream output) {
        this.outStream = new PrintStream(output, true);
    }

    @Override
    public void setEmacsMode(boolean emacsMode) {
    }

    @Override
    public void setErrorPrintStream(PrintStream err) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void synchronizedAppend(Node parent, Node child) {
        Node node = parent;
        synchronized (node) {
            parent.appendChild(child);
        }
    }

    private static class TimedElement {
        private long startTime;
        private Element element;

        private TimedElement() {
        }

        public String toString() {
            return this.element.getTagName() + ":" + this.element.getAttribute(XmlLogger.NAME_ATTR);
        }
    }
}

