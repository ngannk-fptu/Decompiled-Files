/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.remote.source;

import aQute.libg.remote.Area;
import aQute.libg.remote.Event;
import aQute.libg.remote.Sink;
import aQute.libg.remote.Source;
import aQute.libg.remote.Welcome;
import aQute.libg.remote.source.SourceFS;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class RemoteSource
implements Source {
    private Sink sink;
    private Appendable stdout;
    private Appendable stderr;
    private Thread thread;
    volatile AtomicBoolean running = new AtomicBoolean();
    Welcome welcome;
    SourceFS fsync;
    String areaId;
    File cwd;

    public void open(Sink sink, File cwd, String areaId) {
        this.sink = sink;
        this.cwd = cwd;
        this.areaId = areaId;
        this.welcome = sink.getWelcome(1);
        this.fsync = new SourceFS(this.welcome.separatorChar, cwd, sink, areaId);
    }

    @Override
    public byte[] getData(String sha) throws Exception {
        return this.fsync.getData(sha);
    }

    public void close() throws IOException {
    }

    @Override
    public void event(Event e, Area area) throws Exception {
        switch (e) {
            case created: {
                break;
            }
            case deleted: {
                break;
            }
            case exited: {
                this.exit();
                break;
            }
            case launching: {
                break;
            }
            case restarted: {
                break;
            }
            case running: {
                break;
            }
            case started: {
                break;
            }
            case virginal: {
                break;
            }
        }
    }

    private void exit() {
        if (this.running.getAndSet(false)) {
            this.thread.interrupt();
            this.stdout = null;
            this.stderr = null;
        }
    }

    @Override
    public void output(String areaId, CharSequence text, boolean err) throws IOException {
        if (this.running.get()) {
            if (err) {
                this.stderr.append(text);
            } else {
                this.stdout.append(text);
            }
        }
    }

    public Sink getSink() {
        return this.sink;
    }

    public void launch(Map<String, String> env, List<String> args, final InputStream stdin, Appendable stdout, Appendable stderr) throws Exception {
        if (!this.running.getAndSet(true)) {
            for (int i = 0; i < args.size(); ++i) {
                args.set(i, this.fsync.transform(args.get(i)));
            }
            for (Map.Entry<String, String> e : env.entrySet()) {
                e.setValue(this.fsync.transform(e.getValue()));
            }
            this.fsync.sync();
            this.stdout = stdout;
            this.stderr = stderr;
            this.thread = new Thread("source::" + this.areaId){

                @Override
                public void run() {
                    byte[] data = new byte[10000];
                    while (!Thread.currentThread().isInterrupted() && RemoteSource.this.running.get()) {
                        try {
                            int length = stdin.read(data);
                            if (length < 0) {
                                RemoteSource.this.cancel();
                                continue;
                            }
                            if (length <= 0) continue;
                            RemoteSource.this.getSink().input(RemoteSource.this.areaId, new String(data));
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            if (this.sink.launch(this.areaId, env, args)) {
                this.thread.start();
            } else {
                this.exit();
            }
        } else {
            throw new IllegalStateException("Already running " + this.areaId);
        }
    }

    public void cancel() throws Exception {
        this.getSink().cancel(this.areaId);
    }

    public void update(File f) throws Exception {
        this.fsync.markTransform(f);
    }

    public void sync() throws Exception {
        this.fsync.sync();
    }

    public void add(File file) throws Exception {
        this.fsync.add(file);
    }

    public void join() throws InterruptedException {
        while (this.running.get()) {
            Thread.sleep(500L);
        }
    }
}

