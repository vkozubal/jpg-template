package nl.lincsafe.bsc.model;

public class Bill {
	private int billValue;
	private int billAmount;
	private String currency;	
	
	/**
	 * @return the billValue
	 */
	public int getBillValue() {
		return billValue;
	}
	/**
	 * @param billValue the billValue to set
	 */
	private void setBillValue(int billValue) {
		this.billValue = billValue;
	}
	/**
	 * @return the billAmount
	 */
	public int getBillAmount() {
		return billAmount;
	}
	/**
	 * @param billAmount the billAmount to set
	 */
	public void setBillAmount(int billAmount) {
		this.billAmount = billAmount;
	}
	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}
	/**
	 * @param currency the currency to set
	 */
	private void setCurrency(String currency) {
		this.currency = currency;
	}

	
	public Bill(int value,String currency) {
		setBillValue(value);
		setCurrency(currency);
		setBillAmount(0);
	}
	
	public void AddBill() {
		this.billAmount++;
	}
	public int getTotalValue() {
		return this.billAmount * this.billValue;
	}
}
