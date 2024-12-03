/*
 * Decompiled with CFR 0.152.
 */
package oshi.driver.mac;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.Immutable;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.os.OSProcess;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public final class ThreadInfo {
    private static final Pattern PS_M = Pattern.compile("\\D+(\\d+).+(\\d+\\.\\d)\\s+(\\w)\\s+(\\d+)\\D+(\\d+:\\d{2}\\.\\d{2})\\s+(\\d+:\\d{2}\\.\\d{2}).+");

    private ThreadInfo() {
    }

    public static List<ThreadStats> queryTaskThreads(int pid) {
        String pidStr = " " + pid + " ";
        ArrayList<ThreadStats> taskThreads = new ArrayList<ThreadStats>();
        List psThread = ExecutingCommand.runNative("ps -awwxM").stream().filter(s -> s.contains(pidStr)).collect(Collectors.toList());
        int tid = 0;
        for (String thread : psThread) {
            Matcher m = PS_M.matcher(thread);
            if (!m.matches() || pid != ParseUtil.parseIntOrDefault(m.group(1), -1)) continue;
            double cpu = ParseUtil.parseDoubleOrDefault(m.group(2), 0.0);
            char state = m.group(3).charAt(0);
            int pri = ParseUtil.parseIntOrDefault(m.group(4), 0);
            long sTime = ParseUtil.parseDHMSOrDefault(m.group(5), 0L);
            long uTime = ParseUtil.parseDHMSOrDefault(m.group(6), 0L);
            taskThreads.add(new ThreadStats(tid++, cpu, state, sTime, uTime, pri));
        }
        return taskThreads;
    }

    @Immutable
    public static class ThreadStats {
        private final int threadId;
        private final long userTime;
        private final long systemTime;
        private final long upTime;
        private final OSProcess.State state;
        private final int priority;

        public ThreadStats(int tid, double cpu, char state, long sTime, long uTime, int pri) {
            this.threadId = tid;
            this.userTime = uTime;
            this.systemTime = sTime;
            this.upTime = (long)((double)(uTime + sTime) / (cpu / 100.0 + 5.0E-4));
            switch (state) {
                case 'I': 
                case 'S': {
                    this.state = OSProcess.State.SLEEPING;
                    break;
                }
                case 'U': {
                    this.state = OSProcess.State.WAITING;
                    break;
                }
                case 'R': {
                    this.state = OSProcess.State.RUNNING;
                    break;
                }
                case 'Z': {
                    this.state = OSProcess.State.ZOMBIE;
                    break;
                }
                case 'T': {
                    this.state = OSProcess.State.STOPPED;
                    break;
                }
                default: {
                    this.state = OSProcess.State.OTHER;
                }
            }
            this.priority = pri;
        }

        public int getThreadId() {
            return this.threadId;
        }

        public long getUserTime() {
            return this.userTime;
        }

        public long getSystemTime() {
            return this.systemTime;
        }

        public long getUpTime() {
            return this.upTime;
        }

        public OSProcess.State getState() {
            return this.state;
        }

        public int getPriority() {
            return this.priority;
        }
    }
}

