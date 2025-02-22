/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.appender.rolling.action;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.core.appender.rolling.action.AbstractAction;
import org.apache.logging.log4j.core.appender.rolling.action.Action;

public class CompositeAction
extends AbstractAction {
    private final Action[] actions;
    private final boolean stopOnError;

    public CompositeAction(List<Action> actions, boolean stopOnError) {
        this.actions = new Action[actions.size()];
        actions.toArray(this.actions);
        this.stopOnError = stopOnError;
    }

    @Override
    public void run() {
        try {
            this.execute();
        }
        catch (IOException ex) {
            LOGGER.warn("Exception during file rollover.", (Throwable)ex);
        }
    }

    @Override
    public boolean execute() throws IOException {
        if (this.stopOnError) {
            for (Action action : this.actions) {
                if (action.execute()) continue;
                return false;
            }
            return true;
        }
        boolean status = true;
        IOException exception = null;
        for (Action action : this.actions) {
            try {
                status &= action.execute();
            }
            catch (IOException ex) {
                status = false;
                if (exception != null) continue;
                exception = ex;
            }
        }
        if (exception != null) {
            throw exception;
        }
        return status;
    }

    public String toString() {
        return CompositeAction.class.getSimpleName() + Arrays.toString(this.actions);
    }

    public Action[] getActions() {
        return this.actions;
    }

    public boolean isStopOnError() {
        return this.stopOnError;
    }
}

