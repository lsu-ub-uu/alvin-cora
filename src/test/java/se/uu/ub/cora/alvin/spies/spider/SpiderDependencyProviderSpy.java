package se.uu.ub.cora.alvin.spies.spider;

import se.uu.ub.cora.bookkeeper.decorator.DataDecarator;
import se.uu.ub.cora.bookkeeper.linkcollector.DataRecordLinkCollector;
import se.uu.ub.cora.bookkeeper.recordpart.DataRedactor;
import se.uu.ub.cora.bookkeeper.recordtype.RecordTypeHandler;
import se.uu.ub.cora.bookkeeper.termcollector.DataGroupTermCollector;
import se.uu.ub.cora.bookkeeper.validator.DataValidator;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.search.RecordIndexer;
import se.uu.ub.cora.search.RecordSearch;
import se.uu.ub.cora.spider.authentication.Authenticator;
import se.uu.ub.cora.spider.authorization.PermissionRuleCalculator;
import se.uu.ub.cora.spider.authorization.SpiderAuthorizator;
import se.uu.ub.cora.spider.cache.DataChangedSender;
import se.uu.ub.cora.spider.data.DataGroupToFilter;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityProvider;
import se.uu.ub.cora.spider.record.DataGroupToRecordEnhancer;
import se.uu.ub.cora.spider.unique.UniqueValidator;
import se.uu.ub.cora.storage.RecordStorage;
import se.uu.ub.cora.storage.StreamStorage;
import se.uu.ub.cora.storage.archive.RecordArchive;
import se.uu.ub.cora.storage.archive.ResourceArchive;
import se.uu.ub.cora.storage.idgenerator.RecordIdGenerator;

public class SpiderDependencyProviderSpy implements SpiderDependencyProvider {

	@Override
	public RecordStorage getRecordStorage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecordArchive getRecordArchive() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StreamStorage getStreamStorage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecordIdGenerator getRecordIdGenerator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SpiderAuthorizator getSpiderAuthorizator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataValidator getDataValidator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataRecordLinkCollector getDataRecordLinkCollector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataGroupTermCollector getDataGroupTermCollector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PermissionRuleCalculator getPermissionRuleCalculator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataRedactor getDataRedactor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExtendedFunctionalityProvider getExtendedFunctionalityProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Authenticator getAuthenticator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecordSearch getRecordSearch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecordIndexer getRecordIndexer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecordTypeHandler getRecordTypeHandler(String recordTypeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecordTypeHandler getRecordTypeHandlerUsingDataRecordGroup(
			DataRecordGroup dataRecordGroup) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataGroupToRecordEnhancer getDataGroupToRecordEnhancer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataGroupToFilter getDataGroupToFilterConverter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResourceArchive getResourceArchive() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UniqueValidator getUniqueValidator(RecordStorage recordStorage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataChangedSender getDataChangeSender() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataDecarator getDataDecorator() {
		// TODO Auto-generated method stub
		return null;
	}

}
