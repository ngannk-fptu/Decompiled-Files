/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.net;

import com.mchange.util.RobustMessageLogger;
import java.io.IOException;
import java.net.Socket;

public final class SocketUtils {
    public static void attemptClose(Socket socket) {
        SocketUtils.attemptClose(socket, null);
    }

    public static void attemptClose(Socket socket, RobustMessageLogger robustMessageLogger) {
        block4: {
            try {
                socket.close();
            }
            catch (IOException iOException) {
                if (robustMessageLogger != null) {
                    robustMessageLogger.log(iOException, "IOException trying to close Socket");
                }
            }
            catch (NullPointerException nullPointerException) {
                if (robustMessageLogger == null) break block4;
                robustMessageLogger.log(nullPointerException, "NullPointerException trying to close Socket");
            }
        }
    }

    private SocketUtils() {
    }
}

