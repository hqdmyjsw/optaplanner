/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.move.generic;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.multivar.TestdataMultiVarEntity;
import org.optaplanner.core.impl.testdata.domain.multivar.TestdataMultiVarSolution;
import org.optaplanner.core.impl.testdata.domain.multivar.TestdataOtherValue;
import org.optaplanner.core.impl.testdata.domain.valuerange.entityproviding.TestdataEntityProvidingEntity;
import org.optaplanner.core.impl.testdata.domain.valuerange.entityproviding.TestdataEntityProvidingSolution;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertSame;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.*;

public class SwapMoveTest {

    @Test
    public void isMoveDoableValueRangeProviderOnEntity() {
        TestdataValue v1 = new TestdataValue("1");
        TestdataValue v2 = new TestdataValue("2");
        TestdataValue v3 = new TestdataValue("3");
        TestdataValue v4 = new TestdataValue("4");
        TestdataValue v5 = new TestdataValue("5");

        TestdataEntityProvidingEntity a = new TestdataEntityProvidingEntity("a", Arrays.asList(v1, v2, v3), null);
        TestdataEntityProvidingEntity b = new TestdataEntityProvidingEntity("b", Arrays.asList(v2, v3, v4, v5), null);
        TestdataEntityProvidingEntity c = new TestdataEntityProvidingEntity("c", Arrays.asList(v4, v5), null);

        ScoreDirector<TestdataEntityProvidingSolution> scoreDirector = mock(ScoreDirector.class);
        EntityDescriptor<TestdataEntityProvidingSolution> entityDescriptor = TestdataEntityProvidingEntity.buildEntityDescriptor();

        SwapMove<TestdataEntityProvidingSolution> abMove = new SwapMove<>(entityDescriptor.getGenuineVariableDescriptorList(), a, b);
        a.setValue(v1);
        b.setValue(v2);
        assertEquals(false, abMove.isMoveDoable(scoreDirector));
        a.setValue(v2);
        b.setValue(v2);
        assertEquals(false, abMove.isMoveDoable(scoreDirector));
        a.setValue(v2);
        b.setValue(v3);
        assertEquals(true, abMove.isMoveDoable(scoreDirector));
        a.setValue(v3);
        b.setValue(v2);
        assertEquals(true, abMove.isMoveDoable(scoreDirector));
        a.setValue(v3);
        b.setValue(v3);
        assertEquals(false, abMove.isMoveDoable(scoreDirector));
        a.setValue(v2);
        b.setValue(v4);
        assertEquals(false, abMove.isMoveDoable(scoreDirector));

        SwapMove<TestdataEntityProvidingSolution> acMove = new SwapMove<>(entityDescriptor.getGenuineVariableDescriptorList(), a, c);
        a.setValue(v1);
        c.setValue(v4);
        assertEquals(false, acMove.isMoveDoable(scoreDirector));
        a.setValue(v2);
        c.setValue(v5);
        assertEquals(false, acMove.isMoveDoable(scoreDirector));

        SwapMove<TestdataEntityProvidingSolution> bcMove = new SwapMove<>(entityDescriptor.getGenuineVariableDescriptorList(), b, c);
        b.setValue(v2);
        c.setValue(v4);
        assertEquals(false, bcMove.isMoveDoable(scoreDirector));
        b.setValue(v4);
        c.setValue(v5);
        assertEquals(true, bcMove.isMoveDoable(scoreDirector));
        b.setValue(v5);
        c.setValue(v4);
        assertEquals(true, bcMove.isMoveDoable(scoreDirector));
        b.setValue(v5);
        c.setValue(v5);
        assertEquals(false, bcMove.isMoveDoable(scoreDirector));
    }

