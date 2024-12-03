/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jni;

@Deprecated
public class Proc {
    public static final int APR_SHELLCM = 0;
    public static final int APR_PROGRAM = 1;
    public static final int APR_PROGRAM_ENV = 2;
    public static final int APR_PROGRAM_PATH = 3;
    public static final int APR_SHELLCMD_ENV = 4;
    public static final int APR_WAIT = 0;
    public static final int APR_NOWAIT = 1;
    public static final int APR_PROC_EXIT = 1;
    public static final int APR_PROC_SIGNAL = 2;
    public static final int APR_PROC_SIGNAL_CORE = 4;
    public static final int APR_NO_PIPE = 0;
    public static final int APR_FULL_BLOCK = 1;
    public static final int APR_FULL_NONBLOCK = 2;
    public static final int APR_PARENT_BLOCK = 3;
    public static final int APR_CHILD_BLOCK = 4;
    public static final int APR_LIMIT_CPU = 0;
    public static final int APR_LIMIT_MEM = 1;
    public static final int APR_LIMIT_NPROC = 2;
    public static final int APR_LIMIT_NOFILE = 3;
    public static final int APR_OC_REASON_DEATH = 0;
    public static final int APR_OC_REASON_UNWRITABLE = 1;
    public static final int APR_OC_REASON_RESTART = 2;
    public static final int APR_OC_REASON_UNREGISTER = 3;
    public static final int APR_OC_REASON_LOST = 4;
    public static final int APR_OC_REASON_RUNNING = 5;
    public static final int APR_KILL_NEVER = 0;
    public static final int APR_KILL_ALWAYS = 1;
    public static final int APR_KILL_AFTER_TIMEOUT = 2;
    public static final int APR_JUST_WAIT = 3;
    public static final int APR_KILL_ONLY_ONCE = 4;
    public static final int APR_PROC_DETACH_FOREGROUND = 0;
    public static final int APR_PROC_DETACH_DAEMONIZE = 1;
    public static final int MAX_ARGS_SIZE = 1024;
    public static final int MAX_ENV_SIZE = 1024;

    public static native long alloc(long var0);

    public static native int fork(long[] var0, long var1);

    public static native int create(long var0, String var2, String[] var3, String[] var4, long var5, long var7);

    public static native int wait(long var0, int[] var2, int var3);

    public static native int waitAllProcs(long var0, int[] var2, int var3, long var4);

    public static native int detach(int var0);

    public static native int kill(long var0, int var2);
}

