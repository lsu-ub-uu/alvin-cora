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
 */
package se.uu.ub.cora.alvin.extendedfunctionality;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.messaging.ChannelInfo;
import se.uu.ub.cora.messaging.MessagingProvider;
import se.uu.ub.cora.spider.data.SpiderDataAtomic;
import se.uu.ub.cora.spider.data.SpiderDataGroup;

public class AlvinRecordIndexerTest {

	private MessagingFactorySpy messagingFactory;
	private AlvinRecordIndexer indexer;
	private ChannelInfo channelInfo;
	private String messageForCreateForSomeRecordType = "{\"headers\":{\"ACTION\":\"UPDATE\","
			+ "\"PID\":\"alvin-place:1\"},\"action\":\"UPDATE\",\"pid\":\"alvin-place:1\","
			+ "\"routingKey\":\"alvin.updates.someRecordType\"}";
	// private String message = "{\"pid\":\"alvin-place:1\",\"routingKey\":\"alvin.updates.place\","
	// + "\"action\":\"UPDATE\",\"dsId\":null,"
	// + "\"headers\":{\"ACTION\":\"UPDATE\",\"PID\":\"alvin-place:1\"}}";

	// headers.put("__TypeId__", "epc.messaging.amqp.EPCFedoraMessage");
	// headers.put("ACTION", "UPDATE");
	// headers.put("PID", "alvin-place:1");

	@BeforeMethod
	public void setUp() {
		messagingFactory = new MessagingFactorySpy();
		MessagingProvider.setMessagingFactory(messagingFactory);

		channelInfo = new ChannelInfo("someHostname", "somePort", "someChannel");
		indexer = new AlvinRecordIndexer(channelInfo);
	}

	@Test
	public void testExtendedFunctionality() {
		SpiderDataGroup dataGroup = createDataGroup("someRecordType", "alvin-place:1");

		indexer.useExtendedFunctionality("someAuthToken", dataGroup);

		assertTrue(messagingFactory.factorMessageSenderWasCalled);
		MessageSenderSpy messageSenderSpy = messagingFactory.messageSenderSpy;
		assertTrue(messageSenderSpy.sendMessageWasCalled);

		assertChannelInfoWasSentToMessagingFactory();

		String sentMessage = messageSenderSpy.messageSentToSpy;
		assertEquals(sentMessage, messageForCreateForSomeRecordType);

	}

	private SpiderDataGroup createDataGroup(String type, String id) {
		SpiderDataGroup dataGroup = SpiderDataGroup.withNameInData("someDataGroup");
		SpiderDataGroup recordInfo = SpiderDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("id", id));
		SpiderDataGroup typeGroup = SpiderDataGroup.withNameInData("type");
		typeGroup.addChild(
				SpiderDataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		typeGroup.addChild(SpiderDataAtomic.withNameInDataAndValue("linkedRecordId", type));
		recordInfo.addChild(typeGroup);
		dataGroup.addChild(recordInfo);
		return dataGroup;
	}

	private void assertChannelInfoWasSentToMessagingFactory() {
		assertEquals(messagingFactory.channelInfo.hostname, channelInfo.hostname);
		assertEquals(messagingFactory.channelInfo.port, channelInfo.port);
		assertEquals(messagingFactory.channelInfo.channel, channelInfo.channel);
	}

	@Test
	public void testGetChannelInfo() {
		ChannelInfo requestedChannelInfo = indexer.getChannelInfo();
		assertEquals(requestedChannelInfo.hostname, channelInfo.hostname);
		assertEquals(requestedChannelInfo.port, channelInfo.port);
		assertEquals(requestedChannelInfo.channel, channelInfo.channel);
	}
}
