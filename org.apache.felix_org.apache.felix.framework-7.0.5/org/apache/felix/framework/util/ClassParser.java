/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.util;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClassParser {
    Map<String, TypeRef> typeRefCache = new HashMap<String, TypeRef>();
    Map<String, Descriptor> descriptorCache = new HashMap<String, Descriptor>();
    Map<String, PackageRef> packageCache = new HashMap<String, PackageRef>();
    static final PackageRef DEFAULT_PACKAGE = new PackageRef();
    static final PackageRef PRIMITIVE_PACKAGE = new PackageRef();
    static final TypeRef VOID = new ConcreteRef("V", "void", PRIMITIVE_PACKAGE);
    static final TypeRef BOOLEAN = new ConcreteRef("Z", "boolean", PRIMITIVE_PACKAGE);
    static final TypeRef BYTE = new ConcreteRef("B", "byte", PRIMITIVE_PACKAGE);
    static final TypeRef CHAR = new ConcreteRef("C", "char", PRIMITIVE_PACKAGE);
    static final TypeRef SHORT = new ConcreteRef("S", "short", PRIMITIVE_PACKAGE);
    static final TypeRef INTEGER = new ConcreteRef("I", "int", PRIMITIVE_PACKAGE);
    static final TypeRef LONG = new ConcreteRef("J", "long", PRIMITIVE_PACKAGE);
    static final TypeRef DOUBLE = new ConcreteRef("D", "double", PRIMITIVE_PACKAGE);
    static final TypeRef FLOAT = new ConcreteRef("F", "float", PRIMITIVE_PACKAGE);

    public ClassParser() {
        this.packageCache.put("", DEFAULT_PACKAGE);
    }

    private TypeRef getTypeRef(String binaryClassName) {
        TypeRef ref = this.typeRefCache.get(binaryClassName);
        if (ref != null) {
            return ref;
        }
        if (binaryClassName.startsWith("[")) {
            ref = this.getTypeRef(binaryClassName.substring(1));
            ref = new ArrayRef(ref);
        } else {
            if (binaryClassName.length() == 1) {
                switch (binaryClassName.charAt(0)) {
                    case 'V': {
                        return VOID;
                    }
                    case 'B': {
                        return BYTE;
                    }
                    case 'C': {
                        return CHAR;
                    }
                    case 'I': {
                        return INTEGER;
                    }
                    case 'S': {
                        return SHORT;
                    }
                    case 'D': {
                        return DOUBLE;
                    }
                    case 'F': {
                        return FLOAT;
                    }
                    case 'J': {
                        return LONG;
                    }
                    case 'Z': {
                        return BOOLEAN;
                    }
                }
            }
            if (binaryClassName.startsWith("L") && binaryClassName.endsWith(";")) {
                binaryClassName = binaryClassName.substring(1, binaryClassName.length() - 1);
            }
            if ((ref = this.typeRefCache.get(binaryClassName)) != null) {
                return ref;
            }
            int n = binaryClassName.lastIndexOf(47);
            PackageRef pref = n < 0 ? DEFAULT_PACKAGE : this.getPackageRef(binaryClassName.substring(0, n));
            ref = new ConcreteRef(pref, binaryClassName);
        }
        this.typeRefCache.put(binaryClassName, ref);
        return ref;
    }

    private PackageRef getPackageRef(String binaryPackName) {
        PackageRef ref;
        if (binaryPackName.indexOf(46) >= 0) {
            binaryPackName = binaryPackName.replace('.', '/');
        }
        if ((ref = this.packageCache.get(binaryPackName)) != null) {
            return ref;
        }
        ref = new PackageRef(binaryPackName);
        this.packageCache.put(binaryPackName, ref);
        return ref;
    }

    private Descriptor getDescriptor(String descriptor) {
        Descriptor d = this.descriptorCache.get(descriptor);
        if (d != null) {
            return d;
        }
        d = new Descriptor(descriptor);
        this.descriptorCache.put(descriptor, d);
        return d;
    }

    private static String binaryToFQN(String binary) {
        StringBuilder sb = new StringBuilder();
        int l = binary.length();
        for (int i = 0; i < l; ++i) {
            char c = binary.charAt(i);
            if (c == '/') {
                sb.append('.');
                continue;
            }
            sb.append(c);
        }
        String result = sb.toString();
        assert (result.length() > 0);
        return result;
    }

    private static String fqnToBinary(String binary) {
        return binary.replace('.', '/');
    }

    TypeRef getTypeRefFromFQN(String fqn) {
        if (fqn.equals("boolean")) {
            return BOOLEAN;
        }
        if (fqn.equals("byte")) {
            return BOOLEAN;
        }
        if (fqn.equals("char")) {
            return CHAR;
        }
        if (fqn.equals("short")) {
            return SHORT;
        }
        if (fqn.equals("int")) {
            return INTEGER;
        }
        if (fqn.equals("long")) {
            return LONG;
        }
        if (fqn.equals("float")) {
            return FLOAT;
        }
        if (fqn.equals("double")) {
            return DOUBLE;
        }
        return this.getTypeRef(ClassParser.fqnToBinary(fqn));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Set<String> parseClassFileUses(String path, InputStream in) throws Exception {
        try (DataInputStream din = new DataInputStream(in);){
            Set<String> set = new Clazz(this, path).parseClassFileData(din);
            return set;
        }
    }

    private static class Clazz {
        static final int ACC_MODULE = 32768;
        boolean hasDefaultConstructor;
        int depth = 0;
        TypeRef className;
        Object[] pool;
        int[] intPool;
        Set<String> imports = new HashSet<String>();
        String path;
        int minor = 0;
        int major = 0;
        int accessx = 0;
        int forName = 0;
        int class$ = 0;
        TypeRef[] interfaces;
        TypeRef zuper;
        FieldDef last = null;
        final ClassParser classParser;
        String classSignature;
        private boolean detectLdc;
        static final short bipush = 16;
        static final short sipush = 17;
        static final short ldc = 18;
        static final short ldc_w = 19;
        static final short ldc2_w = 20;
        static final short iload = 21;
        static final short lload = 22;
        static final short fload = 23;
        static final short dload = 24;
        static final short aload = 25;
        static final short istore = 54;
        static final short lstore = 55;
        static final short fstore = 56;
        static final short dstore = 57;
        static final short iinc = 132;
        static final short ifeq = 153;
        static final short astore = 58;
        static final short ifne = 154;
        static final short iflt = 155;
        static final short ifge = 156;
        static final short ifgt = 157;
        static final short ifle = 158;
        static final short if_icmpeq = 159;
        static final short if_icmpne = 160;
        static final short if_icmplt = 161;
        static final short if_icmpge = 162;
        static final short if_icmpgt = 163;
        static final short if_icmple = 164;
        static final short if_acmpeq = 165;
        static final short if_acmpne = 166;
        static final short goto_ = 167;
        static final short jsr = 168;
        static final short ret = 169;
        static final short tableswitch = 170;
        static final short lookupswitch = 171;
        static final short getstatic = 178;
        static final short putstatic = 179;
        static final short getfield = 180;
        static final short putfield = 181;
        static final short invokevirtual = 182;
        static final short invokespecial = 183;
        static final short invokestatic = 184;
        static final short invokeinterface = 185;
        static final short invokedynamic = 186;
        static final short new_ = 187;
        static final short newarray = 188;
        static final short anewarray = 189;
        static final short checkcast = 192;
        static final short instanceof_ = 193;
        static final short wide = 196;
        static final short multianewarray = 197;
        static final short ifnull = 198;
        static final short ifnonnull = 199;
        static final short goto_w = 200;
        static final short jsr_w = 201;
        static final byte[] OFFSETS = new byte[256];

        public Clazz(ClassParser classParser, String path) {
            this.path = path;
            this.classParser = classParser;
        }

        Set<String> parseClassFileData(DataInput in) throws Exception {
            int interfacesCount;
            int super_class;
            String superName;
            ++this.depth;
            boolean crawl = false;
            int magic = in.readInt();
            if (magic != -889275714) {
                throw new IOException("Not a valid class file (no CAFEBABE header)");
            }
            this.minor = in.readUnsignedShort();
            this.major = in.readUnsignedShort();
            int count = in.readUnsignedShort();
            this.pool = new Object[count];
            this.intPool = new int[count];
            CONSTANT[] tags = CONSTANT.values();
            block19: for (int poolIndex = 1; poolIndex < count; ++poolIndex) {
                int tagValue = in.readUnsignedByte();
                if (tagValue >= tags.length) {
                    throw new IOException("Unrecognized constant pool tag value " + tagValue);
                }
                CONSTANT tag = tags[tagValue];
                switch (tag) {
                    case Zero: {
                        break block19;
                    }
                    case Utf8: {
                        this.constantUtf8(in, poolIndex);
                        continue block19;
                    }
                    case Integer: {
                        this.constantInteger(in, poolIndex);
                        continue block19;
                    }
                    case Float: {
                        this.constantFloat(in, poolIndex);
                        continue block19;
                    }
                    case Long: {
                        this.constantLong(in, poolIndex);
                        ++poolIndex;
                        continue block19;
                    }
                    case Double: {
                        this.constantDouble(in, poolIndex);
                        ++poolIndex;
                        continue block19;
                    }
                    case Class: {
                        this.constantClass(in, poolIndex);
                        continue block19;
                    }
                    case String: {
                        this.constantString(in, poolIndex);
                        continue block19;
                    }
                    case Fieldref: 
                    case Methodref: 
                    case InterfaceMethodref: {
                        this.ref(in, poolIndex);
                        continue block19;
                    }
                    case NameAndType: {
                        this.nameAndType(in, poolIndex, tag);
                        continue block19;
                    }
                    case MethodHandle: {
                        this.methodHandle(in, poolIndex, tag);
                        continue block19;
                    }
                    case MethodType: {
                        this.methodType(in, poolIndex, tag);
                        continue block19;
                    }
                    case InvokeDynamic: {
                        this.invokeDynamic(in, poolIndex, tag);
                        continue block19;
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
            block20: for (Object o : this.pool) {
                if (o == null || !(o instanceof Assoc)) continue;
                Assoc assoc = (Assoc)o;
                switch (assoc.tag) {
                    case Fieldref: 
                    case Methodref: 
                    case InterfaceMethodref: {
                        this.classConstRef(assoc.a);
                        continue block20;
                    }
                    case NameAndType: 
                    case MethodType: {
                        this.referTo(assoc.b, 0);
                        continue block20;
                    }
                }
            }
            for (Object o : this.pool) {
                if (!(o instanceof ClassConstant)) continue;
                ClassConstant cc = (ClassConstant)o;
                if (cc.referred) continue;
                this.detectLdc = true;
            }
            this.accessx = in.readUnsignedShort();
            int this_class = in.readUnsignedShort();
            this.className = this.classParser.getTypeRef((String)this.pool[this.intPool[this_class]]);
            if (!this.isModule()) {
                this.referTo(this.className, 1);
            }
            if ((superName = (String)this.pool[this.intPool[super_class = in.readUnsignedShort()]]) != null) {
                this.zuper = this.classParser.getTypeRef(superName);
            }
            if (this.zuper != null) {
                this.referTo(this.zuper, this.accessx);
            }
            if ((interfacesCount = in.readUnsignedShort()) > 0) {
                this.interfaces = new TypeRef[interfacesCount];
                for (int i = 0; i < interfacesCount; ++i) {
                    this.interfaces[i] = this.classParser.getTypeRef((String)this.pool[this.intPool[in.readUnsignedShort()]]);
                    this.referTo(this.interfaces[i], this.accessx);
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
                Object mdef = null;
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
            this.last = null;
            this.doAttributes(in, ElementType.TYPE, false, this.accessx);
            this.reset();
            return this.imports;
        }

        private void constantFloat(DataInput in, int poolIndex) throws IOException {
            in.skipBytes(4);
        }

        private void constantInteger(DataInput in, int poolIndex) throws IOException {
            this.intPool[poolIndex] = in.readInt();
            this.pool[poolIndex] = this.intPool[poolIndex];
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
            in.skipBytes(8);
        }

        private void constantLong(DataInput in, int poolIndex) throws IOException {
            in.skipBytes(8);
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

        private static long getUnsignedInt(int x) {
            return (long)x & 0xFFFFFFFFL;
        }

        private static int getUnsingedByte(byte b) {
            return b & 0xFF;
        }

        private static int getUnsingedShort(short s) {
            return s & 0xFFFF;
        }

        private void doAttribute(DataInput in, ElementType member, boolean crawl, int access_flags) throws Exception {
            int attribute_name_index = in.readUnsignedShort();
            String attributeName = (String)this.pool[attribute_name_index];
            long attribute_length = Clazz.getUnsignedInt(in.readInt());
            if (!attributeName.equals("Deprecated")) {
                if (attributeName.equals("RuntimeVisibleAnnotations")) {
                    this.doAnnotations(in, member, RetentionPolicy.RUNTIME, access_flags);
                } else if (attributeName.equals("RuntimeInvisibleAnnotations")) {
                    this.doAnnotations(in, member, RetentionPolicy.CLASS, access_flags);
                } else if (attributeName.equals("RuntimeVisibleParameterAnnotations")) {
                    this.doParameterAnnotations(in, member, RetentionPolicy.RUNTIME, access_flags);
                } else if (attributeName.equals("RuntimeInvisibleParameterAnnotations")) {
                    this.doParameterAnnotations(in, member, RetentionPolicy.CLASS, access_flags);
                } else if (attributeName.equals("RuntimeVisibleTypeAnnotations")) {
                    this.doTypeAnnotations(in, member, RetentionPolicy.RUNTIME, access_flags);
                } else if (attributeName.equals("RuntimeInvisibleTypeAnnotations")) {
                    this.doTypeAnnotations(in, member, RetentionPolicy.CLASS, access_flags);
                } else if (attributeName.equals("InnerClasses")) {
                    this.doInnerClasses(in);
                } else if (attributeName.equals("EnclosingMethod")) {
                    this.doEnclosingMethod(in);
                } else if (attributeName.equals("SourceFile")) {
                    this.doSourceFile(in);
                } else if (attributeName.equals("Code")) {
                    this.doCode(in, crawl);
                } else if (attributeName.equals("Signature")) {
                    this.doSignature(in, member, access_flags);
                } else if (attributeName.equals("ConstantValue")) {
                    this.doConstantValue(in);
                } else if (attributeName.equals("AnnotationDefault")) {
                    this.doElementValue(in, member, RetentionPolicy.RUNTIME, access_flags);
                } else if (attributeName.equals("Exceptions")) {
                    this.doExceptions(in, access_flags);
                } else if (attributeName.equals("BootstrapMethods")) {
                    this.doBootstrapMethods(in);
                } else if (attributeName.equals("StackMapTable")) {
                    this.doStackMapTable(in);
                } else {
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
        }

        private void doInnerClasses(DataInput in) throws Exception {
            int number_of_classes = in.readUnsignedShort();
            for (int i = 0; i < number_of_classes; ++i) {
                int inner_class_info_index = in.readUnsignedShort();
                int outer_class_info_index = in.readUnsignedShort();
                int inner_name_index = in.readUnsignedShort();
                int n = in.readUnsignedShort();
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
        }

        void doExceptions(DataInput in, int access_flags) throws IOException {
            int exception_count = in.readUnsignedShort();
            for (int i = 0; i < exception_count; ++i) {
                int index = in.readUnsignedShort();
                ClassConstant cc = (ClassConstant)this.pool[index];
                TypeRef clazz = this.classParser.getTypeRef(cc.getName());
                this.referTo(clazz, access_flags);
            }
        }

        private void doCode(DataInput in, boolean crawl) throws Exception {
            in.readUnsignedShort();
            in.readUnsignedShort();
            int code_length = in.readInt();
            byte[] code = new byte[code_length];
            in.readFully(code, 0, code_length);
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
            int lastReference = -1;
            block13: while (bb.remaining() > 0) {
                int instruction = Clazz.getUnsingedByte(bb.get());
                switch (instruction) {
                    case 18: {
                        lastReference = Clazz.getUnsingedByte(bb.get());
                        this.classConstRef(lastReference);
                        continue block13;
                    }
                    case 19: {
                        lastReference = Clazz.getUnsingedShort(bb.getShort());
                        this.classConstRef(lastReference);
                        continue block13;
                    }
                    case 187: 
                    case 189: 
                    case 192: 
                    case 193: {
                        int cref = Clazz.getUnsingedShort(bb.getShort());
                        this.classConstRef(cref);
                        lastReference = -1;
                        continue block13;
                    }
                    case 197: {
                        int cref = Clazz.getUnsingedShort(bb.getShort());
                        this.classConstRef(cref);
                        bb.get();
                        lastReference = -1;
                        continue block13;
                    }
                    case 183: {
                        int mref = Clazz.getUnsingedShort(bb.getShort());
                        continue block13;
                    }
                    case 182: {
                        int mref = Clazz.getUnsingedShort(bb.getShort());
                        continue block13;
                    }
                    case 185: {
                        int mref = Clazz.getUnsingedShort(bb.getShort());
                        bb.get();
                        bb.get();
                        continue block13;
                    }
                    case 184: {
                        int methodref = Clazz.getUnsingedShort(bb.getShort());
                        if (methodref != this.forName && methodref != this.class$ || lastReference == -1 || !(this.pool[this.intPool[lastReference]] instanceof String)) continue block13;
                        String fqn = (String)this.pool[this.intPool[lastReference]];
                        if (!fqn.equals("class") && fqn.indexOf(46) > 0) {
                            TypeRef clazz = this.classParser.getTypeRefFromFQN(fqn);
                            this.referTo(clazz, 0);
                        }
                        lastReference = -1;
                        continue block13;
                    }
                    case 196: {
                        int opcode = Clazz.getUnsingedByte(bb.get());
                        bb.getShort();
                        if (opcode != 132) continue block13;
                        bb.getShort();
                        continue block13;
                    }
                    case 170: {
                        while ((bb.position() & 3) != 0) {
                            bb.get();
                        }
                        bb.getInt();
                        int low = bb.getInt();
                        int high = bb.getInt();
                        bb.position(bb.position() + (high - low + 1) * 4);
                        lastReference = -1;
                        continue block13;
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
                        continue block13;
                    }
                }
                lastReference = -1;
                bb.position(bb.position() + OFFSETS[instruction]);
            }
        }

        private void doSourceFile(DataInput in) throws IOException {
            int sourcefile_index = in.readUnsignedShort();
        }

        private void doParameterAnnotations(DataInput in, ElementType member, RetentionPolicy policy, int access_flags) throws Exception {
            int num_parameters = in.readUnsignedByte();
            for (int p = 0; p < num_parameters; ++p) {
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
                this.doAnnotation(in, member, policy, access_flags);
            }
        }

        private void doAnnotations(DataInput in, ElementType member, RetentionPolicy policy, int access_flags) throws Exception {
            int num_annotations = in.readUnsignedShort();
            for (int a = 0; a < num_annotations; ++a) {
                this.doAnnotation(in, member, policy, access_flags);
            }
        }

        private void doAnnotation(DataInput in, ElementType member, RetentionPolicy policy, int access_flags) throws IOException {
            int type_index = in.readUnsignedShort();
            String typeName = (String)this.pool[type_index];
            if (typeName != null && policy == RetentionPolicy.RUNTIME) {
                this.referTo(type_index, 0);
            }
            int num_element_value_pairs = in.readUnsignedShort();
            for (int v = 0; v < num_element_value_pairs; ++v) {
                in.readUnsignedShort();
                this.doElementValue(in, member, policy, access_flags);
            }
        }

        private Object doElementValue(DataInput in, ElementType member, RetentionPolicy policy, int access_flags) throws IOException {
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
                    }
                    int const_name_index = in.readUnsignedShort();
                    return this.pool[const_name_index];
                }
                case 'c': {
                    int class_info_index = in.readUnsignedShort();
                    TypeRef name = this.classParser.getTypeRef((String)this.pool[class_info_index]);
                    if (policy == RetentionPolicy.RUNTIME) {
                        this.referTo(class_info_index, 0);
                    }
                    return name;
                }
                case '@': {
                    this.doAnnotation(in, member, policy, access_flags);
                }
                case '[': {
                    int num_values = in.readUnsignedShort();
                    Object[] result = new Object[num_values];
                    for (int i = 0; i < num_values; ++i) {
                        result[i] = this.doElementValue(in, member, policy, access_flags);
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
                    int n = in.readUnsignedShort();
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

        void referTo(TypeRef typeRef, int modifiers) {
            if (typeRef.isPrimitive()) {
                return;
            }
            PackageRef packageRef = typeRef.getPackageRef();
            if (packageRef.isPrimitivePackage()) {
                return;
            }
            this.imports.add(packageRef.getFQN());
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
                TypeRef ref = this.classParser.getTypeRef(sb.toString());
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

        public Set<String> getReferred() {
            return this.imports;
        }

        public void reset() {
            if (--this.depth == 0) {
                this.pool = null;
                this.intPool = null;
            }
        }

        public String toString() {
            if (this.className != null) {
                return this.className.getFQN();
            }
            return super.toString();
        }

        public boolean isModule() {
            return (0x8000 & this.accessx) != 0;
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
                    TypeRef tr = this.classParser.getTypeRef(name);
                    this.referTo(tr, 0);
                }
            }
        }

        static {
            Clazz.OFFSETS[16] = 1;
            Clazz.OFFSETS[17] = 2;
            Clazz.OFFSETS[18] = 1;
            Clazz.OFFSETS[19] = 2;
            Clazz.OFFSETS[20] = 2;
            Clazz.OFFSETS[21] = 1;
            Clazz.OFFSETS[22] = 1;
            Clazz.OFFSETS[23] = 1;
            Clazz.OFFSETS[24] = 1;
            Clazz.OFFSETS[25] = 1;
            Clazz.OFFSETS[54] = 1;
            Clazz.OFFSETS[55] = 1;
            Clazz.OFFSETS[56] = 1;
            Clazz.OFFSETS[57] = 1;
            Clazz.OFFSETS[132] = 2;
            Clazz.OFFSETS[153] = 2;
            Clazz.OFFSETS[58] = 1;
            Clazz.OFFSETS[154] = 2;
            Clazz.OFFSETS[155] = 2;
            Clazz.OFFSETS[156] = 2;
            Clazz.OFFSETS[157] = 2;
            Clazz.OFFSETS[158] = 2;
            Clazz.OFFSETS[159] = 2;
            Clazz.OFFSETS[160] = 2;
            Clazz.OFFSETS[161] = 2;
            Clazz.OFFSETS[162] = 2;
            Clazz.OFFSETS[163] = 2;
            Clazz.OFFSETS[164] = 2;
            Clazz.OFFSETS[165] = 2;
            Clazz.OFFSETS[166] = 2;
            Clazz.OFFSETS[167] = 2;
            Clazz.OFFSETS[168] = 2;
            Clazz.OFFSETS[169] = 1;
            Clazz.OFFSETS[170] = -1;
            Clazz.OFFSETS[171] = -1;
            Clazz.OFFSETS[178] = 2;
            Clazz.OFFSETS[179] = 2;
            Clazz.OFFSETS[180] = 2;
            Clazz.OFFSETS[181] = 2;
            Clazz.OFFSETS[182] = 2;
            Clazz.OFFSETS[183] = 2;
            Clazz.OFFSETS[184] = 2;
            Clazz.OFFSETS[185] = 2;
            Clazz.OFFSETS[186] = 4;
            Clazz.OFFSETS[187] = 2;
            Clazz.OFFSETS[188] = 1;
            Clazz.OFFSETS[189] = 2;
            Clazz.OFFSETS[192] = 2;
            Clazz.OFFSETS[193] = 2;
            Clazz.OFFSETS[196] = 3;
            Clazz.OFFSETS[197] = 3;
            Clazz.OFFSETS[198] = 2;
            Clazz.OFFSETS[199] = 2;
            Clazz.OFFSETS[200] = 4;
            Clazz.OFFSETS[201] = 4;
        }

        public class MethodDef
        extends FieldDef {
            public MethodDef(int access, String method, String descriptor) {
                super(access, method, descriptor);
            }
        }

        public class FieldDef
        extends Def {
            final String name;
            final Descriptor descriptor;
            String signature;
            Object constant;

            public FieldDef(int access, String name, String descriptor) {
                super(access);
                this.name = name;
                this.descriptor = Clazz.this.classParser.getDescriptor(descriptor);
            }

            public String toString() {
                return this.name;
            }
        }

        public abstract class Def {
            final int access;

            public Def(int access) {
                this.access = access;
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

            int skip() {
                return this.skip;
            }
        }

        class ClassConstant {
            int cname;
            boolean referred;

            ClassConstant(int class_index) {
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

    private class Descriptor {
        final TypeRef type;
        final TypeRef[] prototype;
        final String descriptor;

        Descriptor(String descriptor) {
            this.descriptor = descriptor;
            int index = 0;
            ArrayList<TypeRef> types = new ArrayList<TypeRef>();
            if (descriptor.charAt(index) == '(') {
                ++index;
                while (descriptor.charAt(index) != ')') {
                    index = this.parse(types, descriptor, index);
                }
                ++index;
                this.prototype = types.toArray(new TypeRef[0]);
                types.clear();
            } else {
                this.prototype = null;
            }
            index = this.parse(types, descriptor, index);
            this.type = (TypeRef)types.get(0);
        }

        int parse(List<TypeRef> types, String descriptor, int index) {
            char c;
            StringBuilder sb = new StringBuilder();
            while ((c = descriptor.charAt(index++)) == '[') {
                sb.append('[');
            }
            switch (c) {
                case 'L': {
                    while ((c = descriptor.charAt(index++)) != ';') {
                        sb.append(c);
                    }
                    break;
                }
                case 'B': 
                case 'C': 
                case 'D': 
                case 'F': 
                case 'I': 
                case 'J': 
                case 'S': 
                case 'V': 
                case 'Z': {
                    sb.append(c);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Invalid type in descriptor: " + c + " from " + descriptor + "[" + index + "]");
                }
            }
            types.add(ClassParser.this.getTypeRef(sb.toString()));
            return index;
        }

        public boolean equals(Object other) {
            if (other == null || other.getClass() != this.getClass()) {
                return false;
            }
            return Arrays.equals(this.prototype, ((Descriptor)other).prototype) && this.type == ((Descriptor)other).type;
        }

        public int hashCode() {
            int prime = 31;
            int result = 31 + this.type.hashCode();
            result = 31 * result + (this.prototype == null ? 0 : Arrays.hashCode(this.prototype));
            return result;
        }

        public String toString() {
            return this.descriptor;
        }
    }

    private static class ArrayRef
    implements TypeRef {
        final TypeRef component;

        ArrayRef(TypeRef component) {
            this.component = component;
        }

        @Override
        public String getBinary() {
            return "[" + this.component.getBinary();
        }

        @Override
        public String getFQN() {
            return this.component.getFQN() + "[]";
        }

        @Override
        public String getPath() {
            return this.component.getPath();
        }

        @Override
        public String getSourcePath() {
            return this.component.getSourcePath();
        }

        @Override
        public boolean isPrimitive() {
            return false;
        }

        @Override
        public TypeRef getClassRef() {
            return this.component.getClassRef();
        }

        public boolean equals(Object other) {
            if (other == null || other.getClass() != this.getClass()) {
                return false;
            }
            return this.component.equals(((ArrayRef)other).component);
        }

        @Override
        public PackageRef getPackageRef() {
            return this.component.getPackageRef();
        }

        @Override
        public String getShortName() {
            return this.component.getShortName() + "[]";
        }

        public String toString() {
            return this.component.toString() + "[]";
        }

        @Override
        public String getDottedOnly() {
            return this.component.getDottedOnly();
        }

        @Override
        public int compareTo(TypeRef other) {
            if (this == other) {
                return 0;
            }
            return this.getFQN().compareTo(other.getFQN());
        }

        public int hashCode() {
            return super.hashCode();
        }
    }

    private static class ConcreteRef
    implements TypeRef {
        final String binaryName;
        final String fqn;
        final boolean primitive;
        final PackageRef packageRef;

        ConcreteRef(PackageRef packageRef, String binaryName) {
            this.binaryName = binaryName;
            this.fqn = ClassParser.binaryToFQN(binaryName);
            this.primitive = false;
            this.packageRef = packageRef;
        }

        ConcreteRef(String binaryName, String fqn, PackageRef pref) {
            this.binaryName = binaryName;
            this.fqn = fqn;
            this.primitive = true;
            this.packageRef = pref;
        }

        @Override
        public String getBinary() {
            return this.binaryName;
        }

        @Override
        public String getPath() {
            return this.binaryName + ".class";
        }

        @Override
        public String getSourcePath() {
            return this.binaryName + ".java";
        }

        @Override
        public String getFQN() {
            return this.fqn;
        }

        @Override
        public String getDottedOnly() {
            return this.fqn.replace('$', '.');
        }

        @Override
        public boolean isPrimitive() {
            return this.primitive;
        }

        @Override
        public TypeRef getClassRef() {
            return this;
        }

        @Override
        public PackageRef getPackageRef() {
            return this.packageRef;
        }

        @Override
        public String getShortName() {
            int n = this.binaryName.lastIndexOf(47);
            return this.binaryName.substring(n + 1);
        }

        public String toString() {
            return this.fqn;
        }

        public boolean equals(Object other) {
            assert (other instanceof TypeRef);
            return this == other;
        }

        @Override
        public int compareTo(TypeRef other) {
            if (this == other) {
                return 0;
            }
            return this.fqn.compareTo(other.getFQN());
        }

        public int hashCode() {
            return super.hashCode();
        }
    }

    private static class PackageRef
    implements Comparable<PackageRef> {
        final String binaryName;
        final String fqn;

        PackageRef(String binaryName) {
            this.binaryName = ClassParser.fqnToBinary(binaryName);
            this.fqn = ClassParser.binaryToFQN(binaryName);
        }

        PackageRef() {
            this.binaryName = "";
            this.fqn = ".";
        }

        public String getFQN() {
            return this.fqn;
        }

        public String toString() {
            return this.fqn;
        }

        boolean isPrimitivePackage() {
            return this == PRIMITIVE_PACKAGE;
        }

        @Override
        public int compareTo(PackageRef other) {
            return this.fqn.compareTo(other.fqn);
        }

        public boolean equals(Object o) {
            assert (o instanceof PackageRef);
            return o == this;
        }

        public int hashCode() {
            return super.hashCode();
        }
    }

    private static interface TypeRef
    extends Comparable<TypeRef> {
        public String getBinary();

        public String getFQN();

        public String getPath();

        public boolean isPrimitive();

        public TypeRef getClassRef();

        public PackageRef getPackageRef();

        public String getShortName();

        public String getSourcePath();

        public String getDottedOnly();
    }
}

