/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.sts.endpoints.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.sts.endpoints.internal.Expr;

@SdkInternalApi
public interface TemplateVisitor<T> {
    public T visitStaticTemplate(String var1);

    public T visitSingleDynamicTemplate(Expr var1);

    public T visitStaticElement(String var1);

    public T visitDynamicElement(Expr var1);

    public T startMultipartTemplate();

    public T finishMultipartTemplate();
}

