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

/**
 * <p>Defines a {@link ManagedThreadFactory}
 * to be registered in JNDI by the container
 * under the JNDI name that is specified in the
 * {@link #name()} attribute.</p>
 *
 * <p>Application components can refer to this JNDI name in the
 * {@link jakarta.annotation.Resource#lookup() lookup} attribute of a
 * {@link jakarta.annotation.Resource} annotation,</p>
 *
 * <pre>{@literal @}ManagedThreadFactoryDefinition(
 *     name = "java:global/concurrent/MyThreadFactory",
 *     priority = "4",
 *     context ={@literal @}ContextServiceDefinition(
 *               name = "java:global/concurrent/MyThreadFactoryContext",
 *               propagated = APPLICATION))
 * public class MyServlet extends HttpServlet {
 *    {@literal @}Resource(lookup = "java:global/concurrent/MyThreadFactory",
 *               name = "java:module/concurrent/env/MyThreadFactoryRef")
 *     ManagedThreadFactory myThreadFactory;
 * </pre>
 *
 * <p>Resource environment references in a deployment descriptor
 * can similarly specify the <code>lookup-name</code>,</p>
 *
 * <pre>
 * &lt;resource-env-ref&gt;
 *    &lt;resource-env-ref-name&gt;java:module/env/concurrent/MyThreadFactoryRef&lt;/resource-env-ref-name&gt;
 *    &lt;resource-env-ref-type&gt;javax.enterprise.concurrent.ManagedThreadFactory&lt;/resource-env-ref-type&gt;
 *    &lt;lookup-name&gt;java:global/concurrent/MyThreadFactory&lt;/lookup-name&gt;
 * &lt;/resource-env-ref&gt;
 * </pre>
 *
 * @since 3.0
 */
//TODO could mention relation with <managed-thread-factory> definition in deployment descriptor once that is added
@Repeatable(ManagedThreadFactoryDefinition.List.class)
@Retention(RUNTIME)
@Target(TYPE)
public @interface ManagedThreadFactoryDefinition {
    /**
     * JNDI name of the {@link ManagedThreadFactory} instance.
     * The JNDI name must must be in a valid Jakarta EE namespace,
     * such as,
     * <ul>
     * <li>java:comp</li>
     * <li>java:module</li>
     * <li>java:app</li>
     * <li>java:global</li>
     * </ul>
     *
     * @return <code>ManagedThreadFactory</code> JNDI name.
     */
    String name();

    /**
     * <p>Determines how context is applied to threads from this
     * thread factory.</p>
     *
     * <p>The default value indicates to use the default instance of
     * {@link ContextService} by specifying a
     * {@link ContextServiceDefinition} with the name
     * <code>java:comp/DefaultContextService</code>.</p>
     *
     * @return instructions for capturing and propagating or clearing context.
     */
    ContextServiceDefinition context() default @ContextServiceDefinition(name = "java:comp/DefaultContextService");

    /**
     * <p>Priority for threads created by this thread factory.</p>
     *
     * <p>The default is {@link java.lang.Thread#NORM_PRIORITY}.</p>
     *
     * @return the priority for new threads.
     */
    int priority() default Thread.NORM_PRIORITY;

    /**
     * Enables multiple <code>ManagedThreadFactoryDefinition</code>
     * annotations on the same type.
     */
    @Retention(RUNTIME)
    @Target(TYPE)
    public @interface List {
        ManagedThreadFactoryDefinition[] value();
    }
}
