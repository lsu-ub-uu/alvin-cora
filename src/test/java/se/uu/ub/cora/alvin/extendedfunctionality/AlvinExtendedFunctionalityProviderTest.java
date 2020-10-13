/*
 * Copyright 2019 Uppsala University Library
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extended.ExtendedFunctionality;

public class AlvinExtendedFunctionalityProviderTest {

	private AlvinExtendedFunctionalityProvider functionalityProvider;
	private SpiderDependencyProvider dependencyProvider;

	@BeforeMethod
	public void setUp() {
		dependencyProvider = new DependencyProviderSpy(new HashMap<>());
		RecordStorageProviderSpy storageProvider = new RecordStorageProviderSpy();
		dependencyProvider.setRecordStorageProvider(storageProvider);
		functionalityProvider = new AlvinExtendedFunctionalityProvider(dependencyProvider);
	}

	@Test
	public void testFunctionalityBeforeDeleteWhenNotImplementedForRecordType() {
		List<ExtendedFunctionality> functionalityList = functionalityProvider
				.getFunctionalityBeforeDelete("someRecordType");
		assertEquals(functionalityList, Collections.emptyList());
	}

	@Test
	public void testFunctionalityBeforeDeleteForPlace() {
		List<ExtendedFunctionality> functionalityList = functionalityProvider
				.getFunctionalityBeforeDelete("place");

		assertEquals(functionalityList.size(), 1);
		assertTrue(functionalityList.get(0) instanceof RecordBeforeDeleteUpdater);

	}
}
