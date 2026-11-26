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

import java.util.ArrayList;
import java.util.List;

public class MigrateBinaryInfo {

	public String batchRecordId;
	public List<String> recordIds;
	public long totalNumberToProcess = 0;
	public long numberOfProcessedRecords = 0;
	public int numberOfMigratedBinaries = 0;
	public long amountOfMigratedData = 0;
	public String status = "started";
	public List<Error> errors = new ArrayList<>();
	public String userId;

	public MigrateBinaryInfo(String batchRecordId, List<String> recordIds, String userId) {
		this.batchRecordId = batchRecordId;
		this.recordIds = recordIds;
		this.userId = userId;
		this.totalNumberToProcess = recordIds.size();
	}
}
