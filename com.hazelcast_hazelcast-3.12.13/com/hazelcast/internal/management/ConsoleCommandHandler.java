/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management;

import com.hazelcast.console.ConsoleApp;
import com.hazelcast.core.HazelcastInstance;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConsoleCommandHandler {
    private final ConsoleHandlerApp app;
    private final Lock lock = new ReentrantLock();
    private final StringBuilder buffer = new StringBuilder();

    public ConsoleCommandHandler(HazelcastInstance instance) {
        this.app = new ConsoleHandlerApp(instance);
    }

    public String handleCommand(String command) throws InterruptedException {
        if (this.lock.tryLock(1L, TimeUnit.SECONDS)) {
            try {
                String string = this.doHandleCommand(command);
                return string;
            }
            finally {
                this.lock.unlock();
            }
        }
        return "'" + command + "' execution is timed out!";
    }

    String doHandleCommand(String command) {
        this.app.handleCommand(command);
        String output = this.buffer.toString();
        this.buffer.setLength(0);
        return output;
    }

    private class ConsoleHandlerApp
    extends ConsoleApp {
        public ConsoleHandlerApp(HazelcastInstance hazelcast) {
            super(hazelcast);
        }

        @Override
        protected void handleAddListener(String[] args) {
            this.println("Listener commands are not allowed!");
        }

        @Override
        protected void handleRemoveListener(String[] args) {
            this.println("Listener commands are not allowed!");
        }

        @Override
        public void println(Object obj) {
            this.print(obj);
            this.print(Character.valueOf('\n'));
        }

        @Override
        public void print(Object obj) {
            ConsoleCommandHandler.this.buffer.append(String.valueOf(obj));
        }

        @Override
        protected void handleExit() {
            this.print("'exit' is not allowed!");
        }

        @Override
        protected void handleShutdown() {
            this.print("'shutdown' is not allowed!");
        }
    }
}

