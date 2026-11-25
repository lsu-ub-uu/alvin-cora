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
		updateNumOfProcessedRecordsInDataGroup(migrateBinary, existingMigrateBinary);
		addIndexErrorsToDataGroup(migrateBinary, existingMigrateBinary);
		updateStatusWhenJobbIsFinished(migrateBinary, existingMigrateBinary);
		addUpdateRecordInfo(existingMigrateBinary);
	}

	private void updateNumOfProcessedRecordsInDataGroup(MigrateBinaryInfo migrateBinary,
			DataRecordGroup existingMigrateBinary) {
		replaceAtomicChild(existingMigrateBinary, NUM_OF_PROCESSED_RECORDS,
				String.valueOf(migrateBinary.numberOfProcessedRecords));
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

	private void addIndexErrorsToDataGroup(MigrateBinaryInfo migrateBinary,
			DataParent existingMigrateBinary) {
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

	private void updateStatusWhenJobbIsFinished(MigrateBinaryInfo migrateBinary,
			DataRecordGroup existingMigrateBinary) {
		replaceAtomicChild(existingMigrateBinary, "status", String.valueOf(migrateBinary.status));
	}

	private void addUpdateRecordInfo(DataRecordGroup existingMigrateBinary) {
		DataGroup recordInfo = existingMigrateBinary.getFirstGroupWithNameInData("recordInfo");
		addUpdatedDataGroup(recordInfo);
	}

	private void addUpdatedDataGroup(DataGroup recordInfo) {
		DataGroup updatedDataGroup = DataProvider.createGroupUsingNameInData("updated");
		createAndAddUpdatedBy(updatedDataGroup);
		createAndAddTsUpdated(updatedDataGroup);
		setRepeatIdOnUpdated(recordInfo, updatedDataGroup);
		recordInfo.addChild(updatedDataGroup);
	}

	private void setRepeatIdOnUpdated(DataGroup recordInfo, DataGroup updatedDataGroup) {
		int numberOfUpdated = recordInfo.getAllGroupsWithNameInData("updated").size();
		updatedDataGroup.setRepeatId(String.valueOf(numberOfUpdated + 1));
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

	@Override
	public DataRecordGroup createDataRecordGroup(MigrateBinaryInfo migrateBinary,
			DataGroup filterAsDataGroup) {
		DataRecordGroup dataRecordGroup = DataProvider
				.createRecordGroupUsingNameInData("migrateBinary");
		addRecordInfo(dataRecordGroup);
		addRecordTypeToIndex(migrateBinary, dataRecordGroup);
		addRecordStatus(migrateBinary, dataRecordGroup);
		addNumberOfProcessedRecords(migrateBinary, dataRecordGroup);
		addTotalNumberToIndex(migrateBinary, dataRecordGroup);
		possiblyAddFilter(dataRecordGroup, filterAsDataGroup);
		addIndexErrorsToDataGroup(migrateBinary, dataRecordGroup);
		return dataRecordGroup;
	}

	private void addRecordInfo(DataRecordGroup dataRecordGroup) {
		dataRecordGroup.setDataDivider("coraData");
		dataRecordGroup.setValidationType("migrateBinary");
	}

	private void addRecordTypeToIndex(MigrateBinaryInfo migrateBinary,
			DataRecordGroup dataRecordGroup) {
		addAtomicValueToDataGroup("recordTypeToIndex", migrateBinary.recordTypeToIndex,
				dataRecordGroup);
	}

	private void addRecordStatus(MigrateBinaryInfo migrateBinary, DataRecordGroup dataRecordGroup) {
		addAtomicValueToDataGroup("status", migrateBinary.status, dataRecordGroup);
	}

	private void addNumberOfProcessedRecords(MigrateBinaryInfo migrateBinary,
			DataRecordGroup dataRecordGroup) {
		long numOfProcessedRecords = migrateBinary.numberOfProcessedRecords;
		addAtomicValueToDataGroup(NUM_OF_PROCESSED_RECORDS, String.valueOf(numOfProcessedRecords),
				dataRecordGroup);
	}

	private void addTotalNumberToIndex(MigrateBinaryInfo migrateBinary,
			DataRecordGroup dataRecordGroup) {
		long totalNumberToIndex = migrateBinary.totalNumberToProcess;
		addAtomicValueToDataGroup("totalNumberToIndex", String.valueOf(totalNumberToIndex),
				dataRecordGroup);
	}

	private void possiblyAddFilter(DataRecordGroup dataRecordGroup, DataGroup filterAsDataGroup) {
		if (filterAsDataGroup.hasChildren()) {
			dataRecordGroup.addChild(filterAsDataGroup);
		}
	}
}
