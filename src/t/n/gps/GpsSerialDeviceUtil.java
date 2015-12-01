package t.n.gps;

import gnu.io.CommPortIdentifier;

import java.util.Enumeration;

public class GpsSerialDeviceUtil {

	public GpsSerialDeviceUtil() {
		// TODO Auto-generated constructor stub
	}

	public static CommPortIdentifier getDevice() {
		CommPortIdentifier targetPort = null;

		Enumeration<CommPortIdentifier> portCandidates = CommPortIdentifier.getPortIdentifiers();
		while(portCandidates.hasMoreElements()) {
			CommPortIdentifier port = portCandidates.nextElement();
			String portName = port.getName();
			//最初に見つかったポートが使われる。複数あった場合にどれを使うかが指定できない。
			//TODO Windowsへの対応
			if(portName.startsWith("/dev/tty.usbserial")) {
				targetPort = port;
				break;
			}
		}
		return targetPort;
	}
}
