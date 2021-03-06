/*
 * Copyright 2016 Johns Hopkins University
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.dataconservancy.cos.rdf.support;

import org.dataconservancy.cos.rdf.support.test.model.AnnotatedElementPairTest.Bar;
import org.dataconservancy.cos.rdf.support.test.model.AnnotatedElementPairTest.Foo;
import org.dataconservancy.cos.rdf.annotations.IndividualUri;
import org.junit.Test;
import org.springframework.core.annotation.AnnotationAttributes;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests concerning the behavior of {@code AnnotatedElementPair}
 */
public class AnnotatedElementPairTest {

    /**
     * Insures that even though two different classes share a field of the same name, they
     * are still considered a unique {@code AnnotatedElementPair}.
     * @throws Exception
     */
    @Test
    public void testPairUniqueness() throws Exception {
        // Note that Fields with the same name from different classes are not equal.
        Object foo = new Foo();
        Object bar = new Bar();
        Field fooId = Foo.class.getDeclaredField("id");
        Field barId = Bar.class.getDeclaredField("id");
        assertFalse(fooId.equals(barId));

        // Therefore, the AnnotatedElementPair for the two fields are not equal
        AnnotatedElementPair fooPair = AnnotatedElementPair.forPair(fooId, IndividualUri.class);
        AnnotatedElementPair barPair = AnnotatedElementPair.forPair(barId, IndividualUri.class);
        assertFalse(fooPair.equals(barPair));

        // For collecting AnnotatedElementPairs off of our test classes.
        AnnotatedElementPairMap<AnnotatedElementPair, AnnotationAttributes> attributesMap = new AnnotatedElementPairMap<>();

        // Collect AnnotatedElementPairs off of the Foo instance.
        OwlAnnotationProcessor.getAnnotationsForInstance(foo, attributesMap);

        // Collect AnnotatedElementPairs off of the Bar instance.
        OwlAnnotationProcessor.getAnnotationsForInstance(bar, attributesMap);

        assertEquals(2, attributesMap.size());
        assertTrue(attributesMap.keySet().contains(fooPair));
        assertTrue(attributesMap.keySet().contains(barPair));

        // Sanity check insuring that OwlAnnotationProcessor properly applies the transform to the 'foo' instance
        assertEquals("bar", OwlAnnotationProcessor.getIndividualId(null, bar, attributesMap));
        assertEquals("Moo!", OwlAnnotationProcessor.getIndividualId(null, foo, attributesMap));
    }

}