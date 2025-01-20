package se.uu.ub.cora.alvin.extended.record.urn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityContext;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition;

public class UrnExtendedFunctionalityFactory implements ExtendedFunctionalityFactory {

	private List<ExtendedFunctionalityContext> contexts = new ArrayList<>();

	@Override
	public List<ExtendedFunctionality> factor(ExtendedFunctionalityPosition position,
			String recordType) {
		return Collections.singletonList(new UrnExtendedFunctionality());
	}

	@Override
	public void initializeUsingDependencyProvider(SpiderDependencyProvider dependencyProvider) {
		createListOfContexts();
	}

	private void createListOfContexts() {
		createContext(ExtendedFunctionalityPosition.CREATE_AFTER_METADATA_VALIDATION);
	}

	private void createContext(ExtendedFunctionalityPosition position) {
		contexts.add(new ExtendedFunctionalityContext(position, "alvin-record", 0));
	}

	@Override
	public List<ExtendedFunctionalityContext> getExtendedFunctionalityContexts() {
		return contexts;
	}
}