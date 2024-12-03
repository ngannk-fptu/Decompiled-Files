/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.secrets.cli.db;

public class CliArgs {
    private final String password;
    private final String className;
    private final boolean isSilent;
    private final boolean isHelp;
    private final boolean isEncryptionMode;

    private CliArgs(String password, String className, boolean isSilent, boolean isHelp, boolean isEncryptionMode) {
        this.password = password;
        this.className = className;
        this.isSilent = isSilent;
        this.isHelp = isHelp;
        this.isEncryptionMode = isEncryptionMode;
    }

    public String getPassword() {
        return this.password;
    }

    public String getClassName() {
        return this.className;
    }

    public boolean isSilent() {
        return this.isSilent;
    }

    public boolean isHelp() {
        return this.isHelp;
    }

    public boolean isEncryptionMode() {
        return this.isEncryptionMode;
    }

    public static class CliArgsBuilder {
        private String password;
        private String className;
        private boolean isSilent;
        private boolean isHelp;
        private boolean isEncryptionMode;

        public CliArgsBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        CliArgsBuilder setClassName(String className) {
            this.className = className;
            return this;
        }

        CliArgsBuilder setIsSilent(boolean isSilent) {
            this.isSilent = isSilent;
            return this;
        }

        CliArgsBuilder setIsHelp(boolean isHelp) {
            this.isHelp = isHelp;
            return this;
        }

        CliArgsBuilder isEncryptionMode(boolean isEncryptionMode) {
            this.isEncryptionMode = isEncryptionMode;
            return this;
        }

        CliArgs build() {
            return new CliArgs(this.password, this.className, this.isSilent, this.isHelp, this.isEncryptionMode);
        }
    }
}

