/*
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package jakarta.enterprise.concurrent;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.transaction.UserTransaction;

/**
 * <p>Defines a {@link ContextService}
 * to be registered in JNDI by the container
 * under the JNDI name that is specified in the
 * {@link #name()} attribute.</p>
 *
 * <p>Application components can refer to this JNDI name in the
 * {@link jakarta.annotation.Resource#lookup() lookup} attribute of a
 * {@link jakarta.annotation.Resource} annotation,</p>
 *
 * <pre>{@literal @}ContextServiceDefinition(
 *     name = "java:app/concurrent/MyContext",
 *     propagated = APPLICATION,
 *     unchanged = TRANSACTION,
 *     cleared = ALL_REMAINING)
 * public class MyServlet extends HttpServlet {
 *    {@literal @}Resource(lookup = "java:app/concurrent/MyContext",
 *               name = "java:app/concurrent/env/MyContextRef")
 *     ContextService appContextSvc;
 * </pre>
 *
 * <p>Resource environment references in a deployment descriptor
 * can similarly specify the <code>lookup-name</code>,</p>
 *
 * <pre>
 * &lt;resource-env-ref&gt;
 *    &lt;resource-env-ref-name&gt;java:app/env/concurrent/MyContextRef&lt;/resource-env-ref-name&gt;
 *    &lt;resource-env-ref-type&gt;javax.enterprise.concurrent.ContextService&lt;/resource-env-ref-type&gt;
 *    &lt;lookup-name&gt;java:app/concurrent/MyContext&lt;/lookup-name&gt;
 * &lt;/resource-env-ref&gt;
 * </pre>
 *
 * <p>The {@link #cleared()}, {@link #propagated()}, and {@link #unchanged()}
 * attributes enable the application to configure how thread context
 * is applied to tasks and actions that are contextualized by the
 * <code>ContextService</code>.
 * Constants are provided on this class for context types that are
 * defined by the Jakarta EE Concurrency specification.
 * In addition to those constants, a Jakarta EE product provider
 * may choose to accept additional vendor-specific context types.
 * Usage of vendor-specific types will make applications non-portable.</p>
 *
 * <p>Overlap of the same context type across multiple lists is an error and
 * prevents the <code>ContextService</code> instance from being created.
 * If {@link #ALL_REMAINING} is not present in any of the lists, it is
 * implicitly appended to the {@link #cleared()} context types.</p>
 *
 * @since 3.0
 */
// TODO could mention relation with <context-service> definition in deployment descriptor once that is added
@Repeatable(ContextServiceDefinition.List.class)
@Retention(RUNTIME)
@Target(TYPE)
public @interface ContextServiceDefinition {
    /**
     * <p>JNDI name of the {@link ContextService} instance being defined.
     * The JNDI name must must be in a valid Jakarta EE namespace,
     * such as,</p>
     *
     * <ul>
     * <li>java:comp</li>
     * <li>java:module</li>
     * <li>java:app</li>
     * <li>java:global</li>
     * </ul>
     *
     * @return <code>ContextService</code> JNDI name.
     */
    String name();

    /**
     * <p>Types of context to clear whenever a thread runs the
     * contextual task or action. The thread's previous context
     * is restored afterward.
     *
     * <p>Constants are provided on this class for the context types
     * that are defined by the Jakarta EE Concurrency specification.</p>
     *
     * @return context types to clear.
     */
    String[] cleared() default { TRANSACTION };

    /**
     * <p>Types of context to capture from the requesting thread
     * and propagate to a thread that runs the contextual task
     * or action.
     * The captured context is re-established when threads
     * run the contextual task or action, with the respective
     * thread's previous context being restored afterward.
     *
     * <p>Constants are provided on this class for the context types
     * that are defined by the Jakarta EE Concurrency specification.</p>
     *
     * @return context types to capture and propagate.
     */
    String[] propagated() default { ALL_REMAINING };

    /**
     * <p>Types of context that are left alone when a thread
     * runs the contextual task or action.</p>
     *
     * <p>Constants are provided on this class for the context types
     * that are defined by the Jakarta EE Concurrency specification.</p>
     *
     * @return context types to leave unchanged.
     */
    String[] unchanged() default {};

    /**
     * <p>All available thread context types that are not specified
     * elsewhere.</p>
     *
     * <p>For example, to define a <code>ContextService</code> that
     * propagates {@link #SECURITY} context,
     * leaves {@link #TRANSACTION} context alone,
     * and clears every other context type:</p>
     *
     * <pre>{@literal @}ContextServiceDefinition(
     *     name = "java:module/concurrent/SecurityContext",
     *     propagated = SECURITY,
     *     unchanged = TRANSACTION,
     *     cleared = ALL_REMAINING)
     * public class MyServlet extends HttpServlet ...
     * </pre>
     */
    static final String ALL_REMAINING = "Remaining";

    /**
     * <p>Context pertaining to the application component or module,
     * including its Jakarta EE namespace (such as
     * <code>java:comp/env/</code>) and thread context class loader.</p>
     *
     * <p>A cleared application context means that the thread is
     * not associated with any application component and lacks
     * access to the Jakarta EE namespace and thread context class
     * loader of the application.</p>
     */
    static final String APPLICATION = "Application";

    // TODO: CDI context is the topic of
    // https://github.com/eclipse-ee4j/concurrency-api/issues/105

    /**
     * <p>Context that controls the credentials that are associated
     * with the thread, including the caller subject and
     * invocation/RunAs subject.</p>
     *
     * <p>A cleared security context gives the thread unauthenticated
     * subjects.</p>
     */
    static final String SECURITY = "Security";

    /**
     * <p>Context that controls the transaction that is associated
     * with the thread.</p>
     *
     * <p>A thread with a cleared transaction context can begin
     * a new {@link jakarta.transaction.UserTransaction}.</p>
     *
     * <p>The execution property, {@link ManagedTask#TRANSACTION},
     * if specified, takes precedence over the behavior for
     * transaction context that is specified on the resource
     * definition annotations.</p>
     *
     * <p>Jakarta EE providers need not support the propagation
     * of transactions to other threads and can reject resource
     * definition annotations that include transaction as a
     * propagated context.</p>
     */
    // TODO the last item above is the topic of
    // https://github.com/eclipse-ee4j/concurrency-api/issues/102
    // and can be updated accordingly when that capability is added.
    static final String TRANSACTION = "Transaction";

    /**
     * Enables multiple <code>ContextServiceDefinition</code>
     * annotations on the same type.
     */
    @Retention(RUNTIME)
    @Target(TYPE)
    public @interface List {
        ContextServiceDefinition[] value();
    }
}
