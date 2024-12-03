/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aspectj.util.PartialOrder
 *  org.aspectj.util.PartialOrder$PartialComparable
 *  org.springframework.core.Ordered
 *  org.springframework.util.ClassUtils
 */
package org.springframework.aop.aspectj.autoproxy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.aopalliance.aop.Advice;
import org.aspectj.util.PartialOrder;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AbstractAspectJAdvice;
import org.springframework.aop.aspectj.AspectJPointcutAdvisor;
import org.springframework.aop.aspectj.AspectJProxyUtils;
import org.springframework.aop.aspectj.autoproxy.AspectJPrecedenceComparator;
import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import org.springframework.core.Ordered;
import org.springframework.util.ClassUtils;

public class AspectJAwareAdvisorAutoProxyCreator
extends AbstractAdvisorAutoProxyCreator {
    private static final Comparator<Advisor> DEFAULT_PRECEDENCE_COMPARATOR = new AspectJPrecedenceComparator();

    @Override
    protected List<Advisor> sortAdvisors(List<Advisor> advisors) {
        ArrayList<PartiallyComparableAdvisorHolder> partiallyComparableAdvisors = new ArrayList<PartiallyComparableAdvisorHolder>(advisors.size());
        for (Advisor advisor : advisors) {
            partiallyComparableAdvisors.add(new PartiallyComparableAdvisorHolder(advisor, DEFAULT_PRECEDENCE_COMPARATOR));
        }
        List sorted = PartialOrder.sort(partiallyComparableAdvisors);
        if (sorted != null) {
            ArrayList<Advisor> result = new ArrayList<Advisor>(advisors.size());
            for (PartiallyComparableAdvisorHolder pcAdvisor : sorted) {
                result.add(pcAdvisor.getAdvisor());
            }
            return result;
        }
        return super.sortAdvisors(advisors);
    }

    @Override
    protected void extendAdvisors(List<Advisor> candidateAdvisors) {
        AspectJProxyUtils.makeAdvisorChainAspectJCapableIfNecessary(candidateAdvisors);
    }

    @Override
    protected boolean shouldSkip(Class<?> beanClass, String beanName) {
        List<Advisor> candidateAdvisors = this.findCandidateAdvisors();
        for (Advisor advisor : candidateAdvisors) {
            if (!(advisor instanceof AspectJPointcutAdvisor) || !((AspectJPointcutAdvisor)advisor).getAspectName().equals(beanName)) continue;
            return true;
        }
        return super.shouldSkip(beanClass, beanName);
    }

    private static class PartiallyComparableAdvisorHolder
    implements PartialOrder.PartialComparable {
        private final Advisor advisor;
        private final Comparator<Advisor> comparator;

        public PartiallyComparableAdvisorHolder(Advisor advisor, Comparator<Advisor> comparator) {
            this.advisor = advisor;
            this.comparator = comparator;
        }

        public int compareTo(Object obj) {
            Advisor otherAdvisor = ((PartiallyComparableAdvisorHolder)obj).advisor;
            return this.comparator.compare(this.advisor, otherAdvisor);
        }

        public int fallbackCompareTo(Object obj) {
            return 0;
        }

        public Advisor getAdvisor() {
            return this.advisor;
        }

        public String toString() {
            Advice advice = this.advisor.getAdvice();
            StringBuilder sb = new StringBuilder(ClassUtils.getShortName(advice.getClass()));
            boolean appended = false;
            if (this.advisor instanceof Ordered) {
                sb.append(": order = ").append(((Ordered)this.advisor).getOrder());
                appended = true;
            }
            if (advice instanceof AbstractAspectJAdvice) {
                sb.append(!appended ? ": " : ", ");
                AbstractAspectJAdvice ajAdvice = (AbstractAspectJAdvice)advice;
                sb.append("aspect name = ");
                sb.append(ajAdvice.getAspectName());
                sb.append(", declaration order = ");
                sb.append(ajAdvice.getDeclarationOrder());
            }
            return sb.toString();
        }
    }
}

