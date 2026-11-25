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

import org.testng.annotations.Test;

import se.uu.ub.cora.alvin.batch.BatchRunnerFactory;
import se.uu.ub.cora.alvin.spies.spider.SpiderDependencyProviderSpy;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.spies.DataFactorySpy;

public class MigrateBinaryFactoryTest {
	private DataFactorySpy dataFactorySpy;
	private SpiderDependencyProviderSpy dependencyProvider;

	@Test
	public void testFactor() {
		dataFactorySpy = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactorySpy);
		dependencyProvider = new SpiderDependencyProviderSpy();
		BatchRunnerFactory factory = new BatchRunnerFactoryImp(dependencyProvider);
		MigrateBinaryInfo migrateBinary = new MigrateBinaryInfo("someRecordType", List.of("23", "45"));

		MigrateBinaryRunner runner = (MigrateBinaryRunner) factory.factor(migrateBinary);

		assertSame(runner.getMigrateBinary(), migrateBinary);
		assertSame(runner.getDependencyProvider(), dependencyProvider);

		assertCorrectMigrateBinaryStorer(runner);
	}

	private void assertCorrectMigrateBinaryStorer(MigrateBinaryRunner runner) {
		MigrateBinaryStorerImp migrateInfoStorer = (MigrateBinaryStorerImp) runner.getMigrateBinaryStorer();
		assertTrue(migrateInfoStorer instanceof MigrateBinaryStorerImp);
		assertTrue(migrateInfoStorer
				.getDataGroupHandlerForMigrateBinary() instanceof DataRecordGroupHandlerForMigrateBinaryImp);
		assertEquals(migrateInfoStorer.getDependencyProvider(), dependencyProvider);
	}

}
