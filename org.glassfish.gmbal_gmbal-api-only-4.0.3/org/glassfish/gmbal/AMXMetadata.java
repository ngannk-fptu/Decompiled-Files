/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.gmbal;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.glassfish.gmbal.DescriptorKey;

@Documented
@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface AMXMetadata {
    @DescriptorKey(value="amx.isSingleton")
    public boolean isSingleton() default false;

    @DescriptorKey(value="amx.group")
    public String group() default "other";

    @DescriptorKey(value="amx.subTypes")
    public String[] subTypes() default {};

    @DescriptorKey(value="amx.genericInterfaceName")
    public String genericInterfaceName() default "org.glassfish.admin.amx.core.AMXProxy";

    @DescriptorKey(value="immutableInfo")
    public boolean immutableInfo() default true;

    @DescriptorKey(value="interfaceName")
    public String interfaceClassName() default "";

    @DescriptorKey(value="type")
    public String type() default "";
}

