/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.osgi;

import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Annotation;
import aQute.bnd.osgi.ClassDataCollector;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Instruction;
import aQute.bnd.osgi.OpCodes;
import aQute.bnd.osgi.Resource;
import aQute.lib.io.ByteBufferDataInput;
import aQute.lib.utf8properties.UTF8Properties;
import aQute.libg.generics.Create;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Clazz {
    private static final Logger logger = LoggerFactory.getLogger(Clazz.class);
    static Pattern METHOD_DESCRIPTOR = Pattern.compile("(.*)\\)(.+)");
    public static final EnumSet<QUERY> HAS_ARGUMENT = EnumSet.of(QUERY.IMPLEMENTS, new QUERY[]{QUERY.EXTENDS, QUERY.IMPORTS, QUERY.NAMED, QUERY.VERSION, QUERY.ANNOTATED});
    static final int ACC_PUBLIC = 1;
    static final int ACC_FINAL = 16;
    static final int ACC_SUPER = 32;
    static final int ACC_INTERFACE = 512;
    static final int ACC_ABSTRACT = 1024;
    static final int ACC_SYNTHETIC = 4096;
    static final int ACC_BRIDGE = 64;
    static final int ACC_ANNOTATION = 8192;
    static final int ACC_ENUM = 16384;
    static final int ACC_MODULE = 32768;
    public static final Comparator<Clazz> NAME_COMPARATOR = new Comparator<Clazz>(){

        @Override
        public int compare(Clazz a, Clazz b) {
            return a.className.compareTo(b.className);
        }
    };
    boolean hasRuntimeAnnotations;
    boolean hasClassAnnotations;
    boolean hasDefaultConstructor;
    int depth = 0;
    Deque<ClassDataCollector> cds = new LinkedList<ClassDataCollector>();
    Descriptors.TypeRef className;
    Object[] pool;
    int[] intPool;
    Set<Descriptors.PackageRef> imports = Create.set();
    String path;
    int minor = 0;
    int major = 0;
    int innerAccess = -1;
    int accessx = 0;
    String sourceFile;
    Set<Descriptors.TypeRef> xref;
    Set<Descriptors.TypeRef> annotations;
    int forName = 0;
    int class$ = 0;
    Descriptors.TypeRef[] interfaces;
    Descriptors.TypeRef zuper;
    ClassDataCollector cd = null;
    Resource resource;
    FieldDef last = null;
    boolean deprecated;
    Set<Descriptors.PackageRef> api;
    final Analyzer analyzer;
    String classSignature;
    private boolean detectLdc;
    private Map<String, Object> defaults;

    public Clazz(Analyzer analyzer, String path, Resource resource) {
        this.path = path;
        this.resource = resource;
        this.analyzer = analyzer;
    }

    public Set<Descriptors.TypeRef> parseClassFile() throws Exception {
        return this.parseClassFileWithCollector(null);
    }

    public Set<Descriptors.TypeRef> parseClassFile(InputStream in) throws Exception {
        return this.parseClassFile(in, null);
    }

    public Set<Descriptors.TypeRef> parseClassFileWithCollector(ClassDataCollector cd) throws Exception {
        ByteBuffer bb = this.resource.buffer();
        if (bb != null) {
            return this.parseClassFileData(ByteBufferDataInput.wrap(bb), cd);
        }
        return this.parseClassFile(this.resource.openInputStream(), cd);
    }

    public Set<Descriptors.TypeRef> parseClassFile(InputStream in, ClassDataCollector cd) throws Exception {
        try (DataInputStream din = new DataInputStream(in);){
            Set<Descriptors.TypeRef> set = this.parseClassFileData(din, cd);
            return set;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Set<Descriptors.TypeRef> parseClassFileData(DataInput in, ClassDataCollector cd) throws Exception {
        this.cds.push(this.cd);
        this.cd = cd;
        try {
            Set<Descriptors.TypeRef> set = this.parseClassFileData(in);
            return set;
        }
        finally {
            this.cd = this.cds.pop();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    Set<Descriptors.TypeRef> parseClassFileData(DataInput in) throws Exception {
        logger.debug("parseClassFile(): path={} resource={}", (Object)this.path, (Object)this.resource);
        ++this.depth;
        this.xref = new HashSet<Descriptors.TypeRef>();
        boolean crawl = this.cd != null;
        int magic = in.readInt();
        if (magic != -889275714) {
            throw new IOException("Not a valid class file (no CAFEBABE header)");
        }
        this.minor = in.readUnsignedShort();
        this.major = in.readUnsignedShort();
        if (this.cd != null) {
            this.cd.version(this.minor, this.major);
        }
        int count = in.readUnsignedShort();
        this.pool = new Object[count];
        this.intPool = new int[count];
        CONSTANT[] tags = CONSTANT.values();
        block23: for (int poolIndex = 1; poolIndex < count; ++poolIndex) {
            int tagValue = in.readUnsignedByte();
            if (tagValue >= tags.length) {
                throw new IOException("Unrecognized constant pool tag value " + tagValue);
            }
            CONSTANT tag = tags[tagValue];
            switch (tag) {
                case Zero: {
                    break block23;
                }
                case Utf8: {
                    this.constantUtf8(in, poolIndex);
                    continue block23;
                }
                case Integer: {
                    this.constantInteger(in, poolIndex);
                    continue block23;
                }
                case Float: {
                    this.constantFloat(in, poolIndex);
                    continue block23;
                }
                case Long: {
                    this.constantLong(in, poolIndex);
                    ++poolIndex;
                    continue block23;
                }
                case Double: {
                    this.constantDouble(in, poolIndex);
                    ++poolIndex;
                    continue block23;
                }
                case Class: {
                    this.constantClass(in, poolIndex);
                    continue block23;
                }
                case String: {
                    this.constantString(in, poolIndex);
                    continue block23;
                }
                case Fieldref: 
                case Methodref: 
                case InterfaceMethodref: {
                    this.ref(in, poolIndex);
                    continue block23;
                }
                case NameAndType: {
                    this.nameAndType(in, poolIndex, tag);
                    continue block23;
                }
                case MethodHandle: {
                    this.methodHandle(in, poolIndex, tag);
                    continue block23;
                }
                case MethodType: {
                    this.methodType(in, poolIndex, tag);
                    continue block23;
                }
                case InvokeDynamic: {
                    this.invokeDynamic(in, poolIndex, tag);
                    continue block23;
                }
                default: {
                    int skip = tag.skip();
                    if (skip == -1) {
                        throw new IOException("Invalid tag " + (Object)((Object)tag));
                    }
                    in.skipBytes(skip);
                }
            }
        }
        this.pool(this.pool, this.intPool);
        int index = -1;
        block24: for (Object o : this.pool) {
            ++index;
            if (o == null || !(o instanceof Assoc)) continue;
            Assoc assoc = (Assoc)o;
            switch (assoc.tag) {
                case Fieldref: 
                case Methodref: 
                case InterfaceMethodref: {
                    this.classConstRef(assoc.a);
                    continue block24;
                }
                case NameAndType: 
                case MethodType: {
                    this.referTo(assoc.b, 0);
                    continue block24;
                }
            }
        }
        index = -1;
        for (Object o : this.pool) {
            ++index;
            if (!(o instanceof ClassConstant)) continue;
            ClassConstant cc = (ClassConstant)o;
            if (cc.referred) continue;
            this.detectLdc = true;
        }
        this.accessx = in.readUnsignedShort();
        if (Modifier.isPublic(this.accessx)) {
            this.api = new HashSet<Descriptors.PackageRef>();
        }
        int this_class = in.readUnsignedShort();
        this.className = this.analyzer.getTypeRef((String)this.pool[this.intPool[this_class]]);
        if (!this.isModule()) {
            this.referTo(this.className, 1);
        }
        try {
            int interfacesCount;
            if (this.cd != null && !this.cd.classStart(this)) {
                Set<Descriptors.TypeRef> len$ = null;
                return len$;
            }
            int super_class = in.readUnsignedShort();
            String superName = (String)this.pool[this.intPool[super_class]];
            if (superName != null) {
                this.zuper = this.analyzer.getTypeRef(superName);
            }
            if (this.zuper != null) {
                this.referTo(this.zuper, this.accessx);
                if (this.cd != null) {
                    this.cd.extendsClass(this.zuper);
                }
            }
            if ((interfacesCount = in.readUnsignedShort()) > 0) {
                this.interfaces = new Descriptors.TypeRef[interfacesCount];
                for (int i = 0; i < interfacesCount; ++i) {
                    this.interfaces[i] = this.analyzer.getTypeRef((String)this.pool[this.intPool[in.readUnsignedShort()]]);
                    this.referTo(this.interfaces[i], this.accessx);
                }
                if (this.cd != null) {
                    this.cd.implementsInterfaces(this.interfaces);
                }
            }
            int fieldsCount = in.readUnsignedShort();
            for (int i = 0; i < fieldsCount; ++i) {
                int access_flags = in.readUnsignedShort();
                int name_index = in.readUnsignedShort();
                int descriptor_index = in.readUnsignedShort();
                String name = this.pool[name_index].toString();
                if (name.startsWith("class$") || name.startsWith("$class$")) {
                    crawl = true;
                }
                if (this.cd != null) {
                    this.last = new FieldDef(access_flags, name, this.pool[descriptor_index].toString());
                    this.cd.field(this.last);
                }
                this.referTo(descriptor_index, access_flags);
                this.doAttributes(in, ElementType.FIELD, false, access_flags);
            }
            if (crawl) {
                this.forName = this.findMethodReference("java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;");
                this.class$ = this.findMethodReference(this.className.getBinary(), "class$", "(Ljava/lang/String;)Ljava/lang/Class;");
            } else if (this.major == 48) {
                this.forName = this.findMethodReference("java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;");
                if (this.forName > 0) {
                    crawl = true;
                    this.class$ = this.findMethodReference(this.className.getBinary(), "class$", "(Ljava/lang/String;)Ljava/lang/Class;");
                }
            }
            crawl |= this.detectLdc;
            int methodCount = in.readUnsignedShort();
            for (int i = 0; i < methodCount; ++i) {
                int access_flags = in.readUnsignedShort();
                int name_index = in.readUnsignedShort();
                int descriptor_index = in.readUnsignedShort();
                String name = this.pool[name_index].toString();
                String descriptor = this.pool[descriptor_index].toString();
                MethodDef mdef = null;
                if (this.cd != null) {
                    mdef = new MethodDef(access_flags, name, descriptor);
                    this.last = mdef;
                    this.cd.method(mdef);
                }
                this.referTo(descriptor_index, access_flags);
                if ("<init>".equals(name)) {
                    if (Modifier.isPublic(access_flags) && "()V".equals(descriptor)) {
                        this.hasDefaultConstructor = true;
                    }
                    this.doAttributes(in, ElementType.CONSTRUCTOR, crawl, access_flags);
                    continue;
                }
                this.doAttributes(in, ElementType.METHOD, crawl, access_flags);
            }
            if (this.cd != null) {
                this.cd.memberEnd();
            }
            this.last = null;
            this.doAttributes(in, ElementType.TYPE, false, this.accessx);
            Set<Descriptors.TypeRef> xref = this.xref;
            this.reset();
            Set<Descriptors.TypeRef> set = xref;
            return set;
        }
        finally {
            if (this.cd != null) {
                this.cd.classEnd();
            }
        }
    }

    private void constantFloat(DataInput in, int poolIndex) throws IOException {
        if (this.cd != null) {
            this.pool[poolIndex] = Float.valueOf(in.readFloat());
        } else {
            in.skipBytes(4);
        }
    }

    private void constantInteger(DataInput in, int poolIndex) throws IOException {
        this.intPool[poolIndex] = in.readInt();
        if (this.cd != null) {
            this.pool[poolIndex] = this.intPool[poolIndex];
        }
    }

    private void pool(Object[] pool, int[] intPool) {
    }

    private void nameAndType(DataInput in, int poolIndex, CONSTANT tag) throws IOException {
        int name_index = in.readUnsignedShort();
        int descriptor_index = in.readUnsignedShort();
        this.pool[poolIndex] = new Assoc(tag, name_index, descriptor_index);
    }

    private void methodType(DataInput in, int poolIndex, CONSTANT tag) throws IOException {
        int descriptor_index = in.readUnsignedShort();
        this.pool[poolIndex] = new Assoc(tag, 0, descriptor_index);
    }

    private void methodHandle(DataInput in, int poolIndex, CONSTANT tag) throws IOException {
        int reference_kind = in.readUnsignedByte();
        int reference_index = in.readUnsignedShort();
        this.pool[poolIndex] = new Assoc(tag, reference_kind, reference_index);
    }

    private void invokeDynamic(DataInput in, int poolIndex, CONSTANT tag) throws IOException {
        int bootstrap_method_attr_index = in.readUnsignedShort();
        int name_and_type_index = in.readUnsignedShort();
        this.pool[poolIndex] = new Assoc(tag, bootstrap_method_attr_index, name_and_type_index);
    }

    private void ref(DataInput in, int poolIndex) throws IOException {
        int class_index = in.readUnsignedShort();
        int name_and_type_index = in.readUnsignedShort();
        this.pool[poolIndex] = new Assoc(CONSTANT.Methodref, class_index, name_and_type_index);
    }

    private void constantString(DataInput in, int poolIndex) throws IOException {
        int string_index;
        this.intPool[poolIndex] = string_index = in.readUnsignedShort();
    }

    private void constantClass(DataInput in, int poolIndex) throws IOException {
        int class_index;
        this.intPool[poolIndex] = class_index = in.readUnsignedShort();
        ClassConstant c = new ClassConstant(class_index);
        this.pool[poolIndex] = c;
    }

    private void constantDouble(DataInput in, int poolIndex) throws IOException {
        if (this.cd != null) {
            this.pool[poolIndex] = in.readDouble();
        } else {
            in.skipBytes(8);
        }
    }

    private void constantLong(DataInput in, int poolIndex) throws IOException {
        if (this.cd != null) {
            this.pool[poolIndex] = in.readLong();
        } else {
            in.skipBytes(8);
        }
    }

    private void constantUtf8(DataInput in, int poolIndex) throws IOException {
        String name = in.readUTF();
        this.pool[poolIndex] = name;
    }

    private int findMethodReference(String clazz, String methodname, String descriptor) {
        for (int i = 1; i < this.pool.length; ++i) {
            int class_index;
            int class_name_index;
            if (!(this.pool[i] instanceof Assoc)) continue;
            Assoc methodref = (Assoc)this.pool[i];
            if (methodref.tag != CONSTANT.Methodref || !clazz.equals(this.pool[class_name_index = this.intPool[class_index = methodref.a]])) continue;
            int name_and_type_index = methodref.b;
            Assoc name_and_type = (Assoc)this.pool[name_and_type_index];
            if (name_and_type.tag != CONSTANT.NameAndType) continue;
            int name_index = name_and_type.a;
            int type_index = name_and_type.b;
            if (!methodname.equals(this.pool[name_index]) || !descriptor.equals(this.pool[type_index])) continue;
            return i;
        }
        return -1;
    }

    private void doAttributes(DataInput in, ElementType member, boolean crawl, int access_flags) throws Exception {
        int attributesCount = in.readUnsignedShort();
        for (int j = 0; j < attributesCount; ++j) {
            this.doAttribute(in, member, crawl, access_flags);
        }
    }

    private void doAttribute(DataInput in, ElementType member, boolean crawl, int access_flags) throws Exception {
        int attribute_name_index = in.readUnsignedShort();
        String attributeName = (String)this.pool[attribute_name_index];
        long attribute_length = 0xFFFFFFFFL & (long)in.readInt();
        switch (attributeName) {
            case "Deprecated": {
                if (this.cd == null) break;
                this.cd.deprecated();
                break;
            }
            case "RuntimeVisibleAnnotations": {
                this.doAnnotations(in, member, RetentionPolicy.RUNTIME, access_flags);
                break;
            }
            case "RuntimeInvisibleAnnotations": {
                this.doAnnotations(in, member, RetentionPolicy.CLASS, access_flags);
                break;
            }
            case "RuntimeVisibleParameterAnnotations": {
                this.doParameterAnnotations(in, member, RetentionPolicy.RUNTIME, access_flags);
                break;
            }
            case "RuntimeInvisibleParameterAnnotations": {
                this.doParameterAnnotations(in, member, RetentionPolicy.CLASS, access_flags);
                break;
            }
            case "RuntimeVisibleTypeAnnotations": {
                this.doTypeAnnotations(in, member, RetentionPolicy.RUNTIME, access_flags);
                break;
            }
            case "RuntimeInvisibleTypeAnnotations": {
                this.doTypeAnnotations(in, member, RetentionPolicy.CLASS, access_flags);
                break;
            }
            case "InnerClasses": {
                this.doInnerClasses(in);
                break;
            }
            case "EnclosingMethod": {
                this.doEnclosingMethod(in);
                break;
            }
            case "SourceFile": {
                this.doSourceFile(in);
                break;
            }
            case "Code": {
                this.doCode(in, crawl);
                break;
            }
            case "Signature": {
                this.doSignature(in, member, access_flags);
                break;
            }
            case "ConstantValue": {
                this.doConstantValue(in);
                break;
            }
            case "AnnotationDefault": {
                Object value = this.doElementValue(in, member, RetentionPolicy.RUNTIME, this.cd != null, access_flags);
                if (!(this.last instanceof MethodDef)) break;
                ((MethodDef)this.last).constant = value;
                this.cd.annotationDefault((MethodDef)this.last, value);
                break;
            }
            case "Exceptions": {
                this.doExceptions(in, access_flags);
                break;
            }
            case "BootstrapMethods": {
                this.doBootstrapMethods(in);
                break;
            }
            case "StackMapTable": {
                this.doStackMapTable(in);
                break;
            }
            default: {
                if (attribute_length > Integer.MAX_VALUE) {
                    throw new IllegalArgumentException("Attribute > 2Gb");
                }
                in.skipBytes((int)attribute_length);
            }
        }
    }

    private void doEnclosingMethod(DataInput in) throws IOException {
        int cIndex = in.readUnsignedShort();
        int mIndex = in.readUnsignedShort();
        this.classConstRef(cIndex);
        if (this.cd != null) {
            int nameIndex = this.intPool[cIndex];
            Descriptors.TypeRef cName = this.analyzer.getTypeRef((String)this.pool[nameIndex]);
            String mName = null;
            String mDescriptor = null;
            if (mIndex != 0) {
                Assoc nameAndType = (Assoc)this.pool[mIndex];
                mName = (String)this.pool[nameAndType.a];
                mDescriptor = (String)this.pool[nameAndType.b];
            }
            this.cd.enclosingMethod(cName, mName, mDescriptor);
        }
    }

    private void doInnerClasses(DataInput in) throws Exception {
        int number_of_classes = in.readUnsignedShort();
        for (int i = 0; i < number_of_classes; ++i) {
            int nameIndex;
            int inner_class_info_index = in.readUnsignedShort();
            int outer_class_info_index = in.readUnsignedShort();
            int inner_name_index = in.readUnsignedShort();
            int inner_class_access_flags = in.readUnsignedShort();
            if (this.cd == null) continue;
            Descriptors.TypeRef innerClass = null;
            Descriptors.TypeRef outerClass = null;
            String innerName = null;
            if (inner_class_info_index != 0) {
                nameIndex = this.intPool[inner_class_info_index];
                innerClass = this.analyzer.getTypeRef((String)this.pool[nameIndex]);
            }
            if (outer_class_info_index != 0) {
                nameIndex = this.intPool[outer_class_info_index];
                outerClass = this.analyzer.getTypeRef((String)this.pool[nameIndex]);
            }
            if (inner_name_index != 0) {
                innerName = (String)this.pool[inner_name_index];
            }
            this.cd.innerClass(innerClass, outerClass, innerName, inner_class_access_flags);
        }
    }

    void doSignature(DataInput in, ElementType member, int access_flags) throws IOException {
        int signature_index = in.readUnsignedShort();
        String signature = (String)this.pool[signature_index];
        try {
            this.parseDescriptor(signature, access_flags);
            if (this.last != null) {
                this.last.signature = signature;
            }
            if (this.cd != null) {
                this.cd.signature(signature);
            }
            if (member == ElementType.TYPE) {
                this.classSignature = signature;
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Signature failed for " + signature, e);
        }
    }

    void doConstantValue(DataInput in) throws IOException {
        int constantValue_index = in.readUnsignedShort();
        if (this.cd == null) {
            return;
        }
        Object object = this.pool[constantValue_index];
        if (object == null) {
            object = this.pool[this.intPool[constantValue_index]];
        }
        this.last.constant = object;
        this.cd.constant(object);
    }

    void doExceptions(DataInput in, int access_flags) throws IOException {
        int exception_count = in.readUnsignedShort();
        for (int i = 0; i < exception_count; ++i) {
            int index = in.readUnsignedShort();
            ClassConstant cc = (ClassConstant)this.pool[index];
            Descriptors.TypeRef clazz = this.analyzer.getTypeRef(cc.getName());
            this.referTo(clazz, access_flags);
        }
    }

    private void doCode(DataInput in, boolean crawl) throws Exception {
        in.readUnsignedShort();
        in.readUnsignedShort();
        int code_length = in.readInt();
        byte[] code = new byte[code_length];
        in.readFully(code);
        if (crawl) {
            this.crawl(code);
        }
        int exception_table_length = in.readUnsignedShort();
        for (int i = 0; i < exception_table_length; ++i) {
            int start_pc = in.readUnsignedShort();
            int end_pc = in.readUnsignedShort();
            int handler_pc = in.readUnsignedShort();
            int catch_type = in.readUnsignedShort();
            this.classConstRef(catch_type);
        }
        this.doAttributes(in, ElementType.METHOD, false, 0);
    }

    private void crawl(byte[] code) {
        ByteBuffer bb = ByteBuffer.wrap(code);
        bb.order(ByteOrder.BIG_ENDIAN);
        int lastReference = -1;
        block15: while (bb.remaining() > 0) {
            int instruction = 0xFF & bb.get();
            switch (instruction) {
                case 18: {
                    lastReference = 0xFF & bb.get();
                    this.classConstRef(lastReference);
                    continue block15;
                }
                case 19: {
                    lastReference = 0xFFFF & bb.getShort();
                    this.classConstRef(lastReference);
                    continue block15;
                }
                case 187: 
                case 189: 
                case 192: 
                case 193: {
                    int cref = 0xFFFF & bb.getShort();
                    this.classConstRef(cref);
                    lastReference = -1;
                    continue block15;
                }
                case 197: {
                    int cref = 0xFFFF & bb.getShort();
                    this.classConstRef(cref);
                    bb.get();
                    lastReference = -1;
                    continue block15;
                }
                case 183: {
                    int mref = 0xFFFF & bb.getShort();
                    if (this.cd == null) continue block15;
                    this.getMethodDef(0, mref);
                    continue block15;
                }
                case 182: {
                    int mref = 0xFFFF & bb.getShort();
                    if (this.cd == null) continue block15;
                    this.getMethodDef(0, mref);
                    continue block15;
                }
                case 185: {
                    int mref = 0xFFFF & bb.getShort();
                    if (this.cd != null) {
                        this.getMethodDef(0, mref);
                    }
                    bb.get();
                    bb.get();
                    continue block15;
                }
                case 184: {
                    int methodref = 0xFFFF & bb.getShort();
                    if (this.cd != null) {
                        this.getMethodDef(0, methodref);
                    }
                    if (methodref != this.forName && methodref != this.class$ || lastReference == -1 || !(this.pool[this.intPool[lastReference]] instanceof String)) continue block15;
                    String fqn = (String)this.pool[this.intPool[lastReference]];
                    if (!fqn.equals("class") && fqn.indexOf(46) > 0) {
                        Descriptors.TypeRef clazz = this.analyzer.getTypeRefFromFQN(fqn);
                        this.referTo(clazz, 0);
                    }
                    lastReference = -1;
                    continue block15;
                }
                case 196: {
                    int opcode = 0xFF & bb.get();
                    bb.getShort();
                    if (opcode != 132) continue block15;
                    bb.getShort();
                    continue block15;
                }
                case 170: {
                    while ((bb.position() & 3) != 0) {
                        bb.get();
                    }
                    bb.getInt();
                    int low = bb.getInt();
                    int high = bb.getInt();
                    try {
                        bb.position(bb.position() + (high - low + 1) * 4);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    lastReference = -1;
                    continue block15;
                }
                case 171: {
                    while ((bb.position() & 3) != 0) {
                        byte n = bb.get();
                        assert (n == 0);
                    }
                    int deflt = bb.getInt();
                    int npairs = bb.getInt();
                    bb.position(bb.position() + npairs * 8);
                    lastReference = -1;
                    continue block15;
                }
            }
            lastReference = -1;
            bb.position(bb.position() + OpCodes.OFFSETS[instruction]);
        }
    }

    private void doSourceFile(DataInput in) throws IOException {
        int sourcefile_index = in.readUnsignedShort();
        this.sourceFile = this.pool[sourcefile_index].toString();
    }

    private void doParameterAnnotations(DataInput in, ElementType member, RetentionPolicy policy, int access_flags) throws Exception {
        int num_parameters = in.readUnsignedByte();
        for (int p = 0; p < num_parameters; ++p) {
            if (this.cd != null) {
                this.cd.parameter(p);
            }
            this.doAnnotations(in, member, policy, access_flags);
        }
    }

    private void doTypeAnnotations(DataInput in, ElementType member, RetentionPolicy policy, int access_flags) throws Exception {
        int num_annotations = in.readUnsignedShort();
        for (int p = 0; p < num_annotations; ++p) {
            int target_type = in.readUnsignedByte();
            switch (target_type) {
                case 0: 
                case 1: {
                    in.skipBytes(1);
                    break;
                }
                case 16: {
                    in.skipBytes(2);
                    break;
                }
                case 17: 
                case 18: {
                    in.skipBytes(2);
                    break;
                }
                case 19: 
                case 20: 
                case 21: {
                    break;
                }
                case 22: {
                    in.skipBytes(1);
                    break;
                }
                case 23: {
                    in.skipBytes(2);
                    break;
                }
                case 64: 
                case 65: {
                    int table_length = in.readUnsignedShort();
                    in.skipBytes(table_length * 6);
                    break;
                }
                case 66: {
                    in.skipBytes(2);
                    break;
                }
                case 67: 
                case 68: 
                case 69: 
                case 70: {
                    in.skipBytes(2);
                    break;
                }
                case 71: 
                case 72: 
                case 73: 
                case 74: 
                case 75: {
                    in.skipBytes(3);
                }
            }
            int path_length = in.readUnsignedByte();
            in.skipBytes(path_length * 2);
            this.doAnnotation(in, member, policy, false, access_flags);
        }
    }

    private void doAnnotations(DataInput in, ElementType member, RetentionPolicy policy, int access_flags) throws Exception {
        int num_annotations = in.readUnsignedShort();
        for (int a = 0; a < num_annotations; ++a) {
            if (this.cd == null) {
                this.doAnnotation(in, member, policy, false, access_flags);
                continue;
            }
            Annotation annotion = this.doAnnotation(in, member, policy, true, access_flags);
            this.cd.annotation(annotion);
        }
    }

    private Annotation doAnnotation(DataInput in, ElementType member, RetentionPolicy policy, boolean collect, int access_flags) throws IOException {
        int type_index = in.readUnsignedShort();
        if (this.annotations == null) {
            this.annotations = new HashSet<Descriptors.TypeRef>();
        }
        String typeName = (String)this.pool[type_index];
        Descriptors.TypeRef typeRef = null;
        if (typeName != null) {
            typeRef = this.analyzer.getTypeRef(typeName);
            this.annotations.add(typeRef);
            if (policy == RetentionPolicy.RUNTIME) {
                this.referTo(type_index, 0);
                this.hasRuntimeAnnotations = true;
                if (this.api != null && (Modifier.isPublic(access_flags) || Modifier.isProtected(access_flags))) {
                    this.api.add(typeRef.getPackageRef());
                }
            } else {
                this.hasClassAnnotations = true;
            }
        }
        int num_element_value_pairs = in.readUnsignedShort();
        LinkedHashMap<String, Object> elements = null;
        for (int v = 0; v < num_element_value_pairs; ++v) {
            int element_name_index = in.readUnsignedShort();
            String element = (String)this.pool[element_name_index];
            Object value = this.doElementValue(in, member, policy, collect, access_flags);
            if (!collect) continue;
            if (elements == null) {
                elements = new LinkedHashMap<String, Object>();
            }
            elements.put(element, value);
        }
        if (collect) {
            return new Annotation(typeRef, elements, member, policy);
        }
        return null;
    }

    private Object doElementValue(DataInput in, ElementType member, RetentionPolicy policy, boolean collect, int access_flags) throws IOException {
        char tag = (char)in.readUnsignedByte();
        switch (tag) {
            case 'B': 
            case 'C': 
            case 'I': 
            case 'S': {
                int const_value_index = in.readUnsignedShort();
                return this.intPool[const_value_index];
            }
            case 'D': 
            case 'F': 
            case 'J': 
            case 's': {
                int const_value_index = in.readUnsignedShort();
                return this.pool[const_value_index];
            }
            case 'Z': {
                int const_value_index = in.readUnsignedShort();
                return this.pool[const_value_index] != null && !this.pool[const_value_index].equals(0);
            }
            case 'e': {
                int type_name_index = in.readUnsignedShort();
                if (policy == RetentionPolicy.RUNTIME) {
                    this.referTo(type_name_index, 0);
                    if (this.api != null && (Modifier.isPublic(access_flags) || Modifier.isProtected(access_flags))) {
                        Descriptors.TypeRef name = this.analyzer.getTypeRef((String)this.pool[type_name_index]);
                        this.api.add(name.getPackageRef());
                    }
                }
                int const_name_index = in.readUnsignedShort();
                return this.pool[const_name_index];
            }
            case 'c': {
                int class_info_index = in.readUnsignedShort();
                Descriptors.TypeRef name = this.analyzer.getTypeRef((String)this.pool[class_info_index]);
                if (policy == RetentionPolicy.RUNTIME) {
                    this.referTo(class_info_index, 0);
                    if (this.api != null && (Modifier.isPublic(access_flags) || Modifier.isProtected(access_flags))) {
                        this.api.add(name.getPackageRef());
                    }
                }
                return name;
            }
            case '@': {
                return this.doAnnotation(in, member, policy, collect, access_flags);
            }
            case '[': {
                int num_values = in.readUnsignedShort();
                Object[] result = new Object[num_values];
                for (int i = 0; i < num_values; ++i) {
                    result[i] = this.doElementValue(in, member, policy, collect, access_flags);
                }
                return result;
            }
        }
        throw new IllegalArgumentException("Invalid value for Annotation ElementValue tag " + tag);
    }

    private void doBootstrapMethods(DataInput in) throws IOException {
        int num_bootstrap_methods = in.readUnsignedShort();
        for (int v = 0; v < num_bootstrap_methods; ++v) {
            int bootstrap_method_ref = in.readUnsignedShort();
            int num_bootstrap_arguments = in.readUnsignedShort();
            for (int a = 0; a < num_bootstrap_arguments; ++a) {
                int bootstrap_argument = in.readUnsignedShort();
            }
        }
    }

    private void doStackMapTable(DataInput in) throws IOException {
        int number_of_entries = in.readUnsignedShort();
        for (int v = 0; v < number_of_entries; ++v) {
            int n;
            int number_of_locals;
            int offset_delta;
            int frame_type = in.readUnsignedByte();
            if (frame_type <= 63) continue;
            if (frame_type <= 127) {
                this.verification_type_info(in);
                continue;
            }
            if (frame_type <= 246) continue;
            if (frame_type <= 247) {
                offset_delta = in.readUnsignedShort();
                this.verification_type_info(in);
                continue;
            }
            if (frame_type <= 250) {
                offset_delta = in.readUnsignedShort();
                continue;
            }
            if (frame_type <= 251) {
                offset_delta = in.readUnsignedShort();
                continue;
            }
            if (frame_type <= 254) {
                offset_delta = in.readUnsignedShort();
                number_of_locals = frame_type - 251;
                for (n = 0; n < number_of_locals; ++n) {
                    this.verification_type_info(in);
                }
                continue;
            }
            if (frame_type > 255) continue;
            offset_delta = in.readUnsignedShort();
            number_of_locals = in.readUnsignedShort();
            for (n = 0; n < number_of_locals; ++n) {
                this.verification_type_info(in);
            }
            int number_of_stack_items = in.readUnsignedShort();
            for (int n2 = 0; n2 < number_of_stack_items; ++n2) {
                this.verification_type_info(in);
            }
        }
    }

    private void verification_type_info(DataInput in) throws IOException {
        int tag = in.readUnsignedByte();
        switch (tag) {
            case 7: {
                int cpool_index = in.readUnsignedShort();
                this.classConstRef(cpool_index);
                break;
            }
            case 8: {
                int n = in.readUnsignedShort();
            }
        }
    }

    void referTo(Descriptors.TypeRef typeRef, int modifiers) {
        if (this.xref != null) {
            this.xref.add(typeRef);
        }
        if (typeRef.isPrimitive()) {
            return;
        }
        Descriptors.PackageRef packageRef = typeRef.getPackageRef();
        if (packageRef.isPrimitivePackage()) {
            return;
        }
        this.imports.add(packageRef);
        if (this.api != null && (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers))) {
            this.api.add(packageRef);
        }
        if (this.cd != null) {
            this.cd.referTo(typeRef, modifiers);
        }
    }

    void referTo(int index, int modifiers) {
        String descriptor = (String)this.pool[index];
        this.parseDescriptor(descriptor, modifiers);
    }

    public void parseDescriptor(String descriptor, int modifiers) {
        int rover = 0;
        if (descriptor.charAt(0) == '<') {
            rover = this.parseFormalTypeParameters(descriptor, rover, modifiers);
        }
        if (descriptor.charAt(rover) == '(') {
            rover = this.parseReferences(descriptor, rover + 1, ')', modifiers);
            ++rover;
        }
        this.parseReferences(descriptor, rover, '\u0000', modifiers);
    }

    int parseReferences(String descriptor, int rover, char delimiter, int modifiers) {
        int r = rover;
        while (r < descriptor.length() && descriptor.charAt(r) != delimiter) {
            r = this.parseReference(descriptor, r, modifiers);
        }
        return r;
    }

    int parseReference(String descriptor, int rover, int modifiers) {
        int r = rover;
        char c = descriptor.charAt(r);
        while (c == '[') {
            c = descriptor.charAt(++r);
        }
        if (c == '<') {
            r = this.parseReferences(descriptor, r + 1, '>', modifiers);
        } else if (c == 'T') {
            ++r;
            while (descriptor.charAt(r) != ';') {
                ++r;
            }
        } else if (c == 'L') {
            StringBuilder sb = new StringBuilder();
            ++r;
            while ((c = descriptor.charAt(r)) != ';') {
                if (c == '<') {
                    r = this.parseReferences(descriptor, r + 1, '>', modifiers);
                } else {
                    sb.append(c);
                }
                ++r;
            }
            Descriptors.TypeRef ref = this.analyzer.getTypeRef(sb.toString());
            if (this.cd != null) {
                this.cd.addReference(ref);
            }
            this.referTo(ref, modifiers);
        } else if ("+-*BCDFIJSZV".indexOf(c) < 0) {
            // empty if block
        }
        return r + 1;
    }

    private int parseFormalTypeParameters(String descriptor, int index, int modifiers) {
        ++index;
        while (descriptor.charAt(index) != '>') {
            if ((index = descriptor.indexOf(58, index) + 1) == 0) {
                throw new IllegalArgumentException("Expected ClassBound or InterfaceBounds: " + descriptor);
            }
            char c = descriptor.charAt(index);
            if (c != ':') {
                index = this.parseReference(descriptor, index, modifiers);
                c = descriptor.charAt(index);
            }
            while (c == ':') {
                ++index;
                index = this.parseReference(descriptor, index, modifiers);
                c = descriptor.charAt(index);
            }
        }
        return index + 1;
    }

    public Set<Descriptors.PackageRef> getReferred() {
        return this.imports;
    }

    public String getAbsolutePath() {
        return this.path;
    }

    public String getSourceFile() {
        return this.sourceFile;
    }

    public void reset() {
        if (--this.depth == 0) {
            this.pool = null;
            this.intPool = null;
            this.xref = null;
        }
    }

    public boolean is(QUERY query, Instruction instr, Analyzer analyzer) throws Exception {
        switch (query) {
            case ANY: {
                return true;
            }
            case NAMED: {
                if (instr.matches(this.getClassName().getDottedOnly())) {
                    return !instr.isNegated();
                }
                return false;
            }
            case VERSION: {
                String v = this.major + "." + this.minor;
                if (instr.matches(v)) {
                    return !instr.isNegated();
                }
                return false;
            }
            case IMPLEMENTS: {
                for (int i = 0; this.interfaces != null && i < this.interfaces.length; ++i) {
                    if (!instr.matches(this.interfaces[i].getDottedOnly())) continue;
                    return !instr.isNegated();
                }
                break;
            }
            case EXTENDS: {
                if (this.zuper == null) {
                    return false;
                }
                if (!instr.matches(this.zuper.getDottedOnly())) break;
                return !instr.isNegated();
            }
            case PUBLIC: {
                return Modifier.isPublic(this.accessx);
            }
            case CONCRETE: {
                return !Modifier.isAbstract(this.accessx);
            }
            case ANNOTATED: {
                if (this.annotations == null) {
                    return false;
                }
                for (Descriptors.TypeRef annotation : this.annotations) {
                    if (!instr.matches(annotation.getFQN())) continue;
                    return !instr.isNegated();
                }
                return false;
            }
            case RUNTIMEANNOTATIONS: {
                return this.hasRuntimeAnnotations;
            }
            case CLASSANNOTATIONS: {
                return this.hasClassAnnotations;
            }
            case ABSTRACT: {
                return Modifier.isAbstract(this.accessx);
            }
            case IMPORTS: {
                for (Descriptors.PackageRef imp : this.imports) {
                    if (!instr.matches(imp.getFQN())) continue;
                    return !instr.isNegated();
                }
                break;
            }
            case DEFAULT_CONSTRUCTOR: {
                return this.hasPublicNoArgsConstructor();
            }
        }
        if (this.zuper == null) {
            return false;
        }
        Clazz clazz = analyzer.findClass(this.zuper);
        if (clazz == null) {
            analyzer.warning("While traversing the type tree while searching %s on %s cannot find class %s", new Object[]{query, this, this.zuper});
            return false;
        }
        return clazz.is(query, instr, analyzer);
    }

    public String toString() {
        if (this.className != null) {
            return this.className.getFQN();
        }
        return this.resource.toString();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    void getMethodDef(int access, int methodRefPoolIndex) {
        if (methodRefPoolIndex == 0) {
            return;
        }
        Object o = this.pool[methodRefPoolIndex];
        if (!(o instanceof Assoc)) throw new IllegalArgumentException("Invalid class file (or parsing is wrong), Not an assoc at a method ref");
        Assoc assoc = (Assoc)o;
        if (assoc.tag != CONSTANT.Methodref) throw new IllegalArgumentException("Invalid class file (or parsing is wrong), Assoc is not method ref! (10)");
        int string_index = this.intPool[assoc.a];
        Descriptors.TypeRef className = this.analyzer.getTypeRef((String)this.pool[string_index]);
        int name_and_type_index = assoc.b;
        Assoc name_and_type = (Assoc)this.pool[name_and_type_index];
        if (name_and_type.tag != CONSTANT.NameAndType) {
            throw new IllegalArgumentException("Invalid class file (or parsing is wrong), assoc is not type + name (12)");
        }
        int name_index = name_and_type.a;
        int type_index = name_and_type.b;
        String method = (String)this.pool[name_index];
        String descriptor = (String)this.pool[type_index];
        this.cd.referenceMethod(access, className, method, descriptor);
    }

    public boolean isPublic() {
        return Modifier.isPublic(this.accessx);
    }

    public boolean isProtected() {
        return Modifier.isProtected(this.accessx);
    }

    public boolean isEnum() {
        return this.zuper != null && this.zuper.getBinary().equals("java/lang/Enum");
    }

    public boolean isSynthetic() {
        return (0x1000 & this.accessx) != 0;
    }

    public boolean isModule() {
        return (0x8000 & this.accessx) != 0;
    }

    public JAVA getFormat() {
        return JAVA.format(this.major);
    }

    public static String objectDescriptorToFQN(String string) {
        if ((string.startsWith("L") || string.startsWith("T")) && string.endsWith(";")) {
            return string.substring(1, string.length() - 1).replace('/', '.');
        }
        switch (string.charAt(0)) {
            case 'V': {
                return "void";
            }
            case 'B': {
                return "byte";
            }
            case 'C': {
                return "char";
            }
            case 'I': {
                return "int";
            }
            case 'S': {
                return "short";
            }
            case 'D': {
                return "double";
            }
            case 'F': {
                return "float";
            }
            case 'J': {
                return "long";
            }
            case 'Z': {
                return "boolean";
            }
            case '[': {
                return Clazz.objectDescriptorToFQN(string.substring(1)) + "[]";
            }
        }
        throw new IllegalArgumentException("Invalid type character in descriptor " + string);
    }

    public static String unCamel(String id) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < id.length(); ++i) {
            boolean tolower;
            int n;
            char c = id.charAt(i);
            if (c == '_' || c == '$' || c == '-' || c == '.') {
                if (out.length() <= 0 || Character.isWhitespace(out.charAt(out.length() - 1))) continue;
                out.append(' ');
                continue;
            }
            for (n = i; n < id.length() && Character.isUpperCase(id.charAt(n)); ++n) {
            }
            if (n == i) {
                out.append(id.charAt(i));
                continue;
            }
            boolean bl = tolower = n - i == 1;
            if (i > 0 && !Character.isWhitespace(out.charAt(out.length() - 1))) {
                out.append(' ');
            }
            while (i < n) {
                if (tolower) {
                    out.append(Character.toLowerCase(id.charAt(i)));
                } else {
                    out.append(id.charAt(i));
                }
                ++i;
            }
            --i;
        }
        if (id.startsWith(".")) {
            out.append(" *");
        }
        out.replace(0, 1, Character.toUpperCase(out.charAt(0)) + "");
        return out.toString();
    }

    public boolean isInterface() {
        return Modifier.isInterface(this.accessx);
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(this.accessx);
    }

    public boolean hasPublicNoArgsConstructor() {
        return this.hasDefaultConstructor;
    }

    public int getAccess() {
        if (this.innerAccess == -1) {
            return this.accessx;
        }
        return this.innerAccess;
    }

    public Descriptors.TypeRef getClassName() {
        return this.className;
    }

    public MethodDef getMethodDef(int access, String name, String descriptor) {
        return new MethodDef(access, name, descriptor);
    }

    public Descriptors.TypeRef getSuper() {
        return this.zuper;
    }

    public String getFQN() {
        return this.className.getFQN();
    }

    public Descriptors.TypeRef[] getInterfaces() {
        return this.interfaces;
    }

    public void setInnerAccess(int access) {
        this.innerAccess = access;
    }

    public boolean isFinal() {
        return Modifier.isFinal(this.accessx);
    }

    public void setDeprecated(boolean b) {
        this.deprecated = b;
    }

    public boolean isDeprecated() {
        return this.deprecated;
    }

    public boolean isAnnotation() {
        return (this.accessx & 0x2000) != 0;
    }

    public Set<Descriptors.PackageRef> getAPIUses() {
        if (this.api == null) {
            return Collections.emptySet();
        }
        return this.api;
    }

    public TypeDef getExtends(Descriptors.TypeRef type) {
        return new TypeDef(type, false);
    }

    public TypeDef getImplements(Descriptors.TypeRef type) {
        return new TypeDef(type, true);
    }

    private void classConstRef(int lastReference) {
        Object o = this.pool[lastReference];
        if (o == null) {
            return;
        }
        if (o instanceof ClassConstant) {
            ClassConstant cc = (ClassConstant)o;
            if (cc.referred) {
                return;
            }
            cc.referred = true;
            String name = cc.getName();
            if (name != null) {
                Descriptors.TypeRef tr = this.analyzer.getTypeRef(name);
                this.referTo(tr, 0);
            }
        }
    }

    public String getClassSignature() {
        return this.classSignature;
    }

    public Map<String, Object> getDefaults() throws Exception {
        if (this.defaults == null) {
            this.defaults = new HashMap<String, Object>();
            class DefaultReader
            extends ClassDataCollector {
                DefaultReader() {
                }

                @Override
                public void annotationDefault(MethodDef last, Object value) {
                    Clazz.this.defaults.put(last.name, value);
                }
            }
            this.parseClassFileWithCollector(new DefaultReader());
        }
        return this.defaults;
    }

    public class TypeDef
    extends Def {
        Descriptors.TypeRef type;
        boolean interf;

        public TypeDef(Descriptors.TypeRef type, boolean interf) {
            super(1);
            this.type = type;
            this.interf = interf;
        }

        public Descriptors.TypeRef getReference() {
            return this.type;
        }

        public boolean getImplements() {
            return this.interf;
        }

        @Override
        public String getName() {
            if (this.interf) {
                return "<implements>";
            }
            return "<extends>";
        }

        @Override
        public Descriptors.TypeRef getType() {
            return this.type;
        }

        @Override
        public Descriptors.TypeRef[] getPrototype() {
            return null;
        }
    }

    public class MethodDef
    extends FieldDef {
        public MethodDef(int access, String method, String descriptor) {
            super(access, method, descriptor);
        }

        public boolean isConstructor() {
            return this.name.equals("<init>") || this.name.equals("<clinit>");
        }

        @Override
        public Descriptors.TypeRef[] getPrototype() {
            return this.descriptor.getPrototype();
        }

        public boolean isBridge() {
            return (this.access & 0x40) != 0;
        }
    }

    public class FieldDef
    extends Def {
        final String name;
        final Descriptors.Descriptor descriptor;
        String signature;
        Object constant;
        boolean deprecated;

        public boolean isDeprecated() {
            return this.deprecated;
        }

        public void setDeprecated(boolean deprecated) {
            this.deprecated = deprecated;
        }

        public FieldDef(int access, String name, String descriptor) {
            super(access);
            this.name = name;
            this.descriptor = Clazz.this.analyzer.getDescriptor(descriptor);
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public Descriptors.TypeRef getType() {
            return this.descriptor.getType();
        }

        public Descriptors.TypeRef getContainingClass() {
            return Clazz.this.getClassName();
        }

        public Descriptors.Descriptor getDescriptor() {
            return this.descriptor;
        }

        public void setConstant(Object o) {
            this.constant = o;
        }

        public Object getConstant() {
            return this.constant;
        }

        public String getGenericReturnType() {
            Matcher m;
            String use = this.descriptor.toString();
            if (this.signature != null) {
                use = this.signature;
            }
            if (!(m = METHOD_DESCRIPTOR.matcher(use)).matches()) {
                throw new IllegalArgumentException("Not a valid method descriptor: " + use);
            }
            String returnType = m.group(2);
            return Clazz.objectDescriptorToFQN(returnType);
        }

        @Override
        public Descriptors.TypeRef[] getPrototype() {
            return null;
        }

        public String getSignature() {
            return this.signature;
        }

        public String toString() {
            return this.name;
        }
    }

    public abstract class Def {
        final int access;
        Set<Descriptors.TypeRef> annotations;

        public Def(int access) {
            this.access = access;
        }

        public int getAccess() {
            return this.access;
        }

        public boolean isEnum() {
            return (this.access & 0x4000) != 0;
        }

        public boolean isPublic() {
            return Modifier.isPublic(this.access);
        }

        public boolean isAbstract() {
            return Modifier.isAbstract(this.access);
        }

        public boolean isProtected() {
            return Modifier.isProtected(this.access);
        }

        public boolean isFinal() {
            return Modifier.isFinal(this.access) || Clazz.this.isFinal();
        }

        public boolean isStatic() {
            return Modifier.isStatic(this.access);
        }

        public boolean isPrivate() {
            return Modifier.isPrivate(this.access);
        }

        public boolean isNative() {
            return Modifier.isNative(this.access);
        }

        public boolean isTransient() {
            return Modifier.isTransient(this.access);
        }

        public boolean isVolatile() {
            return Modifier.isVolatile(this.access);
        }

        public boolean isInterface() {
            return Modifier.isInterface(this.access);
        }

        public boolean isSynthetic() {
            return (this.access & 0x1000) != 0;
        }

        void addAnnotation(Annotation a) {
            if (this.annotations == null) {
                this.annotations = Create.set();
            }
            this.annotations.add(Clazz.this.analyzer.getTypeRef(a.getName().getBinary()));
        }

        public Collection<Descriptors.TypeRef> getAnnotations() {
            return this.annotations;
        }

        public Descriptors.TypeRef getOwnerType() {
            return Clazz.this.className;
        }

        public abstract String getName();

        public abstract Descriptors.TypeRef getType();

        public abstract Descriptors.TypeRef[] getPrototype();

        public Object getClazz() {
            return Clazz.this;
        }
    }

    protected static class Assoc {
        CONSTANT tag;
        int a;
        int b;

        Assoc(CONSTANT tag, int a, int b) {
            this.tag = tag;
            this.a = a;
            this.b = b;
        }

        public String toString() {
            return "Assoc[" + (Object)((Object)this.tag) + ", " + this.a + "," + this.b + "]";
        }
    }

    static enum CONSTANT {
        Zero(0),
        Utf8,
        Two,
        Integer(4),
        Float(4),
        Long(8),
        Double(8),
        Class(2),
        String(2),
        Fieldref(4),
        Methodref(4),
        InterfaceMethodref(4),
        NameAndType(4),
        Thirteen,
        Fourteen,
        MethodHandle(3),
        MethodType(2),
        Seventeen,
        InvokeDynamic(4),
        Module(2),
        Package(2);

        private final int skip;

        private CONSTANT(int skip) {
            this.skip = skip;
        }

        private CONSTANT() {
            this.skip = -1;
        }

        public int skip() {
            return this.skip;
        }
    }

    public static enum QUERY {
        IMPLEMENTS,
        EXTENDS,
        IMPORTS,
        NAMED,
        ANY,
        VERSION,
        CONCRETE,
        ABSTRACT,
        PUBLIC,
        ANNOTATED,
        RUNTIMEANNOTATIONS,
        CLASSANNOTATIONS,
        DEFAULT_CONSTRUCTOR;

    }

    public static enum JAVA {
        JDK1_1(45, "JRE-1.1", "(&(osgi.ee=JavaSE)(version=1.1))"),
        JDK1_2(46, "J2SE-1.2", "(&(osgi.ee=JavaSE)(version=1.2))"),
        JDK1_3(47, "J2SE-1.3", "(&(osgi.ee=JavaSE)(version=1.3))"),
        JDK1_4(48, "J2SE-1.4", "(&(osgi.ee=JavaSE)(version=1.4))"),
        J2SE5(49, "J2SE-1.5", "(&(osgi.ee=JavaSE)(version=1.5))"),
        J2SE6(50, "JavaSE-1.6", "(&(osgi.ee=JavaSE)(version=1.6))"),
        OpenJDK7(51, "JavaSE-1.7", "(&(osgi.ee=JavaSE)(version=1.7))"),
        OpenJDK8(52, "JavaSE-1.8", "(&(osgi.ee=JavaSE)(version=1.8))"){
            Map<String, Set<String>> profiles;

            @Override
            public Map<String, Set<String>> getProfiles() throws IOException {
                if (this.profiles == null) {
                    UTF8Properties p = new UTF8Properties();
                    try (InputStream in = Clazz.class.getResourceAsStream("profiles-" + (Object)((Object)this) + ".properties");){
                        ((Properties)p).load(in);
                    }
                    this.profiles = new HashMap<String, Set<String>>();
                    for (Map.Entry<Object, Object> prop : p.entrySet()) {
                        String list = (String)prop.getValue();
                        HashSet<String> set = new HashSet<String>();
                        for (String s : list.split("\\s*,\\s*")) {
                            set.add(s);
                        }
                        this.profiles.put((String)prop.getKey(), set);
                    }
                }
                return this.profiles;
            }
        }
        ,
        OpenJDK9(53, "JavaSE-9", "(&(osgi.ee=JavaSE)(version=9.0))"),
        UNKNOWN(Integer.MAX_VALUE, "<UNKNOWN>", "(osgi.ee=UNKNOWN)");

        final int major;
        final String ee;
        final String filter;

        private JAVA(int major, String ee, String filter) {
            this.major = major;
            this.ee = ee;
            this.filter = filter;
        }

        static JAVA format(int n) {
            for (JAVA e : JAVA.values()) {
                if (e.major != n) continue;
                return e;
            }
            return UNKNOWN;
        }

        public int getMajor() {
            return this.major;
        }

        public boolean hasAnnotations() {
            return this.major >= JAVA.J2SE5.major;
        }

        public boolean hasGenerics() {
            return this.major >= JAVA.J2SE5.major;
        }

        public boolean hasEnums() {
            return this.major >= JAVA.J2SE5.major;
        }

        public static JAVA getJava(int major, int minor) {
            for (JAVA j : JAVA.values()) {
                if (j.major != major) continue;
                return j;
            }
            return UNKNOWN;
        }

        public String getEE() {
            return this.ee;
        }

        public String getFilter() {
            return this.filter;
        }

        public Map<String, Set<String>> getProfiles() throws IOException {
            return null;
        }
    }

    public class ClassConstant {
        int cname;
        public boolean referred;

        public ClassConstant(int class_index) {
            this.cname = class_index;
        }

        public String getName() {
            return (String)Clazz.this.pool[this.cname];
        }

        public String toString() {
            return "ClassConstant[" + this.getName() + "]";
        }
    }
}

