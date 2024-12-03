/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core.server;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.nio.IOUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public final class StartServer {
    private StartServer() {
    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        HazelcastInstance hz = Hazelcast.newHazelcastInstance();
        StartServer.printMemberPort(hz);
    }

    private static void printMemberPort(HazelcastInstance hz) throws FileNotFoundException, UnsupportedEncodingException {
        String printPort = System.getProperty("print.port");
        if (printPort != null) {
            PrintWriter printWriter = null;
            try {
                printWriter = new PrintWriter("ports" + File.separator + printPort, "UTF-8");
                printWriter.println(hz.getCluster().getLocalMember().getAddress().getPort());
            }
            catch (Throwable throwable) {
                IOUtil.closeResource(printWriter);
                throw throwable;
            }
            IOUtil.closeResource(printWriter);
        }
    }
}

