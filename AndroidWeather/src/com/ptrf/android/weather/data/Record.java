package com.ptrf.android.weather.data;

/**
 * Temperature record data transfer object.
 */
public class Record {

	private Temperature normal;
	private Temperature record;
	private String year;
	
	/**
	 * Creates new instance of the class.
	 * @param normal normal temperature
	 * @param record record temperature
	 * @param year record year
	 */
	public Record(Temperature normal, Temperature record, String year) {
		this.normal = normal;
		this.record = record;
		this.year = year;
	}

	public Temperature getNormal() {
		return normal;
	}
	
	public void setNormal(Temperature normal) {
		this.normal = normal;
	}

	public Temperature getRecord() {
		return record;
	}

	public void setRecord(Temperature record) {
		this.record = record;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	@Override
	public String toString() {
		return String.format("Record [normal=%s, record=%s, year=%s]", normal, record, year);
	}
	
}
