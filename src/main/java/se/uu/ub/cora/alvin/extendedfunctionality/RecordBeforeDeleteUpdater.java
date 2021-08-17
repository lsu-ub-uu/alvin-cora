/*
 * Copyright 2019, 2020 Uppsala University Library
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
package se.uu.ub.cora.alvin.extendedfunctionality;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.record.RecordUpdater;

public class RecordBeforeDeleteUpdater implements ExtendedFunctionality {

	@Override
	public void useExtendedFunctionality(String authToken, DataGroup spiderDataGroup) {
		DataGroup recordInfo = spiderDataGroup.getFirstGroupWithNameInData("recordInfo");
		String recordType = extractType(recordInfo);
		String recordId = recordInfo.getFirstAtomicValueWithNameInData("id");
		RecordUpdater spiderRecordUpdater = SpiderInstanceProvider.getRecordUpdater();
		spiderRecordUpdater.updateRecord(authToken, recordType, recordId, spiderDataGroup);
	}

	private String extractType(DataGroup recordInfo) {
		DataGroup type = recordInfo.getFirstGroupWithNameInData("type");
		return type.getFirstAtomicValueWithNameInData("linkedRecordId");
	}

}
