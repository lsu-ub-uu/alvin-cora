/*
 * Copyright 2021 Uppsala University Library
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
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.alvin.batch.BatchRunnerFactory;
import se.uu.ub.cora.alvin.spies.spider.SpiderDependencyProviderSpy;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.httphandler.HttpHandlerFactoryImp;
import se.uu.ub.cora.storage.spies.RecordStorageSpy;

public class MigrateBinaryRunnerFactoryTest {
	private DataFactorySpy dataFactorySpy;
	private SpiderDependencyProviderSpy dependencyProvider;
	private RecordStorageSpy recordStorage;
	private MigrateBinaryInfo migrateInfo;
	private BatchRunnerFactory factory;

	@BeforeMethod
	public void beforeMethod() {
		setUpDataProvider();
		setUpDependencyProvider();
		migrateInfo = new MigrateBinaryInfo("someRecordType", List.of("23", "45"));
		factory = new MigrateBinaryRunnerFactoryImp(dependencyProvider);
	}

	private void setUpDataProvider() {
		dataFactorySpy = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactorySpy);
	}

	private void setUpDependencyProvider() {
		recordStorage = new RecordStorageSpy();
		dependencyProvider = new SpiderDependencyProviderSpy();
		dependencyProvider.MRV.setDefaultReturnValuesSupplier("getRecordStorage",
				() -> recordStorage);
	}

	@Test
	public void testFactorSimpleDependencies() {
		MigrateBinaryRunner runner = (MigrateBinaryRunner) factory.factor(migrateInfo);

		assertSame(runner.onlyForTestGetDependencyProvider(), dependencyProvider);
		assertTrue(runner.onlyForTestGetHttpHandlerFactory() instanceof HttpHandlerFactoryImp);
		assertSame(runner.onlyForTestGetMigrateBinaryInfo(), migrateInfo);
	}

	@Test
	public void testFactor_MigrateBinaryStorer() {
		MigrateBinaryRunner runner = (MigrateBinaryRunner) factory.factor(migrateInfo);

		MigrateBinaryStorerImp migrateInfoStorer = (MigrateBinaryStorerImp) runner
				.onlyForTestGetMigrateBinaryStorer();
		assertTrue(migrateInfoStorer instanceof MigrateBinaryStorerImp);
		assertTrue(migrateInfoStorer
				.getDataGroupHandlerForMigrateBinary() instanceof DataRecordGroupHandlerForMigrateBinaryImp);
		assertEquals(migrateInfoStorer.getDependencyProvider(), dependencyProvider);
	}
}
