package se.uu.ub.cora.alvin.extendedfunctionality;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import se.uu.ub.cora.logger.Logger;

public class LoggerSpy implements Logger {

	public List<String> fatalMessages = new ArrayList<>();
	public List<String> errorMessages = new ArrayList<>();
	public List<String> infoMessages = new ArrayList<>();
	public List<Exception> errorExceptions = new ArrayList<>();

	@Override
	public void logFatalUsingMessage(String message) {
		fatalMessages.add(message);

	}

	@Override
	public void logFatalUsingMessageAndException(String message, Exception exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public void logErrorUsingMessage(String message) {
		errorMessages.add(message);
	}

	@Override
	public void logErrorUsingMessageAndException(String message, Exception exception) {
		errorMessages.add(message);
		errorExceptions.add(exception);
	}

	@Override
	public void logWarnUsingMessage(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void logWarnUsingMessageAndException(String message, Exception exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public void logInfoUsingMessage(String message) {
		infoMessages.add(message);

	}

	@Override
	public void logDebugUsingMessage(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void logDebugUsingMessageSupplier(Supplier<String> messageSupplier) {
		// TODO Auto-generated method stub

	}

	@Override
	public void logTraceUsingMessage(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void logTraceUsingMessageSupplier(Supplier<String> messageSupplier) {
		// TODO Auto-generated method stub

	}

}