    @Test
    public void doMove() {
        TestdataValue v1 = new TestdataValue("1");
        TestdataValue v2 = new TestdataValue("2");
        TestdataValue v3 = new TestdataValue("3");
        TestdataValue v4 = new TestdataValue("4");

        TestdataEntityProvidingEntity a = new TestdataEntityProvidingEntity("a", Arrays.asList(v1, v2, v3), null);
        TestdataEntityProvidingEntity b = new TestdataEntityProvidingEntity("b", Arrays.asList(v1, v2, v3, v4), null);
        TestdataEntityProvidingEntity c = new TestdataEntityProvidingEntity("c", Arrays.asList(v2, v3, v4), null);

        ScoreDirector<TestdataEntityProvidingSolution> scoreDirector = mock(ScoreDirector.class);
        EntityDescriptor<TestdataEntityProvidingSolution> entityDescriptor = TestdataEntityProvidingEntity.buildEntityDescriptor();

        SwapMove<TestdataEntityProvidingSolution> abMove = new SwapMove<>(entityDescriptor.getGenuineVariableDescriptorList(), a, b);

        a.setValue(v1);
        b.setValue(v1);
        abMove.doMove(scoreDirector);
        assertEquals(v1, a.getValue());
        assertEquals(v1, b.getValue());

        a.setValue(v1);
        b.setValue(v2);
        abMove.doMove(scoreDirector);
        assertEquals(v2, a.getValue());
        assertEquals(v1, b.getValue());

        a.setValue(v2);
        b.setValue(v3);
        abMove.doMove(scoreDirector);
        assertEquals(v3, a.getValue());
        assertEquals(v2, b.getValue());
        abMove.doMove(scoreDirector);
        assertEquals(v2, a.getValue());
        assertEquals(v3, b.getValue());

        SwapMove<TestdataEntityProvidingSolution> acMove = new SwapMove<>(entityDescriptor.getGenuineVariableDescriptorList(), a, c);

        a.setValue(v2);
        c.setValue(v2);
        acMove.doMove(scoreDirector);
        assertEquals(v2, a.getValue());
        assertEquals(v2, c.getValue());

        a.setValue(v3);
        c.setValue(v2);
        acMove.doMove(scoreDirector);
        assertEquals(v2, a.getValue());
        assertEquals(v3, c.getValue());

        a.setValue(v3);
        c.setValue(v4);
        acMove.doMove(scoreDirector);
        assertEquals(v4, a.getValue());
        assertEquals(v3, c.getValue());
        acMove.doMove(scoreDirector);
        assertEquals(v3, a.getValue());
        assertEquals(v4, c.getValue());

        SwapMove<TestdataEntityProvidingSolution> bcMove = new SwapMove<>(entityDescriptor.getGenuineVariableDescriptorList(), b, c);

        b.setValue(v2);
        c.setValue(v2);
        bcMove.doMove(scoreDirector);
        assertEquals(v2, b.getValue());
        assertEquals(v2, c.getValue());

        b.setValue(v2);
        c.setValue(v3);
        bcMove.doMove(scoreDirector);
        assertEquals(v3, b.getValue());
        assertEquals(v2, c.getValue());

        b.setValue(v2);
        c.setValue(v3);
        bcMove.doMove(scoreDirector);
        assertEquals(v3, b.getValue());
        assertEquals(v2, c.getValue());
        bcMove.doMove(scoreDirector);
        assertEquals(v2, b.getValue());
        assertEquals(v3, c.getValue());
    }

    @Test
    public void rebase() {
        EntityDescriptor<TestdataSolution> entityDescriptor = TestdataEntity.buildEntityDescriptor();
        List<GenuineVariableDescriptor<TestdataSolution>> variableDescriptorList = entityDescriptor.getGenuineVariableDescriptorList();

        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataEntity e1 = new TestdataEntity("e1", v1);
        TestdataEntity e2 = new TestdataEntity("e2", null);
        TestdataEntity e3 = new TestdataEntity("e3", v1);

        TestdataValue destinationV1 = new TestdataValue("v1");
        TestdataValue destinationV2 = new TestdataValue("v2");
        TestdataEntity destinationE1 = new TestdataEntity("e1", destinationV1);
        TestdataEntity destinationE2 = new TestdataEntity("e2", null);
        TestdataEntity destinationE3 = new TestdataEntity("e3", destinationV1);

        ScoreDirector<TestdataSolution> destinationScoreDirector = mockRebasingScoreDirector(
                entityDescriptor.getSolutionDescriptor(), new Object[][]{
                        {v1, destinationV1},
                        {v2, destinationV2},
                        {e1, destinationE1},
                        {e2, destinationE2},
                        {e3, destinationE3},
                });

        assertSameProperties(destinationE1, destinationE2,
                new SwapMove<>(variableDescriptorList, e1, e2).rebase(destinationScoreDirector));
        assertSameProperties(destinationE1, destinationE3,
                new SwapMove<>(variableDescriptorList, e1, e3).rebase(destinationScoreDirector));
        assertSameProperties(destinationE2, destinationE3,
                new SwapMove<>(variableDescriptorList, e2, e3).rebase(destinationScoreDirector));
    }

