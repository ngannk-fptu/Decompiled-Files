/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.factory;

import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import java.util.Map;

public interface ResultFactory {
    public Result buildResult(ResultConfig var1, Map<String, Object> var2) throws Exception;
}

