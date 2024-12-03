/*
 * Decompiled with CFR 0.152.
 */
package groovy.beans;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.codehaus.groovy.transform.GroovyASTTransformationClass;

@Documented
@Retention(value=RetentionPolicy.SOURCE)
@Target(value={ElementType.FIELD, ElementType.TYPE})
@GroovyASTTransformationClass(value={"groovy.beans.BindableASTTransformation"})
public @interface Bindable {
}

