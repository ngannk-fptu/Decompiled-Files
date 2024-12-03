/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.deployment.wsdd.WSDDDeployment;

public interface WSDDEngineConfiguration
extends EngineConfiguration {
    public WSDDDeployment getDeployment();
}

