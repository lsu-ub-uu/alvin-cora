package se.uu.ub.cora.alvin.extended.record.urn;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.alvin.spies.spider.SpiderDependencyProviderSpy;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionality;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityContext;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityPosition;

public class UrnExtendedFunctionalityFactoryTest {

	private ExtendedFunctionalityFactory factory;
	private SpiderDependencyProvider dependencyProvider;

	@BeforeMethod
	public void beforeMethod() throws Exception {
		factory = new UrnExtendedFunctionalityFactory();
		dependencyProvider = new SpiderDependencyProviderSpy();
		factory.initializeUsingDependencyProvider(dependencyProvider);
	}

	@Test
	public void testInit() throws Exception {
		assertTrue(factory instanceof UrnExtendedFunctionalityFactory);
	}

	@Test
	public void testGetExtendedFunctionalityContextsForCreate() {
		assertContext(ExtendedFunctionalityPosition.CREATE_AFTER_METADATA_VALIDATION);
	}

	@Test
	public void testFactorCreate() throws Exception {
		List<ExtendedFunctionality> functionalities = factory.factor(
				ExtendedFunctionalityPosition.CREATE_AFTER_METADATA_VALIDATION, "alvin-record");
		assertTrue(functionalities.get(0) instanceof UrnExtendedFunctionality);
	}

	private void assertContext(ExtendedFunctionalityPosition position) {
		ExtendedFunctionalityContext extFuncContext = tryToFindMatchingContext(position);
		assertEquals(extFuncContext.recordType, "alvin-record");
		assertEquals(extFuncContext.position, position);
		assertEquals(extFuncContext.runAsNumber, 0);
	}

	private ExtendedFunctionalityContext tryToFindMatchingContext(
			ExtendedFunctionalityPosition position) {
		return factory.getExtendedFunctionalityContexts().stream()
				.filter(contexts -> contexts.position.equals(position)).findFirst().get();
	}
}
