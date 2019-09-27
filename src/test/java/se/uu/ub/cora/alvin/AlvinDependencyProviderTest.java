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
package se.uu.ub.cora.alvin;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.alvin.AlvinDependencyProvider;
import se.uu.ub.cora.alvin.extendedfunctionality.AlvinExtendedFunctionalityProvider;
import se.uu.ub.cora.spider.extended.ExtendedFunctionalityProvider;

public class AlvinDependencyProviderTest {

	private AlvinDependencyProvider dependencyProvider;
	private Map<String, String> initInfo;

	@BeforeMethod
	public void setUp() {
		initInfo = new HashMap<>();
		initInfo.put("gatekeeperURL", "someGatekeeperURL");
		initInfo.put("solrURL", "someSolrURL");
		dependencyProvider = new AlvinDependencyProvider(initInfo);

	}

	@Test
	public void testgetExtendedFunctionalityProvider() {
		ExtendedFunctionalityProvider extendedFunctionalityProvider = dependencyProvider
				.getExtendedFunctionalityProvider();
		assertTrue(extendedFunctionalityProvider instanceof AlvinExtendedFunctionalityProvider);
		AlvinExtendedFunctionalityProvider alvinExtended = (AlvinExtendedFunctionalityProvider) extendedFunctionalityProvider;
		assertSame(alvinExtended.getDependencyProvider(), dependencyProvider);
	}

	@Test
	public void testGetInitInfo() {
		Map<String, String> requestedInitInfo = dependencyProvider.getInitInfo();
		assertEquals(requestedInitInfo.size(), 2);
		assertEquals(requestedInitInfo.get("gatekeeperURL"), initInfo.get("gatekeeperURL"));
		assertEquals(requestedInitInfo.get("someSolrURL"), initInfo.get("someSolrURL"));
	}
}
