import se.uu.ub.cora.alvin.extended.migratebinary.MigrateExtendedFunctionalityFactory;
import se.uu.ub.cora.alvin.extended.record.urn.UrnExtendedFunctionalityFactory;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;

module se.uu.ub.cora.alvin {
	requires se.uu.ub.cora.spider;
	requires se.uu.ub.cora.data;
	requires se.uu.ub.cora.storage;
	requires se.uu.ub.cora.logger;

	provides ExtendedFunctionalityFactory
			with UrnExtendedFunctionalityFactory, MigrateExtendedFunctionalityFactory;

}