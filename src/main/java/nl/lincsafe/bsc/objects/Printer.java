//package nl.lincsafe.bsc.objects;
//
//import java.io.*;
//import java.text.*;
//import java.util.*;
//
//import org.apache.log4j.Logger;
//
//import gnu.io.*;
//
//public class Printer {
//	static Logger logger = Logger.getLogger(Printer.class);
//	private MainController mainController;
//	private Settings settings;
//	private static SerialPort serialPortPrinter;
//	private static OutputStream outputStream;
//	private boolean Active;
//
//	public Printer() {
//
//	}
//
//	public void initialize(MainController mController) {
//		mainController = mController;
//		settings = mController.getSettings();
//		try {
//			CommPortIde ntifier portIdentifier = CommPortIdentifier.getPortIdentifier("/dev/ttyS12");
//			//CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier("COM22");
//			if (portIdentifier.isCurrentlyOwned()) {
//				logger.error("Error: Port is currently in use");
//			} else {
//				CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
//				if (commPort instanceof SerialPort) {
//					serialPortPrinter = (SerialPort) commPort;
//					serialPortPrinter.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
//					serialPortPrinter.setRTS(true);
//					serialPortPrinter.setDTR(true);
//					outputStream = serialPortPrinter.getOutputStream();
//                    outputStream.
//					Active = true;
//				}
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			// e.printStackTrace();
//			logger.error("Error on printer init: " + e.getMessage());
//		}
//	}
//
//	public void Print(String data) {
//		if (Active) {
//			Scanner scanner = new Scanner(data);
//			while (scanner.hasNextLine()) {
//				String line = scanner.nextLine();
//				try {
//					outputStream.write(line.getBytes("UTF-8"));
//					outputStream.write((byte) 10);
//					outputStream.flush();
//				} catch (UnsupportedEncodingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					this.initialize(mainController);
//				}
//			}
//			scanner.close();
//		}
//	}
//
//	private void printLine() {
//		printLine("");
//	}
//
//	private void printLine(int lines) {
//		for (int i = 0; i < lines; i++) {
//			printLine();
//		}
//	}
//
//	private void printLine(String line) {
//		if (Active) {
//			try {
//				outputStream.write(line.getBytes("UTF-8"));
//				outputStream.write((byte) 10);
//				outputStream.flush();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				logger.error("error in printline: " + e.getMessage());
//				logger.error("restarting print object.");
//				this.initialize(mainController);
//			}
//		}
//	}
//
//	private String fillspace(String data, int length) {
//		while (data.length() < length) {
//			data = " " + data;
//		}
//		return data;
//	}
//	@Deprecated
//	public void PrintDayCount(BoxCounter counter) {
//		printLine("Day Total Count");
//		printLine("Machine Name: " + settings.getMachineID());
//		DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
//		Date today = Calendar.getInstance().getTime();
//		printLine("Date: " + df.format(today));
//		// printLine("Previous: " + coun)
//		printLine();
//		printLine("Notes accepted");
//		printLine("    BILL        CB");
//		printLine();
//		printLine("EUR    5" + fillspace(Integer.toString(counter.getFives()) + "x", 10));
//		printLine("EUR   10" + fillspace(Integer.toString(counter.getTens()) + "x", 10));
//		printLine("EUR   20" + fillspace(Integer.toString(counter.getTwenties()) + "x", 10));
//		printLine("EUR   50" + fillspace(Integer.toString(counter.getFifties()) + "x", 10));
//		printLine("EUR  100" + fillspace(Integer.toString(counter.getHundreds()) + "x", 10));
//		printLine("EUR  200" + fillspace(Integer.toString(counter.getTwoHundreds()) + "x", 10));
//		printLine("EUR  500" + fillspace(Integer.toString(counter.getFiveHundreds()) + "x", 10));
//		// Niet mooie manier!
//		printLine();
//		printLine("Grand total:" + fillspace(Integer.toString(counter.getTotalValue()), 9));
//		printLine();
//		printLine("END OF Day Total Count");
//
//		printLine(5);
//
//	}
//
//	public void PrintDeposit(BillCounterObjectTransaction counter) {
//		if (Active) {
//			printLine("Current deposit overview");
//			printLine("Machine Name: " + settings.getMachineID());
//			DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
//			Date today = Calendar.getInstance().getTime();
//			printLine("Date: " + df.format(today));
//			User user = mainController.getUsers().getUserById(counter.getUserID(), false);
//			if (user != null)
//				printLine("User: " + user.getFullName());
//			printLine();
//			for (Bill bill : counter.getBillsList()) {
//				printLine(settings.GetCurrency() + fillspace(Integer.toString(bill.getBillValue()), 5) + fillspace(Integer.toString(bill.getBillAmount()) + "x", 10));
//			}
//			printLine();
//			printLine("Grand total:" + fillspace(Integer.toString(counter.getTotalValue()), 9));
//			printLine();
//			printLine("END OF current deposit overview");
//
//			printLine(5);
//
//		}
//	}
//
//	public void PrintCITCount(BillCounterObject counter) {
//		if (Active) {
//
//			printLine("CIT Bank Deposit Activity");
//			printLine("Machine Name: " + settings.getMachineID());
//			DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
//			Date today = Calendar.getInstance().getTime();
//			printLine("Date: " + df.format(today));
//			// printLine("Previous: " + coun)
//			printLine();
//			printLine("Notes accepted");
//			printLine("    BILL        CB");
//			printLine();
//			for (Bill bill : counter.getBillsList()) {
//				printLine(settings.GetCurrency() + fillspace(Integer.toString(bill.getBillValue()), 5) + fillspace(Integer.toString(bill.getBillAmount()) + "x", 10));
//			}
//			/*
//			 * printLine("EUR    5" +
//			 * fillspace(Integer.toString(counter.getFives()) + "x", 10));
//			 * printLine("EUR   10" +
//			 * fillspace(Integer.toString(counter.getTens()) + "x", 10));
//			 * printLine("EUR   20" +
//			 * fillspace(Integer.toString(counter.getTwenties()) + "x", 10));
//			 * printLine("EUR   50" +
//			 * fillspace(Integer.toString(counter.getFifties()) + "x", 10));
//			 * printLine("EUR  100" +
//			 * fillspace(Integer.toString(counter.getHundreds()) + "x", 10));
//			 * printLine("EUR  200" +
//			 * fillspace(Integer.toString(counter.getTwoHundreds()) + "x", 10));
//			 * printLine("EUR  500" +
//			 * fillspace(Integer.toString(counter.getFiveHundreds()) + "x",
//			 * 10)); // Niet mooie manier!
//			 */
//			printLine();
//			printLine("Grand total:" + fillspace(Integer.toString(counter.getTotalValue()), 9));
//			printLine();
//			printLine("END OF CIT Bank Deposit Activity");
//
//			printLine(5);
//
//		}
//		/*
//		 * RawPrintln('CIT Bank Deposit Activity'); // RawPrintLn('39851831 BP
//		 * Tankstelle Klaus'); RawPrintln('Machine Name:
//		 * '+programsettings.MachineName); RawPrintln('Machine ID:
//		 * '+programsettings.MachineID); RawPrintln('Date: '+datetostr(now)+'
//		 * '+timetostr(now)); RawPrintln('Previous:
//		 * '+datetostr(counters.Bill_Box_Start_Date)+'
//		 * '+timetostr(counters.Bill_Box_Start_Date)); RawPrintln('');
//		 * RawPrintln('Notes accepted');
//		 *
//		 * RawPrintln(fillspace('BILL',8)+' '+fillspace('CB1',8)+'
//		 * '+fillspace('CB2',8));
//		 *
//		 * total_eur:=0; total_eur_box_1:=0; total_eur_box_2:=0; for i := 1 to
//		 * counters.Bill_Items_Count do begin
//		 * if(counters.Bill_Items[i].Box_1_Count>0) OR
//		 * (counters.Bill_Items[i].Box_2_Count>0) then
//		 * RawPrintln(counters.Bill_Items
//		 * [i].Bill_Country+' '+fillspace(inttostr(
//		 * round(counters.Bill_Items[i].Bill_Value
//		 * )),4)+' '+fillspace(inttostr(counters
//		 * .Bill_Items[i].Box_1_Count)+'x',8
//		 * )+' '+fillspace(inttostr(counters.Bill_Items[i].Box_2_Count)+'x',8));
//		 * total_eur
//		 * :=total_eur+(counters.Bill_Items[i].Box_1_Count+counters.Bill_Items
//		 * [i].Box_2_Count)*counters.Bill_Items[i].Bill_Value;
//		 * total_eur_box_1:=total_eur_box_1
//		 * +counters.Bill_Items[i].Box_1_Count*counters
//		 * .Bill_Items[i].Bill_Value;
//		 * total_eur_box_2:=total_eur_box_2+counters.Bill_Items
//		 * [i].Box_2_Count*counters.Bill_Items[i].Bill_Value; end;
//		 * RawPrintln(''); RawPrintln('Total Cash Box 1:
//		 * '+fillspace(floattostr(total_eur_box_1),9));
//		 * if(programsettings.MEI2IsActive) then RawPrintln('Total Cash Box 2:
//		 * '+fillspace(floattostr(total_eur_box_2),9)); RawPrintln('Grand total:
//		 * '+fillspace(floattostr(total_eur),9)); RawPrintln('');
//		 */
//	}
//
//}
