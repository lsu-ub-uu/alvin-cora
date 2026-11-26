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

import java.util.List;

import se.uu.ub.cora.alvin.batch.MigrateBatchHandler;
import se.uu.ub.cora.alvin.extended.migratebinary.internal.MigrateBinaryInfo;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;

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
		MigrateBinaryInfo migrateInfo = fecthInfoFromCompleteExtendedFunctionallity(data);
		batchHandler.runMigrateBinary(migrateInfo);
	}

	private MigrateBinaryInfo fecthInfoFromCompleteExtendedFunctionallity(
			ExtendedFunctionalityData data) {
		DataRecordGroup dataRecordGroup = data.dataRecordGroup;

		List<String> recordIds = (List<String>) data.dataSharer.get("MigrateBinary_recordIds");
		String userId = data.user.id;
		return new MigrateBinaryInfo(dataRecordGroup.getId(), recordIds, userId);
	}

	public SpiderDependencyProvider onlyForTestGetDependencyProvider() {
		return dependencyProvider;
	}

	public MigrateBatchHandler onlyForTestGetIndexBatchHandler() {
		return batchHandler;
	}

}
