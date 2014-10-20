package nl.lincsafe.bsc.printing;

import gnu.io.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

/**
 * Serial port can be used for reading and writing
 */
public class PrinterSerialPort implements AutoCloseable {

    private static Logger logger = Logger.getLogger(CommPortIdentifier.class);
    public SerialPort serialPortPrinter;
    public OutputStream outputStream;
    public InputStream inputStream;

    public void connect(String port) throws IOException {
        try {
            // Obtain a CommPortIdentifier object for the port you want to open
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(port);

            if (portIdentifier.isCurrentlyOwned()) {
                logger.error("Error: Port" + port + " is currently in use");
            } else {

                // Get the port's ownership
                CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
                // Set the parameters of the connection.
                setSerialPortParameters(commPort);
            }
        } catch (PortInUseException | NoSuchPortException e) {
            logger.error("Port is not reachable!", e);
            close();
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Sets the serial port parameters
     */
    private void setSerialPortParameters(CommPort commPort) throws IOException {
        try {
            if (commPort instanceof SerialPort) {
                serialPortPrinter = (SerialPort) commPort;
                serialPortPrinter
                        .setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                serialPortPrinter.setRTS(true);
                serialPortPrinter.setDTR(true);
                outputStream = serialPortPrinter.getOutputStream();
                inputStream = serialPortPrinter.getInputStream();
            }
        } catch (UnsupportedCommOperationException e) {
            throw new IOException("Unsupported serial port parameter");
        }
    }

    /**
     * Register event handler for data available event
     *
     * @param eventHandler Event handler
     */
    public void addDataAvailableEventHandler(
            SerialPortEventListener eventHandler) {
        try {
            // Add the serial port event listener
            serialPortPrinter.addEventListener(eventHandler);
            serialPortPrinter.notifyOnDataAvailable(true);
        } catch (TooManyListenersException e) {
            logger.warn(e);
        }
    }

    /**
     * Disconnect the serial port
     */
    @Override
    public void close() {
        if (serialPortPrinter != null) {
            try {
                // close the i/o streams.
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                logger.warn("Streams ware closed with errors!", e);
            }
            serialPortPrinter.close();
        }
    }

    /**
     * Get the serial port output stream
     *
     * @return The serial port output stream
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * Get the serial port input stream
     *
     * @return The serial port input stream
     */
    public InputStream getInputStream() {
        return inputStream;
    }


    /**
     * Handles serial port's events
     */
    public static class SerialEventHandler implements SerialPortEventListener {

        private InputStream inStream;
        private int readBufferLen;
        private int readBufferOffset;
        private byte[] readBuffer;

        public SerialEventHandler(InputStream inStream, int readBufferLen) {
            this.inStream = inStream;
            this.readBufferLen = readBufferLen;
            readBuffer = new byte[readBufferLen];
        }

        public boolean isBufferFull() {
            return (readBufferOffset == readBufferLen);
        }

        public byte[] getReadBuffer() {
            return readBuffer;
        }

        public void serialEvent(SerialPortEvent event) {
            switch (event.getEventType()) {
                case SerialPortEvent.DATA_AVAILABLE:
                    readSerial();
                    break;
            }
        }

        private void readSerial() {
            try {
                int availableBytes = inStream.available();
                if (availableBytes > 0) {
                    // Read the serial port
                    readBufferOffset +=
                            inStream.read(readBuffer, readBufferOffset,
                                    availableBytes);
                }
            } catch (IOException e) {
                logger.error("Error while reading input stream");
            }
        }
    }
}
