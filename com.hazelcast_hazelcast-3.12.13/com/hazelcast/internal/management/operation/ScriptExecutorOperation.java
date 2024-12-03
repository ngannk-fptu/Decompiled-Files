/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.operation;

import com.hazelcast.config.ManagementCenterConfig;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.management.ScriptEngineManagerContext;
import com.hazelcast.internal.management.operation.AbstractManagementOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.impl.Versioned;
import java.io.IOException;
import java.security.AccessControlException;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptExecutorOperation
extends AbstractManagementOperation
implements Versioned {
    private String engineName;
    private String script;
    private Object result;

    public ScriptExecutorOperation() {
    }

    public ScriptExecutorOperation(String engineName, String script) {
        this.engineName = engineName;
        this.script = script;
    }

    @Override
    public void run() {
        ManagementCenterConfig managementCenterConfig = this.getNodeEngine().getConfig().getManagementCenterConfig();
        if (!managementCenterConfig.isScriptingEnabled()) {
            throw new AccessControlException("Using ScriptEngine is not allowed on this Hazelcast member.");
        }
        ScriptEngineManager scriptEngineManager = ScriptEngineManagerContext.getScriptEngineManager();
        ScriptEngine engine = scriptEngineManager.getEngineByName(this.engineName);
        if (engine == null) {
            throw new IllegalArgumentException("Could not find ScriptEngine named '" + this.engineName + "'.");
        }
        engine.put("hazelcast", this.getNodeEngine().getHazelcastInstance());
        try {
            this.result = engine.eval(this.script);
        }
        catch (ScriptException e) {
            HazelcastException hazelcastException = new HazelcastException(e.getMessage());
            hazelcastException.setStackTrace(e.getStackTrace());
            throw hazelcastException;
        }
    }

    @Override
    public Object getResponse() {
        return this.result;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.engineName);
        out.writeUTF(this.script);
        if (out.getVersion().isUnknownOrLessThan(Versions.V3_10)) {
            out.writeInt(0);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.engineName = in.readUTF();
        this.script = in.readUTF();
        if (in.getVersion().isUnknownOrLessThan(Versions.V3_10)) {
            in.readInt();
        }
    }

    @Override
    public int getId() {
        return 0;
    }
}

