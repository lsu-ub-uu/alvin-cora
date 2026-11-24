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

import se.uu.ub.cora.alvin.batch.MigrateBatchHandler;
import se.uu.ub.cora.alvin.extended.migratebinary.internal.BatchJob;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;
import se.uu.ub.cora.storage.Filter;

public class MigrateExtendedFunctionality implements ExtendedFunctionality {
	private MigrateBatchHandler batchHandler;
	private SpiderDependencyProvider dependencyProvider;

	public MigrateExtendedFunctionality(SpiderDependencyProvider dependencyProvider,
			MigrateBatchHandler batchHandler) {
		this.dependencyProvider = dependencyProvider;
		this.batchHandler = batchHandler;
	}

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		BatchJob batchJob = fecthAndCreateBatchJob(data);
		batchHandler.runBatchJob(batchJob);
	}

	private BatchJob fecthAndCreateBatchJob(ExtendedFunctionalityData data) {
		DataRecordGroup dataRecordGroup = data.dataRecordGroup;
		long totalNumberToProcess = getTotalNumberToProcess(data);

		return createBatchJob(null, totalNumberToProcess, null, dataRecordGroup.getId());
	}

	private BatchJob createBatchJob(String recordTypeForBatch, long totalNumberOfMatches,
			Filter filter, String id) {
		BatchJob batchJob = new BatchJob(recordTypeForBatch, totalNumberOfMatches, filter);
		batchJob.recordId = id;
		return batchJob;
	}

	private long getTotalNumberToProcess(ExtendedFunctionalityData data) {
		return (long) data.dataSharer.get("MigrateBinary_totalNumberToProcess");
	}

	public SpiderDependencyProvider onlyForTestGetDependencyProvider() {
		return dependencyProvider;
	}

	public MigrateBatchHandler onlyForTestGetIndexBatchHandler() {
		return batchHandler;
	}

}
