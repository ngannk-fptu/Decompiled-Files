/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.stc;

import groovy.lang.GroovyRuntimeException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.tools.WideningCategories;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.runtime.EncodingGroovyMethods;
import org.codehaus.groovy.transform.stc.SignatureCodec;
import org.codehaus.groovy.transform.stc.UnionTypeClassNode;

public class SignatureCodecVersion1
implements SignatureCodec {
    private final ClassLoader classLoader;

    public SignatureCodecVersion1(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    private void doEncode(ClassNode node, DataOutputStream dos) throws IOException {
        dos.writeUTF(node.getClass().getSimpleName());
        if (node instanceof UnionTypeClassNode) {
            UnionTypeClassNode union = (UnionTypeClassNode)node;
            ClassNode[] delegates = union.getDelegates();
            dos.writeInt(delegates.length);
            for (ClassNode delegate : delegates) {
                this.doEncode(delegate, dos);
            }
            return;
        }
        if (node instanceof WideningCategories.LowestUpperBoundClassNode) {
            WideningCategories.LowestUpperBoundClassNode lub = (WideningCategories.LowestUpperBoundClassNode)node;
            dos.writeUTF(lub.getLubName());
            this.doEncode(lub.getUnresolvedSuperClass(), dos);
            ClassNode[] interfaces = lub.getInterfaces();
            if (interfaces == null) {
                dos.writeInt(-1);
            } else {
                dos.writeInt(interfaces.length);
                for (ClassNode anInterface : interfaces) {
                    this.doEncode(anInterface, dos);
                }
            }
            return;
        }
        if (node.isArray()) {
            dos.writeBoolean(true);
            this.doEncode(node.getComponentType(), dos);
        } else {
            dos.writeBoolean(false);
            dos.writeUTF(BytecodeHelper.getTypeDescription(node));
            dos.writeBoolean(node.isUsingGenerics());
            GenericsType[] genericsTypes = node.getGenericsTypes();
            if (genericsTypes == null) {
                dos.writeInt(-1);
            } else {
                dos.writeInt(genericsTypes.length);
                for (GenericsType type : genericsTypes) {
                    dos.writeBoolean(type.isPlaceholder());
                    dos.writeBoolean(type.isWildcard());
                    this.doEncode(type.getType(), dos);
                    ClassNode lb = type.getLowerBound();
                    if (lb == null) {
                        dos.writeBoolean(false);
                    } else {
                        dos.writeBoolean(true);
                        this.doEncode(lb, dos);
                    }
                    ClassNode[] upperBounds = type.getUpperBounds();
                    if (upperBounds == null) {
                        dos.writeInt(-1);
                        continue;
                    }
                    dos.writeInt(upperBounds.length);
                    for (ClassNode bound : upperBounds) {
                        this.doEncode(bound, dos);
                    }
                }
            }
        }
    }

    @Override
    public String encode(ClassNode node) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(128);
        DataOutputStream dos = new DataOutputStream(baos);
        StringWriter wrt = new StringWriter();
        String encoded = null;
        try {
            this.doEncode(node, dos);
            EncodingGroovyMethods.encodeBase64(baos.toByteArray()).writeTo(wrt);
            encoded = wrt.toString();
        }
        catch (IOException e) {
            throw new GroovyRuntimeException("Unable to serialize type information", e);
        }
        return encoded;
    }

    private ClassNode doDecode(DataInputStream dis) throws IOException {
        String classNodeType = dis.readUTF();
        if (UnionTypeClassNode.class.getSimpleName().equals(classNodeType)) {
            int len = dis.readInt();
            ClassNode[] delegates = new ClassNode[len];
            for (int i = 0; i < len; ++i) {
                delegates[i] = this.doDecode(dis);
            }
            return new UnionTypeClassNode(delegates);
        }
        if (WideningCategories.LowestUpperBoundClassNode.class.getSimpleName().equals(classNodeType)) {
            String name = dis.readUTF();
            ClassNode upper = this.doDecode(dis);
            int len = dis.readInt();
            ClassNode[] interfaces = null;
            if (len >= 0) {
                interfaces = new ClassNode[len];
                for (int i = 0; i < len; ++i) {
                    interfaces[i] = this.doDecode(dis);
                }
            }
            return new WideningCategories.LowestUpperBoundClassNode(name, upper, interfaces);
        }
        boolean makeArray = dis.readBoolean();
        if (makeArray) {
            return this.doDecode(dis).makeArray();
        }
        String typedesc = dis.readUTF();
        char typeCode = typedesc.charAt(0);
        ClassNode result = ClassHelper.OBJECT_TYPE;
        if (typeCode == 'L') {
            String className = typedesc.replace('/', '.').substring(1, typedesc.length() - 1);
            try {
                result = ClassHelper.make(Class.forName(className, false, this.classLoader)).getPlainNodeReference();
            }
            catch (ClassNotFoundException e) {
                result = ClassHelper.make(className);
            }
            result.setUsingGenerics(dis.readBoolean());
            int len = dis.readInt();
            if (len >= 0) {
                GenericsType[] gts = new GenericsType[len];
                for (int i = 0; i < len; ++i) {
                    boolean placeholder = dis.readBoolean();
                    boolean wildcard = dis.readBoolean();
                    ClassNode type = this.doDecode(dis);
                    boolean low = dis.readBoolean();
                    ClassNode lb = null;
                    if (low) {
                        lb = this.doDecode(dis);
                    }
                    int upc = dis.readInt();
                    ClassNode[] ups = null;
                    if (upc >= 0) {
                        ups = new ClassNode[upc];
                        for (int j = 0; j < upc; ++j) {
                            ups[j] = this.doDecode(dis);
                        }
                    }
                    GenericsType gt = new GenericsType(type, ups, lb);
                    gt.setPlaceholder(placeholder);
                    gt.setWildcard(wildcard);
                    gts[i] = gt;
                }
                result.setGenericsTypes(gts);
            }
        } else {
            switch (typeCode) {
                case 'I': {
                    result = ClassHelper.int_TYPE;
                    break;
                }
                case 'Z': {
                    result = ClassHelper.boolean_TYPE;
                    break;
                }
                case 'B': {
                    result = ClassHelper.byte_TYPE;
                    break;
                }
                case 'C': {
                    result = ClassHelper.char_TYPE;
                    break;
                }
                case 'S': {
                    result = ClassHelper.short_TYPE;
                    break;
                }
                case 'D': {
                    result = ClassHelper.double_TYPE;
                    break;
                }
                case 'F': {
                    result = ClassHelper.float_TYPE;
                    break;
                }
                case 'J': {
                    result = ClassHelper.long_TYPE;
                    break;
                }
                case 'V': {
                    result = ClassHelper.VOID_TYPE;
                }
            }
        }
        return result;
    }

    @Override
    public ClassNode decode(String signature) {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(EncodingGroovyMethods.decodeBase64(signature)));
        try {
            return this.doDecode(dis);
        }
        catch (IOException e) {
            throw new GroovyRuntimeException("Unable to read type information", e);
        }
    }
}

