/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.app.VelocityEngine
 */
package com.atlassian.confluence.servlet.simpledisplay;

import com.atlassian.confluence.servlet.simpledisplay.VelocityEngineResolver;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import org.apache.velocity.app.VelocityEngine;

@Deprecated(forRemoval=true)
public class DefaultVelocityEngineResolver
implements VelocityEngineResolver {
    @Override
    public VelocityEngine getVelocityEngine() throws Exception {
        return VelocityUtils.getVelocityEngine();
    }
}

