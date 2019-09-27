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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.alvin.AlvinInitializationException;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.messaging.ChannelInfo;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extended.ExtendedFunctionality;

public class AlvinExtendedFunctionalityProviderTest {

	private AlvinExtendedFunctionalityProvider functionalityProvider;
	private SpiderDependencyProvider dependencyProvider;
	private Map<String, String> initInfo;
	private LoggerFactorySpy loggerFactorySpy;
	private String testedClassName = "AlvinExtendedFunctionalityProvider";

	@BeforeMethod
	public void setUp() {
		initInfo = new HashMap<>();
		initInfo.put("messageServerHostname", "someHostname");
		initInfo.put("messageServerPort", "somePort");
		initInfo.put("messageChannel", "someChannel");

		setUpFunctionalityProvider();
	}

	private void setUpFunctionalityProvider() {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
		dependencyProvider = new DependencyProviderSpy(initInfo);
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

	@Test
	public void testEnsureListIsRealList() {
		assertTrue(functionalityProvider
				.ensureListExists(Collections.emptyList()) instanceof ArrayList);
		List<ExtendedFunctionality> list = new ArrayList<>();
		list.add(null);
		assertEquals(functionalityProvider.ensureListExists(list), list);
	}

	@Test
	public void testFunctionalityForCreateBeforeReturnWhenNotImplementedForRecordType() {
		List<ExtendedFunctionality> functionalityList = functionalityProvider
				.getFunctionalityForCreateBeforeReturn("someRecordType");
		assertEquals(functionalityList, Collections.emptyList());
	}

	@Test
	public void testFunctionalityForCreateBeforeReturnForPlace() {
		List<ExtendedFunctionality> functionalityList = functionalityProvider
				.getFunctionalityForCreateBeforeReturn("place");

		assertEquals(functionalityList.size(), 1);
		assertTrue(functionalityList.get(0) instanceof AlvinRecordIndexer);
		AlvinRecordIndexer alvinRecordIndexer = (AlvinRecordIndexer) functionalityList.get(0);
		ChannelInfo channelInfo = alvinRecordIndexer.getChannelInfo();
		assertEquals(channelInfo.hostname, initInfo.get("messageServerHostname"));
		assertEquals(channelInfo.port, initInfo.get("messageServerPort"));
		assertEquals(channelInfo.channel, initInfo.get("messageChannel"));
	}

	@Test
	public void testLoggingAndErrorIfMissingParameterMessageServerHostname() {
		assertCorrectErrorAndLogOnMissingParameter("messageServerHostname", 0);
	}

	private void assertCorrectErrorAndLogOnMissingParameter(String parameter,
			int noOfInfoMessages) {
		initInfo.remove(parameter);
		setUpFunctionalityProvider();
		String errorMessage = "InitInfo must contain " + parameter;
		try {
			functionalityProvider.getFunctionalityForCreateBeforeReturn("place");
		} catch (Exception e) {
			assertTrue(e instanceof AlvinInitializationException);
			assertEquals(e.getMessage(), errorMessage);

		}
		assertEquals(loggerFactorySpy.getNoOfInfoLogMessagesUsingClassName(testedClassName),
				noOfInfoMessages);
		assertEquals(loggerFactorySpy.getFatalLogMessageUsingClassNameAndNo(testedClassName, 0),
				errorMessage);
		assertEquals(loggerFactorySpy.getNoOfFatalLogMessagesUsingClassName(testedClassName), 1);
	}

	@Test
	public void testLoggingAndErrorIfMissingParameterMessageServerPort() {
		assertCorrectErrorAndLogOnMissingParameter("messageServerPort", 1);
	}

	@Test
	public void testLoggingAndErrorIfMissingParameterMessageChannel() {
		assertCorrectErrorAndLogOnMissingParameter("messageChannel", 2);
	}

}
