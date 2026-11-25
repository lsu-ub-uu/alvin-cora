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

import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class MigrateBinaryStorerSpy implements MigrateBinaryStorer {

	public MigrateBinaryInfo migrateBinaryInfo;
	public long numberOfIndexed;
	public List<Error> errors = new ArrayList<>();
	public MethodCallRecorder MCR = new MethodCallRecorder();

	@Override
	public void store(MigrateBinaryInfo migrateBinaryInfo) {
		// special to record the number of processed and errors, by each call
		MCR.addCall("migrateBinary", migrateBinaryInfo, "migrateBinary.numberOfProcessedRecords",
				migrateBinaryInfo.numberOfProcessedRecords, "migrateBinary.errors",
				new ArrayList<>(migrateBinaryInfo.errors), "migrateBinary.status",
				migrateBinaryInfo.status);
		this.migrateBinaryInfo = migrateBinaryInfo;
		numberOfIndexed = migrateBinaryInfo.numberOfProcessedRecords;
		errors.addAll(migrateBinaryInfo.errors);
	}
}
