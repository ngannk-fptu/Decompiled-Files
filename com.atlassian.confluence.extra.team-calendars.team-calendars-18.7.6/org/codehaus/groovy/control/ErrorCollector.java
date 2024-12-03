/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.Janitor;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.ExceptionMessage;
import org.codehaus.groovy.control.messages.LocatedMessage;
import org.codehaus.groovy.control.messages.Message;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.control.messages.WarningMessage;
import org.codehaus.groovy.syntax.CSTNode;
import org.codehaus.groovy.syntax.SyntaxException;

public class ErrorCollector {
    protected LinkedList warnings = null;
    protected LinkedList errors = null;
    protected CompilerConfiguration configuration;

    public ErrorCollector(CompilerConfiguration configuration) {
        this.configuration = configuration;
    }

    public void addCollectorContents(ErrorCollector er) {
        if (er.errors != null) {
            if (this.errors == null) {
                this.errors = er.errors;
            } else {
                this.errors.addAll(er.errors);
            }
        }
        if (er.warnings != null) {
            if (this.warnings == null) {
                this.warnings = er.warnings;
            } else {
                this.warnings.addAll(er.warnings);
            }
        }
    }

    public void addErrorAndContinue(Message message) {
        if (this.errors == null) {
            this.errors = new LinkedList();
        }
        this.errors.add(message);
    }

    public void addError(Message message) throws CompilationFailedException {
        this.addErrorAndContinue(message);
        if (this.errors != null && this.errors.size() >= this.configuration.getTolerance()) {
            this.failIfErrors();
        }
    }

    public void addError(Message message, boolean fatal) throws CompilationFailedException {
        if (fatal) {
            this.addFatalError(message);
        } else {
            this.addError(message);
        }
    }

    public void addError(SyntaxException error, SourceUnit source) throws CompilationFailedException {
        this.addError(Message.create(error, source), error.isFatal());
    }

    public void addError(String text, CSTNode context, SourceUnit source) throws CompilationFailedException {
        this.addError(new LocatedMessage(text, context, source));
    }

    public void addFatalError(Message message) throws CompilationFailedException {
        this.addError(message);
        this.failIfErrors();
    }

    public void addException(Exception cause, SourceUnit source) throws CompilationFailedException {
        this.addError(new ExceptionMessage(cause, this.configuration.getDebug(), source));
        this.failIfErrors();
    }

    public boolean hasErrors() {
        return this.errors != null;
    }

    public CompilerConfiguration getConfiguration() {
        return this.configuration;
    }

    public boolean hasWarnings() {
        return this.warnings != null;
    }

    public List getWarnings() {
        return this.warnings;
    }

    public List getErrors() {
        return this.errors;
    }

    public int getWarningCount() {
        return this.warnings == null ? 0 : this.warnings.size();
    }

    public int getErrorCount() {
        return this.errors == null ? 0 : this.errors.size();
    }

    public WarningMessage getWarning(int index) {
        if (index < this.getWarningCount()) {
            return (WarningMessage)this.warnings.get(index);
        }
        return null;
    }

    public Message getError(int index) {
        if (index < this.getErrorCount()) {
            return (Message)this.errors.get(index);
        }
        return null;
    }

    public Message getLastError() {
        return (Message)this.errors.getLast();
    }

    public SyntaxException getSyntaxError(int index) {
        SyntaxException exception = null;
        Message message = this.getError(index);
        if (message != null && message instanceof SyntaxErrorMessage) {
            exception = ((SyntaxErrorMessage)message).getCause();
        }
        return exception;
    }

    public Exception getException(int index) {
        Exception exception = null;
        Message message = this.getError(index);
        if (message != null) {
            if (message instanceof ExceptionMessage) {
                exception = ((ExceptionMessage)message).getCause();
            } else if (message instanceof SyntaxErrorMessage) {
                exception = ((SyntaxErrorMessage)message).getCause();
            }
        }
        return exception;
    }

    public void addWarning(WarningMessage message) {
        if (message.isRelevant(this.configuration.getWarningLevel())) {
            if (this.warnings == null) {
                this.warnings = new LinkedList();
            }
            this.warnings.add(message);
        }
    }

    public void addWarning(int importance, String text, CSTNode context, SourceUnit source) {
        if (WarningMessage.isRelevant(importance, this.configuration.getWarningLevel())) {
            this.addWarning(new WarningMessage(importance, text, context, source));
        }
    }

    public void addWarning(int importance, String text, Object data, CSTNode context, SourceUnit source) {
        if (WarningMessage.isRelevant(importance, this.configuration.getWarningLevel())) {
            this.addWarning(new WarningMessage(importance, text, data, context, source));
        }
    }

    protected void failIfErrors() throws CompilationFailedException {
        if (this.hasErrors()) {
            throw new MultipleCompilationErrorsException(this);
        }
    }

    private void write(PrintWriter writer, Janitor janitor, List messages, String txt) {
        if (messages == null || messages.isEmpty()) {
            return;
        }
        for (Message message : messages) {
            message.write(writer, janitor);
            if (this.configuration.getDebug() && message instanceof SyntaxErrorMessage) {
                SyntaxErrorMessage sem = (SyntaxErrorMessage)message;
                sem.getCause().printStackTrace(writer);
            }
            writer.println();
        }
        writer.print(messages.size());
        writer.print(" " + txt);
        if (messages.size() > 1) {
            writer.print("s");
        }
        writer.println();
    }

    public void write(PrintWriter writer, Janitor janitor) {
        this.write(writer, janitor, this.warnings, "warning");
        this.write(writer, janitor, this.errors, "error");
    }
}

