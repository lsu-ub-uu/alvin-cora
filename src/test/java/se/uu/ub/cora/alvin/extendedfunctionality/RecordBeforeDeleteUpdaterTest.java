/*
 * Copyright 2019 Uppsala University Library
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
 *
 */
package se.uu.ub.cora.alvin.extendedfunctionality;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;

public class RecordBeforeDeleteUpdaterTest {
	SpiderDependencyProvider dependencyProvider;
	private SpiderInstanceFactorySpy spiderInstanceFactory;

	@BeforeMethod
	public void setUp() {
		spiderInstanceFactory = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(spiderInstanceFactory);

	}

	@Test
	public void testInit() {
		RecordBeforeDeleteUpdater updater = new RecordBeforeDeleteUpdater();
		String authToken = "someAuthToken";
		DataGroup dataGroup = createDataGroup();
		updater.useExtendedFunctionality(authToken, dataGroup);
		SpiderRecordUpdaterSpy factoredUpdater = (SpiderRecordUpdaterSpy) spiderInstanceFactory.factoredRecordUpdaters
				.get(0);

		assertEquals(authToken, factoredUpdater.authToken);
		assertSame(dataGroup, factoredUpdater.record);
		assertEquals("someRecordType", factoredUpdater.type);
		assertEquals("someRecordId", factoredUpdater.id);
	}

	private DataGroup createDataGroup() {
		DataGroup dataGroup = new DataGroupSpy("someNameInData");
		DataGroup recordInfo = createRecordInfo();
		dataGroup.addChild(recordInfo);
		return dataGroup;
	}

	private DataGroup createRecordInfo() {
		DataGroup recordInfo = new DataGroupSpy("recordInfo");
		DataGroup type = createType();
		recordInfo.addChild(type);
		DataAtomic recordId = new DataAtomicSpy("id", "someRecordId");
		recordInfo.addChild(recordId);
		return recordInfo;
	}

	private DataGroup createType() {
		DataGroup type = new DataGroupSpy("type");
		DataAtomic recordId = new DataAtomicSpy("linkedRecordId", "someRecordType");
		DataAtomic linkedRecordType = new DataAtomicSpy("linkedRecordType", "recordType");
		type.addChild(recordId);
		type.addChild(linkedRecordType);
		return type;
	}

}
