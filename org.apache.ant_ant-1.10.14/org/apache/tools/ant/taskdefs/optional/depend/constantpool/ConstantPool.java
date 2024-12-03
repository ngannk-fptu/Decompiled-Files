/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ClassCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPoolEntry;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.FieldRefCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.InterfaceMethodRefCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.MethodRefCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.NameAndTypeCPInfo;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.Utf8CPInfo;

public class ConstantPool {
    private final List<ConstantPoolEntry> entries = new ArrayList<ConstantPoolEntry>();
    private final Map<String, Integer> utf8Indexes = new HashMap<String, Integer>();

    public ConstantPool() {
        this.entries.add(null);
    }

    public void read(DataInputStream classStream) throws IOException {
        ConstantPoolEntry nextEntry;
        int numEntries = classStream.readUnsignedShort();
        for (int i = 1; i < numEntries; i += nextEntry.getNumEntries()) {
            nextEntry = ConstantPoolEntry.readEntry(classStream);
            this.addEntry(nextEntry);
        }
    }

    public int size() {
        return this.entries.size();
    }

    public int addEntry(ConstantPoolEntry entry) {
        int index = this.entries.size();
        this.entries.add(entry);
        int numSlots = entry.getNumEntries();
        for (int j = 0; j < numSlots - 1; ++j) {
            this.entries.add(null);
        }
        if (entry instanceof Utf8CPInfo) {
            Utf8CPInfo utf8Info = (Utf8CPInfo)entry;
            this.utf8Indexes.put(utf8Info.getValue(), index);
        }
        return index;
    }

    public void resolve() {
        for (ConstantPoolEntry poolInfo : this.entries) {
            if (poolInfo == null || poolInfo.isResolved()) continue;
            poolInfo.resolve(this);
        }
    }

    public ConstantPoolEntry getEntry(int index) {
        return this.entries.get(index);
    }

    public int getUTF8Entry(String value) {
        int index = -1;
        Integer indexInteger = this.utf8Indexes.get(value);
        if (indexInteger != null) {
            index = indexInteger;
        }
        return index;
    }

    public int getClassEntry(String className) {
        int index = -1;
        int size = this.entries.size();
        for (int i = 0; i < size && index == -1; ++i) {
            ClassCPInfo classinfo;
            ConstantPoolEntry element = this.entries.get(i);
            if (!(element instanceof ClassCPInfo) || !(classinfo = (ClassCPInfo)element).getClassName().equals(className)) continue;
            index = i;
        }
        return index;
    }

    public int getConstantEntry(Object constantValue) {
        int index = -1;
        int size = this.entries.size();
        for (int i = 0; i < size && index == -1; ++i) {
            ConstantCPInfo constantEntry;
            ConstantPoolEntry element = this.entries.get(i);
            if (!(element instanceof ConstantCPInfo) || !(constantEntry = (ConstantCPInfo)element).getValue().equals(constantValue)) continue;
            index = i;
        }
        return index;
    }

    public int getMethodRefEntry(String methodClassName, String methodName, String methodType) {
        int index = -1;
        int size = this.entries.size();
        for (int i = 0; i < size && index == -1; ++i) {
            MethodRefCPInfo methodRefEntry;
            ConstantPoolEntry element = this.entries.get(i);
            if (!(element instanceof MethodRefCPInfo) || !(methodRefEntry = (MethodRefCPInfo)element).getMethodClassName().equals(methodClassName) || !methodRefEntry.getMethodName().equals(methodName) || !methodRefEntry.getMethodType().equals(methodType)) continue;
            index = i;
        }
        return index;
    }

    public int getInterfaceMethodRefEntry(String interfaceMethodClassName, String interfaceMethodName, String interfaceMethodType) {
        int index = -1;
        int size = this.entries.size();
        for (int i = 0; i < size && index == -1; ++i) {
            InterfaceMethodRefCPInfo interfaceMethodRefEntry;
            ConstantPoolEntry element = this.entries.get(i);
            if (!(element instanceof InterfaceMethodRefCPInfo) || !(interfaceMethodRefEntry = (InterfaceMethodRefCPInfo)element).getInterfaceMethodClassName().equals(interfaceMethodClassName) || !interfaceMethodRefEntry.getInterfaceMethodName().equals(interfaceMethodName) || !interfaceMethodRefEntry.getInterfaceMethodType().equals(interfaceMethodType)) continue;
            index = i;
        }
        return index;
    }

    public int getFieldRefEntry(String fieldClassName, String fieldName, String fieldType) {
        int index = -1;
        int size = this.entries.size();
        for (int i = 0; i < size && index == -1; ++i) {
            FieldRefCPInfo fieldRefEntry;
            ConstantPoolEntry element = this.entries.get(i);
            if (!(element instanceof FieldRefCPInfo) || !(fieldRefEntry = (FieldRefCPInfo)element).getFieldClassName().equals(fieldClassName) || !fieldRefEntry.getFieldName().equals(fieldName) || !fieldRefEntry.getFieldType().equals(fieldType)) continue;
            index = i;
        }
        return index;
    }

    public int getNameAndTypeEntry(String name, String type) {
        int index = -1;
        int size = this.entries.size();
        for (int i = 0; i < size && index == -1; ++i) {
            NameAndTypeCPInfo nameAndTypeEntry;
            ConstantPoolEntry element = this.entries.get(i);
            if (!(element instanceof NameAndTypeCPInfo) || !(nameAndTypeEntry = (NameAndTypeCPInfo)element).getName().equals(name) || !nameAndTypeEntry.getType().equals(type)) continue;
            index = i;
        }
        return index;
    }

    public String toString() {
        return IntStream.range(0, this.entries.size()).mapToObj(i -> String.format("[%d] = %s", i, this.getEntry(i))).collect(Collectors.joining("\n", "\n", "\n"));
    }
}

