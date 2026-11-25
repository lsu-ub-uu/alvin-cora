/*
 * Copyright 2021, 2023, 2025 Uppsala University Library
 * Copyright 2024 Olov McKie
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataParent;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;

public class DataRecordGroupHandlerForMigrateBinaryImp
		implements DataRecordGroupHandlerForMigrateBinary {
	private static final String NUM_OF_PROCESSED_RECORDS = "numberOfProcessedRecords";
	private static final String ERROR = "error";
	private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";
	private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter
			.ofPattern(DATE_TIME_PATTERN);

	@Override
	public void updateDataRecordGroup(MigrateBinaryInfo migrateBinary,
			DataRecordGroup existingMigrateBinary) {
		replaceUpdateRecordInfo(existingMigrateBinary);
		updateTotalNumberToProcess(migrateBinary, existingMigrateBinary);
		updateNumOfProcessedRecords(migrateBinary, existingMigrateBinary);
		updateNumberOfMigratedBinaries(migrateBinary, existingMigrateBinary);
		updateAmountOfMigratedData(migrateBinary, existingMigrateBinary);
		replaceIndexErrors(migrateBinary, existingMigrateBinary);
		updateStatus(migrateBinary, existingMigrateBinary);
	}

	private void updateTotalNumberToProcess(MigrateBinaryInfo migrateBinary,
			DataRecordGroup existingMigrateBinary) {
		replaceAtomicChild(existingMigrateBinary, "totalNumberToProcess",
				String.valueOf(migrateBinary.totalNumberToProcess));
	}

	private void updateNumOfProcessedRecords(MigrateBinaryInfo migrateBinary,
			DataRecordGroup existingMigrateBinary) {
		replaceAtomicChild(existingMigrateBinary, NUM_OF_PROCESSED_RECORDS,
				String.valueOf(migrateBinary.numberOfProcessedRecords));
	}

	private void updateNumberOfMigratedBinaries(MigrateBinaryInfo migrateBinary,
			DataRecordGroup existingMigrateBinary) {
		replaceAtomicChild(existingMigrateBinary, "numberOfMigratedBinaries",
				String.valueOf(migrateBinary.numberOfMigratedBinaries));
	}

	private void updateAmountOfMigratedData(MigrateBinaryInfo migrateBinary,
			DataRecordGroup existingMigrateBinary) {
		replaceAtomicChild(existingMigrateBinary, "amountOfMigratedData",
				String.valueOf(migrateBinary.amountOfMigratedData));
	}

	private void replaceAtomicChild(DataRecordGroup existingMigrateBinary, String nameInData,
			String value) {
		existingMigrateBinary.removeFirstChildWithNameInData(nameInData);
		addAtomicValueToDataGroup(nameInData, value, existingMigrateBinary);
	}

	private void addAtomicValueToDataGroup(String nameInData, String value,
			DataParent existingMigrateBinary) {
		DataAtomic atomic = DataProvider.createAtomicUsingNameInDataAndValue(nameInData, value);
		existingMigrateBinary.addChild(atomic);
	}

	private void replaceIndexErrors(MigrateBinaryInfo migrateBinary,
			DataParent existingMigrateBinary) {
		existingMigrateBinary.removeAllChildrenWithNameInData(ERROR);
		int repeatId = initilizeRepeatIdForError(existingMigrateBinary);
		for (Error indexError : migrateBinary.errors) {
			convertIndexErrorAndAddToDataGroup(existingMigrateBinary, indexError, repeatId);
			repeatId++;
		}
	}

	private int initilizeRepeatIdForError(DataParent existingMigrateBinary) {
		return (existingMigrateBinary.getAllGroupsWithNameInData(ERROR).size()) + 1;
	}

	private void convertIndexErrorAndAddToDataGroup(DataParent existingMigrateBinary,
			Error indexError, int repeatId) {
		DataGroup error = DataProvider.createGroupUsingNameInData(ERROR);
		existingMigrateBinary.addChild(error);
		error.setRepeatId(String.valueOf(repeatId));
		addAtomicValueToDataGroup("recordId", indexError.recordId, error);
		addAtomicValueToDataGroup("message", indexError.message, error);
	}

	private void updateStatus(MigrateBinaryInfo migrateBinary,
			DataRecordGroup existingMigrateBinary) {
		replaceAtomicChild(existingMigrateBinary, "status", migrateBinary.status);
	}

	private void replaceUpdateRecordInfo(DataRecordGroup existingMigrateBinary) {
		DataGroup recordInfo = existingMigrateBinary.getFirstGroupWithNameInData("recordInfo");
		replaceUpdatedDataGroup(recordInfo);
	}

	private void replaceUpdatedDataGroup(DataGroup recordInfo) {
		recordInfo.removeAllChildrenWithNameInData("updated");
		DataGroup updatedDataGroup = DataProvider.createGroupUsingNameInData("updated");
		createAndAddUpdatedBy(updatedDataGroup);
		createAndAddTsUpdated(updatedDataGroup);
		updatedDataGroup.setRepeatId("0");
		recordInfo.addChild(updatedDataGroup);
	}

	private void createAndAddUpdatedBy(DataGroup updatedDataGroup) {
		DataRecordLink updatedBy = DataProvider
				.createRecordLinkUsingNameInDataAndTypeAndId("updatedBy", "user", "system");
		updatedDataGroup.addChild(updatedBy);
	}

	private void createAndAddTsUpdated(DataGroup updatedDataGroup) {
		DataAtomic tsUpdated = DataProvider.createAtomicUsingNameInDataAndValue("tsUpdated",
				getCurrentFormattedTime());
		updatedDataGroup.addChild(tsUpdated);
	}

	private static String getCurrentFormattedTime() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		return currentDateTime.format(dateTimeFormatter);
	}

}
