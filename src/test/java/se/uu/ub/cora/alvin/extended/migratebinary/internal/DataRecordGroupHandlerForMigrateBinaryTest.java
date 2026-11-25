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

import static org.testng.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.spies.DataAtomicSpy;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;

public class DataRecordGroupHandlerForMigrateBinaryTest {
	private static final int ERROR_NUMBER_TO_GET_FACTORED = 1;
	private static final int TS_UPDATED_NUMBER_TO_GET_FACTORED = 0;
	private static final String SOME_MIGRATE_BINARY_RECORD_ID = "someRecordId";
	private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";
	private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter
			.ofPattern(DATE_TIME_PATTERN);
	private MigrateBinaryInfo migrateBinary;
	private DataRecordGroupHandlerForMigrateBinaryImp dataGroupHandler;
	private DataFactorySpy dataFactorySpy;
	private List<String> recordIds;
	private DataRecordGroupSpy existingMigrateBinary;
	private DataGroupSpy recordInfoG;

	@BeforeMethod
	public void beforeMethod() {
		recordIds = List.of("12", "23");
		setUpProviders();

		migrateBinary = createMigrateBinary();
		dataGroupHandler = new DataRecordGroupHandlerForMigrateBinaryImp();

		existingMigrateBinary = new DataRecordGroupSpy();

		// setListOfErrors
		List<DataGroupSpy> listOfErrorsSpies = List.of(new DataGroupSpy(), new DataGroupSpy());
		existingMigrateBinary.MRV.setReturnValues("getAllGroupsWithNameInData",
				List.of(listOfErrorsSpies), "error");

		recordInfoG = new DataGroupSpy();
		existingMigrateBinary.MRV.setSpecificReturnValuesSupplier("getFirstGroupWithNameInData",
				() -> recordInfoG, "recordInfo");
	}

