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

import se.uu.ub.cora.alvin.batch.MigrateBatchHandler;
import se.uu.ub.cora.alvin.extended.migratebinary.internal.MigrateBatchHandlerSpy;
import se.uu.ub.cora.alvin.extended.migratebinary.internal.MigrateBinaryInfo;
import se.uu.ub.cora.alvin.spies.spider.SpiderDependencyProviderSpy;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;

public class MigrateExtendedFunctionalityTest {

	private MigrateExtendedFunctionality extFunc;
	private DataRecordGroupSpy dataRecordGroup;
	private ExtendedFunctionalityData data;
	private MigrateBatchHandlerSpy batchHandler;
	private SpiderDependencyProviderSpy dependencyProvider;
	private DataFactorySpy dataFactory;

	@BeforeMethod
	private void beforeMethod() {
		batchHandler = new MigrateBatchHandlerSpy();
		setupDataProvider();
		setupDependencyProvider();
		extFunc = new MigrateExtendedFunctionality(dependencyProvider, batchHandler);
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

		data.dataSharer.put("MigrateBinary_recordIds", List.of("1", "2"));
	}

	@Test
	public void testCallRunMigrateBinary() {
		extFunc.useExtendedFunctionality(data);

		batchHandler.MCR.assertMethodWasCalled("runMigrateBinary");
		MigrateBinaryInfo migrateInfo = (MigrateBinaryInfo) batchHandler.MCR
				.getParameterForMethodAndCallNumberAndParameter("runMigrateBinary", 0, "migrateInfo");

		assertMigrateBinary(migrateInfo);
	}

	private void assertMigrateBinary(MigrateBinaryInfo migrateInfo) {
		assertEquals(migrateInfo.batchRecordId, "someRecordId");
		assertEquals(migrateInfo.totalNumberToProcess, 2);
	}

	@Test
	public void testOnlyForTestGetDependencyProvider() {
		SpiderDependencyProvider dependecyProvider2 = extFunc.onlyForTestGetDependencyProvider();
		assertSame(dependecyProvider2, dependencyProvider);
	}

	@Test
	public void testOnlyForTestGetIndexBatchHandler() {
		MigrateBatchHandler indexBatchHandler2 = extFunc.onlyForTestGetIndexBatchHandler();
		assertSame(indexBatchHandler2, batchHandler);
	}

}
