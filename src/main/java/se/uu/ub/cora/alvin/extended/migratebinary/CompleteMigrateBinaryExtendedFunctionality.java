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

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;

public class CompleteMigrateBinaryExtendedFunctionality implements ExtendedFunctionality {
	private SpiderDependencyProvider dependencyProvider;

	public CompleteMigrateBinaryExtendedFunctionality(SpiderDependencyProvider dependencyProvider) {
		this.dependencyProvider = dependencyProvider;
	}

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		DataRecordGroup dataRecordGroup = data.dataRecordGroup;
		handleTotalRecords(data, dataRecordGroup);

		setDefaults(dataRecordGroup);
	}

	private void setDefaults(DataRecordGroup dataRecordGroup) {
		dataRecordGroup.addChild(createAtomicWithZeroValue("numberOfProcessedRecords"));
		dataRecordGroup.addChild(createAtomicWithZeroValue("numberOfMigratedBinaries"));
		dataRecordGroup.addChild(createAtomicWithZeroValue("amountOfMigratedData"));
		dataRecordGroup.addChild(createAtomic("status", "started"));
	}

	private void handleTotalRecords(ExtendedFunctionalityData data,
			DataRecordGroup dataRecordGroup) {
		List<DataAtomic> alvinRecords = dataRecordGroup.getAllDataAtomicsWithNameInData("recordId");
		int totalNumberOfMatches = alvinRecords.size();
		DataAtomic totalAtomic = createAtomic("totalNumberToProcess",
				String.valueOf(totalNumberOfMatches));
		dataRecordGroup.addChild(totalAtomic);
		data.dataSharer.put("MigrateBinary_totalNumberToProcess", totalNumberOfMatches);

		List<String> recordIds = alvinRecords.stream().map(DataAtomic::getValue).toList();
		data.dataSharer.put("MigrateBinary_recordIds", recordIds);
	}

	private DataAtomic createAtomic(String nameInData, String value) {
		return DataProvider.createAtomicUsingNameInDataAndValue(nameInData, value);
	}

	private DataChild createAtomicWithZeroValue(String nameInData) {
		return DataProvider.createAtomicUsingNameInDataAndValue(nameInData, String.valueOf(0));
	}

	public SpiderDependencyProvider onlyForTestGetDependencyProvider() {
		return dependencyProvider;
	}

}
