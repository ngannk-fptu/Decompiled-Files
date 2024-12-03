/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.upgrade;

public class UpgradeTaskException
extends RuntimeException {
    public UpgradeTaskException() {
    }

    public UpgradeTaskException(String message) {
        super(message);
    }

    public UpgradeTaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpgradeTaskException(Throwable cause) {
        super(cause);
    }
}

