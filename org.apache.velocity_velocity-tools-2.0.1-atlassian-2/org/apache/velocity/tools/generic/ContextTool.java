/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.context.AbstractContext
 *  org.apache.velocity.context.Context
 */
package org.apache.velocity.tools.generic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.velocity.context.AbstractContext;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.InvalidScope;
import org.apache.velocity.tools.generic.SafeConfig;
import org.apache.velocity.tools.generic.ValueParser;

@DefaultKey(value="context")
@InvalidScope(value={"application", "session"})
public class ContextTool
extends SafeConfig {
    protected Context context;
    protected Map<String, Object> toolbox;

    @Override
    protected void configure(ValueParser parser) {
        this.context = (Context)parser.getValue("velocityContext");
    }

    public Context getThis() {
        return this.context;
    }

    public Map<String, Object> getToolbox() {
        if (this.toolbox == null && this.context instanceof ToolContext) {
            this.toolbox = ((ToolContext)this.context).getToolbox();
        }
        return this.toolbox;
    }

    public Set getKeys() {
        HashSet keys = new HashSet();
        this.fillKeyset(keys);
        if (this.isSafeMode()) {
            Iterator i = keys.iterator();
            while (i.hasNext()) {
                String key = String.valueOf(i.next());
                if (key.indexOf(46) < 0) continue;
                i.remove();
            }
        }
        return keys;
    }

    protected void fillKeyset(Set keys) {
        Context velctx = this.context;
        while (velctx != null) {
            Object[] ctxKeys = velctx.getKeys();
            keys.addAll(Arrays.asList(ctxKeys));
            if (velctx instanceof AbstractContext) {
                velctx = ((AbstractContext)velctx).getChainedContext();
                continue;
            }
            velctx = null;
        }
    }

    public Set getValues() {
        Set keys = this.getKeys();
        HashSet<Object> values = new HashSet<Object>(keys.size());
        Iterator i = keys.iterator();
        while (i.hasNext()) {
            String key = String.valueOf(i.next());
            values.add(this.context.get(key));
        }
        return values;
    }

    public boolean contains(Object refName) {
        return this.get(refName) != null;
    }

    public Object get(Object refName) {
        String key = String.valueOf(refName);
        if (this.isSafeMode() && key.indexOf(46) >= 0) {
            return null;
        }
        return this.context.get(key);
    }
}

