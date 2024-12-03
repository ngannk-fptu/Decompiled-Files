/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile.annotation;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.annotation.AnnotationGen;

public abstract class RuntimeAnnos
extends Attribute {
    private List<AnnotationGen> annotations;
    private boolean visible;
    private boolean inflated = false;
    private byte[] annotation_data;

    public RuntimeAnnos(byte attrid, boolean visible, int nameIdx, int len, ConstantPool cpool) {
        super(attrid, nameIdx, len, cpool);
        this.visible = visible;
        this.annotations = new ArrayList<AnnotationGen>();
    }

    public RuntimeAnnos(byte attrid, boolean visible, int nameIdx, int len, byte[] data, ConstantPool cpool) {
        super(attrid, nameIdx, len, cpool);
        this.visible = visible;
        this.annotations = new ArrayList<AnnotationGen>();
        this.annotation_data = data;
    }

    public List<AnnotationGen> getAnnotations() {
        if (!this.inflated) {
            this.inflate();
        }
        return this.annotations;
    }

    public boolean areVisible() {
        return this.visible;
    }

    protected void readAnnotations(DataInputStream dis, ConstantPool cpool) throws IOException {
        this.annotation_data = new byte[this.length];
        dis.readFully(this.annotation_data, 0, this.length);
    }

    protected void writeAnnotations(DataOutputStream dos) throws IOException {
        if (!this.inflated) {
            dos.write(this.annotation_data, 0, this.length);
        } else {
            dos.writeShort(this.annotations.size());
            for (AnnotationGen ann : this.annotations) {
                ann.dump(dos);
            }
        }
    }

    private void inflate() {
        try {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(this.annotation_data));
            int numberOfAnnotations = dis.readUnsignedShort();
            if (numberOfAnnotations > 0) {
                ArrayList<AnnotationGen> inflatedAnnotations = new ArrayList<AnnotationGen>();
                for (int i = 0; i < numberOfAnnotations; ++i) {
                    inflatedAnnotations.add(AnnotationGen.read(dis, this.getConstantPool(), this.visible));
                }
                this.annotations = inflatedAnnotations;
            }
            dis.close();
            this.inflated = true;
        }
        catch (IOException ioe) {
            throw new RuntimeException("Unabled to inflate annotation data, badly formed? ");
        }
    }

    public boolean isInflated() {
        return this.inflated;
    }
}

