/*
 * Copyright 2021, 2024 Uppsala University Library
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
package se.uu.ub.cora.alvin.extended.migratebinary.internal;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.alvin.batch.BatchRunner;
import se.uu.ub.cora.bookkeeper.recordtype.RecordTypeHandler;
import se.uu.ub.cora.bookkeeper.termcollector.DataGroupTermCollector;
import se.uu.ub.cora.httphandler.HttpHandler;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;
import se.uu.ub.cora.search.RecordIndexer;
import se.uu.ub.cora.spider.binary.Uploader;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.storage.RecordStorage;
import se.uu.ub.cora.storage.StorageReadResult;

public class MigrateBinaryRunner implements BatchRunner, Runnable {

	private static final long FROM_NUMBER = 1;
	private static final long TO_NUMBER = 1000;
	private long from = FROM_NUMBER;
	private long to = TO_NUMBER;
	private SpiderDependencyProvider dependencyProvider;
	private MigrateBinaryInfo migrateInfo;
	private RecordStorage recordStorage;
	private RecordTypeHandler recordTypeHandler;
	private DataGroupTermCollector termCollector;
	private RecordIndexer recordIndexer;
	private MigrateBinaryStorer storer;
	private List<Error> errors = new ArrayList<>();
	private long numberRequestedFromListing = 0;
	private HttpHandlerFactory httpHandlerFactory;

	public MigrateBinaryRunner(SpiderDependencyProvider dependencyProvider,
			MigrateBinaryStorer storer, HttpHandlerFactory httpHandlerFactory,
			MigrateBinaryInfo migrateInfo) {
		this.dependencyProvider = dependencyProvider;
		this.storer = storer;
		this.httpHandlerFactory = httpHandlerFactory;
		this.migrateInfo = migrateInfo;
	}

	@Override
	public void run() {
		// setNeededDependenciesInClass();
		// String metadataId = recordTypeHandler.getDefinitionId();
		// ensureNumberOfIndexedIsZero();
		// readListAndIndexDataInBatches(metadataId);
		// updateMigrateBinaryAsFinished();
		System.err.println("TRAMS" + migrateInfo);

		// räv
		// HttpHandler httpHandler = httpHandlerFactory.factor(
		// "https://www.alvin-portal.org/alvin/attachment/download/alvin-record:95599/ATTACHMENT-0001.tiff");
		// vill du läsa
		HttpHandler httpHandler = httpHandlerFactory.factor(
				"https://www.alvin-portal.org/alvin/attachment/download/alvin-record:631044/ATTACHMENT-0001.tiff");
		InputStream responseBinary = httpHandler.getResponseBinary();

		Uploader uploader = SpiderInstanceProvider.getUploader();

		uploader.uploadSpike("121212", "binary", "2", responseBinary, "master");
		// HttpMultiPartUploader httpHandlerUpload =
		// httpHandlerFactory.factorHttpMultiPartUploader();
		// addStreamInfoToHttpHandler(httpHandler);
		// httpHandler.done();
	}
	//
	// public String testDownload() {
	// RestResponse response = recordHandler.download(authToken, recordType, recordId,
	// representation);
	// statusType = Response.Status.fromStatusCode(response.responseCode());
	//
	// return response.responseText();
	// }

	// public String testUpload() throws IOException {
	// String url = baseRecordUrl + type + "/" + id + "/master";
	// url = addAuthTokenToUrl(url);
	//
	// HttpMultiPartUploader httpHandler = httpHandlerFactory.factorHttpMultiPartUploader(url);
	// addStreamInfoToHttpHandler(httpHandler);
	// httpHandler.done();
	//
	// statusType = Response.Status.fromStatusCode(httpHandler.getResponseCode());
	// if (responseIsOk()) {
	// String responseText = httpHandler.getResponseText();
	// streamId = tryToFindStreamId(responseText);
	// return responseText;
	// }
	// return httpHandler.getErrorText();
	// }

	// @Override
	// public RestResponse download(String type, String id, String representation) {
	// HttpHandler httpHandler = createHttpHandlerForDownload(type, id, representation);
	// Supplier<RestResponse> methodToRetry = () -> download(type, id, representation);
	// return handlDownloadResponseFromHttpHandlerUsingMethodToRetry(httpHandler, methodToRetry);
	// }
	//
	// private HttpHandler createHttpHandlerForDownload(String type, String id,
	// String representation) {
	// String url = baseUrl + "record/" + type + "/" + id + "/" + representation;
	// HttpHandler httpHandler = createHttpHandlerWithAuthTokenAndUrl(url);
	// httpHandler.setRequestMethod("GET");
	// return httpHandler;
	// }
	//
	// private RestResponse handlDownloadResponseFromHttpHandlerUsingMethodToRetry(
	// HttpHandler httpHandler, Supplier<RestResponse> methodToRetry) {
	// if (responseIsOk(httpHandler)) {
	// return composeDownloadResponseForOk(httpHandler);
	// }
	// return handleErrorResponses(httpHandler, methodToRetry);
	// }
	//
	// private RestResponse composeDownloadResponseForOk(HttpHandler httpHandler) {
	// InputStream responseBinary = httpHandler.getResponseBinary();
	// return new RestResponse(httpHandler.getResponseCode(), "", Optional.of(responseBinary),
	// Optional.empty());
	// }

	private void setNeededDependenciesInClass() {
		recordStorage = dependencyProvider.getRecordStorage();
		// recordTypeHandler =
		// dependencyProvider.getRecordTypeHandler(migrateInfo.recordTypeToIndex);
		termCollector = dependencyProvider.getDataGroupTermCollector();
		recordIndexer = dependencyProvider.getRecordIndexer();
	}

	private void ensureNumberOfIndexedIsZero() {
		migrateInfo.numberOfProcessedRecords = 0;
	}

	private void readListAndIndexDataInBatches(String metadataId) {
		try {
			while (numberRequestedFromListing < migrateInfo.totalNumberToProcess) {
				indexOneBatch(metadataId);
			}
		} catch (Exception e) {
			Error error = new Error("MigrateBinaryRunner", e.getMessage());
			migrateInfo.errors.add(error);
			updateMigrateBinaryAsFinished();
		}
	}

	private void indexOneBatch(String metadataId) {
		possiblySetToNumToTotalNumberToIndex();
		// setFromAndToInFilter(from, to);
		// readListAndIndexData(metadataId);
		prepareNextIteration();
	}

	private void prepareNextIteration() {
		numberRequestedFromListing = to;
		from = to + FROM_NUMBER;
		to = to + TO_NUMBER;
		to = possiblySetToNoToTotalNumberOfRecords(to);
	}

	private void possiblySetToNumToTotalNumberToIndex() {
		if (migrateInfo.totalNumberToProcess < to) {
			to = migrateInfo.totalNumberToProcess;
		}
	}

	// private void setFromAndToInFilter(long from, long to) {
	// Filter filter = migrateInfo.filter;
	// filter.fromNo = from;
	// filter.toNo = to;
	// }

	// private void readListAndIndexData(String metadataId) {
	// StorageReadResult readResult = readList();
	// indexData(metadataId, readResult);
	// updateAndStoreMigrateBinary(readResult);
	// clearErrors();
	// }

	// private StorageReadResult readList() {
	// Filter filter = migrateInfo.filter;
	// return recordStorage.readList(migrateInfo.recordTypeToIndex, filter);
	// }

	// private void indexData(String metadataId, StorageReadResult readResult) {
	// for (DataRecordGroup dataRecordGroup : readResult.listOfDataRecordGroups) {
	// indexRecord(metadataId, dataRecordGroup);
	// }
	// }

	// private void indexRecord(String metadataId, DataRecordGroup dataRecordGroup) {
	// try {
	// tryToIndexData(metadataId, dataRecordGroup);
	// } catch (Exception e) {
	// String recordId = dataRecordGroup.getId();
	// Error error = new Error(recordId, e.getMessage());
	// errors.add(error);
	// }
	// }

	// private void tryToIndexData(String metadataId, DataRecordGroup dataRecordGroup) {
	// CollectTerms collectedTerms = termCollector.collectTerms(metadataId, dataRecordGroup);
	// String recordId = dataRecordGroup.getId();
	// String recordType = dataRecordGroup.getType();
	// recordIndexer.indexDataWithoutExplicitCommit(recordType, recordId,
	// collectedTerms.indexTerms, dataRecordGroup);
	// }

	private void updateAndStoreMigrateBinary(StorageReadResult readResult) {
		migrateInfo.errors.addAll(errors);
		increaseNumOfIndexedInMigrateBinary(readResult);
		storeMigrateBinary();
	}

	private void increaseNumOfIndexedInMigrateBinary(StorageReadResult readResult) {
		int numberOfRecordsSentToIndex = readResult.listOfDataRecordGroups.size();
		migrateInfo.numberOfProcessedRecords += numberOfRecordsSentToIndex;
	}

	private void storeMigrateBinary() {
		storer.store(migrateInfo);
	}

	private void clearErrors() {
		migrateInfo.errors.clear();
		errors.clear();
	}

	private long possiblySetToNoToTotalNumberOfRecords(long to) {
		if (to > migrateInfo.totalNumberToProcess) {
			to = migrateInfo.totalNumberToProcess;
		}
		return to;
	}

	private void updateMigrateBinaryAsFinished() {
		migrateInfo.status = "finished";
		storeMigrateBinary();
	}

	SpiderDependencyProvider onlyForTestGetDependencyProvider() {
		return dependencyProvider;
	}

	HttpHandlerFactory onlyForTestGetHttpHandlerFactory() {
		return httpHandlerFactory;
	}

	MigrateBinaryStorer onlyForTestGetMigrateBinaryStorer() {
		return storer;
	}

	MigrateBinaryInfo onlyForTestGetMigrateBinaryInfo() {
		return migrateInfo;
	}
}