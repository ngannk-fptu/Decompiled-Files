/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.utils;

public class FileProcessResult {
    String stderr = "";
    String stdout = "";
    int exitValue = -1;
    long processTimeMillis = -1L;
    boolean isTimeout = false;
    long stdoutLength = -1L;
    long stderrLength = -1L;
    boolean stderrTruncated = false;
    boolean stdoutTruncated = false;

    public String getStderr() {
        return this.stderr;
    }

    public String getStdout() {
        return this.stdout;
    }

    public int getExitValue() {
        return this.exitValue;
    }

    public long getProcessTimeMillis() {
        return this.processTimeMillis;
    }

    public boolean isTimeout() {
        return this.isTimeout;
    }

    public long getStdoutLength() {
        return this.stdoutLength;
    }

    public long getStderrLength() {
        return this.stderrLength;
    }

    public boolean isStderrTruncated() {
        return this.stderrTruncated;
    }

    public boolean isStdoutTruncated() {
        return this.stdoutTruncated;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public void setExitValue(int exitValue) {
        this.exitValue = exitValue;
    }

    public void setProcessTimeMillis(long processTimeMillis) {
        this.processTimeMillis = processTimeMillis;
    }

    public void setTimeout(boolean timeout) {
        this.isTimeout = timeout;
    }

    public void setStdoutLength(long stdoutLength) {
        this.stdoutLength = stdoutLength;
    }

    public void setStderrLength(long stderrLength) {
        this.stderrLength = stderrLength;
    }

    public void setStderrTruncated(boolean stderrTruncated) {
        this.stderrTruncated = stderrTruncated;
    }

    public void setStdoutTruncated(boolean stdoutTruncated) {
        this.stdoutTruncated = stdoutTruncated;
    }

    public String toString() {
        return "FileProcessResult{stderr='" + this.stderr + '\'' + ", stdout='" + this.stdout + '\'' + ", exitValue=" + this.exitValue + ", processTimeMillis=" + this.processTimeMillis + ", isTimeout=" + this.isTimeout + ", stdoutLength=" + this.stdoutLength + ", stderrLength=" + this.stderrLength + ", stderrTruncated=" + this.stderrTruncated + ", stdoutTruncated=" + this.stdoutTruncated + '}';
    }
}

