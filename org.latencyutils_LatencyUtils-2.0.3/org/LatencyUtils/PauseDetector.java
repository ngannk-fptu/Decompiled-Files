/*
 * Decompiled with CFR 0.152.
 */
package org.LatencyUtils;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import org.LatencyUtils.PauseDetectorListener;

public abstract class PauseDetector {
    private final ArrayList<PauseDetectorListener> highPriorityListeners = new ArrayList(32);
    private final ArrayList<PauseDetectorListener> normalPriorityListeners = new ArrayList(32);
    private final LinkedBlockingQueue<Object> messages = new LinkedBlockingQueue();
    private final PauseDetectorThread pauseDetectorThread = new PauseDetectorThread();
    private volatile boolean stop;

    protected PauseDetector() {
        this.pauseDetectorThread.setDaemon(true);
        this.stop = false;
        this.pauseDetectorThread.start();
    }

    protected synchronized void notifyListeners(long pauseLengthNsec, long pauseEndTimeNsec) {
        this.messages.add(new PauseNotification(pauseLengthNsec, pauseEndTimeNsec));
    }

    public synchronized void addListener(PauseDetectorListener listener) {
        this.addListener(listener, false);
    }

    public synchronized void addListener(PauseDetectorListener listener, boolean isHighPriority) {
        this.messages.add(new ChangeListenersRequest(isHighPriority ? ChangeListenersRequest.ChangeCommand.ADD_HIGH_PRIORITY : ChangeListenersRequest.ChangeCommand.ADD_NORMAL_PRIORITY, listener));
    }

    public synchronized void removeListener(PauseDetectorListener listener) {
        this.messages.add(new ChangeListenersRequest(ChangeListenersRequest.ChangeCommand.REMOVE, listener));
    }

    public void shutdown() {
        this.stop = true;
        this.pauseDetectorThread.interrupt();
    }

    static class PauseNotification {
        final long pauseLengthNsec;
        final long pauseEndTimeNsec;

        PauseNotification(long pauseLengthNsec, long pauseEndTimeNsec) {
            this.pauseLengthNsec = pauseLengthNsec;
            this.pauseEndTimeNsec = pauseEndTimeNsec;
        }
    }

    static class ChangeListenersRequest {
        final ChangeCommand command;
        final PauseDetectorListener listener;

        ChangeListenersRequest(ChangeCommand changeCommand, PauseDetectorListener listener) {
            this.command = changeCommand;
            this.listener = listener;
        }

        static enum ChangeCommand {
            ADD_HIGH_PRIORITY,
            ADD_NORMAL_PRIORITY,
            REMOVE;

        }
    }

    private class PauseDetectorThread
    extends Thread {
        private PauseDetectorThread() {
        }

        @Override
        public void run() {
            while (!PauseDetector.this.stop) {
                try {
                    Object message = PauseDetector.this.messages.take();
                    if (message instanceof ChangeListenersRequest) {
                        ChangeListenersRequest changeRequest = (ChangeListenersRequest)message;
                        if (changeRequest.command == ChangeListenersRequest.ChangeCommand.ADD_HIGH_PRIORITY) {
                            PauseDetector.this.highPriorityListeners.add(changeRequest.listener);
                            continue;
                        }
                        if (changeRequest.command == ChangeListenersRequest.ChangeCommand.ADD_NORMAL_PRIORITY) {
                            PauseDetector.this.normalPriorityListeners.add(changeRequest.listener);
                            continue;
                        }
                        PauseDetector.this.normalPriorityListeners.remove(changeRequest.listener);
                        PauseDetector.this.highPriorityListeners.remove(changeRequest.listener);
                        continue;
                    }
                    if (message instanceof PauseNotification) {
                        PauseNotification pauseNotification = (PauseNotification)message;
                        for (PauseDetectorListener listener : PauseDetector.this.highPriorityListeners) {
                            listener.handlePauseEvent(pauseNotification.pauseLengthNsec, pauseNotification.pauseEndTimeNsec);
                        }
                        for (PauseDetectorListener listener : PauseDetector.this.normalPriorityListeners) {
                            listener.handlePauseEvent(pauseNotification.pauseLengthNsec, pauseNotification.pauseEndTimeNsec);
                        }
                        continue;
                    }
                    throw new RuntimeException("Unexpected message type received: " + message);
                }
                catch (InterruptedException interruptedException) {
                }
            }
        }
    }
}

