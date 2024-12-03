/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import java.util.List;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.Field;
import org.aspectj.apache.bcel.classfile.Synthetic;
import org.aspectj.apache.bcel.classfile.annotation.AnnotationGen;
import org.aspectj.apache.bcel.generic.FieldGen;
import org.aspectj.util.GenericSignature;
import org.aspectj.util.GenericSignatureParser;
import org.aspectj.weaver.AjAttribute;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.ResolvedMemberImpl;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.bcel.AtAjAttributes;
import org.aspectj.weaver.bcel.BcelAnnotation;
import org.aspectj.weaver.bcel.BcelConstantPoolReader;
import org.aspectj.weaver.bcel.BcelGenericSignatureToTypeXConverter;
import org.aspectj.weaver.bcel.BcelObjectType;
import org.aspectj.weaver.bcel.Utility;

final class BcelField
extends ResolvedMemberImpl {
    public static int AccSynthetic = 4096;
    private Field field;
    private boolean isAjSynthetic;
    private boolean isSynthetic = false;
    private AnnotationAJ[] annotations;
    private final World world;
    private final BcelObjectType bcelObjectType;
    private UnresolvedType genericFieldType = null;
    private boolean unpackedGenericSignature = false;
    private boolean annotationsOnFieldObjectAreOutOfDate = false;

    BcelField(BcelObjectType declaringType, Field field) {
        super(FIELD, declaringType.getResolvedTypeX(), field.getModifiers(), field.getName(), field.getSignature());
        this.field = field;
        this.world = declaringType.getResolvedTypeX().getWorld();
        this.bcelObjectType = declaringType;
        this.unpackAttributes(this.world);
        this.checkedExceptions = UnresolvedType.NONE;
    }

    BcelField(String declaringTypeName, Field field, World world) {
        super(FIELD, UnresolvedType.forName(declaringTypeName), field.getModifiers(), field.getName(), field.getSignature());
        this.field = field;
        this.world = world;
        this.bcelObjectType = null;
        this.unpackAttributes(world);
        this.checkedExceptions = UnresolvedType.NONE;
    }

    private void unpackAttributes(World world) {
        Attribute[] attrs = this.field.getAttributes();
        if (attrs != null && attrs.length > 0) {
            ISourceContext sourceContext = this.getSourceContext(world);
            List<AjAttribute> as = Utility.readAjAttributes(this.getDeclaringType().getClassName(), attrs, sourceContext, world, this.bcelObjectType != null ? this.bcelObjectType.getWeaverVersionAttribute() : AjAttribute.WeaverVersionInfo.CURRENT, new BcelConstantPoolReader(this.field.getConstantPool()));
            as.addAll(AtAjAttributes.readAj5FieldAttributes(this.field, this, world.resolve(this.getDeclaringType()), sourceContext, world.getMessageHandler()));
        }
        this.isAjSynthetic = false;
        for (int i = attrs.length - 1; i >= 0; --i) {
            if (!(attrs[i] instanceof Synthetic)) continue;
            this.isSynthetic = true;
        }
        if ((this.field.getModifiers() & AccSynthetic) != 0) {
            this.isSynthetic = true;
        }
    }

    @Override
    public boolean isAjSynthetic() {
        return this.isAjSynthetic;
    }

    @Override
    public boolean isSynthetic() {
        return this.isSynthetic;
    }

    @Override
    public boolean hasAnnotation(UnresolvedType ofType) {
        this.ensureAnnotationTypesRetrieved();
        for (ResolvedType aType : this.annotationTypes) {
            if (!aType.equals(ofType)) continue;
            return true;
        }
        return false;
    }

    @Override
    public ResolvedType[] getAnnotationTypes() {
        this.ensureAnnotationTypesRetrieved();
        return this.annotationTypes;
    }

    @Override
    public AnnotationAJ[] getAnnotations() {
        this.ensureAnnotationTypesRetrieved();
        return this.annotations;
    }

    @Override
    public AnnotationAJ getAnnotationOfType(UnresolvedType ofType) {
        this.ensureAnnotationTypesRetrieved();
        for (AnnotationAJ annotation : this.annotations) {
            if (!annotation.getTypeName().equals(ofType.getName())) continue;
            return annotation;
        }
        return null;
    }

    private void ensureAnnotationTypesRetrieved() {
        if (this.annotationTypes == null) {
            AnnotationGen[] annos = this.field.getAnnotations();
            if (annos.length == 0) {
                this.annotationTypes = ResolvedType.EMPTY_ARRAY;
                this.annotations = AnnotationAJ.EMPTY_ARRAY;
            } else {
                int annosCount = annos.length;
                this.annotationTypes = new ResolvedType[annosCount];
                this.annotations = new AnnotationAJ[annosCount];
                for (int i = 0; i < annosCount; ++i) {
                    AnnotationGen anno = annos[i];
                    this.annotations[i] = new BcelAnnotation(anno, this.world);
                    this.annotationTypes[i] = this.annotations[i].getType();
                }
            }
        }
    }

    @Override
    public void addAnnotation(AnnotationAJ annotation) {
        this.ensureAnnotationTypesRetrieved();
        int len = this.annotations.length;
        AnnotationAJ[] ret = new AnnotationAJ[len + 1];
        System.arraycopy(this.annotations, 0, ret, 0, len);
        ret[len] = annotation;
        this.annotations = ret;
        ResolvedType[] newAnnotationTypes = new ResolvedType[len + 1];
        System.arraycopy(this.annotationTypes, 0, newAnnotationTypes, 0, len);
        newAnnotationTypes[len] = annotation.getType();
        this.annotationTypes = newAnnotationTypes;
        this.annotationsOnFieldObjectAreOutOfDate = true;
    }

    public void removeAnnotation(AnnotationAJ annotation) {
        this.ensureAnnotationTypesRetrieved();
        int len = this.annotations.length;
        AnnotationAJ[] ret = new AnnotationAJ[len - 1];
        int p = 0;
        for (AnnotationAJ anno : this.annotations) {
            if (anno.getType().equals(annotation.getType())) continue;
            ret[p++] = anno;
        }
        this.annotations = ret;
        ResolvedType[] newAnnotationTypes = new ResolvedType[len - 1];
        p = 0;
        for (ResolvedType anno : this.annotationTypes) {
            if (anno.equals(annotation.getType())) continue;
            newAnnotationTypes[p++] = anno;
        }
        this.annotationTypes = newAnnotationTypes;
        this.annotationsOnFieldObjectAreOutOfDate = true;
    }

    @Override
    public UnresolvedType getGenericReturnType() {
        this.unpackGenericSignature();
        return this.genericFieldType;
    }

    public Field getFieldAsIs() {
        return this.field;
    }

    public Field getField(ConstantPool cpool) {
        if (!this.annotationsOnFieldObjectAreOutOfDate) {
            return this.field;
        }
        FieldGen newFieldGen = new FieldGen(this.field, cpool);
        newFieldGen.removeAnnotations();
        for (AnnotationAJ annotation : this.annotations) {
            newFieldGen.addAnnotation(new AnnotationGen(((BcelAnnotation)annotation).getBcelAnnotation(), cpool, true));
        }
        this.field = newFieldGen.getField();
        this.annotationsOnFieldObjectAreOutOfDate = false;
        return this.field;
    }

    private void unpackGenericSignature() {
        if (this.unpackedGenericSignature) {
            return;
        }
        if (!this.world.isInJava5Mode()) {
            this.genericFieldType = this.getReturnType();
            return;
        }
        this.unpackedGenericSignature = true;
        String gSig = this.field.getGenericSignature();
        if (gSig != null) {
            GenericSignature.FieldTypeSignature fts = new GenericSignatureParser().parseAsFieldSignature(gSig);
            GenericSignature.ClassSignature genericTypeSig = this.bcelObjectType.getGenericClassTypeSignature();
            GenericSignature.FormalTypeParameter[] parentFormals = this.bcelObjectType.getAllFormals();
            GenericSignature.FormalTypeParameter[] typeVars = genericTypeSig == null ? new GenericSignature.FormalTypeParameter[]{} : genericTypeSig.formalTypeParameters;
            GenericSignature.FormalTypeParameter[] formals = new GenericSignature.FormalTypeParameter[parentFormals.length + typeVars.length];
            System.arraycopy(typeVars, 0, formals, 0, typeVars.length);
            System.arraycopy(parentFormals, 0, formals, typeVars.length, parentFormals.length);
            try {
                this.genericFieldType = BcelGenericSignatureToTypeXConverter.fieldTypeSignature2TypeX(fts, formals, this.world);
            }
            catch (BcelGenericSignatureToTypeXConverter.GenericSignatureFormatException e) {
                throw new IllegalStateException("While determing the generic field type of " + this.toString() + " with generic signature " + gSig + " the following error was detected: " + e.getMessage());
            }
        } else {
            this.genericFieldType = this.getReturnType();
        }
    }

    @Override
    public void evictWeavingState() {
        if (this.field != null) {
            this.unpackGenericSignature();
            this.unpackAttributes(this.world);
            this.ensureAnnotationTypesRetrieved();
            this.field = null;
        }
    }
}

