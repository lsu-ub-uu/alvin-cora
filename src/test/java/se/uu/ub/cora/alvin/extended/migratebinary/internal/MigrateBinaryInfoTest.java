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

import java.util.Collections;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MigrateBinaryInfoTest {
	private MigrateBinaryInfo migrateInfo;
	private List<String> recordIds;
	private String batchRecordId;
	private String userId;

	@BeforeMethod
	public void beforeMethod() {
		batchRecordId = "someBatchRecordId";
		recordIds = List.of("1", "44");
		userId = "someUserId";

		migrateInfo = new MigrateBinaryInfo(batchRecordId, recordIds, userId);
	}

	@Test
	public void testValuesSetFromConstructor() {
		assertEquals(migrateInfo.batchRecordId, batchRecordId);
		assertEquals(migrateInfo.totalNumberToProcess, recordIds.size());
		assertEquals(migrateInfo.userId, userId);

		assertEquals(migrateInfo.numberOfProcessedRecords, 0);
		assertEquals(migrateInfo.status, "started");
		assertEquals(migrateInfo.errors, Collections.EMPTY_LIST);
		assertEquals(migrateInfo.numberOfMigratedBinaries, 0);
		assertEquals(migrateInfo.amountOfMigratedData, 0);
	}

	@Test
	public void testOtherValues() {
		migrateInfo.numberOfProcessedRecords = 33;
		assertEquals(migrateInfo.numberOfProcessedRecords, 33);

		migrateInfo.numberOfMigratedBinaries = 343;
		assertEquals(migrateInfo.numberOfMigratedBinaries, 343);

		migrateInfo.amountOfMigratedData = 34333;
		assertEquals(migrateInfo.amountOfMigratedData, 34333);
		//
		// recordId(textVariable, 1-1, noConstraint)
		// message(textVariable, 1-1, noConstraint)
		//
		// status(collectionVariable, 1-1, noConstraint)
	}
}
