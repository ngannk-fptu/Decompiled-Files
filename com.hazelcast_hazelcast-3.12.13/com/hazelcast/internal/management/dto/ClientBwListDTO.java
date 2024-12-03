/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.dto;

import com.hazelcast.internal.json.JsonArray;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.json.JsonValue;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.internal.management.dto.ClientBwListEntryDTO;
import com.hazelcast.util.JsonUtil;
import java.util.ArrayList;
import java.util.List;

public class ClientBwListDTO
implements JsonSerializable {
    public Mode mode;
    public List<ClientBwListEntryDTO> entries;

    public ClientBwListDTO() {
    }

    public ClientBwListDTO(Mode mode, List<ClientBwListEntryDTO> entries) {
        this.mode = mode;
        this.entries = entries;
    }

    @Override
    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.add("mode", this.mode.toString());
        if (this.entries != null) {
            JsonArray entriesArray = new JsonArray();
            for (ClientBwListEntryDTO entry : this.entries) {
                JsonObject json = entry.toJson();
                if (json == null) continue;
                entriesArray.add(json);
            }
            object.add("entries", entriesArray);
        }
        return object;
    }

    @Override
    public void fromJson(JsonObject json) {
        String modeStr = JsonUtil.getString(json, "mode");
        this.mode = Mode.valueOf(modeStr);
        this.entries = new ArrayList<ClientBwListEntryDTO>();
        JsonArray entriesArray = JsonUtil.getArray(json, "entries");
        for (JsonValue jsonValue : entriesArray) {
            ClientBwListEntryDTO entryDTO = new ClientBwListEntryDTO();
            entryDTO.fromJson(jsonValue.asObject());
            this.entries.add(entryDTO);
        }
    }

    public static enum Mode {
        DISABLED,
        WHITELIST,
        BLACKLIST;

    }
}

