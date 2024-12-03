/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaIntHolderEx;
import org.apache.xmlbeans.impl.values.JavaIntegerHolderEx;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STTextRotation;

public class STTextRotationImpl
extends XmlUnionImpl
implements STTextRotation,
STTextRotation.Member,
STTextRotation.Member2 {
    private static final long serialVersionUID = 1L;

    public STTextRotationImpl(SchemaType sType) {
        super(sType, false);
    }

    protected STTextRotationImpl(SchemaType sType, boolean b) {
        super(sType, b);
    }

    public static class MemberImpl2
    extends JavaIntegerHolderEx
    implements STTextRotation.Member2 {
        private static final long serialVersionUID = 1L;

        public MemberImpl2(SchemaType sType) {
            super(sType, false);
        }

        protected MemberImpl2(SchemaType sType, boolean b) {
            super(sType, b);
        }
    }

    public static class MemberImpl
    extends JavaIntHolderEx
    implements STTextRotation.Member {
        private static final long serialVersionUID = 1L;

        public MemberImpl(SchemaType sType) {
            super(sType, false);
        }

        protected MemberImpl(SchemaType sType, boolean b) {
            super(sType, b);
        }
    }
}