    public void assertSameProperties(Object leftEntity, Object rightEntity, SwapMove<?> move) {
        assertSame(leftEntity, move.getLeftEntity());
        assertSame(rightEntity, move.getRightEntity());
    }

    @Test
    public void getters() {
        GenuineVariableDescriptor<TestdataMultiVarSolution> primaryDescriptor = TestdataMultiVarEntity.buildVariableDescriptorForPrimaryValue();
        GenuineVariableDescriptor<TestdataMultiVarSolution> secondaryDescriptor = TestdataMultiVarEntity.buildVariableDescriptorForSecondaryValue();
        SwapMove move = new SwapMove<>(Arrays.asList(primaryDescriptor),
                new TestdataMultiVarEntity("a"), new TestdataMultiVarEntity("b"));
        assertCollectionContainsExactly(move.getVariableNameList(), "primaryValue");
        assertCode("a", move.getLeftEntity());
        assertCode("b", move.getRightEntity());

        move = new SwapMove<>(Arrays.asList(primaryDescriptor, secondaryDescriptor),
                new TestdataMultiVarEntity("c"), new TestdataMultiVarEntity("d"));
        assertCollectionContainsExactly(move.getVariableNameList(), "primaryValue", "secondaryValue");
        assertCode("c", move.getLeftEntity());
        assertCode("d", move.getRightEntity());
    }

    @Test
    public void toStringTest() {
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataEntity a = new TestdataEntity("a", null);
        TestdataEntity b = new TestdataEntity("b", v1);
        TestdataEntity c = new TestdataEntity("c", v2);
        EntityDescriptor<TestdataSolution> entityDescriptor = TestdataEntity.buildEntityDescriptor();
        List<GenuineVariableDescriptor<TestdataSolution>> variableDescriptorList = entityDescriptor.getGenuineVariableDescriptorList();

        assertEquals("a {null} <-> a {null}", new SwapMove<>(variableDescriptorList, a, a).toString());
        assertEquals("a {null} <-> b {v1}", new SwapMove<>(variableDescriptorList, a, b).toString());
        assertEquals("a {null} <-> c {v2}", new SwapMove<>(variableDescriptorList, a, c).toString());
        assertEquals("b {v1} <-> c {v2}", new SwapMove<>(variableDescriptorList, b, c).toString());
        assertEquals("c {v2} <-> b {v1}", new SwapMove<>(variableDescriptorList, c, b).toString());
    }

    @Test
    public void toStringTestMultiVar() {
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataValue v3 = new TestdataValue("v3");
        TestdataValue v4 = new TestdataValue("v4");
        TestdataOtherValue w1 = new TestdataOtherValue("w1");
        TestdataOtherValue w2 = new TestdataOtherValue("w2");
        TestdataMultiVarEntity a = new TestdataMultiVarEntity("a", null, null, null);
        TestdataMultiVarEntity b = new TestdataMultiVarEntity("b", v1, v3, w1);
        TestdataMultiVarEntity c = new TestdataMultiVarEntity("c", v2, v4, w2);
        EntityDescriptor<TestdataMultiVarSolution> entityDescriptor = TestdataMultiVarEntity.buildEntityDescriptor();
        List<GenuineVariableDescriptor<TestdataMultiVarSolution>> variableDescriptorList = entityDescriptor.getGenuineVariableDescriptorList();

        assertEquals("a {null, null, null} <-> a {null, null, null}", new SwapMove<>(variableDescriptorList, a, a).toString());
        assertEquals("a {null, null, null} <-> b {v1, v3, w1}", new SwapMove<>(variableDescriptorList, a, b).toString());
        assertEquals("a {null, null, null} <-> c {v2, v4, w2}", new SwapMove<>(variableDescriptorList, a, c).toString());
        assertEquals("b {v1, v3, w1} <-> c {v2, v4, w2}", new SwapMove<>(variableDescriptorList, b, c).toString());
        assertEquals("c {v2, v4, w2} <-> b {v1, v3, w1}", new SwapMove<>(variableDescriptorList, c, b).toString());
    }

}
