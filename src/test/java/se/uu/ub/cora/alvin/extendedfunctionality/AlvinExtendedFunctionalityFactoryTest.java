/*
 * Copyright 2020 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.alvin.extendedfunctionality;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.DELETE_BEFORE;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityContext;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;

public class AlvinExtendedFunctionalityFactoryTest {

	private ExtendedFunctionalityFactory factory;

	@BeforeMethod
	public void setUp() {
		factory = new AlvinExtendedFunctionalityFactory();
		factory.initializeUsingDependencyProvider(null);
	}

	@Test
	public void testInit() {
		assertEquals(factory.getExtendedFunctionalityContexts().size(), 1);
		assertCorrectContextUsingIndexAndRecordType(0, "place");
	}

	private void assertCorrectContextUsingIndexAndRecordType(int index, String recordType) {
		ExtendedFunctionalityContext updateBefore = factory.getExtendedFunctionalityContexts()
				.get(index);
		assertEquals(updateBefore.position, DELETE_BEFORE);
		assertEquals(updateBefore.recordType, recordType);
		assertEquals(updateBefore.runAsNumber, 0);
	}

	@Test
	public void factorCommonOrganisationUpdateAfter() {
		List<ExtendedFunctionality> functionalities = factory.factor(DELETE_BEFORE, "place");
		assertCorrectFactoredFunctionalities(functionalities);
	}

	private void assertCorrectFactoredFunctionalities(List<ExtendedFunctionality> functionalities) {
		assertEquals(functionalities.size(), 1);
		assertTrue(functionalities.get(0) instanceof RecordBeforeDeleteUpdater);
	}
}
