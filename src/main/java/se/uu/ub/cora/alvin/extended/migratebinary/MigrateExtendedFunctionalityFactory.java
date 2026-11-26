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

import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.CREATE_BEFORE_ENHANCE;
import static se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition.CREATE_BEFORE_STORE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.uu.ub.cora.alvin.batch.BatchRunnerFactory;
import se.uu.ub.cora.alvin.batch.MigrateBatchHandler;
import se.uu.ub.cora.alvin.extended.migratebinary.internal.MigrateBinaryRunnerFactoryImp;
import se.uu.ub.cora.alvin.extended.migratebinary.internal.MigrateBatchHandlerImp;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityContext;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition;

public class MigrateExtendedFunctionalityFactory implements ExtendedFunctionalityFactory {

	private List<ExtendedFunctionalityContext> contexts = new ArrayList<>();
	private SpiderDependencyProvider dependencyProvider;

	@Override
	public void initializeUsingDependencyProvider(SpiderDependencyProvider dependencyProvider) {
		this.dependencyProvider = dependencyProvider;
		createListOfContexts();
	}

	private void createListOfContexts() {
		createContext(CREATE_BEFORE_STORE);
		createContext(CREATE_BEFORE_ENHANCE);
	}

	private void createContext(ExtendedFunctionalityPosition position) {
		contexts.add(new ExtendedFunctionalityContext(position, "migrateBinary", 0));
	}

	@Override
	public List<ExtendedFunctionalityContext> getExtendedFunctionalityContexts() {
		return contexts;
	}

	@Override
	public List<ExtendedFunctionality> factor(ExtendedFunctionalityPosition position,
			String recordType) {
		if (CREATE_BEFORE_STORE.equals(position)) {
			return createCalculateNumberToIndex();
		}
		return createRunMigrateBinary();
	}

	private List<ExtendedFunctionality> createCalculateNumberToIndex() {
		return Collections
				.singletonList(new CompleteMigrateBinaryExtendedFunctionality(dependencyProvider));
	}

	private List<ExtendedFunctionality> createRunMigrateBinary() {
		BatchRunnerFactory batchRunnerFactory = new MigrateBinaryRunnerFactoryImp(dependencyProvider);
		MigrateBatchHandler indexBatchHandler = MigrateBatchHandlerImp
				.usingBatchRunnerFactory(batchRunnerFactory);
		return Collections.singletonList(
				new MigrateExtendedFunctionality(dependencyProvider, indexBatchHandler));
	}

}
