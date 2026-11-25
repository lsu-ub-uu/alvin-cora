/*
 * Copyright 2021, 2024 Uppsala University Library
 * Copyright 2024 Olov McKie
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
package se.uu.ub.cora.alvin.extended.migratebinary.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.alvin.spies.spider.SpiderDependencyProviderSpy;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.collected.CollectTerms;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.storage.spies.RecordStorageSpy;

public class MigrateBinaryStorerTest {
	private SpiderDependencyProviderSpy dependencyProvider;
	private RecordStorageSpy recordStorage;
	private MigrateBinaryInfo migrateBinary;
	private DataGroupTermCollectorSpy termCollector;
	private DataRecordLinkCollectorSpy linkCollector;
	private DataRecordGroupHandlerForMigrateBinarySpy dataRecordGroupHandlerForMigrateBinarySpy;
	private DataFactorySpy dataFactory;
	private MigrateBinaryStorer storer;

	@BeforeMethod
	public void setUp() {
		dataFactory = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactory);

		recordStorage = new RecordStorageSpy();
		termCollector = new DataGroupTermCollectorSpy();
		linkCollector = new DataRecordLinkCollectorSpy();
		dependencyProvider = new SpiderDependencyProviderOldSpy();
		dependencyProvider.recordStorage = recordStorage;
		dependencyProvider.termCollector = termCollector;
		dependencyProvider.linkCollector = linkCollector;

		dataRecordGroupHandlerForMigrateBinarySpy = new DataRecordGroupHandlerForMigrateBinarySpy();

		createDefaultMigrateBinary();

		DataRecordGroupSpy migrateBinaryDataGroup = createMigrateBinaryDataGroup();
		recordStorage.MRV.setDefaultReturnValuesSupplier("read", () -> migrateBinaryDataGroup);

		storer = new MigrateBinaryStorerImp(dependencyProvider,
				dataRecordGroupHandlerForMigrateBinarySpy);
	}

	private DataRecordGroupSpy createMigrateBinaryDataGroup() {
		DataRecordGroupSpy dataRecordGroup = new DataRecordGroupSpy();
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getDataDivider",
				() -> "someDataDivider");
		return dataRecordGroup;
	}

	@Test
	public void testCorrectRead() {
		storer.store(migrateBinary);

		recordStorage.MCR.assertParameters("read", 0, "migrateBinary", migrateBinary.batchRecordId);
	}

	@Test
	public void testCorrectCallToTermCollector() {
		storer.store(migrateBinary);

		Map<String, Object> parameters = termCollector.MCR
				.getParametersForMethodAndCallNumber("collectTerms", 0);
		String metadataIdFromTypeHandler = (String) dependencyProvider.recordTypeHandlerSpy.MCR
				.getReturnValue("getDefinitionId", 0);
		assertEquals(parameters.get("metadataId"), metadataIdFromTypeHandler);
		assertSame(parameters.get("dataRecordGroup"), recordStorage.MCR.getReturnValue("read", 0));

		DataRecordGroupSpy returnedDataGroupFromRead = (DataRecordGroupSpy) recordStorage.MCR
				.getReturnValue("read", 0);
		dependencyProvider.MCR.assertParameters("getRecordTypeHandlerUsingDataRecordGroup", 0,
				returnedDataGroupFromRead);
	}

	@Test
	public void testCorrectCallToLinkCollector() {
		storer.store(migrateBinary);

		String metadataIdFromTypeHandler = (String) dependencyProvider.recordTypeHandlerSpy.MCR
				.getReturnValue("getDefinitionId", 0);
		var readMigrateBinary = recordStorage.MCR.getReturnValue("read", 0);
		var convertedMigrateBinary = dataFactory.MCR
				.assertCalledParametersReturn("factorGroupFromDataRecordGroup", readMigrateBinary);
		linkCollector.MCR.assertParameters("collectLinks", 0, metadataIdFromTypeHandler,
				convertedMigrateBinary);
	}

	@Test
	public void testCorrectCallToConverter() {
		storer.store(migrateBinary);

		var readMigrateBinary = recordStorage.MCR.getReturnValue("read", 0);
		dataRecordGroupHandlerForMigrateBinarySpy.MCR.assertParameters("updateDataRecordGroup", 0,
				migrateBinary, readMigrateBinary);
	}

	@Test
	public void testStore() {
		storer.store(migrateBinary);

		Map<String, Object> parameters = recordStorage.MCR
				.getParametersForMethodAndCallNumber("update", 0);
		DataRecordGroupSpy returnedDataGroupFromRead = (DataRecordGroupSpy) recordStorage.MCR
				.getReturnValue("read", 0);
		var convertedMigrateBinary = dataFactory.MCR.assertCalledParametersReturn(
				"factorGroupFromDataRecordGroup", returnedDataGroupFromRead);
		assertSame(parameters.get("dataRecord"), convertedMigrateBinary);
		assertEquals(parameters.get("type"), "migrateBinary");
		assertEquals(parameters.get("id"), "someRecordId");
		linkCollector.MCR.assertReturn("collectLinks", 0, parameters.get("links"));
		CollectTerms collectTerms = (CollectTerms) termCollector.MCR.getReturnValue("collectTerms",
				0);
		assertSame(parameters.get("storageTerms"), collectTerms.storageTerms);
		assertEquals(parameters.get("dataDivider"), "someDataDivider");
	}

	private void createDefaultMigrateBinary() {
		migrateBinary = new MigrateBinaryInfo("someRecordType");
		migrateBinary.numberOfProcessedRecords = 80;
		migrateBinary.batchRecordId = "someRecordId";
		createAndAddErrors();
	}

	private void createAndAddErrors() {
		List<Error> errors = new ArrayList<>();
		errors.add(new Error("someId3", "Error while indexing"));
		errors.add(new Error("someId89", "IOException while indexing"));
		migrateBinary.errors.addAll(errors);
	}
}
