/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.TemplateElement;
import freemarker.core.TemplateObject;
import freemarker.core._CoreAPI;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.template.Template;
import freemarker.template.utility.CollectionUtils;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

public class TemplateException
extends Exception {
    private static final String FTL_INSTRUCTION_STACK_TRACE_TITLE = "FTL stack trace (\"~\" means nesting-related):";
    private transient _ErrorDescriptionBuilder descriptionBuilder;
    private final transient Environment env;
    private final transient Expression blamedExpression;
    private transient TemplateElement[] ftlInstructionStackSnapshot;
    private String renderedFtlInstructionStackSnapshot;
    private String renderedFtlInstructionStackSnapshotTop;
    private String description;
    private transient String messageWithoutStackTop;
    private transient String message;
    private boolean blamedExpressionStringCalculated;
    private String blamedExpressionString;
    private boolean positionsCalculated;
    private String templateName;
    private String templateSourceName;
    private Integer lineNumber;
    private Integer columnNumber;
    private Integer endLineNumber;
    private Integer endColumnNumber;
    private transient Object lock = new Object();
    private transient ThreadLocal messageWasAlreadyPrintedForThisTrace;

    public TemplateException(Environment env) {
        this(null, null, env);
    }

    public TemplateException(String description, Environment env) {
        this(description, null, env);
    }

    public TemplateException(Exception cause, Environment env) {
        this(null, cause, env);
    }

    public TemplateException(Throwable cause, Environment env) {
        this(null, cause, env);
    }

    public TemplateException(String description, Exception cause, Environment env) {
        this(description, cause, env, null, null);
    }

    public TemplateException(String description, Throwable cause, Environment env) {
        this(description, cause, env, null, null);
    }

    protected TemplateException(Throwable cause, Environment env, Expression blamedExpr, _ErrorDescriptionBuilder descriptionBuilder) {
        this(null, cause, env, blamedExpr, descriptionBuilder);
    }

    private TemplateException(String renderedDescription, Throwable cause, Environment env, Expression blamedExpression, _ErrorDescriptionBuilder descriptionBuilder) {
        super(cause);
        if (env == null) {
            env = Environment.getCurrentEnvironment();
        }
        this.env = env;
        this.blamedExpression = blamedExpression;
        this.descriptionBuilder = descriptionBuilder;
        this.description = renderedDescription;
        if (env != null) {
            this.ftlInstructionStackSnapshot = _CoreAPI.getInstructionStackSnapshot(env);
        }
    }

    private void renderMessages() {
        String description = this.getDescription();
        this.messageWithoutStackTop = description != null && description.length() != 0 ? description : (this.getCause() != null ? "No error description was specified for this error; low-level message: " + this.getCause().getClass().getName() + ": " + this.getCause().getMessage() : "[No error description was available.]");
        String stackTopFew = this.getFTLInstructionStackTopFew();
        if (stackTopFew != null) {
            this.message = this.messageWithoutStackTop + "\n\n" + "----" + "\n" + FTL_INSTRUCTION_STACK_TRACE_TITLE + "\n" + stackTopFew + "----";
            this.messageWithoutStackTop = this.message.substring(0, this.messageWithoutStackTop.length());
        } else {
            this.message = this.messageWithoutStackTop;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void calculatePosition() {
        Object object = this.lock;
        synchronized (object) {
            if (!this.positionsCalculated) {
                Expression templateObject;
                TemplateObject templateObject2 = this.blamedExpression != null ? this.blamedExpression : (templateObject = this.ftlInstructionStackSnapshot != null && this.ftlInstructionStackSnapshot.length != 0 ? this.ftlInstructionStackSnapshot[0] : null);
                if (templateObject != null && templateObject.getBeginLine() > 0) {
                    Template template = templateObject.getTemplate();
                    this.templateName = template != null ? template.getName() : null;
                    this.templateSourceName = template != null ? template.getSourceName() : null;
                    this.lineNumber = templateObject.getBeginLine();
                    this.columnNumber = templateObject.getBeginColumn();
                    this.endLineNumber = templateObject.getEndLine();
                    this.endColumnNumber = templateObject.getEndColumn();
                }
                this.positionsCalculated = true;
                this.deleteFTLInstructionStackSnapshotIfNotNeeded();
            }
        }
    }

    @Deprecated
    public Exception getCauseException() {
        return this.getCause() instanceof Exception ? (Exception)this.getCause() : new Exception("Wrapped to Exception: " + this.getCause(), this.getCause());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getFTLInstructionStack() {
        Object object = this.lock;
        synchronized (object) {
            if (this.ftlInstructionStackSnapshot != null || this.renderedFtlInstructionStackSnapshot != null) {
                if (this.renderedFtlInstructionStackSnapshot == null) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    _CoreAPI.outputInstructionStack(this.ftlInstructionStackSnapshot, false, pw);
                    pw.close();
                    if (this.renderedFtlInstructionStackSnapshot == null) {
                        this.renderedFtlInstructionStackSnapshot = sw.toString();
                        this.deleteFTLInstructionStackSnapshotIfNotNeeded();
                    }
                }
                return this.renderedFtlInstructionStackSnapshot;
            }
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String getFTLInstructionStackTopFew() {
        Object object = this.lock;
        synchronized (object) {
            if (this.ftlInstructionStackSnapshot != null || this.renderedFtlInstructionStackSnapshotTop != null) {
                if (this.renderedFtlInstructionStackSnapshotTop == null) {
                    String s;
                    int stackSize = this.ftlInstructionStackSnapshot.length;
                    if (stackSize == 0) {
                        s = "";
                    } else {
                        StringWriter sw = new StringWriter();
                        _CoreAPI.outputInstructionStack(this.ftlInstructionStackSnapshot, true, sw);
                        s = sw.toString();
                    }
                    if (this.renderedFtlInstructionStackSnapshotTop == null) {
                        this.renderedFtlInstructionStackSnapshotTop = s;
                        this.deleteFTLInstructionStackSnapshotIfNotNeeded();
                    }
                }
                return this.renderedFtlInstructionStackSnapshotTop.length() != 0 ? this.renderedFtlInstructionStackSnapshotTop : null;
            }
            return null;
        }
    }

    private void deleteFTLInstructionStackSnapshotIfNotNeeded() {
        if (this.renderedFtlInstructionStackSnapshot != null && this.renderedFtlInstructionStackSnapshotTop != null && (this.positionsCalculated || this.blamedExpression != null)) {
            this.ftlInstructionStackSnapshot = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String getDescription() {
        Object object = this.lock;
        synchronized (object) {
            if (this.description == null && this.descriptionBuilder != null) {
                this.description = this.descriptionBuilder.toString(this.getFailingInstruction(), this.env != null ? this.env.getShowErrorTips() : true);
                this.descriptionBuilder = null;
            }
            return this.description;
        }
    }

    private TemplateElement getFailingInstruction() {
        if (this.ftlInstructionStackSnapshot != null && this.ftlInstructionStackSnapshot.length > 0) {
            return this.ftlInstructionStackSnapshot[0];
        }
        return null;
    }

    public Environment getEnvironment() {
        return this.env;
    }

    @Override
    public void printStackTrace(PrintStream out) {
        this.printStackTrace(out, true, true, true);
    }

    @Override
    public void printStackTrace(PrintWriter out) {
        this.printStackTrace(out, true, true, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void printStackTrace(PrintWriter out, boolean heading, boolean ftlStackTrace, boolean javaStackTrace) {
        PrintWriter printWriter = out;
        synchronized (printWriter) {
            this.printStackTrace(new PrintWriterStackTraceWriter(out), heading, ftlStackTrace, javaStackTrace);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void printStackTrace(PrintStream out, boolean heading, boolean ftlStackTrace, boolean javaStackTrace) {
        PrintStream printStream = out;
        synchronized (printStream) {
            this.printStackTrace(new PrintStreamStackTraceWriter(out), heading, ftlStackTrace, javaStackTrace);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void printStackTrace(StackTraceWriter out, boolean heading, boolean ftlStackTrace, boolean javaStackTrace) {
        StackTraceWriter stackTraceWriter = out;
        synchronized (stackTraceWriter) {
            Object stackTrace;
            if (heading) {
                out.println("FreeMarker template error:");
            }
            if (ftlStackTrace) {
                stackTrace = this.getFTLInstructionStack();
                if (stackTrace != null) {
                    out.println(this.getMessageWithoutStackTop());
                    out.println();
                    out.println("----");
                    out.println(FTL_INSTRUCTION_STACK_TRACE_TITLE);
                    out.print(stackTrace);
                    out.println("----");
                } else {
                    ftlStackTrace = false;
                    javaStackTrace = true;
                }
            }
            if (javaStackTrace) {
                Throwable causeCause;
                if (ftlStackTrace) {
                    out.println();
                    out.println("Java stack trace (for programmers):");
                    out.println("----");
                    stackTrace = this.lock;
                    synchronized (stackTrace) {
                        if (this.messageWasAlreadyPrintedForThisTrace == null) {
                            this.messageWasAlreadyPrintedForThisTrace = new ThreadLocal();
                        }
                        this.messageWasAlreadyPrintedForThisTrace.set(Boolean.TRUE);
                    }
                    try {
                        out.printStandardStackTrace(this);
                    }
                    finally {
                        this.messageWasAlreadyPrintedForThisTrace.set(Boolean.FALSE);
                    }
                }
                out.printStandardStackTrace(this);
                if (this.getCause() != null && (causeCause = this.getCause().getCause()) == null) {
                    try {
                        Method m = this.getCause().getClass().getMethod("getRootCause", CollectionUtils.EMPTY_CLASS_ARRAY);
                        Throwable rootCause = (Throwable)m.invoke((Object)this.getCause(), CollectionUtils.EMPTY_OBJECT_ARRAY);
                        if (rootCause != null) {
                            out.println("ServletException root cause: ");
                            out.printStandardStackTrace(rootCause);
                        }
                    }
                    catch (Throwable throwable) {
                        // empty catch block
                    }
                }
            }
        }
    }

    public void printStandardStackTrace(PrintStream ps) {
        super.printStackTrace(ps);
    }

    public void printStandardStackTrace(PrintWriter pw) {
        super.printStackTrace(pw);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getMessage() {
        if (this.messageWasAlreadyPrintedForThisTrace != null && this.messageWasAlreadyPrintedForThisTrace.get() == Boolean.TRUE) {
            return "[... Exception message was already printed; see it above ...]";
        }
        Object object = this.lock;
        synchronized (object) {
            if (this.message == null) {
                this.renderMessages();
            }
            return this.message;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getMessageWithoutStackTop() {
        Object object = this.lock;
        synchronized (object) {
            if (this.messageWithoutStackTop == null) {
                this.renderMessages();
            }
            return this.messageWithoutStackTop;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Integer getLineNumber() {
        Object object = this.lock;
        synchronized (object) {
            if (!this.positionsCalculated) {
                this.calculatePosition();
            }
            return this.lineNumber;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    public String getTemplateName() {
        Object object = this.lock;
        synchronized (object) {
            if (!this.positionsCalculated) {
                this.calculatePosition();
            }
            return this.templateName;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getTemplateSourceName() {
        Object object = this.lock;
        synchronized (object) {
            if (!this.positionsCalculated) {
                this.calculatePosition();
            }
            return this.templateSourceName;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Integer getColumnNumber() {
        Object object = this.lock;
        synchronized (object) {
            if (!this.positionsCalculated) {
                this.calculatePosition();
            }
            return this.columnNumber;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Integer getEndLineNumber() {
        Object object = this.lock;
        synchronized (object) {
            if (!this.positionsCalculated) {
                this.calculatePosition();
            }
            return this.endLineNumber;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Integer getEndColumnNumber() {
        Object object = this.lock;
        synchronized (object) {
            if (!this.positionsCalculated) {
                this.calculatePosition();
            }
            return this.endColumnNumber;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getBlamedExpressionString() {
        Object object = this.lock;
        synchronized (object) {
            if (!this.blamedExpressionStringCalculated) {
                if (this.blamedExpression != null) {
                    this.blamedExpressionString = this.blamedExpression.getCanonicalForm();
                }
                this.blamedExpressionStringCalculated = true;
            }
            return this.blamedExpressionString;
        }
    }

    Expression getBlamedExpression() {
        return this.blamedExpression;
    }

    private void writeObject(ObjectOutputStream out) throws IOException, ClassNotFoundException {
        this.getFTLInstructionStack();
        this.getFTLInstructionStackTopFew();
        this.getDescription();
        this.calculatePosition();
        this.getBlamedExpressionString();
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.lock = new Object();
        in.defaultReadObject();
    }

    private static class PrintWriterStackTraceWriter
    implements StackTraceWriter {
        private final PrintWriter out;

        PrintWriterStackTraceWriter(PrintWriter out) {
            this.out = out;
        }

        @Override
        public void print(Object obj) {
            this.out.print(obj);
        }

        @Override
        public void println(Object obj) {
            this.out.println(obj);
        }

        @Override
        public void println() {
            this.out.println();
        }

        @Override
        public void printStandardStackTrace(Throwable exception) {
            if (exception instanceof TemplateException) {
                ((TemplateException)exception).printStandardStackTrace(this.out);
            } else {
                exception.printStackTrace(this.out);
            }
        }
    }

    private static class PrintStreamStackTraceWriter
    implements StackTraceWriter {
        private final PrintStream out;

        PrintStreamStackTraceWriter(PrintStream out) {
            this.out = out;
        }

        @Override
        public void print(Object obj) {
            this.out.print(obj);
        }

        @Override
        public void println(Object obj) {
            this.out.println(obj);
        }

        @Override
        public void println() {
            this.out.println();
        }

        @Override
        public void printStandardStackTrace(Throwable exception) {
            if (exception instanceof TemplateException) {
                ((TemplateException)exception).printStandardStackTrace(this.out);
            } else {
                exception.printStackTrace(this.out);
            }
        }
    }

    private static interface StackTraceWriter {
        public void print(Object var1);

        public void println(Object var1);

        public void println();

        public void printStandardStackTrace(Throwable var1);
    }
}

