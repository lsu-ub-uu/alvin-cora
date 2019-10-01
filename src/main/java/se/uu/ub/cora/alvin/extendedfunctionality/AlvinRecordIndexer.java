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

import java.util.HashMap;
import java.util.Map;

import se.uu.ub.cora.messaging.MessageRoutingInfo;
import se.uu.ub.cora.messaging.MessageSender;
import se.uu.ub.cora.messaging.MessagingProvider;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.extended.ExtendedFunctionality;

public class AlvinRecordIndexer implements ExtendedFunctionality {

	private MessageRoutingInfo channelInfo;
	private String type;
	private String pid;

	public AlvinRecordIndexer(MessageRoutingInfo channelInfo) {
		this.channelInfo = channelInfo;
	}

	@Override
	public void useExtendedFunctionality(String authToken, SpiderDataGroup spiderDataGroup) {
		extractTypeAndPid(spiderDataGroup);
		Map<String, Object> headers = createHeaders();
		String message = createMessage();

		MessageSender messageSender = MessagingProvider.getTopicMessageSender(channelInfo);
		messageSender.sendMessage(headers, message);
	}

	private void extractTypeAndPid(SpiderDataGroup spiderDataGroup) {
		SpiderDataGroup recordInfo = spiderDataGroup.extractGroup("recordInfo");
		type = getRecordType(recordInfo);
		pid = recordInfo.extractAtomicValue("id");
	}

	private String getRecordType(SpiderDataGroup recordInfo) {
		SpiderDataGroup typeGroup = recordInfo.extractGroup("type");
		return typeGroup.extractAtomicValue("linkedRecordId");
	}

	private Map<String, Object> createHeaders() {
		Map<String, Object> headers = new HashMap<>();
		headers.put("__TypeId__", "epc.messaging.amqp.EPCFedoraMessage");
		headers.put("ACTION", "UPDATE");
		headers.put("PID", pid);
		headers.put("messageSentFrom", "Cora");
		return headers;
	}

	private String createMessage() {
		IndexMessageCreator indexMessageCreator = IndexMessageCreator.usingId(pid);
		return indexMessageCreator.createMessage("alvin.updates." + type, "UPDATE");
	}

	MessageRoutingInfo getMessageRoutingInfo() {
		// needed for test
		return channelInfo;
	}

}
