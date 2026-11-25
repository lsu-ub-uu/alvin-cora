/*
 * Copyright 2021, 2024 Uppsala University Library
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

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class DataRecordGroupHandlerForMigrateBinarySpy
		implements DataRecordGroupHandlerForMigrateBinary {
	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public DataRecordGroupHandlerForMigrateBinarySpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("createDataRecordGroup", DataRecordGroupSpy::new);
	}

	@Override
	public void updateDataRecordGroup(MigrateBinaryInfo migrateBinaryInfo,
			DataRecordGroup dataRecordGroup) {
		MCR.addCall("migrateBinaryInfo", migrateBinaryInfo, "dataRecordGroup", dataRecordGroup);
	}

	@Override
	public DataRecordGroup createDataRecordGroup(MigrateBinaryInfo migrateBinaryInfo,
			DataGroup filterAsDataGroup) {
		return (DataRecordGroup) MCR.addCallAndReturnFromMRV("migrateBinary", migrateBinaryInfo,
				"filterAsDataGroup", filterAsDataGroup);
	}
}
