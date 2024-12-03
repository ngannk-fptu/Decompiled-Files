/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build;

import aQute.bnd.build.Project;
import aQute.bnd.service.action.Action;
import aQute.lib.converter.Converter;
import java.lang.reflect.Method;

public class ReflectAction
implements Action {
    String what;

    public ReflectAction(String what) {
        this.what = what;
    }

    @Override
    public void execute(Project project, String action) throws Exception {
        Method m = project.getClass().getMethod(this.what, new Class[0]);
        m.invoke((Object)project, new Object[0]);
    }

    @Override
    public void execute(Project project, Object ... args) throws Exception {
        for (Method m : project.getClass().getMethods()) {
            Class<?>[] types = m.getParameterTypes();
            if (!m.getName().equals(this.what) || args.length != types.length) continue;
            if (args.length == 0) {
                m.invoke((Object)project, new Object[0]);
                continue;
            }
            try {
                Object[] args2 = new Object[args.length];
                for (int i = 0; i < args.length; ++i) {
                    args2[i] = Converter.cnv(m.getGenericParameterTypes()[i], args[i]);
                }
                m.invoke((Object)project, args2);
                return;
            }
            catch (Exception e) {
                // empty catch block
            }
        }
    }

    public String toString() {
        return "ReflectAction:[" + this.what + "]";
    }
}