	private void setUpProviders() {
		dataFactorySpy = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactorySpy);
	}

	private MigrateBinaryInfo createMigrateBinary() {
		MigrateBinaryInfo newMigrateBinary = new MigrateBinaryInfo(SOME_MIGRATE_BINARY_RECORD_ID,
				recordIds);
		createAndAddErrors(newMigrateBinary);
		return newMigrateBinary;
	}

	private void createAndAddErrors(MigrateBinaryInfo migrateBinary) {
		List<Error> errors = new ArrayList<>();
		errors.add(new Error("recordIdOne", "some index error message"));
		errors.add(new Error("recordIdTwo", "some other index error message"));
		migrateBinary.errors = errors;
	}

	String dataGroupLayout = """
			migrateBinary
				recordInfo
					createdBy user someSuperUser
					tsCreated 2021-05-03T14:12:11.657482Z
					updated
						tsUpdated 2021-06-02T14:45:11.657482Z
						updatedBy user someUserId
				recordId migrateBinary:123123123123
				totalNumberToProcess 34
				numberOfProcessedRecords 34
				numberOfMigratedBinaries 34
				amountOfMigratedData 3423434434
				status someStatus
				error
					recordId someRecordId
					message some read error message
				""";

	@Test
	public void testTotalNumberToProcess() {
		dataGroupHandler.updateDataRecordGroup(migrateBinary, existingMigrateBinary);

		assertRemoveAndAdded("totalNumberToProcess", migrateBinary.totalNumberToProcess);
	}

	@Test
	public void testNumberOfProcessedRecords() {
		dataGroupHandler.updateDataRecordGroup(migrateBinary, existingMigrateBinary);

		assertRemoveAndAdded("numberOfProcessedRecords", migrateBinary.numberOfProcessedRecords);
	}

	@Test
	public void testNumberOfMigratedBinaries() {
		dataGroupHandler.updateDataRecordGroup(migrateBinary, existingMigrateBinary);

		assertRemoveAndAdded("numberOfMigratedBinaries", migrateBinary.numberOfMigratedBinaries);
	}

	@Test
	public void testAmountOfMigratedData() {
		dataGroupHandler.updateDataRecordGroup(migrateBinary, existingMigrateBinary);

		assertRemoveAndAdded("amountOfMigratedData", migrateBinary.amountOfMigratedData);
	}

	@Test
	public void testStatus() {
		dataGroupHandler.updateDataRecordGroup(migrateBinary, existingMigrateBinary);

		assertRemoveAndAdded("status", migrateBinary.status);
	}

	@Test
	public void testErrors() {
		dataGroupHandler.updateDataRecordGroup(migrateBinary, existingMigrateBinary);

		existingMigrateBinary.MCR.assertCalledParameters("removeAllChildrenWithNameInData",
				"error");
		assertErrorsFromMigrateBinaryAddedToGroup(existingMigrateBinary);
	}

	private void assertErrorsFromMigrateBinaryAddedToGroup(
			DataRecordGroupSpy existingMigrateBinary) {
		int repeatId = migrateBinary.errors.size() + 1;
		int errorNo = ERROR_NUMBER_TO_GET_FACTORED;
		for (Error indexError : migrateBinary.errors) {
			DataGroupSpy errorGroup = assertFactoredGroupWithNameAndRepeatId(errorNo, "error",
					String.valueOf(repeatId), existingMigrateBinary);

			assertFactoredAtomicWithNameAndValueAddedToGroupAsNo("recordId", indexError.recordId,
					errorGroup);
			assertFactoredAtomicWithNameAndValueAddedToGroupAsNo("message", indexError.message,
					errorGroup);
			repeatId++;
			errorNo++;
		}
	}

	private DataGroupSpy assertFactoredGroupWithNameAndRepeatId(int errorNo, String name,
			String repeatId, DataRecordGroupSpy existingMigrateBinary) {
		dataFactorySpy.MCR.assertParameters("factorGroupUsingNameInData", errorNo, name);
		DataGroupSpy factoredGroup = (DataGroupSpy) dataFactorySpy.MCR
				.getReturnValue("factorGroupUsingNameInData", errorNo);
		factoredGroup.MCR.assertCalledParameters("setRepeatId", repeatId);
		existingMigrateBinary.MCR.assertCalledParameters("addChild", factoredGroup);
		return factoredGroup;
	}

	private void assertFactoredAtomicWithNameAndValueAddedToGroupAsNo(String name, String value,
			DataGroupSpy dataGroup) {
		var factoredAtomic = dataFactorySpy.MCR
				.assertCalledParametersReturn("factorAtomicUsingNameInDataAndValue", name, value);
		dataGroup.MCR.assertCalledParameters("addChild", factoredAtomic);
	}

	private void assertRemoveAndAdded(String nameInData, String value) {
		existingMigrateBinary.MCR.assertCalledParameters("removeFirstChildWithNameInData",
				nameInData);
		assertFactoredAtomicWithNameAndValueAddedToAsNo(nameInData, value);
	}

	private void assertRemoveAndAdded(String nameInData, long value) {
		existingMigrateBinary.MCR.assertCalledParameters("removeFirstChildWithNameInData",
				nameInData);
		assertFactoredAtomicWithNameAndValueAddedToAsNo(nameInData, String.valueOf(value));
	}

	private void assertFactoredAtomicWithNameAndValueAddedToAsNo(String name, String value) {
		var factoredAtomic = dataFactorySpy.MCR
				.assertCalledParametersReturn("factorAtomicUsingNameInDataAndValue", name, value);
		existingMigrateBinary.MCR.assertCalledParameters("addChild", factoredAtomic);
	}

	@Test
	public void testUpdatedReplacedInRecordInfo() {
		LocalDateTime dateTimeBefore = whatTimeIsIt().minus(2, ChronoUnit.SECONDS);

		dataGroupHandler.updateDataRecordGroup(migrateBinary, existingMigrateBinary);

		LocalDateTime dateTimeAfter = whatTimeIsIt().plus(2, ChronoUnit.SECONDS);

		assertNewUpdatedReplacedInRecordInfo(existingMigrateBinary, dateTimeBefore, dateTimeAfter);
	}

	private void assertNewUpdatedReplacedInRecordInfo(DataRecordGroupSpy existingMigrateBinary,
			LocalDateTime dateTimeBefore, LocalDateTime dateTimeAfter) {
		existingMigrateBinary.MCR.assertParameters("getFirstGroupWithNameInData", 0, "recordInfo");

		recordInfoG.MCR.assertCalledParameters("removeAllChildrenWithNameInData", "updated");

		DataGroupSpy updated = (DataGroupSpy) dataFactorySpy.MCR
				.assertCalledParametersReturn("factorGroupUsingNameInData", "updated");

		recordInfoG.MCR.assertCalledParameters("addChild", updated);
		updated.MCR.assertParameters("setRepeatId", 0, "0");

		assertUpdateBy(updated);
		assertTsUpdated(dateTimeBefore, dateTimeAfter, updated);
	}

	private void assertUpdateBy(DataGroupSpy updated) {
		var updatedBy = dataFactorySpy.MCR.assertCalledParametersReturn(
				"factorRecordLinkUsingNameInDataAndTypeAndId", "updatedBy", "user", "system");
		updated.MCR.assertCalledParameters("addChild", updatedBy);
	}

	private void assertTsUpdated(LocalDateTime dateTimeBefore, LocalDateTime dateTimeAfter,
			DataGroupSpy updated) {
		DataAtomicSpy tsUpdated = (DataAtomicSpy) dataFactorySpy.MCR
				.assertCalledParametersReturn("factorAtomicUsingNameInDataAndValue", "tsUpdated");
		String tsUpdatedValue = (String) dataFactorySpy.MCR
				.getParameterForMethodAndCallNumberAndParameter(
						"factorAtomicUsingNameInDataAndValue", TS_UPDATED_NUMBER_TO_GET_FACTORED,
						"value");

		LocalDateTime tsUpdatedDateTime = LocalDateTime.parse(tsUpdatedValue, dateTimeFormatter);
		assertTrue(dateTimeBefore.isBefore(tsUpdatedDateTime));
		assertTrue(dateTimeAfter.isAfter(tsUpdatedDateTime));

		updated.MCR.assertCalledParameters("addChild", tsUpdated);
	}

	private LocalDateTime whatTimeIsIt() {
		return LocalDateTime.now();
	}

}
