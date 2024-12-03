/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build;

import aQute.bnd.build.Project;
import aQute.bnd.service.action.Action;

public class ScriptAction
implements Action {
    final String script;
    final String type;

    public ScriptAction(String type, String script) {
        this.script = script;
        this.type = type;
    }

    @Override
    public void execute(Project project, String action) throws Exception {
        project.script(this.type, this.script);
    }

    @Override
    public void execute(Project project, Object ... args) throws Exception {
        project.script(this.type, this.script, args);
    }
}

