/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.IdentityMapper;
import org.apache.tools.ant.util.PropertyOutputStream;

public class PathConvert
extends Task {
    private static boolean onWindows = Os.isFamily("dos");
    private Resources path;
    private Reference refid;
    private String targetOS;
    private boolean targetWindows;
    private boolean setonempty = true;
    private String property;
    private List<MapEntry> prefixMap = new Vector<MapEntry>();
    private String pathSep;
    private String dirSep;
    private Mapper mapper;
    private boolean preserveDuplicates;
    private Resource dest;

    public Path createPath() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        Path result = new Path(this.getProject());
        this.add(result);
        return result;
    }

    public void add(ResourceCollection rc) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.getPath().add(rc);
    }

    private synchronized Resources getPath() {
        if (this.path == null) {
            this.path = new Resources(this.getProject());
            this.path.setCache(false);
        }
        return this.path;
    }

    public MapEntry createMap() {
        MapEntry entry = new MapEntry();
        this.prefixMap.add(entry);
        return entry;
    }

    @Deprecated
    public void setTargetos(String target) {
        TargetOs to = new TargetOs();
        to.setValue(target);
        this.setTargetos(to);
    }

    public void setTargetos(TargetOs target) {
        this.targetOS = target.getValue();
        this.targetWindows = !"unix".equals(this.targetOS) && !"tandem".equals(this.targetOS);
    }

    public void setSetonempty(boolean setonempty) {
        this.setonempty = setonempty;
    }

    public void setProperty(String p) {
        this.property = p;
    }

    public void setRefid(Reference r) {
        if (this.path != null) {
            throw this.noChildrenAllowed();
        }
        this.refid = r;
    }

    public void setPathSep(String sep) {
        this.pathSep = sep;
    }

    public void setDirSep(String sep) {
        this.dirSep = sep;
    }

    public void setPreserveDuplicates(boolean preserveDuplicates) {
        this.preserveDuplicates = preserveDuplicates;
    }

    public boolean isPreserveDuplicates() {
        return this.preserveDuplicates;
    }

    public boolean isReference() {
        return this.refid != null;
    }

    public void setDest(Resource dest) {
        if (dest != null && this.dest != null) {
            throw new BuildException("@dest already set");
        }
        this.dest = dest;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute() throws BuildException {
        Resources savedPath = this.path;
        String savedPathSep = this.pathSep;
        String savedDirSep = this.dirSep;
        try {
            if (this.isReference()) {
                Object o = this.refid.getReferencedObject(this.getProject());
                if (!(o instanceof ResourceCollection)) {
                    throw new BuildException("refid '%s' does not refer to a resource collection.", this.refid.getRefId());
                }
                this.getPath().add((ResourceCollection)o);
            }
            this.validateSetup();
            boolean first = true;
            try (Output<?> o = this.createOutput();){
                for (String s : this.streamResources()::iterator) {
                    if (first) {
                        first = false;
                    } else {
                        o.accept(this.pathSep);
                    }
                    o.accept(s);
                }
            }
            catch (IOException e) {
                throw new BuildException(e);
            }
        }
        finally {
            this.path = savedPath;
            this.dirSep = savedDirSep;
            this.pathSep = savedPathSep;
        }
    }

    private Output<?> createOutput() throws IOException {
        if (this.dest != null) {
            return new Output<Writer>((Writer)new OutputStreamWriter(this.dest.getOutputStream())){

                @Override
                void doAccept(String t) throws IOException {
                    ((Writer)this.target).write(t);
                }
            };
        }
        OutputStream out = this.property == null ? new LogOutputStream(this) : new PropertyOutputStream(this.getProject(), this.property){

            @Override
            public void close() {
                if (PathConvert.this.setonempty || this.size() > 0) {
                    super.close();
                    PathConvert.this.log("Set property " + PathConvert.this.property + " = " + PathConvert.this.getProject().getProperty(PathConvert.this.property), 3);
                }
            }
        };
        return new Output<OutputStream>(out){

            @Override
            void doAccept(String t) throws IOException {
                ((OutputStream)this.target).write(t.getBytes());
            }
        };
    }

    private Stream<String> streamResources() {
        String fromDirSep;
        DataType resources = this.isPreserveDuplicates() ? this.path : Union.getInstance(this.path);
        FileNameMapper mapperImpl = this.mapper == null ? new IdentityMapper() : this.mapper.getImplementation();
        boolean parallel = false;
        Stream<String> result = StreamSupport.stream(resources.spliterator(), false).map(String::valueOf).map(mapperImpl::mapFileName).filter(Objects::nonNull).flatMap(Stream::of).map(this::mapElement);
        String string = fromDirSep = onWindows ? "\\" : "/";
        if (fromDirSep.equals(this.dirSep)) {
            return result;
        }
        return result.map(s -> s.replace(fromDirSep, this.dirSep));
    }

    private String mapElement(String elem) {
        Predicate<Object> changed = o -> o != elem;
        return (String)this.prefixMap.stream().map(e -> e.apply(elem)).filter(changed).findFirst().orElse(elem);
    }

    public void addMapper(Mapper mapper) {
        if (this.mapper != null) {
            throw new BuildException("Cannot define more than one mapper");
        }
        this.mapper = mapper;
    }

    public void add(FileNameMapper fileNameMapper) {
        Mapper m = new Mapper(this.getProject());
        m.add(fileNameMapper);
        this.addMapper(m);
    }

    private void validateSetup() throws BuildException {
        if (this.path == null) {
            throw new BuildException("You must specify a path to convert");
        }
        if (this.property != null && this.dest != null) {
            throw new BuildException("@property and @dest are mutually exclusive");
        }
        String dsep = File.separator;
        String psep = File.pathSeparator;
        if (this.targetOS != null) {
            psep = this.targetWindows ? ";" : ":";
            String string = dsep = this.targetWindows ? "\\" : "/";
        }
        if (this.pathSep != null) {
            psep = this.pathSep;
        }
        if (this.dirSep != null) {
            dsep = this.dirSep;
        }
        this.pathSep = psep;
        this.dirSep = dsep;
    }

    private BuildException noChildrenAllowed() {
        return new BuildException("You must not specify nested elements when using the refid attribute.");
    }

    public class MapEntry {
        private String from = null;
        private String to = null;

        public void setFrom(String from) {
            this.from = from;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String apply(String elem) {
            if (this.from == null || this.to == null) {
                throw new BuildException("Both 'from' and 'to' must be set in a map entry");
            }
            String cmpElem = onWindows ? elem.toLowerCase().replace('\\', '/') : elem;
            String cmpFrom = onWindows ? this.from.toLowerCase().replace('\\', '/') : this.from;
            return cmpElem.startsWith(cmpFrom) ? this.to + elem.substring(this.from.length()) : elem;
        }
    }

    public static class TargetOs
    extends EnumeratedAttribute {
        @Override
        public String[] getValues() {
            return new String[]{"windows", "unix", "netware", "os/2", "tandem"};
        }
    }

    private static abstract class Output<T extends Closeable>
    implements Consumer<String>,
    Closeable {
        final T target;
        final /* synthetic */ PathConvert this$0;

        Output(T target) {
            this.this$0 = var1_1;
            this.target = target;
        }

        @Override
        public void close() throws IOException {
            this.target.close();
        }

        @Override
        public void accept(String t) {
            try {
                this.doAccept(t);
            }
            catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

        abstract void doAccept(String var1) throws Exception;
    }
}

