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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.alvin.AlvinDependencyProvider;
import se.uu.ub.cora.alvin.AlvinInitializationException;
import se.uu.ub.cora.logger.Logger;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.messaging.MessageRoutingInfo;
import se.uu.ub.cora.metacreator.extended.MetacreatorExtendedFunctionalityProvider;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extended.ExtendedFunctionality;

public class AlvinExtendedFunctionalityProvider extends MetacreatorExtendedFunctionalityProvider {
	private static final String VIRTUAL_HOST = "alvin";

	private Logger log = LoggerProvider.getLoggerForClass(AlvinExtendedFunctionalityProvider.class);

	private static final String PLACE = "place";

	private Map<String, String> initInfo;

	public AlvinExtendedFunctionalityProvider(SpiderDependencyProvider dependencyProvider) {
		super(dependencyProvider);
	}

	@Override
	public List<ExtendedFunctionality> getFunctionalityForCreateBeforeReturn(String recordType) {
		List<ExtendedFunctionality> list = super.getFunctionalityForCreateBeforeReturn(recordType);
		if (PLACE.equals(recordType)) {
			list = ensureListExists(list);
			MessageRoutingInfo channelInfo = createChannelInfo(recordType);
			list.add(new AlvinRecordIndexer(channelInfo));
		}

		return list;
	}

	private MessageRoutingInfo createChannelInfo(String recordType) {
		AlvinDependencyProvider alvinDependenProvider = (AlvinDependencyProvider) dependencyProvider;
		initInfo = alvinDependenProvider.getInitInfo();
		String hostname = tryToGetInitParameterLogIfFound("messageServerHostname");
		String port = tryToGetInitParameterLogIfFound("messageServerPort");
		String routingKey = "alvin.updates." + recordType;
		return new MessageRoutingInfo(hostname, port, VIRTUAL_HOST, "index", routingKey);
	}

	private String tryToGetInitParameterLogIfFound(String parameterName) {
		String basePath = tryToGetInitParameter(parameterName);
		log.logInfoUsingMessage("Found " + basePath + " as " + parameterName);
		return basePath;
	}

	private String tryToGetInitParameter(String parameterName) {
		throwErrorIfKeyIsMissingFromInitInfo(parameterName);
		return initInfo.get(parameterName);
	}

	private void throwErrorIfKeyIsMissingFromInitInfo(String key) {
		if (!initInfo.containsKey(key)) {
			String errorMessage = "InitInfo must contain " + key;
			log.logFatalUsingMessage(errorMessage);
			throw AlvinInitializationException.withMessage(errorMessage);
		}
	}

	@Override
	public List<ExtendedFunctionality> getFunctionalityBeforeDelete(String recordType) {
		List<ExtendedFunctionality> list = super.getFunctionalityForCreateBeforeMetadataValidation(
				recordType);
		if (PLACE.equals(recordType)) {
			list = ensureListExists(list);
			list.add(new RecordBeforeDeleteUpdater());
		}
		return list;
	}

	@Override
	protected List<ExtendedFunctionality> ensureListExists(List<ExtendedFunctionality> list) {
		if (Collections.emptyList().equals(list)) {
			return new ArrayList<>();
		}
		return list;
	}

	public SpiderDependencyProvider getDependencyProvider() {
		return dependencyProvider;
	}

}
