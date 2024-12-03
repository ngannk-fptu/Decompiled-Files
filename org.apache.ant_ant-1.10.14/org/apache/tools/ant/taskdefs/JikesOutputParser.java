/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;

@Deprecated
public class JikesOutputParser
implements ExecuteStreamHandler {
    protected Task task;
    protected boolean errorFlag = false;
    protected int errors;
    protected int warnings;
    protected boolean error = false;
    protected boolean emacsMode;
    protected BufferedReader br;

    @Override
    public void setProcessInputStream(OutputStream os) {
    }

    @Override
    public void setProcessErrorStream(InputStream is) {
    }

    @Override
    public void setProcessOutputStream(InputStream is) throws IOException {
        this.br = new BufferedReader(new InputStreamReader(is));
    }

    @Override
    public void start() throws IOException {
        this.parseOutput(this.br);
    }

    @Override
    public void stop() {
    }

    protected JikesOutputParser(Task task, boolean emacsMode) {
        System.err.println("As of Ant 1.2 released in October 2000, the JikesOutputParser class");
        System.err.println("is considered to be dead code by the Ant developers and is unmaintained.");
        System.err.println("Don't use it!");
        this.task = task;
        this.emacsMode = emacsMode;
    }

    protected void parseOutput(BufferedReader reader) throws IOException {
        if (this.emacsMode) {
            this.parseEmacsOutput(reader);
        } else {
            this.parseStandardOutput(reader);
        }
    }

    private void parseStandardOutput(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            String lower = line.toLowerCase();
            if (line.trim().isEmpty()) continue;
            if (lower.contains("error")) {
                this.setError(true);
            } else if (lower.contains("warning")) {
                this.setError(false);
            } else if (this.emacsMode) {
                this.setError(true);
            }
            this.log(line);
        }
    }

    private void parseEmacsOutput(BufferedReader reader) throws IOException {
        this.parseStandardOutput(reader);
    }

    private void setError(boolean err) {
        this.error = err;
        if (this.error) {
            this.errorFlag = true;
        }
    }

    private void log(String line) {
        if (!this.emacsMode) {
            this.task.log("", this.error ? 0 : 1);
        }
        this.task.log(line, this.error ? 0 : 1);
    }

    protected boolean getErrorFlag() {
        return this.errorFlag;
    }
}

