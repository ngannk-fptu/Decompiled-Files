/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade;

import com.atlassian.confluence.upgrade.UpgradeError;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class UpgradeException
extends Exception {
    private final Collection<UpgradeError> upgradeErrors = new ArrayList<UpgradeError>();
    private final URL kbURL;
    private final boolean fatal;

    public UpgradeException(String message, Collection<UpgradeError> upgradeErrors) {
        super(message);
        this.upgradeErrors.addAll(upgradeErrors);
        this.kbURL = null;
        this.fatal = false;
    }

    @Deprecated
    public UpgradeException(String message) {
        super(message);
        this.kbURL = null;
        this.fatal = false;
    }

    @Deprecated
    public UpgradeException(String message, Throwable cause) {
        super(message, cause);
        this.kbURL = null;
        this.fatal = false;
    }

    public UpgradeException(Throwable cause) {
        super(cause);
        this.kbURL = null;
        this.fatal = false;
    }

    public UpgradeException(String message, URL kbURL) {
        super(message);
        this.kbURL = kbURL;
        this.fatal = false;
    }

    public UpgradeException(String message, URL kbURL, boolean fatal) {
        this(message, kbURL, null, fatal);
    }

    public UpgradeException(String message, URL kbURL, Throwable cause) {
        this(message, kbURL, cause, false);
    }

    public UpgradeException(String message, URL kbURL, Throwable cause, boolean fatal) {
        super(message, cause);
        this.kbURL = kbURL;
        this.fatal = fatal;
    }

    public Collection<UpgradeError> getUpgradeErrors() {
        return Collections.unmodifiableCollection(this.upgradeErrors);
    }

    public String getUpgradeErrorUiMessage() {
        return String.join((CharSequence)"<br>", this.upgradeErrors.stream().map(UpgradeError::getMessage).collect(Collectors.toList()));
    }

    public URL getKbURL() {
        return this.kbURL;
    }

    public boolean isFatal() {
        return this.fatal;
    }
}

