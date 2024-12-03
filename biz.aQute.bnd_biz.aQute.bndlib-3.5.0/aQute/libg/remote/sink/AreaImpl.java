/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.remote.sink;

import aQute.libg.command.Command;
import aQute.libg.remote.Area;
import java.io.File;
import java.io.InputStream;
import java.io.PipedOutputStream;

public class AreaImpl
extends Area {
    File root;
    File cwd;
    Command command;
    Thread thread;
    InputStream stdin;
    Appendable stdout;
    Appendable stderr;
    PipedOutputStream toStdin;
}

