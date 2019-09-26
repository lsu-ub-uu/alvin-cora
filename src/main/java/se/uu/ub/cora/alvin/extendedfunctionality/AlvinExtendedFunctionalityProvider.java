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

import se.uu.ub.cora.metacreator.extended.MetacreatorExtendedFunctionalityProvider;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extended.ExtendedFunctionality;

public class AlvinExtendedFunctionalityProvider extends MetacreatorExtendedFunctionalityProvider {

	private static final String PLACE = "place";

	public AlvinExtendedFunctionalityProvider(SpiderDependencyProvider dependencyProvider) {
		super(dependencyProvider);
	}

	@Override
	public List<ExtendedFunctionality> getFunctionalityForCreateBeforeReturn(String recordType) {
		List<ExtendedFunctionality> list = super.getFunctionalityForCreateBeforeReturn(recordType);
		if (PLACE.equals(recordType)) {
			list = ensureListExists(list);
			list.add(new AlvinRecordIndexer());
		}

		return list;
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
