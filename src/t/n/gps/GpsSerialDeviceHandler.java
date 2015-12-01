package t.n.gps;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TooManyListenersException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import t.n.map.common.LocationInfo;

public class GpsSerialDeviceHandler implements SerialPortEventListener {
	private static final Logger logger = Logger.getLogger(GpsSerialDeviceHandler.class.getSimpleName());

	private SerialPort port;
	private final GpsEventListener listener;
	private CountDownLatch startSignal;
	private boolean terminateFlag = false;
	private long doneTime;
	private int checksumOkCount = 0;
	private int checksumErrorCount = 0;

	public GpsSerialDeviceHandler(CommPortIdentifier portId, GpsEventListener listener) {
		this.listener = listener;
		logger.setLevel(Level.WARNING);

		try {
			port = portId.open(this.getClass().getName(), 2000);
			port.setSerialPortParams(
					9600,                   // 通信速度[bps]
					SerialPort.DATABITS_8,   // データビット数
					SerialPort.STOPBITS_2,   // ストップビット
					SerialPort.PARITY_NONE   // パリティ
					);
			port.setInputBufferSize(1024);
			log("intput buffer size:" + port.getInputBufferSize());
			log("end of input char:" + port.getEndOfInputChar());
			port.enableReceiveThreshold(512+128);
			port.enableReceiveTimeout(500);
			port.addEventListener(this);
			port.notifyOnDataAvailable(true);
			startSignal = new CountDownLatch(1);

			while(!terminateFlag) {
				startSignal.await();
				read();
				startSignal = new CountDownLatch(1);
			}
		} catch (TooManyListenersException e) {
			e.printStackTrace();
		} catch (PortInUseException e) {
			e.printStackTrace();
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");

	@Override
	public void serialEvent(SerialPortEvent evt) {
		if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			long evtTime = System.currentTimeMillis();
			long margin;
			if(doneTime != 0) {
				margin = evtTime - doneTime;
				log("SerialEvent:" + df.format(evtTime) + ", (" + margin + ")");
			} else {
				log("SerialEvent:" + df.format(evtTime));
			}
			startSignal.countDown();
		} else {
			log("Other event:" + evt.getEventType());
		}
	}

	public void read() {
		try {
			InputStream in = port.getInputStream();
			BufferedReader bis = new BufferedReader(new InputStreamReader(in));

			int loopCount = 0;
			while(bis.ready()) {
				log("\trecv:" + df.format(System.currentTimeMillis()));
				String line = bis.readLine();
				checkSentence(line);
				doneTime = System.currentTimeMillis();
				log("\tdone:" + df.format(doneTime) + ": loop count:" + loopCount++ + ",checksum OK:" + checksumOkCount + ", Error:" + checksumErrorCount);
			}
		} catch (IOException e) {
			logger.log(Level.WARNING, e.getMessage());
		}
	}

//	public void read_old() {
//		try {
//			InputStream in = port.getInputStream();
//			int loopCount = 0;
//			while(in.available() > 0) {
//				log("\trecv:" + df.format(System.currentTimeMillis()));
//				StringBuilder sb = new StringBuilder(1024);
//				int len = in.available();
//				//int count = in.read(buf, 0, len);
//				for(int i = 0; i < len; i++) {
//					int c = in.read();
//					if(c == 0x0D)continue;
//					if(c == 0x0A||c == -1)break;
//					sb.append((char)c);
//				}
//				String line = sb.toString();
//				checkSentence(line);
//				doneTime = System.currentTimeMillis();
//				log("\tdone:" + df.format(doneTime) + ": loop count:" + loopCount++);
//			}
//		} catch (IOException e) {
//			logger.log(Level.WARNING, e.getMessage());
//		}
//	}

	private void checkSentence(String line) {
		String actualSum;
		String expectedSum;
		if(line.length() > 0 && GpsLocationInfoDetector.isContainsLocationInfo(line)) {
			byte sum = GpsTextChecksumValidator.computeActualChecksum(line);
			if(sum <= 0xF) {
				actualSum = "0" + Integer.toHexString(sum).toUpperCase();
			} else {
				actualSum = Integer.toHexString(sum).toUpperCase();
			}
			expectedSum = GpsTextChecksumValidator.getExpectedSum(line);

			if(expectedSum != null && expectedSum.equals(actualSum)) {
				//TODO
				log("checksum OK:" + line);
				checksumOkCount++;

				//別Threadで位置情報を得る。ベンチマークを行って、処理時間に余裕があればこのthreadでそのまま処理してもいい。
				LocationInfo locationInfo = GpsLocationInfoDetector.detect(line);
				if(listener != null && !Float.isNaN((locationInfo.getLatInDegrees())) && !Float.isNaN((locationInfo.getLonInDegrees())) ) {
					listener.notifiLocationInfo(locationInfo);
				}
			} else {
				log("checksum error:" + line);
				checksumErrorCount++;
			}
		} else {
//				log("位置情報がないです:" + line);
		}
	}

	private void log(String msg) {
		if(logger.isLoggable(Level.FINE)) {
			logger.info(msg);
		}
	}

	public void close() {
		terminateFlag  = true;
		if(port != null) {
			port.close();
		}
	}

}
