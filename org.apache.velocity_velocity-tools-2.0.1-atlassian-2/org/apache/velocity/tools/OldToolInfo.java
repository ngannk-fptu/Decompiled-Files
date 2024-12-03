/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools;

import java.lang.reflect.Method;
import java.util.Map;
import org.apache.velocity.tools.ToolInfo;

public class OldToolInfo
extends ToolInfo {
    private static final long serialVersionUID = -4062162635847288761L;
    public static final String INIT_METHOD_NAME = "init";
    private transient Method init;

    public OldToolInfo(String key, Class clazz) {
        super(key, clazz);
    }

    protected Method getInit() {
        if (this.init == null) {
            try {
                this.init = this.getToolClass().getMethod(INIT_METHOD_NAME, Object.class);
            }
            catch (NoSuchMethodException noSuchMethodException) {
                // empty catch block
            }
        }
        return this.init;
    }

    @Override
    public void setClass(Class clazz) {
        super.setClass(clazz);
        this.init = null;
    }

    @Override
    protected void configure(Object tool, Map<String, Object> configuration) {
        Object ctx;
        super.configure(tool, configuration);
        Method init = this.getInit();
        if (init != null && (ctx = configuration.get("velocityContext")) != null) {
            this.invoke(init, tool, ctx);
        }
    }
}

