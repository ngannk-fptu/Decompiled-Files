/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.VelocityContext
 *  org.apache.velocity.app.Velocity
 *  org.apache.velocity.app.VelocityEngine
 *  org.apache.velocity.context.Context
 */
package org.apache.velocity.tools.generic;

import java.io.StringWriter;
import java.io.Writer;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.generic.SafeConfig;
import org.apache.velocity.tools.generic.ValueParser;

@DefaultKey(value="render")
public class RenderTool
extends SafeConfig {
    public static final int DEFAULT_PARSE_DEPTH = 20;
    @Deprecated
    public static final String KEY_PARSE_DEPTH = "parse.depth";
    @Deprecated
    public static final String KEY_CATCH_EXCEPTIONS = "catch.exceptions";
    public static final String KEY_FORCE_THREAD_SAFE = "forceThreadSafe";
    private static final String LOG_TAG = "RenderTool.eval()";
    private VelocityEngine engine = null;
    private Context context;
    private int parseDepth = 20;
    private boolean catchExceptions = true;
    private boolean forceThreadSafe = true;

    @Override
    protected void configure(ValueParser parser) {
        Boolean catchEm;
        Integer depth = parser.getInteger(KEY_PARSE_DEPTH);
        if (depth != null) {
            this.setParseDepth(depth);
        }
        if ((catchEm = parser.getBoolean(KEY_CATCH_EXCEPTIONS)) != null) {
            this.setCatchExceptions(catchEm);
        }
        this.forceThreadSafe = parser.getBoolean(KEY_FORCE_THREAD_SAFE, this.forceThreadSafe);
        if ("request".equals(parser.getString("scope"))) {
            this.forceThreadSafe = false;
        }
    }

    public void setVelocityEngine(VelocityEngine ve) {
        this.engine = ve;
    }

    public void setParseDepth(int depth) {
        if (!this.isConfigLocked()) {
            this.parseDepth = depth;
        } else if (this.parseDepth != depth) {
            this.debug("RenderTool: Attempt was made to alter parse depth while config was locked.");
        }
    }

    public void setVelocityContext(Context context) {
        if (!this.isConfigLocked()) {
            if (context == null) {
                throw new NullPointerException("context must not be null");
            }
            this.context = context;
        } else if (this.context != context) {
            this.debug("RenderTool: Attempt was made to set a new context while config was locked.");
        }
    }

    public int getParseDepth() {
        return this.parseDepth;
    }

    public void setCatchExceptions(boolean catchExceptions) {
        if (!this.isConfigLocked()) {
            this.catchExceptions = catchExceptions;
        } else if (this.catchExceptions != catchExceptions) {
            this.debug("RenderTool: Attempt was made to alter catchE while config was locked.");
        }
    }

    public boolean getCatchExceptions() {
        return this.catchExceptions;
    }

    public String eval(String vtl) throws Exception {
        VelocityContext ctx = this.forceThreadSafe ? new VelocityContext(this.context) : this.context;
        return this.eval((Context)ctx, vtl);
    }

    public String recurse(String vtl) throws Exception {
        VelocityContext ctx = this.forceThreadSafe ? new VelocityContext(this.context) : this.context;
        return this.recurse((Context)ctx, vtl);
    }

    public String eval(Context ctx, String vtl) throws Exception {
        if (this.catchExceptions) {
            try {
                return this.internalEval(ctx, vtl);
            }
            catch (Exception e) {
                this.debug("RenderTool.eval() failed due to " + e, e);
                return null;
            }
        }
        return this.internalEval(ctx, vtl);
    }

    protected String internalEval(Context ctx, String vtl) throws Exception {
        if (vtl == null) {
            return null;
        }
        StringWriter sw = new StringWriter();
        boolean success = this.engine == null ? Velocity.evaluate((Context)ctx, (Writer)sw, (String)LOG_TAG, (String)vtl) : this.engine.evaluate(ctx, (Writer)sw, LOG_TAG, vtl);
        if (success) {
            return sw.toString();
        }
        return null;
    }

    public String recurse(Context ctx, String vtl) throws Exception {
        return this.internalRecurse(ctx, vtl, 0);
    }

    protected String internalRecurse(Context ctx, String vtl, int count) throws Exception {
        String result = this.eval(ctx, vtl);
        if (result == null || result.equals(vtl)) {
            return result;
        }
        if (count < this.parseDepth) {
            return this.internalRecurse(ctx, result, count + 1);
        }
        this.debug("RenderTool.recurse() exceeded the maximum parse depth of " + this.parseDepth + "on the following template: " + vtl);
        return result;
    }

    private void debug(String message) {
        if (this.engine == null) {
            Velocity.getLog().debug((Object)message);
        } else {
            this.engine.getLog().debug((Object)message);
        }
    }

    private void debug(String message, Throwable t) {
        if (this.engine == null) {
            Velocity.getLog().debug((Object)message, t);
        } else {
            this.engine.getLog().debug((Object)message, t);
        }
    }
}

