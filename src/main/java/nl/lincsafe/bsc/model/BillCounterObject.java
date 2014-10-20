package nl.lincsafe.bsc.model;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BillCounterObject {
	static Logger logger = Logger.getLogger(BillCounterObject.class);

	private List<Bill> Bills;
	private int CounterID;
	private String FilePath;
	// private File File;
//	protected Ini IniFile;

	public BillCounterObject(int counterID) {
		Bills = new ArrayList<Bill>();
		this.setCounterID(counterID);
	}

	public void load() throws IOException {
		load(FilePath);
	}
	public void load(String fromFilePath) throws IOException {
		FilePath = fromFilePath;
		File iniFile = new File(fromFilePath);
		if (!iniFile.exists()) {
			iniFile.createNewFile();
		}
//		IniFile = new Ini(iniFile);
		loadFromIni();
	}

	@SuppressWarnings("rawtypes")
	public void InitializeBills(int[] values, String currency) {
		int end = values.length;
		Set<Integer> set = new HashSet<Integer>();
		for (int i = 0; i < end; i++) {
			if(values[i] > 0)
				set.add(values[i]);
		}
		Iterator it = set.iterator();
		while (it.hasNext()) {
			int value = (int) it.next();
			logger.info("Add bill value: " + value);
			Bills.add(new Bill(value, currency));
		}
	}

	public void addBill(int value) {
		for (Bill bill : Bills) {
			if (bill.getBillValue() == value) {
				bill.AddBill();
				break;
			}
		}
	}

	public void resetBills() {
		for (Bill bill : Bills) {
			bill.setBillAmount(0);
		}
	}

	public void writeToIni() throws IOException {
		for (Bill bill : Bills) {
//			IniFile.put("BILLS", Integer.toString(bill.getBillValue()), Integer.toString(bill.getBillAmount()));
		}
//		IniFile.store();
	}

	public void loadFromIni() throws IOException {
		for (Bill bill : Bills) {
//			bill.setBillAmount(tryParse(IniFile.get("BILLS", Integer.toString(bill.getBillValue()))));
		}
		writeToIni(); // write it back to be sure.
	}

	public int getTotalValue() {
		int total = 0;
		for (Bill bill : Bills) {
			total += bill.getTotalValue();
		}
		return total;
	}

	public int getCounterID() {
		return CounterID;
	}

	public void setCounterID(int counterID) {
		CounterID = counterID;
	}

	public List<Bill> getBillsList() {
		Bills.sort((w1, w2) -> Integer.compare(w1.getBillValue(),w2.getBillValue()));
		return Bills;
	}

	private Integer tryParse(String obj) {
		Integer retVal;
		try {
			retVal = Integer.parseInt(obj);
		} catch (NumberFormatException nfe) {
			retVal = 0; // or null if that is your preference
		}
		return retVal;
	}

    // adding setter for testing
    public void setBills(List<Bill> bills) {
        this.Bills = bills;
    }
}
