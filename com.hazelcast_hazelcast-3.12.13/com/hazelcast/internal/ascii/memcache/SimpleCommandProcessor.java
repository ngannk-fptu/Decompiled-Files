/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.internal.ascii.TextCommandService;
import com.hazelcast.internal.ascii.memcache.MemcacheCommandProcessor;
import com.hazelcast.internal.ascii.memcache.SimpleCommand;
import com.hazelcast.logging.ILogger;

public class SimpleCommandProcessor
extends MemcacheCommandProcessor<SimpleCommand> {
    private final ILogger logger;

    public SimpleCommandProcessor(TextCommandService textCommandService) {
        super(textCommandService);
        this.logger = textCommandService.getNode().getLogger(this.getClass());
    }

    @Override
    public void handle(SimpleCommand command) {
        if (command.getType() == TextCommandConstants.TextCommandType.QUIT) {
            try {
                command.getDecoder().closeConnection();
            }
            catch (Exception e) {
                this.logger.warning(e);
            }
        } else if (command.getType() == TextCommandConstants.TextCommandType.UNKNOWN) {
            command.setResponse(TextCommandConstants.ERROR);
            this.textCommandService.sendResponse(command);
        }
    }

    @Override
    public void handleRejection(SimpleCommand command) {
        this.handle(command);
    }
}

