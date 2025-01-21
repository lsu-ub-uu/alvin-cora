package se.uu.ub.cora.alvin.extended.record.urn;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityData;

public class UrnExtendedFunctionality implements ExtendedFunctionality {

	private static final String ID = "id";
	private static final String URN = "urn";
	private static final String RECORD_INFO = "recordInfo";
	private static final String URN_FORMAT = "urn:nbn:se:alvin:portal:record-";

	@Override
	public void useExtendedFunctionality(ExtendedFunctionalityData data) {
		DataRecordGroup recordGroup = data.dataRecordGroup;
		if (hasRecordInfo(recordGroup)) {
			possiblyAddUrnNumber(recordGroup);
		}
	}

	private boolean hasRecordInfo(DataRecordGroup recordGroup) {
		return recordGroup != null && recordGroup.containsChildWithNameInData(RECORD_INFO);
	}

	private void possiblyAddUrnNumber(DataRecordGroup recordGroup) {
		DataGroup recordInfoGroup = recordGroup.getFirstGroupWithNameInData(RECORD_INFO);
		if (!recordInfoHasUrn(recordInfoGroup)) {
			createAndAddUrn(recordInfoGroup);
		}
	}

	private boolean recordInfoHasUrn(DataGroup recordInfoGroup) {
		return recordInfoGroup.containsChildWithNameInData(URN);
	}

	private void createAndAddUrn(DataGroup recordInfoGroup) {
		String recordId = recordInfoGroup.getFirstAtomicValueWithNameInData(ID);
		DataAtomic urnNumber = DataProvider.createAtomicUsingNameInDataAndValue(URN,
				URN_FORMAT + recordId);

		recordInfoGroup.addChild(urnNumber);
	}
}