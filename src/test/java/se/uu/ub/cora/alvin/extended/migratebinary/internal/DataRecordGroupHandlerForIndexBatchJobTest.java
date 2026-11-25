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
import se.uu.ub.cora.storage.Filter;

public class DataRecordGroupHandlerForMigrateBinaryTest {

	private static final String SOME_RECORD_TYPE = "someRecordType";

	private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";
	private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter
			.ofPattern(DATE_TIME_PATTERN);

	private MigrateBinaryInfo migrateBinary;
	private DataRecordGroupHandlerForMigrateBinaryImp dataGroupHandler;
	private DataFactorySpy dataFactorySpy;
	/**
	 * numberOfFactoredAtomics
	 */
	int nfa = 0;
	/**
	 * numberOfFactoredRecordLinks
	 */
	int nfrl = 0;
	/**
	 * numberOfFactoredGroups
	 */
	int nfg = 0;

	@BeforeMethod
	public void beforeMethod() {
		nfa = 0;
		nfrl = 0;
		nfg = 0;
		setUpProviders();

		migrateBinary = createMigrateBinary();
		dataGroupHandler = new DataRecordGroupHandlerForMigrateBinaryImp();
	}

	private void setUpProviders() {
		dataFactorySpy = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactorySpy);
	}

	String dataGroupLayout = """
			migrateBinary
				recordInfo
					createdBy user someSuperUser
					tsCreated 2021-05-03T14:12:11.657482Z
					updated
						tsUpdated 2021-06-02T14:45:11.657482Z
						updatedBy user someUserId
				numOfProcessedRecords 34
				totalNumberToIndex 34
				status someStatus
				error
					recordId someRecordId
					message some read error message
				""";

	Integer callsToAddChildForCheckingMigrateBinary = 0;
	Integer callsToRemoveFirstChildWithNameForMigrateBinary = 0;

	@Test
	public void testUpdateDataGroupWithInfoFromMigrateBinaryForUpdate() {
		callsToAddChildForCheckingMigrateBinary = 0;
		callsToRemoveFirstChildWithNameForMigrateBinary = 0;
		DataRecordGroupSpy existingMigrateBinary = new DataRecordGroupSpy();
		// setListOfErrors
		List<DataGroupSpy> listOfErrorsSpies = List.of(new DataGroupSpy(), new DataGroupSpy());
		int firstErrorRepeatId = listOfErrorsSpies.size() + 1;
		existingMigrateBinary.MRV.setReturnValues("getAllGroupsWithNameInData",
				List.of(listOfErrorsSpies), "error");

		// recordInfoSpy
		DataGroupSpy recordInfoG = new DataGroupSpy();
		existingMigrateBinary.MRV.setReturnValues("getFirstGroupWithNameInData",
				List.of(recordInfoG), "recordInfo");
		// setListOfUpdated
		List<DataGroupSpy> listOfUpdatedSpies = List.of(new DataGroupSpy());
		int firstUpdatedRepeatId = listOfUpdatedSpies.size() + 1;
		recordInfoG.MRV.setReturnValues("getAllGroupsWithNameInData", List.of(listOfUpdatedSpies),
				"updated");

		LocalDateTime dateTimeBefore = whatTimeIsIt().minus(2, ChronoUnit.SECONDS);

		dataGroupHandler.updateDataRecordGroup(migrateBinary, existingMigrateBinary);

		LocalDateTime dateTimeAfter = whatTimeIsIt().plus(2, ChronoUnit.SECONDS);

		assertNumberOfProcessedRecords(existingMigrateBinary);
		assertErrorsFromMigrateBinaryAddedToGroupAndFirstRepeatId(existingMigrateBinary,
				firstErrorRepeatId);
		assertStatus(existingMigrateBinary);
		assertNewUpdatedAddedToRecordInfo(existingMigrateBinary, firstUpdatedRepeatId,
				dateTimeBefore, dateTimeAfter);
	}

	private void assertNewUpdatedAddedToRecordInfo(DataRecordGroupSpy existingMigrateBinary,
			int firstUpdatedRepeatId, LocalDateTime dateTimeBefore, LocalDateTime dateTimeAfter) {
		existingMigrateBinary.MCR.assertParameters("getFirstGroupWithNameInData", 0, "recordInfo");
		DataGroupSpy recordInfo = (DataGroupSpy) existingMigrateBinary.MCR
				.getReturnValue("getFirstGroupWithNameInData", 0);

		recordInfo.MCR.assertParameters("getAllGroupsWithNameInData", 0, "updated");

		dataFactorySpy.MCR.assertParameters("factorGroupUsingNameInData", nfg, "updated");
		DataGroupSpy updated = (DataGroupSpy) dataFactorySpy.MCR
				.getReturnValue("factorGroupUsingNameInData", nfg);
		recordInfo.MCR.assertParameters("addChild", 0, updated);
		updated.MCR.assertParameters("setRepeatId", 0, String.valueOf(firstUpdatedRepeatId));
		callsToAddChildForCheckingMigrateBinary++;
		nfg++;

		assertUpdateBy(updated);
		assertTsUpdated(dateTimeBefore, dateTimeAfter, updated);
	}

	private void assertUpdateBy(DataGroupSpy updated) {
		dataFactorySpy.MCR.assertParameters("factorRecordLinkUsingNameInDataAndTypeAndId", nfrl,
				"updatedBy", "user", "system");
		var updatedBy = dataFactorySpy.MCR
				.getReturnValue("factorRecordLinkUsingNameInDataAndTypeAndId", nfrl);
		updated.MCR.assertParameters("addChild", 0, updatedBy);
		nfrl++;
	}

	private void assertTsUpdated(LocalDateTime dateTimeBefore, LocalDateTime dateTimeAfter,
			DataGroupSpy updated) {
		dataFactorySpy.MCR.assertParameters("factorAtomicUsingNameInDataAndValue", nfa,
				"tsUpdated");
		DataAtomicSpy tsUpdated = (DataAtomicSpy) dataFactorySpy.MCR
				.getReturnValue("factorAtomicUsingNameInDataAndValue", nfa);
		String tsUpdatedValue = (String) dataFactorySpy.MCR
				.getParameterForMethodAndCallNumberAndParameter(
						"factorAtomicUsingNameInDataAndValue", nfa, "value");
		nfa++;

		LocalDateTime tsUpdatedDateTime = LocalDateTime.parse(tsUpdatedValue, dateTimeFormatter);
		assertTrue(dateTimeBefore.isBefore(tsUpdatedDateTime));
		assertTrue(dateTimeAfter.isAfter(tsUpdatedDateTime));

		updated.MCR.assertParameters("addChild", 1, tsUpdated);
	}

	private LocalDateTime whatTimeIsIt() {
		return LocalDateTime.now();
	}

	private void assertNumberOfProcessedRecords(DataRecordGroupSpy existingMigrateBinary) {
		existingMigrateBinary.MCR.assertParameters("removeFirstChildWithNameInData",
				callsToRemoveFirstChildWithNameForMigrateBinary, "numberOfProcessedRecords");
		callsToRemoveFirstChildWithNameForMigrateBinary++;

		assertFactoredAtomicWithNameAndValueAddedToAsNo("numberOfProcessedRecords",
				String.valueOf(migrateBinary.numberOfProcessedRecords), existingMigrateBinary,
				callsToAddChildForCheckingMigrateBinary);
		callsToAddChildForCheckingMigrateBinary++;
	}

	private void assertStatus(DataRecordGroupSpy existingMigrateBinary) {
		existingMigrateBinary.MCR.assertParameters("removeFirstChildWithNameInData",
				callsToRemoveFirstChildWithNameForMigrateBinary, "status");
		callsToRemoveFirstChildWithNameForMigrateBinary++;

		assertFactoredAtomicWithNameAndValueAddedToAsNo("status",
				String.valueOf(migrateBinary.status), existingMigrateBinary,
				callsToAddChildForCheckingMigrateBinary);
		callsToAddChildForCheckingMigrateBinary++;
	}

	private MigrateBinaryInfo createMigrateBinary() {
		Filter filter = new Filter();

		MigrateBinaryInfo newMigrateBinary = new MigrateBinaryInfo(SOME_RECORD_TYPE);
		createAndAddErrors(newMigrateBinary);
		return newMigrateBinary;
	}

	private void createAndAddErrors(MigrateBinaryInfo migrateBinary) {
		List<Error> errors = new ArrayList<>();
		errors.add(new Error("recordIdOne", "some index error message"));
		errors.add(new Error("recordIdTwo", "some other index error message"));
		migrateBinary.errors = errors;
	}

	@Test
	public void testConvertMigrateBinaryToDataGroupForCreate() {
		DataGroupSpy filterAsDataGroup = new DataGroupSpy();

		DataRecordGroupSpy createdRecordGroup = (DataRecordGroupSpy) dataGroupHandler
				.createDataRecordGroup(migrateBinary, filterAsDataGroup);

		dataFactorySpy.MCR.assertParameters("factorRecordGroupUsingNameInData", 0, "migrateBinary");
		dataFactorySpy.MCR.assertReturn("factorRecordGroupUsingNameInData", 0, createdRecordGroup);

		assertCorrectRecordInfo(createdRecordGroup);

		assertFactoredAtomicWithNameAndValueAddedToAsNo("recordTypeToIndex",
				migrateBinary.recordTypeToIndex, createdRecordGroup,
				callsToAddChildForCheckingMigrateBinary);
		callsToAddChildForCheckingMigrateBinary++;
		assertFactoredAtomicWithNameAndValueAddedToAsNo("status", "started", createdRecordGroup,
				callsToAddChildForCheckingMigrateBinary);
		callsToAddChildForCheckingMigrateBinary++;
		assertFactoredAtomicWithNameAndValueAddedToAsNo("numberOfProcessedRecords", "0",
				createdRecordGroup, callsToAddChildForCheckingMigrateBinary);
		callsToAddChildForCheckingMigrateBinary++;
		assertFactoredAtomicWithNameAndValueAddedToAsNo("totalNumberToIndex", "10",
				createdRecordGroup, callsToAddChildForCheckingMigrateBinary);
		callsToAddChildForCheckingMigrateBinary++;
		createdRecordGroup.MCR.assertParameters("addChild", callsToAddChildForCheckingMigrateBinary,
				filterAsDataGroup);
		callsToAddChildForCheckingMigrateBinary++;

		assertErrorsFromMigrateBinaryAddedToGroupAndFirstRepeatId(createdRecordGroup, 1);
		createdRecordGroup.MCR.assertNumberOfCallsToMethod("addChild",
				callsToAddChildForCheckingMigrateBinary);
	}

	private void assertCorrectRecordInfo(DataRecordGroupSpy createdRecordGroup) {
		createdRecordGroup.MCR.assertParameters("setDataDivider", 0, "coraData");
		createdRecordGroup.MCR.assertParameters("setValidationType", 0, "migrateBinary");
	}

	private void assertFactoredAtomicWithNameAndValueAddedToAsNo(String name, String value,
			DataRecordGroupSpy existingMigrateBinary, int addedNo) {
		dataFactorySpy.MCR.assertParameters("factorAtomicUsingNameInDataAndValue", nfa, name,
				value);
		DataAtomicSpy factoredAtomic = (DataAtomicSpy) dataFactorySpy.MCR
				.getReturnValue("factorAtomicUsingNameInDataAndValue", nfa);
		existingMigrateBinary.MCR.assertParameters("addChild", addedNo, factoredAtomic);
		nfa++;
	}

	private void assertErrorsFromMigrateBinaryAddedToGroupAndFirstRepeatId(
			DataRecordGroupSpy existingMigrateBinary, int startRepeatId) {
		int repeatId = startRepeatId;
		for (Error indexError : migrateBinary.errors) {
			assertFactoredGroupWithNameAndRepeatIdAddedToAsNo("error", String.valueOf(repeatId),
					existingMigrateBinary, callsToAddChildForCheckingMigrateBinary);
			callsToAddChildForCheckingMigrateBinary++;

			DataGroupSpy errorGroup = (DataGroupSpy) dataFactorySpy.MCR
					.getReturnValue("factorGroupUsingNameInData", nfg);
			nfg++;

			assertFactoredAtomicWithNameAndValueAddedToGroupAsNo("recordId", indexError.recordId,
					errorGroup, 0);
			assertFactoredAtomicWithNameAndValueAddedToGroupAsNo("message", indexError.message,
					errorGroup, 1);
			repeatId++;
		}
	}

	private void assertFactoredAtomicWithNameAndValueAddedToGroupAsNo(String name, String value,
			DataGroupSpy existingMigrateBinary, int addedNo) {
		dataFactorySpy.MCR.assertParameters("factorAtomicUsingNameInDataAndValue", nfa, name,
				value);
		DataAtomicSpy factoredAtomic = (DataAtomicSpy) dataFactorySpy.MCR
				.getReturnValue("factorAtomicUsingNameInDataAndValue", nfa);
		existingMigrateBinary.MCR.assertParameters("addChild", addedNo, factoredAtomic);
		nfa++;
	}

	private void assertFactoredGroupWithNameAndRepeatIdAddedToAsNo(String name, String repeatId,
			DataRecordGroupSpy existingMigrateBinary, int addedNo) {
		dataFactorySpy.MCR.assertParameters("factorGroupUsingNameInData", nfg, name);
		DataGroupSpy factoredGroup = (DataGroupSpy) dataFactorySpy.MCR
				.getReturnValue("factorGroupUsingNameInData", nfg);
		if (!"".equals(repeatId)) {
			factoredGroup.MCR.assertParameters("setRepeatId", 0, repeatId);
		}
		existingMigrateBinary.MCR.assertParameters("addChild", addedNo, factoredGroup);
	}

	@Test
	public void testCreateDataGroupFromMigrateBinaryNoFilterIfFilterIsEmpty() {
		DataGroupSpy filterAsData = new DataGroupSpy("filter");

		DataRecordGroupSpy createdRecordGroup = (DataRecordGroupSpy) dataGroupHandler
				.createDataRecordGroup(migrateBinary, filterAsData);
		createdRecordGroup.MCR.assertNumberOfCallsToMethod("addChild", 6);
	}

	@Test
	public void testCreateDataGroupFromMigrateBinaryEmptyErrors() {
		MigrateBinaryInfo newMigrateBinary = new MigrateBinaryInfo("place");

		DataRecordGroupSpy createdRecordGroup = (DataRecordGroupSpy) dataGroupHandler
				.createDataRecordGroup(newMigrateBinary, new DataGroupSpy("filter"));

		createdRecordGroup.MCR.assertNumberOfCallsToMethod("addChild", 4);
	}

}
