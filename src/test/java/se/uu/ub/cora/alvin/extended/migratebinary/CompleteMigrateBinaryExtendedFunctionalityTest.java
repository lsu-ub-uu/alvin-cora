/*
 * Copyright 2026 Uppsala University Library
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
package se.uu.ub.cora.alvin.extended.migratebinary;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.alvin.spies.spider.SpiderDependencyProviderSpy;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.spies.DataAtomicSpy;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;

public class CompleteMigrateBinaryExtendedFunctionalityTest {

	private CompleteMigrateBinaryExtendedFunctionality extFunc;
	private DataRecordGroupSpy dataRecordGroup;
	private ExtendedFunctionalityData data;
	private SpiderDependencyProviderSpy dependencyProvider;
	private DataFactorySpy dataFactory;

	@BeforeMethod
	private void beforeMethod() {
		setupDataProvider();
		setupDependencyProvider();
		extFunc = new CompleteMigrateBinaryExtendedFunctionality(dependencyProvider);
		setData();
	}

	private void setupDataProvider() {
		dataFactory = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactory);
	}

	public void setupDependencyProvider() {
		dependencyProvider = new SpiderDependencyProviderSpy();
	}

	private void setData() {
		dataRecordGroup = new DataRecordGroupSpy();
		data = new ExtendedFunctionalityData();
		data.dataRecordGroup = dataRecordGroup;

		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getId", () -> "someRecordId");
		DataAtomicSpy atomic1 = new DataAtomicSpy();
		atomic1.MRV.setDefaultReturnValuesSupplier("getValue", () -> "1");

		DataAtomicSpy atomic2 = new DataAtomicSpy();
		atomic2.MRV.setDefaultReturnValuesSupplier("getValue", () -> "2");

		List<DataAtomic> recordIds = List.of(atomic1, atomic2);
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getAllDataAtomicsWithNameInData",
				() -> recordIds, "recordId");
	}

	@Test
	public void testCalculateTotalNoAndPassWithDataSharer() {
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData",
				() -> true, "filter");
		DataGroup filterGroup = new DataGroupSpy();
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstGroupWithNameInData",
				() -> filterGroup, "filter");

		extFunc.useExtendedFunctionality(data);

		assertTotalNoOfMatches();
	}

	private void assertTotalNoOfMatches() {
		var totalAtomic = dataFactory.MCR.assertCalledParametersReturn(
				"factorAtomicUsingNameInDataAndValue", "totalNumberToProcess", "2");
		dataRecordGroup.MCR.assertCalledParameters("addChild", totalAtomic);

		assertEquals(data.dataSharer.get("MigrateBinary_recordIds"), List.of("1", "2"));
	}

	@Test
	public void testDefaultValuesSet() {
		extFunc.useExtendedFunctionality(data);

		var numberOfProcessedRecords = dataFactory.MCR.assertCalledParametersReturn(
				"factorAtomicUsingNameInDataAndValue", "numberOfProcessedRecords", "0");
		dataRecordGroup.MCR.assertCalledParameters("addChild", numberOfProcessedRecords);

		var numberOfMigratedBinaries = dataFactory.MCR.assertCalledParametersReturn(
				"factorAtomicUsingNameInDataAndValue", "numberOfMigratedBinaries", "0");
		dataRecordGroup.MCR.assertCalledParameters("addChild", numberOfMigratedBinaries);

		var amountOfMigratedData = dataFactory.MCR.assertCalledParametersReturn(
				"factorAtomicUsingNameInDataAndValue", "amountOfMigratedData", "0");
		dataRecordGroup.MCR.assertCalledParameters("addChild", amountOfMigratedData);
	}

	@Test
	public void testStatusValuesSet() {
		extFunc.useExtendedFunctionality(data);

		var status = dataFactory.MCR.assertCalledParametersReturn(
				"factorAtomicUsingNameInDataAndValue", "status", "started");
		dataRecordGroup.MCR.assertCalledParameters("addChild", status);
	}

	@Test
	public void testOnlyForTestGetDependencyProvider() {
		SpiderDependencyProvider dependecyProvider2 = extFunc.onlyForTestGetDependencyProvider();
		assertSame(dependecyProvider2, dependencyProvider);
	}
}
