/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management;

import com.hazelcast.client.impl.ClientEngine;
import com.hazelcast.client.impl.ClientSelector;
import com.hazelcast.client.impl.ClientSelectors;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.dto.ClientBwListDTO;
import com.hazelcast.internal.management.dto.ClientBwListEntryDTO;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.util.JsonUtil;
import java.util.List;
import java.util.regex.Pattern;

public class ClientBwListConfigHandler {
    private static final ILogger LOGGER = Logger.getLogger(ClientBwListConfigHandler.class);
    private final ClientEngine clientEngine;

    public ClientBwListConfigHandler(ClientEngine clientEngine) {
        this.clientEngine = clientEngine;
    }

    public void handleLostConnection() {
        try {
            this.clientEngine.applySelector(ClientSelectors.any());
        }
        catch (Exception e) {
            LOGGER.warning("Could not clean up client B/W list filtering.", e);
        }
    }

    public void handleConfig(JsonObject configJson) {
        try {
            JsonObject bwListConfigJson = JsonUtil.getObject(configJson, "clientBwList");
            ClientBwListDTO configDTO = new ClientBwListDTO();
            configDTO.fromJson(bwListConfigJson);
            this.applyConfig(configDTO);
        }
        catch (Exception e) {
            LOGGER.warning("Could not apply client B/W list filtering.", e);
        }
    }

    private void applyConfig(ClientBwListDTO configDTO) {
        ClientSelector selector = null;
        switch (configDTO.mode) {
            case DISABLED: {
                selector = ClientSelectors.any();
                break;
            }
            case WHITELIST: {
                selector = ClientBwListConfigHandler.createSelector(configDTO.entries);
                break;
            }
            case BLACKLIST: {
                selector = ClientSelectors.inverse(ClientBwListConfigHandler.createSelector(configDTO.entries));
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown client B/W list mode: " + (Object)((Object)configDTO.mode));
            }
        }
        this.clientEngine.applySelector(selector);
    }

    private static ClientSelector createSelector(List<ClientBwListEntryDTO> entries) {
        ClientSelector selector = ClientSelectors.none();
        for (ClientBwListEntryDTO entryDTO : entries) {
            ClientSelector entrySelector = ClientBwListConfigHandler.createSelector(entryDTO);
            selector = ClientSelectors.or(selector, entrySelector);
        }
        return selector;
    }

    private static ClientSelector createSelector(ClientBwListEntryDTO entry) {
        switch (entry.type) {
            case IP_ADDRESS: {
                return ClientSelectors.ipSelector(entry.value);
            }
            case INSTANCE_NAME: {
                return ClientSelectors.nameSelector(ClientBwListConfigHandler.sanitizeValueWithWildcards(entry.value));
            }
            case LABEL: {
                return ClientSelectors.labelSelector(ClientBwListConfigHandler.sanitizeValueWithWildcards(entry.value));
            }
        }
        throw new IllegalArgumentException("Unknown client B/W list entry type: " + (Object)((Object)entry.type));
    }

    private static String sanitizeValueWithWildcards(String value) {
        String quoted = Pattern.quote(value);
        return quoted.replaceAll("\\*", "\\\\E.*\\\\Q");
    }
}

