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

import static jakarta.enterprise.concurrent.ContextServiceDefinition.ALL_REMAINING;
import static jakarta.enterprise.concurrent.ContextServiceDefinition.APPLICATION;
import static jakarta.enterprise.concurrent.ContextServiceDefinition.SECURITY;
import static jakarta.enterprise.concurrent.ContextServiceDefinition.TRANSACTION;
import static org.junit.Assert.*;

import jakarta.annotation.Resource;
import org.junit.Test;

@ManagedThreadFactoryDefinition( // from ManagedThreadFactoryDefinition JavaDoc
        name = "java:global/concurrent/MyThreadFactory",
        priority = 4,
        context = @ContextServiceDefinition(
                  name = "java:global/concurrent/MyThreadFactoryContext",
                  propagated = APPLICATION))
@ManagedThreadFactoryDefinition(
        name = "java:comp/concurrent/ManagedThreadFactoryDefinitionDefaults")
public class ManagedThreadFactoryDefinitionTest {

    // from ManagedThreadFactoryDefinition JavaDoc
    @Resource(lookup = "java:global/concurrent/MyThreadFactory",
              name = "java:module/concurrent/env/MyThreadFactoryRef")
    ManagedThreadFactory myThreadFactory;

    /**
     * Validate the default values for ManagedThreadFactoryDefinition.
     */
    @Test
    public void testManagedThreadFactoryDefinitionDefaultValues() throws Exception {
        ManagedThreadFactoryDefinition def = null;
        for (ManagedThreadFactoryDefinition anno : ManagedThreadFactoryDefinitionTest.class
                .getAnnotationsByType(ManagedThreadFactoryDefinition.class))
            if ("java:comp/concurrent/ManagedThreadFactoryDefinitionDefaults".equals(anno.name()))
                def = anno;
        assertNotNull(def);
        assertEquals(Thread.NORM_PRIORITY, def.priority());
        ContextServiceDefinition csd = def.context();
        assertEquals("java:comp/DefaultContextService", csd.name());
        assertArrayEquals(new String[] { TRANSACTION }, csd.cleared());
        assertArrayEquals(new String[] {}, csd.unchanged());
        assertArrayEquals(new String[] { ALL_REMAINING }, csd.propagated());
    }

    /**
     * Validate the example that is used in ManagedThreadFactoryDefinition JavaDoc.
     */
    @Test
    public void testManagedThreadFactoryDefinitionJavaDocExample() throws Exception {
        ManagedThreadFactoryDefinition def = null;
        for (ManagedThreadFactoryDefinition anno : ManagedThreadFactoryDefinitionTest.class
                .getAnnotationsByType(ManagedThreadFactoryDefinition.class))
            if ("java:global/concurrent/MyThreadFactory".equals(anno.name()))
                def = anno;
        assertNotNull(def);
        assertEquals(4, def.priority());
        ContextServiceDefinition csd = def.context();
        assertEquals("java:global/concurrent/MyThreadFactoryContext", csd.name());
        assertArrayEquals(new String[] { APPLICATION }, csd.propagated());
        assertArrayEquals(new String[] { TRANSACTION }, csd.cleared());
        assertArrayEquals(new String[] {}, csd.unchanged());
    }
}
