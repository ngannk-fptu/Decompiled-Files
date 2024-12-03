/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import software.amazon.ion.IonContainer;
import software.amazon.ion.IonSystem;
import software.amazon.ion.IonType;
import software.amazon.ion.IonWriter;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.SymbolToken;
import software.amazon.ion.UnknownSymbolException;
import software.amazon.ion.ValueVisitor;
import software.amazon.ion.system.IonTextWriterBuilder;

public interface IonValue
extends Cloneable {
    public static final IonValue[] EMPTY_ARRAY = new IonValue[0];

    public IonType getType();

    public boolean isNullValue();

    public boolean isReadOnly();

    public SymbolTable getSymbolTable();

    public String getFieldName();

    public SymbolToken getFieldNameSymbol();

    public IonContainer getContainer();

    public boolean removeFromContainer();

    public IonValue topLevelValue();

    public String[] getTypeAnnotations();

    public SymbolToken[] getTypeAnnotationSymbols();

    public boolean hasTypeAnnotation(String var1);

    public void setTypeAnnotations(String ... var1);

    public void setTypeAnnotationSymbols(SymbolToken ... var1);

    public void clearTypeAnnotations();

    public void addTypeAnnotation(String var1);

    public void removeTypeAnnotation(String var1);

    public void writeTo(IonWriter var1);

    public void accept(ValueVisitor var1) throws Exception;

    public void makeReadOnly();

    public IonSystem getSystem();

    public IonValue clone() throws UnknownSymbolException;

    public String toString();

    public String toPrettyString();

    public String toString(IonTextWriterBuilder var1);

    public boolean equals(Object var1);

    public int hashCode();
}

