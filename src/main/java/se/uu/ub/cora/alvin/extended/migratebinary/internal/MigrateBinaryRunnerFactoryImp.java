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

import se.uu.ub.cora.alvin.batch.BatchRunner;
import se.uu.ub.cora.alvin.batch.BatchRunnerFactory;
import se.uu.ub.cora.httphandler.HttpHandlerFactoryImp;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;

public class MigrateBinaryRunnerFactoryImp implements BatchRunnerFactory {

	private SpiderDependencyProvider dependencyProvider;

	public MigrateBinaryRunnerFactoryImp(SpiderDependencyProvider dependencyProvider) {
		this.dependencyProvider = dependencyProvider;
	}

	@Override
	public BatchRunner factor(MigrateBinaryInfo migrateInfo) {
		DataRecordGroupHandlerForMigrateBinaryImp dataGroupHandlerForMigrateBinary = new DataRecordGroupHandlerForMigrateBinaryImp();
		MigrateBinaryStorerImp migrateInfoStorer = new MigrateBinaryStorerImp(dependencyProvider,
				dataGroupHandlerForMigrateBinary);
		HttpHandlerFactoryImp httpHandlerFactory = new HttpHandlerFactoryImp();
		return new MigrateBinaryRunner(dependencyProvider, migrateInfoStorer, httpHandlerFactory,
				migrateInfo);
	}

	public SpiderDependencyProvider getDependencyProvider() {
		return dependencyProvider;
	}

}
