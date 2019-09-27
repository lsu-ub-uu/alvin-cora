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

import se.uu.ub.cora.json.builder.org.OrgJsonObjectBuilderAdapter;
import se.uu.ub.cora.messaging.ChannelInfo;
import se.uu.ub.cora.messaging.MessageSender;
import se.uu.ub.cora.messaging.MessagingProvider;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.extended.ExtendedFunctionality;

public class AlvinRecordIndexer implements ExtendedFunctionality {

	private ChannelInfo channelInfo;

	public AlvinRecordIndexer(ChannelInfo channelInfo) {
		this.channelInfo = channelInfo;
	}

	@Override
	public void useExtendedFunctionality(String authToken, SpiderDataGroup spiderDataGroup) {
		MessageSender messageSender = MessagingProvider.getTopicMessageSender(channelInfo);
		String message = createMessage(spiderDataGroup);
		messageSender.sendMessage(null, message);
	}

	private String createMessage(SpiderDataGroup spiderDataGroup) {
		SpiderDataGroup recordInfo = spiderDataGroup.extractGroup("recordInfo");
		String pid = recordInfo.extractAtomicValue("id");
		String type = getRecordType(recordInfo);
		OrgJsonObjectBuilderAdapter builderAdapter = createJsonObjectBuilderWithValues(pid, type);
		return builderAdapter.toJsonFormattedString();

		// new IndexMessageCreator(type, id);
	}

	// private OrgJsonObjectBuilderAdapter createJsonObject(SpiderDataGroup spiderDataGroup) {
	// SpiderDataGroup recordInfo = spiderDataGroup.extractGroup("recordInfo");
	// String pid = recordInfo.extractAtomicValue("id");
	// String type = getRecordType(recordInfo);
	//
	// return createJsonObjectBuilderWithValues(pid, type);
	// }

	private OrgJsonObjectBuilderAdapter createJsonObjectBuilderWithValues(String pid, String type) {
		OrgJsonObjectBuilderAdapter builderAdapter = new OrgJsonObjectBuilderAdapter();
		builderAdapter.addKeyString("pid", pid);
		builderAdapter.addKeyString("routingKey", "alvin.updates." + type);
		builderAdapter.addKeyString("action", "UPDATE");

		addHeaders(pid, builderAdapter);
		return builderAdapter;
	}

	private void addHeaders(String pid, OrgJsonObjectBuilderAdapter builderAdapter) {
		OrgJsonObjectBuilderAdapter builderAdapterHeaders = createJsonBuilderAdapterForHeaders(pid);
		builderAdapter.addKeyJsonObjectBuilder("headers", builderAdapterHeaders);
	}

	private OrgJsonObjectBuilderAdapter createJsonBuilderAdapterForHeaders(String pid) {
		OrgJsonObjectBuilderAdapter builderAdapterHeaders = new OrgJsonObjectBuilderAdapter();
		builderAdapterHeaders.addKeyString("ACTION", "UPDATE");
		builderAdapterHeaders.addKeyString("PID", pid);
		return builderAdapterHeaders;
	}

	private String getRecordType(SpiderDataGroup recordInfo) {
		SpiderDataGroup typeGroup = recordInfo.extractGroup("type");
		return typeGroup.extractAtomicValue("linkedRecordId");
	}

	ChannelInfo getChannelInfo() {
		// needed for test
		return channelInfo;
	}

}
