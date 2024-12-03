/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.app.VelocityEngine
 */
package com.atlassian.confluence.servlet.simpledisplay;

import org.apache.velocity.app.VelocityEngine;

public interface VelocityEngineResolver {
    public VelocityEngine getVelocityEngine() throws Exception;
}

