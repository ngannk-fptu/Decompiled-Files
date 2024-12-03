/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao;

import java.lang.reflect.Method;
import net.java.ao.ActiveObjectsException;

public class ActiveObjectsConfigurationException
extends ActiveObjectsException {
    public ActiveObjectsConfigurationException() {
    }

    public ActiveObjectsConfigurationException(String message) {
        super(message);
    }

    public ActiveObjectsConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActiveObjectsConfigurationException(Throwable cause) {
        super(cause);
    }

    public ActiveObjectsConfigurationException forMethod(Method method) {
        return new ActiveObjectsConfigurationException(this.getMessage() + " (" + method.getDeclaringClass().getName() + "." + method.getName() + ")");
    }
}

