/*
 * Copyright 2025 Uppsala University Library
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

package se.uu.ub.cora.alvin.extended.record.urn;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.alvin.spies.spider.SpiderDependencyProviderSpy;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityContext;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition;

public class UrnExtendedFunctionalityFactoryTest {

	private static final String ALVIN_RECORD = "alvin-record";
	private ExtendedFunctionalityFactory factory;
	private SpiderDependencyProvider dependencyProvider;

	@BeforeMethod
	public void beforeMethod() throws Exception {
		factory = new UrnExtendedFunctionalityFactory();
		dependencyProvider = new SpiderDependencyProviderSpy();
		factory.initializeUsingDependencyProvider(dependencyProvider);
	}

	@Test
	public void testInit() throws Exception {
		assertTrue(factory instanceof UrnExtendedFunctionalityFactory);
	}

	@Test
	public void testGetExtendedFunctionalityContextsForCreate() {
		assertContext(ExtendedFunctionalityPosition.CREATE_BEFORE_STORE);
	}

	@Test
	public void testFactorCreate() throws Exception {
		List<ExtendedFunctionality> functionalities = factory.factor(
				ExtendedFunctionalityPosition.CREATE_AFTER_METADATA_VALIDATION, ALVIN_RECORD);
		assertTrue(functionalities.get(0) instanceof UrnExtendedFunctionality);
	}

	private void assertContext(ExtendedFunctionalityPosition position) {
		ExtendedFunctionalityContext extFuncContext = tryToFindMatchingContext(position);
		assertEquals(extFuncContext.recordType, ALVIN_RECORD);
		assertEquals(extFuncContext.position, position);
		assertEquals(extFuncContext.runAsNumber, 0);
	}

	private ExtendedFunctionalityContext tryToFindMatchingContext(
			ExtendedFunctionalityPosition position) {
		return factory.getExtendedFunctionalityContexts().stream()
				.filter(contexts -> contexts.position.equals(position)).findFirst().get();
	}
}
