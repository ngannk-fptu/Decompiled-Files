/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.request;

import com.hazelcast.internal.json.Json;
import com.hazelcast.internal.json.JsonArray;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.management.ManagementCenterService;
import com.hazelcast.internal.management.operation.ScriptExecutorOperation;
import com.hazelcast.internal.management.request.ConsoleRequest;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.util.AddressUtil;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.JsonUtil;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.SetUtil;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ExecuteScriptRequest
implements ConsoleRequest {
    private String script;
    private String engine;
    private Set<String> targets;

    public ExecuteScriptRequest() {
    }

    public ExecuteScriptRequest(String script, String engine, Set<String> targets) {
        this.script = script;
        this.engine = engine;
        this.targets = targets;
    }

    @Override
    public int getType() {
        return 3;
    }

    @Override
    public void writeResponse(ManagementCenterService mcs, JsonObject root) throws Exception {
        Map<Address, InternalCompletableFuture<Object>> futures = MapUtil.createHashMap(this.targets.size());
        for (String address : this.targets) {
            AddressUtil.AddressHolder addressHolder = AddressUtil.getAddressHolder(address);
            Address targetAddress = new Address(addressHolder.getAddress(), addressHolder.getPort());
            futures.put(targetAddress, mcs.callOnAddress(targetAddress, new ScriptExecutorOperation(this.engine, this.script)));
        }
        JsonObject responseJson = new JsonObject();
        for (Map.Entry entry : futures.entrySet()) {
            Address address = (Address)entry.getKey();
            Future future = (Future)entry.getValue();
            try {
                ExecuteScriptRequest.addSuccessResponse(responseJson, address, this.prettyPrint(future.get()));
            }
            catch (ExecutionException e) {
                ExecuteScriptRequest.addErrorResponse(responseJson, address, e.getCause());
            }
            catch (InterruptedException e) {
                ExecuteScriptRequest.addErrorResponse(responseJson, address, e);
                Thread.currentThread().interrupt();
            }
        }
        root.add("result", responseJson);
    }

    private String prettyPrint(Object result) {
        StringBuilder sb = new StringBuilder();
        if (result instanceof String) {
            sb.append(result);
        } else if (result instanceof List) {
            List list = (List)result;
            for (Object o : list) {
                sb.append(o).append("\n");
            }
        } else if (result instanceof Map) {
            Map map = (Map)result;
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry o;
                Map.Entry e = o = iterator.next();
                sb.append(e.getKey()).append("->").append(e.getValue()).append("\n");
            }
        } else if (result == null) {
            sb.append("error");
        }
        sb.append("\n");
        return sb.toString();
    }

    @Override
    public void fromJson(JsonObject json) {
        this.script = JsonUtil.getString(json, "script", "");
        this.engine = JsonUtil.getString(json, "engine", "");
        JsonArray array = JsonUtil.getArray(json, "targets", new JsonArray());
        this.targets = SetUtil.createHashSet(array.size());
        for (JsonValue target : array) {
            this.targets.add(target.asString());
        }
    }

    private static void addSuccessResponse(JsonObject root, Address address, String result) {
        ExecuteScriptRequest.addResponse(root, address, true, result, null);
    }

    private static void addErrorResponse(JsonObject root, Address address, Throwable e) {
        ExecuteScriptRequest.addResponse(root, address, false, e.getMessage(), ExceptionUtil.toString(e));
    }

    private static void addResponse(JsonObject root, Address address, boolean success, String result, String stackTrace) {
        JsonObject json = new JsonObject();
        json.add("success", success);
        json.add("result", result);
        json.add("stackTrace", stackTrace != null ? Json.value(stackTrace) : Json.NULL);
        root.add(address.toString(), json);
    }
}

