/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util;

import com.mchange.util.RobustMessageLogger;
import java.util.Iterator;

public interface FailSuppressedMessageLogger
extends RobustMessageLogger {
    public Iterator getFailures();

    public void clearFailures();
}

