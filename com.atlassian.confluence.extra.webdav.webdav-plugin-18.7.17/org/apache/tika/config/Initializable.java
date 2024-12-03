/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.config;

import java.util.Map;
import org.apache.tika.config.InitializableProblemHandler;
import org.apache.tika.config.Param;
import org.apache.tika.exception.TikaConfigException;

public interface Initializable {
    public void initialize(Map<String, Param> var1) throws TikaConfigException;

    public void checkInitialization(InitializableProblemHandler var1) throws TikaConfigException;
}

