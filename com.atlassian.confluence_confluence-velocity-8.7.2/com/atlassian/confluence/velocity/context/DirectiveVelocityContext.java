/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.VelocityContext
 *  org.apache.velocity.context.Context
 */
package com.atlassian.confluence.velocity.context;

import com.atlassian.confluence.velocity.context.DefaultValueStackProvider;
import java.util.Map;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

public class DirectiveVelocityContext
extends VelocityContext
implements DefaultValueStackProvider {
    public DirectiveVelocityContext(Map contextMap) {
        super(contextMap);
    }

    public DirectiveVelocityContext(Context context) {
        super(context);
    }
}

