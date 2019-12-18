package se.uu.ub.cora.alvin.extendedfunctionality;

import se.uu.ub.cora.data.DataAtomic;

public class DataAtomicSpy implements DataAtomic {

	private String nameInData;
	private String value;
	private String repeatId;

	public DataAtomicSpy(String nameInData, String value) {
		this.nameInData = nameInData;
		this.value = value;
	}

	@Override
	public String getRepeatId() {
		return repeatId;
	}

	@Override
	public String getNameInData() {
		return nameInData;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setRepeatId(String repeatId) {
		this.repeatId = repeatId;
	}

}
