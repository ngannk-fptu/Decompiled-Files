/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.remote.sink;

import aQute.lib.io.IO;
import aQute.lib.json.JSONCodec;
import aQute.libg.command.Command;
import aQute.libg.remote.Area;
import aQute.libg.remote.Delta;
import aQute.libg.remote.Event;
import aQute.libg.remote.Sink;
import aQute.libg.remote.Source;
import aQute.libg.remote.Welcome;
import aQute.libg.remote.sink.Appender;
import aQute.libg.remote.sink.AreaImpl;
import aQute.libg.remote.sink.SinkFS;
import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RemoteSink
implements Sink {
    static final JSONCodec codec = new JSONCodec();
    final File root;
    Source[] sources;
    final Map<String, AreaImpl> areas = new ConcurrentHashMap<String, AreaImpl>();
    final File areasDir;
    final SinkFS sinkfs;
    private File shacache;

    public RemoteSink(File root, Source ... s) throws Exception {
        this.root = root;
        this.areasDir = new File(root, "areas");
        IO.mkdirs(this.areasDir);
        for (File areaDir : this.areasDir.listFiles()) {
            this.areas.put(areaDir.getName(), this.read(areaDir));
        }
        this.sources = s;
        this.shacache = new File(root, "shacache");
        IO.mkdirs(this.shacache);
        this.sinkfs = new SinkFS(s, this.shacache);
    }

    @Override
    public AreaImpl getArea(String areaId) throws Exception {
        AreaImpl area = this.areas.get(areaId);
        if (area != null) {
            return area;
        }
        File af = new File(this.areasDir, areaId);
        IO.mkdirs(af);
        return this.read(af);
    }

    @Override
    public boolean removeArea(String areaId) throws Exception {
        AreaImpl area = this.areas.remove(areaId);
        if (area != null) {
            IO.delete(area.root);
            return true;
        }
        return false;
    }

    @Override
    public boolean launch(String areaId, Map<String, String> env, List<String> args) throws Exception {
        final AreaImpl area = this.getArea(areaId);
        if (area == null) {
            throw new IllegalArgumentException("No such area");
        }
        if (area.running) {
            throw new IllegalStateException("Already running");
        }
        area.command = new Command();
        area.command.addAll(args);
        area.command.setCwd(area.cwd);
        if (env != null) {
            for (Map.Entry<String, String> e : env.entrySet()) {
                area.command.var(e.getKey(), e.getValue());
            }
        }
        area.line = area.command.toString();
        PipedInputStream pin = new PipedInputStream();
        PipedOutputStream pout = new PipedOutputStream();
        pout.connect(pin);
        area.toStdin = pout;
        area.stdin = pin;
        area.stdout = new Appender(this.sources, area.id, false);
        area.stderr = new Appender(this.sources, area.id, true);
        area.thread = new Thread(areaId + "::" + args){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                try {
                    RemoteSink.this.event(Event.launching, area);
                    area.running = true;
                    area.command.setCwd(area.cwd);
                    area.command.setUseThreadForInput(true);
                    area.exitCode = area.command.execute(area.stdin, area.stdout, area.stderr);
                }
                catch (Throwable e) {
                    area.exitCode = -1;
                    area.exception = e.toString();
                }
                finally {
                    area.running = false;
                    area.toStdin = null;
                    area.stderr = null;
                    area.stdout = null;
                    area.stdin = null;
                    area.command = null;
                    RemoteSink.this.event(Event.exited, area);
                }
            }
        };
        area.thread.start();
        return true;
    }

    @Override
    public void cancel(String areaId) throws Exception {
        AreaImpl area = this.getArea(areaId);
        if (area == null) {
            throw new IllegalArgumentException("No such area");
        }
        if (!area.running) {
            throw new IllegalStateException("Not running");
        }
        area.canceled = true;
        area.command.cancel();
    }

    @Override
    public void input(String areaId, String text) throws Exception {
        AreaImpl area = this.getArea(areaId);
        PipedOutputStream input = area.toStdin;
        if (input == null) {
            throw new IllegalStateException("Area " + areaId + " is not running");
        }
        input.write(text.getBytes());
    }

    @Override
    public int exit(String areaId) throws Exception {
        AreaImpl area = this.getArea(areaId);
        Command c = area.command;
        if (!area.running || c == null) {
            throw new IllegalStateException("Area " + areaId + " is not running");
        }
        c.cancel();
        area.thread.join(10000L);
        return area.exitCode;
    }

    @Override
    public byte[] view(String areaId, String path) throws Exception {
        AreaImpl area = this.getArea(areaId);
        File f = new File(area.cwd, path);
        if (f.isDirectory()) {
            StringBuilder sb = new StringBuilder();
            for (String s : f.list()) {
                sb.append(s).append("\n");
            }
            return sb.toString().getBytes(StandardCharsets.UTF_8);
        }
        if (f.isFile()) {
            return IO.read(f);
        }
        return null;
    }

    @Override
    public void exit() throws Exception {
    }

    @Override
    public Welcome getWelcome(int highest) {
        Welcome welcome = new Welcome();
        welcome.separatorChar = File.separatorChar;
        welcome.properties = System.getProperties();
        welcome.version = Math.min(highest, 1);
        return welcome;
    }

    @Override
    public AreaImpl createArea(String areaId) throws Exception {
        AreaImpl area = new AreaImpl();
        if (areaId == null) {
            int n = 1000;
            while (!new File(this.areasDir, "" + n).isDirectory()) {
                ++n;
            }
            areaId = "" + n;
        }
        File dir = new File(this.areasDir, areaId);
        IO.mkdirs(dir);
        return this.read(dir);
    }

    @Override
    public Collection<? extends Area> getAreas() {
        return this.areas.values();
    }

    protected AreaImpl read(File areaDir) throws Exception {
        AreaImpl area = new AreaImpl();
        area.id = areaDir.getName();
        area.root = areaDir;
        area.running = false;
        area.cwd = new File(area.root, "cwd");
        IO.mkdirs(area.cwd);
        this.areas.put(area.id, area);
        return area;
    }

    public void setSources(Source ... sources) {
        this.sources = sources;
        this.sinkfs.setSources(sources);
    }

    void event(Event e, AreaImpl area) {
        for (Source source : this.sources) {
            try {
                source.event(e, area);
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public boolean sync(String areaId, Collection<Delta> deltas) throws Exception {
        AreaImpl area = this.getArea(areaId);
        return this.sinkfs.delta(area.cwd, deltas);
    }

    @Override
    public boolean clearCache() {
        try {
            IO.deleteWithException(this.shacache);
            IO.mkdirs(this.shacache);
            return true;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

