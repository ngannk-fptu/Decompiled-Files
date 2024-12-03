/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import org.apache.tools.ant.types.resources.AbstractClasspathResource;

public class JavaConstantResource
extends AbstractClasspathResource {
    @Override
    protected InputStream openInputStream(ClassLoader cl) throws IOException {
        String constant = this.getName();
        if (constant == null) {
            throw new IOException("Attribute 'name' must be set.");
        }
        int index = constant.lastIndexOf(46);
        if (index < 0) {
            throw new IOException("No class name in " + constant);
        }
        String classname = constant.substring(0, index);
        String fieldname = constant.substring(index + 1);
        try {
            Class<?> clazz = cl != null ? Class.forName(classname, true, cl) : Class.forName(classname);
            Field field = clazz.getField(fieldname);
            String value = field.get(null).toString();
            return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
        }
        catch (ClassNotFoundException e) {
            throw new IOException("Class not found:" + classname);
        }
        catch (NoSuchFieldException e) {
            throw new IOException("Field not found:" + fieldname + " in " + classname);
        }
        catch (IllegalAccessException e) {
            throw new IOException("Illegal access to :" + fieldname + " in " + classname);
        }
        catch (NullPointerException npe) {
            throw new IOException("Not a static field: " + fieldname + " in " + classname);
        }
    }
}

