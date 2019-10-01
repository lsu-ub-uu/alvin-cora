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

import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.messaging.MessageRoutingInfo;
import se.uu.ub.cora.messaging.MessagingProvider;
import se.uu.ub.cora.spider.data.SpiderDataAtomic;
import se.uu.ub.cora.spider.data.SpiderDataGroup;

public class AlvinRecordIndexerTest {

	private MessagingFactorySpy messagingFactory;
	private AlvinRecordIndexer indexer;
	private MessageRoutingInfo messageRoutingInfo;
	private String messageForCreateForSomeRecordType = "{\"headers\":{\"ACTION\":\"UPDATE\","
			+ "\"PID\":\"alvin-place:1\"},\"action\":\"UPDATE\",\"pid\":\"alvin-place:1\","
			+ "\"routingKey\":\"alvin.updates.someRecordType\"}";
	// private String message = "{\"pid\":\"alvin-place:1\",\"routingKey\":\"alvin.updates.place\","
	// + "\"action\":\"UPDATE\",\"dsId\":null,"
	// + "\"headers\":{\"ACTION\":\"UPDATE\",\"PID\":\"alvin-place:1\"}}";

	// headers.put("__TypeId__", "epc.messaging.amqp.EPCFedoraMessage");
	// headers.put("ACTION", "UPDATE");
	// headers.put("PID", "alvin-place:1");
	// headers.put("messageSentFrom", "Cora");

	@BeforeMethod
	public void setUp() {
		messagingFactory = new MessagingFactorySpy();
		MessagingProvider.setMessagingFactory(messagingFactory);

		messageRoutingInfo = new MessageRoutingInfo("someHostname", "somePort", "someVirtualHost",
				"index", "alvin.updates.place");
		indexer = new AlvinRecordIndexer(messageRoutingInfo);
	}

	@Test
	public void testExtendedFunctionality() {
		SpiderDataGroup dataGroup = createDataGroup("someRecordType", "alvin-place:1");

		indexer.useExtendedFunctionality("someAuthToken", dataGroup);

		assertTrue(messagingFactory.factorMessageSenderWasCalled);
		MessageSenderSpy messageSenderSpy = messagingFactory.messageSenderSpy;
		assertTrue(messageSenderSpy.sendMessageWasCalled);

		String sentMessage = messageSenderSpy.messageSentToSpy;
		assertEquals(sentMessage, messageForCreateForSomeRecordType);
		Map<String, Object> headersSentToSpy = messageSenderSpy.headersSentToSpy;
		assertEquals(headersSentToSpy.size(), 4);
		assertEquals(headersSentToSpy.get("__TypeId__"), "epc.messaging.amqp.EPCFedoraMessage");
		assertEquals(headersSentToSpy.get("ACTION"), "UPDATE");
		assertEquals(headersSentToSpy.get("PID"), "alvin-place:1");
		assertEquals(headersSentToSpy.get("messageSentFrom"), "Cora");

	}

	@Test
	public void testExtendedFunctionalityCorrectChannelInfoSentToMessageFactory() {
		SpiderDataGroup dataGroup = createDataGroup("someRecordType", "alvin-place:1");

		indexer.useExtendedFunctionality("someAuthToken", dataGroup);

		MessageSenderSpy messageSenderSpy = messagingFactory.messageSenderSpy;

		assertEquals(messagingFactory.messageRoutingInfo.hostname, messageRoutingInfo.hostname);
		assertEquals(messagingFactory.messageRoutingInfo.port, messageRoutingInfo.port);
		assertEquals(messagingFactory.messageRoutingInfo.virtualHost,
				messageRoutingInfo.virtualHost);
		assertEquals(messagingFactory.messageRoutingInfo.exchange, messageRoutingInfo.exchange);
		assertEquals(messagingFactory.messageRoutingInfo.routingKey, messageRoutingInfo.routingKey);

		String sentMessage = messageSenderSpy.messageSentToSpy;
		assertEquals(sentMessage, messageForCreateForSomeRecordType);

	}

	@Test
	public void testExtendedFunctionalityCorrectHeadersSentToMessageFactory() {
		SpiderDataGroup dataGroup = createDataGroup("someRecordType", "alvin-place:1");
		indexer.useExtendedFunctionality("someAuthToken", dataGroup);

		MessageSenderSpy messageSenderSpy = messagingFactory.messageSenderSpy;

		Map<String, Object> headersSentToSpy = messageSenderSpy.headersSentToSpy;
		assertEquals(headersSentToSpy.size(), 4);
		assertEquals(headersSentToSpy.get("__TypeId__"), "epc.messaging.amqp.EPCFedoraMessage");
		assertEquals(headersSentToSpy.get("ACTION"), "UPDATE");
		assertEquals(headersSentToSpy.get("PID"), "alvin-place:1");
		assertEquals(headersSentToSpy.get("messageSentFrom"), "Cora");

	}

	private SpiderDataGroup createDataGroup(String type, String id) {
		SpiderDataGroup dataGroup = SpiderDataGroup.withNameInData("someDataGroup");
		SpiderDataGroup recordInfo = SpiderDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("id", id));
		createAndAddType(type, recordInfo);
		dataGroup.addChild(recordInfo);
		return dataGroup;
	}

	private void createAndAddType(String type, SpiderDataGroup recordInfo) {
		SpiderDataGroup typeGroup = SpiderDataGroup.withNameInData("type");
		typeGroup.addChild(
				SpiderDataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		typeGroup.addChild(SpiderDataAtomic.withNameInDataAndValue("linkedRecordId", type));
		recordInfo.addChild(typeGroup);
	}

	@Test
	public void testGetMessageRoutingInfo() {
		MessageRoutingInfo requestedMessageRoutingInfo = indexer.getMessageRoutingInfo();
		assertEquals(requestedMessageRoutingInfo.hostname, messageRoutingInfo.hostname);
		assertEquals(requestedMessageRoutingInfo.port, messageRoutingInfo.port);
		assertEquals(requestedMessageRoutingInfo.exchange, messageRoutingInfo.exchange);
		assertEquals(requestedMessageRoutingInfo.routingKey, messageRoutingInfo.routingKey);
	}
}
