import se.uu.ub.cora.alvin.extendedfunctionality.AlvinExtendedFunctionalityFactory;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;

module se.uu.ub.cora.alvin {
	// requires se.uu.ub.cora.therest;
	requires se.uu.ub.cora.spider;
	requires se.uu.ub.cora.storage;

	provides ExtendedFunctionalityFactory with AlvinExtendedFunctionalityFactory;
}