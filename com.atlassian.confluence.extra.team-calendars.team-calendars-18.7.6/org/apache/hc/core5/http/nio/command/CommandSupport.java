/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.nio.command;

import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.http.RequestNotExecutedException;
import org.apache.hc.core5.http.nio.command.ExecutableCommand;
import org.apache.hc.core5.reactor.Command;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.util.Args;

@Internal
public final class CommandSupport {
    public static void failCommands(IOSession ioSession, Exception ex) {
        Command command;
        Args.notNull(ioSession, "I/O session");
        while ((command = ioSession.poll()) != null) {
            if (command instanceof ExecutableCommand) {
                ((ExecutableCommand)command).failed(ex);
                continue;
            }
            command.cancel();
        }
    }

    public static void cancelCommands(IOSession ioSession) {
        Command command;
        Args.notNull(ioSession, "I/O session");
        while ((command = ioSession.poll()) != null) {
            if (command instanceof ExecutableCommand) {
                if (!ioSession.isOpen()) {
                    ((ExecutableCommand)command).failed(new RequestNotExecutedException());
                    continue;
                }
                command.cancel();
                continue;
            }
            command.cancel();
        }
    }
}

